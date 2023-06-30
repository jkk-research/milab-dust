package hu.sze.milab.dust.stream.xml;

import hu.sze.milab.dust.stream.DustStreamConsts;

public interface DustStreamXmlConsts extends DustStreamConsts {
	
	String XML_PREF_XMLNS = "xmlns:";
	String XML_ATT_SCHEMALOC = "schemaLocation";

	
//	String XML_ATT_REF = "dustRef";
	String XML_DATA_DOCURL = "dustDocUrl";
	
	
	enum XmlData {
		Element, Attribute, Content, 
	}

}
