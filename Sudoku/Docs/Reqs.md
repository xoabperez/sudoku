Reqs/spec?
1. Display is a sudoku grid
    - 9x9 JTable with no column/row labels and with thicker lines separating groups
1. Upon starting a game, a new game should appear
    - The grid associated with the display will be randomly set
1. User should be able to start a new game at any time
    - A "New Game" button will set the grid to a new random grid
    - **??But how??**
1. User should be able to set the difficulty of a new game
    - **?? New game button pops up window with difficulty level, new grid has X number of spots filled?**
1.  User should be able to have the game be solved at any time
    - Solve button solves the current game
    - **?? What if it's not solvable??**
    - **?? What if the user puts values such that the grid is no longer solvable??**
    - **?? Should the grid be solved as soon as it's setup?? Then we can automatically know if the user put an unsolvable grid**
    - **?? If we have the grid solved, we can tell the user whether they put in a wrong value**
1. User should be able to make their own game at any time
    - A "Clear Game" button resets the grid and the "Set Grid" button sets the grid to the GUI
    - **?? Related to previous point - should we check their grid??**
1. User should be aware of which values are from the game and which values are theirs
    - Show the game values with a different background and make then non-editable
1. The user should be informed if a value they enter cannot be placed
    - If user puts non-{1-9} value, or if the value can't fit because of row/col/group with that number, add red background and prevent user from entering data


