package hu.sze.milab.dust.stream.xml;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;

public class DustXmlDomAgent extends DustAgent implements DustXmlConsts {

	@Override
	protected MindHandle agentBegin() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;

	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		Object hStream = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_STREAM);
		
		Dust.access(MindAccess.Commit, MIND_TAG_ACTION_END, hStream);
		return MIND_TAG_RESULT_ACCEPT;

	}

	@Override
	protected MindHandle agentEnd() throws Exception {

		return MIND_TAG_RESULT_ACCEPT;
	}

}
