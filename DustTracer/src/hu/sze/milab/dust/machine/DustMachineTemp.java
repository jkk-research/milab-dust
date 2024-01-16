package hu.sze.milab.dust.machine;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.machine.DustMachineConsts.MachineAtts;
import hu.sze.milab.dust.stream.json.DustJsonConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsFile;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustMachineTemp implements DustJsonConsts {

	public static void test(Object... params) throws Exception {
		dumpUnits();
	}

	public static void dumpUnits() throws Exception {
		Map units = Dust.access(null, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_ASSEMBLY_UNITS);

		for (Object u : units.values()) {
			writeUnit(u);
		}
	}

	public static Map handleToMap(MindHandle ih) throws Exception {
		Map item = new HashMap<>();

		item.put(JsonApiMember.id, ih.toString());
		String pa = DustUtils.toString(Dust.access(ih, MIND_TAG_ACCESS_PEEK, "???", MIND_ATT_KNOWLEDGE_PRIMARYASPECT));
		item.put(JsonApiMember.type, pa);

		return item;
	}

	public static void writeUnit(Object unit) throws Exception {
		String uName = Dust.access(unit, MIND_TAG_ACCESS_PEEK, "???", DEV_ATT_HINT);
		uName = uName.split(DUST_SEP)[0].toLowerCase();
		String vName = Dust.access(unit, MIND_TAG_ACCESS_PEEK, "???", MIND_ATT_UNIT_AUTHOR, DEV_ATT_HINT);

		vName = DustUtils.getPostfix(vName, DUST_SEP).toLowerCase();

		File dir = new File("work/json/" + vName);
		DustUtilsFile.ensureDir(dir);

		File f = new File(dir, uName + DUST_EXT_JSON);

		try (FileWriter fw = new FileWriter(f)) {
			Map out = new HashMap<>();

			Map jsonapi = DustUtils.safeGet(out, JsonApiMember.jsonapi, MAP_CREATOR);
			jsonapi.put(JsonApiMember.version, JSONAPI_VERSION);

			ArrayList data = DustUtils.safeGet(out, JsonApiMember.data, ARRAY_CREATOR);

			Map<MindHandle, Map<MindHandle, Object>> items = Dust.access(unit, MIND_TAG_ACCESS_PEEK, "???", MIND_ATT_MEMORY_KNOWLEDGE);

			for (Map.Entry<MindHandle, Map<MindHandle, Object>> ie : items.entrySet()) {
				Map item = handleToMap(ie.getKey());

				for (Map.Entry<MindHandle, Object> ce : ie.getValue().entrySet()) {
					MindHandle hAtt = ce.getKey();

					if ( DustUtilsAttCache.getAtt(MachineAtts.PersistentAtt, hAtt, true) ) {
						String key = hAtt.toString();
						Object val = ce.getValue();

						if ( val instanceof MindHandle ) {
							DustUtils.safeGet(item, JsonApiMember.relationships, MAP_CREATOR).put(key, handleToMap((MindHandle) val));
						} else {
							DustUtils.safeGet(item, JsonApiMember.attributes, MAP_CREATOR).put(key, val);
						}
					}
				}

				data.add(item);
			}
			
			DustUtils.safeGet(out, JsonApiMember.meta, MAP_CREATOR).put(JsonApiMember.count, items.size());

			JSONValue.writeJSONString(out, fw);
			fw.flush();
		}
	}

}
