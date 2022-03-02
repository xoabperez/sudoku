/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sudoku.grid;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javafx.util.Pair;

/**
 * This is the based on how I might solve it.
 * 1. For all empty cells, determine their potential values based on other
 *    values in their row/cell/square. If only one potential value is set,
 *    that's the cell's value
 * 2. Next, for each cell group, for each missing value, see if there's
 *    only 1 cell that can hold it.
 * 3. This step is tricky - check a square group for having a value that must go
 *    into a single row/column - then it can't go elsewhere in the row/column
 */
public class MySolver {
    
    private Grid grid;
    
    public MySolver(Grid grid){
        this.grid = grid;
    }
    
    // NOTE - I think I need to make sure that updates use "setCellValueInternally"
    // so that values get checked before they get set - since I'm going to try
    // guessing, we may get failures
    public boolean solve() throws Exception{
        System.out.println("Attempting to solve.");
        int iteration = 0;
        int previousCellsSolved = 0;
        boolean solved = true;
                
        while (!grid.emptyCells.isEmpty()){
            int numEmptyCells = grid.emptyCells.size();
            
            // Step 1.
            solved = checkForCellsWithOnlyOnePotentialValue() && solved;
            
            // Step 2. 
            solved = checkGroupsForMissingValues() && solved;
            

            // Check how many cells we solved in this round
            int currentSolves = numEmptyCells - grid.emptyCells.size();
            
            System.out.println("At the end of iteration " + iteration + ", " +
                               currentSolves + " cells have been solved");
            
            // Give it a couple of rounds to check over cells before trying to guess
            if (currentSolves == 0 && previousCellsSolved == 0){
                System.out.println("Two rounds without solves, time to start guessing");
                solved = guessSolver() && solved;
                
            } else {
                previousCellsSolved = currentSolves;
            }
            
            iteration++;
        }
        
        return (solved && grid.emptyCells.size() == 0);
    }
    
    /**
     * If we get stuck but we haven't had an error thrown, make a guess. The 
     * easiest guess would be with a cell that only has two potential values. If
     * this leads to a failure, we just try with the other. 
     */
    private boolean guessSolver() throws Exception{
        // Find the cell with the smallest number of potential values
        int smallestNumPotentialValues = 9;
        
        // Hope we don't have an empty grid, because then every cell will be tried
        // and it'll probably be a mess and crash from OOM
        Cell bestCell = this.grid.getCellAt(0,0); 
        for (Pair rowColPair : this.grid.emptyCells){
            Cell cell = this.grid.getCellAt((int) rowColPair.getKey(), (int)rowColPair.getValue());
            if (cell.potentialValues.size() < smallestNumPotentialValues){
                smallestNumPotentialValues = cell.potentialValues.size();
                bestCell = cell;
            }
        }
        
        // Clone the current grid to try guessing
        Grid testGrid = new Grid(this.grid);
        Cell newBestCell = testGrid.getCellAt(bestCell.row, bestCell.col);
        
        // Try each potential value; if it doesn't work, try the next
        for (int val : newBestCell.potentialValues){
            testGrid.setCellValueInternally(newBestCell, val);
            if (testGrid.solve1()){
                HashSet<Pair> formerlyEmptyCells = (HashSet<Pair>) this.grid.emptyCells.clone();
                for (Pair rowColPair : formerlyEmptyCells){
                    int row = (int) rowColPair.getKey();
                    int col = (int) rowColPair.getKey();
                    Cell solvedCell = testGrid.getCellAt(row, col);
                    Cell cellToFill = this.grid.getCellAt(row, col);
                    this.grid.setCellValueInternally(cellToFill, solvedCell.getValue());
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Based on other cells in the same groups, a cell may only have one possible
     * value.
     * @throws Exception 
     */
    private boolean checkForCellsWithOnlyOnePotentialValue() throws Exception{
        // Clone so we can remove values from the actual set in setCellValueInternally
        HashSet<Pair> emptyCells = (HashSet<Pair>) grid.emptyCells.clone();
        for (Iterator<Pair> it = emptyCells.iterator(); it.hasNext();){
            Pair rowColPair = it.next();
            int row = (int) rowColPair.getKey();
            int col = (int) rowColPair.getValue();
            Cell cell = grid.getCellAt(row, col);

            List<Integer> potentialValues = cell.potentialValues;
            if (potentialValues.size() == 1){
                if (grid.setCellValueInternally(cell, potentialValues.get(0))){
                    it.remove();
                } else {
                    System.out.println("We've got a problem. Trying to add " + 
                            potentialValues.get(0) + " to (" + row + "," + col +")");
                    return false;
                }
            } else if (potentialValues.size() == 0){
                System.out.println("We've got a problem. (" + row + "," + col +")"
                                    + " has no potential values possible");
                return false;
            }
        }
        return true;
    }
    
    /**
     * Groups should know which values they need and whether any of their cells
     * can accommodate them. 
     * @return 
     */
    private boolean checkGroupsForMissingValues() throws Exception{
        HashSet<Pair> cellsToRemove = new HashSet<>();
        for (CellGroup group : grid.gridRows){
            cellsToRemove.addAll(group.lookForMissingValues());
        }
        for (CellGroup group : grid.gridCols){
            cellsToRemove.addAll(group.lookForMissingValues());
        }
        for (CellGroup group : grid.gridSquares){
            cellsToRemove.addAll(group.lookForMissingValues());
        }
        
        for (Iterator<Pair> it = cellsToRemove.iterator(); it.hasNext();){
            Pair cellValuePair = it.next();
            Cell cell = (Cell) cellValuePair.getKey();
            int value = (int) cellValuePair.getValue();
            
            if (!grid.setCellValueInternally(cell, value)){
                System.out.println("We've got a problem. Trying to add " + 
                        value + " to (" + cell.row + "," + cell.col +")");
                return false;
            }
        }
        return true;
    }
    
//    private void checkValuesMustGoInGroup(){
//        for (CellGroup group : grid.gridSquares){
//            //removedCells.addAll(group.lookForMissingValues());
//        }
//    }
}
