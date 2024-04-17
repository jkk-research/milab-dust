package hu.sze.milab.dust.stream.xml;

import hu.sze.milab.dust.DustAgent;

public class DustXmlDomAgent extends DustAgent implements DustXmlConsts {

	@Override
	protected MindHandle agentBegin() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;

	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;

	}

	@Override
	protected MindHandle agentEnd() throws Exception {

		return MIND_TAG_RESULT_ACCEPT;
	}

}
