package hu.sze.milab.dust.brain;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustConsts.MindAccess;
import hu.sze.milab.dust.DustConsts.MindHandle;
import hu.sze.milab.dust.DustMetaConsts;

public class DustBrainHandle implements MindHandle {

	@Override
	public String getID() {
		String id = Dust.access(this, MindAccess.Peek, null, DustMetaConsts.MIND_ATT_KNOWLEDGE_ID);
		
		if ( null == id ) {
			String lid = Dust.access(this, MindAccess.Peek, null, DustMetaConsts.TEXT_ATT_NAMED_NAME);
			String uid = Dust.access(this, MindAccess.Peek, null, DustMetaConsts.MIND_ATT_KNOWLEDGE_UNIT, DustMetaConsts.MIND_ATT_KNOWLEDGE_ID);
			
			id = uid + DustConsts.SEP_ID + lid;
			Dust.access(this, MindAccess.Set, id, DustMetaConsts.MIND_ATT_KNOWLEDGE_ID);
		}
		
		return id;
	}

	@Override
	public String toString() {
		return DustBrain.handleToString(this);
	}
}
