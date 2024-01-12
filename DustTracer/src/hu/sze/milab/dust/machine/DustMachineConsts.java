package hu.sze.milab.dust.machine;

import java.util.Map;

import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.utils.DustUtils;

interface DustMachineConsts extends DustMetaConsts {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	class DustHandle implements MindHandle {
		private static Map<MindHandle, MindHandle> TOSRT_TOKENMAP;
		private static Map<MindHandle, Map> TOSRT_TOKENS;

		public static void setTranslator(Map<MindHandle, MindHandle> tokenmap, Map<MindHandle, Map> tokenTexts) {
			TOSRT_TOKENMAP = tokenmap;
			TOSRT_TOKENS = tokenTexts;
		}

		private final DustHandle unit;
		private final String id;

		private String toStr;

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
			return (this == unit) ? id : unit.id + DUST_SEP_ID + id;
		}

		@Override
		public String toString() {
			if ( (null != TOSRT_TOKENMAP) && (null == toStr) ) {
				toStr = getId();
				MindHandle hTxt = TOSRT_TOKENMAP.get(this);
				if ( null != hTxt ) {
					Map m = TOSRT_TOKENS.get(hTxt);
					String str = (String) m.getOrDefault(TEXT_ATT_PLAIN_TEXT, "???");
					toStr = DustUtils.sbAppend(null, "", false, str, " (", toStr, ")").toString();
				}
			}

			return toStr;
		}
	}

}
