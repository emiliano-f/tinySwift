package analizadorlexico;

/**
 * Methods for literals in AnalizadorLexico class
 * 
 * @author: D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico;
 */

import java.util.ArrayList;

public class LiteralsManager extends Manager {
    private LiteralsManager(){}
    
    /**
     * This method captures string literals adding a '"' symbol 
     * Precondition: capture all the characters until finding the '"' character
     * 
     * @param file the ArrayList that contains all lines of file
     * @param line the current line in file
     * @param row the current row (line number)
     * @param column the current column in line
     * @return Line, row and column updated
     * @throws NoSuchTokenException if the string is incomplete ('"' not found)
     */
    static Location stringConsumption(ArrayList file,
                                      String line,
                                      int row,
                                      int column)
        throws IllegalTokenException {
            
        /*
         * Adding a symbol will allow us to disting
         * an id from a stringlit
         */
        String literal = "\""; 
        
        try{
            // Search end of string literal
            while (line.charAt(column) != '"') {
                // Search of '/' or '"'
                if (line.charAt(column) == '\\'){
                    column += 1;
                    switch(line.charAt(column)){
                        case 'r':
                        case 't':
                        case 'n':
                            literal += '\\';
                        case '\\':
                        case '"':
                            literal += line.charAt(column);
                            break;
                        default:
                            throw new IllegalTokenException(row,
                                                            column-1,
                                                            "Invalid escape sequence in literal");
                    }
                }
                else{
                    literal += line.charAt(column);
                }
                column += 1;                
            } 
        }
        catch (IndexOutOfBoundsException e){
            throw new IllegalTokenException(row,
                                            column-1,
                                            "Unterminated string literal");
        }
        
        column += 1;
        return new Location(column, row, line, literal);
        
    }
    
    
    static Location charConsumption(ArrayList file,
                                    String line,
                                    int row,
                                    int column)
        throws NoSuchTokenException {
            
        String literal = "'";
        
        try{
            if (line.charAt(column) == '\\'){
                literal += "\\";
                column += 1;
                switch(line.charAt(column)){
                    case 'r':
                    case 't':
                    case 'n':
                    case '\'':
                    case '\\':
                        literal += line.charAt(column);
                        break;
                    default:
                        throw new NoSuchTokenException("Invalid escape sequence"
                                                       +" in character literal");
                }
            }
            else{
                literal += String.valueOf(line.charAt(column));
            }
            
            column += 1;
            
            if (line.charAt(column) != '\''){
                throw new NoSuchTokenException("Unterminated char literal");
            }
        }
        catch (IndexOutOfBoundsException e){
            throw new NoSuchTokenException("Unterminated char literal");
        }
        
        column += 1; 
        return new Location(column, row, line, literal); 
        
    }
    
    
    /**
     * Determines if an lexeme is Int literal
     * 
     * @param lexeme the lexeme
     * @return true if is int literal
     */
    static boolean isIntLiteral(String lexeme) {
        
        int i = 0;
        while (i<lexeme.length() &&
               Character.isDigit(lexeme.charAt(i))){
            i++;
        }
        if (i < lexeme.length()){
            return false;
        }
        return true;
    }
    
}
