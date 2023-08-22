package analizadorlexico;

/**
 * Object that returns token, lexeme and current line in file
 * 
 * @author D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico;
 */

public class Token {
    
    private String token;
    private String lexem;
    private int row;
    private int column;
    
    /**
     * @param token the name of value
     * @param lexem the value of token
     * @param var ubication in file or length ef lexeme
     */
    public Token(String token,
                 String lexem,
                 int row,
                 int column){
        
        if (row < 1){
            throw new IllegalArgumentException("Invalid line number");
        }
        this.token = token;
        this.lexem = lexem;
        this.row = row;
        this.column = column;
    }
    
    public String getToken(){
        return token;
    }
    
    public String getLexeme(){
        return lexem;
    }
    
    public int getLine(){
        return row;
    }
    
    public int getColumn(){
        return column;
    }
    
    public int getLexemeLength(){
        return row;
    }
}
