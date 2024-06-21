package hu.sze.milab.dust.dev.forge;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
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

		class GraphCfg {
			FontMetrics fntMet;
			int lblOffY;

			public GraphCfg(Component c, Graphics g) {
				fntMet = g.getFontMetrics(getFont());
				lblOffY = fntMet.getHeight();
			}
		}

		GraphCfg gCfg;

		class ItemShape {
			public final Shape shp;

			public final MindHandle hndl;

			String lbl;
			int lblOffX;

			public ItemShape(Shape shp, MindHandle hndl) {
				super();
				this.shp = shp;
				this.hndl = hndl;

				lbl = Dust.access(MindAccess.Peek, null, hndl, DEV_ATT_HINT);
				if (null != lbl) {
					lblOffX = -(gCfg.fntMet.stringWidth(lbl) / 2);
				}
			}

			boolean chkTag(MindHandle tag) {
				return DustDevUtils.chkTag(hndl, tag);
			}

			void draw(Graphics2D g) {
				g.draw(shp);

				if (null != lbl) {
					Rectangle rct = shp.getBounds();
					g.drawString(lbl, rct.x + lblOffX, rct.y + gCfg.lblOffY);
				}
			}
		}

		class GraphPanel extends JPanel {
			private static final long serialVersionUID = 1L;

			public GraphPanel() {
				super(null);
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (null == gCfg) {
					gCfg = new GraphCfg(this, g);
				}

				Graphics2D g2 = (Graphics2D) g;
				for (ItemShape is : shapes ) {
					is.draw(g2);
				}
			}
		}

		ArrayList<ItemShape> shapes = new ArrayList<>();

		EnumMap<DataGridType, TableModel> tms = new EnumMap<DataGridType, TableModel>(DataGridType.class);
		EnumMap<DataGridType, JTable> tbls = new EnumMap<DataGridType, JTable>(DataGridType.class);

		DefaultTableModel tmUnits;

		MindHandle hUnit;
		MindHandle hUnitGraph;

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

			shapes.clear();
			
			if (null != hUnitGraph) {
				Dust.access(MindAccess.Visit, new DustVisitor() {
					@Override
					protected MindHandle agentProcess() throws Exception {
						Object hEdge = getInfo().getValue();

						if (null != hEdge) {
							MindHandle src = Dust.access(MindAccess.Peek, null, hEdge, MISC_ATT_CONN_SOURCE, MISC_ATT_SHAPE_VECTORS,
									GEOMETRY_TAG_VECTOR_LOCATION);
							MindHandle target = Dust.access(MindAccess.Peek, null, hEdge, MISC_ATT_CONN_TARGET,
									MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION);

							int xS = Dust.access(MindAccess.Peek, 0, src, MISC_ATT_VECTOR_COORDINATES, 0);
							int yS = Dust.access(MindAccess.Peek, 0, src, MISC_ATT_VECTOR_COORDINATES, 1);

							int xT = Dust.access(MindAccess.Peek, 0, target, MISC_ATT_VECTOR_COORDINATES, 0);
							int yT = Dust.access(MindAccess.Peek, 0, target, MISC_ATT_VECTOR_COORDINATES, 1);

							shapes.add(new ItemShape(new Line2D.Double(xS, yS, xT, yT), (MindHandle) hEdge));
						}
						return MIND_TAG_RESULT_READACCEPT;
					}
				}, hUnitGraph, GEOMETRY_ATT_GRAPH_EDGES);

				Dust.access(MindAccess.Visit, new DustVisitor() {
					@Override
					protected MindHandle agentProcess() throws Exception {
						Object hNode = getInfo().getValue();

						if (null != hNode) {
							int x = Dust.access(MindAccess.Peek, 0, hNode, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
									MISC_ATT_VECTOR_COORDINATES, 0);
							int y = Dust.access(MindAccess.Peek, 0, hNode, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
									MISC_ATT_VECTOR_COORDINATES, 1);

							shapes.add(new ItemShape(new Ellipse2D.Double(x - 5, y - 5, 10, 10), (MindHandle) hNode));
						}
						return MIND_TAG_RESULT_READACCEPT;
					}
				}, hUnitGraph, GEOMETRY_ATT_GRAPH_NODES);
			}
			
			gp.invalidate();
			gp.repaint();

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
