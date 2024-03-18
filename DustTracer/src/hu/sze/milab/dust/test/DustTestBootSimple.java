package hu.sze.milab.dust.test;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.machine.DustMachineTempUtils;

public class DustTestBootSimple implements DustTestConsts {
	
	public static void boot(String[] launchParams) throws Exception {
		DustMachineTempUtils.test();
		
		startPortal();
//		helloWorld();
	}
	
	public static void helloWorld() throws Exception {
		MindHandle hLogHelloWorld = DustDevUtils.registerLogic(TEST0_UNIT, DustTestAgentHelloWorld.class.getCanonicalName());
		
		MindHandle hAgtHelloWorld = DustDevUtils.registerAgent(TEST0_UNIT, hLogHelloWorld); 	

		Dust.access(MindAccess.Set, hAgtHelloWorld, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
	}
	
	public static void defineClient() throws Exception {
		MindHandle hModDustJS = DustDevUtils.newHandle(TEST0_UNIT, DUST_ASP_MODULE);
		Dust.access(MindAccess.Insert, "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js", hModDustJS, DUST_ATT_MODULE_LIBRARIES, KEY_ADD);
		Dust.access(MindAccess.Set, "script/Dust.js", hModDustJS, RESOURCE_ATT_URL_PATH);
		
		MindHandle hModChart = DustDevUtils.newHandle(TEST0_UNIT, DUST_ASP_MODULE);
		Dust.access(MindAccess.Insert, "https://cdnjs.cloudflare.com/ajax/libs/cytoscape/3.28.1/cytoscape.min.js", hModChart, DUST_ATT_MODULE_LIBRARIES, KEY_ADD);
		Dust.access(MindAccess.Set, "script/DustGraphCytoscape.js", hModChart, RESOURCE_ATT_URL_PATH);
		
		MindHandle hModClient = DustDevUtils.newHandle(TEST0_UNIT, DUST_ASP_MACHINE);
		DustDevUtils.setText(hModClient, TEXT_TAG_TYPE_LABEL, TEXT_TAG_LANGUAGE_EN_US, "DustJS Test01");
		Dust.access(MindAccess.Insert, hModDustJS, hModClient, DUST_ATT_MACHINE_MODULES, KEY_ADD);
		Dust.access(MindAccess.Insert, hModChart, hModClient, DUST_ATT_MACHINE_MODULES, KEY_ADD);

	}
	
	public static void startPortal() throws Exception {
		MindHandle hRequest = DustDevUtils.newHandle(TEST0_UNIT, NET_ASP_SRVCALL);

		MindHandle hAgtSrv = DustDevUtils.registerAgent(TEST0_UNIT, NET_LOG_HTTPSRV); 	

		Dust.access(MindAccess.Set, 8090L, hAgtSrv, NET_ATT_HOST_PORT);
		Dust.access(MindAccess.Set, "admin", hAgtSrv, TEXT_ATT_TOKEN);
		Dust.access(MindAccess.Set, hRequest, hAgtSrv, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Insert, hAgtSrv, hAgtSrv, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Insert, hAgtSrv, hAgtSrv, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		
		MindHandle hAgtJsonapi = DustDevUtils.registerAgent(TEST0_UNIT, NET_LOG_HTTPSVCJSONAPI); 	

		Dust.access(MindAccess.Set, "jsonapi", hAgtJsonapi, TEXT_ATT_TOKEN);
		Dust.access(MindAccess.Set, hRequest, hAgtJsonapi, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Insert, hAgtJsonapi, hAgtJsonapi, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Insert, hAgtJsonapi, hAgtSrv, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		
		MindHandle hAgtFile = DustDevUtils.registerAgent(TEST0_UNIT, NET_LOG_HTTPSVCFILES); 	

		Dust.access(MindAccess.Set, "", hAgtFile, TEXT_ATT_TOKEN);
		Dust.access(MindAccess.Set, "index.html", hAgtFile, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, hRequest, hAgtFile, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Insert, hAgtFile, hAgtFile, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Insert, hAgtFile, hAgtSrv, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		
		Dust.access(MindAccess.Set, "text/html; charset=utf-8", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "html");
		Dust.access(MindAccess.Set, "text/html; charset=utf-8", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "xhtml");
		Dust.access(MindAccess.Set, "application/javascript; charset=utf-8", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "js");
		Dust.access(MindAccess.Set, "application/json; charset=utf-8", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "json");
		Dust.access(MindAccess.Set, "image/png", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "png");

		MindHandle hDirDustJS = DustDevUtils.registerAgent(TEST0_UNIT, RESOURCE_LOG_FILESYSTEM);
		Dust.access(MindAccess.Set, "web/DustJS", hDirDustJS, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Insert, hDirDustJS, hAgtFile, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		MindHandle hDirTest01 = DustDevUtils.registerAgent(TEST0_UNIT, RESOURCE_LOG_FILESYSTEM);
		Dust.access(MindAccess.Set, "web/test01", hDirTest01, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Insert, hDirTest01, hAgtFile, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Set, "res/img/favicon.png", hDirTest01, MISC_ATT_CONN_MEMBERMAP, "favicon.ico");
		
		Dust.access(MindAccess.Set, hAgtFile, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
		Dust.access(MindAccess.Set, hAgtJsonapi, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
		Dust.access(MindAccess.Set, hAgtSrv, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
	}
	
}
