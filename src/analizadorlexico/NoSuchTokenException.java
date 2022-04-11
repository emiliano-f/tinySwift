package analizadorlexico;

/**
 * Exception definition for Lexical Analyzer. TinySwift+
 * @author: D. Emiliano F.
 * @version: 0.01
 * @see analizadorlexico.AnalizadorLexico;
 */

public class NoSuchTokenException extends Exception {
    
    /**
     * @param description error description
     */
    public NoSuchTokenException(String description) {
        super(description);
    }

}
