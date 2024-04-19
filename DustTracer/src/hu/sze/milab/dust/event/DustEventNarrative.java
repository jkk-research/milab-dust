package hu.sze.milab.dust.event;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;

public interface DustEventNarrative extends DustEventConsts {

	public static class Throttle extends DustAgent implements DustEventConsts {
		@Override
		protected MindHandle agentProcess() throws Exception {
			long last = Dust.access(MindAccess.Peek, 0L, MIND_TAG_CONTEXT_SELF, EVENT_ATT_LAST_ACTION);
			long dist = Dust.access(MindAccess.Peek, 0L, MIND_TAG_CONTEXT_SELF, EVENT_ATT_TIME_MILLI);

			long ts = System.currentTimeMillis();
			long w = (last + dist) - ts;
			
			if ( 0 < w ) {
				MindHandle hSelf = Dust.access(MindAccess.Peek, 0L, MIND_TAG_CONTEXT_SELF);
				Dust.log(EVENT_TAG_TYPE_TRACE, "Throttle wait", w, "msec");
				
				synchronized (hSelf) {
					hSelf.wait(w);
				}
			}
			
			Dust.access(MindAccess.Set, System.currentTimeMillis(), MIND_TAG_CONTEXT_SELF, EVENT_ATT_LAST_ACTION);
			
			return MIND_TAG_RESULT_ACCEPT;
		}
	}
}