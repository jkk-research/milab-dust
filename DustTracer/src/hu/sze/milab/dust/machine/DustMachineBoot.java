package hu.sze.milab.dust.machine;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsEnumTranslator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustMachineBoot implements DustMachineConsts {
	
	private static DustMachine THE_MACHINE;

	public static void main(String[] args) {
		DustMachine machine = THE_MACHINE = new DustMachine();

		try {
			boot(machine);

			Dust.log(EVENT_TAG_TYPE_TRACE, "Machine initializing...");
			machine.agentInit();
			Dust.log(EVENT_TAG_TYPE_TRACE, "Thinking start...");
			machine.agentProcess();
			Dust.log(EVENT_TAG_TYPE_TRACE, "Thinking complete.");
		} catch (Exception e) {
			DustException.swallow(e, "Uncaught exception in the thinking process, exiting.");
		} finally {
			Dust.log(EVENT_TAG_TYPE_TRACE, "Releasing Machine...");
			try {
				machine.agentRelease();
				Dust.log(EVENT_TAG_TYPE_TRACE, "Machine released.");
			} catch (Exception e) {
				DustException.swallow(e, "Uncaught exception during Machine release.");
			}
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
	}

	static void boot(DustMachine machine) throws Exception {
		BootHandles bh = new BootHandles();

		machine.idRes = bh;

		Map units = new HashMap();
		machine.mainDialog.context.put(MIND_ATT_ASSEMBLY_UNITS, units);
		Map rootKnowledge = new HashMap();
		machine.mainDialog.context.put(MIND_ATT_DIALOG_KNOWLEDGE, rootKnowledge);

		DustUtilsEnumTranslator.register(MindAccess.class, MIND_TAG_ACCESS_CHECK, MIND_TAG_ACCESS_PEEK, MIND_TAG_ACCESS_GET, MIND_TAG_ACCESS_SET, MIND_TAG_ACCESS_INSERT, MIND_TAG_ACCESS_DELETE,
				MIND_TAG_ACCESS_RESET, MIND_TAG_ACCESS_COMMIT);

		DustUtilsAttCache.set(MachineAtts.CreatorAccess, true, MIND_TAG_ACCESS_GET, MIND_TAG_ACCESS_SET, MIND_TAG_ACCESS_INSERT);
		DustUtilsAttCache.set(MachineAtts.PersistentAtt, false, MIND_ATT_KNOWLEDGE_HANDLE, MIND_ATT_UNIT_HANDLES, MIND_ATT_DIALOG_KNOWLEDGE);
		DustUtilsAttCache.setWithPairs(MachineAtts.PrimaryAspectNames, "ASP", MIND_ASP_ASPECT, "ATT", MIND_ASP_ATTRIBUTE, "UNIT", MIND_ASP_UNIT, "TAG", MIND_ASP_TAG, "AUTHOR", MIND_ASP_AUTHOR);

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
		
		DustMachineTempUtils.test();
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
