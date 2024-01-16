package hu.sze.milab.dust.machine;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsConsts;

interface DustMachineConsts extends DustMetaConsts, DustUtilsConsts {

	enum MachineAtts {
		CreatorAccess, PrimaryAspectNames, PersistentAtt
	}

	enum MindAccess {
		Check, Peek, Get, Set, Insert, Delete, Reset, Commit,
	};

	class DustHandle implements MindHandle {
		private final DustHandle unit;
		private final String id;

		private String toStr;

		public DustHandle(String id) {
			unit = this;
			this.id = id;
		}

		public DustHandle(DustHandle unit, String id) {
			this.unit = unit;
			this.id = id;
		}

		@Override
		public DustHandle getUnit() {
			return unit;
		}

		@Override
		public String getId() {
			return (this == unit) ? id : unit.id + DUST_SEP_ID + id;
		}

		@Override
		public String toString() {
			if ( null == toStr ) {
				toStr = getId();
				String str = Dust.access(this, MIND_TAG_ACCESS_PEEK, null, DEV_ATT_HINT);
				if ( !DustUtils.isEmpty(str) ) {
					toStr = DustUtils.sbAppend(null, "", false, toStr, " (", str, ")").toString();
				}
			}

			return toStr;
		}
	}

}
