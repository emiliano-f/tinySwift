package analizadorlexico;

import java.util.Scanner;

/**
 * Implements methods for whitespaces in AnalizadorLexico class
 * 
 * @author D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico
 */

class WhitespacesManager extends Manager {
    
    private WhitespacesManager(){};
    
    /**
     * Deletes all whitespaces in a line.
     * 
     * @param line current line of file
     * @param row current number line in file
     * @param column current number column in file
     * @return Update row and column numbers
     */
    static Location consumption(String line,
                                int row, 
                                int column){
        
        while (isInLine(line, column) && 
               Character.isWhitespace(line.charAt(column))){
            column += 1;
        }
        
        return new Location(column, row);
    }
}
