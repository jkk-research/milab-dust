package hu.sze.milab.dust;

public abstract class DustAgent implements DustConsts.MindAgent, DustMetaConsts {

	@Override
	public MindHandle agentBegin() throws Exception {
		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}

}