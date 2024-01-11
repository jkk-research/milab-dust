package hu.sze.milab.dust.machine;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustMachineBoot implements DustMachineConsts {

	public static void main(String[] args) {
		DustMachine machine = new DustMachine();

		boot(machine);

		try {
			Dust.log(EVENT_TAG_TRACE, "Machine initializing...");
			machine.agentInit();
			Dust.log(EVENT_TAG_TRACE, "Thinking start...");
			machine.agentProcess();
			Dust.log(EVENT_TAG_TRACE, "Thinking complete.");
		} catch (Exception e) {
			DustException.swallow(e, "Uncaught exception in the thinking process, exiting.");
		} finally {
			Dust.log(EVENT_TAG_TRACE, "Releasing Machine...");
			try {
				machine.agentRelease();
				Dust.log(EVENT_TAG_TRACE, "Machine released.");
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
					mh = new DustHandle();
				}

				put(id, mh);
			}

			return mh;
		}
	}

	static void boot(DustMachine machine) {
		BootHandles bh = new BootHandles();

		machine.idRes = bh;

		Map units = new HashMap();
		machine.mainDialog.knowledge.put(MIND_ATT_ASSEMBLY_UNITS, units);
		Map rootKnowledge = new HashMap();
		machine.mainDialog.knowledge.put(MIND_ATT_MEMORY_KNOWLEDGE, rootKnowledge);

		Map k;
		MindHandle h;

		for (String ui : bh.cnt.keySet()) {
			h = bh.get(ui);
			units.put(ui, h);

			k = new HashMap();
			k.put(MIND_ATT_KNOWLEDGE_HANDLE, h);
			k.put(MIND_ATT_MEMORY_KNOWLEDGE, new HashMap());
			k.put(MIND_ATT_UNIT_HANDLES, new HashMap());
			rootKnowledge.put(h, k);
		}

		for (String id : bh.keySet()) {
			if ( id.contains(DUST_SEP_ID) ) {
				String[] ii = id.split(DUST_SEP_ID);
				h = bh.get(id);

				MindHandle hUnit = bh.get(ii[0]);
				Map uk = (Map) rootKnowledge.get(hUnit);
				((Map) uk.get(MIND_ATT_UNIT_HANDLES)).put(ii[1], h);

				k = new HashMap();
				k.put(MIND_ATT_KNOWLEDGE_HANDLE, h);
				((Map) uk.get(MIND_ATT_MEMORY_KNOWLEDGE)).put(h, k);
			}
		}

		machine.idRes = machine.mainDialog;
	}
}
