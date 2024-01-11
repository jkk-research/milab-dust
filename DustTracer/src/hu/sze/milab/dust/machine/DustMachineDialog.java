package hu.sze.milab.dust.machine;

import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;

//@SuppressWarnings({ "rawtypes", "unchecked" })
class DustMachineDialog implements DustMachineConsts, Dust.IdResolver {

	Map<MindHandle, Object> knowledge = new HashMap<>();

	protected <RetType> RetType access(Object root, MindHandle cmd, Object val, Object... path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MindHandle recall(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
