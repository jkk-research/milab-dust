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
import hu.sze.milab.dust.dev.DustDevUtils;
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

		if (null == root) {
			curr = context;
		} else {
			MindHandle activeAgent = (MindHandle) context.get(MIND_ATT_DIALOG_ACTIVEAGENT);

			if (null == activeAgent) {
				curr = (root instanceof MindContext) ? DustUtils.safeGet(context, root, MAP_CREATOR)
						: resolveKnowledge((MindHandle) root, createIfMissing);
			} else {
				MindContext ctx = (root instanceof MindContext) ? (MindContext) root
						: DustUtilsEnumTranslator.getEnum((MindHandle) root, MindContext.Direct);

				switch (ctx) {
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
				case Direct:
					curr = resolveKnowledge((MindHandle) root, true);
					break;
				default:
					curr = context.get((MindHandle) root);
					break;
				}
			}
		}

		DustHandle hLastItem = (DustHandle) ((curr instanceof Map) ? ((Map) curr).get(MIND_ATT_KNOWLEDGE_HANDLE)
				: (curr instanceof DustHandle) ? curr : (root instanceof DustHandle) ? root : null);

		Object currItem = curr;
		Object prev = null;
		Object lastKey = null;

		Object prevColl = null;
		MindCollType collType = null;
//		Map prevMap = null;
//		ArrayList prevArr = null;
//		Set prevSet = null;

		boolean setLastAtt = true;
		DustHandle hLastAtt = null;
		Map kAtt = null;

		for (Object p : path) {
			if (curr instanceof Enum) {
				MindHandle h = DustUtilsEnumTranslator.getHandle(curr);
				curr = (null == h) ? ((Enum) curr).name() : h;
			}

			if (curr instanceof DustHandle) {
				hLastItem = (DustHandle) curr;
				currItem = curr = resolveKnowledge(hLastItem, createIfMissing);
				setLastAtt = true;
			} else if (null == curr) {
				if (createIfMissing) {
					curr = (p instanceof Integer) ? new ArrayList() : new HashMap();

					if (null != prevColl) {
						switch (collType) {
						case Arr:
							DustUtils.safePut((ArrayList) prevColl, (Integer) lastKey, val, false);
							break;
						case Map:
							((Map) prevColl).put(lastKey, curr);
							break;
						case One:
							break;
						case Set:
							((Set) prevColl).add(curr);
							break;
						}
					}

					updateEnvInfo(currItem, kAtt, collType, prevColl, val);
//					if (null != prevArr) {
//						DustUtils.safePut(prevArr, (Integer) lastKey, val, false);
//					} else if (null != prevMap) {
//						prevMap.put(lastKey, curr);
//					} else if (null != prevSet) {
//						prevSet.add(curr);
//					}
				} else {
					break;
				}
			}

			prev = curr;
			collType = DustDevUtils.getCollType(prev);
			prevColl = (null == collType) ? null : prev;
//			prevArr = (prev instanceof ArrayList) ? (ArrayList) prev : null;
//			prevMap = (prev instanceof Map) ? (Map) prev : null;
//			prevSet = (prev instanceof Set) ? (Set) prev : null;

			lastKey = p;

			if (setLastAtt) {
				setLastAtt = false;
				if (p instanceof DustHandle) {
					hLastAtt = (DustHandle) p;
					kAtt = resolveKnowledge(hLastAtt, false);
				}
			}

			if (curr instanceof ArrayList) {
				ArrayList al = (ArrayList) curr;
				Integer idx = (Integer) p;

				if ((KEY_SIZE == idx)) {
					curr = al.size();
				} else if ((KEY_ADD == idx) || (idx >= al.size())) {
					curr = null;
				} else {
					curr = al.get(idx);
				}
			} else if (curr instanceof Map) {
				curr = DustUtils.isEqual(KEY_SIZE, p) ? ((Map) curr).size() : ((Map) curr).get(p);
			} else {
				curr = null;
			}

			// factory

			if ((null == curr) && createIfMissing && (null != hLastAtt)) {
				DustHandle hAttInfo = DustUtils.simpleGet(kAtt, MIND_ATT_KNOWLEDGE_TAGS, MIND_TAG_VALTYPE);

				if (MIND_TAG_VALTYPE_HANDLE == hAttInfo) {
					hAttInfo = DustUtils.simpleGet(kAtt, MIND_ATT_KNOWLEDGE_TAGS, MIND_TAG_COLLTYPE);
					MindCollType ct = DustUtilsEnumTranslator.getEnum(hAttInfo, MindCollType.One);
					switch (ct) {
					case Arr:
					case Map:
					case Set:
						if (hLastAtt == lastKey) {
							continue;
						}
						break;
					case One:
						break;
					}
					DustHandle dh = DustUtils.simpleGet(resolveKnowledge(hLastItem, false), MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
					if (MIND_ASP_AGENT == dh) {
						dh = DustUtils.simpleGet(resolveKnowledge(hLastItem, false), MIND_ATT_AGENT_NARRATIVE);
					}
					dh = DustUtils.simpleGet(resolveKnowledge(dh, false), MIND_ATT_ASPECT_ATTFACTORIES, hLastAtt);

					if (null == dh) {
						dh = DustUtils.simpleGet(resolveKnowledge(hLastAtt, false), MIND_ATT_ATTRIBUTE_FACTORY);
					}

					if (null != dh) {
						Object kFact = resolveKnowledge(dh, false);
						DustHandle hUnit = DustUtils.simpleGet(currItem, MIND_ATT_KNOWLEDGE_UNIT);
						DustHandle hPA = DustUtils.simpleGet(kFact, MIND_ATT_FACTORY_PRIMARYASPECT);

						curr = DustDevUtils.newHandle(hUnit, hPA);
						Object lk = lastKey;

						switch (ct) {
						case Arr: {
							ArrayList prevArr = (ArrayList) prevColl;
							if (DustUtils.isEqual(KEY_ADD, lastKey)) {
								prevArr.add(curr);
							} else {
								DustUtils.ensureSize(prevArr, (int) lastKey);
								prevArr.set((int) lastKey, curr);
							}
						}
							break;
						case Map:
							((Map) prevColl).put(lastKey, curr);
							break;
						case Set:
							break;
						case One:
							((Map) currItem).put(hLastAtt, curr);
							lk = null;
							break;
						}

						updateEnvInfo(currItem, kAtt, collType, prevColl, curr);

						DustHandle hNar = DustUtils.simpleGet(kFact, MIND_ATT_FACTORY_NARRATIVE);
						if (null != hNar) {
							MindAgent fn;
							try {
								fn = machine.selectAgent(hNar);
								context.put(MIND_TAG_CONTEXT_VISITITEM, hLastItem);
								context.put(MIND_TAG_CONTEXT_VISITATT, hLastAtt);
								context.put(MIND_TAG_CONTEXT_VISITKEY, lk);
								context.put(MIND_TAG_CONTEXT_VISITVALUE, curr);
								fn.agentProcess(MindAction.Process);
							} catch (Exception e) {
								DustException.swallow(e, "Factory init logic failure", curr, hNar);
							} finally {
								context.remove(MIND_TAG_CONTEXT_VISITITEM);
								context.remove(MIND_TAG_CONTEXT_VISITATT);
								context.remove(MIND_TAG_CONTEXT_VISITKEY);
								context.remove(MIND_TAG_CONTEXT_VISITVALUE);
							}
						}

					}
				}
			}
		}

		if (createIfMissing) {
			updateEnvInfo(currItem, kAtt, collType, prevColl, val);
		}

		switch (cmd) {
		case Check:
			ret = DustUtils.isEqual(val, curr);
			break;
		case Commit:
			if (curr instanceof DustHandle) {
				curr = resolveKnowledge((DustHandle) curr, createIfMissing);
			}
			if (curr instanceof Map) {
				ArrayList listeners = DustUtils.safeGet(curr, MIND_ATT_KNOWLEDGE_LISTENERS, null);
				if (null != listeners) {
					Object hTarget = DustUtils.safeGet(curr, MIND_ATT_KNOWLEDGE_HANDLE, null);
					Object oldAgent = Dust.access(MindAccess.Peek, null, null, MIND_ATT_DIALOG_ACTIVEAGENT);
					Dust.access(MindAccess.Set, null, null, MIND_ATT_DIALOG_ACTIVEAGENT);
					MindAction action = (val instanceof MindAction) ? (MindAction) val
							: DustUtilsEnumTranslator.getEnum((MindHandle) val, MindAction.Process);

					boolean stop = false;
					for (Object a : listeners) {
						try {
							if (a instanceof MindCommitFilter) {
								a = ((MindCommitFilter) a).getAgent(val);
								if (null == a) {
									continue;
								}
							}

							if (a == oldAgent) {
								// the committer may listen to the committed handle but should not be called now
								continue;
							}

							MindAgent agent = machine.selectAgent(a);
							if (null != agent) {
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

						if (stop) {
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
//			if (curr instanceof Set) {
//				prevColl = curr;
////				prevSet = (Set) curr;
//			}
			if (!DustUtils.isEqual(curr, val) && (null != prevColl)) {
				switch (collType) {
				case Arr:
					DustUtils.safePut((ArrayList) prevColl, (Integer) lastKey, val, false);
					break;
				case Map:
					Set s = (curr instanceof Set) ? (Set) curr : new HashSet();
					s.add(val);
					((Map) prevColl).put(lastKey, s);
					break;
				case One:
					break;
				case Set:
					((Set) prevColl).add(curr);
					break;
				}

//				if (null != prevArr) {
//					DustUtils.safePut(prevArr, (Integer) lastKey, val, false);
//				} else if (null != prevSet) {
//					prevSet.add(val);
//				} else {
//					Set s = new HashSet();
//					s.add(val);
//					prevMap.put(lastKey, s);
//				}
			}
			break;
		case Peek:
			ret = (null == curr) ? val : curr;
			break;
		case Reset:
			if (curr instanceof Map) {
				((Map) curr).clear();
			} else if (curr instanceof Collection) {
				((Collection) curr).clear();
			}
			break;
		case Set:
			if ((null != lastKey) && (null != prevColl)) {
				switch (collType) {
				case Arr:
					DustUtils.safePut((ArrayList) prevColl, (Integer) lastKey, val, true);
					break;
				case Map:
					if (!DustUtils.isEqual(curr, val)) {
						((Map) prevColl).put(lastKey, val);
					}
					break;
				case One:
					break;
				case Set:
					((Set) prevColl).add(curr);
					break;
				}

//				if (null != prevMap) {
//					if (!DustUtils.isEqual(curr, val)) {
//						prevMap.put(lastKey, val);
//					}
//				} else if (null != prevArr) {
//					DustUtils.safePut(prevArr, (Integer) lastKey, val, true);
//				}
			}

			break;
		case Broadcast:
			break;
		case Lookup:
			break;
		case Visit:
			doVisit((DustVisitor) val, hLastItem, lastKey, curr);
			break;
		}

		return (RetType) ret;
	}

	private void updateEnvInfo(Object kItem, Object kAtt, MindCollType collType, Object coll, Object val) {
		MindHandle hAsp = DustUtils.simpleGet(kAtt, MISC_ATT_CONN_PARENT);

		if ((null != hAsp) && (MIND_ASP_KNOWLEDGE != hAsp)) {
			Set as = DustUtils.safeGet(kItem, MIND_ATT_KNOWLEDGE_ASPECTS, SET_CREATOR);
			as.add(hAsp);
		}

		if ((null != kAtt) && (null != val)) {
			Map tm = DustUtils.safeGet(kAtt, MIND_ATT_KNOWLEDGE_TAGS, MAP_CREATOR);

			if (!tm.containsKey(MIND_TAG_VALTYPE)) {
				MindValType vt = DustUtils.getValType(val);
				MindHandle hVT = DustUtilsEnumTranslator.getHandle(vt);
				if (null != hVT) {
					tm.put(MIND_TAG_VALTYPE, hVT);
				}
			}

			if (!tm.containsKey(MIND_TAG_COLLTYPE)) {
				MindHandle hCT = (kItem == coll) ? MIND_TAG_COLLTYPE_ONE : DustUtilsEnumTranslator.getHandle(collType);
				if (null != hCT) {
					tm.put(MIND_TAG_COLLTYPE, hCT);
				}
			}
		}
	}

	private void doVisit(DustVisitor visitor, DustHandle hLastItem, Object lastKey, Object curr) {
		if (null == hLastItem) {
			return;
		}

		if ((null == curr) && (null != lastKey)) {
			return;
		}

		boolean visitRoot = (null == visitCtx);

		try {
			Object collection;
			MindHandle hAtt = null;
			if (curr instanceof DustHandle) {
				hLastItem = (DustHandle) curr;
				collection = resolveKnowledge(hLastItem, false);
			} else {
				hAtt = (MindHandle) lastKey;
				collection = curr;
			}

			if (visitRoot) {
				visitCtx = new DustMachineVisitContext(this);
			}
			visitCtx.visit(visitor, hLastItem, collection, hAtt);
		} finally {
			if (visitRoot) {
				visitCtx = null;
			}
		}
	}

}