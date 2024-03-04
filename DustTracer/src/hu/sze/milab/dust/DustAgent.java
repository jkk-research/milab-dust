package hu.sze.milab.dust;

public abstract class DustAgent implements DustConsts.MindAgent, DustHandles {
	
	@Override
	public final MindHandle agentProcess(MindAction action) throws Exception {
		switch ( action) {
		case Begin:
			return agentBegin();
		case End:
			return agentEnd();
		case Init:
			return agentInit();
		case Process:
			return agentProcess();
		case Release:
			return agentRelease();
		}
		
		return null;
	}

	protected MindHandle agentInit() throws Exception {
		return MIND_TAG_RESULT_READACCEPT;
	}

	protected MindHandle agentBegin() throws Exception {
		return MIND_TAG_RESULT_READACCEPT;
	}

	protected MindHandle agentProcess() throws Exception {
		return MIND_TAG_RESULT_READACCEPT;
	}

	protected MindHandle agentEnd() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}

	protected MindHandle agentRelease() throws Exception {
		return MIND_TAG_RESULT_READACCEPT;
	}

}