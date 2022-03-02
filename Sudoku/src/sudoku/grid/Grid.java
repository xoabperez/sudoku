package sudoku.grid;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.util.Pair;
import sudoku.grid.CellGroup.GroupType;

/**
 * This class is used to store the sudoku grid. The values are of class {@Cell} 
 * since that helps with some in-cell operations. The grid is stored in 
 * different arrangements to help with solving.
 * @author xoab
 */
public class Grid implements PropertyChangeListener {
    // 3 arrangements of the sudoku grid: by rows, by cols, and by 3x3 squares
    CellGroup[] gridRows = new CellGroup[9];
    CellGroup[] gridCols = new CellGroup[9];
    CellGroup[] gridSquares = new CellGroup[9];
    
    // Keep track of how complete we are and what cells need solving
    HashSet<Pair> emptyCells = new HashSet<>();
    HashMap<Pair, Integer> filledCells = new HashMap<>();
    
    // We'll notify the display when a cell's value has changed
    PropertyChangeSupport propChangeSupport;
    public static String NEW_CELL_VALUE = "new cell value";
    
    /**
     * Make an empty grid of cells, storing them in the arrangements above. This
     * constructor MUST be used to get the cells in the right order in their groups
     */
    public Grid(){
        this.propChangeSupport = new PropertyChangeSupport(this);
        
        IntStream.range(0, 9).forEach(i -> gridRows[i] = new CellGroup(GroupType.ROW, i));
        IntStream.range(0, 9).forEach(i -> gridCols[i] = new CellGroup(GroupType.COL, i));
        IntStream.range(0, 9).forEach(i -> gridSquares[i] = new CellGroup(GroupType.SQUARE, i));
        
        // NOTE: to make the group solvers work, the cells MUST be placed in
        // order - row groups have to be left to right, columns top to bottom,
        // squares as shown below.
        try {
            for(int row = 0; row < 9; row++){
                for(int col = 0; col < 9; col++){
                    Cell cell = new Cell(row, col);
                    gridRows[row].addCell(col, cell);
                    gridCols[col].addCell(row, cell);

                    // Consider their position in the square like this:
                    // 0 1 2
                    // 3 4 5
                    // 6 7 8
                    int gridSquare = cell.square;
                    int squarePosition = (row%3)*3 + (col%3);
                    gridSquares[gridSquare].addCell(squarePosition, cell);

                    // Add all cells to empty cells
                    emptyCells.add(new Pair(row, col));
                }
            }
        } catch (Exception ex){
            System.out.println("Unable to add cells to grid. Exiting");
            System.exit(-1);
        }
        
        // Step 2. is done by the groups
        for (CellGroup group : gridRows){
            group.addPropertyChangeListener(this);
        }
        for (CellGroup group : gridCols){
            group.addPropertyChangeListener(this);
        }
        for (CellGroup group : gridSquares){
            group.addPropertyChangeListener(this);
        }
    }
    
    /**
     * Initialize a grid based on a given array input
     * @param data 
     */
    public Grid(Integer[][] data) throws Exception{
        this();
        for (int row = 0; row < data.length; row++){
            for (int col = 0; col < data[0].length; col++){
                if (data[row][col] != null){
                    Cell cell = getCellAt(row, col);
                    setCellValueInternally(cell, data[row][col]);
                }
            }
        }
    }
    
    /**
     * Easy way of cloning a grid
     * @param grid 
     */
    public Grid(Grid grid) throws Exception{
        this();
        for (HashMap.Entry entry : grid.filledCells.entrySet()){
            Pair pair = (Pair) entry.getKey();
            int value = (int) entry.getValue();
            
            int row = (int) pair.getKey();
            int col = (int) pair.getValue();
            
            Cell cell = getCellAt(row, col);
            setCellValueInternally(cell, value);
        }
    }
    
    /**
     * Initialize a grid with some number of entries filled randomly (random
     * locations as well as random (but valid) values). 
     * @param numberOfEntries 
     */
    public Grid(int numberOfEntries){
        this(); // Start with an empty grid
        
        // Get cells at random from list of empty ones
        ArrayList<Integer> emptyCellInds = 
            new ArrayList<>(IntStream.range(0, 81).
                    mapToObj(Integer::new).collect(Collectors.toList()));
        
        // Seed the random number generator
        Random rand = new Random(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        
        try {
            // Try to set random cells; hopefully # of entries is small enough that
            // this isn't too difficult
            // TODO: test how many iterations this takes for various # of entries?
            int i = 0;
            while(i < numberOfEntries){
                int cellNum = rand.nextInt(emptyCellInds.size());
                int cellInd = emptyCellInds.get(cellNum);
                int row = cellInd/9;
                int col = cellInd%9;
                int value = rand.nextInt(9) + 1;

                // We may not be successful based on whether placement is valid.
                if (checkValueValidInGrid(row, col, value)){
                    setCellValueInternally(this.getCellAt(row, col), value);
                    i++;
                    emptyCellInds.remove(cellNum);
                }
            }
        } catch (Exception ex){
            System.out.println("Unable to set random cell values");
            System.exit(-2);
        }
    }
    
    /**
     * Check whether a value can be placed in a cell. To be a valid operation,
     * the value must not exist elsewhere in the row, column, or square.
     * @param row
     * @param col
     * @param value
     * @return 
     */
    public boolean checkValueValidInGrid(int row, int col, int value){
        boolean validValue = (0 < value && value <= 9);
        return (validValue && 
                checkValueValidInRow(gridRows[row], value) &&
                checkValueValidInCol(gridCols[col], value) && 
                checkValueValidInSquare(gridSquares[getSquare(row, col)], value));
    }
    
    /**
     * Check whether a value can be placed in a row, i.e. it's not already in the row.
     * @param cellRow
     * @param value
     * @return 
     */
    public boolean checkValueValidInRow(CellGroup cellRow, Integer value){
        return !cellRow.containsValue(value);
    }
    
    /**
     * Check whether a value can be placed in a column.
     * @param cellCol
     * @param value
     * @return 
     */
    public boolean checkValueValidInCol(CellGroup cellCol, Integer value){
        return !cellCol.containsValue(value);
    }
    
    /**
     * Check whether a value can be placed in a square.
     * @param cellSquare
     * @param value
     * @return 
     */
    public boolean checkValueValidInSquare(CellGroup cellSquare, Integer value){
        return !cellSquare.containsValue(value);
    }
    
    /** 
     * Solve the puzzle! 
     */
    public boolean solve1() throws Exception{
        MySolver solver = new MySolver(this);
        return solver.solve();
    }
    
    /**
     * Try to set the value internally, meaning we should be more confident
     * in its correctness (but check just in case). 
     * @param cell
     * @param value 
     */
    boolean setCellValueInternally(Cell cell, int value) throws Exception{
        if (!checkValueValidInGrid(cell.row, cell.col, value)){
            System.out.println("Value " + value + " is invalid for cell " + cell.toString());
            return false;
        }
        
        cell.setValue(value);
        cell.setInternally = true;
        
        emptyCells.remove(new Pair(cell.row, cell.col));
        filledCells.put(new Pair(cell.row,cell.col), value);
                    
        // When we update the cell's value, we should also 
        // update the cell groups it's in and let the display know to fill the cell
        removedCellUpdateGrid(cell, value);
        
        propChangeSupport.firePropertyChange(NEW_CELL_VALUE, null, cell);
        
        return true;
    }
    
    private void removedCellUpdateGrid(Cell cell, int value) throws Exception{            
        // TODO: is there a better way than doing this manually? Fire a property
        // change from within the cell and have all groups listen to their cells
        // for such a change? That's a lot of listening going on
        gridRows[cell.row].clearValueFromCells(cell.col, value);
        gridCols[cell.col].clearValueFromCells(cell.row, value);
        int square = getSquare(cell.row, cell.col);
        int position = getSquarePosition(cell.row, cell.col);
        gridSquares[square].clearValueFromCells(position, value);
    }
    
    public Cell getCellAt(int row, int col){
        return gridRows[row].getCell(col);
    }
    
    public Integer getValueAt(int row, int col){
        return getCellAt(row, col).getValue();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl){
        propChangeSupport.addPropertyChangeListener(pcl);
    }
    
    /**
     * Given the row and column of a cell, determine the "square" it's in.
     * @param row
     * @param col
     * @return 
     */
    public static int getSquare(int row, int col){
        // Consider the squares like this:
        // 0 1 2
        // 3 4 5
        // 6 7 8
        return (row/3)*3 + (col/3);
    }
    
    /**
     * Given the row and column of a cell, determine where it is within its 
     * square.
     * @param row
     * @param col
     * @return 
     */
    public static int getSquarePosition(int row, int col){
        return (row%3)*3 + (col%3);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName() == NEW_CELL_VALUE){
            Cell cell = (Cell) pce.getNewValue();
            try{
                removedCellUpdateGrid(cell, cell.getValue());
            } catch (Exception ex){
                System.out.println("Error trying to set (" + cell.row + "," + 
                        cell.col + ") to " + cell.getValue());
            }
        }
    }
    
    public HashSet<Pair> getEmptyCells(){
        return emptyCells;
    }
    
    public HashMap<Pair, Integer> getFilledCells(){
        return filledCells;
    }
}
