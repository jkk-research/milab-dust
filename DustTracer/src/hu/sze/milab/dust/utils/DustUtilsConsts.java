package hu.sze.milab.dust.utils;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustException;

public interface DustUtilsConsts extends DustConsts {
	
	interface DustCreator<Type> {
		Type create(Object key, Object... hints);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	class DustCreatorSimple<Type> implements DustCreator<Type> {
		
		Class cc;
		
		public DustCreatorSimple(Class cc) {
			this.cc = cc;
		}

		@Override
		public Type create(Object key, Object... hints) {
			try {
				return (Type) cc.newInstance();
			} catch (Throwable e) {
				return DustException.wrap(e, key, hints);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	DustCreator<Map> MAP_CREATOR = new DustCreatorSimple<>(HashMap.class);

}
