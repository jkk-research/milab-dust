package hu.sze.milab.dust.stream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustStreamUrlCache implements DustStreamConsts {
	private File root;
	private boolean useHash;

	public DustStreamUrlCache(File root, boolean useHash) {
		this.root = root;
		if ( !root.exists() ) {
			root.mkdirs();
		}
		this.useHash = useHash;
	}
	
	public <ContentType> ContentType access(String url, StreamProcessor<ContentType> proc) throws Exception {
		String fileName = url;
		
		int cut = fileName.indexOf("://");
		if ( -1 != cut ) {
			fileName = fileName.substring(cut+3);
		}
		
		return access(url, fileName, proc);
	}
	
	public <ContentType> ContentType access(String url, String fileName, StreamProcessor<ContentType> proc) throws Exception {
		ContentType ret = null;
		String fn = useHash ? DustUtilsFile.getHashName(fileName) : fileName;
		File f = new File(root, fn);
		
		if (!f.exists()) {
			File fp = f.getParentFile();
			if ( !fp.exists() ) {
				fp.mkdirs();
			}
			download(url, f);
		}
		
		if ( null != proc ) {
			try (FileInputStream is = new FileInputStream(f)) {
				ret = proc.processStream(is, url);
			}
		}
		
		return ret;
		
	}

	public static void download(String url, File file) throws Exception {
		Dust.dumpObs("Downloading url", url, "to file", file.getCanonicalPath() );

		url = url.replace(" ", "%20");
		
		try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream()); FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
		}
	}

}
