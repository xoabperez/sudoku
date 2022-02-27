/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sudoku.display;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import sudoku.grid.Cell;
import sudoku.grid.Grid;

/**
 *
 * @author xoab
 */
public class SudokuTable extends JTable implements PropertyChangeListener {
    
    private SudokuTableModel model;
    
    public SudokuTable(){
        model = new SudokuTableModel();
        this.setModel(model);
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
     */
    public void startNewGame(){
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
}
