package hu.sze.milab.dust.machine;

import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.mvel.DustMvelUtils;
import hu.sze.milab.dust.stream.DustStreamConsts;

interface DustMachineNarrative extends DustMachineConsts {

	public static class PopulateAgent extends DustAgent implements DustStreamConsts {
		@Override
		protected MindHandle agentProcess() throws Exception {
			Object hTo = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET);

			Object hFrom = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);
			Object rootAtt = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MIND_ATT_POPULATE_ROOTATT);
			Object root = (null == rootAtt) ? hTo : Dust.access(MindAccess.Peek, null, hFrom, rootAtt);

			Map<Object, Object> transfer = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_MEMBERMAP);

			for (Map.Entry<Object, Object> et : transfer.entrySet()) {
				Object key = et.getKey();
				Object val = et.getValue();

				if ( val instanceof String ) {
					String expr = (String) val;

					if ( expr.startsWith("!") ) {
						val = DustMvelUtils.eval(expr.substring(1), root);
					} else {
						val = Dust.access(MindAccess.Peek, null, root, expr);
					}
				} else if ( val instanceof MindHandle ) {
					val = Dust.access(MindAccess.Peek, null, hFrom, val);
				}
				
//				Dust.log(EVENT_TAG_TYPE_TRACE, "PopulateAgent set", key, val);
				Dust.access(MindAccess.Set, val, hTo, key);
			}
			
			Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hTo);

			return MIND_TAG_RESULT_READACCEPT;
		}
	}
}