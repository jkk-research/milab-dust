package hu.sze.milab.dust.machine;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsConsts;

interface DustMachineConsts extends DustMetaConsts, DustUtilsConsts {

	enum MachineAtts {
		CreatorAccess, PrimaryAspectNames, PersistentAtt, CanContinue,
	}

	class DustHandle implements MindHandle {
		private final String id;
		
		public DustHandle(String id) {
			this.id = id;
		}
		
		@Override
		public String getId() {
			return id;
		}
		
		@Override
		public String toString() {
			return id;
		}
	}

	DustCreator<DustHandle> HANDLE_CREATOR = new DustCreator<DustHandle>() {
		@Override
		public DustHandle create(Object key, Object... hints) {
			return new DustHandle((String) key);
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	DustCreator<Map> KNOWLEDGE_CREATOR = new DustCreatorSimple<Map>(HashMap.class) {
		@Override
		public Map create(Object key, Object... hints) {
			Map m = super.create(key, hints);
			m.put(MIND_ATT_KNOWLEDGE_HANDLE, key);
			return m;
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	DustCreator<Map> UNIT_CREATOR = new DustCreatorSimple<Map>(HashMap.class) {
		@Override
		public Map create(Object key, Object... hints) {
			Map m = super.create(key, hints);
			
			String id = (0 == hints.length) ? (String) key : DustUtils.sbAppend(null, DUST_SEP_ID, true, hints[0], key).toString();

			DustHandle h = new DustHandle(id);

			Map mH = new HashMap();
			m.put(MIND_ATT_UNIT_HANDLES, mH);
			mH.put(null, h);

			Map mK = new HashMap();
			m.put(MIND_ATT_UNIT_KNOWLEDGE, mK);

			Map k = KNOWLEDGE_CREATOR.create(h);
			mK.put(h, k);

			k.put(MIND_ATT_KNOWLEDGE_HANDLE, h);
			k.put(MIND_ATT_KNOWLEDGE_PRIMARYASPECT, MIND_ASP_UNIT);

			return m;
		}
	};

	public interface IdResolver {
		DustHandle recall(String id);
//		DustHandle recall(DustHandle hUnit, String itemId);
	}

}
