package hu.sze.milab.dust.net;

import hu.sze.milab.dust.DustConsts;

public interface DustNetConsts extends DustConsts, DustNetHandles {
	
	int NO_PORT_SET = -1;

	String CHARSET_POSTFIX_UTF8 = "; charset=UTF-8";

	String MEDIATYPE_UTF8_TEXT = "text/plain" + CHARSET_POSTFIX_UTF8;
	String MEDIATYPE_UTF8_HTML = "text/html" + CHARSET_POSTFIX_UTF8;
	String MEDIATYPE_UTF8_CSV = "text/csv" + CHARSET_POSTFIX_UTF8;
	String MEDIATYPE_UTF8_XML = "text/xml" + CHARSET_POSTFIX_UTF8;

	String MEDIATYPE_JSON = "application/json";
	String MEDIATYPE_JSONAPI = "application/vnd.api+json";
	String MEDIATYPE_ZIP = "application/zip";


}
