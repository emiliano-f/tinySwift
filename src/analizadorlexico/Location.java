package analizadorlexico;

/**
 * Object that returns the current location in the file
 * 
 * @author D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico;
 */

class Location {
    
    // Position in file
    private int column;
    private int row;
    // Line of file
    private String line;
    // Literal to return
    private String literal;
    
    private boolean hasLine = true;
    private boolean hasLiteral = true;
    
    /**
     * @param column the current column in file
     * @param row the current row in file
     * @return void
     */
    Location(int column, int row){
        this(column, row, "");
        hasLine = false; // construct with only 2 arguments
    }
    
    /**
     * @param line the current line in file
     */
    Location(int column, int row, String line){
        this(column, row, line, "");
        hasLiteral = false;
    }
    
    /**
     * @param literal the string/int literal captured
     */
    Location(int column, int row, String line, String literal){
        this.row = row;
        this.column = column;
        this.line = line;
        this.literal = literal;
    }
    
    
    /**
     * @return the current column in file
     */
    public int getColumn(){
        return column;
    }
    
    /**
     * @return the current row in file
     */
    public int getRow(){
        return row;
    }
    
    /**
     * @return the current line of file
     */
    public String getLine(){
        return line;
    }
    
    /**
     * @return the literal captured
     */
    public String getLiteral(){
        return literal;
    }
    
    public boolean hasLine(){
        return hasLine;
    }
    
    public boolean hasLiteral(){
        return hasLiteral;
    }
}
