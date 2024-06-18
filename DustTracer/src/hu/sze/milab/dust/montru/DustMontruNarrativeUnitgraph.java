package hu.sze.milab.dust.montru;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.dev.DustDevUtils;

public class DustMontruNarrativeUnitgraph extends DustVisitor implements DustMontruConsts {

	public DustMontruNarrativeUnitgraph() {
		super(VisitFollowRef.Once);
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		VisitInfo info = getInfo();

		MindHandle hUnit = info.getKey();
		MindHandle hGraph = info.getValue();
		
		Dust.access(MindAccess.Visit, new DustVisitor(VisitFollowRef.Once) {
			@Override
			protected MindHandle agentProcess() throws Exception {
				VisitInfo info = getInfo();

				MindHandle hAtt = info.getAttHandle();

				Object val = info.getValue();
				if (val instanceof MindHandle) {
					MindHandle hItemNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, val);

					if (null != hAtt) {
						MindHandle hParentLabel = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, info.getItemHandle());
						if (null != hParentLabel) {
							MindHandle hEdge = DustDevUtils.newHandle(hUnit, GEOMETRY_ASP_EDGE, "edge");
							Dust.access(MindAccess.Set, hParentLabel, hEdge, MISC_ATT_CONN_SOURCE);
							Dust.access(MindAccess.Set, hItemNode, hEdge, MISC_ATT_CONN_TARGET);
//							Dust.access(MindAccess.Set, hAtt, hEdge, GEOMETRY_ATT_EDGE_CLASS);
							DustDevUtils.setTag(hEdge, hAtt, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

							Dust.access(MindAccess.Insert, hEdge, hGraph, GEOMETRY_ATT_GRAPH_EDGES, KEY_ADD);
						}
					}
				}

				return MIND_TAG_RESULT_READACCEPT;
			}
		}, hUnit, MIND_ATT_UNIT_HANDLES);
		
		return MIND_TAG_RESULT_READACCEPT;
	}

}
