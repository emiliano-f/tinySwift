package analizadorsemantico;

/**
 *
 * @author emiliano
 */
public class SemanticSentenceException extends Exception {
    private final int row;
    
    /**
     * Constructor for SemanticDeclarationException
     * 
     * @param description the description to throws
     * @param row the line number
     */
    public SemanticSentenceException(String description,
                                     int row){
        
        super(description);
        this.row = row;
    }
    
    /**
     * Returns the line number
     * 
     * @return the line number
     */
    public int getLine(){
        return row;
    }
}