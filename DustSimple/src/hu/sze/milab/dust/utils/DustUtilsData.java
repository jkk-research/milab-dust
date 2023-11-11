package hu.sze.milab.dust.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hu.sze.milab.dust.DustException;

@SuppressWarnings("unchecked")
public interface DustUtilsData extends DustUtilsConsts {

	@SuppressWarnings("rawtypes")
	public static class TableReader {
		String[] headers;
		private Map<String, Integer> columns = new HashMap<>();

		public TableReader(String[] data) {
			int l = data.length;
			headers = Arrays.copyOf(data, l);
			for (int i = l; i-- > 0;) {
				columns.put(data[i], i);
			}
		}

		public int getSize() {
			return columns.size();
		}

		public String getColumn(int i) {
			return headers[i];
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

			if ( 0 == cols.length ) {
				cols = headers;
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

		public <ValType> String format(ValType[] row, String sep, String... cols) {
			return format(row, sep, true, cols);
		}

		public <ValType> String format(ValType[] row, String sep, boolean strict, String... cols) {
			StringBuilder sb = null;

			for (String c : cols) {
				int ci = getColIdx(c);
				ValType v = (-1 == ci) ? null : row[ci];
				sb = DustUtils.sbAppend(sb, sep, strict, v);
			}
			
			return (null == sb) ? "" : sb.toString();
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
		
		@Override
		public String toString() {
			return DustUtils.sbAppend(null, ", ", true, (Object[]) headers).toString();
		}
	}

	public static class TableIterator implements Closeable, Iterable<String[]>, Iterator<String[]> {
		String fName;
		String sep;
		TableReader reader;

		BufferedReader br;
		int row;
		String line;
		String[] data;

		public TableIterator(File file, String sep) throws Exception {
			this(file, sep, null);
		}

		public TableIterator(File file, String sep, TableReader reader) throws Exception {			
			this.sep = sep;
			fName = file.getCanonicalPath();
			this.reader = reader;
			
			br = new BufferedReader(new FileReader(file));
			
			if ( null == reader ) {
				line = br.readLine();// skip head!
				reader = new TableReader(line.split(sep));
			}
			
			line = br.readLine();
			data = line.split(sep);

			row = 1;
		}
		
		public TableReader getReader() {
			return reader;
		}
		
		public String getLine() {
			return line;
		}

		public String getAt(int i) {
			return data[i];
		}

		public <ValType> ValType get(Object key) {
			return null;
		}
		
		public void set(Object key, Object val) {
		}
		
		@Override
		public boolean hasNext() {
			return !DustUtils.isEmpty(line);
		}

		@Override
		public String[] next() {
			data = line.split(sep);

			try {
				++row;
				line = br.readLine();
			} catch (Throwable e) {
				line = null;
				data = null;
				DustException.wrap(e, fName, row);
			}

			return data;
		}

		@Override
		public Iterator<String[]> iterator() {
			return this;
		}

		@Override
		public void close() throws IOException {
			br.close();
		}

	}

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

		public Iterable<KeyType> keys() {
			return idxArr;
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
				ret = sgn[i] * DustUtils.safeCompare(o1.get(sf[i]), o2.get(sf[i]));
				if ( 0 != ret ) {
					return ret;
				}
			}

			return ret;
		}
	};
}
