package sudoku.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A group of 9 cells - either a row group, column group, or square group.
 */
public class CellGroup {
    public enum GroupType {ROW, COL, SQUARE};
    
    // Make it easier to identify issues
    private final GroupType groupType;
    private final int groupNumber;
    
    private final Cell[] cells = new Cell[9];
    private final ArrayList<Integer> missingValues = new ArrayList<>(Cell.VALID_VALUES);
    public final ArrayList<Integer> emptyCellInds = 
            new ArrayList<>(IntStream.range(0, 9).
                    mapToObj(Integer::new).collect(Collectors.toList()));
        
    public CellGroup(GroupType groupType, int groupNumber){
        this.groupType = groupType;
        this.groupNumber = groupNumber;
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
     * @throws java.lang.Exception 
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
     * @param position
     * @param value 
     * @throws java.lang.Exception 
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
     * @param position
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
     * @return cellsToRemove A Map<Cell, Integer> of cells and their found values
     */ 
    public HashMap<Cell, Integer> lookForMissingValues(){
        HashMap<Cell, Integer> cellsToRemove = new HashMap<>();
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
                cellsToRemove.put(cell, value);
            }
        }
        return cellsToRemove;
    }
    
    public String groupId(){
        return (this.groupType.toString() + " " + this.groupNumber);
    }
}
