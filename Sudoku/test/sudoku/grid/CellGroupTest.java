/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package sudoku.grid;

import java.util.HashMap;
import sudoku.grid.Cell;
import sudoku.grid.CellGroup;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sudoku.grid.CellGroup.GroupType;

/**
 *
 * @author xoab
 */
public class CellGroupTest {
    
    CellGroup instance;
    
    public CellGroupTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        // Add cells. No values since that'll muck things up when adding found value
        instance = new CellGroup(GroupType.SQUARE, 0);
        for(int i = 0; i < 9; i++){
            Cell cell = new Cell(i, 0);
            instance.addCell(i, cell);
        }
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addCell method, of class CellGroup.
     */
    @Test
    public void testAddCell() {
        System.out.println("addCell");
        try {
            int position = 0;
            Cell cell = new Cell(1,2);
            instance = new CellGroup(GroupType.COL, 0);
            instance.addCell(position, cell);

            // Make sure we get the correct cell back
            Cell returnCell = instance.getCell(position);
            assertEquals(returnCell.row, 1);
            assertEquals(returnCell.col, 2);
        } catch (Exception ex){
            fail("Exception in this test.");
        }
    }

    /**
     * Test of addFoundValue method, of class CellGroup.
     */
    @Test
    public void testAddFoundValue() {
        System.out.println("addFoundValue");
        int position = 1;
        int value = 2;            
        try {   
            instance.addFoundValue(position, value);

            // Since the value has been found, it's not longer missing from the 
            // group, and we know the position isn't empty
            assert(instance.containsValue(value));
            assert(!instance.emptyCellInds.contains(position));
        } catch (Exception ex){
            fail("Exception in this test.");
        }
    }

    /**
     * Test of lookForMissingValues method, of class CellGroup.
     */
    @Test
    public void testLookForMissingValues() {
        
        System.out.println("lookForMissingValues");
            
        try {
            // Pretend we've found all other values
            for(int i = 0; i < 8; i++){
                instance.clearValueFromCells(i, i+1);
            }
            
            HashMap<Cell, Integer> cellsToRemove = instance.lookForMissingValues();
            assert(cellsToRemove.get(instance.getCell(8)) == 9);
        } catch (Exception ex) {
            fail("Exception in this test");
        }
    }

    /**
     * Test of clearValueFromCells method, of class CellGroup.
     */
    @Test
    public void testClearValueFromCells() throws Exception {
        System.out.println("clearValueFromCells");
        instance.clearValueFromCells(0, 1);
        assert(!instance.getCell(1).potentialValues.contains(1));
    }

    
}
