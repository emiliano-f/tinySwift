package analizadorlexico;

/**
 * Exception definition for Lexical Analyzer. TinySwift+
 * 
 * @author D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico
 */

class NoSuchTokenException extends Exception {
    
    /**
     * @param description error description
     */
    public NoSuchTokenException(String description) {
        super(description);
    }

}
