package hu.sze.milab.dust.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.utils.IOUtils;

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

	public static void download(String url, OutputStream target, Collection<String> headers) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

		for (String h : headers) {
			int s = h.indexOf(":");
			String key = h.substring(0, s).trim();
			String val = h.substring(s + 1).trim();
			conn.setRequestProperty(key, val);
		}

		InputStream is = conn.getInputStream();

		if ( "gzip".equals(conn.getContentEncoding()) ) {
			try (GZIPInputStream i = new GZIPInputStream(is)) {
				IOUtils.copy(i, target);
			}
		} else {
			try (BufferedInputStream in = new BufferedInputStream(is)) {
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					target.write(dataBuffer, 0, bytesRead);
				}
			}
		}
	}

}
