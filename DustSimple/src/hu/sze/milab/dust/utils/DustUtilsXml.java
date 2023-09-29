package hu.sze.milab.dust.utils;

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DustUtilsXml implements DustUtilsConsts {

	public static Element getFirstElement(Element e, String ns, String tagName) {
		NodeList nl = (null == ns) ? e.getElementsByTagName(tagName) : e.getElementsByTagNameNS(ns, tagName);
		return (0 < nl.getLength()) ? (Element) nl.item(0) : null;
	}

	public static String getTagText(Element e, String ns, String tagName) {
		Element ee = getFirstElement(e, ns, tagName);
		if ( null != ee ) {
			String val = ee.getTextContent();
			if ( !DustUtils.isEmpty(val) ) {
				return val.trim();
			}
		}
		return null;
	}

	public static boolean optLoadTagText(Map<String, String> cd, Element e, String ns, String tagName) {
		String val = getTagText(e, ns, tagName);
		if ( !DustUtils.isEmpty(val) ) {
			cd.put(tagName, val);
			return true;
		}
		return false;
	}

}
