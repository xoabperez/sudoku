package sudoku.grid;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
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
        if (missingValues.contains(value)){
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Within the group, check if there is only one empty cell that can house
     * any of the missing values.
     */ 
    public HashSet<Pair> lookForMissingValues(){
        HashSet<Pair> removedCells = new HashSet<>();
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
            
            // Update the cell and let everybody else know.
            if (numValidCells == 1){
                it.remove();
                Cell cell = getCell(lastValidCell);
                cell.setValue(value);
                removedCells.add(new Pair(cell.row, cell.col));
                propChangeSupport.firePropertyChange(NEW_CELL_VALUE, null, cell);
            }
        }
        return removedCells;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl){
        propChangeSupport.addPropertyChangeListener(pcl);
    }
    
    public String groupId(){
        return (this.groupType.toString() + " " + this.groupNumber);
    }
}
