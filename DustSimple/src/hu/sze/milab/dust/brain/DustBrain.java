package hu.sze.milab.dust.brain;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.utils.DustUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustBrain implements DustBrainConsts, Dust.Brain, DustConsts.MindAgent {

	static Map<MindHandle, Map> ctxBrain = new HashMap<>();
	static Map brainRoot = new HashMap<>();
	static DustBrainUtils utils = new DustBrainUtils();

	private boolean booting = true;

	private static final ThreadLocal<EnumMap<MindContext, MindHandle>> threadCtx = new ThreadLocal<EnumMap<MindContext, MindHandle>>() {
		@Override
		protected EnumMap<MindContext, MindHandle> initialValue() {
			return new EnumMap<>(MindContext.class);
		}
	};

	@Override
	public MindHandle resolveID(String id, MindHandle primaryAspect) {
		MindHandle ret = null;
		if ( null == id ) {
			ret = new DustBrainHandle();
		} else {
			String[] ii = id.split(SEP_ID);
			MindHandle hUnit = access(brainRoot, MindAccess.Peek, null, DUST_ATT_BRAIN_UNITS, ii[0]);

			if ( null == hUnit ) {
				if ( booting || (MIND_ASP_UNIT == primaryAspect) ) {
					hUnit = new DustBrainHandle();
					access(hUnit, MindAccess.Set, primaryAspect, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
					access(hUnit, MindAccess.Set, id, MIND_ATT_KNOWLEDGE_ID);
					access(brainRoot, MindAccess.Set, hUnit, DUST_ATT_BRAIN_UNITS, ii[0]);
				} else {
					return null;
				}
			}

			if ( 1 < ii.length ) {
				ret = access(hUnit, MindAccess.Peek, null, MISC_ATT_CONN_MEMBERMAP, ii[1]);

				if ( (null == ret) && (booting || (null != primaryAspect)) ) {
					ret = new DustBrainHandle();
					access(ret, MindAccess.Set, primaryAspect, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
					access(ret, MindAccess.Set, id, MIND_ATT_KNOWLEDGE_ID);
					access(ret, MindAccess.Set, ii[1], TEXT_ATT_NAMED_NAME);
					access(hUnit, MindAccess.Set, ret, MISC_ATT_CONN_MEMBERMAP, ii[1]);

				}
			} else {
				ret = hUnit;
			}
		}

		return ret;
	}

	static public Map resolveKnowledge(MindHandle bh, boolean createIfMissing) {
		if ( null == bh ) {
			bh = new DustBrainHandle();
			createIfMissing = true;
		}

		synchronized (bh) {
			Map k = ctxBrain.get(bh);

			if ( (null == k) && createIfMissing ) {
				k = new HashMap<>();
				k.put(MIND_ATT_KNOWLEDGE_HANDLE, bh);
				ctxBrain.put(bh, k);
			}

			return k;
		}
	}

	static public String handleToString(DustBrainHandle bh) {
		Map k = resolveKnowledge(bh, false);
		return (null == k) ? STR_UNKNOWN : (String) k.getOrDefault(DustMetaConsts.TEXT_ATT_NAMED_NAME, STR_UNKNOWN);
	}

	static public void dumpHandle(String prefix, MindHandle bh) {
		Map k = resolveKnowledge(bh, false);
		Dust.dumpObs(prefix, (null == k) ? STR_UNKNOWN : DustUtils.toString(k));
	}

	@Override
	public MindStatus agentExecAction(MindAction action) throws Exception {
		switch ( action ) {
		case Init:
			Thread t = Thread.currentThread();
			MindHandle ht = resolveID(null, DUST_ASP_THREAD);
//			Map mt = resolveKnowledge(ht, true);

			MindHandle hd = resolveID(null, MIND_ASP_DIALOG);

			EnumMap<MindContext, MindHandle> currCtx = threadCtx.get();
			currCtx.put(MindContext.Dialog, hd);

//			mt.put(DUST_ATT_THREAD_CONTEXTS, ctxMap);
			access(brainRoot, MindAccess.Insert, hd, DUST_ATT_BRAIN_DIALOGS);

			WeakHashMap<Thread, MindHandle> tm = new WeakHashMap<>();
			brainRoot.put(DUST_ATT_BRAIN_THREADS, tm);
			tm.put(t, ht);

			utils.initBrain(this);

//			booting = false;
			break;
		case Begin:
			utils.loadConfigs();
			break;
		case Process:
			break;
		case End:
			break;
		case Release:
			break;
		}

		return MindStatus.Accept;
	}

	@Override
	public <RetType> RetType access(Object root, MindAccess cmd, Object val, Object... path) {
		Object ret = null;

		if ( root instanceof MindContext ) {
			root = threadCtx.get().get((MindContext) root);
//			root = access(brainRoot, MindAccess.Peek, null, DUST_ATT_BRAIN_THREADS, Thread.currentThread());
		}

		Object curr = root;
		Object prev = null;
		Object lastKey = null;

		for (Object p : path) {
			if ( p instanceof Enum ) {
				p = ((Enum) p).name();
			}
			if ( curr instanceof MindHandle ) {
				curr = resolveKnowledge((DustBrainHandle) curr, true);
			} else if ( null == curr ) {
				if ( MindUtils.isCreateAccess(cmd) ) {
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

		ret = (null == curr) ? val : curr;

		switch ( cmd ) {
		case Check:
			break;
		case Commit:
			if ( curr instanceof MindHandle ) {
				MindHandle h = Dust.access(curr, MindAccess.Peek, null, MIND_ATT_KNOWLEDGE_LISTENERS);
				EnumMap<MindContext, MindHandle> prevCtx = new EnumMap<>(MindContext.class);
				EnumMap<MindContext, MindHandle> currCtx = threadCtx.get();
				prevCtx.putAll(currCtx);

				currCtx.put(MindContext.Message, (MindHandle) curr);
				currCtx.put(MindContext.Self, h);

				try {
					MindAgent a = Dust.access(h, MindAccess.Peek, null, DUST_ATT_NATIVE_INSTANCE);

					if ( null == a ) {
						MindHandle hL = null;
						String agentClass = null;
						try {
							hL = Dust.access(h, MindAccess.Peek, null, MIND_ATT_AGENT_LOGIC);
							agentClass = Dust.access(brainRoot, MindAccess.Peek, null, DUST_ATT_NATIVE_IMPLEMENTATIONS, hL);
							a = (MindAgent) Class.forName(agentClass).newInstance();
							a.agentExecAction(MindAction.Init);
							Dust.access(h, MindAccess.Set, a, DUST_ATT_NATIVE_INSTANCE);
						} catch (Throwable e) {
							DustException.wrap(e, hL, agentClass);
						}
					}

					if ( null != a ) {
						try {
							a.agentExecAction((MindAction) val);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				} finally {
					currCtx.putAll(prevCtx);
				}
			}
			break;
		case Delete:
			break;
		case Get:
			break;
		case Insert:
			break;
		case Peek:
			break;
		case Reset:
			if ( curr instanceof MindHandle ) {
				curr = resolveKnowledge((DustBrainHandle) curr, false);
			}
			if ( curr instanceof Map ) {
				Map m = (Map) curr;
				Object l = m.get(MIND_ATT_KNOWLEDGE_LISTENERS);
				m.clear();
				m.put(MIND_ATT_KNOWLEDGE_LISTENERS, l);
			}
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
		default:
			break;
		}

		return (RetType) ret;
	}
}
