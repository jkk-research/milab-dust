package hu.sze.milab.dust.stream;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustUnitHandles;

public interface DustStreamHandles extends DustConsts, DustUnitHandles {
	
	MindHandle STANDARD_ASP_JSONAPIDOM = Dust.lookup("giskard:11:?");
	
	MindHandle STANDARD_ASP_JSONAPIFETCH = Dust.lookup("giskard:11:?");
	MindHandle STANDARD_ATT_JSONAPIFETCH_INCLUDE = Dust.lookup("giskard:11:?");
	MindHandle STANDARD_ATT_JSONAPIFETCH_FIELDS = Dust.lookup("giskard:11:?");
	MindHandle STANDARD_ATT_JSONAPIFETCH_FILTER = Dust.lookup("giskard:11:?");
	MindHandle STANDARD_ATT_JSONAPIFETCH_SORT = Dust.lookup("giskard:11:?");
	MindHandle STANDARD_ATT_JSONAPIFETCH_PAGELIMIT = Dust.lookup("giskard:11:?");
	MindHandle STANDARD_ATT_JSONAPIFETCH_PAGEOFFSET = Dust.lookup("giskard:11:?");

	MindHandle STANDARD_ASP_XMLELEMENT = Dust.lookup("giskard:11:?");
	MindHandle STANDARD_ATT_XMLELEMENT_ATTRIBUTES = Dust.lookup("giskard:11:?");

}
