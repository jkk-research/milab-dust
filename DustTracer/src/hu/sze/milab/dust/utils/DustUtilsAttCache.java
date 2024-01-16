package hu.sze.milab.dust.utils;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DustUtilsAttCache implements DustUtilsConsts {
	static Map<Enum<?>, Map<Object, Object>> ATTS = new HashMap<>();

	public static void set(Enum<?> kind, Object value, Object... handles) {
		Map<Object, Object> ac = DustUtils.safeGet(ATTS, kind, MAP_CREATOR);

		for (Object handle : handles) {
			ac.put(handle, value);
		}
	}

	public static void setWithPairs(Enum<?> kind, Object... pairs) {
		Map<Object, Object> ac = DustUtils.safeGet(ATTS, kind, MAP_CREATOR);

		for (int i = 0; i < pairs.length; ) {
			ac.put(pairs[i++], pairs[i++]);
		}
	}

	public static <RetType> RetType getAtt(Enum<?> kind, Object handle, RetType defVal) {
		Map<Object, Object> ac = ATTS.get(kind);

		return (RetType) ((null == ac) ? defVal : ac.getOrDefault(handle, defVal));
	}
}
