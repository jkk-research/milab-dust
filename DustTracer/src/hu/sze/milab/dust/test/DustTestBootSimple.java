package hu.sze.milab.dust.test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.machine.DustMachineTempMetaGenJavaScript;
import hu.sze.milab.dust.machine.DustMachineTempUtils;

public class DustTestBootSimple implements DustTestConsts {

	public static void boot(String[] launchParams) throws Exception {
		DustMachineTempUtils.test();

		startGui();
//		startPortal();
//		helloWorld();
	}

	public static void helloWorld() throws Exception {
		MindHandle hLogHelloWorld = DustDevUtils.registerLogic(TEST0_UNIT, DustTestAgentHelloWorld.class.getCanonicalName(), "Hello, world LOGIC");

		MindHandle hAgtHelloWorld = DustDevUtils.registerAgent(TEST0_UNIT, hLogHelloWorld);

		Dust.access(MindAccess.Set, hAgtHelloWorld, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
	}

	public static void startGui() throws Exception {
		MindHandle hAgtGuiFrame = DustDevUtils.registerAgent(TEST0_UNIT, MONTRU_NAR_WINDOW, "GUI frame");
		Dust.access(MindAccess.Set, "Nimbus", hAgtGuiFrame, MONTRU_ATT_GUI_THEME);

		Dust.access(MindAccess.Set, "XBRLDock - Graph test", hAgtGuiFrame, MONTRU_ATT_GEN_LABEL);
		Dust.access(MindAccess.Insert, 10, hAgtGuiFrame, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION, KEY_ADD);
		Dust.access(MindAccess.Insert, 10, hAgtGuiFrame, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION, KEY_ADD);
		Dust.access(MindAccess.Insert, 1000, hAgtGuiFrame, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_SIZE, KEY_ADD);
		Dust.access(MindAccess.Insert, 800, hAgtGuiFrame, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_SIZE, KEY_ADD);

		MindHandle hAgtGuiMainPanel = DustDevUtils.registerAgent(TEST0_UNIT, MONTRU_NAR_GRAPH, "Main panel");
		Dust.access(MindAccess.Set, hAgtGuiMainPanel, hAgtGuiFrame, MONTRU_ATT_WINDOW_MAIN);

		MindHandle hUnits = DustDevUtils.newHandle(TEST0_UNIT, MISC_ASP_VARIANT, "unit list");
//		Dust.access(MindAccess.Insert, MIND_UNIT, hUnits, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
//		Dust.access(MindAccess.Insert, MISC_UNIT, hUnits, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, TEXT_UNIT, hUnits, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		
		Dust.access(MindAccess.Set, hUnits, hAgtGuiMainPanel, MISC_ATT_CONN_SOURCE);

		Dust.access(MindAccess.Set, hAgtGuiFrame, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
	}

	public static void startPortal() throws Exception {
		String genBoot = "/script/DustBoot.js";
		String appWebRoot = "web/test01";

		MindHandle hRequest = DustDevUtils.newHandle(TEST0_UNIT, NET_ASP_SRVCALL, "Server call");

		MindHandle hAgtSrv = DustDevUtils.registerAgent(TEST0_UNIT, NET_NAR_HTTPSRV);

		Dust.access(MindAccess.Set, 8090L, hAgtSrv, NET_ATT_HOST_PORT);
		Dust.access(MindAccess.Set, "admin", hAgtSrv, TEXT_ATT_TOKEN);
		Dust.access(MindAccess.Set, hRequest, hAgtSrv, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Insert, hAgtSrv, hAgtSrv, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Insert, hAgtSrv, hAgtSrv, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		MindHandle hAgtJsonapi = DustDevUtils.registerAgent(TEST0_UNIT, NET_NAR_HTTPSVCJSONAPI);

		Dust.access(MindAccess.Set, "jsonapi", hAgtJsonapi, TEXT_ATT_TOKEN);
		Dust.access(MindAccess.Set, hRequest, hAgtJsonapi, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Insert, hAgtJsonapi, hAgtJsonapi, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Insert, hAgtJsonapi, hAgtSrv, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		MindHandle hAgtFile = DustDevUtils.registerAgent(TEST0_UNIT, NET_NAR_HTTPSVCFILES);

		Dust.access(MindAccess.Set, "", hAgtFile, TEXT_ATT_TOKEN);
		Dust.access(MindAccess.Set, "index.html", hAgtFile, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, hRequest, hAgtFile, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Insert, hAgtFile, hAgtFile, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Insert, hAgtFile, hAgtSrv, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		Dust.access(MindAccess.Set, "text/html; charset=utf-8", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "html");
		Dust.access(MindAccess.Set, "text/html; charset=utf-8", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "xhtml");
		Dust.access(MindAccess.Set, "text/css", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "css");
		Dust.access(MindAccess.Set, "application/javascript; charset=utf-8", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "js");
		Dust.access(MindAccess.Set, "application/json; charset=utf-8", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "json");
		Dust.access(MindAccess.Set, "image/png", hAgtFile, RESOURCE_ATT_STREAM_CTYPEMAP, "png");

		MindHandle hDirDustJS = DustDevUtils.registerAgent(TEST0_UNIT, RESOURCE_NAR_FILESYSTEM);
		Dust.access(MindAccess.Set, "web/DustJS", hDirDustJS, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Insert, hDirDustJS, hAgtFile, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		MindHandle hDirTest01 = DustDevUtils.registerAgent(TEST0_UNIT, RESOURCE_NAR_FILESYSTEM);
		Dust.access(MindAccess.Set, appWebRoot, hDirTest01, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Insert, hDirTest01, hAgtFile, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Set, "res/img/favicon.png", hDirTest01, MISC_ATT_CONN_MEMBERMAP, "favicon.ico");

		Dust.access(MindAccess.Set, hAgtFile, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
		Dust.access(MindAccess.Set, hAgtJsonapi, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
		Dust.access(MindAccess.Set, hAgtSrv, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);

		// ------------- Client ----------- //

		MindHandle hModDustJS = DustDevUtils.newHandle(TEST1_UNIT, DUST_ASP_MODULE, "Dust JS module");
		Dust.access(MindAccess.Insert, "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js", hModDustJS, DUST_ATT_MODULE_LIBRARIES, KEY_ADD);
		Dust.access(MindAccess.Insert, "/script/DustHandles.js", hModDustJS, DUST_ATT_MODULE_LIBRARIES, KEY_ADD);
		Dust.access(MindAccess.Insert, genBoot, hModDustJS, DUST_ATT_MODULE_LIBRARIES, KEY_ADD);
		Dust.access(MindAccess.Set, "/script/Dust.js", hModDustJS, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, "modDustNarrative", hModDustJS, TEXT_ATT_TOKEN);
		DustDevUtils.registerNative(DUST_NAR_MACHINE, TEST1_UNIT, hModDustJS, "MachineNarrative");

		MindHandle hModComm = DustDevUtils.newHandle(TEST1_UNIT, DUST_ASP_MODULE, "Comm JS module");
		Dust.access(MindAccess.Set, "/script/DustComm.js", hModComm, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, "modCommNarrative", hModComm, TEXT_ATT_TOKEN);
		DustDevUtils.registerNative(NET_NAR_HTTPCLICOMM, TEST1_UNIT, hModComm, "CommNarrative");

		MindHandle hModMontru = DustDevUtils.newHandle(TEST1_UNIT, DUST_ASP_MODULE, "Montru JS module");
		Dust.access(MindAccess.Set, "/script/DustMontru.js", hModMontru, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, "modMontruNarrative", hModMontru, TEXT_ATT_TOKEN);
		DustDevUtils.registerNative(MONTRU_NAR_GUI, TEST1_UNIT, hModMontru, "GuiNarrative", true);
		DustDevUtils.registerNative(MONTRU_NAR_CONTAINER, TEST1_UNIT, hModMontru, "ContainerNarrative");
		DustDevUtils.registerNative(MONTRU_NAR_AREA, TEST1_UNIT, hModMontru, "AreaNarrative");
		DustDevUtils.registerNative(MONTRU_NAR_GRID, TEST1_UNIT, hModMontru, "GridNarrative");

		MindHandle hModGraph = DustDevUtils.newHandle(TEST1_UNIT, DUST_ASP_MODULE, "Graph JS module");
		Dust.access(MindAccess.Insert, "https://cdnjs.cloudflare.com/ajax/libs/cytoscape/3.28.1/cytoscape.min.js", hModGraph, DUST_ATT_MODULE_LIBRARIES, KEY_ADD);
		Dust.access(MindAccess.Set, "/script/DustGraphCytoscape.js", hModGraph, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, "modCytoscapeNarrative", hModGraph, TEXT_ATT_TOKEN);
		DustDevUtils.registerNative(MONTRU_NAR_GRAPH, TEST1_UNIT, hModGraph, "GraphNarrative");

		MindHandle hWebClient = DustDevUtils.registerAgent(TEST1_UNIT, DUST_NAR_MACHINE, "Web Client logic");
		Dust.access(MindAccess.Set, "index.html", hWebClient, TEXT_ATT_TOKEN);
		DustDevUtils.setText(hWebClient, TEXT_TAG_TYPE_LABEL, TEXT_TAG_LANGUAGE_EN_US, "DustJS Test01");
		Dust.access(MindAccess.Insert, hModDustJS, hWebClient, DUST_ATT_MACHINE_MODULES, KEY_ADD);
		Dust.access(MindAccess.Insert, hModComm, hWebClient, DUST_ATT_MACHINE_MODULES, KEY_ADD);
		Dust.access(MindAccess.Insert, hModMontru, hWebClient, DUST_ATT_MACHINE_MODULES, KEY_ADD);
		Dust.access(MindAccess.Insert, hModGraph, hWebClient, DUST_ATT_MACHINE_MODULES, KEY_ADD);

		Object tSrv = Dust.access(MindAccess.Get, "???", hAgtSrv, TEXT_ATT_TOKEN);
		Object tDA = Dust.access(MindAccess.Get, "???", hAgtJsonapi, TEXT_ATT_TOKEN);

		MindHandle hWebCmdInfo = DustDevUtils.newHandle(TEST1_UNIT, RESOURCE_ASP_URL, "SrvCall info");
		Dust.access(MindAccess.Set, tSrv, hWebCmdInfo, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, "info", hWebCmdInfo, TEXT_ATT_TOKEN);

		MindHandle hWebCmdStop = DustDevUtils.newHandle(TEST1_UNIT, RESOURCE_ASP_URL, "SrvCall stop");
		Dust.access(MindAccess.Set, tSrv, hWebCmdStop, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, "stop", hWebCmdStop, TEXT_ATT_TOKEN);

		MindHandle hWebDataRequest = DustDevUtils.newHandle(TEST1_UNIT, STANDARD_ASP_JSONAPIFETCH, "SrvCall Jsonapi");
		Dust.access(MindAccess.Set, tDA, hWebDataRequest, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Set, "test:0", hWebDataRequest, TEXT_ATT_TOKEN);

		MindHandle hWebSrvResponse = DustDevUtils.newHandle(TEST1_UNIT, NET_ASP_SRVRESP, "Srv Response");

		MindHandle hJsonapiDom = DustDevUtils.newHandle(TEST1_UNIT, STANDARD_ASP_JSONAPIDOM, "Jsonapi Dom");
		Dust.access(MindAccess.Set, "Jsonapi Dom", hJsonapiDom, DEV_ATT_HINT);
		Dust.access(MindAccess.Set, hJsonapiDom, hWebSrvResponse, NET_ATT_SRVRESP_PAYLOAD);

		MindHandle hWebComm = DustDevUtils.registerAgent(TEST1_UNIT, NET_NAR_HTTPCLICOMM, "Comm logic");
		Dust.access(MindAccess.Set, hWebSrvResponse, hWebComm, MISC_ATT_CONN_TARGET);

		Dust.access(MindAccess.Insert, hWebComm, hWebCmdInfo, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Insert, hWebComm, hWebCmdStop, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);

		MindHandle hGetDataBtn = DustDevUtils.registerAgent(TEST1_UNIT, MONTRU_NAR_WIDGET, "Btn logic - Jsonapi");
		Dust.access(MindAccess.Set, hWebDataRequest, hGetDataBtn, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Set, "btnGetData", hGetDataBtn, TEXT_ATT_TOKEN);
		DustDevUtils.setText(hGetDataBtn, TEXT_TAG_TYPE_LABEL, TEXT_TAG_LANGUAGE_EN_US, "Get Data!");
		DustDevUtils.setTag(hGetDataBtn, MONTRU_TAG_WIDGET_BUTTON, MONTRU_TAG_WIDGET);

		MindHandle hSrvInfoBtn = DustDevUtils.registerAgent(TEST1_UNIT, MONTRU_NAR_WIDGET, "Btn logic - info");
		Dust.access(MindAccess.Set, hWebCmdInfo, hSrvInfoBtn, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Set, "btnInfo", hSrvInfoBtn, TEXT_ATT_TOKEN);
		DustDevUtils.setText(hSrvInfoBtn, TEXT_TAG_TYPE_LABEL, TEXT_TAG_LANGUAGE_EN_US, "Server info");
		DustDevUtils.setTag(hSrvInfoBtn, MONTRU_TAG_WIDGET_BUTTON, MONTRU_TAG_WIDGET);

		MindHandle hSrvStopBtn = DustDevUtils.registerAgent(TEST1_UNIT, MONTRU_NAR_WIDGET, "Btn logic - stop");
		Dust.access(MindAccess.Set, hWebCmdStop, hSrvStopBtn, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Set, "btnStop", hSrvStopBtn, TEXT_ATT_TOKEN);
		DustDevUtils.setText(hSrvStopBtn, TEXT_TAG_TYPE_LABEL, TEXT_TAG_LANGUAGE_EN_US, "Server STOP!");
		DustDevUtils.setTag(hSrvStopBtn, MONTRU_TAG_WIDGET_BUTTON, MONTRU_TAG_WIDGET);

		MindHandle hResponseArea = DustDevUtils.registerAgent(TEST1_UNIT, MONTRU_NAR_AREA, "Srv response text area");
		Dust.access(MindAccess.Set, "srvResponse", hResponseArea, TEXT_ATT_TOKEN);
		Dust.access(MindAccess.Insert, hResponseArea, hWebSrvResponse, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);

		MindHandle hRequestPropertiesGrid = DustDevUtils.registerAgent(TEST1_UNIT, MONTRU_NAR_GRID);
		Dust.access(MindAccess.Set, "fetchParams", hRequestPropertiesGrid, TEXT_ATT_TOKEN);
		MindHandle hRequestPropertiesRows = DustDevUtils.newHandle(TEST1_UNIT, MISC_ASP_CONN, "Grid rows");
		Dust.access(MindAccess.Insert, RESOURCE_ATT_URL_PATH, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, TEXT_ATT_TOKEN, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, STANDARD_ATT_JSONAPIFETCH_INCLUDE, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, STANDARD_ATT_JSONAPIFETCH_FIELDS, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, STANDARD_ATT_JSONAPIFETCH_FILTER, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
//		Dust.access(MindAccess.Insert, STANDARD_ATT_JSONAPIFETCH_SORT, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
//		Dust.access(MindAccess.Insert, STANDARD_ATT_JSONAPIFETCH_PAGELIMIT, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
//		Dust.access(MindAccess.Insert, STANDARD_ATT_JSONAPIFETCH_PAGEOFFSET, hRequestPropertiesRows, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		MindHandle hRequestPropertiesCols = DustDevUtils.newHandle(TEST1_UNIT, MISC_ASP_CONN, "Grid cols");
		Dust.access(MindAccess.Insert, hWebDataRequest, hRequestPropertiesCols, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		Dust.access(MindAccess.Set, hRequestPropertiesRows, hRequestPropertiesGrid, MONTRU_ATT_GRID_AXES, GEOMETRY_TAG_VALTYPE_CARTESIAN_Y);
		Dust.access(MindAccess.Set, hRequestPropertiesCols, hRequestPropertiesGrid, MONTRU_ATT_GRID_AXES, GEOMETRY_TAG_VALTYPE_CARTESIAN_X);

		Dust.access(MindAccess.Insert, hWebComm, hWebDataRequest, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Insert, hRequestPropertiesGrid, hWebDataRequest, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);

		Dust.access(MindAccess.Insert, hWebClient, hJsonapiDom, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);

		MindHandle hContButtons = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_NAR_CONTAINER, "button panel");
		DustDevUtils.setTag(hContButtons, MONTRU_TAG_LAYOUT_PAGE, MONTRU_TAG_LAYOUT);

		DustDevUtils.setTag(hGetDataBtn, MONTRU_TAG_LINE_LEAD, MONTRU_TAG_LINE);
		DustDevUtils.setTag(hSrvInfoBtn, MONTRU_TAG_LINE_TAIL, MONTRU_TAG_LINE);
		DustDevUtils.setTag(hSrvStopBtn, MONTRU_TAG_LINE_TAIL, MONTRU_TAG_LINE);

		Dust.access(MindAccess.Insert, hGetDataBtn, hContButtons, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hSrvInfoBtn, hContButtons, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hSrvStopBtn, hContButtons, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		MindHandle hGraph = DustDevUtils.registerAgent(TEST1_UNIT, MONTRU_NAR_GRAPH, "Graph");
		Dust.access(MindAccess.Set, "graph", hGraph, TEXT_ATT_TOKEN);

		MindHandle hContMain = DustDevUtils.newHandle(TEST1_UNIT, MONTRU_NAR_CONTAINER, "resp/graph panel");
		DustDevUtils.setTag(hContMain, MONTRU_TAG_LAYOUT_PAGE, MONTRU_TAG_LAYOUT);
		
		DustDevUtils.setTag(hResponseArea, MONTRU_TAG_LINE_LEAD, MONTRU_TAG_LINE);
		DustDevUtils.setTag(hGraph, MONTRU_TAG_PAGE_CENTER, MONTRU_TAG_PAGE);
		
		Dust.access(MindAccess.Insert, hResponseArea, hContMain, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hGraph, hContMain, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		MindHandle hGui = DustDevUtils.registerAgent(TEST1_UNIT, MONTRU_NAR_GUI, "Web GUI");
		DustDevUtils.setTag(hGui, MONTRU_TAG_LAYOUT_BOX, MONTRU_TAG_LAYOUT);
		DustDevUtils.setTag(hGui, MONTRU_TAG_BOX_PAGE, MONTRU_TAG_BOX);
		
		Dust.access(MindAccess.Insert, hRequestPropertiesGrid, hGui, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hContButtons, hGui, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		Dust.access(MindAccess.Insert, hContMain, hGui, MISC_ATT_CONN_MEMBERARR, KEY_ADD);

		MindHandle hGuiSel = DustDevUtils.newHandle(TEST1_UNIT, MISC_ASP_CONN, "Selected items");
		DustDevUtils.setTag(hGuiSel, MISC_TAG_HIDDEN);
		Dust.access(MindAccess.Set, hGuiSel, hGui, MONTRU_ATT_GUI_SELECTED);

		Dust.access(MindAccess.Insert, hGraph, hGui, MIND_ATT_KNOWLEDGE_LISTENERS, KEY_ADD);
		Dust.access(MindAccess.Set, hGui, hWebClient, MISC_ATT_CONN_TARGET);

		Map<String, MindHandle> bootNodes = new TreeMap<>();

		bootNodes.put("narMachine", hWebClient);
		bootNodes.put("dataBulkLoad", hJsonapiDom);
		bootNodes.put("narComm", hWebComm);
		bootNodes.put("dataSrvReq", hWebDataRequest);
		bootNodes.put("narGui", hGui);

		bootNodes.put("modDust", hModDustJS);
		bootNodes.put("modComm", hModComm);
		bootNodes.put("modMontru", hModMontru);
		bootNodes.put("modGraph", hModGraph);

		String bootJS = DustMachineTempMetaGenJavaScript.genJSObject(genBoot, bootNodes);

		File appScript = new File(appWebRoot, "script");
		appScript.mkdirs();

		try (PrintWriter out = new PrintWriter(appWebRoot + genBoot)) {
			out.println(bootJS);
			out.flush();
		}
	}

}
