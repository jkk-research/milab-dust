package hu.sze.milab.dust.utils;

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
		String ret = (null == valStr) ? "" : valStr.replace("\"", "\"\"").replaceAll("\\s+", " ");

		if ( addQuotes ) {
			ret = "\"" + ret + "\"";
		}

		return ret;
	}

	public static String csvUnEscape(String valStr, boolean removeQuotes) {
		if ( isEmpty(valStr) || !valStr.contains("\"")) {
			return valStr;
		}
		
		String ret = ( removeQuotes && valStr.startsWith("\"") ) ? valStr.substring(1, valStr.length()-1) : valStr;
		ret = valStr.replace("\"\"", "\"");

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

	public static class ProcessMonitor {
		private String name;
		private long segment;

		private long tsStart;
		private long tsLast;
		
		private long count;
		
		public ProcessMonitor(String name, long segment) {
			reset(name, segment);
		}
		
		public void reset(String name, long segment) {
			this.name = name;
			this.segment = segment;
			tsStart = tsLast = System.currentTimeMillis();
		}
		
		public synchronized boolean step() {
			boolean ret = false;
			++count;
			
			if ( (0 != segment) && (0 == (count % segment)) ) {
				long ts = System.currentTimeMillis();
				System.out.println(name + " current count: " + count + " time: " + (ts - tsLast));
				tsLast = ts;
				ret = true;
			}
			
			return ret;
		}
		
		public long getCount() {
			return count;
		}
		
		@Override
		public String toString() {
			return name + " total count: " + count + " time: " + (System.currentTimeMillis() - tsStart);
		}
	}

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

	public static void breakpoint(Object ...params ) {
		System.out.println("BREAKPOINT - " + sbAppend(null, " ", false, params));
	}

}
