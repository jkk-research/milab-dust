package hu.sze.milab.dust;

public interface DustMetaConsts extends DustConsts {

	public static MindHandle MIND_UNIT = Dust.recall("1");

	public static MindHandle MIND_ASP_AUTHOR = Dust.recall("1:1");
	public static MindHandle MIND_ASP_UNIT = Dust.recall("1:2");
	public static MindHandle MIND_ATT_UNIT_HANDLES = Dust.recall("1:3");
	public static MindHandle MIND_ATT_UNIT_AUTHOR = Dust.recall("1:4");

	public static MindHandle MIND_ASP_MEMORY = Dust.recall("1:5");
	public static MindHandle MIND_ATT_MEMORY_KNOWLEDGE = Dust.recall("1:6");

	public static MindHandle MIND_ASP_HANDLE = Dust.recall("1:7");
	public static MindHandle MIND_ATT_HANDLE_UNIT = Dust.recall("1:8");
	public static MindHandle MIND_ATT_HANDLE_ID = Dust.recall("1:9");

	public static MindHandle MIND_ASP_KNOWLEDGE = Dust.recall("1:10");
	public static MindHandle MIND_ATT_KNOWLEDGE_HANDLE = Dust.recall("1:11");
	public static MindHandle MIND_ATT_KNOWLEDGE_TAGS = Dust.recall("1:12");
	public static MindHandle MIND_ATT_KNOWLEDGE_LISTENERS = Dust.recall("1:13");
	public static MindHandle MIND_ATT_KNOWLEDGE_PRIMARYASPECT = Dust.recall("1:14");

	public static MindHandle MIND_ASP_ASPECT = Dust.recall("1:15");
	public static MindHandle MIND_ASP_ATTRIBUTE = Dust.recall("1:16");
	public static MindHandle MIND_ASP_TAG = Dust.recall("1:17");
	public static MindHandle MIND_ASP_LOGIC = Dust.recall("1:18");
	public static MindHandle MIND_ASP_ASSEMBLY = Dust.recall("1:19");
	public static MindHandle MIND_ATT_ASSEMBLY_UNITS = Dust.recall("1:20");

	public static MindHandle MIND_ASP_DIALOG = Dust.recall("1:21");
	public static MindHandle MIND_ATT_DIALOG_ASSEMBLY = Dust.recall("1:22");
	public static MindHandle MIND_ATT_DIALOG_LAUNCHPARAMS = Dust.recall("1:23");

	public static MindHandle MIND_ASP_AGENT = Dust.recall("1:24");
	public static MindHandle MIND_ATT_AGENT_LOGIC = Dust.recall("1:25");

	public static MindHandle MIND_TAG_AGENT_SELFLISTENER = Dust.recall("1:26");

	public static MindHandle MIND_TAG_VALTYPE = Dust.recall("1:27");
	public static MindHandle MIND_TAG_VALTYPE_INT = Dust.recall("1:28");
	public static MindHandle MIND_TAG_VALTYPE_REAL = Dust.recall("1:29");
	public static MindHandle MIND_TAG_VALTYPE_HANDLE = Dust.recall("1:30");
	public static MindHandle MIND_TAG_VALTYPE_BIN = Dust.recall("1:31");

	public static MindHandle MIND_TAG_COLLTYPE = Dust.recall("1:32");
	public static MindHandle MIND_TAG_COLLTYPE_ONE = Dust.recall("1:33");
	public static MindHandle MIND_TAG_COLLTYPE_SET = Dust.recall("1:34");
	public static MindHandle MIND_TAG_COLLTYPE_ARR = Dust.recall("1:35");
	public static MindHandle MIND_TAG_COLLTYPE_MAP = Dust.recall("1:36");

	public static MindHandle MIND_TAG_ACCESS = Dust.recall("1:37");
	public static MindHandle MIND_TAG_ACCESS_CHECK = Dust.recall("1:38");
	public static MindHandle MIND_TAG_ACCESS_PEEK = Dust.recall("1:39");
	public static MindHandle MIND_TAG_ACCESS_GET = Dust.recall("1:40");
	public static MindHandle MIND_TAG_ACCESS_SET = Dust.recall("1:41");
	public static MindHandle MIND_TAG_ACCESS_INSERT = Dust.recall("1:42");
	public static MindHandle MIND_TAG_ACCESS_DELETE = Dust.recall("1:43");
	public static MindHandle MIND_TAG_ACCESS_RESET = Dust.recall("1:44");
	public static MindHandle MIND_TAG_ACCESS_COMMIT = Dust.recall("1:45");

	public static MindHandle MIND_TAG_ACTION = Dust.recall("1:46");
	public static MindHandle MIND_TAG_ACTION_INIT = Dust.recall("1:47");
	public static MindHandle MIND_TAG_ACTION_BEGIN = Dust.recall("1:48");
	public static MindHandle MIND_TAG_ACTION_PROCESS = Dust.recall("1:49");
	public static MindHandle MIND_TAG_ACTION_END = Dust.recall("1:50");
	public static MindHandle MIND_TAG_ACTION_RELEASE = Dust.recall("1:51");

	public static MindHandle MIND_TAG_STATUS = Dust.recall("1:52");
	public static MindHandle MIND_TAG_STATUS_IDLE = Dust.recall("1:53");
	public static MindHandle MIND_TAG_STATUS_PROCESSING = Dust.recall("1:54");
	public static MindHandle MIND_TAG_STATUS_WAITING = Dust.recall("1:55");
	public static MindHandle MIND_TAG_STATUS_ERROR = Dust.recall("1:56");

	public static MindHandle MIND_TAG_RESULT = Dust.recall("1:57");
	public static MindHandle MIND_TAG_RESULT_REJECT = Dust.recall("1:58");
	public static MindHandle MIND_TAG_RESULT_PASS = Dust.recall("1:59");
	public static MindHandle MIND_TAG_RESULT_READ = Dust.recall("1:60");
	public static MindHandle MIND_TAG_RESULT_READACCEPT = Dust.recall("1:61");
	public static MindHandle MIND_TAG_RESULT_ACCEPT = Dust.recall("1:62");

	public static MindHandle MIND_AUTHOR_DUST = Dust.recall("1:63");

	public static MindHandle DUST_UNIT = Dust.recall("2");

	public static MindHandle DUST_ASP_MACHINE = Dust.recall("2:1");
	public static MindHandle DUST_ATT_MACHINE_ASSEMBLIES = Dust.recall("2:2");
	public static MindHandle DUST_ATT_MACHINE_DIALOGS = Dust.recall("2:3");
	public static MindHandle DUST_ATT_MACHINE_THREADS = Dust.recall("2:4");

	public static MindHandle DUST_ASP_THREAD = Dust.recall("2:5");
	public static MindHandle DUST_ASP_NATIVE = Dust.recall("2:6");
	public static MindHandle DUST_ATT_NATIVE_IMPLEMENTATION = Dust.recall("2:7");
	public static MindHandle DUST_ATT_NATIVE_INSTANCE = Dust.recall("2:8");
	public static MindHandle DUST_ATT_NATIVE_CONTEXT = Dust.recall("2:9");

	public static MindHandle MISC_UNIT = Dust.recall("3");

	public static MindHandle MISC_ASP_CONN = Dust.recall("3:1");
	public static MindHandle MISC_ATT_CONN_OWNER = Dust.recall("3:2");
	public static MindHandle MISC_ATT_CONN_PARENT = Dust.recall("3:3");
	public static MindHandle MISC_ATT_CONN_TARGET = Dust.recall("3:4");
	public static MindHandle MISC_ATT_CONN_REQUIRES = Dust.recall("3:5");
	public static MindHandle MISC_ATT_CONN_MEMBERMAP = Dust.recall("3:6");
	public static MindHandle MISC_ATT_CONN_MEMBERARR = Dust.recall("3:7");
	public static MindHandle MISC_ATT_CONN_MEMBERSET = Dust.recall("3:8");

	public static MindHandle MISC_ASP_GEN = Dust.recall("3:9");
	public static MindHandle MISC_ATT_ALIAS = Dust.recall("3:10");
	public static MindHandle MISC_ATT_COUNT = Dust.recall("3:11");
	public static MindHandle MISC_ATT_CUSTOM = Dust.recall("3:12");

	public static MindHandle MISC_TAG_GEN_EMPTY = Dust.recall("3:13");

	public static MindHandle MISC_TAG_GEN_ACTIVE = Dust.recall("3:14");

	public static MindHandle MISC_ASP_VARIANT = Dust.recall("3:15");
	public static MindHandle MISC_ATT_VARIANT_VALUE = Dust.recall("3:16");

	public static MindHandle TEXT_UNIT = Dust.recall("4");

	public static MindHandle TEXT_TAG_LANGUAGE = Dust.recall("4:1");
	public static MindHandle TEXT_TAG_LANGUAGE_EN_US = Dust.recall("4:2");

	public static MindHandle TEXT_ATT_LANGUAGE_DEFAULT = Dust.recall("4:3");
	public static MindHandle TEXT_ATT_TOKEN = Dust.recall("4:4");

	public static MindHandle TEXT_TAG_TYPE = Dust.recall("4:5");
	public static MindHandle TEXT_TAG_TYPE_TOKEN = Dust.recall("4:6");
	public static MindHandle TEXT_TAG_TYPE_NAME = Dust.recall("4:7");

	public static MindHandle TEXT_ASP_PLAIN = Dust.recall("4:8");
	public static MindHandle TEXT_ATT_PLAIN_TEXT = Dust.recall("4:9");

	public static MindHandle EVENT_UNIT = Dust.recall("5");

	public static MindHandle EVENT_ASP_EVENT = Dust.recall("5:1");

	public static MindHandle EVENT_TAG_TYPE = Dust.recall("5:2");
	public static MindHandle EVENT_TAG_TYPE_EXCEPTIONTHROWN = Dust.recall("5:3");
	public static MindHandle EVENT_TAG_TYPE_EXCEPTIONSWALLOWED = Dust.recall("5:4");
	public static MindHandle EVENT_TAG_TYPE_TRACE = Dust.recall("5:5");
	public static MindHandle EVENT_TAG_TYPE_BREAKPOINT = Dust.recall("5:6");

	public static MindHandle RESOURCE_UNIT = Dust.recall("6");

	public static MindHandle DEV_UNIT = Dust.recall("7");

	public static MindHandle DEV_ATT_HINT = Dust.recall("7:1");

}