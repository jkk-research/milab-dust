package hu.sze.milab.dust.utils;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.DustException;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DustUtilsEnumTranslator implements DustUtilsConsts {
	static Map<Class, Map<MindHandle, Object>> H2CE = new HashMap<>();
	static Map<Object, MindHandle> E2H = new HashMap<>();
	
	public static void register(Class<?> ec, MindHandle...handles) {
		Object[] enums = ec.getEnumConstants();
		int i = enums.length;
		
		if ( i != handles.length ) {
			DustException.wrap(null, "Invalid enum registration, count mismatch");
		}
		
		Map<MindHandle, Object> H2E = new HashMap<>();
		H2CE.put(ec, H2E);
		
		for ( ; i-->0; ) {
			H2E.put(handles[i], enums[i]);
			E2H.put(enums[i], handles[i]);
		}
		
	}
	
	public static <RetType> RetType getEnum(MindHandle h, Object defVal) {
		Map<MindHandle, Object> H2E = H2CE.get(defVal.getClass());
		return (RetType) ((null == H2E) ? defVal : H2E.getOrDefault(h, defVal));
	}
	
	public static <RetType> RetType getHandle(Object eVal) {
		return (RetType) E2H.get(eVal);
	}
}
