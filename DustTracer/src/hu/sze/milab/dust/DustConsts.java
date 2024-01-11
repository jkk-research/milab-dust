package hu.sze.milab.dust;

import java.nio.charset.StandardCharsets;

public interface DustConsts {
	String DUST_SEP = "_";
	String DUST_SEP_ID = ":";

	String DUST_CHARSET_UTF8 = StandardCharsets.UTF_8.name();// "UTF-8";
	String DUST_FMT_TIMESTAMP = "yyyy-MM-dd'T'HH_mm_ss";
	String DUST_FMT_DATE = "yyyy-MM-dd";

	String DUST_EXT_JAR = ".jar";
	String DUST_EXT_JSON = ".json";
	String DUST_EXT_CSV = ".csv";
	String DUST_EXT_XML = ".xml";

	interface MindHandle {
		MindHandle getUnit();
		String getId();
	};

	interface MindAgent {
		MindHandle agentInit() throws Exception;

		MindHandle agentBegin() throws Exception;

		MindHandle agentProcess() throws Exception;

		MindHandle agentEnd() throws Exception;

		MindHandle agentRelease() throws Exception;
	}

	interface DustThreadOwner {
		boolean isCurrentThreadOwned();
	}

}
