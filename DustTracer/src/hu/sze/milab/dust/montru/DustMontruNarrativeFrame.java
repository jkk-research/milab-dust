package hu.sze.milab.dust.montru;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.UIManager;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;

public class DustMontruNarrativeFrame extends DustAgent implements DustMontruConsts {

	static DustCreator<JFrame> CREATOR = new DustCreator<JFrame>() {

		@Override
		public JFrame create(Object key, Object... hints) {

			String theme = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MONTRU_ATT_GUI_THEME);

			if ( !DustUtils.isEmpty(theme) ) {
				UIManager.LookAndFeelInfo laf = null;
				
				for ( UIManager.LookAndFeelInfo li : UIManager.getInstalledLookAndFeels() ) {
					if ( DustUtils.isEqual(theme, li.getName())) {
						laf = li;
						break;
					}
				}
				
				if ( null == laf ) {
					Dust.log(EVENT_TAG_TYPE_WARNING, "Theme not found", theme);
				} else {
					String lc = laf.getClassName();
					try {
						UIManager.setLookAndFeel(lc);
					} catch (Throwable e) {
						DustException.swallow(e, "Theme name", theme, "class", lc);
					}
				}
			}

			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			String title = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MONTRU_ATT_GEN_LABEL);
			if ( !DustUtils.isEmpty(title) ) {
				frame.setTitle(title);
			}

			Dust.access(MindAccess.Commit, MIND_TAG_ACTION_INIT, MIND_TAG_CONTEXT_SELF, MONTRU_ATT_WINDOW_MAIN);

			CompWrapper<Component> mw = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MONTRU_ATT_WINDOW_MAIN, DUST_ATT_IMPL_DATA);
			if ( null != mw ) {
				MindHandle hSelf = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF);
				Dust.access(MindAccess.Set, hSelf, mw.hComp, MISC_ATT_CONN_OWNER);
				frame.getContentPane().add(mw.comp);
			}

			frame.pack();
			frame.setVisible(true);

			DustMontruUtils.setBounds(frame);

			return frame;
		}

	};

	@Override
	protected MindHandle agentInit() throws Exception {
		DustDevUtils.getImplOb(CREATOR, "");
		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, MIND_TAG_CONTEXT_SELF, MONTRU_ATT_WINDOW_MAIN);

		return MIND_TAG_RESULT_ACCEPT;
	}

}
