package hu.sze.milab.dust.montru;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DustMontruNarrativeWidget extends DustAgent implements DustMontruConsts {

	private static MouseListener MOUSE_LISTENER = new MouseListener() {
		
		Border borderSel = new LineBorder(Color.blue, 2);

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			((JComponent) e.getComponent()).setBorder(null);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			((JComponent) e.getComponent()).setBorder(borderSel);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			JComponent c = (JComponent) e.getComponent();
			c.setBackground(Color.yellow);
			c.setForeground(Color.blue);
		}
	};

	static abstract class WidgetWrapper<CompType extends Component> extends CompWrapper<CompType> {
		protected WidgetWrapper(CompType comp) {
			super(comp);
		}

		protected void update() {
		};
	}

	static class LabelWrapper extends WidgetWrapper<JLabel> {
		protected LabelWrapper() {
			super(new JLabel());
		}
	}

	static class ButtonWrapper extends WidgetWrapper<JButton> {
		protected ButtonWrapper() {
			super(new JButton());

			comp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hComp, MISC_ATT_CONN_TARGET);
				}
			});
		}
	}

	static class InputWrapper extends WidgetWrapper<JTextField> {
		protected InputWrapper() {
			super(new JTextField());
		}
	}

	static class TextWrapper extends WidgetWrapper<JTextArea> {
		protected TextWrapper() {
			super(new JTextArea());
		}
	}

	static class ToggleWrapper extends WidgetWrapper<JToggleButton> {
		protected ToggleWrapper() {
			super(new JToggleButton());
		}
	}

	static class ListWrapper extends WidgetWrapper<JList> {
		protected ListWrapper() {
			super(new JList());
		}
	}

	static class ComboWrapper extends WidgetWrapper<JComboBox<Object>> {

		DefaultComboBoxModel<Object> cbm;

		protected ComboWrapper() {
			super(new JComboBox<>());

			cbm = new DefaultComboBoxModel<>();
			comp.setModel(cbm);
		}

		@Override
		protected void update() {
			if (0 == cbm.getSize()) {
				Object src = DustDevUtils.getValueRec(hComp, MISC_ATT_CONN_SOURCE, MISC_ATT_CONN_OWNER);

				if (null != src) {
					ArrayList<Object> path = Dust.access(MindAccess.Peek, null, hComp, MISC_ATT_REF_PATH);
					Object o = (null == path) ? src : Dust.access(MindAccess.Peek, null, src, path.toArray());

					if (o instanceof Map) {
						cbm.addElement("");

						for (Object k : ((Map<Object, Object>) o).keySet()) {
							cbm.addElement(k);
						}
					}
				}
			}
		}
	}

	static class TreeWrapper extends WidgetWrapper<JScrollPane> {
		JTree tree;

		protected TreeWrapper() {
			super(new JScrollPane());
			tree = new JTree();
			comp.setViewportView(tree);
		}
	}

	static DustCreator<WidgetWrapper<? extends Component>> CREATOR = new DustCreator<WidgetWrapper<? extends Component>>() {

		@Override
		public WidgetWrapper<? extends Component> create(Object key, Object... hints) {
			WidgetWrapper<? extends Component> ret = null;

			MindHandle hType = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MIND_ATT_KNOWLEDGE_TAGS,
					MONTRU_TAG_WIDGET);

			if (hType == MONTRU_TAG_WIDGET_LABEL) {
				ret = new LabelWrapper();
			} else if (hType == MONTRU_TAG_WIDGET_BUTTON) {
				ret = new ButtonWrapper();
			} else if (hType == MONTRU_TAG_WIDGET_INPUT) {
				ret = new InputWrapper();
			} else if (hType == MONTRU_TAG_WIDGET_TEXT) {
				ret = new TextWrapper();
			} else if (hType == MONTRU_TAG_WIDGET_TOGGLE) {
				ret = new ToggleWrapper();
			} else if (hType == MONTRU_TAG_WIDGET_LIST) {
				ret = new ListWrapper();
			} else if (hType == MONTRU_TAG_WIDGET_COMBO) {
				ret = new ComboWrapper();
			} else if (hType == MONTRU_TAG_WIDGET_TREE) {
				ret = new TreeWrapper();
			}

			ret.comp.addMouseListener(MOUSE_LISTENER);

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
		WidgetWrapper<? extends Component> ww = DustDevUtils.getImplOb(CREATOR, "");

		ww.update();

		return MIND_TAG_RESULT_ACCEPT;
	}
}
