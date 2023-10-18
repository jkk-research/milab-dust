package hu.sze.milab.dust.stream.json;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.stream.DustStreamUtils;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsData;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustStreamJsonapiUtils implements DustStreamJsonConsts {

	public static Map<String, String[]> getTypeFields(String... types) {
		Map<String, String[]> ret = new HashMap<>();

		for (String type : types) {
			String flds = Dust.access(MindContext.LocalCtx, MindAccess.Peek, null, JsonApiMember.jsonapi, JsonApiParam.fields, type);

			String[] ef = {};
			if ( !DustUtils.isEmpty(flds) ) {
				ef = flds.split(",");
				for (int i = ef.length; i-- > 0;) {
					ef[i] = DustUtils.getPostfix(ef[i], ":").trim();
				}
			}
			ret.put(type, ef);
		}

		return ret;
	}

	public static class StreamWriter implements Closeable {
		private final DustStreamUtils.PrintWriterProvider pwp;
		private final Map<String, String[]> ef;

		private Map<JsonApiMember, PrintWriter> writers = new HashMap<>();
		private Map<String, Object> resp = new HashMap<>();
		private Map<String, Object> respAtts = new HashMap<>();
		private Map<String, Object> relationships = new HashMap<>();

		public StreamWriter(DustStreamUtils.PrintWriterProvider pwp, String... types) {
			this.pwp = pwp;
			ef = getTypeFields(types);

			resp.put("attributes", respAtts);
		}

		public void addRelationship(String relName, String dataType, String dataId) {
			addRelationship(relName, dataType, dataId, null, null, null);
		}

		public void addRelationship(String relName, String dataType, String dataId, String linkSelf, String linkRelated) {
			addRelationship(relName, dataType, dataId, linkSelf, linkRelated, null);
		}

		public void addRelationship(String relName, String dataType, String dataId, String linkSelf, String linkRelated, Object meta) {
			Map m = new HashMap<>();

			if ( !DustUtils.isEmpty(dataId) ) {
				Dust.access(m, MindAccess.Set, dataType, JsonApiMember.data, JsonApiMember.type);
				Dust.access(m, MindAccess.Set, dataId, JsonApiMember.data, JsonApiMember.id);
			}

			if ( !DustUtils.isEmpty(linkSelf) ) {
				Dust.access(m, MindAccess.Set, linkSelf, JsonApiMember.links, JsonApiMember.self);
			}
			if ( !DustUtils.isEmpty(linkRelated) ) {
				Dust.access(m, MindAccess.Set, linkRelated, JsonApiMember.links, JsonApiMember.related);
			}

			if ( null != meta ) {
				Dust.access(m, MindAccess.Set, meta, JsonApiMember.meta);
			}

			if ( !m.isEmpty() ) {
				Object curr = relationships.get(relName);

				if ( null == curr ) {
					relationships.put(relName, m);
				} else if ( curr instanceof List ) {
					((List) curr).add(m);
				} else {
					List l = new ArrayList<>();
					l.add(curr);
					l.add(m);
					relationships.put(relName, l);
				}
			}
		}

		public void write(JsonApiMember target, String type, String id, DustUtilsData.TableReader tr, String[] row) throws Exception {
			resp.put("type", type);
			resp.put("id", id);

			respAtts.clear();
			for (String f : ef.get(type)) {
				String value = tr.get(row, f);
				if ( !DustUtils.isEmpty(value) ) {
					respAtts.put(f, value);
				}
			}

			PrintWriter w = writers.get(target);

			if ( null == w ) {
				w = pwp.getStream(target);
				writers.put(target, w);
			} else {
				w.print(",");
			}

			if ( relationships.isEmpty() ) {
				resp.remove("relationships");
			} else {
				resp.put("relationships", relationships);
			}

			JSONValue.writeJSONString(resp, w);
			w.flush();

			relationships.clear();
		}

		@Override
		public void close() throws IOException {
			for (PrintWriter pw : writers.values()) {
				pw.close();
			}
		}
	}

	public static class Filter {
		public boolean equals(Object p1, Object p2) {
			return DustUtils.isEqual(p1, p2);
		}

		int compare(Object p1, Object p2) {
			return (null == p1) ? (null == p2) ? 0 : 1 : (null == p2) ? -1 : ((Comparable) p1).compareTo(p2);
		}

		public boolean lessThan(Object p1, Object p2) {
			return 0 > compare(p1, p2);
		}

		public boolean lessOrEqual(Object p1, Object p2) {
			return 0 >= compare(p1, p2);
		}

		public boolean greaterThan(Object p1, Object p2) {
			return 0 < compare(p1, p2);
		}

		public boolean greaterOrEqual(Object p1, Object p2) {
			return 0 <= compare(p1, p2);
		}

		public boolean containsx(String s1, String s2) {
			return (null == s1) ? (null == s2) : -1 != s1.indexOf(s2);
		}

		public boolean startsWith(String s1, String s2) {
			return (null == s1) ? (null == s2) : s1.startsWith(s2);
		}

		public boolean endsWith(String s1, String s2) {
			return (null == s1) ? (null == s2) : s1.endsWith(s2);
		}

		public int count(Object p) {
			int c = 0;

			if ( null != p ) {
				if ( p instanceof Collection ) {
					c = ((Collection) p).size();
				} else if ( p instanceof Object[] ) {
					c = ((Object[]) p).length;
				}
				if ( p instanceof Map ) {
					c = ((Map) p).size();
				}
			}

			return c;
		}

		public boolean any(Object... params) {
			Object o = params[0];

			for (int i = 1; i < params.length; ++i) {
				if ( DustUtils.isEqual(o, params[i]) ) {
					return true;
				}
			}
			return false;
		}

//	public boolean isType(Object... params) {
//		return DustException.wrap(null, "Function not implemented yet");
//	}
//
//	public boolean has(Object... params) {
//		return false;
//	}
//

		public boolean not(boolean p) {
			return !p;
		}

		public boolean or(boolean... params) {
			for (boolean b : params) {
				if ( b ) {
					return true;
				}
			}
			return false;
		}

		public boolean and(boolean... params) {
			for (boolean b : params) {
				if ( !b ) {
					return false;
				}
			}
			return true;
		}
	}
}
