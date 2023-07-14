package hu.sze.milab.dust.dev;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.brain.DustBrain;

public class DustDevAgentDump implements DustDevConsts, DustConsts.MindAgent {
	
//	public MindHandle hTarget;
	public String prefix = "Dump";

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
