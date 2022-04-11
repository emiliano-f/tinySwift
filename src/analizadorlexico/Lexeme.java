package analizadorlexico;

/**
 * Lexeme class for tinySwift+
 * @author D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico
 */

class Lexeme {
    
    private boolean hasLexeme;
    private String lexeme;
    
    Lexeme (String lexeme, boolean hasLexeme){
        this.lexeme = lexeme;
        this.hasLexeme = hasLexeme;
    }
    
    public String getLexeme(){
        return lexeme;
    }
    
    public boolean hasLexeme(){
        return hasLexeme;
    }
}