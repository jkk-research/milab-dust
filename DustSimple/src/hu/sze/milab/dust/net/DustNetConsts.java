package hu.sze.milab.dust.net;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustMetaConsts;

public interface DustNetConsts extends DustMetaConsts {
	
	int NO_PORT_SET = -1;

	String CHARSET_POSTFIX_UTF8 = "; charset=UTF-8";

	String MEDIATYPE_UTF8_TEXT = "text/plain" + CHARSET_POSTFIX_UTF8;
	String MEDIATYPE_UTF8_HTML = "text/html" + CHARSET_POSTFIX_UTF8;
	String MEDIATYPE_UTF8_CSV = "text/csv" + CHARSET_POSTFIX_UTF8;

	String MEDIATYPE_JSON = "application/json";
	String MEDIATYPE_JSONAPI = "application/vnd.api+json";
	String MEDIATYPE_ZIP = "application/zip";



	MindHandle NET_UNIT = Dust.resolveID(null, null);

	MindHandle NET_ASP_HOST = Dust.resolveID(null, null);
	MindHandle NET_ATT_HOST_PORT = Dust.resolveID(null, null);
	MindHandle NET_ATT_HOST_IPV4 = Dust.resolveID(null, null);

	MindHandle NET_ASP_SSLINFO = Dust.resolveID(null, null);
	MindHandle NET_ATT_SSLINFO_PORT = Dust.resolveID(null, null);
	MindHandle NET_ATT_SSLINFO_STOREPATH = Dust.resolveID(null, null);
	MindHandle NET_ATT_SSLINFO_STOREPASS = Dust.resolveID(null, null);
	MindHandle NET_ATT_SSLINFO_KEYMANAGERPASS = Dust.resolveID(null, null);

	MindHandle NET_ASP_SRVCALL = Dust.resolveID(null, null);
	MindHandle NET_ATT_SRVCALL_REQUEST = Dust.resolveID(null, null);
	MindHandle NET_ATT_SRVCALL_RESPONSE = Dust.resolveID(null, null);
	MindHandle NET_ATT_SRVCALL_PATHINFO = Dust.resolveID(null, null);
	MindHandle NET_ATT_SRVCALL_PAYLOAD = Dust.resolveID(null, null);
	MindHandle NET_ATT_SRVCALL_HEADERS = Dust.resolveID(null, null);
	MindHandle NET_ATT_SRVCALL_ATTRIBUTES = Dust.resolveID(null, null);

	MindHandle NET_LOG_SRVJETTY = Dust.resolveID(null, null);

}
