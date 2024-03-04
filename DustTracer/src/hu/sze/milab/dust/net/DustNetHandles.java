package hu.sze.milab.dust.net;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;

public interface DustNetHandles extends DustConsts {
	
	MindHandle NET_UNIT = Dust.lookup("giskard.me:10");

	MindHandle NET_ASP_HOST = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_HOST_PORT = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_HOST_IPV4 = Dust.lookup("giskard.me:10:?");

	MindHandle NET_ASP_SSLINFO = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SSLINFO_PORT = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SSLINFO_STOREPATH = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SSLINFO_STOREPASS = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SSLINFO_KEYMANAGERPASS = Dust.lookup("giskard.me:10:?");

	MindHandle NET_ASP_SRVCALL = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SRVCALL_REQUEST = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SRVCALL_RESPONSE = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SRVCALL_METHOD = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SRVCALL_PATHINFO = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SRVCALL_PAYLOAD = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SRVCALL_HEADERS = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SRVCALL_ATTRIBUTES = Dust.lookup("giskard.me:10:?");
	MindHandle NET_ATT_SRVCALL_STATUS = Dust.lookup("giskard.me:10:?");

	MindHandle NET_LOG_HTTPSRV = Dust.lookup("giskard.me:10:?");
	MindHandle NET_LOG_HTTPSVCFILES = Dust.lookup("giskard.me:10:?");
	MindHandle NET_LOG_HTTPSVCJSONAPI = Dust.lookup("giskard.me:10:?");
	
}
