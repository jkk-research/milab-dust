package hu.sze.milab.dust.stream;

import java.io.File;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustStreamUtils extends DustUtilsFile implements DustStreamConsts {

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
		
		String ret = ( removeQuotes && valStr.startsWith("\"") ) ? valStr.substring(1, valStr.length()-1) : valStr;
		ret = valStr.replace("\"\"", "\"");

		return ret;
	}

}
