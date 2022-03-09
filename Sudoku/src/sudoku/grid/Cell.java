package sudoku.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static sudoku.grid.Grid.getSquare;

/**
 * The class of a specific cell at a row/col location, which can contain an
 * integer value from 1-9.
 */
public class Cell {
    public static final List<Integer> VALID_VALUES = Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9});
    private Integer value = null;
    public int row;
    public int col;
    public int square; // 3x3 squares
    public List<Integer> potentialValues;
    boolean setInternally = false; // Whether the cell was set by the app or by the user/solver
    
    /**
     * Cells should be initialized empty and with only a row and column
     * assigned, from which we can gather their square.
     * 
     * Note: initializing them with a value could be problematic because we'd
     * have to check potential values of other, possibly uninitialized, cells
     * @param row
     * @param col 
     */
    public Cell(int row, int col){
        assert(0 <= row && row < 9);
        assert(0 <= col && col < 9);
        this.row = row;
        this.col = col;
        this.square = getSquare(row, col);
        potentialValues = new ArrayList<>(VALID_VALUES);
    }
    
    /**
     * Give this cell a definite value. Null is acceptable for removing a value.
     * @param value 
     */
    public void setValue(Integer value){
        assert(VALID_VALUES.contains(value) || value == null);
        this.value = value;
    }
    
    public Integer getValue(){
        return this.value;
    }
    
    public boolean isEmpty(){
        return (this.value == null);
    }
    
    @Override
    public String toString(){
        String string = new String();
        string.concat("row " + this.row + " col " + this.col + " square " + this.square + "\n");
        string.concat("   value " + this.value + "\n");
        if (this.setInternally){
            string.concat("   set internally \n");
        } else {
            string.concat("   not set internally \n");
        }
        string.concat("   potential values: " + this.potentialValues.toString());
        return "row " + this.row + " col " + this.col;
    }
    
    public boolean isSetInternally(){
        return this.setInternally;
    }
}
