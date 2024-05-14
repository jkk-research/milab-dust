package hu.sze.milab.dust.montru;

import javax.swing.JComponent;

import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;

public abstract class DustMontruNarrativeContainer extends DustAgent implements DustMontruConsts {

	static DustCreator<JComponent> CREATOR = new DustCreator<JComponent>() {

		@Override
		public JComponent create(Object key, Object... hints) {
			JComponent ret = null;

			return ret;
		}

	};

	@SuppressWarnings("unused")
	@Override
	protected MindHandle agentProcess() throws Exception {
		JComponent comp = DustDevUtils.getImplOb(CREATOR, "");

		return super.agentProcess();
	}

}
