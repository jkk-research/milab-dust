package hu.sze.milab.dust.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustConsts.MindAgent;
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
		Map knowledge = DustUtils.safeGet(mainDialog.context, MIND_ATT_DIALOG_KNOWLEDGE, MAP_CREATOR);
		return  DustUtils.safeGet(knowledge, h, createIfMissing ? KNOWLEDGE_CREATOR : null);
	}

	@Override
	protected <RetType> RetType access(Object root, MindHandle cmd, Object val, Object... path) {
		Object ret = null;

		MindAccess ac = DustUtilsEnumTranslator.getEnum(cmd, MindAccess.Peek);
		boolean createIfMissing = DustUtilsAttCache.getAtt(MachineAtts.CreatorAccess, cmd, false);

		Object curr = (null == root) ? mainDialog.context : resolveKnowledge((MindHandle) root, createIfMissing);
		Object prev = null;
		Object lastKey = null;

		Map prevMap = null;
		ArrayList prevArr = null;
		for (Object p : path) {
			if ( curr instanceof DustHandle ) {
				curr = resolveKnowledge((DustHandle) curr, createIfMissing);
			} else if ( null == curr ) {
				if ( createIfMissing ) {
					curr = (p instanceof Integer) ? new ArrayList() : new HashMap();

					if ( null != prevArr ) {
						if ( KEY_ADD == (Integer) lastKey ) {
							prevArr.add(curr);
						} else {
							prevArr.set((Integer) lastKey, curr);
						}
					} else if ( null != prevMap ) {
						prevMap.put(lastKey, curr);
					}
				} else {
					break;
				}
			}

			prev = curr;
			prevArr = (prev instanceof ArrayList) ? (ArrayList) prev : null;
			prevMap = (prev instanceof Map) ? (Map) prev : null;

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
			ret = curr;
			break;
		case Reset:
			break;
		case Set:
			if ( null != lastKey ) {
				if ( null != prevMap ) {
					if ( !DustUtils.isEqual(curr, val) ) {
						prevMap.put(lastKey, val);
					}
				} else if ( null != prevArr ) {
					if ( KEY_ADD == (Integer) lastKey ) {
						prevArr.add(val);
					} else {
						prevArr.set((Integer) lastKey, val);
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
		
		ArrayList allNatLog = new ArrayList();
		ArrayList mods = Dust.access(APP_MACHINE_MAIN, MIND_TAG_ACCESS_PEEK, APP_MODULE_MAIN, DUST_ATT_MACHINE_MODULES);
		for ( Object m : mods ) {
			ArrayList nls = Dust.access(m, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_MODULE_NATIVELOGICS);
			if ( null != nls ) {
				for ( Object nl : nls ) {
					allNatLog.add(nl);
				}
			}
		}
		
		ArrayList sa = Dust.access(APP_ASSEMBLY_MAIN, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_ASSEMBLY_STARTAGENTS);
		if ( null != sa ) {
			for ( Object a : sa ) {
				Object l = Dust.access(a, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_AGENT_LOGIC);
				Object n = null;
				for ( Object nl : allNatLog ) {
					if ( l == Dust.access(nl, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_NATIVELOGIC_LOGIC)) {
						n = nl;
						break;
					}
				}
				if ( null != n ) {
					MindAgent agent = Dust.access(n, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_NATIVELOGIC_INSTANCE);
					
					if ( null == agent ) {
						String ac = Dust.access(n, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_NATIVELOGIC_IMPLEMENTATION);
						agent = (MindAgent) Class.forName(ac).newInstance();
						agent.agentInit();
						Dust.access(n, MIND_TAG_ACCESS_SET, agent, DUST_ATT_NATIVELOGIC_INSTANCE);
					}
					
					if ( null != agent ) {
						agent.agentProcess();
					}
				}
			}
		}		return null;
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
