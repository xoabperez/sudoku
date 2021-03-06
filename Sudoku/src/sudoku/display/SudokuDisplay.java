package sudoku.display;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JTable;
import sudoku.grid.Grid;

/**
 *
 * @author xoab
 */
public class SudokuDisplay extends javax.swing.JFrame {
    /**
     * Creates new form AppDisplay
     */
    public SudokuDisplay() {
        initComponents();
        sudokuTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        sudokuTable.setFillsViewportHeight(true);
        sudokuTable.setTableHeader(null); // Don't need headers
        
        // From https://stackoverflow.com/questions/29174942/make-jtable-rows-fill-the-entire-height-of-jscrollpane
        sudokuTable.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                sudokuTable.setRowHeight(16);
                Dimension p = sudokuTable.getPreferredSize();
                Dimension v = jScrollPane1.getViewportBorderBounds().getSize();
                if (v.height > p.height)
                {
                    int available = v.height - sudokuTable.getRowCount() * sudokuTable.getRowMargin();
                    int perRow = available / sudokuTable.getRowCount();
                    sudokuTable.setRowHeight(perRow);
                }
            }
        });
    }
    
    public void test() throws Exception{
        // Test with a puzzle from NYT
        Integer[][] testData = new Integer[9][9];
        testData[0] = new Integer[]{null,6,null,7,null,8,null,3,null};
        testData[1][5] = 1;
        testData[2] = new Integer[]{null,null,4,9,3,null,8,7,null};
        testData[3] = new Integer[]{null,3,null,null,null,null,1,8,null};
        testData[4][0] = 2; 
        testData[4][8] = 9;
        testData[5][2] = 7; 
        testData[5][3] = 5;
        testData[6] = new Integer[]{null,7,null,null,null,null,3,2,null};
        testData[7][7] = 4;
        testData[8][0] = 8;
        testData[8][5] = 6;

        Grid testGrid = new Grid(testData);
        sudokuTable.setGrid(testGrid);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        sudokuTable = new sudoku.display.SudokuTable();
        controlPanel = new javax.swing.JPanel();
        solverButton = new javax.swing.JButton();
        newGameButton = new javax.swing.JButton();
        clearGridButton = new javax.swing.JButton();
        setGridButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        sudokuTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        sudokuTable.setFillsViewportHeight(true);
        sudokuTable.setRowHeight(36);
        sudokuTable.setTableHeader(null);
        jScrollPane1.setViewportView(sudokuTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        controlPanel.setLayout(new java.awt.GridBagLayout());

        solverButton.setFont(new java.awt.Font("Ubuntu", 1, 20)); // NOI18N
        solverButton.setText("Solve");
        solverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solverButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        controlPanel.add(solverButton, gridBagConstraints);

        newGameButton.setFont(new java.awt.Font("Ubuntu", 1, 20)); // NOI18N
        newGameButton.setText("New Game");
        newGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        controlPanel.add(newGameButton, gridBagConstraints);

        clearGridButton.setFont(new java.awt.Font("Ubuntu", 1, 20)); // NOI18N
        clearGridButton.setText("Clear Grid");
        clearGridButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearGridButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        controlPanel.add(clearGridButton, gridBagConstraints);

        setGridButton.setFont(new java.awt.Font("Ubuntu", 1, 20)); // NOI18N
        setGridButton.setText("Set Grid");
        setGridButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setGridButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        controlPanel.add(setGridButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(controlPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void solverButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solverButtonActionPerformed
        new Thread(() ->  {
            try {
                this.sudokuTable.solve();
            } catch (Exception ex) {
               System.out.println("Solver's got problems, I'm afraid you're on your own.");
               ex.printStackTrace();
            }
        }).start();
    }//GEN-LAST:event_solverButtonActionPerformed

    private void clearGridButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearGridButtonActionPerformed
        sudokuTable.clear();
    }//GEN-LAST:event_clearGridButtonActionPerformed

    private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameButtonActionPerformed
        try {
            sudokuTable.startNewGame();
        } catch (Exception ex) {
            System.out.println("Unable to start a new game");
        }
    }//GEN-LAST:event_newGameButtonActionPerformed

    private void setGridButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setGridButtonActionPerformed
        sudokuTable.setGrid();
    }//GEN-LAST:event_setGridButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SudokuDisplay display = new SudokuDisplay();
                display.setVisible(true);
                display.pack();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearGridButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton newGameButton;
    private javax.swing.JButton setGridButton;
    private javax.swing.JButton solverButton;
    private sudoku.display.SudokuTable sudokuTable;
    // End of variables declaration//GEN-END:variables
}
