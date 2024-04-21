package hu.sze.milab.dust.stream.xml;

import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import hu.sze.milab.dust.utils.DustUtils;

public class DustXmlUtils implements DustXmlConsts {
	private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();

	public static Document parse(InputStream is) throws Exception {
		DocumentBuilder db = DBF.newDocumentBuilder();
		Document doc = db.parse(is);
		return doc;
	}

	public static String getInfo(Element e, String tagName) {
		NodeList nl = e.getElementsByTagName(tagName);
		if ( 0 < nl.getLength() ) {
			String val = nl.item(0).getTextContent();
			if ( !DustUtils.isEmpty(val) ) {
				return val;
			}
		}
		return null;
	}

	public static boolean optLoadInfo(Map<String, String> cd, Element e, String tagName) {
		String val = getInfo(e, tagName);
		if ( !DustUtils.isEmpty(val) ) {
			cd.put(tagName, val);
			return true;
		}
		return false;
	}


}
