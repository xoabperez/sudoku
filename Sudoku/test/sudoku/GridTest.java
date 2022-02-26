/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package sudoku;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author xoab
 */
public class GridTest {
    
    public GridTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setCellValue method, of class Grid.
     */
    @Test
    public void testSetCellValue() throws Exception {
        System.out.println("setCellValue");
        int row = 0;
        int col = 0;
        int value = 0;
        Grid instance = new Grid();
        boolean expResult = false;
        boolean result = instance.setCellValue(row, col, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkValueValidInGrid method, of class Grid.
     */
    @Test
    public void testCheckValueValidInGrid() {
        System.out.println("checkValueValidInGrid");
        int row = 0;
        int col = 0;
        int value = 0;
        Grid instance = new Grid();
        boolean expResult = false;
        boolean result = instance.checkValueValidInGrid(row, col, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkValueValidInRow method, of class Grid.
     */
    @Test
    public void testCheckValueValidInRow() {
        System.out.println("checkValueValidInRow");
        CellGroup cellRow = null;
        Integer value = null;
        Grid instance = new Grid();
        boolean expResult = false;
        boolean result = instance.checkValueValidInRow(cellRow, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkValueValidInCol method, of class Grid.
     */
    @Test
    public void testCheckValueValidInCol() {
        System.out.println("checkValueValidInCol");
        CellGroup cellCol = null;
        Integer value = null;
        Grid instance = new Grid();
        boolean expResult = false;
        boolean result = instance.checkValueValidInCol(cellCol, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkValueValidInSquare method, of class Grid.
     */
    @Test
    public void testCheckValueValidInSquare() {
        System.out.println("checkValueValidInSquare");
        CellGroup cellSquare = null;
        Integer value = null;
        Grid instance = new Grid();
        boolean expResult = false;
        boolean result = instance.checkValueValidInSquare(cellSquare, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of solve1 method, of class Grid.
     */
    @Test
    public void testSolve1() throws Exception {
        System.out.println("solve1");
        Grid instance = new Grid();
        instance.solve1();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCellAt method, of class Grid.
     */
    @Test
    public void testGetCellAt() {
        System.out.println("getCellAt");
        int row = 0;
        int col = 0;
        Grid instance = new Grid();
        Cell expResult = null;
        Cell result = instance.getCellAt(row, col);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValueAt method, of class Grid.
     */
    @Test
    public void testGetValueAt() {
        System.out.println("getValueAt");
        int row = 0;
        int col = 0;
        Grid instance = new Grid();
        Integer expResult = null;
        Integer result = instance.getValueAt(row, col);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPropertyChangeListener method, of class Grid.
     */
    @Test
    public void testAddPropertyChangeListener() {
        System.out.println("addPropertyChangeListener");
        PropertyChangeListener pcl = null;
        Grid instance = new Grid();
        instance.addPropertyChangeListener(pcl);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSquare method, of class Grid.
     */
    @Test
    public void testGetSquare() {
        System.out.println("getSquare");
        int row = 0;
        int col = 0;
        int expResult = 0;
        int result = Grid.getSquare(row, col);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSquarePosition method, of class Grid.
     */
    @Test
    public void testGetSquarePosition() {
        System.out.println("getSquarePosition");
        int row = 0;
        int col = 0;
        int expResult = 0;
        int result = Grid.getSquarePosition(row, col);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of propertyChange method, of class Grid.
     */
    @Test
    public void testPropertyChange() {
        System.out.println("propertyChange");
        PropertyChangeEvent pce = null;
        Grid instance = new Grid();
        instance.propertyChange(pce);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
