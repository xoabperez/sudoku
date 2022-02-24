package sudoku;

import java.util.Random;

/**
 *
 * @author xoab
 */
public class Grid {
    // 3 arrangements of the sudoku grid: by rows, by cols, and by groups
    Cell[][] gridRows = new Cell[9][9];
    Cell[][] gridCols = new Cell[9][9];
    Cell[][] gridGroups = new Cell[9][9];
    
    /**
     * Make an empty grid of cells.
     */
    public Grid(){
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 9; col++){
                Cell cell = new Cell(row, col);
                gridRows[row][col] = cell;
                gridCols[col][row] = cell;

                // Consider their position in the group like this:
                // 0 1 2
                // 3 4 5
                // 6 7 8
                int gridGroup = cell.group;
                int groupPosition = (row%3)*3 + (col%3);
                gridGroups[gridGroup][groupPosition] = cell;
            }
        }
    }
    
    public Grid(int numberOfEntries){
        this();
        Random rand = new Random();
        int i = 0;
        while(i < numberOfEntries){
            int row = rand.nextInt(9);
            int col = rand.nextInt(9);
            int value = rand.nextInt(9) + 1;
            if (gridRows[row][col].isEmpty() && checkValueValidInGrid(row, col, value)){
                setCellValue(row, col, value);
                i++;
            }
        }
    }
    
    public void setCellValue(int row, int col, int value){
        Cell cell = gridRows[row][col];
        cell.setValue(value);
    }
    
    public boolean checkValueValidInGrid(int row, int col, int value){
        boolean validValue = (0 <= value && value < 9);
        return (validValue && 
                checkValueValidInRow(gridRows[row], value) &&
                checkValueValidInCol(gridCols[col], value) && 
                checkValueValidInGroup(gridGroups[getGroup(row, col)], value));
    }
    
    public boolean checkValueValidInRow(Cell[] cellRow, int value){
        for (int col = 0; col < 9; col++){
            Cell cell = cellRow[col];
            if (cell.getValue() == value){
                return false;
            }
        }
        return true;
    }
    
    public boolean checkValueValidInCol(Cell[] cellCol, int value){
        for (int row = 0; row < 9; row++){
            Cell cell = cellCol[row];
            if (cell.getValue() == value){
                return false;
            }
        }
        return true;
    }
    
    public boolean checkValueValidInGroup(Cell[] cellGroup, int value){
        for (int ind = 0; ind < 9; ind++){
            Cell cell = cellGroup[ind];
            if (cell.getValue() == value){
                return false;
            }
        }
        return true;
    }
    
    public static int getGroup(int row, int col){
        // Consider the groups like this:
        // 0 1 2
        // 3 4 5
        // 6 7 8
        return (row/3)*3 + (col/3);
    }
}
