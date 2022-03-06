package sudoku.display;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import sudoku.grid.Cell;
import static sudoku.grid.Cell.VALID_VALUES;
import sudoku.grid.Grid;

/**
 *
 * @author xoab
 */
public class SudokuTable extends JTable implements PropertyChangeListener {
    
    private final SudokuTableModel model;
    
    public SudokuTable(){
        model = new SudokuTableModel();
        this.setModel(model);
        this.setDefaultEditor(Integer.class, new SudokuEditor());
    }
    
    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return new SudokuRenderer();
    };

    /**
     * Set the grid for the game, meaning we should display its values.
     * @param grid 
     */
    public void setGrid(Grid grid){
        model.setGrid(grid);
        
        // Listen for cell changes
        grid.addPropertyChangeListener(this);
    }
    
    /**
     * Set the grid based on the gui.
     */
    public void setGrid(){
        model.setGrid();
        model.grid.addPropertyChangeListener(this);
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
     * @throws java.lang.Exception
     */
    public void startNewGame() throws Exception{
        Grid grid = new Grid(30);
        this.setGrid(grid);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        // If we get this, we're solving internally so don't need to 
        // runthe table setValueAt
        if (pce.getPropertyName().equals(Grid.NEW_CELL_VALUE)){
            Cell cell = (Cell) pce.getNewValue();
            this.setValueAt(cell.getValue(), cell.row, cell.col);
            this.repaint();
        }
    }
    
    public class SudokuRenderer extends JLabel implements TableCellRenderer {

        public SudokuRenderer() {
            setOpaque(true); // so the background shows up
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                                JTable table, Object color,
                                boolean isSelected, boolean hasFocus,
                                int row, int column) {
                        
            // We'll have to render based on whether the cell has 1 or 2 borders
            if ((row%3) == 0 && (column%3 == 1)){
                setBorder(new MatteBorder(2,0,0,0, Color.BLACK));
            }
            if ((row%3) == 2 && (column%3 == 1)){
                setBorder(new MatteBorder(0,0,2,0, Color.BLACK));
            }
            if ((row%3) == 1 && (column%3 == 0)){
                setBorder(new MatteBorder(0,2,0,0, Color.BLACK));
            }
            if ((row%3) == 1 && (column%3 == 2)){
                setBorder(new MatteBorder(0,0,0,2, Color.BLACK));
            }
            if ((row%3) == 0 && (column%3 == 0)){
                setBorder(new MatteBorder(2,2,0,0, Color.BLACK));
            }
            if ((row%3) == 2 && (column%3 == 2)){
                setBorder(new MatteBorder(0,0,2,2, Color.BLACK));
            }
            if ((row%3) == 0 && (column%3 == 2)){
                setBorder(new MatteBorder(2,0,0,2, Color.BLACK));
            }
            if ((row%3) == 2 && (column%3 == 0)){
                setBorder(new MatteBorder(0,2,2,0, Color.BLACK));
            }
            
            // Default will be white background with plain text
            this.setBackground(Color.WHITE);
            this.setFont(new Font("SansSerif", Font.PLAIN, 20));
            
            if(table.getValueAt(row, column) != null){
                this.setText(table.getValueAt(row, column).toString());
                
                // Different rendering for "original" values
                if (!table.isCellEditable(row, column)){
                    this.setBackground(Color.LIGHT_GRAY);
                    this.setFont(new Font("SansSerif", Font.BOLD, 20));
                }
            }

            return this;
        }
    }
    
    /**
     * A custom editor specific to sudoku - make sure the text is centered
     * and normal size, inform user if value doesn't fit.
     */
    public class SudokuEditor extends DefaultCellEditor{
        SudokuEditor(){
            super(new JTextField());

            // Listen for inputs to show immediate feedback based on user input
            this.editorComponent.addKeyListener(new KeyListener(){
                @Override
                public void keyTyped(KeyEvent ke) {
                }

                @Override
                public void keyPressed(KeyEvent ke) {
                }

                @Override
                public void keyReleased(KeyEvent ke) {
                    checkInput();
                }
            });

        }

        /**
         * Don't allow an invalid value.
         * @return 
         */
        @Override
        public boolean stopCellEditing(){
            if (checkInput()){
                return super.stopCellEditing();
            } else {
                return false;
            }
        }
        
        // If an invalid value is entered - if it's not an integer, don't allow
        // it, otherwise check if it's a valid value
        private boolean checkInput(){
            // We'll use the table to check the input
            SudokuTable table = (SudokuTable)getComponent().getParent();

            String text = (String) getCellEditorValue(); 
            
            // We're okay with null value
            if (text.equals("")){
                showCellOkay();
                return true;
            }
            
            // We might get an error if the input isn't an integer
            int editingValue;
            try{
                editingValue = Integer.parseInt(text);
                int row = table.getEditingRow();
                int col = table.getEditingColumn();

                if(!VALID_VALUES.contains(editingValue) ||
                   !table.model.grid.checkValueValidInGrid(row, col, editingValue)){
                    showCellError();
                    return false;
                } else {
                    showCellOkay();
                    return true;
                }
            } catch(NumberFormatException ex){
                showCellError();
                return false;
            }
        }

        /**
         * Not doing anything too fancy for the editor - just make it similar
         * to filled cell look.
         * @param table
         * @param value
         * @param isSelected
         * @param row
         * @param column
         * @return 
         */
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                    boolean isSelected, int row, int column){
            Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            c.setFont(new Font("SansSerif", Font.PLAIN, 18));
            ((JTextField)c).setHorizontalAlignment(JTextField.CENTER);
            ((JComponent)c).setBorder(new LineBorder(Color.black));

            return c;
        }
        
        // If the cell input is okay, don't show anything alarming
        public void showCellOkay(){
            JTextField textField = (JTextField)getComponent();
            textField.setBorder(new LineBorder(Color.black));
            textField.requestFocusInWindow();
        }
               
        // If the cell input is bad, show a red border to warn the user
        public void showCellError(){
            JTextField textField = (JTextField)getComponent();
            textField.setBorder(new LineBorder(Color.red));
            textField.selectAll();
            textField.requestFocusInWindow();
        }
    }
}
