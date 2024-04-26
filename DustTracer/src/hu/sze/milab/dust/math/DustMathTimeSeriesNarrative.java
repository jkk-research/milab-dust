package hu.sze.milab.dust.math;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;

public class DustMathTimeSeriesNarrative extends DustAgent implements DustMathConsts {
	
	@Override
	protected MindHandle agentBegin() throws Exception {
		DustDevUtils.setTag(MIND_TAG_CONTEXT_SELF, MISC_TAG_LOADING);
		return MIND_TAG_RESULT_ACCEPT;
	}
	
	@Override
	protected MindHandle agentProcess() throws Exception {
		boolean loading = DustDevUtils.chkTag(MIND_TAG_CONTEXT_SELF, MISC_TAG_LOADING);
		
		
		if ( loading ) {
			
		} else {
			
		}
		long last = Dust.access(MindAccess.Peek, 0L, MIND_TAG_CONTEXT_SELF, EVENT_ATT_LAST_ACTION);
		long dist = Dust.access(MindAccess.Peek, 0L, MIND_TAG_CONTEXT_SELF, EVENT_ATT_TIME_MILLI);

		long ts = System.currentTimeMillis();
		long w = (last + dist) - ts;

		if ( 0 < w ) {
			Object self = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF);
			Dust.log(EVENT_TAG_TYPE_TRACE, "Throttle wait", w, "msec");

			synchronized (self) {
				self.wait(w);
			}
		}

		Dust.access(MindAccess.Set, System.currentTimeMillis(), MIND_TAG_CONTEXT_SELF, EVENT_ATT_LAST_ACTION);

		return MIND_TAG_RESULT_ACCEPT;
	}
}