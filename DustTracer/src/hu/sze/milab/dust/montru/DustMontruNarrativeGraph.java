package hu.sze.milab.dust.montru;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import hu.sze.milab.dust.utils.DustutilsFactory;

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

	static DustutilsFactory<String, Image> imgs = new DustutilsFactory<String, Image>(IMG_CREATOR);

	static class GraphWrapper extends CompWrapper<JScrollPane> {

//		ArrayList<Object> cols = new ArrayList<>();

		class GraphPanel extends JPanel implements MouseWheelListener {
			private static final long serialVersionUID = 1L;

			private ComponentListener cl = new  ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					layoutChildren();
				}
				
				@Override
				public void componentShown(ComponentEvent e) {
					layoutChildren();
				}
			};

			private int zoomFactor = 0;
			double zf = Math.pow(1.1, zoomFactor);

			public GraphPanel() {
				super(null);

				addMouseWheelListener(this);
				comp.addComponentListener(cl);
			}

			public void layoutChildren() {
				{
					int cc = getComponentCount();
					
					Dimension md = null;
					Dimension d = new Dimension();

					for ( int i = 0; i < cc; ++i) {
						getComponent(i).getSize(d);
						
						if ( null == md ) {
							md = new Dimension(d);
						} else {
							if ( d.height > md.height ) {
								md.height = d.height;
							}
							if ( d.width > md.width ) {
								md.width = d.width;
							}
						}
					}
					
					md.height += 10;
					md.width += 10;
					int dx = md.width / 2;
					
					Dimension dPnl = comp.getViewport().getExtentSize();
					
					int pw = dPnl.width;
					if ( 0 != zoomFactor ) {
						pw = (int) ((double) pw / zf);
					}
					
					Point ptChild = null;
					
					for ( int i = 0; i < cc; ++i) {
						Component comp = getComponent(i);
						
						comp.getSize(d);
						
						if ( null == ptChild ) {
							ptChild = new Point(md.width / 2, md.height/2);
						} else {
							ptChild.x += md.width;
							
							if ( (ptChild.x + dx ) > pw ) {
								ptChild.x = md.width / 2;
								ptChild.y += md.height;
							}
						}
						
						comp.setLocation(ptChild.x - (d.width / 2), ptChild.y - (d.height / 2));
					}
					
					dPnl.height = ptChild.y + (md.height / 2);
					if ( 0 != zoomFactor ) {
						dPnl.height = (int) ((double) dPnl.height * zf);
					}

					setPreferredSize(dPnl);
					
					comp.revalidate();
					comp.repaint();

					
				}
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rot = e.getWheelRotation();

				if (0 != rot) {
					zoomFactor += rot;
					zf = Math.pow(1.1, zoomFactor);
					layoutChildren();					
				}

			}

			@Override
			public void paint(Graphics g) {
				if (0 != zoomFactor) {
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

//				Graphics2D g2 = (Graphics2D) g;
//
//				g2.drawLine(20, 20, 200, 200);
//				g2.drawLine(20, 200, 200, 20);

			}

		};

		GraphPanel gp = new GraphPanel();

		protected GraphWrapper() {
			super(new JScrollPane());

//			gp.setSize(400, 400);

			comp.setViewportView(gp);
		}
	}

	static DustCreator<GraphWrapper> CREATOR = new DustCreator<GraphWrapper>() {

		@Override
		public GraphWrapper create(Object key, Object... hints) {
			GraphWrapper ret = new GraphWrapper();
			return ret;
		}
	};

	@Override
	protected MindHandle agentInit() throws Exception {

		GraphWrapper gw = DustDevUtils.getImplOb(CREATOR, "");
		JPanel pnl = gw.gp;

		Collection<Object> units = Dust.access(MindAccess.Peek, Collections.EMPTY_LIST, gw.hComp, MISC_ATT_CONN_SOURCE,
				MISC_ATT_CONN_MEMBERARR);

		MindHandle hUnit = Dust.access(MindAccess.Peek, null, gw.hComp, MIND_ATT_KNOWLEDGE_UNIT);
		ArrayList<CompWrapper<?>> mcw = new ArrayList<>();

		for (Object unit : units) {
			Dust.access(MindAccess.Visit, new DustVisitor() {
				@Override
				protected MindHandle agentProcess() throws Exception {
					MindHandle hItem = getInfo().getValue();

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
						mcw.add(cw);

						Dust.access(MindAccess.Set, hItemLabel, gw.hComp, MISC_ATT_CONN_MEMBERMAP, hItem);
						Dust.access(MindAccess.Insert, hItemLabel, gw.hComp, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
					}

					return MIND_TAG_RESULT_READACCEPT;
				}
			}, unit, MIND_ATT_UNIT_HANDLES);

			Component cChild;
			Dimension d;

			for (CompWrapper<?> cw : mcw) {
				cChild = cw.comp;
				d = cChild.getPreferredSize();
				cChild.setBounds(0, 0, d.width, d.height);
			}

		}

		return MIND_TAG_RESULT_READACCEPT;
	}

//	@SuppressWarnings("unchecked")
	@Override
	protected MindHandle agentProcess() throws Exception {
//		GraphWrapper gw = DustDevUtils.getImplOb(CREATOR, "");

//		Object src = DustDevUtils.getValueRec(gw.hComp, MISC_ATT_CONN_SOURCE, MISC_ATT_CONN_OWNER);
//
//		if (null != src) {
//			ArrayList<Object> path = Dust.access(MindAccess.Peek, null, gw.hComp, MISC_ATT_REF_PATH);
//			Object o = (null == path) ? src : Dust.access(MindAccess.Peek, null, src, path.toArray());
//
//			if (o instanceof Map) {
//				for (Map.Entry<Object, Object> e : ((Map<Object, Object>) o).entrySet()) {
//					Map<Object, String> row = new HashMap<>();
//
//					row.put(TEXT_ATT_TOKEN, (String) e.getKey());
//
//					Map<Object, Object> m = Dust.access(MindAccess.Peek, Collections.EMPTY_MAP, e.getValue(),
//							MISC_ATT_GEN_EXTMAP);
//
//					for (Map.Entry<Object, Object> me : m.entrySet()) {
//						Object mk = me.getKey();
//						if (!gw.cols.contains(mk)) {
//							gw.cols.add(mk);
//						}
//
//						row.put(mk, DustUtils.toString(me.getValue()));
//					}
//				}
//			}
//		}

		return super.agentProcess();
	}

}
