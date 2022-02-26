package sudoku;

/*
 * TableDemo.java requires no other files.
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** 
 * TableDemo is just like SimpleTableDemo, except that it
 * uses a custom TableModel.
 */
public class GridDisplay extends JPanel implements PropertyChangeListener{
    private boolean DEBUG = false;
    private JTable table;
    private SudokuGridModel model = new SudokuGridModel();
    
    public GridDisplay() {
        super(new GridLayout(0,1));

        table = new JTable(model);
        table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
        table.setFillsViewportHeight(true);
        
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        
        // From https://stackoverflow.com/questions/29174942/make-jtable-rows-fill-the-entire-height-of-jscrollpane
        table.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                table.setRowHeight(16);
                Dimension p = table.getPreferredSize();
                Dimension v = scrollPane.getViewportBorderBounds().getSize();
                if (v.height > p.height)
                {
                    int available = v.height - 
                        table.getRowCount() * table.getRowMargin();
                    int perRow = available / table.getRowCount();
                    table.setRowHeight(perRow);
                }
            }
        });
        
        add(scrollPane);
    }

    /**
     * Set the grid for the game, meaning we should display its values.
     * @param grid 
     */
    public void setGrid(Grid grid){
        model.setGrid(grid);
        
        // Listen for cell changes
        grid.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        // If we get this, we're solving internally so don't need to 
        // runthe table setValueAt
        if (pce.getPropertyName().equals(Grid.NEW_CELL_VALUE)){
            Cell cell = (Cell) pce.getNewValue();
            this.table.setValueAt(cell.getValue(), cell.row, cell.col);
            this.table.repaint();
        }
    }
    
    class SudokuGridModel extends AbstractTableModel {
        private String[] columnNames = null;
        public Object data[][] = new Integer[9][9];
        private Grid grid = new Grid();

        public void setGrid(Grid grid){
            this.grid = grid;
            
            // Need to clear out some values and set others;
            grid.emptyCells.forEach((pair) -> {
                Integer row = (int) pair.getKey();
                Integer col = (int) pair.getValue();

                setValueAt(null, row, col);
            });
            grid.filledCells.forEach((pair, value) -> {
                Integer row = (int) pair.getKey();
                Integer col = (int) pair.getValue();

                setValueAt(value, row, col);
            });
        }
        
        public void solve() throws Exception{
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
            Object val = (grid.getValueAt(row, col) == null) ? "" : grid.getValueAt(row, col);
            return grid.getValueAt(row, col);
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return Integer.class;
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (grid.getCellAt(row, col).setInternally) {
                return false;
            } else {
                return true;
            }
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value);
            }

            if (value != null){
                if (!grid.checkValueValidInGrid(row, col, (Integer) value)){
                    System.out.println("Invalid number placement");
                    return;
                }

                if (grid.getCellAt(row, col).isEmpty()){
                    grid.getCellAt(row, col).setValue((int) value);
                }
            }
            
            data[row][col] = null;
            fireTableCellUpdated(row, col);

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + grid.getValueAt(i, j));
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }
    
    public void solve() throws Exception{
        this.model.solve();
    }
    
    /**
     * Clear out the numbers; easier to do with a new grid.
     */
    public void clear(){
        Grid grid = new Grid();
        this.setGrid(grid);
    }
    
    /**
     * Make a grid with random numbers to be played.
     */
    public void startNewGame(){
        Grid grid = new Grid(30);
        this.setGrid(grid);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        GridDisplay newContentPane = new GridDisplay();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
