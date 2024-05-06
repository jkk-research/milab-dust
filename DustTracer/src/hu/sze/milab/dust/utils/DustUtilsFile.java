package hu.sze.milab.dust.utils;

import java.io.File;
import java.io.IOException;

public class DustUtilsFile extends DustUtils implements DustUtilsConsts {

	public static void ensureDir(File f) throws Exception {
		if ( !f.isDirectory() && !f.mkdirs() ) {
			throw new IOException("failed to create directory " + f);
		}
	}

	public static String addHash2(String str) {
		return addHash2(str, ".");
	}

	public static String addHash2(String str, String sep) {
		return new StringBuilder(getHash2(str, sep)).append(File.separator).append(str).toString();
	}

	public static String getHash2(String str) {
		return getHash2(str, ".");
	}

	public static String getHash2(String str, String sep) {
		return DustUtils.getHash2(cutPostfix(str, sep), File.separator);
	}

	public static boolean exists(Object... pathSegments) {
		String path = DustUtils.sbAppend(null, File.separator, false, pathSegments).toString();
		
		return new File(path).exists();
	}

}
