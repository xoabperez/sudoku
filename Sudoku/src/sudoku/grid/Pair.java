package sudoku.grid;

/**
 * Just a class to hold a row/col pair.
 */
public class Pair{

    private final int row;
    private final int col;

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Pair(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        
        if(obj == null || obj.getClass()!= this.getClass()){
            return false;
        }
        
        Pair pair = (Pair) obj;
        return (pair.row == this.row && pair.col == this.col);
    }
      
    // From https://stackoverflow.com/a/24262965
    @Override
    public int hashCode(){
        return new Integer(row).hashCode() * 31 + new Integer(col).hashCode();
    }
}
