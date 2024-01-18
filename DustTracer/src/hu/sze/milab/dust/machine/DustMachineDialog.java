package hu.sze.milab.dust.machine;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.utils.DustUtils;

class DustMachineDialog implements DustMachineConsts, Dust.IdResolver {

	Map<MindHandle, Object> context = new HashMap<>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public MindHandle recall(String id) {
		String[] ss = (id + " ").split(DUST_SEP_ID);
		
		DustHandle hUnit = DustUtils.simpleGet(context, DUST_ATT_MACHINE_UNITS, ss[0].trim());
		
		if ( ss.length == 1 ) {
			return hUnit;
		}
		
		DustHandle hItem = null;
		
		Map knowledge = DustUtils.simpleGet(context, MIND_ATT_DIALOG_KNOWLEDGE);
		Map m = DustUtils.simpleGet(knowledge, hUnit, MIND_ATT_UNIT_HANDLES);

		String itemId = ss[1].trim();
		if ( DustUtils.isEmpty(itemId) ) {
			itemId = "" + (m.size() + 1);
		} else {
			hItem = DustUtils.simpleGet(m, itemId);
		}
		
		if ( null == hItem ) {
			hItem = new DustHandle(hUnit, itemId);
			m.put(itemId, hItem);
			
			DustUtils.safeGet(knowledge, hItem, KNOWLEDGE_CREATOR);
		}
		
		return hItem;
	}
}