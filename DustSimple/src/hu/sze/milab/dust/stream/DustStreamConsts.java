package hu.sze.milab.dust.stream;

import java.io.InputStream;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustMetaConsts;

public interface DustStreamConsts extends DustMetaConsts {
	
	public static MindHandle STREAM_UNIT = Dust.createHandle();
	
	public static MindHandle STREAM_ASP_STREAM = Dust.createHandle();
	public static MindHandle STREAM_ATT_STREAM_FILE = Dust.createHandle();

	
	interface StreamProcessor {
		public void processStream(InputStream is, String url) throws Exception;
	}
}
