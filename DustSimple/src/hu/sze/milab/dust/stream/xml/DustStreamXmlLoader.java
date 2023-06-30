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
import hu.sze.milab.dust.stream.DustStreamUrlCache;
import hu.sze.milab.dust.utils.DustUtils;

public class DustStreamXmlLoader implements DustStreamXmlConsts, DustUtils.QueueContainer<String> {

	public interface NamespaceProcessor {
		void namespaceLoaded(Element root, DustUtils.QueueContainer<String> loader);
	}

	DustStreamUrlCache cache;

	DocumentBuilderFactory dbf;

	class NamespaceLoader implements StreamProcessor {
		Element root;

		@Override
		public void processStream(InputStream is, String myUrl) throws Exception {
			if ( null == dbf ) {
				dbf = DocumentBuilderFactory.newInstance();
			}

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			
			root = doc.getDocumentElement();
			
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
						schemaLoc = sVal.trim().split(" ");
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

					if ( !namespaces.containsKey(uri) ) {
						String url = atts.getNamedItem("schemaLocation").getNodeValue();

						if ( !url.startsWith("http") ) {
//							Dust.dumpObs("      Plain ref", url, "replaced to", DustUtils.replacePostfix(myUrl, "/", url));
							url = DustUtils.replacePostfix(myUrl, "/", url);
						}
						enqueue(uri, url);
					}
				}
			}
		}
	};

	NamespaceLoader nsLoader = new NamespaceLoader();

	Map<String, Element> namespaces = new TreeMap<>();
	Map<String, String> queue = new TreeMap<>();

	File root;
	@SuppressWarnings("unchecked")
	Map<String, String> urlRewrite = Collections.EMPTY_MAP;
	NamespaceProcessor nsProc = null;

	public DustStreamXmlLoader(DustStreamUrlCache cache) {
		this.cache = cache;
	}

	@Override
	public void enqueue(String item, Object... hints) {
		if ( !queue.containsKey(item) && !namespaces.containsKey(item) ) {
			queue.put(item, (String) hints[0]);

//			Dust.dumpObs("      Queueing", item, hints[0]);
		}
	}

	private Element resolveUrl(String url) throws Exception {
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
//		url.startsWith("http") ? null : (null == currParent) ? new File(root, url) : new File(currParent, url);

		URL ref = null;
		if ( null == f ) {
			cache.access(url, nsLoader);
			ref = new URL(url);
		} else {
			if ( !f.exists() ) {
				Dust.dumpObs("HEH??");
			}
			ref = f.toURI().toURL();
			try (FileInputStream is = new FileInputStream(f)) {
				nsLoader.processStream(is, ref.toString());
			}
		}

		Element root = nsLoader.root;
//		root.setAttribute(XML_ATT_REF, ref.toString());

		if ( null != nsProc ) {
			nsProc.namespaceLoaded(root, this);
		}

		return nsLoader.root;
	}

	private void processQueue() throws Exception {
		while (!queue.isEmpty()) {
			Iterator<Entry<String, String>> qi = queue.entrySet().iterator();
			Map.Entry<String, String> item = qi.next();
			qi.remove();

			String uri = item.getKey();
			if ( !namespaces.containsKey(uri) ) {
				namespaces.put(uri, resolveUrl(item.getValue()));
			}
		}
	}

	public Element getNamespace(String uri) throws Exception {
		return namespaces.get(uri);
	}

	public Element loadNamespace(File root, String path, NamespaceProcessor proc, Map<String, String> urlRewrite) throws Exception {
		this.root = root;
		this.urlRewrite = urlRewrite;
		this.nsProc = proc;

		Element ret = resolveUrl(path);

		processQueue();

		return ret;
	}

}
