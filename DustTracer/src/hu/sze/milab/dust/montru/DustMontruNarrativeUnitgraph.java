package hu.sze.milab.dust.montru;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;

public class DustMontruNarrativeUnitgraph extends DustAgent implements DustMontruConsts {

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
				
				String nodeKey = "Node " + hItem;
				Dust.access(MindAccess.Set, nodeKey, hItemNode, DEV_ATT_HINT);
				

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
