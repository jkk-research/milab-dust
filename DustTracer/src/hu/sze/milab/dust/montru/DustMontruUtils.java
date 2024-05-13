package hu.sze.milab.dust.montru;

import java.awt.Component;

import hu.sze.milab.dust.Dust;

public class DustMontruUtils implements DustMontruConsts {

	public static void setBounds(Component comp) {
		setBounds(comp, MIND_TAG_CONTEXT_SELF);
	}
	
	public static void setBounds(Component comp, MindHandle hArea) {
		
		Number x = Dust.access(MindAccess.Peek, 10, hArea, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION, 0);
		Number y = Dust.access(MindAccess.Peek, 10, hArea, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION, 1);
		Number w = Dust.access(MindAccess.Peek, 800, hArea, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_SIZE, 0);
		Number h = Dust.access(MindAccess.Peek, 400, hArea, MONTRU_ATT_AREA_VECTORS, GEOMETRY_TAG_VECTOR_SIZE, 1);
		
		comp.setBounds(x.intValue(), y.intValue(), w.intValue(), h.intValue());
	}
}
