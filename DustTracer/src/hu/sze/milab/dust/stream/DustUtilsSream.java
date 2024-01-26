package hu.sze.milab.dust.stream;

import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.utils.DustUtils;

public class DustUtilsSream implements DustConsts {
	
	public static String csvEscape(String valStr, boolean addQuotes) {
		String ret = (null == valStr) ? "" : valStr.replace("\"", "\"\"").replaceAll("\\s+", " ");

		if ( addQuotes ) {
			ret = "\"" + ret + "\"";
		}

		return ret;
	}

	public static String csvUnEscape(String valStr, boolean removeQuotes) {
		if ( DustUtils.isEmpty(valStr) || !valStr.contains("\"")) {
			return valStr;
		}
		
		String ret = ( removeQuotes && valStr.startsWith("\"") ) ? valStr.substring(1, valStr.length()-1) : valStr;
		ret = valStr.replace("\"\"", "\"");

		return ret;
	}

}
