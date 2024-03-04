package hu.sze.milab.dust.test;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustHandles;
import hu.sze.milab.dust.utils.DustUtilsConsts;

public interface DustTestConsts extends DustHandles, DustUtilsConsts {
	MindHandle TEST0_UNIT = Dust.lookup("test:0");
}
