package analizadorsintactico;

/**
 * Exception definition for Lexical Analyzer. TinySwift+
 * 
 * @author D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico
 */

public class SyntacticErrorException extends Exception {

    private int row;
    private int column;
    String description;
    
    /**
     * @param row current row in file
     * @param description error description
     */
    public SyntacticErrorException(int row,
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
