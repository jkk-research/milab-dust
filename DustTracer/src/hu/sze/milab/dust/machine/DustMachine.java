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
