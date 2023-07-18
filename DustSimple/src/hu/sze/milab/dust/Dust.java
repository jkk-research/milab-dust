package hu.sze.milab.dust;

import hu.sze.milab.dust.utils.DustUtils;

public class Dust implements DustConsts {

	public interface Brain {
		MindHandle resolveID(String id, MindHandle primaryType);

		<RetType> RetType access(Object root, MindAccess cmd, Object val, Object... path);
	}

	private static Brain BRAIN;

	public static <RetType> RetType access(Object root, MindAccess cmd, Object val, Object... path) {
		return BRAIN.access(root, cmd, val, path);
	}

	public static MindHandle resolveID(String id, MindHandle primaryType) {
		return BRAIN.resolveID(id, primaryType);
	}

	public static void dump(Object sep, boolean strict, Object... objects) {
		StringBuilder sb = DustUtils.sbAppend(null, sep, strict, objects);

		if ( null != sb ) {
			System.out.println(sb);
		}
	}

	public static void dumpObs(Object... obs) {
		log(null, obs);
	}

	public static void log(Object event, Object... params) {
		dump(", ", false, params);
	}

	public static void main(String[] args) throws Exception {
		if ( null != BRAIN ) {
			DustException.wrap(null, "multiple Dust initialization");
		}
		
		BRAIN = (Brain) Class.forName("hu.sze.milab.dust.brain.DustBrain").newInstance();
		((MindAgent) BRAIN).agentExecAction(MindAction.Init);
		for ( String s : args ) {
			access(MindContext.Dialog, MindAccess.Set, s, DustMetaConsts.MIND_ATT_DIALOG_LAUNCHPARAMS, KEY_ADD);
		}
		
		((MindAgent) BRAIN).agentExecAction(MindAction.Begin);
	}
}
