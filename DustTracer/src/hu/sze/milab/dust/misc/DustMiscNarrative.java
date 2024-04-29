package hu.sze.milab.dust.misc;

import java.util.ArrayList;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;

public interface DustMiscNarrative extends DustMiscConsts {

	public class TableAgent extends DustAgent implements DustMiscConsts {

		@Override
		protected MindHandle agentProcess() throws Exception {
			ArrayList<Object> headRow = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, MISC_ATT_CONN_MEMBERARR);
			Map<Object, Object> dataRow = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, MISC_ATT_CONN_MEMBERMAP);

			MindHandle hTarget = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET);
			boolean first = true;

			for (Object h : headRow) {
				Object val = dataRow.get(h);

				if ( first ) {
					Dust.access(MindAccess.Set, val, hTarget, MISC_ATT_VECTOR_COORDINATES, 1);
					first = false;
				} else {
					Dust.access(MindAccess.Set, h, hTarget, MISC_ATT_VECTOR_COORDINATES, 0);
					Dust.access(MindAccess.Set, val, hTarget, MISC_ATT_VARIANT_VALUE);
					Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hTarget);
				}
			}

			return MIND_TAG_RESULT_ACCEPT;
		}
	}
}