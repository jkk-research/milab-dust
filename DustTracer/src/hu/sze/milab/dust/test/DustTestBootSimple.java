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
		
		
		// ------------- Client ----------- //
		
		MindHandle hModDustJS = DustDevUtils.newHandle(TEST1_UNIT, DUST_ASP_MODULE);
		Dust.access(MindAccess.Insert, "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js", hModDustJS, DUST_ATT_MODULE_LIBRARIES, KEY_ADD);
		Dust.access(MindAccess.Set, "script/Dust.js", hModDustJS, RESOURCE_ATT_URL_PATH);
		
		MindHandle hModChart = DustDevUtils.newHandle(TEST1_UNIT, DUST_ASP_MODULE);
		Dust.access(MindAccess.Insert, "https://cdnjs.cloudflare.com/ajax/libs/cytoscape/3.28.1/cytoscape.min.js", hModChart, DUST_ATT_MODULE_LIBRARIES, KEY_ADD);
		Dust.access(MindAccess.Set, "script/DustGraphCytoscape.js", hModChart, RESOURCE_ATT_URL_PATH);
		
		MindHandle hWebClient = DustDevUtils.newHandle(TEST1_UNIT, DUST_ASP_MACHINE);
		Dust.access(MindAccess.Set, "index.html", hWebClient, TEXT_ATT_TOKEN);
		DustDevUtils.setText(hWebClient, TEXT_TAG_TYPE_LABEL, TEXT_TAG_LANGUAGE_EN_US, "DustJS Test01");
		Dust.access(MindAccess.Insert, hModDustJS, hWebClient, DUST_ATT_MACHINE_MODULES, KEY_ADD);
		Dust.access(MindAccess.Insert, hModChart, hWebClient, DUST_ATT_MACHINE_MODULES, KEY_ADD);
		
		MindHandle hLabels = DustDevUtils.newHandle(TEST1_UNIT, MISC_ASP_CONN);

		
		Object tSrv = Dust.access(MindAccess.Get, "???", hAgtSrv, TEXT_ATT_TOKEN);
		Object tDA = Dust.access(MindAccess.Get, "???", hAgtJsonapi, TEXT_ATT_TOKEN);

		
		MindHandle hWebCmdInfo = DustDevUtils.newHandle(TEST1_UNIT, RESOURCE_ASP_URL);
		Dust.access(MindAccess.Set, tSrv + "/info", hWebCmdInfo, RESOURCE_ATT_URL_PATH);

		MindHandle hWebCmdStop = DustDevUtils.newHandle(TEST1_UNIT, RESOURCE_ASP_URL);
		Dust.access(MindAccess.Set, tSrv + "/stop", hWebCmdStop, RESOURCE_ATT_URL_PATH);
		
		MindHandle hWebDataRequest = DustDevUtils.newHandle(TEST1_UNIT, JSONAPI_ASP_FETCHPARAMS);
		Dust.access(MindAccess.Set, "giskard", hWebDataRequest, RESOURCE_ATT_URL_PATH);
		
		MindHandle hWebSrvResponseTxt = DustDevUtils.newHandle(TEST1_UNIT, TEXT_ASP_PLAIN);


		MindHandle hWebDataAccess = DustDevUtils.newHandle(TEST1_UNIT, NET_LOG_HTTPCLIJSONAPI);
		Dust.access(MindAccess.Set, tDA, hWebDataAccess, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, hWebSrvResponseTxt, hWebDataAccess, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Set, hWebDataAccess, hWebClient, DUST_ATT_MACHINE_UNIT_RESOLVER);
		Dust.access(MindAccess.Insert, hWebDataRequest, hWebDataAccess, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		
		MindHandle hWebDirectRequest = DustDevUtils.newHandle(TEST1_UNIT, NET_LOG_HTTPCLIDIRECT);
		Dust.access(MindAccess.Set, hWebSrvResponseTxt, hWebDirectRequest, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Insert, hWebCmdInfo, hWebDirectRequest, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Insert, hWebCmdStop, hWebDirectRequest, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		

		MindHandle hGetDataBtn = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_ASP_WIDGET);
		Dust.access(MindAccess.Set, hWebDataRequest, hGetDataBtn, MISC_ATT_CONN_TARGET);
		DustDevUtils.setText(hGetDataBtn, TEXT_TAG_TYPE_LABEL, TEXT_TAG_LANGUAGE_EN_US, "Get Data!");
		DustDevUtils.setTag(hGetDataBtn, MONTRU_TAG_WIDGET_BUTTON, MONTRU_TAG_WIDGET);
		DustDevUtils.setTag(hGetDataBtn, MONTRU_TAG_PAGE_LEAD, MONTRU_TAG_PAGE);

		MindHandle hSrvInfoBtn = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_ASP_WIDGET);
		Dust.access(MindAccess.Set, hWebCmdInfo, hSrvInfoBtn, MISC_ATT_CONN_TARGET);
		DustDevUtils.setText(hSrvInfoBtn, TEXT_TAG_TYPE_LABEL, TEXT_TAG_LANGUAGE_EN_US, "Server info");
		DustDevUtils.setTag(hSrvInfoBtn, MONTRU_TAG_WIDGET_BUTTON, MONTRU_TAG_WIDGET);
		DustDevUtils.setTag(hSrvInfoBtn, MONTRU_TAG_PAGE_TAIL, MONTRU_TAG_PAGE);
		
		MindHandle hSrvStopBtn = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_ASP_WIDGET);
		Dust.access(MindAccess.Set, hWebCmdStop, hSrvStopBtn, MISC_ATT_CONN_TARGET);
		DustDevUtils.setText(hSrvStopBtn, TEXT_TAG_TYPE_LABEL, TEXT_TAG_LANGUAGE_EN_US, "Server STOP!");
		DustDevUtils.setTag(hSrvStopBtn, MONTRU_TAG_WIDGET_BUTTON, MONTRU_TAG_WIDGET);
		DustDevUtils.setTag(hSrvStopBtn, MONTRU_TAG_PAGE_TAIL, MONTRU_TAG_PAGE);

		MindHandle hResponseArea = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_ASP_WIDGET);
		Dust.access(MindAccess.Set, hWebSrvResponseTxt, hResponseArea, MISC_ATT_CONN_TARGET);
		DustDevUtils.setTag(hResponseArea, MONTRU_TAG_WIDGET_TEXTAREA, MONTRU_TAG_WIDGET);
		DustDevUtils.setTag(hResponseArea, MISC_TAG_READONLY);
		DustDevUtils.setTag(hResponseArea, MONTRU_TAG_PAGE_LEAD, MONTRU_TAG_PAGE);


		MindHandle hRequestPropertiesGrid = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_ASP_GRID);
		MindHandle hRequestPropertiesRows = DustDevUtils.newHandle(TEST1_UNIT, MISC_ASP_CONN);
		Dust.access(MindAccess.Insert, RESOURCE_ATT_URL_PATH, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, JSONAPI_ATT_FETCHPARAMS_INCLUDE, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, JSONAPI_ATT_FETCHPARAMS_FIELDS, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, JSONAPI_ATT_FETCHPARAMS_FILTER, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, JSONAPI_ATT_FETCHPARAMS_SORT, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, JSONAPI_ATT_FETCHPARAMS_PAGELIMIT, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, JSONAPI_ATT_FETCHPARAMS_PAGEOFFSET, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		MindHandle hRequestPropertiesCols = DustDevUtils.newHandle(TEST1_UNIT, MISC_ASP_CONN);
		Dust.access(MindAccess.Insert, hLabels, hRequestPropertiesCols, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hWebDataRequest, hRequestPropertiesCols, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		Dust.access(MindAccess.Set, hRequestPropertiesRows, hRequestPropertiesGrid, MONTRU_ATT_GRID_AXES, GEOMETRY_TAG_VALTYPE_CARTESIAN_Y);
		Dust.access(MindAccess.Set, hRequestPropertiesCols, hRequestPropertiesGrid, MONTRU_ATT_GRID_AXES, GEOMETRY_TAG_VALTYPE_CARTESIAN_X);

		MindHandle hContButtons = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_ASP_CONTAINER);
		DustDevUtils.setTag(hContButtons, MONTRU_TAG_LAYOUT_PAGE, MONTRU_TAG_LAYOUT);
		Dust.access(MindAccess.Insert, hGetDataBtn, hContButtons, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hSrvInfoBtn, hContButtons, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hSrvStopBtn, hContButtons, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		MindHandle hGraph = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_LOG_GRAPH);
		DustDevUtils.setTag(hGraph, MONTRU_TAG_PAGE_CENTER, MONTRU_TAG_PAGE);

		MindHandle hContMain = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_ASP_CONTAINER);
		DustDevUtils.setTag(hContMain, MONTRU_TAG_LAYOUT_PAGE, MONTRU_TAG_LAYOUT);
		Dust.access(MindAccess.Insert, hResponseArea, hContMain, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hGraph, hContMain, MISC_ATT_CONN_MEMBERARR, KEY_ADD);


		MindHandle hPage = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_LOG_WINDOW);
		DustDevUtils.setTag(hPage, MONTRU_TAG_LAYOUT_FLOW, MONTRU_TAG_LAYOUT);
		DustDevUtils.setTag(hPage, MONTRU_TAG_FLOW_PAGE, MONTRU_TAG_FLOW);
		
		Dust.access(MindAccess.Insert, hRequestPropertiesGrid, hPage, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hContButtons, hPage, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hContMain, hPage, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
	}
	
}
