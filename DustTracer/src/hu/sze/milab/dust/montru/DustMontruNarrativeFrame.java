package hu.sze.milab.dust.montru;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;

public class DustMontruNarrativeFrame extends DustAgent implements DustMontruConsts {

	static DustCreator<JFrame> CREATOR = new DustCreator<JFrame>() {

		@Override
		public JFrame create(Object key, Object... hints) {
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			String title = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MONTRU_ATT_GEN_LABEL);
			if ( !DustUtils.isEmpty(title) ) {
				frame.setTitle(title);
			}
			
			Dust.access(MindAccess.Commit, MIND_TAG_ACTION_INIT, MIND_TAG_CONTEXT_SELF, MONTRU_ATT_WINDOW_MAIN);
			
			CompWrapper<Component> mw = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MONTRU_ATT_WINDOW_MAIN, DUST_ATT_IMPL_DATA);
			
			if ( null != mw ) {
				frame.getContentPane().add(mw.comp, BorderLayout.CENTER);
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
	protected MindHandle agentBegin() throws Exception {
		DustDevUtils.getImplOb(CREATOR, "");
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		return super.agentProcess();
	}

}
