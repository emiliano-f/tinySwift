package analizadorlexico;

/**
 * Utilities for AnalizadorLexico class
 * 
 * @author: D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico;
 */

import java.util.ArrayList;


public class CommentsManager extends Manager {
    
    private CommentsManager() {};
    
    /**
     * This method deletes comment if it finds it in the first two symbols
     * Precondition: only checks first and second symbol of current line
     * 
     * @param file the ArrayList that contains all lines of file
     * @param line the current line in file
     * @param row the current row (line number)
     * @param column the current column in line
     * @return Line, row and column updated
     * @throws NoSuchTokenException if the comment is incomplete (comment block)
     */
    static Location extract(ArrayList file,
                            String line,
                            int row,
                            int column)
    
        throws NoSuchTokenException,
               IllegalTokenException {
        
        Location toReturn = new Location(column, row, line);
        int errCol = column-1;
        
        // Check comment type
        try{
            if (line.charAt(column) == '/'){
                toReturn = skipLine(file, line, row, column);
            }
            else{
                if (line.charAt(column) == '*'){
                    toReturn = deleteCommentBlock(file, line, row, column+1);
                }
            }
        }
        catch (NoSuchTokenException e){
            throw new IllegalTokenException(row, errCol, "Incomplete comment");
        }
        return toReturn;
    }
    
    /**
     * This method deletes comment if it finds it in the first two symbols
     * Precondition: only checks first and second symbol of current line
     * 
     * @param file the ArrayList that contains all lines of file
     * @param line the current line in file
     * @param row the current row (line number)
     * @param column the current column in line
     * @return Line, row and column updated
     * @throws NoSuchTokenException if the comment is incomplete (comment block)
     */
    static Location deleteCommentBlock(ArrayList file,
                                       String line,
                                       int row,
                                       int column)
        throws NoSuchTokenException{
        
        Location temp = new Location(column,row,line);
        // index of '*'
        int astIndex = -1;
        
        while (astIndex == -1){
            
            // Current index (column) in line
            if (isInLine(line, column)){
                
                // Search '*'
                astIndex = line.indexOf('*', column);
                // Not Found
                if (astIndex == -1){
                    
                    // Next line
                    temp = skipLine(file, line, row, column);
                    row = temp.getRow();
                    column = temp.getColumn();
                    line = temp.getLine();
                }
                
                // Found!
                else{
                    
                    // Search '/'
                    if (!isInLine(line, astIndex+1) ||
                        line.charAt(astIndex+1) != '/'){
                        
                        column++;
                        astIndex = -1;
                    }
                }
            }
            
            // Next line
            else{
                temp = skipLine(file, line, row, column);
                row = temp.getRow();
                column = temp.getColumn();
                line = temp.getLine();
            }
        }
        
        return new Location(astIndex+2,row,line);
    }
}
