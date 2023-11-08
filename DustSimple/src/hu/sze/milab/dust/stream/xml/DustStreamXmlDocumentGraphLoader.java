package hu.sze.milab.dust.stream.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.stream.DustStreamConsts;
import hu.sze.milab.dust.stream.DustStreamUrlCache;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustStreamXmlDocumentGraphLoader implements DustStreamXmlConsts, DustUtils.QueueContainer<String>, DustStreamConsts.StreamProcessor<Element> {

	public interface XmlDocumentProcessor {
		void documentLoaded(Element root, DustUtils.QueueContainer<String> loader);
	}

	DustStreamUrlCache cache;

	DocumentBuilderFactory dbf;

	Map<String, Element> docById = new TreeMap<>();
	Map<String, Element> docByUrl = new TreeMap<>();

	Map<String, String> readQueue = new TreeMap<>();

	File root;
	@SuppressWarnings("unchecked")
	Map<String, String> urlRewrite = Collections.EMPTY_MAP;
	XmlDocumentProcessor docProc = null;

	public DustStreamXmlDocumentGraphLoader(DustStreamUrlCache cache) {
		this.cache = cache;
	}

	@Override
	public void enqueue(String item, Object... hints) {
		String url = DustUtilsFile.optRemoveUpFromPath((String) hints[0]);
		if ( DustUtils.isEmpty(url) ) {
			Dust.dumpObs("Hey", item, hints[0]);
		}
		if ( !readQueue.containsKey(item) && !docById.containsKey(item) && !docByUrl.containsKey(url) ) {
			readQueue.put(item, url);

//			Dust.dumpObs("      Queueing", item, hints[0]);
//		} else {
//			Dust.dumpObs("      SKIPPING", item, hints[0]);
		}
	}

	private Element resolveUrl(String url) throws Exception {
		Element eRoot = docByUrl.get(url);

		if ( null == eRoot ) {
			File f = null;

			if ( url.startsWith("file") ) {
				f = Paths.get(new URL(url).toURI()).toFile();
			} else if ( url.startsWith("http") ) {
				for (Map.Entry<String, String> e : urlRewrite.entrySet()) {
					String prefix = e.getKey();
					if ( url.startsWith(prefix) ) {
						f = new File(root, e.getValue());
						f = new File(f, url.substring(prefix.length()));
						break;
					}
				}
			} else {
				f = new File(root, url);
			}

			URL ref = null;
			if ( null == f ) {
				eRoot = cache.access(url, this);
				ref = new URL(url);
			} else {
				ref = f.toURI().toURL();
				try (FileInputStream is = new FileInputStream(f)) {
					eRoot = processStream(is, ref.toString());
				}
			}

			docByUrl.put(url, eRoot);
			if ( null != docProc ) {
				docProc.documentLoaded(eRoot, this);
			}
		}

		return eRoot;
	}

	private void processQueue() throws Exception {
		while (!readQueue.isEmpty()) {
			Iterator<Entry<String, String>> qi = readQueue.entrySet().iterator();
			Map.Entry<String, String> item = qi.next();
			qi.remove();

			String uri = item.getKey();
			if ( !docById.containsKey(uri) ) {
				docById.put(uri, resolveUrl(item.getValue()));
			}
		}
	}

	public Element getDocRoot(String uri) throws Exception {
		return docById.get(uri);
	}

	public Element loadDocument(File root, String path, XmlDocumentProcessor proc, Map<String, String> urlRewrite) throws Exception {
		this.root = root;
		this.urlRewrite = urlRewrite;
		this.docProc = proc;

		Element ret = resolveUrl(path);

		processQueue();

		return ret;
	}

	public Element processStream(InputStream is, String myUrl) throws Exception {
		Element root = null;
		if ( null == dbf ) {
			dbf = DocumentBuilderFactory.newInstance();
		}

		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(is);

		root = doc.getDocumentElement();

		myUrl = DustUtilsFile.optRemoveUpFromPath(myUrl);

		root.setUserData(XML_DATA_DOCURL, myUrl, null);

		Map<String, String> nsRefs = new TreeMap<>();

		String schemaLoc[] = {};

		if ( root.hasAttributes() ) {
			NamedNodeMap nnm = root.getAttributes();

			for (int ni = nnm.getLength(); ni-- > 0;) {
				Node att = nnm.item(ni);

				String name = att.getNodeName();
				String sVal = att.getNodeValue();

				if ( name.startsWith(XML_PREF_XMLNS) ) {
					nsRefs.put(name.substring(XML_PREF_XMLNS.length()), sVal);
				} else if ( name.endsWith(XML_ATT_SCHEMALOC) ) {
					schemaLoc = sVal.trim().replaceAll("\\s+", " ").split(" ");
				}
			}
		}

		if ( !nsRefs.isEmpty() ) {
			for (int i = 0; i < schemaLoc.length;) {
				String uri = schemaLoc[i++].trim();
				String url = schemaLoc[i++].trim();
				enqueue(uri, url);
			}

			NodeList nl = root.getElementsByTagName("xsd:import");
			for (int ni = nl.getLength(); ni-- > 0;) {
				NamedNodeMap atts = nl.item(ni).getAttributes();
				String uri = atts.getNamedItem("namespace").getNodeValue();

				if ( !docById.containsKey(uri) ) {
					String url = atts.getNamedItem("schemaLocation").getNodeValue();

					if ( !url.startsWith("http") ) {
						url = DustUtils.replacePostfix(myUrl, "/", url);
					}
					enqueue(uri, url);
				}
			}
		}

		return root;
	}
}
