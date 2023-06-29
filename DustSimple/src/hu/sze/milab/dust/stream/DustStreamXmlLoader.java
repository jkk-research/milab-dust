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

public class DustStreamXmlLoader implements DustStreamConsts {
	
	public interface NamespaceProcessor {
		void namespaceLoaded(Element root);
	}

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
			}
			
			if ( null != nsProc ) {
				nsProc.namespaceLoaded(root);
			}
		}
	};

	NamespaceLoader nsLoader = new NamespaceLoader();

	Map<String, Element> namespaces = new TreeMap<>();
	Map<String, String> queue = new TreeMap<>();

	File root;
	File currParent;
	@SuppressWarnings("unchecked")
	Map<String, String> urlRewrite = Collections.EMPTY_MAP;
	NamespaceProcessor nsProc = null;

	public DustStreamXmlLoader(DustStreamUrlCache cache) {
		this.cache = cache;
	}

	private Element resolveUrl(String url) throws Exception {
		
//		Dust.dumpObs("Resolving url", url);
		
		File f = url.startsWith("http") ? null : (null == currParent) ? new File(root, url) : new File(currParent, url); 

		for (Map.Entry<String, String> e : urlRewrite.entrySet()) {
			String prefix = e.getKey();
			if ( url.startsWith(prefix) ) {
				f = new File(root, e.getValue());
				f = new File(f, url.substring(prefix.length()));
				currParent = f.getParentFile();
				break;
			}
		}
		
		if ( null == f ) {
			cache.access(url, nsLoader);
		} else {
			try (FileInputStream is = new FileInputStream(f)) {
				nsLoader.processStream(is);
			}
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
