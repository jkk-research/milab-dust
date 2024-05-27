package hu.sze.milab.dust.dev;

import java.util.ArrayList;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustHandles;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsConsts;

public interface DustDevNarrative extends DustHandles, DustUtilsConsts {

	public class DevCounter extends DustAgent {
		static DustCreator<DustDevCounter> CREATOR = new DustCreator<DustDevCounter>() {

			@Override
			public DustDevCounter create(Object key, Object... hints) {
				String head = Dust.access(MindAccess.Peek, "Counter", MIND_TAG_CONTEXT_SELF, TEXT_ATT_TOKEN);
				boolean sorted = DustDevUtils.chkTag(MIND_TAG_CONTEXT_SELF, MISC_TAG_SORTED);

				DustDevCounter ret = new DustDevCounter(head, sorted);
				return ret;
			}
		};

		@Override
		protected MindHandle agentBegin() throws Exception {
			DustDevCounter ret = DustDevUtils.getImplOb(CREATOR, null);
			ret.reset();
			return MIND_TAG_RESULT_READACCEPT;
		}

		@Override
		protected MindHandle agentProcess() throws Exception {
//			Object att = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_GEN_TARGET_ATT);
//			Object val = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, att);

			ArrayList<Object> path = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_REF_PATH);
			String val = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, path.toArray());

			val = DustUtils.getPostfix(val, "reports/");

			DustDevCounter cnt = DustDevUtils.getImplOb(CREATOR, null);

			cnt.add(val);
			Long tc = cnt.add(" << TOTAL >>");

			if ( 0 == (tc % 100) ) {
				Dust.log(EVENT_TAG_TYPE_INFO, tc);
			}

			return super.agentProcess();
		}

		@Override
		protected MindHandle agentEnd() throws Exception {
			Long limit = Dust.access(MindAccess.Peek, 1L, MIND_TAG_CONTEXT_SELF, MISC_ATT_GEN_COUNT);
			DustDevCounter cnt = DustDevUtils.getImplOb(CREATOR, null);

			for (Map.Entry<Object, Long> re : cnt) {
				if ( limit < re.getValue() ) {
					Dust.log(EVENT_TAG_TYPE_INFO, cnt.head, re.getKey(), re.getValue());
				}
			}

			return super.agentEnd();
		}
	}
}
