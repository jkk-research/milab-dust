package hu.sze.milab.dust.machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import hu.sze.milab.dust.DustVisitor;

@SuppressWarnings({ "unchecked", "rawtypes" })
class DustMachineVisitContext extends DustVisitor.VisitContext implements DustMachineConsts {
	
	class MachineVisitItem implements DustVisitor.VisitItem {
		Object key;
		Object val;

		@Override
		public Object getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return val;
		}
	}

	class MachineVisitInfo implements DustMachineConsts, DustVisitor.VisitInfo {
		final DustVisitor visitor;
		
		final MindHandle hItem;
		MindHandle hAtt;

		Iterator it;
		MachineVisitItem item = new MachineVisitItem();

		ArrayList<DustVisitor.VisitItem> removed;
		
		public MachineVisitInfo(DustVisitor v, MindHandle hItem, Object coll, MindHandle hAtt) {
			this.visitor = v;
			this.hItem = hItem;
			this.hAtt = hAtt;
			it = ( coll instanceof Map ) ? ((Map)coll).entrySet().iterator() : ((Collection)coll).iterator();
		}

		@Override
		public MindHandle getItemHandle() {
			return hItem;
		}

		@Override
		public MindHandle getAttHandle() {
			return hAtt;
		}

		@Override
		public Object getKey() {
			return item.key;
		}

		@Override
		public Object getValue() {
			return item.val;
		}

		@Override
		public ArrayList<Object> getPath(ArrayList<Object> target) {
			if ( null == target ) {
				target = new ArrayList<Object>();
			} else {
				target.clear();
			}
			
			populatePath(target);
			
			return target;
		}
		
		@Override
		public void remove() {
			if (null == removed) {
				removed = new ArrayList<DustVisitor.VisitItem>();
			}
			removed.add(item);
			item = new MachineVisitItem();

			it.remove();
		}

		@Override
		public Collection<DustVisitor.VisitItem> getRemoved(Collection<DustVisitor.VisitItem> target) {
			if (null == target) {
				target = (null == removed) ? Collections.EMPTY_LIST : new ArrayList<DustVisitor.VisitItem>();
			} else {
				target.clear();
			}

			if (null != removed) {
				target.addAll(removed);
			}

			return target;
		}

		MachineVisitInfo step() {
			return this;
		}
	}

	public void populatePath(ArrayList<Object> target) {
		// TODO Auto-generated method stub
		
	}
}
