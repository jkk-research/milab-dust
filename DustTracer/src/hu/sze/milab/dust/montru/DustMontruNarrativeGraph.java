package hu.sze.milab.dust.montru;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsFactory;

public class DustMontruNarrativeGraph extends DustAgent implements DustMontruConsts {

	static DustCreator<Image> IMG_CREATOR = new DustCreator<Image>() {

		@Override
		public Image create(Object key, Object... hints) {
			try {
				Image img = ImageIO.read(new File("res/" + key));
				img = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);

				return img;
			} catch (IOException e) {
				DustException.swallow(e, "Image creation", key);
			}
			return null;
		}
	};

	static DustUtilsFactory<String, Image> imgs = new DustUtilsFactory<String, Image>(IMG_CREATOR);

	static class GraphWrapper extends CompWrapper<JScrollPane> {

//		ArrayList<Object> cols = new ArrayList<>();

		class GraphPanel extends JPanel implements MouseWheelListener {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isFocusable() {
				return true;
			}

			private ComponentListener cl = new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
//					layoutChildren();
				}

				@Override
				public void componentShown(ComponentEvent e) {
					layoutChildren();
				}
			};

			int zoomFactor = 0;
			Double zf = null; // 1.0;

			public GraphPanel() {
				super(null);

				addMouseWheelListener(this);
				comp.addComponentListener(cl);
			}

			public void layoutChildren() {
				{
					Dimension dPnl = comp.getViewport().getExtentSize();

					DustMontruUtils.gridLayout(this, dPnl, zf);

					comp.revalidate();
					comp.repaint();

				}
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rot = e.getWheelRotation();

				if (0 != rot) {
					zoomFactor += rot;
//					zf = Math.pow(1.1, zoomFactor);
//					layoutChildren();
				}

			}

			@Override
			public void paint(Graphics g) {
				if (null != zf) {
					Graphics2D g2 = (Graphics2D) g;

					AffineTransform atOrig = g2.getTransform();

					AffineTransform at = new AffineTransform();
					at.scale(zf, zf);
					g2.transform(at);

					super.paint(g);

					g2.setTransform(atOrig);
				} else {
					super.paint(g);
				}
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;
				Dimension d = new Dimension();

				Point ptFrom = new Point();
				Point ptTo = new Point();

				int edgeCount = Dust.access(MindAccess.Peek, 0, hComp, GEOMETRY_ATT_GRAPH_EDGES, KEY_SIZE);

				if (0 < edgeCount) {
					Dust.access(MindAccess.Visit, new DustVisitor() {
						@Override
						protected MindHandle agentProcess() throws Exception {
							MindHandle hEdge = getInfo().getValue();

							CompWrapper<?> gwFrom = Dust.access(MindAccess.Peek, null, hEdge, MISC_ATT_CONN_SOURCE,
									DUST_ATT_IMPL_DATA);
							CompWrapper<?> gwTo = Dust.access(MindAccess.Peek, null, hEdge, MISC_ATT_CONN_TARGET, DUST_ATT_IMPL_DATA);

							getCenter(d, ptFrom, gwFrom);
							getCenter(d, ptTo, gwTo);

							g2.drawLine(ptFrom.x, ptFrom.y - 10, ptTo.x, ptTo.y - 10);

							return MIND_TAG_RESULT_READACCEPT;
						}
					}, hComp, GEOMETRY_ATT_GRAPH_EDGES);
				}
			}

		};

		GraphPanel gp = new GraphPanel();

		protected GraphWrapper() {
			super(new JScrollPane());

			comp.setViewportView(gp);
		}
	}

	static {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				boolean ret = false;
				Component comp = e.getComponent();
				if (DustUtils.isEqual(comp.getClass(), GraphWrapper.GraphPanel.class)) {
					GraphWrapper.GraphPanel gp = (GraphWrapper.GraphPanel) comp;
					int id = e.getID();

					if (id == KeyEvent.KEY_TYPED) {
						ret = true;
						
						switch (e.getKeyChar()) {
						case 'g':
							gp.layoutChildren();
							break;
						default:
							ret = false;
						}
					}
				}
				return ret;
			}
		});
	}

	static DustCreator<GraphWrapper> CREATOR = new DustCreator<GraphWrapper>() {

		@Override
		public GraphWrapper create(Object key, Object... hints) {
			GraphWrapper ret = new GraphWrapper();
			return ret;
		}
	};

	private static void getCenter(Dimension d, Point ptFrom, CompWrapper<?> gwFrom) {
		gwFrom.comp.getSize(d);
		gwFrom.comp.getLocation(ptFrom);
		ptFrom.x += (d.width / 2);
		ptFrom.y += (d.height / 2);
	}

	@Override
	protected MindHandle agentInit() throws Exception {

		GraphWrapper gw = DustDevUtils.getImplOb(CREATOR, "");
		JPanel pnl = gw.gp;

		Collection<Object> units = Dust.access(MindAccess.Peek, Collections.EMPTY_LIST, gw.hComp, MISC_ATT_CONN_SOURCE,
				MISC_ATT_CONN_MEMBERARR);

		MindHandle hUnit = Dust.access(MindAccess.Peek, null, gw.hComp, MIND_ATT_KNOWLEDGE_UNIT);

		for (Object unit : units) {
//			Dust.access(MindAccess.Visit, new DustVisitor() {
			Dust.access(MindAccess.Visit, new DustVisitor(VisitFollowRef.Once) {
				@Override
				protected MindHandle agentProcess() throws Exception {
					VisitInfo info = getInfo();

					MindHandle hAtt = info.getAttHandle();

					Object val = info.getValue();
					if (val instanceof MindHandle) {
						MindHandle hItemLabel = getItemLabel(gw, pnl, hUnit, (MindHandle) val);

						if (null != hAtt) {
							MindHandle hParentLabel = getItemLabel(gw, pnl, hUnit, info.getItemHandle());
							if (null != hParentLabel) {
								MindHandle hEdge = DustDevUtils.newHandle(hUnit, GEOMETRY_ASP_EDGE, "edge");
								Dust.access(MindAccess.Set, hParentLabel, hEdge, MISC_ATT_CONN_SOURCE);
								Dust.access(MindAccess.Set, hItemLabel, hEdge, MISC_ATT_CONN_TARGET);
//								Dust.access(MindAccess.Set, hAtt, hEdge, GEOMETRY_ATT_EDGE_CLASS);
								DustDevUtils.setTag(hEdge, hAtt, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

								Dust.access(MindAccess.Insert, hEdge, gw.hComp, GEOMETRY_ATT_GRAPH_EDGES, KEY_ADD);
							}
						} else {
							DustDevUtils.breakpoint("heh");
						}
					}

					return MIND_TAG_RESULT_READACCEPT;
				}
			}, unit, MIND_ATT_UNIT_HANDLES);

		}

		return MIND_TAG_RESULT_READACCEPT;
	}

//	@SuppressWarnings("unchecked")
	@Override
	protected MindHandle agentProcess() throws Exception {
		return super.agentProcess();
	}

	private MindHandle getItemLabel(GraphWrapper gw, JPanel pnl, MindHandle hUnit, MindHandle hItem) {
		MindHandle hItemLabel = Dust.access(MindAccess.Peek, null, gw.hComp, MISC_ATT_CONN_MEMBERMAP, hItem);

		if (null == hItemLabel) {
			String lbl = hItem.toString();
			hItemLabel = DustDevUtils.registerAgent(hUnit, MONTRU_NAR_WIDGET, lbl);
			DustDevUtils.setTag(hItemLabel, MONTRU_TAG_WIDGET_LABEL, MONTRU_TAG_WIDGET);

			Dust.access(MindAccess.Commit, MIND_TAG_ACTION_INIT, hItemLabel);

			CompWrapper<?> cw = Dust.access(MindAccess.Peek, null, hItemLabel, DUST_ATT_IMPL_DATA);
			JLabel jl = (JLabel) cw.comp;

			ImageIcon icon = new ImageIcon(imgs.get("ball_purple.png"));
			jl.setText(lbl);
			jl.setIcon(icon);
			jl.setVerticalTextPosition(JLabel.BOTTOM);
			jl.setHorizontalTextPosition(JLabel.CENTER);

			pnl.add(jl);

			Dimension d = jl.getPreferredSize();
			jl.setBounds(0, 0, d.width, d.height);

//				mcw.add(cw);

			Dust.access(MindAccess.Set, hItemLabel, gw.hComp, MISC_ATT_CONN_MEMBERMAP, hItem);
			Dust.access(MindAccess.Insert, hItemLabel, gw.hComp, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
		}

		return hItemLabel;
	}
}
