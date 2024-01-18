package hu.sze.milab.dust.test;

import hu.sze.milab.dust.Dust;

public class DustTestBootSimple implements DustTestConsts {
	
	public static void helloWorld() throws Exception {
		MindHandle hLogHelloWorld = Dust.recall("0:");
		Dust.access(hLogHelloWorld, MIND_TAG_ACCESS_SET, MIND_ASP_LOGIC, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

		MindHandle hNatHelloWorld = Dust.recall("0:");
		Dust.access(hNatHelloWorld, MIND_TAG_ACCESS_SET, DUST_ASP_NATIVELOGIC, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
		
		Dust.access(hNatHelloWorld, MIND_TAG_ACCESS_SET, hLogHelloWorld, DUST_ATT_NATIVELOGIC_LOGIC);
		Dust.access(hNatHelloWorld, MIND_TAG_ACCESS_SET, DustTestAgentHelloWorld.class.getCanonicalName(), DUST_ATT_NATIVELOGIC_IMPLEMENTATION);
		
		Dust.access(APP_MODULE_MAIN, MIND_TAG_ACCESS_SET, hNatHelloWorld, DUST_ATT_MODULE_NATIVELOGICS, KEY_ADD);

		MindHandle hAgtHelloWorld = Dust.recall("0:");
		Dust.access(hAgtHelloWorld, MIND_TAG_ACCESS_SET, MIND_ASP_AGENT, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

		Dust.access(hAgtHelloWorld, MIND_TAG_ACCESS_SET, hLogHelloWorld, MIND_ATT_AGENT_LOGIC);

		Dust.access(APP_ASSEMBLY_MAIN, MIND_TAG_ACCESS_SET, hAgtHelloWorld, MIND_ATT_ASSEMBLY_STARTAGENTS, KEY_ADD);

	}

}
