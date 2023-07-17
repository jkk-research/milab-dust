package hu.sze.milab.dust.net;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustMetaConsts;

public interface DustNetConsts extends DustMetaConsts {

	String CONTENT_JSON = "application/json";
	int NO_PORT_SET = -1;
	
	MindHandle NET_UNIT = Dust.createHandle();
	
	MindHandle NET_ASP_HOST = Dust.createHandle();
	MindHandle NET_ATT_HOST_PORT = Dust.createHandle();
	MindHandle NET_ATT_HOST_IPV4 = Dust.createHandle();
	
	MindHandle NET_LOG_SRVJETTY = Dust.createHandle();

}
