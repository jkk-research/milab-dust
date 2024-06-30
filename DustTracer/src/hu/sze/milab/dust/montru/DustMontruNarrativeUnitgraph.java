package hu.sze.milab.dust.montru;

import java.util.Set;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustMontruNarrativeUnitgraph extends DustAgent implements DustMontruConsts {

	static int gridX = 100;
	static int gridY = 40;
	static int width = 600;

	static int nextX = gridX / 2;
	static int nextY = gridY / 2;

	public static void reset() {
		nextX = gridX / 2;
		nextY = gridY / 2;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {

		MindHandle hUnit = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_VISITKEY);
		MindHandle hGraph = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_VISITVALUE);

		DustUtilsFactory<MindHandle, Set> factItemAtts = new DustUtilsFactory(SET_CREATOR);
		DustUtilsFactory<MindHandle, Set> factItemKeys = new DustUtilsFactory(SET_CREATOR);

		Dust.access(MindAccess.Visit, new DustVisitor(VisitFollowRef.Once) {
			@Override
			protected MindHandle agentBegin() throws Exception {
				VisitInfo info = getInfo();

				MindHandle hItem = info.getItemHandle();
				optCreateGraphNode(hGraph, hItem);
				boolean read = (hItem == hUnit)
						|| !(boolean) Dust.access(MindAccess.Check, MIND_ASP_UNIT, hItem, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

				if (read) {
					return MIND_TAG_RESULT_READACCEPT;
				} else {
					return MIND_TAG_RESULT_PASS;
				}
			}

			@Override
			protected MindHandle agentEnd() throws Exception {
				VisitInfo info = getInfo();

				MindHandle hItem = info.getItemHandle();
				MindHandle hItemNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, hItem);

				String nodeKey = hItem.toString();

				Dust.log(EVENT_TAG_TYPE_TRACE, "End", nextX, nextY, hItem, nodeKey, hItemNode);

				return MIND_TAG_RESULT_ACCEPT;
			}

			@Override
			protected MindHandle agentProcess() throws Exception {
				VisitInfo info = getInfo();

				MindHandle hItem = info.getItemHandle();
				MindHandle hAtt = info.getAttHandle();

				if (null == hAtt) {
					Dust.log(EVENT_TAG_TYPE_TRACE, " --- No attribute in process", hItem);
				} else if (!DustUtilsAttCache.getAtt(MachineAtts.TransientAtt, hAtt, false)) {

					Object key = info.getKey();
					Object val = info.getValue();

					MindHandle hItemNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, hItem);
					MindHandle hTargetNode;

					if (val instanceof MindHandle) {
						String idx = DustUtils.isEqual(hAtt, key) ? hAtt.toString() : hAtt + ":" + key;
						String edgeKey = hItem + "[" + idx + "] -> " + val;

						hTargetNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, val);
						MindHandle hEdge = addEdge(hUnit, hGraph, hItemNode, hTargetNode, MONTRU_TAG_UNITGRAPHEDGE_REF, edgeKey);

						DustDevUtils.setTag(hEdge, hAtt, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
					}

					if (factItemAtts.get(hItem).add(hAtt)) {
						hTargetNode = optCreateGraphNode(hGraph, hAtt);

//						hTargetNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, hAtt);
						addEdge(hUnit, hGraph, hItemNode, hTargetNode, MONTRU_TAG_UNITGRAPHEDGE_ATT, "att");
					}

					if ((key instanceof MindHandle) && !info.isRoot() && factItemKeys.get(hItem).add(key)) {
						hTargetNode = optCreateGraphNode(hGraph, (MindHandle) key);
//						hTargetNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, key);
						addEdge(hUnit, hGraph, hItemNode, hTargetNode, MONTRU_TAG_UNITGRAPHEDGE_KEY, "key");
					}
				}
				return MIND_TAG_RESULT_READACCEPT;
			}

		}, hUnit, MIND_ATT_UNIT_HANDLES);

		return MIND_TAG_RESULT_READACCEPT;
	}

	private MindHandle addEdge(MindHandle hUnit, MindHandle hGraph, MindHandle hSourceNode, MindHandle hTargetNode,
			MindHandle hEdgeType, String edgeKey) {
		MindHandle hEdge = DustDevUtils.newHandle(hUnit, GEOMETRY_ASP_EDGE, edgeKey);

		Dust.access(MindAccess.Set, hSourceNode, hEdge, MISC_ATT_CONN_SOURCE);
		Dust.access(MindAccess.Set, hTargetNode, hEdge, MISC_ATT_CONN_TARGET);
		DustDevUtils.setTag(hEdge, hEdgeType, MONTRU_TAG_UNITGRAPHEDGE);

		Dust.access(MindAccess.Insert, hEdge, hGraph, GEOMETRY_ATT_GRAPH_EDGES, KEY_ADD);

		Dust.log(EVENT_TAG_TYPE_TRACE, "Edge", edgeKey, hEdge);
		return hEdge;
	}

	private MindHandle optCreateGraphNode(MindHandle hGraph, MindHandle hItem) {
		if (null == hItem) {
			Dust.log(EVENT_TAG_TYPE_WARNING, "null item in optCreateGraphNode");
			return null;
		}

		MindHandle hItemNode = Dust.access(MindAccess.Get, null, hGraph, MISC_ATT_CONN_MEMBERMAP, hItem);

		if (null == Dust.access(MindAccess.Peek, null, hItemNode, MISC_ATT_CONN_OWNER)) {
			String nodeKey = hItem.toString();
			Dust.access(MindAccess.Set, hItem, hItemNode, MISC_ATT_CONN_OWNER);
			Dust.access(MindAccess.Set, nodeKey, hItemNode, DEV_ATT_HINT);

			Dust.access(MindAccess.Set, nextX, hItemNode, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
					MISC_ATT_VECTOR_COORDINATES, 0);
			Dust.access(MindAccess.Set, nextY, hItemNode, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
					MISC_ATT_VECTOR_COORDINATES, 1);

			Dust.access(MindAccess.Insert, hItemNode, hGraph, GEOMETRY_ATT_GRAPH_NODES, KEY_ADD);

			Dust.log(EVENT_TAG_TYPE_TRACE, "Begin", nextX, nextY, hItem, hItemNode);

			nextX += gridX;

			if (nextX > width) {
				nextX = gridX / 2;
				nextY += gridY;
			}
		} else {
			DustDevUtils.breakpoint("Repeating graph item", hItem, hItemNode);
		}
		
		return hItemNode;
	}

}
