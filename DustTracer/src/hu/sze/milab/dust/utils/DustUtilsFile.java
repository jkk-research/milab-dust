package hu.sze.milab.dust.utils;

import java.io.File;
import java.io.IOException;

public class DustUtilsFile extends DustUtils implements DustUtilsConsts {

	public static void ensureDir(File f) throws Exception {
		if ( !f.isDirectory() && !f.mkdirs() ) {
			throw new IOException("failed to create directory " + f);
		}
	}

}
