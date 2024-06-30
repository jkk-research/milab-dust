package hu.sze.milab.dust;

import java.util.ArrayList;

public abstract class DustVisitor extends DustAgent {
	public static final int DEPTH_DEFAULT = 200;
	public static final int DEPTH_UNLIMITED = -1;
		
	public interface VisitItem {
		<RetType> RetType getKey();
		<RetType> RetType getValue();
	}
	
	public interface VisitInfo extends VisitItem {
		ArrayList<Object> getPath(ArrayList<Object> target);
		
		MindHandle getItemHandle();
		MindHandle getAttHandle();

		boolean isRoot();
		
//		void remove();
//		Collection<DustVisitor.VisitItem> getRemoved(Collection<DustVisitor.VisitItem> removed);
	}
	
	public final VisitFollowRef followRef;
	public final int maxDepth;
	
	private VisitInfo info;
	
	public DustVisitor() {
		this(VisitFollowRef.No, DEPTH_DEFAULT);
	}
	
	public DustVisitor(VisitFollowRef followRef) {
		this(followRef, DEPTH_DEFAULT);
	}
	
	public DustVisitor(VisitFollowRef followRef, int maxDepth) {
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
		protected static VisitInfo setVI(DustVisitor visitor, VisitInfo info) {
			return visitor.setInfo(info);
		}
	}
}
