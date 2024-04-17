package hu.sze.milab.dust.machine;

import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.stream.DustStreamConsts;

interface DustMachineNarrative extends DustMachineConsts {
	public class PopulateAgent extends DustAgent implements DustStreamConsts {

		@Override
		protected MindHandle agentProcess() throws Exception {
			Object hSource = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);
			Object hTarget = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET);

			Map<Object, Object> transfer = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_MEMBERMAP);

			for (Map.Entry<Object, Object> et : transfer.entrySet()) {
				Object calc = et.getKey();
				
				Object v = Dust.access(MindAccess.Peek, null, hSource, calc);
				Dust.access(MindAccess.Set, v, hTarget, et.getKey());
			}

			return MIND_TAG_RESULT_READACCEPT;
		}
	}
}