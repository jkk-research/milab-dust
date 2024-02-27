package hu.sze.milab.dust.machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;

@SuppressWarnings({ "rawtypes", "unchecked" })
class DustMachine extends Dust.Machine implements DustMachineConsts, DustConsts.MindAgent, DustMachineConsts.IdResolver {

	IdResolver idRes;
	final Map memex = new HashMap();
	final ThreadLocal<DustMachineDialog> dialogs = new ThreadLocal<DustMachineDialog>() {
		@Override
		protected DustMachineDialog initialValue() {
			return new DustMachineDialog(DustMachine.this);
		}
	};
	
	private final Thread shutdownHook = new Thread() {
		@Override
		public synchronized void run() {
			Dust.log(EVENT_TAG_TYPE_TRACE, "Releasing Machine...");
			try {
				agentRelease();
				Dust.log(EVENT_TAG_TYPE_TRACE, "Machine released.");
			} catch (Exception e) {
				DustException.swallow(e, "Uncaught exception during Machine release.");
			}
		}
	};

//	public DustMachine() {
//		mainDialog = new DustMachineDialog();
//	}
	
//	@Override
//	public DustHandle recall(DustHandle hUnit, String itemId) {
//		DustHandle hItem = null;
//
//		Map knowledge = null;// DustUtils.simpleGet(context, MIND_ATT_DIALOG_KNOWLEDGE);
//		Map m = DustUtils.simpleGet(knowledge, hUnit, MIND_ATT_UNIT_HANDLES);
//
//		if ( DustUtils.isEmpty(itemId) ) {
//			itemId = "" + (m.size() + 1);
//		} else {
//			hItem = DustUtils.simpleGet(m, itemId);
//		}
//
//		if ( null == hItem ) {
//			hItem = new DustHandle((DustHandle) hUnit, itemId);
//			m.put(itemId, hItem);
//
//			DustUtils.safeGet(knowledge, hItem, KNOWLEDGE_CREATOR);
//		}
//
//		return hItem;
//	}

	Map getUnit(String authorID, String unitID) {
		Map m = DustUtils.safeGet(memex, authorID, UNIT_CREATOR);
		m = DustUtils.safeGet(m, unitID, UNIT_CREATOR, authorID);
		return m;
	}

	@Override
	public DustHandle recall(String id) {
		String[] ii = id.split(DUST_SEP_ID);
		DustHandle h;
		
		if ( ii.length == 3 ) {
			Map m = getUnit(ii[0], ii[1]);
			m = DustUtils.simpleGet(m, MIND_ATT_UNIT_HANDLES);
//			h = (DustHandle) m.get(null);		
//			h = DustUtils.safeGet(m, ii[0], HANDLE_CREATOR, h);
			h = DustUtils.safeGet(m, id, HANDLE_CREATOR);
		} else {
			Map m = getUnit(ii[0], ii[1]);
			h = DustUtils.simpleGet(m, MIND_ATT_UNIT_HANDLES, null);
			DustUtils.safeGet(m, ii[1], HANDLE_CREATOR);
		}

		return h;
	}

	Map getMemexKnowledge(DustHandle h, boolean createIfMissing) {
		String[] ii = h.getId().split(DUST_SEP_ID);
		Map m = getUnit(ii[0], ii[1]);
		m = DustUtils.simpleGet(m, MIND_ATT_UNIT_KNOWLEDGE);
		return DustUtils.safeGet(m, h, createIfMissing ? KNOWLEDGE_CREATOR : null);
	}

	@Override
	protected <RetType> RetType access(MindAccess cmd, Object val, Object root, Object... path) {
		Object ret = null;
		
		switch ( cmd ) {
		case Broadcast:
			log((MindHandle) val, path);
			break;
		case Lookup:
			ret = idRes.recall((String) val);
			break;
		default:
			ret = dialogs.get().access(cmd, val, root, path);
			break;
		}
		
		return (RetType) ret;
		
//		boolean createIfMissing = DustUtilsAttCache.getAtt(MachineAtts.CreatorAccess, cmd, false);
//
//		Object curr;
//
//		if ( null == root ) {
//			curr = mainDialog.context;
//		} else {
//			MindHandle activeAgent = (MindHandle) mainDialog.context.get(MIND_ATT_DIALOG_ACTIVEAGENT);
//
//			if ( null == activeAgent ) {
//				curr = resolveKnowledge((MindHandle) root, createIfMissing);
//			} else {
//				MindContext ctx = DustUtilsEnumTranslator.getEnum((MindHandle) root, MindContext.Direct);
//
//				switch ( ctx ) {
//				case Dialog:
//					curr = mainDialog.context;
//					break;
//				case Self:
//					curr = resolveKnowledge(activeAgent, createIfMissing);
//					break;
//				case Target:
//					curr = resolveKnowledge(activeAgent, createIfMissing);
//					curr = DustUtils.safeGet(curr, MIND_ATT_AGENT_TARGET, createIfMissing ? KNOWLEDGE_CREATOR : null);
//					break;
//				default:
//					curr = resolveKnowledge((MindHandle) root, createIfMissing);
//					break;
//				}
//			}
//		}
//
//		Object prev = null;
//		Object lastKey = null;
//
//		Map prevMap = null;
//		ArrayList prevArr = null;
//		Set prevSet = null;
//
//		for (Object p : path) {
//			if ( curr instanceof DustHandle ) {
//				curr = resolveKnowledge((DustHandle) curr, createIfMissing);
//			} else if ( null == curr ) {
//				if ( createIfMissing ) {
//					curr = (p instanceof Integer) ? new ArrayList() : new HashMap();
//
//					if ( null != prevArr ) {
//						DustUtils.safePut(prevArr, (Integer) lastKey, val, false);
//					} else if ( null != prevMap ) {
//						prevMap.put(lastKey, curr);
//					} else if ( null != prevSet ) {
//						prevSet.add(curr);
//					}
//				} else {
//					break;
//				}
//			}
//
//			prev = curr;
//			prevArr = (prev instanceof ArrayList) ? (ArrayList) prev : null;
//			prevMap = (prev instanceof Map) ? (Map) prev : null;
//			prevSet = (prev instanceof Set) ? (Set) prev : null;
//
//			lastKey = p;
//
//			if ( curr instanceof ArrayList ) {
//				ArrayList al = (ArrayList) curr;
//				Integer idx = (Integer) p;
//
//				if ( (KEY_ADD == idx) || (idx >= al.size()) ) {
//					curr = null;
//				} else {
//					curr = al.get(idx);
//				}
//			} else if ( curr instanceof Map ) {
//				curr = ((Map) curr).get(p);
//			} else {
//				curr = null;
//			}
//		}
//
//		switch ( cmd ) {
//		case Check:
//			break;
//		case Commit:
//			if ( curr instanceof DustHandle ) {
//				curr = resolveKnowledge((DustHandle) curr, createIfMissing);
//			}
//			if ( curr instanceof Map ) {
//				ArrayList listeners = DustUtils.safeGet(curr, MIND_ATT_KNOWLEDGE_LISTENERS, null);
//				if ( null != listeners ) {
//					Object hTarget = DustUtils.safeGet(curr, MIND_ATT_KNOWLEDGE_HANDLE, null);
//					Object oldAgent = Dust.access(MindAccess.Peek, null, null, MIND_ATT_DIALOG_ACTIVEAGENT);
//					MindAction action = DustUtilsEnumTranslator.getEnum((MindHandle) val, MindAction.Process);
//
//					for (Object a : listeners) {
//						try {
//							MindAgent agent = selectAgent(a);
//							if ( null != agent ) {
//								Dust.access(MindAccess.Set, hTarget, a, MIND_ATT_AGENT_TARGET);
//								Dust.access(MindAccess.Set, a, null, MIND_ATT_DIALOG_ACTIVEAGENT);
//								switch ( action ) {
//								case Begin:
//								case Process:
//								case End:
//									agent.agentProcess(action);
//									break;
//								case Init:
//								case Release:
//									DustException.wrap(null, "Invalid commit action", val);
//									break;
//								}
//							}
//						} catch (Throwable e) {
//							DustException.swallow(e);
//						}
//					}
//					Dust.access(MindAccess.Set, oldAgent, null, MIND_ATT_DIALOG_ACTIVEAGENT);
//				}
//			}
//
//			break;
//		case Delete:
//			break;
//		case Get:
//			ret = curr;
//			break;
//		case Insert:
//			if ( curr instanceof Set ) {
//				prevSet = (Set) curr;
//			}
//			if ( !DustUtils.isEqual(curr, val) ) {
//				if ( null != prevArr ) {
//					DustUtils.safePut(prevArr, (Integer) lastKey, val, false);
//				} else if ( null != prevSet ) {
//					prevSet.add(val);
//				} else {
//					Set s = new HashSet();
//					s.add(val);
//					prevMap.put(lastKey, s);
//				}
//			}
//			break;
//		case Peek:
//			ret = (null == curr) ? val : curr;
//			break;
//		case Reset:
//			break;
//		case Set:
//			if ( null != lastKey ) {
//				if ( null != prevMap ) {
//					if ( !DustUtils.isEqual(curr, val) ) {
//						prevMap.put(lastKey, val);
//					}
//				} else if ( null != prevArr ) {
//					DustUtils.safePut(prevArr, (Integer) lastKey, val, true);
//				}
//			}
//
//			break;
//		case Broadcast:
//			break;
//		case Lookup:
//			break;
//		}
//
//		return (RetType) ret;
	}

	protected void log(MindHandle event, Object... params) {
		StringBuilder sb = DustUtils.sbAppend(null, ", ", false, params);

		if ( null != sb ) {
			System.out.println(DustUtils.sbAppend(null, "", true, DustDevUtils.getTimeStr(), " [", event, "] ", sb));
		}
	}

	protected MindHandle agentInit() throws Exception {
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		return MIND_TAG_RESULT_ACCEPT;
	}

	protected MindHandle agentBegin() throws Exception {

		MindHandle ret = MIND_TAG_RESULT_REJECT;

		Collection mods = Dust.access(MindAccess.Peek, Collections.EMPTY_LIST, APP_MACHINE_MAIN, DUST_ATT_MACHINE_MODULES);
		for (Object m : mods) {
			Collection nls = Dust.access(MindAccess.Peek, Collections.EMPTY_LIST, m, DUST_ATT_MODULE_NATIVELOGICS);
			for (Object nl : nls) {
				Dust.access(MindAccess.Insert, nl, APP_MACHINE_MAIN, DUST_ATT_MACHINE_ALL_IMPLEMENTATIONS);
			}
		}

		ArrayList sa = Dust.access(MindAccess.Peek, null, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS);
		if ( null == sa ) {
			ret = MIND_TAG_RESULT_PASS;
		} else {
			for (Object a : sa) {
				MindAgent agent = selectAgent(a);

				if ( null != agent ) {
					Dust.access(MindAccess.Set, a, null, MIND_ATT_DIALOG_ACTIVEAGENT);

					try {
						if ( DustUtilsAttCache.getAtt(MachineAtts.CanContinue, agent.agentProcess(MindAction.Begin), false) ) {
							do {
							} while (DustUtilsAttCache.getAtt(MachineAtts.CanContinue, agent.agentProcess(MindAction.Process), false));
						}
					} finally {
						agent.agentProcess(MindAction.End);
					}
				}
			}

			ret = (null == Dust.access(MindAccess.Peek, null, APP_ASSEMBLY_MAIN, DUST_ATT_MACHINE_ACTIVE_SERVERS)) ? MIND_TAG_RESULT_ACCEPT : MIND_TAG_RESULT_READACCEPT;
		}

		return ret;
	}

	public MindAgent selectAgent(Object a) throws Exception {
		MindAgent agent = Dust.access(MindAccess.Peek, null, a, DUST_ATT_NATIVELOGIC_INSTANCE);

		if ( null == agent ) {
			Object l = Dust.access(MindAccess.Peek, null, a, MIND_ATT_AGENT_LOGIC);
			Object n = null;
			Collection allNatLog = Dust.access(MindAccess.Peek, Collections.EMPTY_SET, APP_MACHINE_MAIN, DUST_ATT_MACHINE_ALL_IMPLEMENTATIONS);
			for (Object nl : allNatLog) {
				if ( l == Dust.access(MindAccess.Peek, null, nl, DUST_ATT_NATIVELOGIC_LOGIC) ) {
					n = nl;
					break;
				}
			}

			if ( null != n ) {
				agent = Dust.access(MindAccess.Peek, null, n, DUST_ATT_NATIVELOGIC_INSTANCE);
				Dust.access(MindAccess.Set, a, null, MIND_ATT_DIALOG_ACTIVEAGENT);

				if ( null == agent ) {
					String ac = Dust.access(MindAccess.Peek, null, n, DUST_ATT_NATIVELOGIC_IMPLEMENTATION);
					agent = (MindAgent) Class.forName(ac).newInstance();
					boolean srv = Dust.access(MindAccess.Peek, false, n, MIND_ATT_KNOWLEDGE_TAGS, DUST_TAG_NATIVELOGIC_SERVER);
					if ( srv ) {
						agent.agentProcess(MindAction.Init);
						Dust.access(MindAccess.Insert, agent, APP_ASSEMBLY_MAIN, DUST_ATT_MACHINE_ACTIVE_SERVERS, 0);
					}
					Dust.access(MindAccess.Set, agent, n, DUST_ATT_NATIVELOGIC_INSTANCE);
				}
			}

			Dust.access(MindAccess.Set, agent, a, DUST_ATT_NATIVELOGIC_INSTANCE);
		} else {
			Dust.access(MindAccess.Set, a, null, MIND_ATT_DIALOG_ACTIVEAGENT);
		}

		return agent;
	}

	@Override
	public MindHandle agentProcess(MindAction action) throws Exception {
		switch ( action) {
		case Begin:
			return agentBegin();
		case End:
			return agentEnd();
		case Init:
			return agentInit();
		case Process:
			return agentProcess();
		case Release:
			return agentRelease();
		}
		
		return null;
	}

	protected MindHandle agentProcess() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected MindHandle agentEnd() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected MindHandle agentRelease() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_PASS;

		Collection<MindAgent> servers = Dust.access(MindAccess.Peek, null, APP_ASSEMBLY_MAIN, DUST_ATT_MACHINE_ACTIVE_SERVERS);
		if ( null == servers ) {
			ret = MIND_TAG_RESULT_PASS;
		} else {
			ret = MIND_TAG_RESULT_ACCEPT;

			for (MindAgent s : servers) {
				try {
					s.agentProcess(MindAction.Release);
				} catch (Throwable e) {
					DustException.swallow(e, "Machnie release");
					ret = MIND_TAG_RESULT_REJECT;
				}
			}

			Dust.access(MindAccess.Reset, null, APP_ASSEMBLY_MAIN, DUST_ATT_MACHINE_ACTIVE_SERVERS);
		}

		return ret;
	}
}
