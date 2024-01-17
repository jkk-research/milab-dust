package hu.sze.milab.dust.machine;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.utils.DustUtils;

//@SuppressWarnings({ "rawtypes", "unchecked" })
class DustMachineDialog implements DustMachineConsts, Dust.IdResolver {

	Map<MindHandle, Object> knowledge = new HashMap<>();

	protected <RetType> RetType access(Object root, MindHandle cmd, Object val, Object... path) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public MindHandle recall(String id) {
		String[] ss = id.split(DUST_SEP_ID);
		
		DustHandle hUnit = DustUtils.simpleGet(knowledge, MIND_ATT_ASSEMBLY_UNITS, ss[0]);
		
		if ( ss.length == 1 ) {
			return hUnit;
		}
		
		Map m = DustUtils.simpleGet(knowledge, MIND_ATT_MEMORY_KNOWLEDGE, hUnit, MIND_ATT_UNIT_HANDLES);
		DustHandle hItem = DustUtils.simpleGet(m, ss[1]);
		
		if ( null == hItem ) {
			hItem = new DustHandle(hUnit, ss[1]);
			m.put(ss[1], hItem);
			
			m = DustUtils.simpleGet(knowledge, MIND_ATT_MEMORY_KNOWLEDGE, hUnit, MIND_ATT_MEMORY_KNOWLEDGE);
			DustUtils.safeGet(m, hItem, MAP_CREATOR);
		}
		
		return hItem;
	}

}
