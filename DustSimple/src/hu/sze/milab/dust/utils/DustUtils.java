package hu.sze.milab.dust.utils;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.DustException;

@SuppressWarnings("unchecked")
public class DustUtils implements DustUtilsConsts {

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

	public static String csvEscape(String valStr, boolean addQuotes) {
		String ret = valStr.replace("\"", "\"\"").replaceAll("\\s+", " ");

		if ( addQuotes ) {
			ret = "\"" + ret + "\"";
		}

		return ret;
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
		String[] headers;
		private Map<String, Integer> columns = new HashMap<>();

		public TableReader(String[] data) {
			headers = data;
			for (int i = data.length; i-- > 0;) {
				columns.put(data[i], i);
			}
		}

		public int getSize() {
			return columns.size();
		}

		protected Object optConvert(String col, Object val) {
			return val;
		}

		public Map getUntil(Object[] row, Map target, String until) {
			if ( null == target ) {
				target = new HashMap();
			}

			int ui = DustUtils.isEmpty(until) ? Integer.MAX_VALUE : columns.get(until);

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

		public void set(Object[] row, Map<String, Object> source, String... cols) {
			if ( 0 == cols.length ) {
				for (int i = row.length; i-- > 0;) {
					row[i] = source.get(headers[i]);
				}
			} else {
				for (int i = row.length; i-- > 0;) {
					row[i] = null;
				}
				for (int i = cols.length; i-- > 0;) {
					int idx = getColIdx(cols[i]);
					if ( -1 != idx ) {
						row[idx] = source.get(cols[i]);
					}
				}
			}
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

		public void set(Object[] row, String col, Object val) {
			int ci = columns.getOrDefault(col, Integer.MAX_VALUE);
			if ( row.length > ci ) {
				row[ci] = val;
			}
		}

		public void writeHead(PrintWriter out, String sep) {
			writeHeadPart(out, sep, Integer.MAX_VALUE);
			out.println();
		}

		public void writeHeadPart(PrintWriter out, String sep, int lastCol) {
			boolean first = true;

			int l = Math.min(lastCol, headers.length);

			for (int i = 0; i < l; ++i) {
				if ( first ) {
					first = false;
				} else {
					out.print(sep);
				}
				out.print(headers[i]);
			}
		}

		public int getColIdx(String col) {
			return columns.getOrDefault(col, -1);
		}

		public <ValType> void writePart(ValType[] row, PrintWriter out, String sep, String... cols) {
			boolean first = true;

			for (String c : cols) {
				if ( first ) {
					first = false;
				} else {
					out.print(sep);
				}
				int ci = getColIdx(c);
				ValType v = (-1 == ci) ? null : row[ci];
				if ( null != v ) {
					out.print(v);
				}
			}
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

	@SuppressWarnings("rawtypes")
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
				ret = sgn[i] * safeCompare(o1.get(sf[i]), o2.get(sf[i]));
				if ( 0 != ret ) {
					return ret;
				}
			}

			return ret;
		}
	};

	@SuppressWarnings("rawtypes")
	public static int safeCompare(Object v1, Object v2) {
		return (null == v1) ? (null == v2) ? 0 : 1 : (null == v2) ? 1 : ((Comparable) v1).compareTo(v2);
	};

	public static String getPostfix(String strSrc, String pfSep) {
		int sep = strSrc.lastIndexOf(pfSep);
		return strSrc.substring(sep + 1);
	}

	public static String cutPostfix(String strSrc, String pfSep) {
		int sep = strSrc.lastIndexOf(pfSep);
		return strSrc.substring(0, sep);
	}

	public static String replacePostfix(String strSrc, String pfSep, String postfix) {
		int sep = strSrc.lastIndexOf(pfSep);
		return strSrc.substring(0, sep + 1) + postfix;
	}

}
