package hu.sze.milab.dust.montru;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;

public class DustMontruNarrativeGrid extends DustAgent implements DustMontruConsts {

	static class GridWrapper extends CompWrapper<JScrollPane> {

		ArrayList<Object> cols = new ArrayList<>();
		ArrayList<Map<Object, String>> rows = new ArrayList<>();

		class GridTableModel extends AbstractTableModel {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object key = cols.get(columnIndex);
				return rows.get(rowIndex).get(key);
			}

			@Override
			public int getRowCount() {
				return rows.size();
			}

			@Override
			public int getColumnCount() {
				return cols.size();
			}

			@Override
			public String getColumnName(int column) {
				return DustUtils.toString(cols.get(column));
			}

			public void updated() {
				fireTableStructureChanged();
			}
		};

		GridTableModel gtm = new GridTableModel();
		JTable tbl;

		protected GridWrapper() {
			super(new JScrollPane());

			tbl = new JTable(gtm);
			tbl.setAutoCreateRowSorter(true);
			
			comp.setViewportView(tbl);
		}
	}

	static DustCreator<GridWrapper> CREATOR = new DustCreator<GridWrapper>() {

		@Override
		public GridWrapper create(Object key, Object... hints) {
			GridWrapper ret = new GridWrapper();
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
		GridWrapper gw = DustDevUtils.getImplOb(CREATOR, "");

		gw.cols.clear();
		gw.rows.clear();
		
		gw.cols.add(TEXT_ATT_TOKEN);

		Object src = DustDevUtils.getValueRec(gw.hComp, MISC_ATT_CONN_SOURCE, MISC_ATT_CONN_OWNER);

		if ( null != src ) {
			ArrayList<Object> path = Dust.access(MindAccess.Peek, null, gw.hComp, MISC_ATT_REF_PATH);
			Object o = (null == path) ? src : Dust.access(MindAccess.Peek, null, src, path.toArray());

			if ( o instanceof Map ) {
				for (Map.Entry<Object, Object> e : ((Map<Object, Object>) o).entrySet()) {
					Map<Object, String> row = new HashMap<>();
					gw.rows.add(row);
					
					row.put(TEXT_ATT_TOKEN, (String) e.getKey());
					
					Map<Object, Object> m = Dust.access(MindAccess.Peek, Collections.EMPTY_MAP, e.getValue(), MISC_ATT_GEN_EXTMAP);
					
					for ( Map.Entry<Object, Object> me : m.entrySet() ) {
						Object mk = me.getKey();
						if ( !gw.cols.contains(mk)) {
							gw.cols.add(mk);
						}
						
						row.put(mk, DustUtils.toString(me.getValue()));
					}
				}
			}
		}
		
		gw.gtm.updated();
		
		return super.agentProcess();
	}

}
