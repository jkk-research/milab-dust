package hu.sze.milab.dust.montru;

import java.awt.Component;

import javax.swing.JComponent;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustHandles;
import hu.sze.milab.dust.utils.DustUtilsConsts;

public interface DustMontruConsts extends DustHandles, DustMontruHandles, DustUtilsConsts {
	
	Object SWING_ITEM_HANDLE = new Object();

	public static class CompWrapper<CompType extends Component> {
		public final MindHandle hComp;
		public final CompType comp;
		
		public CompWrapper(CompType c) {
			this.comp = c;
			hComp = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF);
			((JComponent)c).putClientProperty(SWING_ITEM_HANDLE, hComp);
		}
	}
	
	
}
