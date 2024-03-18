package hu.sze.milab.dust.stream;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustUnitHandles;

public interface DustStreamHandles extends DustConsts, DustUnitHandles {
	
//	MindHandle JSONAPI_UNIT = Dust.lookup("giskard:11");

	MindHandle JSONAPI_ASP_FETCHPARAMS = Dust.lookup("giskard:11:?");
	MindHandle JSONAPI_ATT_FETCHPARAMS_INCLUDE = Dust.lookup("giskard:11:?");
	MindHandle JSONAPI_ATT_FETCHPARAMS_FIELDS = Dust.lookup("giskard:11:?");
	MindHandle JSONAPI_ATT_FETCHPARAMS_FILTER = Dust.lookup("giskard:11:?");
	MindHandle JSONAPI_ATT_FETCHPARAMS_SORT = Dust.lookup("giskard:11:?");
	MindHandle JSONAPI_ATT_FETCHPARAMS_PAGELIMIT = Dust.lookup("giskard:11:?");
	MindHandle JSONAPI_ATT_FETCHPARAMS_PAGEOFFSET = Dust.lookup("giskard:11:?");

}
