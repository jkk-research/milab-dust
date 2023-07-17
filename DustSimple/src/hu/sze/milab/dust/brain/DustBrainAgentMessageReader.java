package hu.sze.milab.dust.brain;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;

public class DustBrainAgentMessageReader implements DustBrainConsts, DustConsts.MindAgent {

	public String prefix = "Reading message";

	@Override
	public MindStatus agentExecAction(MindAction action) throws Exception {
		switch ( action ) {
		case Begin:
			break;
		case End:
			break;
		case Init:
			break;
		case Process:
			break;
		case Release:
			break;
		}
		
		MindHandle hTarget = Dust.access(MindContext.Message, MindAccess.Peek, null);
		DustBrain.dumpHandle(prefix + " " + action, hTarget);
		return MindStatus.Accept;
	}

}
