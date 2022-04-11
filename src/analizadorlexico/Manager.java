package analizadorlexico;

import java.util.ArrayList;

/**
 * Utilities for AnalizadorLexico class
 * 
 * @author D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico
 */
abstract class Manager {
    
    /**
     * @param column the current column in line
     * @param line the current line in file
     * @return true if column is in [0: line.length())
     */
    static boolean isInLine(String line,
                            int column){
        return (column < line.length());
    }
    
    /**
     * 
     * @param value the value to add in column
     * @return true if column+value is in [0: line.length())
     */
    static boolean isInLine(String line,
                            int column,
                            int value){
        return (column + value < line.length());
    }
    
    /**
     * @param file the ArrayList that contains all the lines of the file
     * @param line the current line of file
     * @param row the current row (line number)
     * @param column the current column in line
     * @return Location object with new (last) row, column and line.
     * @throws NoSuchTokenException if there are not more lines (EOF)
     */
    static Location skipLine(ArrayList file,
                             String line,
                             int row, 
                             int column)
    
        throws NoSuchTokenException {
        
        if (row >= file.size()){
            throw new NoSuchTokenException("End of file");
        }
        
        line = (String) file.get(row);
        row += 1; // new current line in file
        column = 0; // reset column
        return new Location (column, row, line);
    }

}
