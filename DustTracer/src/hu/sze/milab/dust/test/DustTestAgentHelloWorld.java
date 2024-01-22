package hu.sze.milab.dust.test;

import hu.sze.milab.dust.DustAgent;

public class DustTestAgentHelloWorld extends DustAgent implements DustTestConsts {

	@Override
	public MindHandle agentProcess() throws Exception {
		System.out.println("\n*** Hello, world! ***\n");
		return MIND_TAG_RESULT_ACCEPT;
	}

}
