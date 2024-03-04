package hu.sze.milab.dust.test;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;

public class DustTestAgentHelloWorld extends DustAgent implements DustTestConsts {

	@Override
	public MindHandle agentProcess() throws Exception {
		Dust.log(EVENT_TAG_TYPE_TRACE, "\n\n*** Hello, world! ***\n\n", MIND_TAG_ACTION_PROCESS);
		return MIND_TAG_RESULT_ACCEPT;
	}
	
	
	@Override
	public MindHandle agentInit() throws Exception {
		Dust.log(EVENT_TAG_TYPE_TRACE, "Hello, world", MIND_TAG_ACTION_INIT);
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public MindHandle agentBegin() throws Exception {
		Dust.log(EVENT_TAG_TYPE_TRACE, "Hello, world", MIND_TAG_ACTION_BEGIN);
		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		Dust.log(EVENT_TAG_TYPE_TRACE, "Hello, world", MIND_TAG_ACTION_END);
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public MindHandle agentRelease() throws Exception {
		Dust.log(EVENT_TAG_TYPE_TRACE, "Hello, world", MIND_TAG_ACTION_RELEASE);
		return MIND_TAG_RESULT_ACCEPT;
	}

}
