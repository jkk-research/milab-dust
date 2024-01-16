package hu.sze.milab.dust.stream.json;

import java.util.EnumSet;

import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.utils.DustUtilsConsts;

public interface DustJsonConsts extends DustMetaConsts, DustUtilsConsts {
	
	String JSONCONST_NULL = "null";
	String JSONCONST_TRUE = "true";
	String JSONCONST_FALSE = "false";

// https://jsonapi.org/format/
	String JSONAPI_VERSION = "1.1";

//@formatter:off
	enum JsonApiMember {
		jsonapi, version, ext, profile,
		
		meta, links, type, describedby,
		
		data, errors, included, 

		id, lid, attributes, relationships,
		
		self, related,
		href, rel, title, hreflang,
		first, last, prev, next, 
		
		detail, count,
		
		;
		
		public static final EnumSet<JsonApiMember> TOP = EnumSet.of(jsonapi, data, errors, meta, links, included);
		public static final EnumSet<JsonApiMember> DATA = EnumSet.of(data, included);
		public static final EnumSet<JsonApiMember> HEADER = EnumSet.of(version, ext, profile);
	};
	
	enum JsonApiParam {
		include, fields, sort, filter, page, limit, offset
	}
	
	enum JsonFilterFunction {
		equals, lessThan, lessOrEqual, greaterThan, greaterOrEqual,
		contains, startsWith, endsWith,
		isType, count, any, has,
		not, or, and,
	}
//@formatter:on	

}
