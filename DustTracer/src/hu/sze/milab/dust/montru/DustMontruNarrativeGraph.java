package hu.sze.milab.dust.montru;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustutilsFactory;

public class DustMontruNarrativeGraph extends DustAgent implements DustMontruConsts {

	static DustCreator<BufferedImage> IMG_CREATOR = new DustCreator<BufferedImage>() {

		@Override
		public BufferedImage create(Object key, Object... hints) {
			try {
				return ImageIO.read(new File("res/" + key));
			} catch (IOException e) {
				DustException.swallow(e, "Image creation", key);
			}
			return null;
		}
	};

	static DustutilsFactory<String, BufferedImage> imgs = new DustutilsFactory<String, BufferedImage>(IMG_CREATOR);

	static class GraphWrapper extends CompWrapper<JScrollPane> {

		ArrayList<Object> cols = new ArrayList<>();

		class GraphPanel extends JPanel {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				BufferedImage image = imgs.get("ball_purple.png");

				if (image != null) {
					g.drawImage(image, 10, 10, this);
				}
			}

		};

		GraphPanel gp = new GraphPanel();

		protected GraphWrapper() {
			super(new JScrollPane());

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
		DustDevUtils.getImplOb(CREATOR, "");
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
