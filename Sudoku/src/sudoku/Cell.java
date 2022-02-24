package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static sudoku.Grid.getGroup;

/**
 *
 * @author xoab
 */
public class Cell {
    public static final List<Integer> VALID_VALUES = Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9});
    public static final int EMPTY_VALUE = -1;
    private int value = EMPTY_VALUE;
    public int row;
    public int col;
    public int group; // 3x3 groups
    public List<Integer> potentialValues;
    
    /**
     * Many new cells will be initialized empty and with only a row and column
     * assigned, from which we can gather their group.
     * @param row
     * @param col 
     */
    public Cell(int row, int col){
        assert(0 <= row && row < 9);
        assert(0 <= col && col < 9);
        this.row = row;
        this.col = col;
        this.group = getGroup(row, col);
        potentialValues = new ArrayList<>(VALID_VALUES);
    }
    
    /** 
     * Some cells will be created with a value.
     * @param row
     * @param col
     * @param value 
     */
    public Cell(int row, int col, int value){
        this(row, col);
        this.setValue(value);
    }
    
    /**
     * Give this cell a definite value.
     * @param value 
     */
    public void setValue(int value){
        assert(VALID_VALUES.contains(value));
        this.value = value;
        this.potentialValues.clear();
    }
    
    public int getValue(){
        return this.value;
    }
    
    public boolean isEmpty(){
        return (this.value == EMPTY_VALUE);
    }
}
