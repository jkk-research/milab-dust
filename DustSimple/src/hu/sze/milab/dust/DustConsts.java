package hu.sze.milab.dust;

import java.nio.charset.StandardCharsets;

public interface DustConsts {
	String CHARSET_UTF8 = StandardCharsets.UTF_8.name();// "UTF-8";
	String FMT_TIMESTAMP = "yyyy-MM-dd'T'HH_mm_ss";
	String FMT_DATE = "yyyy-MM-dd";

	String EXT_JAR = ".jar";
	String EXT_JSON = ".json";
	String EXT_CSV = ".csv";
	String EXT_XML = ".xml";
	String SEP = "_";
	String SEP_ID = ":";

	int KEY_ADD = -1;
	int KEY_SIZE = -2;
	int KEY_ITER = -3;
	int KEY_KEYS = -4;
	
	String PREFIX_MILAB = "hu.sze.milab.";

	interface MindHandle {
		String getID();
	};

	interface MindAgent {
		MindStatus agentExecAction(MindAction action) throws Exception;
	}

	enum MindValType {
		tagValtypeInt, tagValtypeReal, tagValtypeHandle, tagValtypeBin;
	};

	enum MindColl {
		One, Set, Arr, Map;
	};

	enum MindAccess {
		Check, Peek, Get, Set, Insert, Delete, Reset, Commit,
	};

	enum MindContext {
		Dialog, Self, Message
	};

	enum MindAction {
		Init, Begin, Process, End, Release,
	};

	enum MindStatus {
		Waiting, Processing, Reject, Pass, Read, ReadAccept, Accept, Error,
	};

	class MindUtils {
		public static boolean isCreateAccess(MindAccess acc) {
			return (acc == MindAccess.Set) || (acc == MindAccess.Insert);
		}
	}

}
