package sudoku;

import sudoku.display.SudokuDisplay;

public class StartGame {

    /**
     * Start the game by displaying the GUI.
     * @param args the command line arguments, unused
     */
    public static void main(String[] args) {       
        SudokuDisplay display = new SudokuDisplay();
        display.pack();
        display.setVisible(true);
        
        // For now, I only have 1 grid to test with; need to be able to make 
        // new ones
        try {
            display.test();
        } catch (Exception ex) {
            System.out.println("Error setting up test grid");
        }
    }    
}
