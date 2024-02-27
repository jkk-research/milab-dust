package hu.sze.milab.dust.test;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.machine.DustMachineTempUtils;

public class DustTestBootSimple implements DustTestConsts {
	
	public static void boot(String[] launchParams) throws Exception {
		DustMachineTempUtils.test();
		
		helloWorld();
	}
	
	public static void helloWorld() throws Exception {
		MindHandle hLogHelloWorld = Dust.lookup("test:0:?");
		Dust.access(MindAccess.Set, MIND_ASP_LOGIC, hLogHelloWorld, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

		MindHandle hNatHelloWorld = Dust.lookup("test:0:?");
		Dust.access(MindAccess.Set, DUST_ASP_NATIVELOGIC, hNatHelloWorld, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
		
		Dust.access(MindAccess.Set, hLogHelloWorld, hNatHelloWorld, DUST_ATT_NATIVELOGIC_LOGIC);
		Dust.access(MindAccess.Set, DustTestAgentHelloWorld.class.getCanonicalName(), hNatHelloWorld, DUST_ATT_NATIVELOGIC_IMPLEMENTATION);
		Dust.access(MindAccess.Set, true, hNatHelloWorld, MIND_ATT_KNOWLEDGE_TAGS, DUST_TAG_NATIVELOGIC_SERVER);
		
		Dust.access(MindAccess.Set, hNatHelloWorld, APP_MODULE_MAIN, DUST_ATT_MODULE_NATIVELOGICS, KEY_ADD);

		MindHandle hAgtHelloWorld = Dust.lookup("test:0:?");
		Dust.access(MindAccess.Set, MIND_ASP_AGENT, hAgtHelloWorld, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

		Dust.access(MindAccess.Set, hLogHelloWorld, hAgtHelloWorld, MIND_ATT_AGENT_LOGIC);

		Dust.access(MindAccess.Set, hAgtHelloWorld, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);
	}
	
}
