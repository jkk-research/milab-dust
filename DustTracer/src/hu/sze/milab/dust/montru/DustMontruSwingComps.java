package hu.sze.milab.dust.montru;

import javax.swing.table.AbstractTableModel;

public interface DustMontruSwingComps extends DustMontruConsts {

	abstract class MontruSwingTableModelMutable extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		public void updated() {
			fireTableStructureChanged();
		}
	};

	class MontruSwingTableModelTest extends MontruSwingTableModelMutable {
		private static final long serialVersionUID = 1L;

		public int cc;
		public int rc;

		public MontruSwingTableModelTest(int cc, int rc) {
			this.cc = cc;
			this.rc = rc;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return "Cell (" + (rowIndex + 1) + ", " + (columnIndex + 1) + ")";
		}

		@Override
		public int getRowCount() {
			return rc;
		}

		@Override
		public int getColumnCount() {
			return cc;
		}

		@Override
		public String getColumnName(int column) {
			return "Col " + (column + 1);
		}
	};
}
