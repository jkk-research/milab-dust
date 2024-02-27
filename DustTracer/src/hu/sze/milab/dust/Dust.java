package hu.sze.milab.dust;

public class Dust implements DustConsts {

	private static Machine MACHINE;

	public static abstract class Machine {
		protected Machine() {
			if ( null != MACHINE ) {
				DustException.wrap(null, "Multiple Dust machine initialization");
			}

			MACHINE = this;
		}

		protected abstract <RetType> RetType access(MindAccess cmd, Object val, Object root, Object... path);
	}

	public static <RetType> RetType access(MindAccess cmd, Object val, Object root, Object... path) {
		return MACHINE.access(cmd, val, root, path);
	}

	public static MindHandle lookup(String id) {
		return MACHINE.access(MindAccess.Lookup, id, null);
	}

//	public static MindHandle lookup(MindHandle hUnit, String itemId) {
//		return MACHINE.access(MindAccess.Lookup, itemId, hUnit);
//	}

	public static void log(MindHandle event, Object... params) {
		MACHINE.access(MindAccess.Broadcast, event, null, params);
	}

}
