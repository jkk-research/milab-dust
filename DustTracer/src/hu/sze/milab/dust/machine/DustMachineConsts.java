package hu.sze.milab.dust.machine;

import hu.sze.milab.dust.DustMetaConsts;

interface DustMachineConsts extends DustMetaConsts {

	public class DustHandle implements MindHandle {
		private final DustHandle unit;
		private final String id;

		public DustHandle() {
			unit = this;
			id = "0";
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
			return id;
		}
	}

}
