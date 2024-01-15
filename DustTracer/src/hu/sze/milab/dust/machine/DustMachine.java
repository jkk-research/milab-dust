package hu.sze.milab.dust.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsEnumTranslator;

@SuppressWarnings({ "rawtypes", "unchecked" })
class DustMachine extends Dust.Machine implements DustMachineConsts, DustConsts.MindAgent {

	Dust.IdResolver idRes;
	final DustMachineDialog mainDialog;

	public DustMachine() {
		mainDialog = new DustMachineDialog();
	}

	@Override
	public DustHandle recall(String id) {
		return (DustHandle) idRes.recall(id);
	}

	Map resolveKnowledge(MindHandle h, boolean createIfMissing) {
		MindHandle hu = h.getUnit();

		Map k = DustUtils.safeGet(mainDialog.knowledge, MIND_ATT_MEMORY_KNOWLEDGE, MAP_CREATOR);
		k = DustUtils.safeGet(k, hu, MAP_CREATOR);

		if ( hu != h ) {
			k = DustUtils.safeGet(k, MIND_ATT_MEMORY_KNOWLEDGE, MAP_CREATOR);
			k = DustUtils.safeGet(k, h, MAP_CREATOR);
		}

		return k;
	}

	@Override
	protected <RetType> RetType access(Object root, MindHandle cmd, Object val, Object... path) {
		Object ret = null;

		DustHandle hRoot;

		if ( null == root ) {
			hRoot = null;
			// resolve from current Dialog
		} else {
			hRoot = (DustHandle) root;
		}

		MindAccess ac = DustUtilsEnumTranslator.getEnum(cmd, MindAccess.Peek);

//		boolean createIfMissing = DustMachineUtils.isCreatorAccess(ac);
		boolean createIfMissing = DustUtilsAttCache.getAtt(MachineAtts.CreatorAccess, cmd, false);

		Object curr = hRoot;
		Object prev = null;
		Object lastKey = null;

		for (Object p : path) {
			if ( curr instanceof DustHandle ) {
				curr = resolveKnowledge((DustHandle) curr, createIfMissing);
			} else if ( null == curr ) {
				if ( createIfMissing ) {
					curr = (p instanceof Integer) ? new ArrayList() : new HashMap();

					if ( prev instanceof ArrayList ) {
						if ( KEY_ADD == (Integer) lastKey ) {
							((ArrayList) prev).add(curr);
						} else {
							((ArrayList) prev).set((Integer) lastKey, curr);
						}
					} else if ( prev instanceof Map ) {
						((Map) prev).put(lastKey, curr);
					}
				} else {
					break;
				}
			}
			
			prev = curr;
			lastKey = p;

			if ( curr instanceof ArrayList ) {
				ArrayList al = (ArrayList) curr;
				Integer idx = (Integer) p;

				if ( (KEY_ADD == idx) || (idx >= al.size()) ) {
					curr = null;
				} else {
					curr = al.get(idx);
				}
			} else if ( curr instanceof Map ) {
				curr = ((Map) curr).get(p);
			} else {
				curr = null;
			}
		}
		
		switch ( ac ) {
		case Check:
			break;
		case Commit:
			break;
		case Delete:
			break;
		case Get:
			ret = curr;
			break;
		case Insert:
			break;
		case Peek:
			break;
		case Reset:
			break;
		case Set:
			if ( null != lastKey ) {
				if ( prev instanceof Map ) {
					((Map) prev).put(lastKey, val);
				} else if ( prev instanceof ArrayList ) {
					if ( KEY_ADD == (Integer) lastKey ) {
						((ArrayList) prev).add(val);
					} else {
						((ArrayList) prev).set((Integer) lastKey, val);
					}
				}
			}

			break;		
		}

		return (RetType) ret;
	}

	@Override
	protected void log(MindHandle event, Object... params) {
		StringBuilder sb = DustUtils.sbAppend(null, ", ", false, params);

		if ( null != sb ) {
			System.out.println(sb);
		}
	}

	@Override
	public MindHandle agentInit() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MindHandle agentBegin() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MindHandle agentProcess() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MindHandle agentRelease() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
