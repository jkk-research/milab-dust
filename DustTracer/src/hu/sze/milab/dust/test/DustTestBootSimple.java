package hu.sze.milab.dust.test;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.machine.DustMachineTempUtils;

public class DustTestBootSimple implements DustTestConsts {
	
	public static void boot(String[] launchParams) throws Exception {
		DustMachineTempUtils.test();
		
		helloWorld();
//		startPortal();
//		helloWorld();
	}
	
	public static void helloWorld() throws Exception {
		MindHandle hLogHelloWorld = DustDevUtils.registerLogic(TEST0_UNIT, DustTestAgentHelloWorld.class.getCanonicalName());
		
		MindHandle hAgtHelloWorld = DustDevUtils.registerAgent(TEST0_UNIT, hLogHelloWorld); 	

		Dust.access(MindAccess.Set, hAgtHelloWorld, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
	}
	
	public static void startPortal() throws Exception {
		MindHandle hAgtSrv = DustDevUtils.registerAgent(TEST0_UNIT, NET_LOG_HTTPSRV); 	
		
		Dust.access(MindAccess.Set, 8090L, hAgtSrv, NET_ATT_HOST_PORT);
		
		Dust.access(MindAccess.Set, hAgtSrv, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
	}
	
}
