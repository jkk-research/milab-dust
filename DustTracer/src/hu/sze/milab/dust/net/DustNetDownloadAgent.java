package hu.sze.milab.dust.net;

import hu.sze.milab.dust.DustAgent;

public class DustNetDownloadAgent extends DustAgent implements DustNetConsts {

	@Override
	protected MindHandle agentProcess() throws Exception {

		return MIND_TAG_RESULT_READACCEPT;
	}

}
