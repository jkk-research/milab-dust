package hu.sze.milab.dust;

public abstract class DustAgent implements DustConsts.MindAgent, DustMetaConsts {

	@Override
	public MindHandle agentInit() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public MindHandle agentBegin() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public MindHandle agentRelease() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}
}