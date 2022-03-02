package sudoku.display;

import javax.swing.table.AbstractTableModel;
import sudoku.grid.Grid;

/**
 * Make a specialized table model to interact with the underlying grid.
 */
public class SudokuTableModel extends AbstractTableModel {

    private String[] columnNames = null;
    public Object data[][] = new Integer[9][9];
    Grid grid = new Grid();

    /**
     * Set the grid based on the gui
     */
    public void setGrid() {
        try {
            Grid grid = new Grid((Integer[][]) this.data);
            this.grid = grid;
        } catch (Exception ex) {
            System.out.println("Unable to set the grid based on input");
        }
    }

    public void setGrid(Grid grid) {
        this.grid = grid;

        // Need to clear out some values and set others;
        grid.getEmptyCells().forEach((pair) -> {
            Integer row = (int) pair.getKey();
            Integer col = (int) pair.getValue();

            setValueAt(null, row, col);
        });
        grid.getFilledCells().forEach((pair, value) -> {
            Integer row = (int) pair.getKey();
            Integer col = (int) pair.getValue();

            setValueAt(value, row, col);
        });
    }

    public void solve() throws Exception {
        this.grid.solve1();
    }

    public int getColumnCount() {
        return 9;
    }

    public int getRowCount() {
        return 9;
    }

    public String getColumnName(int col) {
        return null;
    }

    public Object getValueAt(int row, int col) {
        Object val = (grid.getValueAt(row, col) == null) ? null : grid.getValueAt(row, col);
        return grid.getValueAt(row, col);
    }

    public Class getColumnClass(int c) {
        return Integer.class;
    }

    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (grid.getCellAt(row, col).isSetInternally()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @param value
     * @param row
     * @param col
     */
    public void setValueAt(Object value, int row, int col) {
        // The value will be null if we're clearing the cell
        if (value != null) {
            // If the user tries to set the value, we should check it;
            // otherwise, if it's set internally, we can just display it
            if (!grid.getCellAt(row, col).isSetInternally()) {
                // Table will try to set the value using a string. If it's
                // not a valid integer, return
                if (value instanceof String) {
                    try {
                        value = Integer.parseInt((String) value);
                    } catch(ClassCastException exception){
                        return;
                    }
                }
                if (!grid.checkValueValidInGrid(row, col, (Integer) value)) {
                    System.out.println("Invalid number placement");
                    return;
                }

                // Since this is being set external to the Grid class, it 
                // shouldn't affect the solver - won't change empty/filled
                // cells, etc.
                grid.getCellAt(row, col).setValue((Integer) value);
            }
        } else {
            grid.getCellAt(row, col).setValue(null);
        }

        data[row][col] = value;

        fireTableCellUpdated(row, col);
    }
}
