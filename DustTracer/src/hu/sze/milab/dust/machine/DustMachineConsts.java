package hu.sze.milab.dust.machine;

import java.util.Set;
import java.util.TreeSet;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.utils.DustUtilsConsts;

interface DustMachineConsts extends DustMetaConsts, DustUtilsConsts {

	enum MachineAtts {
		CreatorAccess, PrimaryAspectNames, PersistentAtt, CanContinue,
	}

	class DustHandle implements MindHandle {
		private final String id;

		private String str;

		private static final Set<String> IDS = new TreeSet<>();

		public DustHandle(String id) {
			this.id = id;
			setHint(id);

			if (!IDS.add(id)) {
				Dust.log(null, "Creating handle", this);
			}
		}

		public void setHint(String hint) {
			this.str = hint;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return str;
		}
	}

	public interface IdResolver {
		DustHandle recall(String id);
	}

}
