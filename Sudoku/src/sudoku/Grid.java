package sudoku;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.util.Pair;
import sudoku.CellGroup.GroupType;

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
     * Make an empty grid of cells, storing them in the arrangements above.
     */
    public Grid(){
        this.propChangeSupport = new PropertyChangeSupport(this);
        
        IntStream.range(0, 9).forEach(i -> gridRows[i] = new CellGroup(GroupType.ROW, i));
        IntStream.range(0, 9).forEach(i -> gridCols[i] = new CellGroup(GroupType.COL, i));
        IntStream.range(0, 9).forEach(i -> gridSquares[i] = new CellGroup(GroupType.SQUARE, i));
        
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
                if (setCellValue(row, col, value)){
                    this.getCellAt(row, col).setInternally = true;
                    i++;
                    emptyCellInds.remove(cellNum);
                    emptyCells.remove(new Pair(row, col));
                    filledCells.put(new Pair(row,col), value);
                }
            }
        } catch (Exception ex){
            System.out.println("Unable to set random cell values");
            System.exit(-2);
        }
    }
    
    /**
     * Setting the value of a cell if it's valid.
     * @param row
     * @param col
     * @param value 
     * @return whether the operation was successful
     */
    public boolean setCellValue(int row, int col, int value) throws Exception{
        if (checkValueValidInGrid(row, col, value)){
            Cell cell = gridRows[row].getCell(col);
            setCellValue(cell, value);
            return true;
        } else {
            System.out.println("Filling cell (" + row + "," + col + ") with " + 
                                value + " is an invalid operation.");
            return false;
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
     * Solve the puzzle! This is the based on how I might solve it
     * 1. For all empty cells, determine their potential values based on other
     *    values in their row/cell/square. If only one potential value is set,
     *    that's the cell's value
     * 2. Next, for each cell group, for each missing value, see if there's
     *    only 1 cell that can hold it.
     */
    public void solve1() throws Exception{
        int iteration = 0;
        int previousSolves = 1;
        while (!emptyCells.isEmpty()){
            int numEmptyCells = emptyCells.size();
            
            // Step 1.
            for (Iterator<Pair> it = emptyCells.iterator(); it.hasNext();){
                Pair rowColPair = it.next();
                int row = (int) rowColPair.getKey();
                int col = (int) rowColPair.getValue();
                Cell cell = this.getCellAt(row, col);
                
                List<Integer> potentialValues = cell.getPotentialValues();
                for (int i = potentialValues.size() - 1; i >= 0; i--){
                    int val = potentialValues.get(i);
                    if (!checkValueValidInGrid(row, col, val)){
                        potentialValues.remove(i); 
                    }
                }
                if (potentialValues.size() == 1){
                    if (setCellValue(cell.row, cell.col, potentialValues.get(0))){
                        it.remove();
                    } else {
                        System.out.println("We've got a problem. Trying to add" + 
                                potentialValues.get(0) + " to (" + row + "," + col +")");
                        throw new Exception();
                    }
                }
            }
            
            // Step 2. is done by the groups
            for (CellGroup group : gridRows){
                group.lookForMissingValues();
            }
            for (CellGroup group : gridCols){
                group.lookForMissingValues();
            }
            for (CellGroup group : gridSquares){
                group.lookForMissingValues();
            }
            
            int currentSolves = numEmptyCells - emptyCells.size();
            
            System.out.println("At the end of iteration " + iteration + " " +
                               currentSolves + " cells have been solved");
            
            if (currentSolves == 0 && previousSolves == 0){
                System.out.println("Two rounds without solves - probably won't solve it now");
                break;
            }
            
            previousSolves = currentSolves;
            
            iteration++;
        }
    }
    
    /**
     * When we update the cell's value, we should also update the cell groups
     * it's in and let the display know to fill the cell
     * @param cell
     * @param value 
     */
    private void setCellValue(Cell cell, int value) throws Exception{
        cell.setValue(value);
        
        // TODO: is there a better way than doing this manually? Fire a property
        // change from within the cell and have all groups listen to their cells
        // for such a change? That's a lot of listening going on
        gridRows[cell.row].clearValueFromCells(cell.col, value);
        gridCols[cell.col].clearValueFromCells(cell.row, value);
        int square = getSquare(cell.row, cell.col);
        int position = getSquarePosition(cell.row, cell.col);
        gridSquares[square].clearValueFromCells(position, value);
        
        propChangeSupport.firePropertyChange(NEW_CELL_VALUE, null, cell);
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
                setCellValue(cell, cell.getValue());
            } catch (Exception ex){
                System.out.println("Error trying to set (" + cell.row + "," + 
                        cell.col + ") to " + cell.getValue());
            }
        }
    }
}
