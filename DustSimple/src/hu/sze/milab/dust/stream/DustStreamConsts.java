package hu.sze.milab.dust.stream;

import java.io.InputStream;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustMetaConsts;

public interface DustStreamConsts extends DustMetaConsts {
	
	public static MindHandle STREAM_UNIT = Dust.resolveID(null, null);
	
	public static MindHandle STREAM_ASP_STREAM = Dust.resolveID(null, null);
	public static MindHandle STREAM_ATT_STREAM_PATH = Dust.resolveID(null, null);
	public static MindHandle STREAM_ATT_STREAM_FILE = Dust.resolveID(null, null);
	public static MindHandle STREAM_ATT_STREAM_PROVIDER = Dust.resolveID(null, null);

	public static MindHandle STREAM_LOG_JSONPARSER = Dust.resolveID(null, null);
	
	public static MindHandle STREAM_LOG_JSONAPISERIALIZER = Dust.resolveID(null, null);
	public static MindHandle STREAM_LOG_JSONAPIREADER = Dust.resolveID(null, null);

	interface StreamProcessor {
		public void processStream(InputStream is, String url) throws Exception;
	}
	
	interface StreamProvider<StreamType> {
		public StreamType getStream(Object id) throws Exception;
	}
}
