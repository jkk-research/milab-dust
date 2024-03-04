package hu.sze.milab.dust.machine;

import java.util.Set;
import java.util.TreeSet;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustHandles;
import hu.sze.milab.dust.utils.DustUtilsConsts;

interface DustMachineConsts extends DustHandles, DustUtilsConsts {

	enum MachineAtts {
		CreatorAccess, PrimaryAspectNames, PersistentAtt, CanContinue,
	}

	class DustHandle implements MindHandle {
		private final String id;

		private static final Set<String> IDS = new TreeSet<>();

		public DustHandle(String id) {
			this.id = id;

			if (!IDS.add(id)) {
				Dust.log(null, "Creating handle", this);
			}
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			String str = Dust.access(MindAccess.Peek, id, this, DEV_ATT_HINT);
			
			return str;
		}
	}

	public interface IdResolver {
		DustHandle recall(String id);
	}

}
