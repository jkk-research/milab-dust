package hu.sze.milab.dust.stream.json;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;

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

}
