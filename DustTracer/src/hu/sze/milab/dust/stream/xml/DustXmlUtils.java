package hu.sze.milab.dust.stream.xml;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class DustXmlUtils implements DustXmlConsts {
	private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();

	public static Document parse(InputStream is) throws Exception {
		DocumentBuilder db = DBF.newDocumentBuilder();
		Document doc = db.parse(is);
		return doc;
	}

}
