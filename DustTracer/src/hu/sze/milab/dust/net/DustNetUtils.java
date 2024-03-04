package hu.sze.milab.dust.net;

import java.io.File;

import hu.sze.milab.dust.utils.DustUtils;

public class DustNetUtils implements DustNetConsts {
	
	public static String getContentType(File f) {
		String ct = null;

		String ext = DustUtils.getPostfix(f.getName(), ".").toLowerCase();

		switch ( ext ) {
		case "csv":
			ct = MEDIATYPE_UTF8_CSV;
			break;
		case "xml":
			ct = MEDIATYPE_UTF8_XML;
			break;
		case "html":
		case "xhtml":
			ct = MEDIATYPE_UTF8_HTML;
			break;
			
		case "jpg":
			ct = "image/jpeg";
			break;
		case "ico":
			ct = "image/x-icon";
			break;
			
		case "png":
		case "jpeg":
		case "gif":
		case "bmp":
			ct = "image/" + ext;
			break;
		}

		return ct;
	}
}
