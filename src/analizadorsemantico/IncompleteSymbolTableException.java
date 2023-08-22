package analizadorsemantico;

/**
 *
 * @author emiliano
 */
public class IncompleteSymbolTableException extends SemanticDeclarationException {
    String description;
    
    /**
     * Constructor for IncompleteSymboltableException
     * 
     * @param description error description
     */
    public IncompleteSymbolTableException(String description) {
        super(description, 0, 0);
    }
    
}
