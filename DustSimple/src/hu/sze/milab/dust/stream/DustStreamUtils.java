package hu.sze.milab.dust.stream;

public class DustStreamUtils implements DustStreamConsts {
	
	public static String cutExcelSheetName(String ns) {
		int sep = ns.indexOf("://");
		if ( -1 != sep ) {
			ns = ns.substring(sep + 3);
		}
		ns = ns.replace("/", "_");
		
		int nl = ns.length();
		if ( 31 <= nl ) {
			ns = ns.substring(nl - 31, nl);
		}
		return ns;
	}

}
