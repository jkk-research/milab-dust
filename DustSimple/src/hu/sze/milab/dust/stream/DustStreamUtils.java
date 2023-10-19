package hu.sze.milab.dust.stream;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.utils.DustUtilsFactory;

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
	
	public static class TempFileFactory {
		private File dir;
		
		private long lastFileID = 0;
		
		long getFileId() {
			long l = System.currentTimeMillis();
			
			if ( l <= lastFileID ) {
				l = ++lastFileID;
			} else {
				lastFileID = l;
			}
			
			return l;
		}
				
		public TempFileFactory(String queueDirPath) {
			this(new File("."), queueDirPath);
		}

		public TempFileFactory(File root, String queueDirPath) {
			dir = new File(root, queueDirPath);
			dir.mkdirs();
		}
		
		public synchronized File createFile(String ext) {
			return new File(dir, getFileId() + ext);
		}
	}
	
	public static class PrintWriterProvider implements StreamProvider<PrintWriter>, Closeable {
		private TempFileFactory ff;
		private String ext;
		
		Map<Object, File> files = new HashMap<>();
		
		private DustUtilsFactory<Object, PrintWriter> streams = new DustUtilsFactory<Object, PrintWriter>(false) {
			@Override
			protected PrintWriter create(Object key, Object... hints) {
				try {
					File f = ff.createFile(ext);
					files.put(key, f);
					return new PrintWriter(f);
				} catch (FileNotFoundException e) {
					return DustException.wrap(e);
				}
			}
			
		};
		
		public PrintWriterProvider(TempFileFactory ff, String ext) {
			this.ff = ff;
			this.ext = ext;
		}
		
		@Override
		public PrintWriter getStream(Object id) throws Exception {
			return streams.get(id);
		}
		
		public File getFile(Object id) {
			return files.get(id);
		}
		
		@Override
		public void close() throws IOException {
			for ( PrintWriter pw : streams.values() ) {
				pw.close();
			}
			for ( File f : files.values() ) {
				if ( f.exists() ) {
					f.delete();
				}
			}
			streams.clear();
			files.clear();
		}
	}	

}
