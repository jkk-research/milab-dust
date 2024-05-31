package hu.sze.milab.dust;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public interface DustConsts {
	String DUST_SEP = "_";
	String DUST_SEP_ID = ":";

	String DUST_CHARSET_UTF8 = StandardCharsets.UTF_8.name();// "UTF-8";
	String DUST_FMT_TIMESTAMP = "yyyy-MM-dd'T'HH_mm_ss";
	String DUST_FMT_DATE = "yyyy-MM-dd";
	
	String DUST_EXT_JSON = ".json";
	String DUST_EXT_CSV = ".csv";
	String DUST_EXT_XML = ".xml";

	String DUST_EXT_JS = ".js";

	String DUST_EXT_JAR = ".jar";
	String DUST_EXT_JAVA = ".java";
	String DUST_EXT_CLASS = ".class";
	
	enum MindAccess {
		Check, Peek, Get, Set, Insert, Delete, Reset, Commit, Broadcast, Lookup, Visit, 
	};
	
	enum MindAction {
		Init, Begin, Process, End, Release,
	};

	enum MindContext {
		Action, Self, Target, Dialog, Direct, 
	};

	int KEY_ADD = -1;
	int KEY_SIZE = -2;
//	int KEY_ITER = -3;
//	int KEY_KEYS = -4;

	String ITEMID_NEW = "?";

	interface MindHandle {
		String getId();
	};

	interface MindAgent {
		MindHandle agentProcess(MindAction action) throws Exception;
	}

	class MindCommitFilter {
		private MindHandle agent;
		
		private Set<MindHandle> actions;
		
		public MindCommitFilter(MindHandle agent, MindHandle... actions) {
			this.agent = agent;
			this.actions = new HashSet<MindHandle>();
			
			for ( MindHandle ah : actions ) {
				this.actions.add(ah);
			}
		}
		
		public MindHandle getAgent(Object action) {
			if ((null == action) || actions.contains(action)) {
				return agent;
			}
			return null;
		}
	}

}
