package hu.sze.milab.dust.machine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsEnumTranslator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustMachineBoot implements DustMachineConsts {

	public static void main(String[] args) {
		DustMachine machine = new DustMachine();

		try {
			boot(machine);

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
		machine.mainDialog.knowledge.put(MIND_ATT_ASSEMBLY_UNITS, units);
		Map rootKnowledge = new HashMap();
		machine.mainDialog.knowledge.put(MIND_ATT_MEMORY_KNOWLEDGE, rootKnowledge);

		DustUtilsEnumTranslator.register(MindAccess.class, MIND_TAG_ACCESS_CHECK, MIND_TAG_ACCESS_PEEK, MIND_TAG_ACCESS_GET, MIND_TAG_ACCESS_SET, MIND_TAG_ACCESS_INSERT, MIND_TAG_ACCESS_DELETE,
				MIND_TAG_ACCESS_RESET, MIND_TAG_ACCESS_COMMIT);

		DustUtilsAttCache.set(MachineAtts.CreatorAccess, true, MIND_TAG_ACCESS_GET, MIND_TAG_ACCESS_SET, MIND_TAG_ACCESS_INSERT);

		DustUtilsAttCache.set(MachineAtts.PersistentAtt, false, MIND_ATT_KNOWLEDGE_HANDLE);

		DustUtilsAttCache.setWithPairs(MachineAtts.PrimaryAspectNames, "ASP", MIND_ASP_ASPECT, "ATT", MIND_ASP_ATTRIBUTE, "UNIT", MIND_ASP_UNIT, "TAG", MIND_ASP_TAG);

		Map k;
		MindHandle h;

		for (String ui : bh.cnt.keySet()) {
			h = bh.get(ui);
			units.put(ui, h);

			k = new HashMap();
			k.put(MIND_ATT_KNOWLEDGE_HANDLE, h);
			k.put(MIND_ATT_UNIT_AUTHOR, MIND_AUTHOR_DUST);
			k.put(MIND_ATT_MEMORY_KNOWLEDGE, new HashMap());
			k.put(MIND_ATT_UNIT_HANDLES, new HashMap());
			rootKnowledge.put(h, k);
		}

		for (String id : bh.keySet()) {
			if ( id.contains(DUST_SEP_ID) ) {
				String[] ii = id.split(DUST_SEP_ID);
				MindHandle hUnit = bh.get(ii[0]);
				Map uk = (Map) rootKnowledge.get(hUnit);

				h = bh.get(id);

				k = createKnowledge(uk, h, ii[1]);
			}
		}

		machine.idRes = machine.mainDialog;
		
		
		Class[] handleSources = new Class[] { DustMetaConsts.class };
		Map<String, MindHandle> parents = new TreeMap<>();



		machine.mainDialog.knowledge.put(TEXT_ATT_LANGUAGE_DEFAULT, TEXT_TAG_LANGUAGE_EN_US);
		Map uRes = (Map) rootKnowledge.get(RESOURCE_UNIT);

		for (Class constClass : handleSources) {
			for (Field f : constClass.getDeclaredFields()) {
				Object ch = f.get(null);
				if ( ch instanceof MindHandle ) {
					String name = f.getName();
					Dust.access(ch, MIND_TAG_ACCESS_SET, name, DEV_ATT_HINT);

					String[] nn = name.split(DUST_SEP);
					String tokenVal = (2 == nn.length) ? nn[0] : name.substring(nn[0].length() + nn[1].length() + 2);

					if ( "ASP".equals(nn[1]) || "TAG".equals(nn[1]) ) {
						parents.put(name, (MindHandle) ch);
					}

					Map token = createKnowledge(uRes, null, null);
					token.put(TEXT_ATT_PLAIN_TEXT, tokenVal);
					token.put(MIND_ATT_KNOWLEDGE_PRIMARYASPECT, TEXT_ASP_PLAIN);
					token.put(TEXT_TAG_LANGUAGE_EN_US, TEXT_TAG_LANGUAGE);
					token.put(MISC_ATT_CONN_OWNER, ch);
//					DustUtils.safeGet(token, MIND_ATT_KNOWLEDGE_TAGS, MAP_CREATOR).put(TEXT_TAG_LANGUAGE, TEXT_TAG_LANGUAGE_EN_US);

					Dust.log(null, name, " -> ", tokenVal);
				}
			}
		}

		for (Class constClass : handleSources) {
			for (Field f : constClass.getDeclaredFields()) {
				Object ch = f.get(null);
				if ( ch instanceof MindHandle ) {
					String name = f.getName();
					String[] nn = name.split(DUST_SEP);
					MindHandle hPA = DustUtilsAttCache.getAtt(MachineAtts.PrimaryAspectNames, nn[1], null);

					Dust.access(ch, MIND_TAG_ACCESS_SET, hPA, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

					if ( "ATT".equals(nn[1]) || "TAG".equals(nn[1]) ) {
						String pName = name.substring(0, nn[0].length() + nn[1].length() + nn[2].length() + 2);
						pName = pName.replace("_ATT_", "_ASP_");
						MindHandle hParent = parents.get(pName);

						Dust.access(ch, MIND_TAG_ACCESS_SET, hParent, MISC_ATT_CONN_PARENT);
					}
				}
			}
		}

		DustMachineTemp.test();

	}

	public static Map createKnowledge(Map uk, MindHandle h, String localId) {
		Map unitHandles = (Map) uk.get(MIND_ATT_UNIT_HANDLES);

		if ( DustUtils.isEmpty(localId) ) {
			localId = "" + unitHandles.size();
		}
		if ( null == h ) {
			h = new DustHandle((DustHandle) uk.get(MIND_ATT_KNOWLEDGE_HANDLE), localId);
		}
		unitHandles.put(localId, h);

		Map k = new HashMap();
		k.put(MIND_ATT_KNOWLEDGE_HANDLE, h);
		((Map) uk.get(MIND_ATT_MEMORY_KNOWLEDGE)).put(h, k);

		return k;
	}

}
