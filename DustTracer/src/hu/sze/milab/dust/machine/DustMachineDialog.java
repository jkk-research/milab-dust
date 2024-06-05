package hu.sze.milab.dust.machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsEnumTranslator;

@SuppressWarnings({ "unchecked", "rawtypes" })
class DustMachineDialog implements DustMachineConsts {

	DustMachine machine;
	DustCreator<Map> memexLoader = new DustCreator<Map>() {
		@Override
		public Map create(Object key, Object... hints) {
			return machine.getMemexKnowledge((DustHandle) key, true);
		}
	};
	
	Map<MindHandle, Object> context = new HashMap<>();
	
	DustMachineVisitContext visitCtx = null;
	
	public DustMachineDialog(DustMachine machine) {
		this.machine = machine;
		context.put(MIND_ATT_UNIT_CONTENT, new HashMap());
	}
	

	Map resolveKnowledge(MindHandle h, boolean createIfMissing) {
		Map knowledge = DustUtils.simpleGet(context, MIND_ATT_UNIT_CONTENT);
		return DustUtils.safeGet(knowledge, h, createIfMissing ? memexLoader : null);
	}

	protected <RetType> RetType access(MindAccess cmd, Object val, Object root, Object... path) {
		Object ret = null;
		
		boolean createIfMissing = DustUtilsAttCache.getAtt(MachineAtts.CreatorAccess, cmd, false);
		
		Object curr;

		if ( null == root ) {
			curr = context;
		} else {
			MindHandle activeAgent = (MindHandle) context.get(MIND_ATT_DIALOG_ACTIVEAGENT);

			if ( null == activeAgent ) {
				curr = (root instanceof MindContext) ? DustUtils.safeGet(context, root, MAP_CREATOR): resolveKnowledge((MindHandle) root, createIfMissing);
			} else {
				MindContext ctx = (root instanceof MindContext) ? (MindContext) root : DustUtilsEnumTranslator.getEnum((MindHandle) root, MindContext.Direct);

				switch ( ctx ) {
				case Action:
					curr = context.get(MIND_TAG_CONTEXT_ACTION);
					break;
				case Dialog:
					curr = context;
					break;
				case Self:
					curr = activeAgent;
//					curr = resolveKnowledge(activeAgent, true);
					break;
				case Target:
					curr = resolveKnowledge(activeAgent, true);
					curr = DustUtils.safeGet(curr, MIND_ATT_AGENT_TARGET, null);
					break;
				default:
					curr = resolveKnowledge((MindHandle) root, true);
					break;
				}
			}
		}
		
		DustHandle hLastItem = (DustHandle) ((curr instanceof Map) ? ((Map)curr).get(MIND_ATT_KNOWLEDGE_HANDLE) : (curr instanceof DustHandle) ? curr : (root instanceof DustHandle) ? root : null);

		Object prev = null;
		Object lastKey = null;

		Map prevMap = null;
		ArrayList prevArr = null;
		Set prevSet = null;

		for (Object p : path) {
			if ( curr instanceof Enum ) {
				MindHandle h = DustUtilsEnumTranslator.getHandle(curr);
				curr = (null == h) ? ((Enum)curr).name() : h;
			}
			
			if ( curr instanceof DustHandle ) {
				hLastItem = (DustHandle) curr;
				curr = resolveKnowledge(hLastItem, createIfMissing);
			} else if ( null == curr ) {
				if ( createIfMissing ) {
					curr = (p instanceof Integer) ? new ArrayList() : new HashMap();

					if ( null != prevArr ) {
						DustUtils.safePut(prevArr, (Integer) lastKey, val, false);
					} else if ( null != prevMap ) {
						prevMap.put(lastKey, curr);
					} else if ( null != prevSet ) {
						prevSet.add(curr);
					}
				} else {
					break;
				}
			}

			prev = curr;
			prevArr = (prev instanceof ArrayList) ? (ArrayList) prev : null;
			prevMap = (prev instanceof Map) ? (Map) prev : null;
			prevSet = (prev instanceof Set) ? (Set) prev : null;

			lastKey = p;

			if ( curr instanceof ArrayList ) {
				ArrayList al = (ArrayList) curr;
				Integer idx = (Integer) p;

				if ( (KEY_SIZE == idx) ) {
					curr = al.size();
				} else if ( (KEY_ADD == idx) || (idx >= al.size()) ) {
					curr = null;
				} else {
					curr = al.get(idx);
				}
			} else if ( curr instanceof Map ) {
				curr = DustUtils.isEqual(KEY_SIZE, p) ? ((Map) curr).size() :((Map) curr).get(p);
			} else {
				curr = null;
			}
		}

		switch ( cmd ) {
		case Check:
			ret = DustUtils.isEqual(val, curr);
			break;
		case Commit:
			if ( curr instanceof DustHandle ) {
				curr = resolveKnowledge((DustHandle) curr, createIfMissing);
			}
			if ( curr instanceof Map ) {
				ArrayList listeners = DustUtils.safeGet(curr, MIND_ATT_KNOWLEDGE_LISTENERS, null);
				if ( null != listeners ) {
					Object hTarget = DustUtils.safeGet(curr, MIND_ATT_KNOWLEDGE_HANDLE, null);
					Object oldAgent = Dust.access(MindAccess.Peek, null, null, MIND_ATT_DIALOG_ACTIVEAGENT);
					Dust.access(MindAccess.Set, null, null, MIND_ATT_DIALOG_ACTIVEAGENT);
					MindAction action = (val instanceof MindAction) ? (MindAction)val : DustUtilsEnumTranslator.getEnum((MindHandle) val, MindAction.Process);

					boolean stop = false;
					for (Object a : listeners) {
						try {
							if ( a instanceof MindCommitFilter ) {
								a = ((MindCommitFilter)a).getAgent(val);
								if ( null == a ) {
									continue;
								}
							}
							
							if ( a == oldAgent ) {
								// the committer may listen to the committed handle but should not be called now
								continue;
							}

							MindAgent agent = machine.selectAgent(a);
							if ( null != agent ) {
								Dust.access(MindAccess.Set, hTarget, a, MIND_ATT_AGENT_TARGET);
								Dust.access(MindAccess.Set, a, null, MIND_ATT_DIALOG_ACTIVEAGENT);
								Dust.access(MindAccess.Set, val, null, MIND_TAG_CONTEXT_ACTION);
								MindHandle result = agent.agentProcess(action);
								stop = ((MIND_TAG_RESULT_REJECT == result) || (MIND_TAG_RESULT_READ == result));
							}
						} catch (Throwable e) {
							Dust.access(MindAccess.Set, null, a, MIND_ATT_AGENT_TARGET);
							DustException.swallow(e);
						}
						
						if ( stop ) {
							break;
						}
					}
					
					Dust.access(MindAccess.Set, oldAgent, null, MIND_ATT_DIALOG_ACTIVEAGENT);
				}
			}

			break;
		case Delete:
			break;
		case Get:
			ret = (null == curr) ? val : curr;
			break;
		case Insert:
			if ( curr instanceof Set ) {
				prevSet = (Set) curr;
			}
			if ( !DustUtils.isEqual(curr, val) ) {
				if ( null != prevArr ) {
					DustUtils.safePut(prevArr, (Integer) lastKey, val, false);
				} else if ( null != prevSet ) {
					prevSet.add(val);
				} else {
					Set s = new HashSet();
					s.add(val);
					prevMap.put(lastKey, s);
				}
			}
			break;
		case Peek:
			ret = (null == curr) ? val : curr;
			break;
		case Reset:
			if ( curr instanceof Map ) {
				((Map)curr).clear();
			} else if ( curr instanceof Collection ) {
				((Collection)curr).clear();
			}
			break;
		case Set:
			if ( null != lastKey ) {
				if ( null != prevMap ) {
					if ( !DustUtils.isEqual(curr, val) ) {
						prevMap.put(lastKey, val);
					}
				} else if ( null != prevArr ) {
					DustUtils.safePut(prevArr, (Integer) lastKey, val, true);
				}
			}

			break;
		case Broadcast:
			break;
		case Lookup:
			break;
		case Visit:
			
			if ( null == hLastItem ) {
				break;
			}
			
			boolean visitRoot = (null == visitCtx);
			
			try {
				DustVisitor visitor = (DustVisitor) val;
				Object collection;
				MindHandle hAtt = null;
				if ( curr instanceof DustHandle ) {
					hLastItem = (DustHandle) curr;
					collection = resolveKnowledge(hLastItem, false);
				} else {
					 hAtt = (MindHandle) lastKey;
					 collection = curr;
				}
				
				if ( visitRoot ) {
					visitCtx = new DustMachineVisitContext(this);
				}
				visitCtx.visit(visitor, hLastItem, collection, hAtt);
			} finally {
				if ( visitRoot ) {
					visitCtx = null;
				}
			}
			
			break;
		}

		return (RetType) ret;
	}

}