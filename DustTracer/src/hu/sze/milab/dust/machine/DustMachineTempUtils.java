package hu.sze.milab.dust.machine;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONValue;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.machine.DustMachineConsts.DustHandle;
import hu.sze.milab.dust.machine.DustMachineConsts.MachineAtts;
import hu.sze.milab.dust.stream.json.DustJsonConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsFile;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustMachineTempUtils implements DustJsonConsts {

	private static final File MODULE_DIR = new File("work/json/");

	public static void test(Object... params) throws Exception {
		initFromInterfaces(DustMetaConsts.class);
		
//		dumpUnits();
		
//		readUnits();
		
		writeJavaMeta();
	}

	public static void readUnits() throws Exception {
		File dir = new File(MODULE_DIR, "dust");

		if ( dir.isDirectory() ) {
			for (File f : dir.listFiles()) {
				if ( f.getName().toLowerCase().endsWith(DUST_EXT_JSON) ) {
					readUnit(f);
				}
			}
		}
	}

	public static void dumpUnits() throws Exception {
		Map units = Dust.access(MindAccess.Peek, null, null, DUST_ATT_MACHINE_UNITS);

		for (Object u : units.values()) {
//			if ( u.toString().contains("DEV") ) 
			{
				writeUnit((MindHandle) u);
			}
		}
	}

	public static Map handleToMap(MindHandle ih, String localPrefix) throws Exception {
		Map item = new HashMap<>();

		String hId = ih.toString();
		if ( (null != localPrefix) && hId.startsWith(localPrefix) ) {
			hId = hId.substring(localPrefix.length());
		}
		item.put(JsonApiMember.id, hId);
		String pa = DustUtils.toString(Dust.access(MindAccess.Peek, "???", ih, MIND_ATT_KNOWLEDGE_PRIMARYASPECT));
		item.put(JsonApiMember.type, pa);

		return item;
	}

	public static File getUnitFile(MindHandle unit) throws Exception {
		String uName = Dust.access(MindAccess.Peek, "???", unit, DEV_ATT_HINT);
		uName = uName.split(DUST_SEP)[0].toLowerCase();
		String vName = Dust.access(MindAccess.Peek, "???", unit, MIND_ATT_UNIT_AUTHOR, DEV_ATT_HINT);

		vName = DustUtils.getPostfix(vName, DUST_SEP).toLowerCase();

		File dir = new File(MODULE_DIR, vName);
		DustUtilsFile.ensureDir(dir);

		File f = new File(dir, uName + DUST_EXT_JSON);

		return f;
	}

	public static void readUnit(File f) throws Exception {
		try (FileReader fr = new FileReader(f)) {
			Object jsonRoot = JSONValue.parse(fr);

			Object ob = DustUtils.simpleGet(jsonRoot, JsonApiMember.jsonapi, JsonApiMember.version);
			if ( !DustUtils.isEqual(JSONAPI_VERSION, ob) ) {
				DustException.wrap(null, "Invalid JSON:API version", ob);
			}

			ArrayList data = DustUtils.simpleGet(jsonRoot, JsonApiMember.data);

			for (Object item : data) {
				MindHandle hI = handleFromMap(item);

				Map<String, Object> atts = DustUtils.simpleGet(item, JsonApiMember.attributes);
				for (Map.Entry<String, Object> ae : atts.entrySet()) {
					MindHandle hA = handleFromKey(ae.getKey());
					Dust.access(MindAccess.Set, ae.getValue(), hI, hA);
				}

				Map<String, Object> refs = DustUtils.simpleGet(item, JsonApiMember.relationships);
				for (Map.Entry<String, Object> re : refs.entrySet()) {
					MindHandle hA = handleFromKey(re.getKey());
					Object rv = re.getValue();

					if ( rv instanceof List ) {
						for (Object ro : (List) rv) {
							MindHandle hV = handleFromMap(ro);
							Dust.access(MindAccess.Set, hV, hI, hA, KEY_ADD);
						}
					} else {
						MindHandle hV = handleFromMap(rv);
						Dust.access(MindAccess.Set, hV, hI, hA);
					}
				}
			}
		}
	}

	public static MindHandle handleFromKey(String key) {
		String id = key.split(" ")[0];
		MindHandle ret = Dust.lookup(id);

		if ( null == ret ) {
			DustDevUtils.breakpoint("No handle found for key", key);
		}

		return ret;
	}

	public static MindHandle handleFromMap(Object item) {
		String id = DustUtils.simpleGet(item, JsonApiMember.id);
		return handleFromKey(id);
	}

	public static void writeUnit(MindHandle unit) throws Exception {

		File f = getUnitFile(unit);

		try (FileWriter fw = new FileWriter(f)) {
			Map out = new HashMap<>();

			Map jsonapi = DustUtils.safeGet(out, JsonApiMember.jsonapi, MAP_CREATOR);
			jsonapi.put(JsonApiMember.version, JSONAPI_VERSION);

			ArrayList data = DustUtils.safeGet(out, JsonApiMember.data, ARRAY_CREATOR);

			String localPrefix = unit.getId() + DUST_SEP_ID;
			localPrefix = null;

			Map<MindHandle, Object> unitData = Dust.access(MindAccess.Peek, null, null, MIND_ATT_DIALOG_KNOWLEDGE, unit);
			data.add(knowledgeToMap(localPrefix, unit, unitData));

			Map<Object, MindHandle> items = Dust.access(MindAccess.Peek, null, unit, MIND_ATT_UNIT_HANDLES);
			for (MindHandle hItem : items.values()) {
				Map<MindHandle, Object> itemData = Dust.access(MindAccess.Peek, null, null, MIND_ATT_DIALOG_KNOWLEDGE, hItem);
				Map item = knowledgeToMap(localPrefix, hItem, itemData);
				data.add(item);
			}

			DustUtils.safeGet(out, JsonApiMember.meta, MAP_CREATOR).put(JsonApiMember.count, data.size());

			JSONValue.writeJSONString(out, fw);
			fw.flush();
		}
	}

	public static Map knowledgeToMap(String localPrefix, MindHandle hItem, Map<MindHandle, Object> itemData) throws Exception {
		Map item = handleToMap(hItem, localPrefix);

		for (Map.Entry<MindHandle, Object> ce : itemData.entrySet()) {
			MindHandle hAtt = ce.getKey();

			if ( DustUtilsAttCache.getAtt(MachineAtts.PersistentAtt, hAtt, true) ) {
				String key = hAtt.toString();
				Object val = ce.getValue();

				Object mem = JsonApiMember.attributes;

				if ( val instanceof MindHandle ) {
					mem = JsonApiMember.relationships;
					val = handleToMap((MindHandle) val, localPrefix);
				} else if ( val instanceof Map ) {
					Map mp = new HashMap();
					for (Map.Entry<Object, Object> ee : ((Map<Object, Object>) val).entrySet()) {
						Object mk = ee.getKey();
						if ( mk instanceof MindHandle ) {
							mk = mk.toString();
						}
						Object mv = ee.getValue();
						if ( mv instanceof MindHandle ) {
							mem = JsonApiMember.relationships;
							mk = handleToMap((MindHandle) mv, localPrefix);
						}
						mp.put(mk, mv);
					}
					val = mp;
				}
				if ( val instanceof Collection ) {
					ArrayList al = new ArrayList();
					for (Object oo : ((Collection) val)) {
						if ( oo instanceof MindHandle ) {
							mem = JsonApiMember.relationships;
							oo = handleToMap((MindHandle) oo, localPrefix);
						}
						al.add(oo);
					}
					val = al;
				}

				DustUtils.safeGet(item, mem, MAP_CREATOR).put(key, val);
			}
		}
		return item;
	}

	public static void writeJavaMeta() throws Exception {
		DustMachineTempJavaMeta metaWriter = null;

		Map units = Dust.access(MindAccess.Peek, null, null, DUST_ATT_MACHINE_UNITS);
		for (Object u : units.values()) {
			if ( null == metaWriter ) {
				metaWriter = new DustMachineTempJavaMeta("gen", "hu.sze.milab.dust", "DustMetaConsts", 
						MIND_ASP_UNIT, MIND_ASP_ASPECT, MIND_ASP_ATTRIBUTE, MIND_ASP_TAG, MIND_ASP_AGENT, 
						MIND_ASP_AUTHOR, DUST_ASP_MODULE, MIND_ASP_ASSEMBLY, DUST_ASP_MACHINE);
				metaWriter.agentBegin();
			}
			metaWriter.unitToAdd = (MindHandle) u;
			metaWriter.agentProcess(MindAction.Process);
		}

		if ( null != metaWriter ) {
			metaWriter.agentEnd();
		}
	}

	public static void initFromInterfaces(Class... ifClasses) throws IllegalAccessException {
		Map<String, MindHandle> parents = new TreeMap<>();

		Dust.access(MindAccess.Set, TEXT_TAG_LANGUAGE_EN_US, null, TEXT_ATT_LANGUAGE_DEFAULT);

		for (Class constClass : ifClasses) {
			for (Field f : constClass.getDeclaredFields()) {
				Object ch = f.get(null);
				if ( ch instanceof DustHandle ) {
					String name = f.getName();
					((DustHandle)ch).setStr(name);
					Dust.access(MindAccess.Set, name, ch, DEV_ATT_HINT);

					String[] nn = name.split(DUST_SEP);
					String tokenVal = (2 == nn.length) ? nn[0] : name.substring(nn[0].length() + nn[1].length() + 2);

					if ( "ASP".equals(nn[1]) || "TAG".equals(nn[1]) ) {
						parents.put(name, (MindHandle) ch);
					}

					Map token = DustMachineBoot.createKnowledge((DustHandle) L10N_UNIT, null, null);
					token.put(TEXT_ATT_PLAIN_TEXT, tokenVal);
					token.put(MIND_ATT_KNOWLEDGE_PRIMARYASPECT, TEXT_ASP_PLAIN);
					token.put(TEXT_TAG_LANGUAGE, TEXT_TAG_LANGUAGE_EN_US);
					token.put(MISC_ATT_CONN_OWNER, ch);

					Dust.log(null, name, " -> ", tokenVal);
				}
			}
		}

		for (Class constClass : ifClasses) {
			for (Field f : constClass.getDeclaredFields()) {
				Object ch = f.get(null);
				if ( ch instanceof MindHandle ) {
					String name = f.getName();
					String[] nn = name.split(DUST_SEP);
					MindHandle hPA = DustUtilsAttCache.getAtt(MachineAtts.PrimaryAspectNames, nn[1], null);

					Dust.access(MindAccess.Set, hPA, ch, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

					if ( "ATT".equals(nn[1]) || "TAG".equals(nn[1]) ) {
						String pName = name.substring(0, nn[0].length() + nn[1].length() + nn[2].length() + 2);
						pName = pName.replace("_ATT_", "_ASP_");
						MindHandle hParent = parents.get(pName);

						Dust.access(MindAccess.Set, hParent, ch, MISC_ATT_CONN_PARENT);
					}
				}
			}
		}
	}
}
