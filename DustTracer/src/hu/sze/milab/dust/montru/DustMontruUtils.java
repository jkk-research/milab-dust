package hu.sze.milab.dust.montru;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;

import hu.sze.milab.dust.Dust;

public class DustMontruUtils implements DustMontruConsts {

	public static MindHandle getItemHandle(JComponent comp) {
		return (MindHandle) comp.getClientProperty(SWING_ITEM_HANDLE);
	}
	
	public static void setBounds(Component comp) {
		setBounds(comp, MIND_TAG_CONTEXT_SELF);
	}
	
	public static void gridLayout(Container c, Dimension dPnl) {
		gridLayout(c, dPnl, null);
	}
	
	public static void gridLayout(Container c, Dimension dPnl, Double zoomFactor) {

		int cc = c.getComponentCount();

		Dimension md = null;
		Dimension d = new Dimension();

		for (int i = 0; i < cc; ++i) {
			c.getComponent(i).getSize(d);

			if (null == md) {
				md = new Dimension(d);
			} else {
				if (d.height > md.height) {
					md.height = d.height;
				}
				if (d.width > md.width) {
					md.width = d.width;
				}
			}
		}

		md.height += 10;
		md.width += 10;
		int dx = md.width / 2;

		int pw = dPnl.width;
		if (null != zoomFactor) {
			pw = (int) ((double) pw / zoomFactor);
		}

		Point ptChild = null;

		for (int i = 0; i < cc; ++i) {
			Component comp = c.getComponent(i);

			comp.getSize(d);

			if (null == ptChild) {
				ptChild = new Point(md.width / 2, md.height / 2);
			} else {
				ptChild.x += md.width;

				if ((ptChild.x + dx) > pw) {
					ptChild.x = md.width / 2;
					ptChild.y += md.height;
				}
			}

			comp.setLocation(ptChild.x - (d.width / 2), ptChild.y - (d.height / 2));
		}

		dPnl.height = ptChild.y + (md.height / 2);
		if (null != zoomFactor) {
			dPnl.height = (int) ((double) dPnl.height * zoomFactor);
		}

		c.setPreferredSize(dPnl);
	}
	
	public static void setBounds(Component comp, MindHandle hArea) {
		
		Number x = Dust.access(MindAccess.Peek, 10, hArea, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION, 0);
		Number y = Dust.access(MindAccess.Peek, 10, hArea, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION, 1);
		Number w = Dust.access(MindAccess.Peek, 800, hArea, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_SIZE, 0);
		Number h = Dust.access(MindAccess.Peek, 400, hArea, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_SIZE, 1);
		
		comp.setBounds(x.intValue(), y.intValue(), w.intValue(), h.intValue());
	}
	
}
