package sudoku.grid;

import java.util.Objects;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author xoab
 */
public class CellTest {
    
    public CellTest() {
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
     * Test of setValue method, of class Cell.
     */
    @Test
    public void testSetValue() {
        System.out.println("setValue");
        Integer value = 1;
        Cell instance = new Cell(0, 0);
        assert(instance.getValue() == null);
        instance.setValue(value);
        assert(Objects.equals(instance.getValue(), value));
    }

    /**
     * Test of isEmpty method, of class Cell.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        Cell instance = new Cell(0,0);
        assert(instance.isEmpty());
        
        instance.setValue(1);
        assert(!instance.isEmpty()); 
    }    
}
