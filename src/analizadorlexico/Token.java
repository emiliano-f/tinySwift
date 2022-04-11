package analizadorlexico;

/**
 * Object that returns token, lexeme and current line in file
 * 
 * @author: D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico;
 */

class Token {
    
    private String token;
    private String lexem;
    private int var;
    
    /**
     * @param token the name of value
     * @param lexem the value of token
     * @param var ubication in file or length ef lexeme
     */
    Token(String token,
          String lexem,
          int var){
        
        if (var < 1){
            throw new IllegalArgumentException("Invalid line number");
        }
        this.token = token;
        this.lexem = lexem;
        this.var = var;
    }
    
    public String getToken(){
        return token;
    }
    
    public String getLexeme(){
        return lexem;
    }
    
    public int getLine(){
        return var;
    }
    
    public int getLexemeLength(){
        return var;
    }
}
