package hu.sze.milab.dust.montru;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.DustConsts.MindAccess;
import hu.sze.milab.dust.DustConsts.MindHandle;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.stream.json.DustJsonUtils;
import hu.sze.milab.dust.utils.DustUtils;
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

		ArrayList<Object> cols = new ArrayList<>();

		class GraphPanel extends JPanel implements MouseWheelListener {
			private static final long serialVersionUID = 1L;

			private int zoomFactor = 0;

			public GraphPanel() {
				super(null);

				addMouseWheelListener(this);
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rot = e.getWheelRotation();

				if (0 != rot) {
					zoomFactor += rot;

					repaint();
				}

			}

			@Override
			public void paint(Graphics g) {
				if (0 != zoomFactor) {
					Graphics2D g2 = (Graphics2D) g;

					AffineTransform atOrig = g2.getTransform();

					AffineTransform at = new AffineTransform();
					double zf = 1.0 + (0.1 * zoomFactor);
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

				g2.drawLine(20, 20, 200, 200);
				g2.drawLine(20, 200, 200, 20);

			}

		};

		GraphPanel gp = new GraphPanel();

		protected GraphWrapper() {
			super(new JScrollPane());

			ImageIcon icon = new ImageIcon(imgs.get("ball_purple.png"));

			JLabel label1 = new JLabel("Test", icon, JLabel.CENTER);
			label1.setVerticalTextPosition(JLabel.BOTTOM);
			label1.setHorizontalTextPosition(JLabel.CENTER);
			gp.add(label1);

			Dimension size = label1.getPreferredSize();
			label1.setBounds(10, 10, size.width, size.height);

			gp.setSize(400, 400);

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

		for (Object unit : units) {
			Map<Object, MindHandle> items = Dust.access(MindAccess.Peek, Collections.EMPTY_MAP, unit, MIND_ATT_UNIT_HANDLES);
			for (MindHandle hItem : items.values()) {

				Map<MindHandle, Object> itemData = Dust.access(MindAccess.Peek, Collections.EMPTY_MAP, unit,
						MIND_ATT_UNIT_CONTENT, hItem);

				if (null != itemData) {
					Object item = DustJsonUtils.handleToMap(hItem);

					for (Map.Entry<MindHandle, Object> ce : itemData.entrySet()) {
						MindHandle hAtt = ce.getKey();

					}
				}
			}
		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected MindHandle agentProcess() throws Exception {
		GraphWrapper gw = DustDevUtils.getImplOb(CREATOR, "");

		Object src = DustDevUtils.getValueRec(gw.hComp, MISC_ATT_CONN_SOURCE, MISC_ATT_CONN_OWNER);

		if (null != src) {
			ArrayList<Object> path = Dust.access(MindAccess.Peek, null, gw.hComp, MISC_ATT_REF_PATH);
			Object o = (null == path) ? src : Dust.access(MindAccess.Peek, null, src, path.toArray());

			if (o instanceof Map) {
				for (Map.Entry<Object, Object> e : ((Map<Object, Object>) o).entrySet()) {
					Map<Object, String> row = new HashMap<>();

					row.put(TEXT_ATT_TOKEN, (String) e.getKey());

					Map<Object, Object> m = Dust.access(MindAccess.Peek, Collections.EMPTY_MAP, e.getValue(),
							MISC_ATT_GEN_EXTMAP);

					for (Map.Entry<Object, Object> me : m.entrySet()) {
						Object mk = me.getKey();
						if (!gw.cols.contains(mk)) {
							gw.cols.add(mk);
						}

						row.put(mk, DustUtils.toString(me.getValue()));
					}
				}
			}
		}

		return super.agentProcess();
	}

}
