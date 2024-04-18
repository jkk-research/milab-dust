package hu.sze.milab.dust.stream;

import java.io.File;
import java.io.IOException;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustStreamUtils extends DustUtilsFile implements DustStreamConsts {

	public static boolean checkPathBound(String path) throws IOException {
		File root = new File(".");
		File f = new File(root, path);

		return f.getCanonicalPath().startsWith(root.getCanonicalPath()) ;
	}
	
	public static File optGetFile(Object... path) throws IOException {
		String p = DustUtils.toString(DustUtils.sbAppend(null, File.separator, false, path));
		File f = new File(p);
		return f.isFile() ? f : null ;
	}
	
	public static File getFile(MindHandle ctx, Object... path) throws Exception {

		String fileName = Dust.access(MindAccess.Peek, null, ctx, path);
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
	
	public static String csvOptEscape(String valStr, String sepChar) {
		String ret = valStr;
		
		if ( valStr.contains(sepChar) || valStr.contains("\"") || valStr.contains("\n") ) {
			ret = csvEscape(valStr, true);
		}
		return ret;
	}
	
	public static String csvEscape(String valStr, boolean addQuotes) {
		String ret = (null == valStr) ? "" : valStr.replace("\"", "\"\"").replaceAll("\\s+", " ");

		if ( addQuotes ) {
			ret = "\"" + ret + "\"";
		}

		return ret;
	}

	public static String csvOptUnEscape(String valStr, boolean removeQuotes) {
		if ( DustUtils.isEmpty(valStr) || !valStr.contains("\"")) {
			return valStr;
		}
		
		String ret = valStr;
		if ( removeQuotes ) {
			if ( valStr.startsWith("\"") ) {
				ret = valStr.substring(1, valStr.length()-1);
			}
		}
		
		ret = ret.replace("\"\"", "\"");

		return ret;
	}

}
