package hu.sze.milab.dust.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustException;

@SuppressWarnings("unchecked")
public class DustUtils implements DustConsts {

	public interface QueueContainer<MainType> {
		void enqueue(MainType item, Object... hints);
	}

	public static boolean isEqual(Object o1, Object o2) {
		return (null == o1) ? (null == o2) : (null != o2) && o1.equals(o2);
	}

	public static String toString(Object ob) {
		return toString(ob, ", ");
	}

	public static String toString(Object ob, String sep) {
		if ( null == ob ) {
			return "";
		} else if ( ob.getClass().isArray() ) {
			StringBuilder sb = null;
			for (Object oo : (Object[]) ob) {
				sb = sbAppend(sb, sep, false, oo);
			}
			return (null == sb) ? "" : sb.toString();
		} else {
			return ob.toString();
		}
	}

	public static boolean isEmpty(String str) {
		return (null == str) || str.isEmpty();
	}

	public static StringBuilder sbAppend(StringBuilder sb, Object sep, boolean strict, Object... objects) {
		for (Object ob : objects) {
			String str = toString(ob);

			if ( strict || (0 < str.length()) ) {
				if ( null == sb ) {
					sb = new StringBuilder(str);
				} else {
					sb.append(sep);
					sb.append(str);
				}
			}
		}

		return sb;
	}

	public static <RetType> RetType createInstance(ClassLoader cl, String className) {
		try {
			return (RetType) cl.loadClass(className).getConstructor().newInstance();
		} catch (Throwable e) {
			return DustException.wrap(e);
		}
	}

	public static boolean isAccessAdd(MindAccess acc) {
		switch ( acc ) {
		case Get:
		case Insert:
		case Set:
			return true;
		default:
			return false;
		}
	}

	public static String wrapCollSize(MindColl coll, Number size) {
		switch ( coll ) {
		case Arr:
			return "[" + size + "]";
		case Map:
			return "{" + size + "}";
		case One:
			return size.toString();
		case Set:
			return "(" + size + ")";
		}

		return "?";
	}

	@SuppressWarnings("rawtypes")
	public static class TableReader {
		private Map<String, Integer> columns = new HashMap<>();

		public TableReader(String[] data) {
			for (int i = data.length; i-- > 0;) {
				columns.put(data[i], i);
			}
		}

		protected Object optConvert(String col, Object val) {
			return val;
		}

		public Map getUntil(Object[] row, Map target, String until) {
			if ( null == target ) {
				target = new HashMap();
			}

			int ui = columns.get(until);

			for (Map.Entry<String, Integer> ec : columns.entrySet()) {
				if ( ec.getValue() < ui ) {
					String c = ec.getKey();
					Object v = get(row, c);
					if ( null != v ) {
						target.put(c, optConvert(c, v));
					}
				}
			}

			return target;
		}

		public Map<String, Object> get(Object[] row, Map<String, Object> target, String... cols) {
			if ( null == target ) {
				target = new HashMap<>();
			}

			for (String c : cols) {
				Object v = get(row, c);
				if ( null != v ) {
					target.put(c, optConvert(c, v));
				}
			}

			return target;
		}

		public <ValType> ValType get(ValType[] row, String col) {
			return get(row, col, null);
		}

		public <ValType> ValType get(ValType[] row, String col, ValType def) {
			int ci = columns.getOrDefault(col, Integer.MAX_VALUE);
			return (row.length > ci) ? row[ci] : def;
		}
	}

	public static class Indexer<KeyType> {
		private Map<KeyType, Integer> indexes = new HashMap<>();

		public synchronized int getIndex(KeyType ob) {
			Integer ret = indexes.get(ob);

			if ( null == ret ) {
				ret = indexes.size();
				indexes.put(ob, ret);
			}

			return ret;
		}

		public int getSize() {
			return indexes.size();
		}

		public Collection<KeyType> keys() {
			return indexes.keySet();
		}

		@Override
		public String toString() {
			return indexes.toString();
		}
	}

	public static String getPostfix(String where, String pfSep) {
		int sep = where.lastIndexOf(pfSep);
		return where.substring(sep + 1);
	}

	public static String cutPostfix(String where, String pfSep) {
		int sep = where.lastIndexOf(pfSep);
		return where.substring(0, sep);
	}

	public static String replacePostfix(String where, String pfSep, String postfix) {
		int sep = where.lastIndexOf(pfSep);
		return where.substring(0, sep + 1) + postfix;
	}

}
