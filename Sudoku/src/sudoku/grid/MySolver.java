/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sudoku.grid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * This is the based on how I might solve it.
 * 1. For all empty cells, determine their potential values based on other
 *    values in their row/cell/square. If only one potential value is set,
 *    that's the cell's value
 * 2. Next, for each cell group, for each missing value, see if there's
 *    only 1 cell that can hold it.
 * 3. Other techniques that seem tricky to implement, so do something else
 * Once we haven't found any new values by steps 1 or 2, we can guess the value 
 * for a single cell and try to solve then; do this recursively until solved.
 */
public class MySolver {
    
    private final Grid grid;
    
    public MySolver(Grid grid){
        this.grid = grid;
    }
    
    // NOTE - I think I need to make sure that updates use "setCellValueInternally"
    // so that values get checked before they get set - since I'm going to try
    // guessing, we may get failures
    public boolean solve() throws Exception{
        System.out.println("Attempting to solve.");
        long startTime = System.nanoTime();
        
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
        
        long endTime = System.nanoTime();

        double duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
        
        System.out.println("Solved in " + duration + " milliseconds");
        
        return (solved && grid.emptyCells.isEmpty());
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
            Cell cell = this.grid.getCellAt((int) rowColPair.getRow(), (int)rowColPair.getCol());
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
                    int row = (int) rowColPair.getRow();
                    int col = (int) rowColPair.getCol();
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
            int row = (int) rowColPair.getRow();
            int col = (int) rowColPair.getCol();
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
            } else if (potentialValues.isEmpty()){
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
        HashMap<Cell, Integer> cellsToRemove = new HashMap<>();
        for (CellGroup group : grid.gridRows){
            cellsToRemove.putAll(group.lookForMissingValues());
        }
        for (CellGroup group : grid.gridCols){
            cellsToRemove.putAll(group.lookForMissingValues());
        }
        for (CellGroup group : grid.gridSquares){
            cellsToRemove.putAll(group.lookForMissingValues());
        }
        
        for (Entry<Cell, Integer> entry : cellsToRemove.entrySet()){
            Cell cell = entry.getKey();
            int value = (int) entry.getValue();
            if (!grid.setCellValueInternally(cell, value)){
                System.out.println("We've got a problem. Trying to add " + 
                        value + " to (" + cell.row + "," + cell.col +")");
                return false;
            }
        }
        return true;
    }
}
