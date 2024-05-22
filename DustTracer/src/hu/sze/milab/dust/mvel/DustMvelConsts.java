package hu.sze.milab.dust.mvel;

import hu.sze.milab.dust.DustHandles;
import hu.sze.milab.dust.utils.DustUtilsConsts;

public interface DustMvelConsts extends DustHandles, DustUtilsConsts {

	public interface MvelDataWrapper {
		Number getNum(String conceptId);
	}
}
