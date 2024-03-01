package hu.sze.milab.dust.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.DustMetaConsts;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface DustUtilsConsts extends DustConsts, DustMetaConsts {
	
	interface DustCreator<Type> {
		Type create(Object key, Object... hints);
	}
	
	class DustCreatorSimple<Type> implements DustCreator<Type> {
		
		Class cc;
		
		public DustCreatorSimple(Class cc) {
			this.cc = cc;
		}

		@Override
		public Type create(Object key, Object... hints) {
			try {
				return (Type) cc.getDeclaredConstructor().newInstance();
			} catch (Throwable e) {
				return DustException.wrap(e, key, hints);
			}
		}
	}
	
	DustCreator<Map> MAP_CREATOR = new DustCreatorSimple<>(HashMap.class);
	DustCreator<ArrayList> ARRAY_CREATOR = new DustCreatorSimple<>(ArrayList.class);

}
