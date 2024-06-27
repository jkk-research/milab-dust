package hu.sze.milab.dust;

public interface DustCoreHandles extends DustConsts, DustUnitHandles {
	// Original

	MindHandle APP_MACHINE_MAIN = Dust.lookup("giskard:0:?");

	MindHandle APP_MODULE_MAIN = Dust.lookup("giskard:0:?");

	MindHandle APP_ASSEMBLY_MAIN = Dust.lookup("giskard:0:?");

	MindHandle MIND_ASP_UNIT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_UNIT_HANDLES = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_UNIT_CONTENT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_UNIT_AUTHOR = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_KNOWLEDGE = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_HANDLE = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_UNIT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_TAGS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_LISTENERS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_PRIMARYASPECT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_KNOWLEDGE_ASPECTS = Dust.lookup("giskard:1:?");
	
	MindHandle MIND_ASP_FACTORY = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_FACTORY_PRIMARYASPECT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_FACTORY_DEFATTS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_FACTORY_NARRATIVE = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_ASPECT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_ASPECT_ATTFACTORIES = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_ATTRIBUTE = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_ATTRIBUTE_FACTORY = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_TAG = Dust.lookup("giskard:1:?");
	MindHandle MIND_ASP_NARRATIVE = Dust.lookup("giskard:1:?");
	
	MindHandle MIND_ASP_ASSEMBLY = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_ASSEMBLY_UNITS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_ASSEMBLY_STARTAGENTS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_ASSEMBLY_STARTCOMMITS = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_DIALOG = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_DIALOG_ASSEMBLY = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_DIALOG_LAUNCHPARAMS = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_DIALOG_ACTIVEAGENT = Dust.lookup("giskard:1:?");

	MindHandle MIND_ASP_AGENT = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_AGENT_NARRATIVE = Dust.lookup("giskard:1:?");
	MindHandle MIND_ATT_AGENT_TARGET = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_CONTEXT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_ACTION = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_SELF = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_TARGET = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_DIALOG = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_DIRECT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_VISITITEM = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_VISITATT = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_VISITKEY = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_CONTEXT_VISITVALUE = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_VISITFOLLOWREF = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_VISITFOLLOWREF_NO = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_VISITFOLLOWREF_ONCE = Dust.lookup("giskard:1:?");
	MindHandle MIND_TAG_VISITFOLLOWREF_ALWAYS = Dust.lookup("giskard:1:?");

	MindHandle MIND_TAG_UNIT = Dust.lookup("giskard:1:?");

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
	MindHandle MIND_TAG_ACCESS_VISIT = Dust.lookup("giskard:1:?");

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

	MindHandle MIND_NAR_SELECT = Dust.lookup("giskard:1:?");
	MindHandle MIND_NAR_JOIN = Dust.lookup("giskard:1:?");

	MindHandle DUST_NAR_MACHINE = Dust.lookup("giskard:2:?");
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
	MindHandle DUST_ATT_MACHINE_UNIT_RESOLVER = Dust.lookup("giskard:2:?");

	MindHandle DUST_ASP_MODULE = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MODULE_UNITS = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MODULE_NARRATIVEIMPLS = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_MODULE_LIBRARIES = Dust.lookup("giskard:2:?");

	MindHandle DUST_ASP_THREAD = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_THREAD_DIALOG = Dust.lookup("giskard:2:?");

	MindHandle DUST_ASP_IMPL = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_IMPL_NARRATIVE = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_IMPL_INSTANCE = Dust.lookup("giskard:2:?");
	MindHandle DUST_ATT_IMPL_DATA = Dust.lookup("giskard:2:?");

	MindHandle DUST_TAG_NATIVE_SERVER = Dust.lookup("giskard:2:?");

	MindHandle MISC_ASP_CONN = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_OWNER = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_PARENT = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_SOURCE = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_TARGET = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_REQUIRES = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_MEMBERMAP = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_MEMBERARR = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_MEMBERSET = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_SPACE = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_NEXT = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_CONN_PREV = Dust.lookup("giskard:3:?");

	MindHandle MISC_ASP_ALIAS = Dust.lookup("giskard:3:?");
	MindHandle MISC_ASP_GEN = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_GEN_COUNT = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_GEN_SEP_ITEM = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_GEN_SEP_LINE = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_GEN_TARGET_ATT = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_GEN_EXTMAP = Dust.lookup("giskard:3:?");


	MindHandle MISC_TAG_EMPTY = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_ACTIVE = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_NEGATE = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_DBLHASH = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_READONLY = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_HIDDEN = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_SORTED = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_TRANSACTION = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_LOADING = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_ROOT = Dust.lookup("giskard:3:?");

	MindHandle MISC_TAG_DIRECTION = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_DIRECTION_IN = Dust.lookup("giskard:3:?");
	MindHandle MISC_TAG_DIRECTION_OUT = Dust.lookup("giskard:3:?");

	MindHandle MISC_ASP_REF = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_REF_PATH = Dust.lookup("giskard:3:?");

	MindHandle MISC_ASP_SPACE = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_SPACE_DIMENSIONS = Dust.lookup("giskard:3:?");
	
	MindHandle MISC_ASP_SHAPE = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_SHAPE_VECTORS = Dust.lookup("giskard:3:?");
	
	MindHandle MISC_ASP_VECTOR = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_VECTOR_COORDINATES = Dust.lookup("giskard:3:?");

	MindHandle MISC_ASP_VARIANT = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_VARIANT_VALUE = Dust.lookup("giskard:3:?");

	MindHandle MISC_NAR_ATTCOLLECTOR = Dust.lookup("giskard:3:?");

	MindHandle MISC_NAR_TABLE = Dust.lookup("giskard:3:?");

	MindHandle MISC_NAR_FACTORY = Dust.lookup("giskard:3:?");
	MindHandle MISC_ATT_FACTORY_TYPE = Dust.lookup("giskard:3:?");
	
	MindHandle MISC_NAR_COUNTER = Dust.lookup("giskard:3:?");
	

	MindHandle TEXT_TAG_LANGUAGE = Dust.lookup("giskard:4:?");
	MindHandle TEXT_TAG_LANGUAGE_EN_US = Dust.lookup("giskard:4:?");

	MindHandle TEXT_ATT_LANGUAGE_DEFAULT = Dust.lookup("giskard:4:?");
	MindHandle TEXT_ATT_TOKEN = Dust.lookup("giskard:4:?");

	MindHandle TEXT_TAG_TYPE = Dust.lookup("giskard:4:?");
	MindHandle TEXT_TAG_TYPE_TOKEN = Dust.lookup("giskard:4:?");
	MindHandle TEXT_TAG_TYPE_LABEL = Dust.lookup("giskard:4:?");

	MindHandle TEXT_ASP_PLAIN = Dust.lookup("giskard:4:?");
	MindHandle TEXT_ATT_PLAIN_TEXT = Dust.lookup("giskard:4:?");

	MindHandle EVENT_ASP_TIME = Dust.lookup("giskard:5:?");
	
	MindHandle EVENT_ATT_TIME_YEAR = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_TIME_DAYOFYEAR = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_TIME_HOUR24 = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_TIME_MIN = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_TIME_SEC = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_TIME_MILLI = Dust.lookup("giskard:5:?");

	MindHandle EVENT_ASP_EVENT = Dust.lookup("giskard:5:?");
	
	MindHandle EVENT_ATT_EVENT_START = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_EVENT_END = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_EVENT_DURATION = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_EVENT_REPETITION = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_EVENT_TIMEFORMAT = Dust.lookup("giskard:5:?");

	MindHandle EVENT_ASP_CALENDAR = Dust.lookup("giskard:5:?");
	
	MindHandle EVENT_NAR_THROTTLE = Dust.lookup("giskard:5:?");
	MindHandle EVENT_ATT_LAST_ACTION = Dust.lookup("giskard:5:?");
	
	MindHandle EVENT_TAG_CAL = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_CAL_GREGORIAN = Dust.lookup("giskard:5:?");
	
	MindHandle EVENT_TAG_TIMEZONE = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TIMEZONE_GMT = Dust.lookup("giskard:5:?");

	MindHandle EVENT_TAG_DST = Dust.lookup("giskard:5:?");

	MindHandle EVENT_TAG_TYPE = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_EXCEPTIONTHROWN = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_EXCEPTIONSWALLOWED = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_ERROR = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_WARNING = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_INFO = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_TRACE = Dust.lookup("giskard:5:?");
	MindHandle EVENT_TAG_TYPE_BREAKPOINT = Dust.lookup("giskard:5:?");

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

	MindHandle RESOURCE_NAR_CACHE = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_ATT_CACHE_REQUEST = Dust.lookup("giskard:6:?");

	MindHandle RESOURCE_NAR_FILESYSTEM = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_NAR_ZIPREADER = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_NAR_XMLDOM = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_NAR_JSONDOM = Dust.lookup("giskard:6:?");
	MindHandle RESOURCE_NAR_CSVSAX = Dust.lookup("giskard:6:?");

	MindHandle DEV_ATT_HINT = Dust.lookup("giskard:7:?");
	MindHandle DEV_TAG_TEST = Dust.lookup("giskard:7:?");

	MindHandle DEV_NAR_FORGEUI = Dust.lookup("giskard:7:?");

	MindHandle GEOMETRY_ASP_NODE = Dust.lookup("giskard:12:?");

	MindHandle GEOMETRY_ASP_EDGE = Dust.lookup("giskard:12:?");
//	MindHandle GEOMETRY_ATT_EDGE_CLASS = Dust.lookup("giskard:12:?"); SHOULD still use tag until counter example found

	MindHandle GEOMETRY_ASP_GRAPH = Dust.lookup("giskard:12:?");
	MindHandle GEOMETRY_ATT_GRAPH_NODES = Dust.lookup("giskard:12:?");
	MindHandle GEOMETRY_ATT_GRAPH_EDGES = Dust.lookup("giskard:12:?");

	MindHandle GEOMETRY_TAG_VECTOR_LOCATION = Dust.lookup("giskard:12:?");
	MindHandle GEOMETRY_TAG_VECTOR_SIZE = Dust.lookup("giskard:12:?");
	MindHandle GEOMETRY_TAG_VECTOR_WEIGHT = Dust.lookup("giskard:12:?");
	MindHandle GEOMETRY_TAG_VECTOR_SCALE = Dust.lookup("giskard:12:?");
	MindHandle GEOMETRY_TAG_VECTOR_ROTATE = Dust.lookup("giskard:12:?");
	
	MindHandle GEOMETRY_TAG_VALTYPE = Dust.lookup("giskard:12:?");
	MindHandle GEOMETRY_TAG_VALTYPE_CARTESIAN_X = Dust.lookup("giskard:12:?");
	MindHandle GEOMETRY_TAG_VALTYPE_CARTESIAN_Y = Dust.lookup("giskard:12:?");
	MindHandle GEOMETRY_TAG_VALTYPE_CARTESIAN_Z = Dust.lookup("giskard:12:?");

	MindHandle EXPR_ASP_EXPRESSION = Dust.lookup("giskard:14:?");
	MindHandle EXPR_ATT_EXPRESSION_STATIC = Dust.lookup("giskard:14:?");
	MindHandle EXPR_ATT_EXPRESSION_STR = Dust.lookup("giskard:14:?");

	MindHandle EXPR_NAR_POPULATE = Dust.lookup("giskard:14:?");

	MindHandle EXPR_NAR_FILTER = Dust.lookup("giskard:14:?");

}