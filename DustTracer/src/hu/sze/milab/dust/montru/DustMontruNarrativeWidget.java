package hu.sze.milab.dust.montru;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;

@SuppressWarnings("rawtypes")
public class DustMontruNarrativeWidget extends DustAgent implements DustMontruConsts {

	static class LabelWrapper extends CompWrapper<JLabel> {
		protected LabelWrapper() {
			super(new JLabel());
		}
	}

	static class ButtonWrapper extends CompWrapper<JButton> {
		protected ButtonWrapper() {
			super(new JButton());
		}
	}

	static class InputWrapper extends CompWrapper<JTextField> {
		protected InputWrapper() {
			super(new JTextField());
		}
	}

	static class TextWrapper extends CompWrapper<JTextArea> {
		protected TextWrapper() {
			super(new JTextArea());
		}
	}

	static class ToggleWrapper extends CompWrapper<JToggleButton> {
		protected ToggleWrapper() {
			super(new JToggleButton());
		}
	}

	static class ListWrapper extends CompWrapper<JList> {
		protected ListWrapper() {
			super(new JList());
		}
	}

	static class ComboWrapper extends CompWrapper<JComboBox> {
		protected ComboWrapper() {
			super(new JComboBox());
		}
	}

	static DustCreator<CompWrapper<? extends Component>> CREATOR = new DustCreator<CompWrapper<? extends Component>>() {

		@Override
		public CompWrapper<? extends Component> create(Object key, Object... hints) {
			CompWrapper<? extends Component> ret = null;

			MindHandle hType = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MIND_ATT_KNOWLEDGE_TAGS, MONTRU_TAG_WIDGET);

			if ( hType == MONTRU_TAG_WIDGET_LABEL ) {
				ret = new LabelWrapper();
			} else if ( hType == MONTRU_TAG_WIDGET_BUTTON ) {
				ret = new ButtonWrapper();
			} else if ( hType == MONTRU_TAG_WIDGET_INPUT ) {
				ret = new InputWrapper();
			} else if ( hType == MONTRU_TAG_WIDGET_TEXT ) {
				ret = new TextWrapper();
			} else if ( hType == MONTRU_TAG_WIDGET_TOGGLE ) {
				ret = new ToggleWrapper();
			} else if ( hType == MONTRU_TAG_WIDGET_LIST ) {
				ret = new ListWrapper();
			} else if ( hType == MONTRU_TAG_WIDGET_COMBO ) {
				ret = new ComboWrapper();
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
