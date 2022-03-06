package sudoku.display;

import javax.swing.table.AbstractTableModel;
import sudoku.grid.Grid;

/**
 * Make a specialized table model to interact with the underlying grid.
 */
public class SudokuTableModel extends AbstractTableModel {

    public Object data[][] = new Integer[9][9];
    Grid grid = new Grid();

    /**
     * Set the underlying grid based on the gui.
     */
    public void setGrid() {
        try {
            Grid newGrid = new Grid((Integer[][]) this.data);
            this.grid = newGrid;
        } catch (Exception ex) {
            System.out.println("Unable to set the grid based on input");
        }
    }

    /**
     * Set both the underlying grid and the GUI grid to some input grid.
     * @param grid 
     */
    public void setGrid(Grid grid) {
        this.grid = grid;

        // Need to clear out some values and set others;
        grid.getEmptyCells().forEach((pair) -> {
            Integer row = (int) pair.getRow();
            Integer col = (int) pair.getCol();

            setValueAt(null, row, col);
        });
        grid.getFilledCells().forEach((pair, value) -> {
            Integer row = (int) pair.getRow();
            Integer col = (int) pair.getCol();

            setValueAt(value, row, col);
        });
    }

    public void solve() throws Exception {
        this.grid.solve1();
    }

    /**
     * We'll always have 9 rows and 9 columns in sudoku
     * @return 9
     */
    @Override
    public int getColumnCount() {
        return 9;
    }

    /**
     * We'll always have 9 rows and 9 columns in sudoku
     * @return 9
     */
    @Override
    public int getRowCount() {
        return 9;
    }

    /**
     * We don't need column names for the sudoku grid.
     * @param col
     * @return
     */
    @Override
    public String getColumnName(int col) {
        return null;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object val = (grid.getValueAt(row, col) == null) ? null : grid.getValueAt(row, col);
        return grid.getValueAt(row, col);
    }

    @Override
    public Class getColumnClass(int c) {
        return Integer.class;
    }

    /**
     * We'll determine whether a cell is editable based on whether the value
     * was set as part of the initial grid.
     * @param row
     * @param col
     * @return 
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return !grid.getCellAt(row, col).isSetInternally();
    }

    /**
     * Before the set the value of a cell, we'll need to do some checks.
     * @param value
     * @param row
     * @param col
     */
    @Override
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
