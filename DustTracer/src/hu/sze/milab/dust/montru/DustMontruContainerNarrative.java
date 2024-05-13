package hu.sze.milab.dust.montru;

import javax.swing.JFrame;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;

public abstract class DustMontruContainerNarrative extends DustAgent implements DustMontruConsts {
		
	public static class Frame extends DustMontruContainerNarrative {

		static DustCreator<JFrame> CREATOR = new DustCreator<JFrame>() {

			@Override
			public JFrame create(Object key, Object... hints) {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				String title = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MONTRU_ATT_GEN_LABEL);
				if ( !DustUtils.isEmpty(title) ) {
					frame.setTitle(title);
				}
				
				frame.pack();

				frame.setVisible(true);
				
				DustMontruUtils.setBounds(frame);
				
				return frame;
			}
			
		};
		
		@SuppressWarnings("unused")
		@Override
		protected MindHandle agentProcess() throws Exception {
			JFrame frm = DustDevUtils.getImplOb(CREATOR, "");

			return super.agentProcess();
		}
	}
	
}
