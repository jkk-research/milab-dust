package hu.sze.milab.dust.stream.json;

import hu.sze.milab.dust.stream.DustStreamConsts;

public interface DustStreamJsonConsts extends DustStreamConsts {
	
	String JSONCONST_NULL = "null";
	String JSONCONST_TRUE = "true";
	String JSONCONST_FALSE = "false";

// https://jsonapi.org/format/
	String JSONAPI_VERSION = "1.1";

//@formatter:off
	enum JsonApiMembers {
		jsonapi, version, ext, profile,
		
		meta, links, type, describedby,
		
		data, errors, included, 

		id, lid, attributes, relationships,
		
		self, related,
		href, rel, title, hreflang,
		first, last, prev, next,
		
		;
		
	};
	
	enum JsonApiParams {
		sort, filter, page, limit, offset
	}
	
	enum JsonFilterFunctions {
		equals, lessThan, lessOrEqual, greaterThan, greaterOrEqual,
		contains, startsWith, endsWith,
		isType, count, any, has,
		not, or, and,
	}
//@formatter:on	

}
