package hu.sze.milab.dust.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import hu.sze.milab.dust.Dust;

@SuppressWarnings({ "rawtypes" })
public class DustUtilsNarrative implements DustUtilsConsts {

	public static class Indexer<KeyType> {
		private Map<KeyType, Integer> indexes = new HashMap<>();
		private ArrayList<KeyType> idxArr = new ArrayList<>();

		public synchronized int getIndex(KeyType ob) {
			Integer ret = indexes.get(ob);

			if ( null == ret ) {
				ret = indexes.size();
				indexes.put(ob, ret);
				idxArr.add(ob);
			}

			return ret;
		}

		public int peekIndex(KeyType ob) {
			return indexes.getOrDefault(ob, -1);
		}

		public int getSize() {
			return indexes.size();
		}

		public KeyType getKey(int idx) {
			return idxArr.get(idx);
		}

		public Iterable<KeyType> keys() {
			return idxArr;
		}

		@Override
		public String toString() {
			return indexes.toString();
		}
	}

	public static class MapComparator implements Comparator<Map> {
		String[] sf;
		int sfl;
		int[] sgn;

		public MapComparator(String fieldList, String sep) {
			this(fieldList.split(sep));
		}

		public MapComparator(String... fieldList) {
			sf = fieldList;
			sfl = sf.length;
			sgn = new int[sfl];

			for (int i = 0; i < sfl; ++i) {
				sf[i] = sf[i].trim();
				if ( sf[i].startsWith("-") ) {
					sf[i] = sf[i].substring(1);
					sgn[i] = -1;
				} else {
					sgn[i] = 1;
				}
			}
		}

		@Override
		public int compare(Map o1, Map o2) {
			int ret = 0;

			for (int i = 0; i < sfl; ++i) {
				ret = sgn[i] * DustUtils.safeCompare(o1.get(sf[i]), o2.get(sf[i]));
				if ( 0 != ret ) {
					return ret;
				}
			}

			return ret;
		}
	};

	public static class Counter implements Iterable<Map.Entry<Object, Long>> {
		Map<Object, Long> counts;

		public Counter(boolean sorted) {
			counts = sorted ? new TreeMap<>() : new HashMap<>();
		}

		public void reset() {
			if ( null != counts ) {
				counts.clear();
			}
		}

		public void add(Object ob) {
			add(ob, 1L);
		}

		public void add(Object ob, long count) {
			Long l = counts.getOrDefault(ob, 0L);
			counts.put(ob, l + count);
		}

		@Override
		public Iterator<Entry<Object, Long>> iterator() {
			return counts.entrySet().iterator();
		}

		public void dump(MindHandle level, String head) {
			Dust.log(level, head);
			Dust.log(level, "{");
			for (Map.Entry<Object, Long> e : counts.entrySet()) {
				Dust.log(level, "   " + e.getKey(), e.getValue());
			}
			Dust.log(level, "}\n");
		}
	}

}
