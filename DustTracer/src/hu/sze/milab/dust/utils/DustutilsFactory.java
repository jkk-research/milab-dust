package hu.sze.milab.dust.utils;

import java.util.HashMap;
import java.util.Map;

public class DustutilsFactory<K, V> extends HashMap<K, V> implements DustUtilsConsts {
	private static final long serialVersionUID = 1L;
	
	DustCreator<V> creator;
	
	public DustutilsFactory(DustCreator<V> creator) {
		this.creator = creator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized V get(Object key) {
		V ret = super.get(key);
		
		if ( null == ret ) {
			ret = creator.create(key);
			put((K) key, ret);
		}
		
		return ret;
	}

	public V peek(Object key) {
		return super.get(key);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static class Simple extends DustutilsFactory<Object, Map> {
		public Simple() {
			super(new DustCreatorSimple(HashMap.class));
		}

		private static final long serialVersionUID = 1L;
		
	}

}
