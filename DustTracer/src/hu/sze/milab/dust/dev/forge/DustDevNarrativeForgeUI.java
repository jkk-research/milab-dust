package hu.sze.milab.dust.dev.forge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.dev.forge.DustDevNarrativeForgeUI.ForgePanel.GridDataModel;
import hu.sze.milab.dust.montru.DustMontruConsts;
import hu.sze.milab.dust.montru.DustMontruNarrativeUnitgraph;
import hu.sze.milab.dust.montru.DustMontruSwingComps;
import hu.sze.milab.dust.montru.DustMontruUtils;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustDevNarrativeForgeUI extends DustAgent implements DustMontruConsts, DustMontruSwingComps {

	enum GridCol {
		ID, Name, Count, Unit, Color, Show, Value, PrimAsp,
	}

	enum DataGridType {
		Units(1, SortOrder.DESCENDING, GridCol.Name, GridCol.Count), Aspects(GridCol.Name, GridCol.Unit, GridCol.Show, GridCol.Color),
		Attributes(GridCol.Name, GridCol.Unit, GridCol.Show, GridCol.Color),
		Focused(GridCol.Name, GridCol.Unit, GridCol.Value), Items(GridCol.ID, GridCol.Name, GridCol.Unit, GridCol.PrimAsp),

		;

		public final int defSortColIdx;
		public final SortOrder defSortOrder;
		public final GridCol colNames[];

		private DataGridType(GridCol... uc) {
			this(0, SortOrder.ASCENDING, uc);
		}

		private DataGridType(int defSortColIdx, SortOrder defSortOrder, GridCol... uc) {
			this.defSortColIdx = defSortColIdx;
			this.defSortOrder = defSortOrder;
			this.colNames = uc;
		}
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
			GridDataModel unitModel = ret.comp.tms.get(DataGridType.Units);

			Dust.access(MindAccess.Visit, new DustVisitor() {
				@Override
				protected MindHandle agentProcess() throws Exception {
					MindHandle hCnt = getInfo().getValue();
					unitModel.optAdd(hCnt);
					return MIND_TAG_RESULT_READACCEPT;
				}
			}, APP_UNIT, DUST_ATT_MACHINE_UNITS);
			
			unitModel.updated();

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

		abstract class ItemShape<ShapeClass extends Shape> {
			public final MindHandle hndl;
			public final boolean moveable;
			public final ShapeClass shp;

			protected boolean filled = false;

			public final Set<ItemShape<?>> conn = new HashSet<>();

			boolean focused;
			boolean selected;

			String lbl;
			int lblOffX;

			protected ItemShape(MindHandle hndl, ShapeClass shp, boolean moveable) {
				this.hndl = hndl;
				this.shp = shp;
				this.moveable = moveable;
				updateShape();
			}

			protected void connect(MindHandle h) {
				ItemShape is = shapes.get(h);
				if (null == is) {
					DustDevUtils.breakpoint("NO shape for item", h);
					return;
				}
				conn.add(is);
				is.conn.add(this);
			}

			public void updateShape() {
				lbl = Dust.access(MindAccess.Peek, null, hndl, DEV_ATT_HINT);
				if (null != lbl) {
					if (lbl.contains("[")) {
						lbl = DustUtils.cutPostfix(lbl, "]");
						lbl = DustUtils.getPostfix(lbl, "[");
					}
					lblOffX = -(gCfg.fntMet.stringWidth(lbl) / 2);
				}
			}

			public void moveShape(int x, int y) {
				updateShape();
			}

			boolean chkTag(MindHandle tag) {
				return DustDevUtils.chkTag(hndl, tag);
			}

			void draw(Graphics2D g) {
				if (filled) {
					g.fill(shp);
				} else {
					g.draw(shp);
				}

				if (null != lbl) {
					Rectangle rct = shp.getBounds();
					g.drawString(lbl, (int) rct.getCenterX() + lblOffX, (int) rct.getCenterY() + gCfg.lblOffY);
				}
			}
		}

		public class ItemShapeEdge extends ItemShape<Line2D.Double> {
			protected ItemShapeEdge(MindHandle hndl) {
				super(hndl, new Line2D.Double(), false);

				connect(Dust.access(MindAccess.Peek, null, hndl, MISC_ATT_CONN_SOURCE));
				connect(Dust.access(MindAccess.Peek, null, hndl, MISC_ATT_CONN_TARGET));
			}

			@Override
			public void updateShape() {
				MindHandle src = Dust.access(MindAccess.Peek, null, hndl, MISC_ATT_CONN_SOURCE, MISC_ATT_SHAPE_VECTORS,
						GEOMETRY_TAG_VECTOR_LOCATION);
				MindHandle target = Dust.access(MindAccess.Peek, null, hndl, MISC_ATT_CONN_TARGET, MISC_ATT_SHAPE_VECTORS,
						GEOMETRY_TAG_VECTOR_LOCATION);

				int xS = Dust.access(MindAccess.Peek, 0, src, MISC_ATT_VECTOR_COORDINATES, 0);
				int yS = Dust.access(MindAccess.Peek, 0, src, MISC_ATT_VECTOR_COORDINATES, 1);

				int xT = Dust.access(MindAccess.Peek, 0, target, MISC_ATT_VECTOR_COORDINATES, 0);
				int yT = Dust.access(MindAccess.Peek, 0, target, MISC_ATT_VECTOR_COORDINATES, 1);

				shp.setLine(xS, yS, xT, yT);

				super.updateShape();
			}
		}

		public class ItemShapeLoop extends ItemShape<Ellipse2D.Double> {
			protected ItemShapeLoop(MindHandle hndl) {
				super(hndl, new Ellipse2D.Double(), false);

				connect(Dust.access(MindAccess.Peek, null, hndl, MISC_ATT_CONN_SOURCE));
			}

			@Override
			public void updateShape() {
				int x = Dust.access(MindAccess.Peek, 0, hndl, MISC_ATT_CONN_SOURCE, MISC_ATT_SHAPE_VECTORS,
						GEOMETRY_TAG_VECTOR_LOCATION, MISC_ATT_VECTOR_COORDINATES, 0);
				int y = Dust.access(MindAccess.Peek, 0, hndl, MISC_ATT_CONN_SOURCE, MISC_ATT_SHAPE_VECTORS,
						GEOMETRY_TAG_VECTOR_LOCATION, MISC_ATT_VECTOR_COORDINATES, 1);

				shp.setFrame(x - 5, y - 5, 10, 100);

				super.updateShape();
			}
		}

		public class ItemShapeNode extends ItemShape<Ellipse2D.Double> {
			protected ItemShapeNode(MindHandle hndl) {
				super(hndl, new Ellipse2D.Double(), true);

				filled = Dust.access(MindAccess.Check, hUnit, hndl, MISC_ATT_CONN_OWNER, MIND_ATT_KNOWLEDGE_UNIT);
			}

			@Override
			public void updateShape() {
				int x = Dust.access(MindAccess.Peek, 0, hndl, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
						MISC_ATT_VECTOR_COORDINATES, 0);
				int y = Dust.access(MindAccess.Peek, 0, hndl, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
						MISC_ATT_VECTOR_COORDINATES, 1);

				shp.setFrame(x - 5, y - 5, 10, 10);
				super.updateShape();
			}

			@Override
			public void moveShape(int x, int y) {
				int origX = Dust.access(MindAccess.Peek, 0, hndl, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
						MISC_ATT_VECTOR_COORDINATES, 0);
				int origY = Dust.access(MindAccess.Peek, 0, hndl, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
						MISC_ATT_VECTOR_COORDINATES, 1);

				Dust.access(MindAccess.Set, origX + x, hndl, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
						MISC_ATT_VECTOR_COORDINATES, 0);
				Dust.access(MindAccess.Set, origY + y, hndl, MISC_ATT_SHAPE_VECTORS, GEOMETRY_TAG_VECTOR_LOCATION,
						MISC_ATT_VECTOR_COORDINATES, 1);

				super.moveShape(x, y);
			}
		}

		class GridDataModel extends AbstractTableModel implements ListSelectionListener {
			private static final long serialVersionUID = 1L;

			public final DataGridType type;

			private final ArrayList<MindHandle> rows = new ArrayList<MindHandle>();
			private final DustUtilsFactory extData = new DustUtilsFactory(MAP_CREATOR);

			public GridDataModel(DataGridType type) {
				this.type = type;
			}

			public void reset() {
				rows.clear();
				extData.clear();
			}

			public void updated() {
				fireTableDataChanged();
			}

			public int optAdd(MindHandle h) {
				int ret = rows.indexOf(h);

				if (-1 == ret) {
					ret = rows.size();
					rows.add(h);
				}

				return ret;
			}

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int idx = ((ListSelectionModel) e.getSource()).getMinSelectionIndex();
					int ii = tbls.get(type).getRowSorter().convertRowIndexToModel(idx);
					MindHandle h = rows.get(ii);

					switch (type) {
					case Units:
						selectUnit(h);
						break;
					default:
						break;
					}
				}
			}

			@Override
			public int getRowCount() {
				return rows.size();
			}

			@Override
			public int getColumnCount() {
				return type.colNames.length;
			}

			@Override
			public String getColumnName(int column) {
				return type.colNames[column].name();
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				Class<?> ret = Object.class;

				GridCol gc = type.colNames[columnIndex];

				switch (gc) {
				case Color:
					break;
				case Count:
					ret = Integer.class;
					break;
				case ID:
					break;
				case Name:
					break;
				case PrimAsp:
					break;
				case Show:
					ret = Boolean.class;
					break;
				case Unit:
					break;
				case Value:
					break;
				}

				return ret;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object ret = null;

				GridCol gc = type.colNames[columnIndex];
				MindHandle h = rows.get(rowIndex);

				switch (gc) {
				case Show:
				case Color:
					ret = DustUtils.simpleGet(extData.peek(h), gc);
					break;
				case Count:
					ret = Dust.access(MindAccess.Peek, 0, h, MIND_ATT_UNIT_HANDLES, KEY_SIZE);
					break;
				case ID:
					ret = h.getId();
					break;
				case Name:
					ret = h.toString();
					break;
				case PrimAsp:
					ret = Dust.access(MindAccess.Peek, 0, h, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
					break;
				case Unit:
					ret = Dust.access(MindAccess.Peek, 0, h, MIND_ATT_KNOWLEDGE_UNIT);
					break;
				case Value:
					break;
				}

				return ret;
			}
		}

		class MouseMonitor extends MouseInputAdapter {

			public void follow(JComponent comp) {
				comp.addMouseListener(this);
				comp.addMouseWheelListener(this);
				comp.addMouseMotionListener(this);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				selectFocused(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				selectFocused(e);
				dragFrom = e.getPoint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				dragTo = e.getPoint();
				moveSelected();

				gp.invalidate();
				gp.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Point mpos = e.getPoint();
				int r = 4;
				int d = 2 * r;

				int mc = Cursor.DEFAULT_CURSOR;

				if (null != mpos) {
					Rectangle2D ht = new Rectangle2D.Double(mpos.x - r, mpos.y - r, d, d);
					Set<ItemShape> hit = new HashSet<>();
					for (ItemShape is : shapes.values()) {
						if (is.shp.intersects(ht)) {
							hit.add(is);

							if (is.moveable) {
								mc = Cursor.MOVE_CURSOR;

								for (Object conn : is.conn) {
									hit.add((ItemShape) conn);
								}
							}
						}
					}

					if (!hit.equals(focused)) {
						focused.clear();
						focused.addAll(hit);

						gp.invalidate();
						gp.repaint();

						Cursor cc = Cursor.getPredefinedCursor(mc);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								Component comp = ForgePanel.this;
								comp.setCursor(cc);
							}
						});

//						Dust.log(EVENT_TAG_TYPE_TRACE, "Set cursor", mc, cc);
					}
				}
			}
		}

		class GraphPanel extends JPanel {
			private static final long serialVersionUID = 1L;

			MouseMonitor mm = new MouseMonitor();

			public GraphPanel() {
				super(null);
				mm.follow(this);
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (null == gCfg) {
					gCfg = new GraphCfg(this, g);
				}

				Graphics2D g2 = (Graphics2D) g;

				Map rh = new HashMap();
				rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHints(rh);

				Color c = g2.getColor();
				for (ItemShape is : shapes.values()) {
					if (is.selected || focused.contains(is)) {
						continue;
					}
					is.draw(g2);
				}

				g2.setColor(Color.BLUE);
				for (ItemShape is : shapes.values()) {
					if (is.selected && !focused.contains(is)) {
						is.draw(g2);
					}
				}

				g2.setColor(Color.MAGENTA);
				for (ItemShape is : focused) {
					is.draw(g2);
				}

				g2.setColor(c);
			}
		}

		EnumMap<DataGridType, GridDataModel> tms = new EnumMap<DataGridType, GridDataModel>(DataGridType.class);
		EnumMap<DataGridType, JTable> tbls = new EnumMap<DataGridType, JTable>(DataGridType.class);

		DefaultTableModel tmUnitsX;

		MindHandle hUnit;
		MindHandle hUnitGraph;

		GraphPanel gp;

		Map<MindHandle, ItemShape> shapes = new HashMap<>();
		Set<ItemShape> focused = new HashSet<>();

		Point dragFrom;
		Point dragTo;

		public ForgePanel() {
			super(new BorderLayout());

			gp = new GraphPanel();
			gp.setBorder(new TitledBorder(LineBorder.createBlackLineBorder(), "Unit graph"));

			EnumMap<DataGridType, JComponent> tables = new EnumMap<DataGridType, JComponent>(DataGridType.class);

			for (DataGridType gt : DataGridType.values()) {
				GridDataModel tm = new GridDataModel(gt);
				tms.put(gt, tm);

				JTable tbl = new JTable(tm);
				tbls.put(gt, tbl);
				tbl.setAutoCreateRowSorter(true);

				RowSorter.SortKey defSort = new RowSorter.SortKey(gt.defSortColIdx, gt.defSortOrder);
				List<RowSorter.SortKey> sortKeys = new ArrayList<>();
				sortKeys.add(defSort);
				tbl.getRowSorter().setSortKeys(sortKeys);

				ListSelectionModel selModel = tbl.getSelectionModel();
				selModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				selModel.addListSelectionListener(tm);

				JScrollPane scp = new JScrollPane(tbl);
				scp.setBorder(new TitledBorder(LineBorder.createBlackLineBorder(), gt.name()));
				tables.put(gt, scp);
			}

			JSplitPane spLB = DustMontruUtils.createSplit(false, tables.get(DataGridType.Aspects),
					tables.get(DataGridType.Attributes), 0.5);

			JSplitPane spLeft = DustMontruUtils.createSplit(false, tables.get(DataGridType.Units), spLB, 0.2);

			JSplitPane spTop = DustMontruUtils.createSplit(true, spLeft, gp, 0.2);

			JSplitPane spBottom = DustMontruUtils.createSplit(true, tables.get(DataGridType.Focused),
					tables.get(DataGridType.Items), 0.2);

			JSplitPane spMain = DustMontruUtils.createSplit(false, spTop, spBottom, 0.2);

			add(spMain, BorderLayout.CENTER);

			setPreferredSize(new Dimension(1000, 800));

			spLeft.setDividerLocation(250);
			spLB.setDividerLocation(200);
			spTop.setDividerLocation(250);
			spBottom.setDividerLocation(250);
			spMain.setDividerLocation(600);
		}

		public void moveSelected() {
			int dx = dragTo.x - dragFrom.x;
			int dy = dragTo.y - dragFrom.y;

			dragFrom.setLocation(dragTo);

//			Dust.log(EVENT_TAG_TYPE_TRACE, "Moving", dx, dy);

			Set<ItemShape> delayed = new HashSet<>();

			for (ItemShape is : shapes.values()) {
				if (is.selected) {
					if (is.moveable) {
						is.moveShape(dx, dy);
					} else {
						delayed.add(is);
					}
				}
			}

			for (ItemShape is : delayed) {
				is.moveShape(dx, dy);
			}

			gp.invalidate();
			gp.repaint();
		}

		protected void selectUnit(MindHandle h) {
			hUnit = h;

			MindHandle hComp = DustMontruUtils.getItemHandle(gp);

			DustMontruNarrativeUnitgraph.reset();
			hUnitGraph = Dust.access(MindAccess.Get, null, hComp, MISC_ATT_CONN_MEMBERMAP, hUnit);

			shapes.clear();
			focused.clear();

			if (null != hUnitGraph) {

				Dust.access(MindAccess.Visit, new DustVisitor() {
					@Override
					protected MindHandle agentProcess() throws Exception {
						MindHandle hNode = getInfo().getValue();

						if (null != hNode) {
							shapes.put(hNode, new ItemShapeNode(hNode));
						} else {
							Dust.log(EVENT_TAG_TYPE_TRACE, "No node in visitor");
						}
						return MIND_TAG_RESULT_READACCEPT;
					}
				}, hUnitGraph, GEOMETRY_ATT_GRAPH_NODES);

				Dust.access(MindAccess.Visit, new DustVisitor() {
					@Override
					protected MindHandle agentProcess() throws Exception {
						MindHandle hEdge = getInfo().getValue();

						if (null != hEdge) {
							MindHandle src = Dust.access(MindAccess.Peek, null, hEdge, MISC_ATT_CONN_SOURCE);
							MindHandle target = Dust.access(MindAccess.Peek, null, hEdge, MISC_ATT_CONN_TARGET);

							shapes.put(hEdge, DustUtils.isEqual(src, target) ? new ItemShapeLoop(hEdge) : new ItemShapeEdge(hEdge));
						}
						return MIND_TAG_RESULT_READACCEPT;
					}
				}, hUnitGraph, GEOMETRY_ATT_GRAPH_EDGES);
			}

			gp.invalidate();
			gp.repaint();

			Dust.log(null, "Unit selected", hUnit);
		}

		private void selectFocused(MouseEvent e) {
			boolean add = ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0);

			if (!add) {
				for (ItemShape is : shapes.values()) {
					is.selected = false;
				}
			}

			for (ItemShape is : focused) {
				is.selected = true;
			}

			gp.invalidate();
			gp.repaint();
		}
	}

	@Override
	protected MindHandle agentInit() throws Exception {
		@SuppressWarnings("unused")
		ForgeWrapper fp = DustDevUtils.getImplOb(CREATOR, "");

//		Cursor cc = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
//		fp.comp.setCursor(cc);

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		return super.agentProcess();
	}

}
