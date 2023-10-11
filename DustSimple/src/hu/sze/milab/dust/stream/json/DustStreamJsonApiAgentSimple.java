package hu.sze.milab.dust.stream.json;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;

@SuppressWarnings("rawtypes")
public class DustStreamJsonApiAgentSimple implements DustStreamJsonConsts, DustConsts.MindAgent {

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
			MindHandle hMsg = Dust.access(MindContext.Message, MindAccess.Peek, null);

			break;
		case Release:
			break;
		}

		return MindStatus.Accept;
	}




}
