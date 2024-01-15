package hu.sze.milab.dust.utils;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.DustException;

@SuppressWarnings("unchecked")
public class DustUtilsEnumTranslator implements DustUtilsConsts {
	static Map<MindHandle, Object> H2E = new HashMap<>();
	static Map<Object, MindHandle> E2H = new HashMap<>();
	
	public static void register(Class<?> ec, MindHandle...handles) {
		Object[] enums = ec.getEnumConstants();
		int i = enums.length;
		
		if ( i != handles.length ) {
			DustException.wrap(null, "Invalid enum registration, count mismatch");
		}
		
		for ( ; i-->0; ) {
			H2E.put(handles[i], enums[i]);
			E2H.put(enums[i], handles[i]);
		}
		
	}
	
	public static <RetType> RetType getEnum(MindHandle h, Object defVal) {
		return (RetType) H2E.getOrDefault(h, defVal);
	}
	
	public static <RetType> RetType getHandle(Object eVal) {
		return (RetType) E2H.get(eVal);
	}
}
