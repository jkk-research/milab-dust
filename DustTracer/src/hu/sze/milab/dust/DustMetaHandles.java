package hu.sze.milab.dust;

public interface DustMetaHandles extends DustConsts, DustUnitHandles {
	// Original

//	MindHandle APP_UNIT = Dust.lookup("giskard:0");

	MindHandle APP_MACHINE_MAIN = Dust.lookup("giskard:0:?");

	MindHandle APP_MODULE_MAIN = Dust.lookup("giskard:0:?");

	MindHandle APP_ASSEMBLY_MAIN = Dust.lookup("giskard:0:?");

//	MindHandle MIND_UNIT = Dust.lookup("giskard:1");

	MindHandle MIND_ASP_UNIT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_UNIT_HANDLES = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_UNIT_CONTENT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_UNIT_AUTHOR = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_HANDLE = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_HANDLE_UNIT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_HANDLE_ID = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_KNOWLEDGE = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_HANDLE = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_TAGS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_LISTENERS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_PRIMARYASPECT = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_ASPECT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ASP_ATTRIBUTE = Dust.lookup("giskard:1:?");
	MindHandle MIND_ASP_TAG = Dust.lookup("giskard:1:?");
	MindHandle MIND_ASP_LOGIC = Dust.lookup("giskard:1:?");
	MindHandle MIND_ASP_ASSEMBLY = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_ASSEMBLY_UNITS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_ASSEMBLY_STARTAGENTS = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_DIALOG = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_DIALOG_ASSEMBLY = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_DIALOG_LAUNCHPARAMS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_DIALOG_ACTIVEAGENT = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_AGENT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_AGENT_LOGIC = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_AGENT_TARGET = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_CONTEXT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_DIALOG = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_SELF = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_TARGET = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_DIRECT = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_VALTYPE = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_VALTYPE_INT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_VALTYPE_REAL = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_VALTYPE_HANDLE = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_VALTYPE_BIN = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_COLLTYPE = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_COLLTYPE_ONE = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_COLLTYPE_SET = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_COLLTYPE_ARR = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_COLLTYPE_MAP = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_ACCESS = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_CHECK = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_PEEK = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_GET = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_SET = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_INSERT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_DELETE = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_RESET = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_COMMIT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_BROADCAST = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACCESS_LOOKUP = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_ACTION = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACTION_INIT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACTION_BEGIN = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACTION_PROCESS = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACTION_END = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_ACTION_RELEASE = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_STATUS = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_STATUS_IDLE = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_STATUS_PROCESSING = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_STATUS_WAITING = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_STATUS_ERROR = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_RESULT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_RESULT_REJECT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_RESULT_PASS = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_RESULT_READ = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_RESULT_READACCEPT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_RESULT_ACCEPT = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_AUTHOR = Dust.lookup("giskard:1:?");

//	MindHandle DUST_UNIT = Dust.lookup("giskard:2");

	MindHandle DUST_ASP_MACHINE = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_AUTHORS = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_UNITS = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_ASSEMBLIES = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_MAINASSEMBLY = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_MODULES = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_DIALOGS = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_MAINDIALOG = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_THREADS = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_ALL_IMPLEMENTATIONS = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MACHINE_ACTIVE_SERVERS = Dust.lookup("giskard:2:?");

	MindHandle DUST_ASP_MODULE = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MODULE_UNITS = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MODULE_NATIVELOGICS = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MODULE_LIBRARIES = Dust.lookup("giskard:2:?");

	MindHandle DUST_ASP_THREAD = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_THREAD_DIALOG = Dust.lookup("giskard:2:?");

	MindHandle DUST_ASP_NATIVELOGIC = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_NATIVELOGIC_LOGIC = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_NATIVELOGIC_IMPLEMENTATION = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_NATIVELOGIC_INSTANCE = Dust.lookup("giskard:2:?");

	MindHandle DUST_TAG_NATIVELOGIC_SERVER = Dust.lookup("giskard:2:?");

//	MindHandle MISC_UNIT = Dust.lookup("giskard:3");

	MindHandle MISC_ASP_CONN = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_OWNER = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_PARENT = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_SOURCE = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_TARGET = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_REQUIRES = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_MEMBERMAP = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_MEMBERARR = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_MEMBERSET = Dust.lookup("giskard:3:?");

	MindHandle MISC_ASP_ALIAS = Dust.lookup("giskard:3:?");
	MindHandle MISC_ASP_GEN = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_GEN_COUNT = Dust.lookup("giskard:3:?");

	MindHandle MISC_TAG_EMPTY = Dust.lookup("giskard:3:?");

	MindHandle MISC_TAG_ACTIVE = Dust.lookup("giskard:3:?");

	MindHandle MISC_TAG_DIRECTION = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_DIRECTION_IN = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_DIRECTION_OUT = Dust.lookup("giskard:3:?");

	MindHandle MISC_ASP_VARIANT = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_VARIANT_VALUE = Dust.lookup("giskard:3:?");

//	MindHandle TEXT_UNIT = Dust.lookup("giskard:4");

	MindHandle TEXT_TAG_LANGUAGE = Dust.lookup("giskard:4:?");
	MindHandle TEXT_TAG_LANGUAGE_EN_US = Dust.lookup("giskard:4:?");

	MindHandle TEXT_ATT_LANGUAGE_DEFAULT = Dust.lookup("giskard:4:?");
	MindHandle TEXT_ATT_TOKEN = Dust.lookup("giskard:4:?");

	MindHandle TEXT_TAG_TYPE = Dust.lookup("giskard:4:?");
	MindHandle TEXT_TAG_TYPE_TOKEN = Dust.lookup("giskard:4:?");
	MindHandle TEXT_TAG_TYPE_LABEL = Dust.lookup("giskard:4:?");

	MindHandle TEXT_ASP_PLAIN = Dust.lookup("giskard:4:?");
	MindHandle TEXT_ATT_PLAIN_TEXT = Dust.lookup("giskard:4:?");

//	MindHandle EVENT_UNIT = Dust.lookup("giskard:5");

	MindHandle EVENT_ASP_EVENT = Dust.lookup("giskard:5:?");

	MindHandle EVENT_TAG_TYPE = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_EXCEPTIONTHROWN = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_EXCEPTIONSWALLOWED = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_ERROR = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_WARNING = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_INFO = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_TRACE = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_BREAKPOINT = Dust.lookup("giskard:5:?");

//	MindHandle RESOURCE_UNIT = Dust.lookup("giskard:6");

	MindHandle RESOURCE_ASP_URL = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_URL_SCHEME = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_URL_USERINFO = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_URL_HOST = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_URL_PATH = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_URL_QUERY = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_URL_FRAGMENT = Dust.lookup("giskard:6:?");

	MindHandle RESOURCE_ASP_STREAM = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_STREAM_CTYPEMAP = Dust.lookup("giskard:6:?");

	MindHandle RESOURCE_TAG_STREAMTYPE = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_TAG_STREAMTYPE_RAW = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_TAG_STREAMTYPE_TEXT = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_TAG_STREAMTYPE_ZIPENTRY = Dust.lookup("giskard:6:?");

	MindHandle RESOURCE_ASP_PROCESSOR = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_PROCESSOR_STREAM = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_PROCESSOR_DATA = Dust.lookup("giskard:6:?");

	MindHandle RESOURCE_LOG_FILESYSTEM = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_LOG_ZIPREADER = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_LOG_JSONDOM = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_LOG_CSVSAX = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_CSVSAX_SEP = Dust.lookup("giskard:6:?");

//	MindHandle DEV_UNIT = Dust.lookup("giskard:7");

	MindHandle DEV_ATT_HINT = Dust.lookup("giskard:7:?");

//	MindHandle DUSTJAVA_UNIT = Dust.lookup("giskard:8");

//	MindHandle L10N_UNIT = Dust.lookup("giskard:100");

}