package hu.sze.milab.dust.net;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustUnitHandles;

public interface DustNetHandles extends DustConsts, DustUnitHandles {
	
//	MindHandle NET_UNIT = Dust.lookup("giskard:10");

	MindHandle NET_ASP_HOST = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_HOST_PORT = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_HOST_IPV4 = Dust.lookup("giskard:10:?");

	MindHandle NET_ASP_SSLINFO = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SSLINFO_PORT = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SSLINFO_STOREPATH = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SSLINFO_STOREPASS = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SSLINFO_KEYMANAGERPASS = Dust.lookup("giskard:10:?");

	MindHandle NET_ASP_SRVCALL = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVCALL_REQUEST = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVCALL_RESPONSE = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVCALL_METHOD = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVCALL_PATHINFO = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVCALL_PAYLOAD = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVCALL_HEADERS = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVCALL_ATTRIBUTES = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVCALL_STATUS = Dust.lookup("giskard:10:?");

	MindHandle NET_ASP_SRVRESP = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVRESP_STATUS = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVRESP_TYPE = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVRESP_HEADER = Dust.lookup("giskard:10:?");
	MindHandle NET_ATT_SRVRESP_PAYLOAD = Dust.lookup("giskard:10:?");

	MindHandle NET_NAR_HTTPSRV = Dust.lookup("giskard:10:?");
	MindHandle NET_NAR_HTTPSVCFILES = Dust.lookup("giskard:10:?");
	MindHandle NET_NAR_HTTPSVCJSONAPI = Dust.lookup("giskard:10:?");
	
	MindHandle NET_NAR_HTTPCLICOMM = Dust.lookup("giskard:10:?");
	
}
