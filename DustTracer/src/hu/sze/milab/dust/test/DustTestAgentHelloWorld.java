package hu.sze.milab.dust.test;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustConsts;

public class DustTestAgentHelloWorld extends DustAgent implements DustTestConsts, DustConsts.MindServer {

	@Override
	public MindHandle agentProcess() throws Exception {
		System.out.println("\n*** Hello, world! ***\n");
		return MIND_TAG_RESULT_ACCEPT;
	}
	
	@Override
	public MindHandle agentInit() throws Exception {
		Dust.log(EVENT_TAG_TYPE_TRACE, "Hello, world", MIND_TAG_ACTION_INIT);
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public MindHandle agentRelease() throws Exception {
		Dust.log(EVENT_TAG_TYPE_TRACE, "Hello, world", MIND_TAG_ACTION_RELEASE);
		return MIND_TAG_RESULT_ACCEPT;
	}

}
