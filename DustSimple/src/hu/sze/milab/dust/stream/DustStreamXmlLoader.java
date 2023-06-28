package hu.sze.milab.dust.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

public class DustStreamXmlLoader implements DustStreamConsts {

	DustStreamUrlCache cache;

	DocumentBuilderFactory dbf;

	class NamespaceLoader implements StreamProcessor {
		private static final String XML_XMLNS = "xmlns:";
		Element root;

		@Override
		public void processStream(InputStream is) throws Exception {
			if ( null == dbf ) {
				dbf = DocumentBuilderFactory.newInstance();
			}

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);

			root = doc.getDocumentElement();

			Map<String, String> nsRefs = new TreeMap<>();

			if ( root.hasAttributes() ) {
				NamedNodeMap nnm = root.getAttributes();

				for (int ni = nnm.getLength(); ni-- > 0;) {
					Node att = nnm.item(ni);

					String name = att.getNodeName();

					if ( name.startsWith(XML_XMLNS) ) {
						nsRefs.put(name.substring(XML_XMLNS.length()), att.getNodeValue());
					}
				}
			}
			
			if ( !nsRefs.isEmpty() ) {
				NodeList nl = root.getElementsByTagName("xsd:import");
				for (int ni = nl.getLength(); ni-- > 0;) {
					NamedNodeMap atts = nl.item(ni).getAttributes();
					String uri = atts.getNamedItem("namespace").getNodeValue();
					
					if ( !namespaces.containsKey(uri) ) {
						String url = atts.getNamedItem("schemaLocation").getNodeValue();
						queue.put(uri, url);
					}
				}

				//	<xsd:import namespace="http://www.xbrl.org/2003/instance"
				// schemaLocation="http://www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd" />

			}
		}
	};

	NamespaceLoader nsLoader = new NamespaceLoader();

	Map<String, Element> namespaces = new TreeMap<>();
	Map<String, String> queue = new TreeMap<>();

	File root;
	@SuppressWarnings("unchecked")
	Map<String, String> urlRewrite = Collections.EMPTY_MAP;

	public DustStreamXmlLoader(DustStreamUrlCache cache) {
		this.cache = cache;
	}

	private Element resolveUrl(String url) throws Exception {
		
		Dust.dumpObs("Resolving url", url);

		for (Map.Entry<String, String> e : urlRewrite.entrySet()) {
			String prefix = e.getKey();
			if ( url.startsWith(prefix) ) {
				File f = new File(root, e.getValue());
				f = new File(f, url.substring(prefix.length()));

				try (FileInputStream is = new FileInputStream(f)) {
					nsLoader.processStream(is);
					return nsLoader.root;
				}
			}
		}

		cache.access(url, nsLoader);
		return nsLoader.root;
	}

	private void processQueue() throws Exception {
		while (!queue.isEmpty()) {
			Iterator<Entry<String, String>> qi = queue.entrySet().iterator();

			Map.Entry<String, String> item = qi.next();
			String uri = item.getKey();
			if ( !namespaces.containsKey(uri) ) {
				namespaces.put(uri, resolveUrl(item.getValue()));
			}
			qi.remove();
		}
	}

	public void loadNamespace(String uri, String url) throws Exception {
		Element ns = namespaces.get(uri);

		if ( null == ns ) {
			cache.access(url, nsLoader);
			namespaces.put(uri, nsLoader.root);

			processQueue();
		}
	}

	public Element getNamespace(String uri) throws Exception {
		return namespaces.get(uri);
	}

	public Element loadNamespace(File root, String path, Map<String, String> urlRewrite) throws Exception {
		this.root = root;
		this.urlRewrite = urlRewrite;

		Element ret = resolveUrl(path);

		processQueue();

		return ret;
	}

}
