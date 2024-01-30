package hu.sze.milab.dust.machine;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.stream.DustStreamCsvSaxAgent;
import hu.sze.milab.dust.stream.DustStreamFilesystemServer;
import hu.sze.milab.dust.stream.json.DustJsonDomAgent;
import hu.sze.milab.dust.stream.zip.DustZipAgentReader;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsEnumTranslator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustMachineBoot implements DustMachineConsts {
	
	private static DustMachine THE_MACHINE;

	public static void main(String[] args) {
		DustMachine machine = THE_MACHINE = new DustMachine();

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
	}

	static class BootHandles extends HashMap<String, DustHandle> implements Dust.IdResolver {
		private static final long serialVersionUID = 1L;

		Map<String, Integer> cnt = new HashMap<>();

		@Override
		public DustHandle recall(String id) {
			DustHandle mh = get(id);

			if ( null == mh ) {
				if ( id.contains(DUST_SEP_ID) ) {
					String[] ii = id.split(DUST_SEP_ID);
					String lid;
					if ( 1 == ii.length ) {
						Integer c = cnt.getOrDefault(ii[0], 0);
						cnt.put(ii[0], ++c);
						lid = "" + c;
						id += lid;
					} else {
						lid = ii[1];
					}
					mh = new DustHandle(recall(ii[0]), lid);
				} else {
					mh = new DustHandle(id);
					if ( !cnt.containsKey(id) ) {
						cnt.put(id, 0);
					}
				}

				put(id, mh);
			}

			return mh;
		}

		@Override
		public MindHandle recall(MindHandle hUnit, String itemId) {
			return DustException.wrap(null, "Should not get here!" );
		}
	}

	static void boot(DustMachine machine, String[] args) throws Exception {
		BootHandles bh = new BootHandles();

		machine.idRes = bh;

		Map units = new HashMap();
		machine.mainDialog.context.put(DUST_ATT_MACHINE_UNITS, units);
		Map rootKnowledge = new HashMap();
		machine.mainDialog.context.put(MIND_ATT_DIALOG_KNOWLEDGE, rootKnowledge);

		DustUtilsEnumTranslator.register(MindAccess.class, MIND_TAG_ACCESS_CHECK, MIND_TAG_ACCESS_PEEK, MIND_TAG_ACCESS_GET, MIND_TAG_ACCESS_SET, MIND_TAG_ACCESS_INSERT, MIND_TAG_ACCESS_DELETE,
				MIND_TAG_ACCESS_RESET, MIND_TAG_ACCESS_COMMIT);

		DustUtilsEnumTranslator.register(MindAction.class, MIND_TAG_ACTION_INIT, MIND_TAG_ACTION_BEGIN, MIND_TAG_ACTION_PROCESS, 
				MIND_TAG_ACTION_END, MIND_TAG_ACTION_RELEASE);
		
		DustUtilsEnumTranslator.register(MindContext.class, MIND_TAG_CONTEXT_DIALOG, MIND_TAG_CONTEXT_SELF, MIND_TAG_CONTEXT_TARGET, MIND_TAG_CONTEXT_DIRECT);

		DustUtilsAttCache.set(MachineAtts.CreatorAccess, true, MIND_TAG_ACCESS_GET, MIND_TAG_ACCESS_SET, MIND_TAG_ACCESS_INSERT);
		DustUtilsAttCache.set(MachineAtts.CanContinue, true, MIND_TAG_RESULT_READ, MIND_TAG_RESULT_READACCEPT);
		DustUtilsAttCache.set(MachineAtts.PersistentAtt, false, MIND_ATT_KNOWLEDGE_HANDLE, MIND_ATT_UNIT_HANDLES, MIND_ATT_DIALOG_KNOWLEDGE, DUST_ATT_NATIVELOGIC_INSTANCE);
		DustUtilsAttCache.setWithPairs(MachineAtts.PrimaryAspectNames, "ASP", MIND_ASP_ASPECT, "ATT", MIND_ASP_ATTRIBUTE, "UNIT", MIND_ASP_UNIT, "TAG", MIND_ASP_TAG
				, "AGT", MIND_ASP_AGENT, "SRV", MIND_ASP_AGENT
				, "AUTHOR", MIND_ASP_AUTHOR, "MODULE", DUST_ASP_MODULE, "ASSEMBLY", MIND_ASP_ASSEMBLY, "MACHINE", DUST_ASP_MACHINE);

		Map k;
		MindHandle h;

		for (String ui : bh.cnt.keySet()) {
			h = bh.get(ui);
			units.put(ui, h);

			k = new HashMap();
			k.put(MIND_ATT_KNOWLEDGE_HANDLE, h);
			k.put(MIND_ATT_UNIT_AUTHOR, MIND_AUTHOR_DUST);
			k.put(MIND_ATT_UNIT_HANDLES, new HashMap());
			rootKnowledge.put(h, k);
		}

		for (String id : bh.keySet()) {
			if ( id.contains(DUST_SEP_ID) ) {
				String[] ii = id.split(DUST_SEP_ID);
				DustHandle hUnit = bh.get(ii[0]);

				h = bh.get(id);

				k = createKnowledge(hUnit, h, ii[1]);
			}
		}

		machine.idRes = machine.mainDialog;
		
		Dust.access(APP_MACHINE_MAIN, MIND_TAG_ACCESS_SET, APP_ASSEMBLY_MAIN, DUST_ATT_MACHINE_MAINASSEMBLY);
		Dust.access(APP_MACHINE_MAIN, MIND_TAG_ACCESS_SET, APP_MODULE_MAIN, DUST_ATT_MACHINE_MODULES, KEY_ADD);
		
		DustDevUtils.registerNative(RESOURCE_SRV_FILESYSTEM, DUSTJAVA_UNIT, DustStreamFilesystemServer.class.getCanonicalName());
		DustDevUtils.registerNative(RESOURCE_AGT_ZIPREADER, DUSTJAVA_UNIT, DustZipAgentReader.class.getCanonicalName());
		DustDevUtils.registerNative(RESOURCE_AGT_JSONDOM, DUSTJAVA_UNIT, DustJsonDomAgent.class.getCanonicalName());
		DustDevUtils.registerNative(RESOURCE_AGT_CSVSAX, DUSTJAVA_UNIT, DustStreamCsvSaxAgent.class.getCanonicalName());
		
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

	public static Map createKnowledge(DustHandle hUnit, MindHandle h, String localId) {
		Map rootKnowledge = DustUtils.simpleGet(THE_MACHINE.mainDialog.context, MIND_ATT_DIALOG_KNOWLEDGE);
		Map unitHandles = DustUtils.simpleGet(rootKnowledge, hUnit, MIND_ATT_UNIT_HANDLES);

		if ( DustUtils.isEmpty(localId) ) {
			localId = "" + unitHandles.size();
		}
		if ( null == h ) {
			h = new DustHandle(hUnit, localId);
		}
		unitHandles.put(localId, h);

		Map k = new HashMap();
		k.put(MIND_ATT_KNOWLEDGE_HANDLE, h);
		rootKnowledge.put(h, k);
		return k;
	}

}
