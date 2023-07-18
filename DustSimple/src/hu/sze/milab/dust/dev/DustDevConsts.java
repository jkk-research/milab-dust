package hu.sze.milab.dust.dev;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustMetaConsts;

public interface DustDevConsts extends DustMetaConsts {
	
	public static MindHandle DEV_UNIT = Dust.resolveID(null, null);
	
	public static MindHandle DEV_LOG_DUMP = Dust.resolveID(null, null);
}
