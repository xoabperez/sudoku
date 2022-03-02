package sudoku.grid;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.util.Pair;
import static sudoku.grid.Grid.NEW_CELL_VALUE;

/**
 * A group of 9 cells - either a row group, column group, or square group.
 */
public class CellGroup {
    public enum GroupType {ROW, COL, SQUARE};
    
    // Make it easier to identify issues
    private GroupType groupType;
    private int groupNumber;
    
    private final Cell[] cells = new Cell[9];
    private final ArrayList<Integer> missingValues = new ArrayList<>(Cell.VALID_VALUES);
    public final ArrayList<Integer> emptyCellInds = 
            new ArrayList<>(IntStream.range(0, 9).
                    mapToObj(Integer::new).collect(Collectors.toList()));
    
    PropertyChangeSupport propChangeSupport;
    
    public CellGroup(GroupType groupType, int groupNumber){
        this.groupType = groupType;
        this.groupNumber = groupNumber;
        this.propChangeSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * Add a cell to the group. For a row, the position will be the column; 
     * vice-versa for a column; and for a square, the position will be as
     * follows:
     * 
     * 0 1 2
     * 3 4 5
     * 6 7 8
     * 
     * @param position
     * @param cell 
     */
    public void addCell(int position, Cell cell) throws Exception{
        // Make sure we haven't put anything here yet
        if (cells[position] != null){
            System.out.println("Cell is being overwritten in " + groupId());
            throw new Exception();
        }
        
        // Make sure we don't have the cell's value yet
        if (!cell.isEmpty()){
            if (this.containsValue(cell.getValue())){
                System.out.println(groupId() + " already has value " + cell.getValue());
                throw new Exception();
            }
        }

        // TODO: maybe allow this later, it requires us to update not only group
        // propertie slike missingValues, but also the other cells' properties
        if (!cell.isEmpty()){
            System.out.println("CellGroups do not allow adding new cells with values");
            throw new Exception();
        }
        
        cells[position] = cell;
    }
    
    /**
     * Indicate that we've found a value in one of the cells, so update group
     * properties
     * @param value 
     */
    public void addFoundValue(int position, int value) throws Exception{
        if (cells[position] == null){
            System.out.println("We don't seem to have a cell at position " + 
                    position + " in this group.");
            throw new Exception();
        }
        
        // Remove the actual values, not the indices
        missingValues.remove((Object) value);
        emptyCellInds.remove((Object) position);
    }
    
    /**
     * If we've found a value for one of the cells, it should no longer be 
     * possible for other cells in this group.
     * @param value 
     */
    public void clearValueFromCells(int position, int value) throws Exception{
        addFoundValue(position, value);
        for (Integer ind : emptyCellInds){
            cells[ind].potentialValues.remove((Object) value);
        }
    }
    
    public Cell getCell(int position){
        return cells[position];
    }
    
    public boolean containsValue(int value){
        return !missingValues.contains(value);
    }
    
    /**
     * Within the group, check if there is only one empty cell that can house
     * any of the missing values.
     */ 
    public HashSet<Pair> lookForMissingValues(){
        HashSet<Pair> cellsToRemove = new HashSet<>();
        for (Iterator it = missingValues.iterator(); it.hasNext();){
            int value = (Integer) it.next();
            int numValidCells = 0;
            Integer lastValidCell = null;
            for (Integer ind : emptyCellInds){
                if (getCell(ind).potentialValues.contains(value)){
                    numValidCells++;
                    lastValidCell = ind;
                }
            }
            
            if (numValidCells == 1){
                Cell cell = getCell(lastValidCell);
                cellsToRemove.add(new Pair(cell, value));
            }
        }
        return cellsToRemove;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        propChangeSupport.addPropertyChangeListener(pcl);
    }
    
    public String groupId(){
        return (this.groupType.toString() + " " + this.groupNumber);
    }

    
    /** 
     * Check if there is a value that must go into a specific row of a
     * square group - then it can't go elsewhere in the row.
     */
//    public HashMap<Integer, Integer> checkValueMustBeInRow(){
//        if (!this.groupType.equals(GroupType.SQUARE)){
//            System.out.println("Can't look for square-specific values in rows/columns");
//            return null;
//        }
//        
//        HashMap<Integer, Integer> rowAndValue
//        for (MissingValue mv : this.missingValues.values()){
//            
//        }
//    }
        
    /**
     * This is kind of the counterpart to a cell - we'll look at the missing
     * values in each group and where they might fit. We'll only do this for
     * square groups now to help us determine if values have to stay in a 
     * row/col
     */
    public class MissingValue {
        public int value;

        // We'll want to keep track of whether possible placement is specified
        // in column or row order
        private BitSet potentialRowInds = new BitSet(9);
        private BitSet potentialColInds = new BitSet(9);
        private int numPotentialInds = 9;
        
        /**
         * For initialization, assume the missing value can go in any of the 
         * cells.
         * @param value 
         */
        public MissingValue(int value){
            this.value = value;
            this.potentialColInds.set(0, 9, true);
            this.potentialRowInds.set(0, 9, true);
        }
        
        /**
         * Since row order is typically used, that can be used as the interface
         * for this, though we'll also remove it from the column bitset 
         * @param i 
         */
        public void removePotentialRowInd(int i){
            this.potentialRowInds.set(i, false);
            this.potentialColInds.set(colOrder[i], false);
            this.numPotentialInds -= 1;
        }
        
        public boolean hasOneInd(){
            return numPotentialInds == 1;
        }
        
        public int oneInd(){
            return potentialRowInds.nextSetBit(0);
        }
        
        /**
         * Check whether the only possible spot for a value is in some row
         * @return 
         */
        public Integer isOnlyInRow(){
            return isOnlyInBitset(potentialRowInds);
        }
        
        public Integer isOnlyInCol(){
            return isOnlyInBitset(potentialColInds);
        }
        
        /**
         * Check whether a value is in the first 3, second 3, or third 3 
         * spots only
         * @param bitset
         * @return 
         */
        private Integer isOnlyInBitset(BitSet bitset){
            boolean inFirstRow = !bitset.get(0,3).isEmpty();
            boolean inSecondRow = !bitset.get(3,6).isEmpty();
            boolean inThirdRow = !bitset.get(6,9).isEmpty();
            
            if (inFirstRow && !inSecondRow && !inThirdRow){
                return 0;
            } else if (!inFirstRow && inSecondRow && !inThirdRow){
                return 1;
            } else if (!inFirstRow && !inSecondRow && inThirdRow){
                return 2;
            } else {
                return null;
            }
        }
    }
    
    /**
     * convert from one grid to another; input should be from:
     * 0 1 2
     * 3 4 5
     * 6 7 8
     * 
     * Output will be:
     * 0 3 6
     * 1 4 7
     * 2 5 8
     * 
     * @param i
     * @return 
     */
    private final static int[] colOrder = new int[]{0,3,6,1,4,7,2,5,8};
}
