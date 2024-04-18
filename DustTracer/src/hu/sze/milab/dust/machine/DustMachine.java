package hu.sze.milab.dust.machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.stream.json.DustJsonApiDomAgent;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;

@SuppressWarnings({ "rawtypes", "unchecked" })
class DustMachine extends Dust.Machine
		implements DustMachineConsts, DustConsts.MindAgent, DustMachineConsts.IdResolver {

//long lastId = 100000L;
//private synchronized String getTempId() {
//	return String.valueOf( ++lastId );
//}

	static class KnowledgeMap extends HashMap {
		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			return "{...}";
		}
	}

	boolean loadUnits = false;
//	boolean loadUnits = true;

	IdResolver idRes;
	private final Map rootUnit = new KnowledgeMap();

	DustCreator<DustHandle> crtHandle = new DustCreator<DustHandle>() {
		@Override
		public DustHandle create(Object key, Object... hints) {
			return new DustHandle((String) key);
		}
	};

	DustCreator<Map> crtKnowledge = new DustCreator<Map>() {
		@Override
		public Map create(Object key, Object... hints) {
			Map m = new KnowledgeMap();
			m.put(MIND_ATT_KNOWLEDGE_HANDLE, key);
			return m;
		}
	};

	DustCreator<Map> crtUnit = new DustCreator<Map>() {
		@Override
		public Map create(Object key, Object... hints) {
			Map m = new KnowledgeMap();
			initUnit(m, (DustHandle) key, hints);

			return m;
		}
	};

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

	public void bootInit(Map<String, DustHandle> bh) {
		initUnit(rootUnit, APP_UNIT);
		Map rootHandles = DustUtils.simpleGet(rootUnit, MIND_ATT_UNIT_HANDLES);
		Map rootContent = DustUtils.simpleGet(rootUnit, MIND_ATT_UNIT_CONTENT);

		Map<String, Map> unitHandles = new TreeMap<>();

		Set<MindHandle> units = new HashSet();
		units.add(APP_UNIT);

		for (Map.Entry<String, DustHandle> be : bh.entrySet()) {
			String id = be.getKey();

			String[] ids = id.split(DUST_SEP_ID);
			int idl = ids.length;

			if (idl < 3) {
				DustHandle h = be.getValue();
				rootHandles.put(id, h);

				if (APP_UNIT == h) {
					unitHandles.put(id, rootHandles);
					continue;
				}

				if (idl == 2) {
					Map m = DustUtils.safeGet(rootContent, h, crtKnowledge);
					DustHandle hAuthor = bh.get(ids[0]);
					if (null == hAuthor) {
						hAuthor = lookup(rootUnit, ids[0]);
						Map mA = DustUtils.safeGet(rootContent, hAuthor, crtKnowledge);
						mA.put(MIND_ATT_KNOWLEDGE_PRIMARYASPECT, MIND_ASP_AUTHOR);
					}
					initUnit(m, h, hAuthor);
					units.add(h);

					unitHandles.put(id, (Map) m.get(MIND_ATT_UNIT_HANDLES));
				}
			}
		}

		for (Map.Entry<String, DustHandle> be : bh.entrySet()) {
			String id = be.getKey();

			if (3 == id.split(DUST_SEP_ID).length) {
				DustHandle h = be.getValue();
				String unitId = DustUtils.cutPostfix(id, DUST_SEP_ID);
				unitHandles.get(unitId).put(id, h);
			}
		}

		idRes = this;

		for (MindHandle hu : units) {
			optLoadUnit(hu);
		}

		Dust.access(MindAccess.Set, APP_ASSEMBLY_MAIN, APP_MACHINE_MAIN, DUST_ATT_MACHINE_MAINASSEMBLY);
		Dust.access(MindAccess.Set, APP_MODULE_MAIN, APP_MACHINE_MAIN, DUST_ATT_MACHINE_MODULES, KEY_ADD);
	}

	private void initUnit(Map m, MindHandle h, Object... hints) {
		Map mH = new HashMap();
		m.put(MIND_ATT_UNIT_HANDLES, mH);
		mH.put(null, h);

		Map mK = new HashMap();
		m.put(MIND_ATT_UNIT_CONTENT, mK);
		mK.put(h, m);

		m.put(MIND_ATT_KNOWLEDGE_HANDLE, h);
		m.put(MIND_ATT_KNOWLEDGE_PRIMARYASPECT, MIND_ASP_UNIT);
		if (0 < hints.length) {
			m.put(MIND_ATT_UNIT_AUTHOR, hints[0]);
		}

		optLoadUnit(h);
	}

	boolean canLoadUnit = true;
	private void optLoadUnit(MindHandle h) {
		if (loadUnits && canLoadUnit && (this == idRes)) {
			try {
				canLoadUnit = false;
				DustJsonApiDomAgent.readUnit(DustMachineUtils.getUnitFile(h));
			} catch (Exception e) {
				DustException.swallow(e, "Loading unit", h);
			} finally {
				canLoadUnit = true;
			}
		}
	}

	DustHandle lookup(Map unit, String id) {
		synchronized (unit) {
			DustHandle ret = DustUtils.simpleGet(unit, MIND_ATT_UNIT_HANDLES, id);

			if (null == ret) {
				Map m = (Map) unit.get(MIND_ATT_UNIT_HANDLES);
				ret = DustUtils.safeGet(m, id, crtHandle);
			}
			return ret;
		}
	}

	Map getUnit(String unitID, MindHandle hAuthor) {
//		if ( "giskard:10".equals(unitID) ) {
//			DustDevUtils.breakpoint("hipp");
//		}
		MindHandle hUnit = lookup(rootUnit, unitID);
		Map m = (Map) rootUnit.get(MIND_ATT_UNIT_CONTENT);
		Map ret = DustUtils.safeGet(m, hUnit, crtUnit, hAuthor);
		return ret;
	}

	@Override
	public DustHandle recall(String id) {
		String[] ii = id.split(DUST_SEP_ID);
		DustHandle hAuthor = lookup(rootUnit, ii[0]);
		DustHandle ret = hAuthor;

		if (ii.length > 1) {
			String unitID = (ii.length == 2) ? id : DustUtils.cutPostfix(id, DUST_SEP_ID);
			ret = lookup(rootUnit, unitID);

			if (ii.length > 2) {
				Map unit = getUnit(unitID, hAuthor);

				if (ITEMID_NEW.equals(ii[2])) {
					Map mh = DustUtils.simpleGet(unit, MIND_ATT_UNIT_HANDLES);
					id = DustUtils.sbAppend(null, DUST_SEP_ID, true, ii[0], ii[1], mh.size()).toString();
				}

				ret = lookup(unit, id);
			}
		}

		return ret;
	}

	Map getMemexKnowledge(DustHandle h, boolean createIfMissing) {
		String id = h.getId();
		String[] ii = id.split(DUST_SEP_ID);
		Map unit = null;
		DustHandle hAuthor = null;

		if (ii.length < 3) {
			unit = rootUnit;
		} else {
			hAuthor = lookup(rootUnit, ii[0]);
			unit = getUnit(DustUtils.cutPostfix(id, DUST_SEP_ID), hAuthor);
		}

		Map m = DustUtils.simpleGet(unit, MIND_ATT_UNIT_CONTENT);
		return DustUtils.safeGet(m, h, createIfMissing ? (ii.length == 2) ? crtUnit : crtKnowledge  : null, hAuthor);
	}

	@Override
	protected <RetType> RetType access(MindAccess cmd, Object val, Object root, Object... path) {
		Object ret = null;

		switch (cmd) {
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

		if (null != sb) {
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
			Collection nls = Dust.access(MindAccess.Peek, Collections.EMPTY_LIST, m, DUST_ATT_MODULE_NARRATIVEIMPLS);
			for (Object nl : nls) {
				Dust.access(MindAccess.Insert, nl, APP_MACHINE_MAIN, DUST_ATT_MACHINE_ALL_IMPLEMENTATIONS);
			}
		}

		ArrayList sa = Dust.access(MindAccess.Peek, null, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTAGENTS);
		if (null == sa) {
			ret = MIND_TAG_RESULT_PASS;
		} else {
			for (Object a : sa) {
				MindAgent agent = selectAgent(a);

				if (null != agent) {
					Dust.access(MindAccess.Set, a, null, MIND_ATT_DIALOG_ACTIVEAGENT);

					try {
						if (DustUtilsAttCache.getAtt(MachineAtts.CanContinue, agent.agentProcess(MindAction.Begin), false)) {
							do {
							} while (DustUtilsAttCache.getAtt(MachineAtts.CanContinue, agent.agentProcess(MindAction.Process),
									false));
						}
					} finally {
						agent.agentProcess(MindAction.End);
					}
				}
			}

			ret = (null == Dust.access(MindAccess.Peek, null, APP_ASSEMBLY_MAIN, DUST_ATT_MACHINE_ACTIVE_SERVERS))
					? MIND_TAG_RESULT_ACCEPT
					: MIND_TAG_RESULT_READACCEPT;
		}

		ArrayList sc = Dust.access(MindAccess.Peek, null, APP_ASSEMBLY_MAIN, MIND_ATT_ASSEMBLY_STARTCOMMITS);
		if (null != sc) {
			for (Object c : sc) {
				boolean transaction = Dust.access(MindAccess.Check, MISC_TAG_TRANSACTION, c, MIND_ATT_KNOWLEDGE_TAGS, MISC_TAG_TRANSACTION);
				if ( transaction ) {
					Dust.access(MindAccess.Commit, MIND_TAG_ACTION_BEGIN, c);
				}
				try {
					Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, c);
				} finally {
					Dust.access(MindAccess.Commit, MIND_TAG_ACTION_END, c);					
				}
			}
		}

		return ret;
	}

	public MindAgent selectAgent(Object a) throws Exception {
		MindAgent agent = Dust.access(MindAccess.Peek, null, a, DUST_ATT_IMPL_INSTANCE);

		if (null == agent) {
			Object l = Dust.access(MindAccess.Peek, null, a, MIND_ATT_AGENT_NARRATIVE);
			Object n = null;
			Collection allNatLog = Dust.access(MindAccess.Peek, Collections.EMPTY_SET, APP_MACHINE_MAIN, DUST_ATT_MACHINE_ALL_IMPLEMENTATIONS);
			for (Object nl : allNatLog) {
				if (l == Dust.access(MindAccess.Peek, null, nl, DUST_ATT_IMPL_NARRATIVE)) {
					n = nl;
					break;
				}
			}

			if (null != n) {
				agent = Dust.access(MindAccess.Peek, null, n, DUST_ATT_IMPL_INSTANCE);
				Dust.access(MindAccess.Set, a, null, MIND_ATT_DIALOG_ACTIVEAGENT);

				if (null == agent) {
					String ac = Dust.access(MindAccess.Peek, null, n, TEXT_ATT_TOKEN);
					agent = (MindAgent) Class.forName(ac).getDeclaredConstructor().newInstance();
					boolean srv = Dust.access(MindAccess.Check, DUST_TAG_NATIVE_SERVER, n, MIND_ATT_KNOWLEDGE_TAGS, DUST_TAG_NATIVE_SERVER);
					if (srv) {
						agent.agentProcess(MindAction.Init);
						Dust.access(MindAccess.Insert, agent, APP_ASSEMBLY_MAIN, DUST_ATT_MACHINE_ACTIVE_SERVERS, 0);
					}
					Dust.access(MindAccess.Set, agent, n, DUST_ATT_IMPL_INSTANCE);
				}
			}

			Dust.access(MindAccess.Set, agent, a, DUST_ATT_IMPL_INSTANCE);
		} else {
			Dust.access(MindAccess.Set, a, null, MIND_ATT_DIALOG_ACTIVEAGENT);
		}

		return agent;
	}

	@Override
	public MindHandle agentProcess(MindAction action) throws Exception {
		switch (action) {
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

		Collection<MindAgent> servers = Dust.access(MindAccess.Peek, null, APP_ASSEMBLY_MAIN,
				DUST_ATT_MACHINE_ACTIVE_SERVERS);
		if (null == servers) {
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
