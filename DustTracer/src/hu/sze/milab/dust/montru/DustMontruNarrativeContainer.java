package hu.sze.milab.dust.montru;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;

@SuppressWarnings("rawtypes")
public class DustMontruNarrativeContainer extends DustAgent implements DustMontruConsts {

	static class PanelWrapper extends CompWrapper<JPanel> {
		protected PanelWrapper(ArrayList<CompWrapper<?>> members, MindHandle hLayout) {
			super(new JPanel(null));

			if ( hLayout == MONTRU_TAG_LAYOUT_PAGE ) {
				comp.setLayout(new BorderLayout());

				for (CompWrapper<?> mw : members) {
					Object bl = BorderLayout.CENTER;
					
					Object a = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MIND_ATT_KNOWLEDGE_TAGS, MONTRU_TAG_PAGE);
					if ( MONTRU_TAG_PAGE_HEADER == a ) {
						bl = BorderLayout.NORTH;
					} else if ( MONTRU_TAG_PAGE_FOOTER == a ) {
						bl = BorderLayout.SOUTH;
					} else {
						a = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MIND_ATT_KNOWLEDGE_TAGS, MONTRU_TAG_LINE);
						if ( MONTRU_TAG_LINE_LEAD == a ) {
							bl = BorderLayout.WEST;
						} else if ( MONTRU_TAG_LINE_TAIL == a ) {
							bl = BorderLayout.EAST;
						} 
					}
					
					comp.add(mw.comp, bl);
				}
			} else if ( hLayout == MONTRU_TAG_LAYOUT_BOX ) {
				int align = DustDevUtils.chkTag(MIND_TAG_CONTEXT_SELF, MONTRU_TAG_BOX_LINE, MONTRU_TAG_BOX) ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS;
				comp.setLayout(new BoxLayout(comp, align));

				for (CompWrapper<?> mw : members) {
					comp.add(mw.comp);
				}
			} else if ( hLayout == MONTRU_TAG_LAYOUT_GRID ) {
				comp.setLayout(new GridBagLayout());
			}
		}
	}

	static class SplitWrapper extends CompWrapper<JSplitPane> {
		protected SplitWrapper(ArrayList<CompWrapper<?>> members) {
			super(new JSplitPane(DustDevUtils.chkTag(MIND_TAG_CONTEXT_SELF, GEOMETRY_TAG_VALTYPE_CARTESIAN_Y, GEOMETRY_TAG_VALTYPE) ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT));

			comp.setLeftComponent(members.get(0).comp);
			comp.setRightComponent(members.get(1).comp);
		}
	}

	static class TabWrapper extends CompWrapper<JTabbedPane> {
		protected TabWrapper(ArrayList<CompWrapper<?>> members) {
			super(new JTabbedPane());

			for (CompWrapper<?> mw : members) {
				String label = Dust.access(MindAccess.Peek, null, mw.hComp, MONTRU_ATT_GEN_LABEL);
				comp.addTab(label, mw.comp);
			}
		}
	}

	static DustCreator<CompWrapper<? extends Component>> CREATOR = new DustCreator<CompWrapper<? extends Component>>() {

		@Override
		public CompWrapper<? extends Component> create(Object key, Object... hints) {
			CompWrapper<? extends Component> ret = null;

			MindHandle hLayout = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MIND_ATT_KNOWLEDGE_TAGS, MONTRU_TAG_LAYOUT);

			ArrayList members = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_MEMBERARR);
			ArrayList<CompWrapper<?>> mcw = new ArrayList<>();

			for (Object m : members) {
				Dust.access(MindAccess.Commit, MIND_TAG_ACTION_INIT, m);
				mcw.add(Dust.access(MindAccess.Peek, null, m, DUST_ATT_IMPL_DATA));
			}

			if ( hLayout == MONTRU_TAG_LAYOUT_SPLIT ) {
				ret = new SplitWrapper(mcw);
			} else if ( hLayout == MONTRU_TAG_LAYOUT_TAB ) {
				ret = new TabWrapper(mcw);
			} else {
				ret = new PanelWrapper(mcw, hLayout);
			}

			return ret;
		}
	};

	@Override
	protected MindHandle agentInit() throws Exception {
		DustDevUtils.getImplOb(CREATOR, "");

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}

}
