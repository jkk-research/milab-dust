package hu.sze.milab.dust.utils;

import java.io.File;
import java.io.IOException;

import hu.sze.milab.dust.Dust;

public class DustUtilsFile extends DustUtils implements DustUtilsConsts {

	public static void ensureDir(File f) throws Exception {
		if ( !f.isDirectory() && !f.mkdirs() ) {
			throw new IOException("failed to create directory " + f);
		}
	}

	public static File getFile(MindHandle ctx, Object... path) throws Exception {

		String fileName = Dust.access(ctx, MIND_TAG_ACCESS_PEEK, null, path);
		Dust.log(EVENT_TAG_TYPE_TRACE, "Accessing file", fileName);
		
		File f;
		if ( fileName.startsWith(File.separator) ) {
			f = new File(fileName);
		} else {
			File home = new File(System.getProperty("user.home"));
			f = DustUtils.isEmpty(fileName) ? home : new File(home, fileName);
		}
		
		return f;
	}

}
