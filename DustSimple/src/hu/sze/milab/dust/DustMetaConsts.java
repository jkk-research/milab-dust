package hu.sze.milab.dust;

public interface DustMetaConsts extends DustConsts {
		
	public static MindHandle MIND_UNIT = Dust.createHandle();
	
	public static MindHandle MIND_ASP_UNIT = Dust.createHandle();
	public static MindHandle MIND_ATT_UNIT_CONTEXT = Dust.createHandle();
	
	public static MindHandle MIND_ASP_KNOWLEDGE = Dust.createHandle();
	public static MindHandle MIND_ATT_KNOWLEDGE_HANDLE = Dust.createHandle();
	public static MindHandle MIND_ATT_KNOWLEDGE_UNIT = Dust.createHandle();
	public static MindHandle MIND_ATT_KNOWLEDGE_TAGS = Dust.createHandle();
	public static MindHandle MIND_ATT_KNOWLEDGE_LISTENERS = Dust.createHandle();
	public static MindHandle MIND_ATT_KNOWLEDGE_PRIMARYASPECT = Dust.createHandle();
	public static MindHandle MIND_ATT_KNOWLEDGE_ID = Dust.createHandle();
	
	public static MindHandle MIND_ASP_TYPE = Dust.createHandle();
	public static MindHandle MIND_ASP_MEMBER = Dust.createHandle();
	public static MindHandle MIND_ASP_TAG = Dust.createHandle();
	
	public static MindHandle MIND_ASP_AGENT = Dust.createHandle();
	public static MindHandle MIND_ATT_AGENT_SELF = Dust.createHandle();
	
	public static MindHandle MIND_ASP_DIALOG = Dust.createHandle();
	
	public static MindHandle DUST_UNIT = Dust.createHandle();

	public static MindHandle DUST_ASP_BRAIN = Dust.createHandle();
	public static MindHandle DUST_ATT_BRAIN_LAUNCHPARAMS = Dust.createHandle();
	public static MindHandle DUST_ATT_BRAIN_UNITS = Dust.createHandle();
	public static MindHandle DUST_ATT_BRAIN_DIALOGS = Dust.createHandle();
	public static MindHandle DUST_ATT_BRAIN_THREADS = Dust.createHandle();
	
	public static MindHandle DUST_ASP_THREAD = Dust.createHandle();
	public static MindHandle DUST_ATT_THREAD_DIALOG = Dust.createHandle();
	public static MindHandle DUST_ATT_THREAD_ACTIVEAGENT = Dust.createHandle();

	public static MindHandle DUST_ASP_NATIVE = Dust.createHandle();
	public static MindHandle DUST_ATT_NATIVE_INSTANCE = Dust.createHandle();
	public static MindHandle DUST_ATT_NATIVE_CONTEXT = Dust.createHandle();
	
	public static MindHandle MISC_UNIT = Dust.createHandle();
	
	public static MindHandle MISC_ASP_CONN = Dust.createHandle();
	public static MindHandle MISC_ATT_CONN_PARENT = Dust.createHandle();
	public static MindHandle MISC_ATT_CONN_TARGET = Dust.createHandle();
	public static MindHandle MISC_ATT_CONN_REQUIRES = Dust.createHandle();
	
	public static MindHandle MISC_ATT_CONN_MEMBERMAP = Dust.createHandle();
	public static MindHandle MISC_ATT_CONN_MEMBERARR = Dust.createHandle();
	public static MindHandle MISC_ATT_CONN_MEMBERSET = Dust.createHandle();
	
	public static MindHandle MISC_ASP_GEN = Dust.createHandle();
	public static MindHandle MISC_TAG_GEN_EMPTY = Dust.createHandle();
	
	public static MindHandle MISC_ASP_VARIANT = Dust.createHandle();
	public static MindHandle MISC_ATT_VARIANT_VALUE = Dust.createHandle();
	
	public static MindHandle TEXT_UNIT = Dust.createHandle();
	
	public static MindHandle TEXT_ASP_NAMED = Dust.createHandle();
	public static MindHandle TEXT_ATT_NAMED_NAME = Dust.createHandle();
	
}
