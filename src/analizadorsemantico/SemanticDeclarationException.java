package analizadorsemantico;

/**
 *
 * @author emiliano
 */
public class SemanticDeclarationException extends Exception {
    
    private int row;
    private int column;
    
    /**
     * Constructor for SemanticDeclarationException
     * 
     * @param description the description to throws
     * @param row the line number
     * @param column the column number
     */
    public SemanticDeclarationException(String description,
                                  int row,
                                  int column){
        
        super(description);
        this.row = row;
        this.column = column;
    }
    
    /**
     * Returns the line number
     * 
     * @return the line number
     */
    public int getLine(){
        return row;
    }
    
    /**
     * Returns the column number
     * 
     * @return the column number
     */
    public int getColumn(){
        return column;
    }
}
