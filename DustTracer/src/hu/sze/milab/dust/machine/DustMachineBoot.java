package hu.sze.milab.dust.machine;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.dev.DustDevNarrative;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.dev.forge.DustDevNarrativeForgeUI;
import hu.sze.milab.dust.event.DustEventNarrative;
import hu.sze.milab.dust.misc.DustMiscNarrative;
import hu.sze.milab.dust.montru.DustMontruNarrativeContainer;
import hu.sze.milab.dust.montru.DustMontruNarrativeFrame;
import hu.sze.milab.dust.montru.DustMontruNarrativeGraph;
import hu.sze.milab.dust.montru.DustMontruNarrativeGrid;
import hu.sze.milab.dust.montru.DustMontruNarrativeUnitgraph;
import hu.sze.milab.dust.montru.DustMontruNarrativeWidget;
import hu.sze.milab.dust.mvel.DustMvelNarrative;
import hu.sze.milab.dust.net.DustNetDownloadAgent;
import hu.sze.milab.dust.net.httpsrv.DustHttpFileAgent;
import hu.sze.milab.dust.net.httpsrv.DustHttpJsonapiAgent;
import hu.sze.milab.dust.net.httpsrv.DustHttpServerJetty;
import hu.sze.milab.dust.stream.DustStreamCache;
import hu.sze.milab.dust.stream.DustStreamCsvSaxAgent;
import hu.sze.milab.dust.stream.DustStreamFilesystemServer;
import hu.sze.milab.dust.stream.json.DustJsonDomAgent;
import hu.sze.milab.dust.stream.xml.DustXmlDomAgent;
import hu.sze.milab.dust.stream.zip.DustZipAgentReader;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsEnumTranslator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustMachineBoot implements DustMachineConsts {
	
	public static void main(String[] args) {
		long ts = System.currentTimeMillis();
		DustMachine machine = new DustMachine();

		try {
			boot(machine, args);

			Dust.log(EVENT_TAG_TYPE_TRACE, "Machine initializing...");
			machine.agentInit();
			Dust.log(EVENT_TAG_TYPE_TRACE, "Thinking start...");
			machine.agentBegin();
			Dust.log(EVENT_TAG_TYPE_TRACE, "Thinking complete.");
		} catch (Exception e) {
			DustException.swallow(e, "Uncaught exception in the thinking process, exiting.");
		}
		
		Dust.log(EVENT_TAG_TYPE_TRACE, "Total runtime: " + (System.currentTimeMillis() - ts) + " msec.");
	}

	static class BootHandles extends HashMap<String, DustHandle> implements IdResolver {
		private static final long serialVersionUID = 1L;

		Map<String, Integer> cnt = new HashMap<>();

		@Override
		public DustHandle recall(String id) {
			DustHandle mh = get(id);

			if ( null == mh ) {				
				String[] ii = id.split(DUST_SEP_ID);
				
				if ( 2 == ii.length ) {
					mh = new DustHandle(id);
					if ( !cnt.containsKey(id) ) {
						cnt.put(id, 0);
					}
				} else {
					String locID = ii[2];
					String pID = DustUtils.cutPostfix(id, DUST_SEP_ID);
					
					Integer c = cnt.getOrDefault(pID, 0);
					if ( ITEMID_NEW.equals(locID) ) {
						locID = c.toString();
						id = pID + DUST_SEP_ID + locID;
					}
					cnt.put(pID, ++c);

					mh = new DustHandle(id);
				}

				put(id, mh);
			}

			return mh;
		}
	}

	static void boot(DustMachine machine, String[] args) throws Exception {
		BootHandles bh = new BootHandles();

		machine.idRes = bh;

		DustUtilsEnumTranslator.register(MindValType.class, MIND_TAG_VALTYPE_INT, MIND_TAG_VALTYPE_REAL, MIND_TAG_VALTYPE_HANDLE, MIND_TAG_VALTYPE_BIN);
		
		DustUtilsEnumTranslator.register(MindCollType.class, MIND_TAG_COLLTYPE_ONE, MIND_TAG_COLLTYPE_SET, MIND_TAG_COLLTYPE_ARR, MIND_TAG_COLLTYPE_MAP);

		DustUtilsEnumTranslator.register(MindAccess.class, MIND_TAG_ACCESS_CHECK, MIND_TAG_ACCESS_PEEK, MIND_TAG_ACCESS_GET, MIND_TAG_ACCESS_SET, MIND_TAG_ACCESS_INSERT, MIND_TAG_ACCESS_DELETE,
				MIND_TAG_ACCESS_RESET, MIND_TAG_ACCESS_COMMIT, MIND_TAG_ACCESS_BROADCAST, MIND_TAG_ACCESS_LOOKUP, MIND_TAG_ACCESS_VISIT);

		DustUtilsEnumTranslator.register(MindAction.class, MIND_TAG_ACTION_INIT, MIND_TAG_ACTION_BEGIN, MIND_TAG_ACTION_PROCESS, 
				MIND_TAG_ACTION_END, MIND_TAG_ACTION_RELEASE);
		
		DustUtilsEnumTranslator.register(MindContext.class, MIND_TAG_CONTEXT_ACTION, MIND_TAG_CONTEXT_SELF, MIND_TAG_CONTEXT_TARGET, MIND_TAG_CONTEXT_DIALOG, MIND_TAG_CONTEXT_DIRECT,
				 MIND_TAG_CONTEXT_VISITITEM, MIND_TAG_CONTEXT_VISITATT, MIND_TAG_CONTEXT_VISITKEY, MIND_TAG_CONTEXT_VISITVALUE);
		
		DustUtilsEnumTranslator.register(VisitFollowRef.class, MIND_TAG_VISITFOLLOWREF_NO, MIND_TAG_VISITFOLLOWREF_ONCE, MIND_TAG_VISITFOLLOWREF_ALWAYS);
		
		DustUtilsAttCache.set(MachineAtts.CreatorAccess, true, MindAccess.Get, MindAccess.Set, MindAccess.Insert);
		DustUtilsAttCache.set(MachineAtts.CanContinue, true, MIND_TAG_RESULT_READ, MIND_TAG_RESULT_READACCEPT);
		DustUtilsAttCache.set(MachineAtts.TransientAtt, true, MIND_ATT_KNOWLEDGE_HANDLE, MIND_ATT_UNIT_HANDLES, MIND_ATT_UNIT_CONTENT, DUST_ATT_IMPL_INSTANCE, DUST_ATT_MACHINE_ACTIVE_SERVERS,
				NET_ATT_SRVCALL_REQUEST, NET_ATT_SRVCALL_RESPONSE);
		DustUtilsAttCache.setWithPairs(MachineAtts.PrimaryAspectNames, "ASP", MIND_ASP_ASPECT, "ATT", MIND_ASP_ATTRIBUTE, "UNIT", MIND_ASP_UNIT, "TAG", MIND_ASP_TAG
				, "AGT", MIND_ASP_AGENT, "NAR", MIND_ASP_NARRATIVE
				, "AUTHOR", MIND_ASP_AUTHOR, "MODULE", DUST_ASP_MODULE, "ASSEMBLY", MIND_ASP_ASSEMBLY, "MACHINE", DUST_NAR_MACHINE);
		
		// TODO not good... fix: ensure getUnit call on units when resolving from file...
		Dust.log(EVENT_ASP_EVENT, NET_NAR_HTTPSRV);
		Dust.log(EVENT_ASP_EVENT, STANDARD_ASP_JSONAPIFETCH);

		machine.bootInit(bh);
		
		// core narratives
		DustDevUtils.registerNative(MONTRU_NAR_WINDOW, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustMontruNarrativeFrame.class.getName(), true);
		DustDevUtils.registerNative(MONTRU_NAR_CONTAINER, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustMontruNarrativeContainer.class.getName());
		DustDevUtils.registerNative(MONTRU_NAR_WIDGET, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustMontruNarrativeWidget.class.getName());
		DustDevUtils.registerNative(MONTRU_NAR_GRID, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustMontruNarrativeGrid.class.getName());
		DustDevUtils.registerNative(MONTRU_NAR_GRAPH, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustMontruNarrativeGraph.class.getName());
		DustDevUtils.registerNative(MONTRU_NAR_UNITGRAPH, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustMontruNarrativeUnitgraph.class.getName());
		
		DustDevUtils.registerNative(MISC_NAR_COUNTER, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustDevNarrative.DevCounter.class.getName());
		DustDevUtils.registerNative(MISC_NAR_TABLE, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustMiscNarrative.TableAgent.class.getName());
		
		DustDevUtils.registerNative(EXPR_NAR_POPULATE, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustMvelNarrative.PopulateAgent.class.getName());
		DustDevUtils.registerNative(EXPR_NAR_FILTER, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustMvelNarrative.FilterAgent.class.getName());
		DustDevUtils.registerNative(EVENT_NAR_THROTTLE, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustEventNarrative.Throttle.class.getName());
		
		DustDevUtils.registerNative(RESOURCE_NAR_FILESYSTEM, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustStreamFilesystemServer.class.getName(), true);
		DustDevUtils.registerNative(RESOURCE_NAR_CACHE, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustStreamCache.class.getName(), true);

		DustDevUtils.registerNative(RESOURCE_NAR_ZIPREADER, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustZipAgentReader.class.getName());
		DustDevUtils.registerNative(RESOURCE_NAR_XMLDOM, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustXmlDomAgent.class.getName());
		DustDevUtils.registerNative(RESOURCE_NAR_JSONDOM, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustJsonDomAgent.class.getName());
		DustDevUtils.registerNative(RESOURCE_NAR_CSVSAX, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustStreamCsvSaxAgent.class.getName());

		DustDevUtils.registerNative(NET_NAR_DOWNLOAD, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustNetDownloadAgent.class.getName(), true);
		DustDevUtils.registerNative(NET_NAR_HTTPSRV, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustHttpServerJetty.class.getName(), true);
		DustDevUtils.registerNative(NET_NAR_HTTPSVCFILES, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustHttpFileAgent.class.getName());
		DustDevUtils.registerNative(NET_NAR_HTTPSVCJSONAPI, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustHttpJsonapiAgent.class.getName());
		
		DustDevUtils.registerNative(DEV_NAR_FORGEUI, DUSTJAVA_UNIT, APP_MODULE_MAIN, DustDevNarrativeForgeUI.class.getName());
		
		// other info

		DustDevUtils.setTag(MISC_ATT_CONN_MEMBERMAP, MIND_TAG_COLLTYPE_MAP, MIND_TAG_COLLTYPE);
		DustDevUtils.setTag(MISC_ATT_CONN_MEMBERARR, MIND_TAG_COLLTYPE_ARR, MIND_TAG_COLLTYPE);
		DustDevUtils.setTag(MISC_ATT_CONN_MEMBERSET, MIND_TAG_COLLTYPE_SET, MIND_TAG_COLLTYPE);
		
		DustDevUtils.setTag(MISC_ATT_CONN_MEMBERMAP, MIND_TAG_VALTYPE_HANDLE, MIND_TAG_VALTYPE);
		DustDevUtils.setTag(MISC_ATT_CONN_MEMBERARR, MIND_TAG_VALTYPE_HANDLE, MIND_TAG_VALTYPE);
		DustDevUtils.setTag(MISC_ATT_CONN_MEMBERSET, MIND_TAG_VALTYPE_HANDLE, MIND_TAG_VALTYPE);

		MindHandle hFact;
		MindHandle hAgtUnitGraph = DustDevUtils.registerAgent(MIND_UNIT, MONTRU_NAR_UNITGRAPH);
		
		hFact = DustDevUtils.newHandle(MIND_UNIT, MIND_ASP_FACTORY);
		Dust.access(MindAccess.Set, GEOMETRY_ASP_GRAPH, hFact, MIND_ATT_FACTORY_PRIMARYASPECT);
		Dust.access(MindAccess.Set, hAgtUnitGraph, hFact, MIND_ATT_FACTORY_NARRATIVE);
		Dust.access(MindAccess.Insert, MISC_ATT_CONN_SOURCE, hFact, MIND_ATT_FACTORY_DEFATTS);
		Dust.access(MindAccess.Set, hFact, DEV_NAR_FORGEUI, MIND_ATT_ASPECT_ATTFACTORIES, MISC_ATT_CONN_MEMBERMAP);

		hFact = DustDevUtils.newHandle(MIND_UNIT, MIND_ASP_FACTORY);
		Dust.access(MindAccess.Set, GEOMETRY_ASP_NODE, hFact, MIND_ATT_FACTORY_PRIMARYASPECT);
		Dust.access(MindAccess.Insert, MISC_ATT_CONN_SOURCE, hFact, MIND_ATT_FACTORY_DEFATTS);
		Dust.access(MindAccess.Set, hFact, GEOMETRY_ASP_GRAPH, MIND_ATT_ASPECT_ATTFACTORIES, MISC_ATT_CONN_MEMBERMAP);

		
		String bootClass = System.getProperty("DustBootClass");
		
		if ( !DustUtils.isEmpty(bootClass)) {
			Class bc = Class.forName(bootClass);
			Method bm = bc.getMethod("boot", String[].class);
			Object[] params = { args };
			bm.invoke(null, params);
		} else {
			Dust.log(EVENT_TAG_TYPE_INFO, "Launch without boot class");
		}
	}
}
