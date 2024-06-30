package hu.sze.milab.dust.machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;

@SuppressWarnings({ "unchecked", "rawtypes" })
class DustMachineVisitContext extends DustVisitor.VisitContext implements DustMachineConsts {

	class MachineVisitItem implements DustVisitor.VisitItem {
		Object key;
		Object val;

		public MachineVisitItem(boolean isMap) {
			key = isMap ? null : -1;
		}

		public MachineVisitItem(MachineVisitItem prev) {
			key = prev.key;
		}

		@Override
		public Object getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return val;
		}

		@Override
		public String toString() {
			return "[" + key + "] = " + val;
		}
	}

	class MachineVisitInfo implements DustMachineConsts, DustVisitor.VisitInfo {

		final DustVisitor visitor;

		final MindHandle hItem;
		MindHandle hAtt;

		boolean isRoot;
		boolean isMap;
		Collection cs;
		Iterator it;
		MachineVisitItem item = null;

//		ArrayList<DustVisitor.VisitItem> removed;

		public MachineVisitInfo(DustVisitor v, MindHandle hItem, Object coll, MindHandle hAtt) {
			this.visitor = v;
			this.hItem = hItem;
			this.hAtt = hAtt;

			if (MIND_ATT_KNOWLEDGE_ASPECTS == hAtt) {
				DustDevUtils.breakpoint();
			}

			isMap = coll instanceof Map;
			cs = isMap ? ((Map) coll).entrySet() : (Collection) coll;
			it = new ArrayList(cs).iterator();
//			it = isMap ? ((Map) coll).entrySet().iterator() : ((Collection) coll).iterator();

			isRoot = isMap && (null == hAtt);
		}

		@Override
		public String toString() {
			return "VisitInfo [" + hItem + ":" + hAtt + "] -> " + item;
		}
		
		@Override
		public boolean isRoot() {
			return isRoot;
		}

		@Override
		public MindHandle getItemHandle() {
			return hItem;
		}

		@Override
		public MindHandle getAttHandle() {
			return isRoot ? getKey() : hAtt;
		}

		@Override
		public <RetType> RetType getKey() {
			return (RetType) item.key;
		}

		@Override
		public <RetType> RetType getValue() {
			return (RetType) item.val;
		}

		@Override
		public ArrayList<Object> getPath(ArrayList<Object> target) {
			if (null == target) {
				target = new ArrayList<Object>();
			} else {
				target.clear();
			}

			populatePath(target);

			return target;
		}

//		@Override
//		public void remove() {
//			if (null == removed) {
//				removed = new ArrayList<DustVisitor.VisitItem>();
//			}
//			removed.add(item);
//			item = new MachineVisitItem(item);
//
//			it.remove();
//		}
//
//		@Override
//		public Collection<DustVisitor.VisitItem> getRemoved(Collection<DustVisitor.VisitItem> target) {
//			if (null == target) {
//				target = (null == removed) ? Collections.EMPTY_LIST : new ArrayList<DustVisitor.VisitItem>();
//			} else {
//				target.clear();
//			}
//
//			if (null != removed) {
//				target.addAll(removed);
//			}
//
//			return target;
//		}

		MachineVisitInfo step() throws Exception {
			MachineVisitInfo ret = this;

			MindHandle hProcRet = MIND_TAG_RESULT_READACCEPT;
			
			if (null == item) {
				item = new MachineVisitItem(isMap);
				setVI(visitor, this);
				hProcRet = visitor.agentProcess(MindAction.Begin);
			}

			if (it.hasNext() && DustUtilsAttCache.getAtt(MachineAtts.CanContinue, hProcRet, false) ) {
				Object next = it.next();

				if (isMap) {
					Map.Entry<Object, Object> ne = (Entry<Object, Object>) next;
					item.key = ne.getKey();
					next = item.val = ne.getValue();
				} else {
					item.val = next;
					item.key = ((int) item.key) + 1;
				}

				if (MIND_ATT_UNIT_CONTENT == hAtt) {
					// do nothing!
				} else if ((next instanceof Map) || (next instanceof Collection)) {
					ret = new MachineVisitInfo(visitor, hItem, next, (null == hAtt) ? (MindHandle) item.key : hAtt);
//					ret = new MachineVisitInfo(visitor, hItem, next, hAtt);
				} else {
					setVI(visitor, this);
					hProcRet = visitor.agentProcess(MindAction.Process);

					if ((VisitFollowRef.No != visitor.followRef)
							&& DustUtilsAttCache.getAtt(MachineAtts.CanContinue, hProcRet, false) && (next instanceof MindHandle)) {
						MindHandle hNext = (MindHandle) next;
						if ((VisitFollowRef.Always == visitor.followRef) || shouldVisit(hNext)) {
							Map kNext = dialog.resolveKnowledge(hNext, false);
							if (null != kNext) {
								ret = new MachineVisitInfo(visitor, hNext, kNext, null);
							}
						}
					}
				}
			} else {
				setVI(visitor, this);
				visitor.agentProcess(MindAction.End);
				ret = null;
			}

			return ret;
		}
	}

	final DustMachineDialog dialog;

	MachineVisitInfo viCurrent;

	Stack<MachineVisitInfo> viStack = new Stack<MachineVisitInfo>();

	Set<MindHandle> skipRef = new HashSet();

	DustMachineVisitContext(DustMachineDialog dialog) {
		this.dialog = dialog;
	}

	void visit(DustVisitor visitor, MindHandle hItem, Object collection, MindHandle hAtt) {
		skipRef.add(hItem);
		MachineVisitInfo vi = new MachineVisitInfo(visitor, hItem, collection, hAtt);
		int depth = stepIn(vi);

		for (; null != vi;) {
			try {
				vi = viCurrent.step();

				if (null == vi) {
					vi = stepOut(depth);
				} else if (vi != viCurrent) {
					stepIn(vi);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
		}
	}

	private MachineVisitInfo stepOut(int depth) {
		if (viStack.isEmpty()) {
			viCurrent = null;
		} else {
			viCurrent = viStack.pop();
		}

		return (depth <= viStack.size()) ? viCurrent : null;
	}

	private int stepIn(MachineVisitInfo vi) {
		int depth = 0;

		if (null != viCurrent) {
			viStack.push(viCurrent);

			depth = viStack.size();
		}
		viCurrent = vi;

		return depth;
	}

	boolean shouldVisit(MindHandle h) {
		return skipRef.add(h);
	}

	void populatePath(ArrayList<Object> target) {
		// TODO Auto-generated method stub

	}
}
