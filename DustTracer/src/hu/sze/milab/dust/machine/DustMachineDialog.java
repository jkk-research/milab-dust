package hu.sze.milab.dust.machine;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.utils.DustUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
class DustMachineDialog implements DustMachineConsts, Dust.IdResolver {

	Map<MindHandle, Object> context = new HashMap<>();

	@Override
	public MindHandle recall(MindHandle hUnit, String itemId) {
		DustHandle hItem = null;

		Map knowledge = DustUtils.simpleGet(context, MIND_ATT_DIALOG_KNOWLEDGE);
		Map m = DustUtils.simpleGet(knowledge, hUnit, MIND_ATT_UNIT_HANDLES);

		if ( DustUtils.isEmpty(itemId) ) {
			itemId = "" + (m.size() + 1);
		} else {
			hItem = DustUtils.simpleGet(m, itemId);
		}

		if ( null == hItem ) {
			hItem = new DustHandle((DustHandle) hUnit, itemId);
			m.put(itemId, hItem);

			DustUtils.safeGet(knowledge, hItem, KNOWLEDGE_CREATOR);
		}

		return hItem;
	}

	@Override
	public MindHandle recall(String id) {
		String[] ss = (id + " ").split(DUST_SEP_ID);

		DustHandle hUnit = DustUtils.simpleGet(context, DUST_ATT_MACHINE_UNITS, ss[0].trim());

		return (ss.length == 1) ? hUnit : recall(hUnit, ss[1].trim());

	}
}