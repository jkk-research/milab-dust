package hu.sze.milab.dust.math;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustUnitHandles;

public interface DustMathHandles extends DustConsts, DustUnitHandles {
	
	MindHandle MATH_ASP_HOST = Dust.lookup("giskard:15:?");
	MindHandle MATH_ATT_HOST_PORT = Dust.lookup("giskard:15:?");
	MindHandle MATH_ATT_HOST_IPV4 = Dust.lookup("giskard:15:?");

	MindHandle MATH_TAG_AGGRTYPE = Dust.lookup("giskard:15:?");
	MindHandle MATH_TAG_AGGRTYPE_FIRST = Dust.lookup("giskard:15:?");
	MindHandle MATH_TAG_AGGRTYPE_LAST = Dust.lookup("giskard:15:?");
	MindHandle MATH_TAG_AGGRTYPE_MIN = Dust.lookup("giskard:15:?");
	MindHandle MATH_TAG_AGGRTYPE_MAX = Dust.lookup("giskard:15:?");
	MindHandle MATH_TAG_AGGRTYPE_AVG = Dust.lookup("giskard:15:?");

}
