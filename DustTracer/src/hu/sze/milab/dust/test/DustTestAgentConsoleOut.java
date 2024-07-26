package hu.sze.milab.dust.test;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;

public class DustTestAgentConsoleOut extends DustAgent implements DustTestConsts {

	@Override
	public MindHandle agentProcess() throws Exception {
		String msg = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, TEXT_ATT_PLAIN_TEXT);
		
		if ( null == msg ) {
			msg = Dust.access(MindAccess.Peek, "???", MIND_TAG_CONTEXT_TARGET, TEXT_ATT_TOKEN);
		}

		Dust.log(EVENT_TAG_TYPE_TRACE, msg, MIND_TAG_ACTION_PROCESS);
		return MIND_TAG_RESULT_ACCEPT;
	}

}
