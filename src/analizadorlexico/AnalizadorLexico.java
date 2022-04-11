package analizadorlexico;

// Files handle
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
// Table
import java.util.ArrayList;
// Exceptions
import java.util.NoSuchElementException;

/**
 * Lexical Analyzer definition for tinySwift+
 * @author: D. Emiliano F.
 * @see ejecutador.Ejecutador
 */

class AnalizadorLexico {
    
    // Current line in file
    private int row;
    // Current column in String line
    private int column;
    // Current read line
    private String line;
    // File (already opende)
    private ArrayList<String> file = new ArrayList();
    // Tokens Table 
    private ArrayList<Token> table;
    
    /**
     * Class constructor
     * Checks if it is possible to open the file and load values
     * 
     * @param args the command line arguments
     * @throws IOException
     * @throws NoSuchElementException
     * @throws IllegalStateException 
     */
    public AnalizadorLexico(String args[])
        throws IOException, 
               NoSuchElementException,
               IllegalStateException {
        
        if (validArgs(args)){
            
            try(Scanner input = new Scanner(Paths.get(args[0]))){
                while (input.hasNextLine()){
                    file.add(input.nextLine());
                }
                row = 0;
                column = 0;
                line = "";
                table = loadTable();
            }
            catch (IOException | NoSuchElementException | IllegalStateException e) {
                e.printStackTrace();
                throw e;
            }
            
        }
        
        
    }
    
    /**
     * @param args the command line arguments
     * @return source file name validity
     */
    private boolean validArgs(String[] args) {
        
        // Number of arguments: 1 -> ARCHIVO_FUENTE 
        if (args.length != 1){
            throw new IllegalArgumentException("Invalid number of arguments");
        }
        
        // index of '.'
        int dot = args[0].lastIndexOf('.');
        if (dot == -1 || !args[0].regionMatches(true, dot+1, "swift", 0, 5)){
            throw new IllegalArgumentException("Invalid name of file");
        }
    
        return true;
    }
    
    /**
     * Returns next token from file
     * 
     * @return Token object that contains token, lexeme and line number
     * @throws NoSuchTokenException
     * @throws IllegalTokenException 
     */
    public Token nextToken() 
        throws NoSuchTokenException, IllegalTokenException{
        
        Lexeme temp;
        
        //Loops if there are comments
        do{
            if (!hasNextValidSymbol()){
                throw new NoSuchTokenException("Token not found");
            }
            temp = getLexeme();
        } while(!temp.hasLexeme());
        
        String lexeme = temp.getLexeme();
        String token = getToken(lexeme);
        
        // Delete first symbol '"' from stringlit
        if (token.equals("stringlit") || token.equals("charlit")){
            lexeme = lexeme.substring(1, lexeme .length());
        }
        
        return new Token(token,
                         lexeme, 
                         getRow());
    }
    
    /**
     * Obtains a lexeme from file (implements AFD)
     * Precondition: search in first symbol (column value)
     * That implies no whitespaces.
     * @see hasNextValidSymbol()
     * 
     * @return Lexeme object that contains an availability indicator and lexeme 
     * @throws IllegalTokenException
     * @throws NoSuchTokenException 
     */
    private Lexeme getLexeme()
            throws IllegalTokenException,
                   NoSuchTokenException {
        
        /**
         * 1. Deleted whitespaces (by hasNextValidSymbol())
         * 2. At the first symbol, construct lexeme
         */
        
        Location temp;
        // Save first symbol in current column
        char first = line.charAt(getColumn());
        // Save symbol in lexeme
        String lexeme = String.valueOf(first);
        // We have a lexeme. False when gets a comment
        boolean hasLexeme = true;
        // Next column
        addColumn(1);
        
        // Checks if it's a digit
        if (Character.isDigit(first)){
            // It's an int literal
            while (isInLine() && Character.isDigit(line.charAt(getColumn()))){
                
                lexeme += line.charAt(getColumn());
                addColumn(1);
            }
        }
        else{
            
            // It's a id
            if (Character.isLetter(first) || first == '_'){
                                
                while (isInLine() &&
                       (Character.isLetter(line.charAt(getColumn())) ||
                       Character.isDigit(line.charAt(getColumn())) ||
                       line.charAt(getColumn()) == '_')){
                    
                    lexeme += line.charAt(getColumn());
                    addColumn(1);
                }
            }
            else{
                
                /*
                 * Switch for other lexemes
                 * default: invalid character
                 */
                switch (first) {
                    
                    // possibly double symbols:
                    case '|':
                        if (!isInLine() || (line.charAt(getColumn()) != '|')){
                            throw new IllegalTokenException(getRow(),
                                                            getColumnFile(),
                                                            "Token not valid");
                        }
                        lexeme += "|";
                        break;

                    case '&':
                        if (!isInLine() || (line.charAt(getColumn()) != '&')){
                            throw new IllegalTokenException(getRow(),
                                                            getColumnFile(),
                                                            "Token not valid");
                        }
                        lexeme += "&";
                        break;

                    case '+':
                    case '-':
                    case '*':
                    case '<':
                    case '>':
                    case '!':
                    case '=':
                        if (isInLine()){
                            if (line.charAt(getColumn()) == '='){
                                lexeme += "=";
                            }
                            else{
                                addColumn(-1);
                            }
                        }
                        break;

                    //One symbol
                    case ',':
                    case '.':
                    case '{':
                    case '}':
                    case '(':
                    case ')':
                    case '[':
                    case ']':
                    case '%':
                    case ';':
                    case ':':
                        // have already been added symbol
                        //Later, by default, we add a column.
                        addColumn(-1);
                        break;
                    
                    // Char literal
                    case '\'':
                        temp = LiteralsManager.charConsumption(file,
                                                               line,
                                                               getRow(),
                                                               getColumn());
                        update(temp);
                        addColumn(-1);
                        lexeme = temp.getLiteral();
                        break;
                        
                    // String literal
                    case '"':
                        // Get string literal
                        temp = LiteralsManager.stringConsumption(file,
                                                                 line,
                                                                 getRow(),
                                                                 getColumn());
                        // Update last line, row and column from LiteralManager
                        update(temp);
                        addColumn(-1);
                        // innecesary? 
                        //if (temp.hasLiteral()){
                        lexeme = temp.getLiteral();
                        //}
                        break;
                    // Comment or division
                    case '/': 

                        // If true, it's a comment or assigment
                        // If false, it's a division
                        if (isInLine()){
                            char second = line.charAt(getColumn());

                            // Is assignment?
                            if (second == '='){
                                lexeme += "=";
                            }
                            else{

                                // Is comment?
                                if (second == '/' || second == '*'){
                                    temp = CommentsManager.extract(file, line, row, column);
                                    update(temp);
                                    /**
                                     * temp.getColumn() contains the immediate column
                                     * to continue reading. Later, by default, 
                                     * we add a column.
                                     */
                                    addColumn(-1);
                                    // Remove first /
                                    lexeme = "";
                                    hasLexeme = false;
                                }
                            }
                        }
                        break;
                    default:
                        throw new IllegalTokenException(getRow(),
                                                        getColumn(),
                                                        "Invalid character");
                }
                // Update column when leaving switch block
                addColumn(1);
            }
        }
        return new Lexeme(lexeme, hasLexeme);
    }
    
    /**
     * Searches next valid character: deletes whitespaces and empty lines
     * 
     * @return true if there is valid symbol
     */
    private boolean hasNextValidSymbol(){
        
        /**
         * Calling hasNextValidSymbol() method, we could have:
         * 1. First symbol in line (col = 0) [VALID]
         * 2. Any symbol in line (col in (0:line.length()-1] ) [VALID]
         * 3. Out of line (col = line.length()) [INVALID]
         * 
         * Additionally, this method moves row and column to first symbol != " "
         */
        
        // whitespacesConsumption method works checking (1) and (2) items
        Location upd = WhitespacesManager.consumption(line, getRow(), getColumn());
        update(upd);
        
        // in case of (3) item
        while (!isInLine()){
                
            // Is there more lines?
            if (getRow() < file.size()){
                line = file.get(getRow());
                // Set row++ and column=0
                addRow(1);
                resetColumn();
            }
            else{
                return false;
            }
            
            // Skip whitespaces
            upd = WhitespacesManager.consumption(line, getRow(), getColumn());
            update(upd);
        }
        // At this point, (1) and (2) conditions are true and there is symbol
        return true;
    }
    

    /**
     * Updates received line, row and column attributes
     * 
     * @param data the object with the new values
     */
    private void update(Location data){
        row = data.getRow();
        column = data.getColumn();
        
        // hasLine() method indicates us if we should update line
        // (false if we continue on the same line)
        if (data.hasLine()){
            line = data.getLine();
        }
    }
    
    /**
     * Gets token.
     * 
     * @param lexeme the lexeme to search for its corresponding token
     * @return the string token
     */
    private String getToken(String lexeme){
        
        
        if (lexeme.charAt(0) == '\''){
            return "charlit";
        }
        
        // String literal begins with '"'
        // This case occurs with empty literals
        // Second condition differentiates empty string from "\"" string
        if (lexeme.charAt(0) == '"'){
            return "stringlit";
        }
        
        if (Character.isDigit(lexeme.charAt(0))){
            // This is necessary?
            if (LiteralsManager.isIntLiteral(lexeme)){
                return "intlit";
            }
        }
        
        int idx = binarySearch(lexeme);
        // Index not found in table
        if (idx == -1){
            //asumo que cualquier simbolo posterior constituye un id
            return "id";
        }
        
        idx = actualIndex(idx, lexeme);
        if (idx == -1){
            return "id";
        }
        return table.get(idx).getToken();
    }
    
    /**
     * Search same length of the lexeme in table
     * @param lexeme the lexeme string
     * @return the value indicating that the length of the lexeme
     *         matches the length of the reserved words
     */
    private int binarySearch(String lexeme){
        
        int l = 0, r = table.size()-1;
        int mid = l + (r-l)/2;
        Token temp = table.get(mid); // error if table.size() == 0
        
        while (lexeme.length() != temp.getLexemeLength()){
            
            if (lexeme.length() < temp.getLexemeLength()){
                r = mid - 1;
            }
            else{
                l = mid + 1;
            }
            
            if (l<=r){
                mid = l + (r-l)/2;
                temp = table.get(mid);
            }
            else{
                break;
            }
        }
        
        if (lexeme.length() == temp.getLexemeLength()){
            return mid;
        }
        return -1;
    }
    
    /**
     * Search actual lexeme's index using regionMatches
     * @param idx The length of the reserved word (Also Index)
     * @return the true index where the searched token is found
     */
    private int actualIndex(int idx, String lexeme) {
        
        int l = idx, r = idx+1;
        
        while (l > -1 &&
               table.get(l).getLexemeLength() == lexeme.length() &&
               !lexeme.equals(table.get(l).getLexeme())){
            l--;
        }
        if (l < 0 || table.get(l).getLexemeLength() != lexeme.length()){
            while (r < table.size() &&
                   table.get(r).getLexemeLength() == lexeme.length() &&
                   !lexeme.equals(table.get(r).getLexeme())){
                r++;
            }
            if (r >= table.size() || table.get(r).getLexemeLength() != lexeme.length()){
                return -1;
            }
            return r;
        }
        return l;
    }
        
    /**
     * Loads table with tokens, lexemes and lengths of the lexemes.
     * The table should be sorted in increasing order
     * 
     * @return the table with Token(token, lexeme, length_lexeme) objects
     */
    private ArrayList loadTable(){
        ArrayList<Token> table = new ArrayList<Token>();
        String[] tokens = {"apos", "lbrace", "rbrace",
                           "lparent", "rparent", "semicolon", "colon",
                           "comma", "dot", "percent", "apos",
                           "assign", "lbracket", "rbracket",
                           "plus", "minus", "ast",
                           "slash", "excmark", "less",
                           "greater", "leq", "geq",
                           "or", "and", "noteq", 
                           "equal", "if", "null",
                           "var", "new", "int", 
                           "mainmet", "maincls", "init",
                           "self", "void", "func", 
                           "else", "char", "true",
                           "bool", "false", "array",
                           "while", "class", "static",
                           "string", "return", "private"};
       
        String[] lexemes = {"'", "{", "}",
                            "(", ")", ";", ":",
                            ",", ".", "%", "'",
                            "=", "[", "]",
                            "+", "-", "*",
                            "/", "!", "<",
                            ">", "<=", ">=",
                            "||", "&&", "!=",
                            "==", "if", "nil",
                            "var", "new", "Int", 
                            "main", "Main", "init",
                            "self", "void", "func", 
                            "else", "Char", "true",
                            "Bool", "false", "Array",
                            "while", "class", "static",
                            "String", "return", "private"};
                           
        for (int i=0; i<lexemes.length; i++){
            table.add(new Token(tokens[i], lexemes[i], lexemes[i].length()));
        }
        
        return table;        
    }
    
    private boolean isInLine(){
        return (getColumn() < line.length());
    }
    
    private boolean isInLine(int forward){
        return (getColumn() + forward < line.length());
    }
    
    private int getRow(){
        return row;
    }
    
    private int getColumn(){
        return column;
    }
    
    private int getColumnFile(){
        return column + 1;
    }
    
    private int addRow(int value){ //considerar lanzar error si es un valor 
                                    //negativo que sea menor a 1
        row += value;
        return row;
    }
    
    /**
     * Moves current column
     * @param value the number of columns to move
     * @return the new current column
     */
    private int addColumn(int value){
        column += value;
        return column;
    }
    
    /**
     * Sets column at the beginning of the line
     */
    private void resetColumn(){
        column = 0;
    }
}