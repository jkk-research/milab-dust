package hu.sze.milab.dust;

public interface DustMetaConsts extends DustConsts {
		
	public static MindHandle MIND_UNIT = Dust.recall("1");
	
	public static MindHandle MIND_ASP_UNIT = Dust.recall("1:");
	public static MindHandle MIND_ATT_UNIT_HANDLES = Dust.recall("1:");
	
	public static MindHandle MIND_ASP_MEMORY = Dust.recall("1:");
	public static MindHandle MIND_ATT_MEMORY_KNOWLEDGE = Dust.recall("1:");
	
	public static MindHandle MIND_ASP_HANDLE = Dust.recall("1:");
	public static MindHandle MIND_ATT_HANDLE_UNIT = Dust.recall("1:");
	public static MindHandle MIND_ATT_HANDLE_ID = Dust.recall("1:");

	public static MindHandle MIND_ASP_KNOWLEDGE = Dust.recall("1:");
	public static MindHandle MIND_ATT_KNOWLEDGE_HANDLE = Dust.recall("1:");
	public static MindHandle MIND_ATT_KNOWLEDGE_TAGS = Dust.recall("1:");
	public static MindHandle MIND_ATT_KNOWLEDGE_LISTENERS = Dust.recall("1:");
	public static MindHandle MIND_ATT_KNOWLEDGE_PRIMARYASPECT = Dust.recall("1:");
	
	public static MindHandle MIND_ASP_ASPECT = Dust.recall("1:");
	public static MindHandle MIND_ASP_ATTRIBUTE = Dust.recall("1:");
	public static MindHandle MIND_ASP_TAG = Dust.recall("1:");
	
	public static MindHandle MIND_ASP_LOGIC = Dust.recall("1:");
	
	public static MindHandle MIND_ASP_ASSEMBLY = Dust.recall("1:");
	public static MindHandle MIND_ATT_ASSEMBLY_UNITS = Dust.recall("1:");

	public static MindHandle MIND_ASP_DIALOG = Dust.recall("1:");
	public static MindHandle MIND_ATT_DIALOG_ASSEMBLY = Dust.recall("1:");
	public static MindHandle MIND_ATT_DIALOG_LAUNCHPARAMS = Dust.recall("1:");
	public static MindHandle MIND_ATT_DIALOG_KNOWLEDGE = Dust.recall("1:");

	public static MindHandle MIND_ASP_AGENT = Dust.recall("1:");
	public static MindHandle MIND_ATT_AGENT_LOGIC = Dust.recall("1:");
	public static MindHandle MIND_TAG_AGENT_SELFLISTENER = Dust.recall("1:");
	
	public static MindHandle MIND_TAG_VALTYPE = Dust.recall("1:");
	public static MindHandle MIND_TAG_VALTYPE_INT = Dust.recall("1:");
	public static MindHandle MIND_TAG_VALTYPE_REAL = Dust.recall("1:");
	public static MindHandle MIND_TAG_VALTYPE_HANDLE = Dust.recall("1:");
	public static MindHandle MIND_TAG_VALTYPE_BIN = Dust.recall("1:");

	public static MindHandle MIND_TAG_COLLTYPE = Dust.recall("1:");
	public static MindHandle MIND_TAG_COLLTYPE_ONE = Dust.recall("1:");
	public static MindHandle MIND_TAG_COLLTYPE_SET = Dust.recall("1:");
	public static MindHandle MIND_TAG_COLLTYPE_ARR = Dust.recall("1:");
	public static MindHandle MIND_TAG_COLLTYPE_MAP = Dust.recall("1:");

	public static MindHandle MIND_TAG_ACCESS = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACCESS_CHECK = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACCESS_PEEK = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACCESS_GET = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACCESS_SET = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACCESS_INSERT = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACCESS_DELETE = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACCESS_RESET = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACCESS_COMMIT = Dust.recall("1:");

	public static MindHandle MIND_TAG_ACTION = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACTION_INIT = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACTION_BEGIN = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACTION_PROCESS = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACTION_END = Dust.recall("1:");
	public static MindHandle MIND_TAG_ACTION_RELEASE = Dust.recall("1:");

	public static MindHandle MIND_TAG_STATUS = Dust.recall("1:");
	public static MindHandle MIND_TAG_STATUS_IDLE = Dust.recall("1:");
	public static MindHandle MIND_TAG_STATUS_PROCESSING = Dust.recall("1:");
	public static MindHandle MIND_TAG_STATUS_WAITING = Dust.recall("1:");
	public static MindHandle MIND_TAG_STATUS_ERROR = Dust.recall("1:");

	public static MindHandle MIND_TAG_RESULT = Dust.recall("1:");
	public static MindHandle MIND_TAG_RESULT_REJECT = Dust.recall("1:");
	public static MindHandle MIND_TAG_RESULT_PASS = Dust.recall("1:");
	public static MindHandle MIND_TAG_RESULT_READ = Dust.recall("1:");
	public static MindHandle MIND_TAG_RESULT_READACCEPT = Dust.recall("1:");
	public static MindHandle MIND_TAG_RESULT_ACCEPT = Dust.recall("1:");

	
	public static MindHandle DUST_UNIT = Dust.recall("2");

	public static MindHandle DUST_ASP_MACHINE = Dust.recall("2:");
	public static MindHandle DUST_ATT_MACHINE_ASSEMBLIES = Dust.recall("2:");
	public static MindHandle DUST_ATT_MACHINE_DIALOGS = Dust.recall("2:");
	public static MindHandle DUST_ATT_MACHINE_THREADS = Dust.recall("2:");
	
	public static MindHandle DUST_ASP_THREAD = Dust.recall("2:");
//	public static MindHandle DUST_ATT_THREAD_CONTEXTS = Dust.createHandle();

	public static MindHandle DUST_ASP_NATIVE = Dust.recall("2:");
	public static MindHandle DUST_ATT_NATIVE_IMPLEMENTATION = Dust.recall("2:");
	public static MindHandle DUST_ATT_NATIVE_INSTANCE = Dust.recall("2:");
	public static MindHandle DUST_ATT_NATIVE_CONTEXT = Dust.recall("2:");
	
	public static MindHandle MISC_UNIT = Dust.recall("3");
	
	public static MindHandle MISC_ASP_CONN = Dust.recall("3:");
	public static MindHandle MISC_ATT_CONN_PARENT = Dust.recall("3:");
	public static MindHandle MISC_ATT_CONN_TARGET = Dust.recall("3:");
	public static MindHandle MISC_ATT_CONN_REQUIRES = Dust.recall("3:");
	
	public static MindHandle MISC_ATT_CONN_MEMBERMAP = Dust.recall("3:");
	public static MindHandle MISC_ATT_CONN_MEMBERARR = Dust.recall("3:");
	public static MindHandle MISC_ATT_CONN_MEMBERSET = Dust.recall("3:");
	
	public static MindHandle MISC_ASP_GEN = Dust.recall("3:");
	public static MindHandle MISC_ATT_ALIAS = Dust.recall("3:");
	public static MindHandle MISC_ATT_COUNT = Dust.recall("3:");
	public static MindHandle MISC_ATT_CUSTOM = Dust.recall("3:");
	public static MindHandle MISC_TAG_GEN_EMPTY = Dust.recall("3:");
	public static MindHandle MISC_TAG_GEN_ACTIVE = Dust.recall("3:");
	
	public static MindHandle MISC_ASP_VARIANT = Dust.recall("3:");
	public static MindHandle MISC_ATT_VARIANT_VALUE = Dust.recall("3:");
	
	public static MindHandle TEXT_UNIT = Dust.recall("4");
	
	public static MindHandle TEXT_ASP_NAMED = Dust.recall("4:");
	public static MindHandle TEXT_ATT_NAME = Dust.recall("4:");	
	
	public static MindHandle TEXT_ASP_PLAIN = Dust.recall("4:");
	public static MindHandle TEXT_ATT_PLAIN_TEXT = Dust.recall("4:");	
	
	
	public static MindHandle EVENT_UNIT = Dust.recall("5");

	public static MindHandle EVENT_ASP_EVENT = Dust.recall("5:");

	public static MindHandle EVENT_TAG_TYPE_EXCEPTIONTHROWN = Dust.recall("5:");
	public static MindHandle EVENT_TAG_TYPE_EXCEPTIONSWALLOWED = Dust.recall("5:");

	public static MindHandle EVENT_TAG_TRACE = Dust.recall("5:");

}
