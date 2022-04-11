package analizadorlexico;

/**
 * Exception definition for Lexical Analyzer. TinySwift+
 * @author: D. Emiliano F.
 * @version: 0.01
 * @see analizadorlexico.AnalizadorLexico;
 */

public class IllegalTokenException extends Exception {
    
    int row;
    int column;
    String description;
    
    /**
     * @param row current row in file
     * @param column current column in file
     * @param description error description
     */
    public IllegalTokenException(int row,
                                 int column,
                                 String description) {
        super(description);
        this.row = row;
        this.column = column;
    }
    
    public int getLine(){
        return row;
    }
    
    public int getColumn(){
        return column;
    }
}
