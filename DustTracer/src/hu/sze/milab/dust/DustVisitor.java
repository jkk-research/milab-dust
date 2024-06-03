package hu.sze.milab.dust;

import java.util.ArrayList;
import java.util.Collection;

public abstract class DustVisitor extends DustAgent {
	public static final int DEPTH_DEFAULT = 200;
	public static final int DEPTH_UNLIMITED = -1;
	
	public enum FollowRef {
		No, Once, Always
	}
	
	public interface VisitItem {
		Object getKey();
		Object getValue();
	}
	
	public interface VisitInfo extends VisitItem {
		ArrayList<Object> getPath(ArrayList<Object> target);
		
		MindHandle getItemHandle();
		MindHandle getAttHandle();
		
		void remove();
		Collection<DustVisitor.VisitItem> getRemoved(Collection<DustVisitor.VisitItem> removed);
	}
	
	public final FollowRef followRef;
	public final int maxDepth;
	
	private VisitInfo info;
	
	public DustVisitor() {
		this(FollowRef.No, DEPTH_DEFAULT);
	}
	
	public DustVisitor(FollowRef followRef) {
		this(followRef, DEPTH_DEFAULT);
	}
	
	public DustVisitor(FollowRef followRef, int maxDepth) {
		super();
		this.followRef = followRef;
		this.maxDepth = maxDepth;
	}

	protected final VisitInfo getInfo() {
		return info;
	}
	
	private VisitInfo setInfo(VisitInfo info) {
		VisitInfo i = this.info;
		this.info = info;
		
		return i;
	}
	
	public static abstract class VisitContext {
		protected VisitInfo setVI(DustVisitor visitor, VisitInfo info) {
			return visitor.setInfo(info);
		}
	}
}
