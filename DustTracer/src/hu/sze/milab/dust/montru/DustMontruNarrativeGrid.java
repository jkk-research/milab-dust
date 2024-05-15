package hu.sze.milab.dust.montru;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;

public class DustMontruNarrativeGrid extends DustAgent implements DustMontruConsts {

	static class GridWrapper extends CompWrapper<JScrollPane> {
		
		JTable tbl;
		
		protected GridWrapper() {
			super(new JScrollPane());
			tbl = new JTable();
			
			comp.setViewportView(tbl);
		}
	}

	static DustCreator<CompWrapper<? extends Component>> CREATOR = new DustCreator<CompWrapper<? extends Component>>() {

		@Override
		public CompWrapper<? extends Component> create(Object key, Object... hints) {
			CompWrapper<? extends Component> ret = new GridWrapper();

			return ret;
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
