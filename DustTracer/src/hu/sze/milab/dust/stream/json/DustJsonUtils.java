package hu.sze.milab.dust.stream.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustJsonUtils implements DustJsonConsts {
	
	public static String jsonKeyToId(String key) {
		return key.split(" ")[0].replace(JSONAPI_IDSEP, DUST_SEP_ID);
	}
	
	public static MindHandle handleFromKey(String key) {
		String id = jsonKeyToId(key);
		MindHandle ret = Dust.lookup(id);

		if (null == ret) {
			DustDevUtils.breakpoint("No handle found for key", key);
		}

		return ret;
	}

	public static MindHandle handleFromMap(Object item) {
		String id = DustUtils.simpleGet(item, JsonApiMember.id);
		return handleFromKey(id);
	}

	public static String handleToString(MindHandle ih) throws Exception {
		if ( null == ih ) {
			return "???";
		}

		String hId = ih.getId();

		return hId.replace(DUST_SEP_ID, JSONAPI_IDSEP) + " " + ih.toString();
	}

	public static Map handleToMap(MindHandle ih) throws Exception {
		Map item = new HashMap<>();

		String hId = handleToString(ih);
		
		item.put(JsonApiMember.id, hId);
		MindHandle pa = Dust.access(MindAccess.Peek, null, ih, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
		item.put(JsonApiMember.type, handleToString(pa));

		return item;
	}

	public static Map knowledgeToMap(MindHandle unit, MindHandle hItem, Collection<MindHandle> atts) throws Exception {
		Map item = null;
		
		Map<MindHandle, Object> itemData = Dust.access(MindAccess.Peek, null, unit, MIND_ATT_UNIT_CONTENT, hItem);
		
		if (null != itemData) {
			 item = DustJsonUtils.handleToMap(hItem);

			for (Map.Entry<MindHandle, Object> ce : itemData.entrySet()) {
				MindHandle hAtt = ce.getKey();
				
				if ( DustUtilsAttCache.getAtt(MachineAtts.TransientAtt, hAtt, false) ) {
					continue;
				}

				if ((null == atts) || atts.contains(hAtt) ) {
					String key = DustJsonUtils.handleToString(hAtt);
					Object val = ce.getValue();

					Object mem = JsonApiMember.attributes;

					if (val instanceof MindHandle) {
						mem = JsonApiMember.relationships;
						val = DustJsonUtils.handleToMap((MindHandle) val);
					} else if (val instanceof Map) {
						Map mp = null;
						ArrayList al = null;
						for (Map.Entry<Object, Object> ee : ((Map<Object, Object>) val).entrySet()) {
							Object mk = ee.getKey();
							if (mk instanceof MindHandle) {
								mk = DustJsonUtils.handleToString((MindHandle) mk);
							}
							
							Object mv = ee.getValue();
							if (mv instanceof MindHandle) {
								mem = JsonApiMember.relationships;
								mp = DustJsonUtils.handleToMap((MindHandle) mv);
								DustUtils.safeGet(mp, JsonApiMember.meta, MAP_CREATOR).put("key", mk);
								
								if ( null == al ) {
									val = al = new ArrayList();
								}
								al.add(mp);
								
							} else {
								if ( null == mp ) {
									val = mp = new HashMap();
								}
								mp.put(mk, mv);
							}
						}
					} else if (val instanceof Collection) {
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
		}
		return item;
	}
	
	public static Map toJsonApiMap(ArrayList data) throws Exception {
		Map out = new HashMap<>();

		Map jsonapi = DustUtils.safeGet(out, JsonApiMember.jsonapi, MAP_CREATOR);
		jsonapi.put(JsonApiMember.version, JSONAPI_VERSION);

		out.put(JsonApiMember.data, data);

		DustUtils.safeGet(out, JsonApiMember.meta, MAP_CREATOR).put(JsonApiMember.count, data.size());

		return out;
	}

}
