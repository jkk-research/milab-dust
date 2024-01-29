package hu.sze.milab.dust;

public class Dust implements DustConsts {

	private static Machine MACHINE;

	public interface IdResolver {
		MindHandle recall(String id);

		MindHandle recall(MindHandle hUnit, String itemId);
	}

	public static abstract class Machine implements IdResolver {

		protected Machine() {
			if ( null != MACHINE ) {
				DustException.wrap(null, "Multiple Dust machine initialization");
			}

			MACHINE = this;
		}

		protected abstract <RetType> RetType access(Object root, MindHandle cmd, Object val, Object... path);

		protected abstract void log(MindHandle event, Object... params);
	}

	public static MindHandle recall(String id) {
		return MACHINE.recall(id);
	}

	public static MindHandle recall(MindHandle hUnit, String itemId) {
		return MACHINE.recall(hUnit, itemId);
	}

	public static <RetType> RetType access(Object root, MindHandle cmd, Object val, Object... path) {
		return MACHINE.access(root, cmd, val, path);
	}

	public static void log(MindHandle event, Object... params) {
		MACHINE.log(event, params);
	}

}
