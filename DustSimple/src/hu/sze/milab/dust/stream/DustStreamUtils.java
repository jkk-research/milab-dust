package hu.sze.milab.dust.stream;

import java.io.File;
import java.io.FileNotFoundException;
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
				
		public TempFileFactory(String queueDirPath) {
			this(new File("."), queueDirPath);
		}

		public TempFileFactory(File root, String queueDirPath) {
			dir = new File(root, queueDirPath);
			dir.mkdirs();
		}
		
		public synchronized File createFile(String ext) {
			return new File(dir, System.currentTimeMillis() + ext);
		}
	}
	
	public static class PrintWriterProvider implements StreamProvider<PrintWriter> {
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
	}

}
