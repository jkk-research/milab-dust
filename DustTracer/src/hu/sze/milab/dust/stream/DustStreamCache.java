package hu.sze.milab.dust.stream;

import hu.sze.milab.dust.DustAgent;

public class DustStreamCache extends DustAgent implements DustStreamConsts {

	@Override
	protected MindHandle agentProcess() throws Exception {

		return MIND_TAG_RESULT_READACCEPT;
	}

}
