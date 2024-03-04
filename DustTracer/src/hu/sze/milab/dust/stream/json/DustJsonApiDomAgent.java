package hu.sze.milab.dust.stream.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustJsonApiDomAgent extends DustAgent implements DustJsonConsts {

	@Override
	protected MindHandle agentBegin() throws Exception {
		// TODO Auto-generated method stub
		return super.agentBegin();
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		Object hData = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_DATA);
		Object hStream = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_STREAM);

		Object current = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);

		if (null != current) {
			Object s = Dust.access(MindAccess.Peek, null, hStream, MISC_ATT_VARIANT_VALUE);
			File f = (File) s;

			if (hData == current) {
				Object d = Dust.access(MindAccess.Peek, null, hData, MISC_ATT_VARIANT_VALUE);
				writeUnit((MindHandle) d, f, null);
			} else {
				readUnit(f);
			}

		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	protected MindHandle agentEnd() throws Exception {
		// TODO Auto-generated method stub
		return super.agentEnd();
	}

	public static void readUnit(File f) throws Exception {
		if (f.isFile()) {
			try (FileReader fr = new FileReader(f)) {
				Object jsonRoot = JSONValue.parse(fr);

				Object ob = DustUtils.simpleGet(jsonRoot, JsonApiMember.jsonapi, JsonApiMember.version);
				if (!DustUtils.isEqual(JSONAPI_VERSION, ob)) {
					DustException.wrap(null, "Invalid JSON:API version", ob);
				}

				ArrayList data = DustUtils.simpleGet(jsonRoot, JsonApiMember.data);

				for (Object item : data) {
					MindHandle hI = DustJsonUtils.handleFromMap(item);

					Map<String, Object> atts = DustUtils.simpleGet(item, JsonApiMember.attributes);
					for (Map.Entry<String, Object> ae : atts.entrySet()) {
						MindHandle hA = DustJsonUtils.handleFromKey(ae.getKey());
						Dust.access(MindAccess.Set, ae.getValue(), hI, hA);
					}

					Map<String, Object> refs = DustUtils.simpleGet(item, JsonApiMember.relationships);
					for (Map.Entry<String, Object> re : refs.entrySet()) {
						MindHandle hA = DustJsonUtils.handleFromKey(re.getKey());
						Object rv = re.getValue();

						if (rv instanceof List) {
							for (Object ro : (List) rv) {
								MindHandle hV = DustJsonUtils.handleFromMap(ro);
								Dust.access(MindAccess.Set, hV, hI, hA, KEY_ADD);
							}
						} else {
							MindHandle hV = DustJsonUtils.handleFromMap(rv);
							Dust.access(MindAccess.Set, hV, hI, hA);
						}
					}
				}
			}
		}
	}

	public static void writeUnit(MindHandle unit, File f, Enum persAttEnum) throws Exception {

		try (FileWriter fw = new FileWriter(f)) {
			Map out = new HashMap<>();

			Map jsonapi = DustUtils.safeGet(out, JsonApiMember.jsonapi, MAP_CREATOR);
			jsonapi.put(JsonApiMember.version, JSONAPI_VERSION);

			ArrayList data = DustUtils.safeGet(out, JsonApiMember.data, ARRAY_CREATOR);

			Map<Object, MindHandle> items = Dust.access(MindAccess.Peek, null, unit, MIND_ATT_UNIT_HANDLES);
			for (MindHandle hItem : items.values()) {
				Map<MindHandle, Object> itemData = Dust.access(MindAccess.Peek, null, unit, MIND_ATT_UNIT_CONTENT, hItem);
				if (null != itemData) {
					Map item = knowledgeToMap(persAttEnum, hItem, itemData);
					data.add(item);
				}
			}

			DustUtils.safeGet(out, JsonApiMember.meta, MAP_CREATOR).put(JsonApiMember.count, data.size());

			JSONValue.writeJSONString(out, fw);
			fw.flush();
		}
	}

	public static Map knowledgeToMap(Enum persAttEnum, MindHandle hItem, Map<MindHandle, Object> itemData)
			throws Exception {
		Map item = DustJsonUtils.handleToMap(hItem);

		for (Map.Entry<MindHandle, Object> ce : itemData.entrySet()) {
			MindHandle hAtt = ce.getKey();

			if (DustUtilsAttCache.getAtt(persAttEnum, hAtt, true)) {
				String key = DustJsonUtils.handleToString(hAtt);
				Object val = ce.getValue();

				Object mem = JsonApiMember.attributes;

				if (val instanceof MindHandle) {
					mem = JsonApiMember.relationships;
					val = DustJsonUtils.handleToMap((MindHandle) val);
				} else if (val instanceof Map) {
					Map mp = new HashMap();
					for (Map.Entry<Object, Object> ee : ((Map<Object, Object>) val).entrySet()) {
						Object mk = ee.getKey();
						if (mk instanceof MindHandle) {
							mk = DustJsonUtils.handleToString((MindHandle) mk);
						}
						Object mv = ee.getValue();
						if (mv instanceof MindHandle) {
							mem = JsonApiMember.relationships;
							mk = DustJsonUtils.handleToMap((MindHandle) mv);
						}
						mp.put(mk, mv);
					}
					val = mp;
				}
				if (val instanceof Collection) {
					ArrayList al = new ArrayList();
					for (Object oo : ((Collection) val)) {
						if (oo instanceof MindHandle) {
							mem = JsonApiMember.relationships;
							oo = DustJsonUtils.handleToMap((MindHandle) oo);
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

}
