package hu.sze.milab.dust.montru;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;

public class DustMontruNarrativeUnitgraph extends DustAgent implements DustMontruConsts {
	
	int gridX = 100;
	int gridY = 40;
	int width = 600;
	int rowOff = 1;

	
	int nextX = gridX / 2;
	int nextY = gridY / 2;

	@Override
	protected MindHandle agentProcess() throws Exception {

		MindHandle hUnit = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_VISITKEY);
		MindHandle hGraph = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_VISITVALUE);
		

		Dust.access(MindAccess.Visit, new DustVisitor(VisitFollowRef.Once) {
			@Override
			protected MindHandle agentBegin() throws Exception {
				VisitInfo info = getInfo();

				MindHandle hItem = info.getItemHandle();
				MindHandle hItemNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, hItem);
				
				String nodeKey = hItem.toString();
				Dust.access(MindAccess.Set, hItem, hItemNode, MISC_ATT_CONN_OWNER);
				Dust.access(MindAccess.Set, nodeKey, hItemNode, DEV_ATT_HINT);
				
				Dust.access(MindAccess.Insert, nextX, hItemNode, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION, MISC_ATT_VECTOR_COORDINATES, KEY_ADD);
				Dust.access(MindAccess.Insert, nextY, hItemNode, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION, MISC_ATT_VECTOR_COORDINATES, KEY_ADD);
				
				nextX += gridX;
				nextY += 10;
				
				if ( nextX > width ) {
					++rowOff;
					nextX = gridX / 2;
					nextY = rowOff * gridY;
				}

				Dust.access(MindAccess.Insert, hItemNode, hGraph, GEOMETRY_ATT_GRAPH_NODES, KEY_ADD);

				Dust.log(EVENT_TAG_TYPE_TRACE, "Begin", hItem, nodeKey, hItemNode);
				return MIND_TAG_RESULT_ACCEPT;
			}

			@Override
			protected MindHandle agentProcess() throws Exception {
				VisitInfo info = getInfo();

				MindHandle hItem = info.getItemHandle();
				MindHandle hAtt = info.getAttHandle();

				if (null == hAtt) {
					Dust.log(EVENT_TAG_TYPE_TRACE, " --- Begin", hItem);
				} else if (!DustUtilsAttCache.getAtt(MachineAtts.TransientAtt, hAtt, false)) {

					Object key = info.getKey();
					Object val = info.getValue();

//					Dust.log(EVENT_TAG_TYPE_TRACE, hItem, hAtt, key, val);

					if ((null != hAtt) && (val instanceof MindHandle)) {
						MindHandle hItemNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, hItem);
						MindHandle hTargetNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, val);
						
						String idx = DustUtils.isEqual(hAtt, key) ? hAtt.toString() : hAtt + ":" + key;
						
						String edgeKey = hItem + "[" + idx + "] -> " + val;

						MindHandle hEdge = DustDevUtils.newHandle(hUnit, GEOMETRY_ASP_EDGE, edgeKey);
						Dust.access(MindAccess.Set, hItemNode, hEdge, MISC_ATT_CONN_SOURCE);
						Dust.access(MindAccess.Set, hTargetNode, hEdge, MISC_ATT_CONN_TARGET);
						DustDevUtils.setTag(hEdge, hAtt, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

						Dust.access(MindAccess.Insert, hEdge, hGraph, GEOMETRY_ATT_GRAPH_EDGES, KEY_ADD);
						
						Dust.log(EVENT_TAG_TYPE_TRACE, "Edge", edgeKey, hEdge);
					}
				}
				return MIND_TAG_RESULT_READACCEPT;
			}
		}, hUnit, MIND_ATT_UNIT_HANDLES);

		return MIND_TAG_RESULT_READACCEPT;
	}

}
