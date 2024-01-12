package hu.sze.milab.dust.machine;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsFile;

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
					mh = new DustHandle();
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

		Map uRes = (Map) rootKnowledge.get(RESOURCE_UNIT);

		Map resRoot = new HashMap();
		uRes.put(TEXT_ATT_RESOURCE_TOKENS, resRoot);

		Map langEn = new HashMap();
		resRoot.put(TEXT_TAG_LANGUAGE_EN_US, langEn);
		
		machine.mainDialog.knowledge.put(TEXT_ATT_LANGUAGE_DEFAULT, TEXT_TAG_LANGUAGE_EN_US);

		Class[] handleSources = new Class[] { DustMetaConsts.class };

		for (Class constClass : handleSources) {
			for (Field f : constClass.getDeclaredFields()) {
				Object ch = f.get(null);
				if ( ch instanceof MindHandle ) {
					String name = f.getName();
					Dust.log(null, name);
					
					Map token = createKnowledge(uRes, null, null);
					token.put(TEXT_ATT_PLAIN_TEXT, name);
					langEn.put(ch, token.get(MIND_ATT_KNOWLEDGE_HANDLE));
				}
			}
		}

		DustHandle.setTranslator(langEn, (Map<MindHandle, Map>) uRes.get(MIND_ATT_MEMORY_KNOWLEDGE));
		
		Map out = new HashMap<>();
		Map jsonapi = new HashMap<>();
		
		jsonapi.put("version", "1.1");
		out.put("jsonapi", jsonapi);
		
		for ( Object u : units.values() ) {
			String uName = u.toString().split(DUST_SEP)[0].toLowerCase();
			String vName = ((Map) rootKnowledge.get(u)).get(MIND_ATT_UNIT_AUTHOR).toString();
			
			vName = DustUtils.getPostfix(vName, DUST_SEP).split(" ")[0].toLowerCase();
			
			File dir = new File("work/json/" + vName);
			DustUtilsFile.ensureDir(dir);
			
			File f = new File(dir, uName + DUST_EXT_JSON);
			
			try ( FileWriter fw = new FileWriter(f)) {
				JSONValue.writeJSONString(out, fw);
				fw.flush();
			}
		}

		machine.idRes = machine.mainDialog;
	}

	public static Map createKnowledge(Map uk, MindHandle h, String localId) {
		if ( DustUtils.isEmpty(localId)) {
			localId = "" + uk.size();
		}
		if ( null == h ) {
			h = new DustHandle((DustHandle) uk.get(MIND_ATT_KNOWLEDGE_HANDLE), localId);
		}
		((Map) uk.get(MIND_ATT_UNIT_HANDLES)).put(localId, h);

		Map k = new HashMap();
		k.put(MIND_ATT_KNOWLEDGE_HANDLE, h);
		((Map) uk.get(MIND_ATT_MEMORY_KNOWLEDGE)).put(h, k);

		return k;
	}

}
