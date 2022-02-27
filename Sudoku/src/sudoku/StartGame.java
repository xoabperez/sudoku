package sudoku;

import sudoku.display.SudokuDisplay;

/**
 *
 * @author xoab
 */
public class StartGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {       
        SudokuDisplay display = new SudokuDisplay();
        display.pack();
        display.setVisible(true);
        
        try {
            display.test();
        } catch (Exception ex) {
            System.out.println("Error setting up test grid");
        }
    }    
}
