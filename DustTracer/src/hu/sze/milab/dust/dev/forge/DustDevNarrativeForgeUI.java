package hu.sze.milab.dust.dev.forge;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.montru.DustMontruConsts;
import hu.sze.milab.dust.montru.DustMontruSwingComps;
import hu.sze.milab.dust.montru.DustMontruUtils;

public class DustDevNarrativeForgeUI extends DustAgent implements DustMontruConsts, DustMontruSwingComps {

	enum DataGridType {
		Units, Aspects, Atts, SelectedData,
	}

	static class ForgeWrapper extends CompWrapper<ForgePanel> {
		public ForgeWrapper() {
			super(new ForgePanel());
		}
	}

	static DustCreator<ForgeWrapper> CREATOR = new DustCreator<ForgeWrapper>() {
		@Override
		public ForgeWrapper create(Object key, Object... hints) {
			ForgeWrapper ret = new ForgeWrapper();

			ForgePanel fp = ret.comp;

			Dust.access(MindAccess.Visit, new DustVisitor() {
				@Override
				protected MindHandle agentProcess() throws Exception {
					MindHandle hCnt = getInfo().getValue();

					int count = Dust.access(MindAccess.Peek, 0, hCnt, MIND_ATT_UNIT_HANDLES, KEY_SIZE);
					Dust.log(null, "Unit found", hCnt, "Item count", count);

					fp.tmUnits.addRow(new Object[] { hCnt, count });
					return MIND_TAG_RESULT_READACCEPT;
				}
			}, APP_UNIT, DUST_ATT_MACHINE_UNITS);

			return ret;
		}
	};

	static class ForgePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		class GraphPanel extends JPanel {
			private static final long serialVersionUID = 1L;

			public GraphPanel() {
				super(null);

//				setBackground(Color.yellow);
			}
		}

		EnumMap<DataGridType, TableModel> tms = new EnumMap<DataGridType, TableModel>(DataGridType.class);
		EnumMap<DataGridType, JTable> tbls = new EnumMap<DataGridType, JTable>(DataGridType.class);

		DefaultTableModel tmUnits;
		
		MindHandle hUnit;
		MindHandle hUnitGraph;
		
//		DustUtilsFactory<MindHandle, MindHandle> factUnitGraph = new DustUtilsFactory<MindHandle, MindHandle>(new DustCreator<MindHandle>() {
//			@Override
//			public MindHandle create(Object key, Object... hints) {
//				
//				MindHandle hGraph = DustDevUtils.newHandle(FORGE_UNIT, GEOMETRY_ASP_GRAPH, "Unit graph " + key);
//
//				
//				Dust.access(MindAccess.Visit, new DustVisitor(VisitFollowRef.Once) {
//					@Override
//					protected MindHandle agentProcess() throws Exception {
//						VisitInfo info = getInfo();
//
//						MindHandle hAtt = info.getAttHandle();
//
//						Object val = info.getValue();
//						if (val instanceof MindHandle) {
//							MindHandle hItemLabel = getItemLabel(gw, pnl, hUnit, (MindHandle) val);
//
//							if (null != hAtt) {
//								MindHandle hParentLabel = getItemLabel(gw, pnl, hUnit, info.getItemHandle());
//								if (null != hParentLabel) {
//									MindHandle hEdge = DustDevUtils.newHandle(hUnit, GEOMETRY_ASP_EDGE, "edge");
//									Dust.access(MindAccess.Set, hParentLabel, hEdge, MISC_ATT_CONN_SOURCE);
//									Dust.access(MindAccess.Set, hItemLabel, hEdge, MISC_ATT_CONN_TARGET);
////									Dust.access(MindAccess.Set, hAtt, hEdge, GEOMETRY_ATT_EDGE_CLASS);
//									DustDevUtils.setTag(hEdge, hAtt, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
//
//									Dust.access(MindAccess.Insert, hEdge, gw.hComp, GEOMETRY_ATT_GRAPH_EDGES, KEY_ADD);
//								}
//							} else {
//								DustDevUtils.breakpoint("heh");
//							}
//						}
//
//						return MIND_TAG_RESULT_READACCEPT;
//					}
//				}, key, MIND_ATT_UNIT_HANDLES);
//				
//				return hGraph;
//			}
//		});

		GraphPanel gp;

		public ForgePanel() {
			super(new BorderLayout());

			gp = new GraphPanel();
			gp.setBorder(new TitledBorder(LineBorder.createBlackLineBorder(), "Unit graph"));

			EnumMap<DataGridType, JComponent> tables = new EnumMap<DataGridType, JComponent>(DataGridType.class);

			for (DataGridType gt : DataGridType.values()) {
				JTable tbl = new JTable(null);
				tbls.put(gt, tbl);
				ListSelectionModel selModel = tbl.getSelectionModel();

				TableModel tm;
				RowSorter.SortKey defSort = null;

				switch (gt) {
				case Units:
					tmUnits = new DefaultTableModel(new Object[] { "Name", "Count" }, 0) {
						private static final long serialVersionUID = 1L;

						public Class<?> getColumnClass(int columnIndex) {
							switch (columnIndex) {
							case 1:
								return Integer.class;
							default:
								return Object.class;
							}
						};
					};

					tm = tmUnits;
					defSort = new RowSorter.SortKey(1, SortOrder.DESCENDING);
					selModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					selModel.addListSelectionListener(new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent e) {
							if (!e.getValueIsAdjusting()) {
								int idx = ((ListSelectionModel) e.getSource()).getMinSelectionIndex();
								int ii = tbls.get(DataGridType.Units).getRowSorter().convertRowIndexToModel(idx);
								selectUnit(ii);
							}
						}
					});
					break;
				default:
					tm = new MontruSwingTableModelTest(3, 5);
					break;
				}
				tms.put(gt, tm);

				tbl.setModel(tm);
				tbl.setAutoCreateRowSorter(true);

				if (null != defSort) {
					List<RowSorter.SortKey> sortKeys = new ArrayList<>();
					sortKeys.add(defSort);
					tbl.getRowSorter().setSortKeys(sortKeys);
				}

				JScrollPane scp = new JScrollPane(tbl);
				scp.setBorder(new TitledBorder(LineBorder.createBlackLineBorder(), gt.name()));
				tables.put(gt, scp);
			}

			JSplitPane spLB = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tables.get(DataGridType.Aspects),
					tables.get(DataGridType.Atts));
			spLB.setResizeWeight(0.5);
			spLB.setContinuousLayout(true);

			JSplitPane spLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tables.get(DataGridType.Units), spLB);
			spLB.setResizeWeight(0.2);
			spLB.setContinuousLayout(true);

			JSplitPane spRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gp, tables.get(DataGridType.SelectedData));
			spLB.setResizeWeight(0.2);
			spLB.setContinuousLayout(true);

			JSplitPane spMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spLeft, spRight);
			spMain.setResizeWeight(0.2);
			spMain.setContinuousLayout(true);
			add(spMain, BorderLayout.CENTER);

			setPreferredSize(new Dimension(1000, 800));

			spLeft.setDividerLocation(200);
			spLB.setDividerLocation(300);
			spRight.setDividerLocation(600);
			spMain.setDividerLocation(300);
		}

		protected void selectUnit(int idx) {
			hUnit = (MindHandle) tmUnits.getValueAt(idx, 0);
			
			MindHandle hComp = DustMontruUtils.getItemHandle(gp);

			hUnitGraph = Dust.access(MindAccess.Get, null, hComp, MISC_ATT_CONN_MEMBERMAP, hUnit);

			Dust.log(null, "Unit selected", hUnit);
		}
	}

	@Override
	protected MindHandle agentInit() throws Exception {
		@SuppressWarnings("unused")
		ForgeWrapper fp = DustDevUtils.getImplOb(CREATOR, "");

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		return super.agentProcess();
	}

}
