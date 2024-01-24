package hu.sze.milab.dust.machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsEnumTranslator;

@SuppressWarnings({ "rawtypes", "unchecked" })
class DustMachine extends Dust.Machine implements DustMachineConsts, DustConsts.MindAgentServer {

	Dust.IdResolver idRes;
	final DustMachineDialog mainDialog;

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

	public DustMachine() {
		mainDialog = new DustMachineDialog();
	}

	@Override
	public DustHandle recall(String id) {
		return (DustHandle) idRes.recall(id);
	}

	Map resolveKnowledge(MindHandle h, boolean createIfMissing) {
		Map knowledge = DustUtils.safeGet(mainDialog.context, MIND_ATT_DIALOG_KNOWLEDGE, MAP_CREATOR);
		return DustUtils.safeGet(knowledge, h, createIfMissing ? KNOWLEDGE_CREATOR : null);
	}

	@Override
	protected <RetType> RetType access(Object root, MindHandle cmd, Object val, Object... path) {
		Object ret = null;

		MindAccess ac = DustUtilsEnumTranslator.getEnum(cmd, MindAccess.Peek);
		boolean createIfMissing = DustUtilsAttCache.getAtt(MachineAtts.CreatorAccess, cmd, false);

		Object curr;

		if ( null == root ) {
			curr = mainDialog.context;
		} else {
			MindHandle activeAgent = (MindHandle) mainDialog.context.get(MIND_ATT_DIALOG_ACTIVEAGENT);

			if ( null == activeAgent ) {
				curr = resolveKnowledge((MindHandle) root, createIfMissing);
			} else {
				MindContext ctx = DustUtilsEnumTranslator.getEnum((MindHandle) root, MindContext.Direct);

				switch ( ctx ) {
				case Dialog:
					curr = mainDialog.context;
					break;
				case Self:
					curr = resolveKnowledge(activeAgent, createIfMissing);
					break;
				case Target:
					curr = resolveKnowledge(activeAgent, createIfMissing);
					curr = DustUtils.safeGet(curr, MIND_ATT_AGENT_TARGET, createIfMissing ? KNOWLEDGE_CREATOR : null);
					break;
				default:
					curr = resolveKnowledge((MindHandle) root, createIfMissing);
					break;
				}
			}
		}

		Object prev = null;
		Object lastKey = null;

		Map prevMap = null;
		ArrayList prevArr = null;
		Set prevSet = null;

		for (Object p : path) {
			if ( curr instanceof DustHandle ) {
				curr = resolveKnowledge((DustHandle) curr, createIfMissing);
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
			if ( curr instanceof DustHandle ) {
				curr = resolveKnowledge((DustHandle) curr, createIfMissing);
			}
			if ( curr instanceof Map ) {
				ArrayList listeners = DustUtils.safeGet(curr, MIND_ATT_KNOWLEDGE_LISTENERS, null);
				if ( null != listeners ) {
					Object hTarget = DustUtils.safeGet(curr, MIND_ATT_KNOWLEDGE_HANDLE, null);
					Object oldAgent = Dust.access(null, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_DIALOG_ACTIVEAGENT);
					for (Object a : listeners) {
						try {
							MindAgent agent = selectAgent(a);
							if ( null != agent ) {
								Dust.access(a, MIND_TAG_ACCESS_SET, hTarget, MIND_ATT_AGENT_TARGET);
								Dust.access(null, MIND_TAG_ACCESS_SET, a, MIND_ATT_DIALOG_ACTIVEAGENT);
								agent.agentProcess();
							}
						} catch (Throwable e) {
							DustException.swallow(e);
						}
					}
					Dust.access(null, MIND_TAG_ACCESS_SET, oldAgent, MIND_ATT_DIALOG_ACTIVEAGENT);
				}
			}

			break;
		case Delete:
			break;
		case Get:
			ret = curr;
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
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public MindHandle agentBegin() throws Exception {

		MindHandle ret = MIND_TAG_RESULT_REJECT;

		Collection mods = Dust.access(APP_MACHINE_MAIN, MIND_TAG_ACCESS_PEEK, Collections.EMPTY_LIST, DUST_ATT_MACHINE_MODULES);
		for (Object m : mods) {
			Collection nls = Dust.access(m, MIND_TAG_ACCESS_PEEK, Collections.EMPTY_LIST, DUST_ATT_MODULE_NATIVELOGICS);
			for (Object nl : nls) {
				Dust.access(APP_MACHINE_MAIN, MIND_TAG_ACCESS_INSERT, nl, DUST_ATT_MACHINE_ALL_IMPLEMENTATIONS);
			}
		}

		ArrayList sa = Dust.access(APP_ASSEMBLY_MAIN, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_ASSEMBLY_STARTAGENTS);
		if ( null == sa ) {
			ret = MIND_TAG_RESULT_PASS;
		} else {
			for (Object a : sa) {
				MindAgent agent = selectAgent(a);

				if ( null != agent ) {
					Dust.access(null, MIND_TAG_ACCESS_SET, a, MIND_ATT_DIALOG_ACTIVEAGENT);

					try {
						if ( DustUtilsAttCache.getAtt(MachineAtts.CanContinue, agent.agentBegin(), false) ) {
							do {
							} while (DustUtilsAttCache.getAtt(MachineAtts.CanContinue, agent.agentProcess(), false));
						}
					} finally {
						agent.agentEnd();
					}
				}
			}

			ret = (null == Dust.access(APP_ASSEMBLY_MAIN, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_MACHINE_ACTIVE_SERVERS)) ? MIND_TAG_RESULT_ACCEPT : MIND_TAG_RESULT_READACCEPT;
		}

		return ret;
	}

	public MindAgent selectAgent(Object a) throws Exception {
		MindAgent agent = Dust.access(a, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_NATIVELOGIC_INSTANCE);

		if ( null == agent ) {
			Object l = Dust.access(a, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_AGENT_LOGIC);
			Object n = null;
			Collection allNatLog = Dust.access(APP_MACHINE_MAIN, MIND_TAG_ACCESS_PEEK, Collections.EMPTY_SET, DUST_ATT_MACHINE_ALL_IMPLEMENTATIONS);
			for (Object nl : allNatLog) {
				if ( l == Dust.access(nl, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_NATIVELOGIC_LOGIC) ) {
					n = nl;
					break;
				}
			}

			if ( null != n ) {
				agent = Dust.access(n, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_NATIVELOGIC_INSTANCE);
				Dust.access(null, MIND_TAG_ACCESS_SET, a, MIND_ATT_DIALOG_ACTIVEAGENT);

				if ( null == agent ) {
					String ac = Dust.access(n, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_NATIVELOGIC_IMPLEMENTATION);
					agent = (MindAgent) Class.forName(ac).newInstance();
					if ( agent instanceof MindAgentServer ) {
						((MindAgentServer) agent).agentInit();
						Dust.access(APP_ASSEMBLY_MAIN, MIND_TAG_ACCESS_INSERT, agent, DUST_ATT_MACHINE_ACTIVE_SERVERS, 0);
					}
					Dust.access(n, MIND_TAG_ACCESS_SET, agent, DUST_ATT_NATIVELOGIC_INSTANCE);
				}
			}

			Dust.access(a, MIND_TAG_ACCESS_SET, agent, DUST_ATT_NATIVELOGIC_INSTANCE);
		} else {
			Dust.access(null, MIND_TAG_ACCESS_SET, a, MIND_ATT_DIALOG_ACTIVEAGENT);
		}

		return agent;
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
		MindHandle ret = MIND_TAG_RESULT_PASS;

		Collection servers = Dust.access(APP_ASSEMBLY_MAIN, MIND_TAG_ACCESS_PEEK, null, DUST_ATT_MACHINE_ACTIVE_SERVERS);
		if ( null == servers ) {
			ret = MIND_TAG_RESULT_PASS;
		} else {
			ret = MIND_TAG_RESULT_ACCEPT;

			for (Object s : servers) {
				try {
					((MindAgentServer) s).agentRelease();
				} catch (Throwable e) {
					DustException.swallow(e, "Machnie release");
					ret = MIND_TAG_RESULT_REJECT;
				}
			}

			Dust.access(APP_ASSEMBLY_MAIN, MIND_TAG_ACCESS_RESET, null, DUST_ATT_MACHINE_ACTIVE_SERVERS);
		}

		return ret;
	}
}
