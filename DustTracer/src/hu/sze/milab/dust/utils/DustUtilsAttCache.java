package hu.sze.milab.dust.utils;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DustUtilsAttCache implements DustUtilsConsts {
	static Map<Enum<?>, Map<MindHandle, Object>> ATTS = new HashMap<>();

	public static void set(Enum<?> kind, Object value, MindHandle... handles) {
		Map<MindHandle, Object> ac = DustUtils.safeGet(ATTS, kind, MAP_CREATOR);

		for (MindHandle handle : handles) {
			ac.put(handle, value);
		}
	}

	public static <RetType> RetType getAtt(Enum<?> kind, MindHandle handle, RetType defVal) {
		Map<MindHandle, Object> ac = ATTS.get(kind);

		return (RetType) ((null == ac) ? defVal : ac.getOrDefault(handle, defVal));
	}
}
