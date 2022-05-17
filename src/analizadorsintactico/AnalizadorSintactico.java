package analizadorsintactico;

import analizadorlexico.AnalizadorLexico;
import analizadorlexico.Token;
import analizadorlexico.IllegalTokenException;

import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import analizadorsemantico.IncompleteSymbolTableException;
import analizadorsemantico.SemanticDeclarationException;

import java.util.Arrays;

/**
 * Syntactic Analyzer definition for tinySwift+
 * 
 * @author D. Emiliano F.
 * @see ejecutador.Ejecutador
 */
public class AnalizadorSintactico {
    
    private final AnalizadorLexico lexical;
    private Token currentToken;
    private Token nextToken;
    private boolean EOF = false;
    private boolean claseMain = false;
    private boolean metodoMain = false;
    
    // Semantic Analyzer
    private final SymbolTable symbolTable;
    
    public AnalizadorSintactico(AnalizadorLexico lexical)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        symbolTable = new SymbolTable();
        this.lexical = lexical;
        currentToken = lexical.nextToken();
        nextToken = lexical.nextToken();
        
        if (currentToken == null){
            setEOF(true);
        }
    }
    
    /**
     * Gets symbol table when reading from the file is complete
     * 
     * @return the symbol table
     * @throws IncompleteSymbolTableException if the file reading is incomplete
     */
    public SymbolTable getSymbolTable()
        throws IncompleteSymbolTableException {
        
        if (!getEOF()){
            throw new IncompleteSymbolTableException("Unexpected error: not end of file");
        }
        
        return symbolTable;
    }
    
    /**
     * Gets name of file
     * 
     * @return the name of file
     */
    public String getNameOfFile(){
        return lexical.getNameOfFile();
    }
    
    /**
     * Throws SyntacticErrorException
     * 
     * @param description
     * @throws SyntacticErrorException 
     */
    private void throwException(String description)
        throws SyntacticErrorException {
        
        throw new SyntacticErrorException(currentToken.getLine(),
                                          currentToken.getColumn(),
                                          description);
    }
    
    private void throwExceptionMatcher(String description)
        throws SyntacticErrorException {
        
        if (!claseMain){
            throw new SyntacticErrorException("End of file: class Main not found");
        }
        throw new SyntacticErrorException("End of file. Expected: " + description);
    }
    
    private void throwExceptionMatcher(String[] description)
        throws SyntacticErrorException {
        
        throw new SyntacticErrorException("End of file. Expected:" + 
                                          Arrays.toString(description));
    }
    
    /**
     * Match name with current token name
     * 
     * @param name the name to match
     * @throws IllegalTokenException lexical analyzer error
     * @throws SyntacticErrorException no match the expected token name 
     */
    private Token matcher(String name)
        throws IllegalTokenException,
               SyntacticErrorException{
        
        if (currentToken == null){
            throwExceptionMatcher(name);
        }
        
        if (currentToken.getToken().equals(name)){
            //System.out.println(currentToken.getToken() + "  " + name);
            Token returnToken = currentToken;
            updateToken();
            return returnToken;
        }
        
        throwException("Expected " + name +
                                    " but found " + currentToken.getToken());
        return null;
    }
    
    /**
     * Match token comparing the lexemes
     * 
     * @param name the name of the token
     * @param lexeme the lexeme contained in the token
     * @throws IllegalTokenException lexical analyzer error
     * @throws SyntacticErrorException no match the expected token name 
     */
    private Token matcherWithLexeme(String name, String lexeme)
        throws IllegalTokenException,
               SyntacticErrorException{
        
        if (currentToken == null){
            throwExceptionMatcher(name);
        }
        
        if (currentToken.getToken().equals(name) &&
            currentToken.getLexeme().equals(lexeme)){
                
            Token toReturn = currentToken;
            updateToken();
            return toReturn;
        }
        
        throwException("Expected " + lexeme +
                                    " but found " + currentToken.getLexeme());
        return null;
    }
    
    /**
     * Match some token in array
     * 
     * @param terminals the array of tokens
     * @throws IllegalTokenException lexical analyzer error
     * @throws SyntacticErrorException no match any token
     */
    private Token matcherSomeTerminal(String ... terminals)
        throws IllegalTokenException,
               SyntacticErrorException{
        
        if (currentToken == null){
            throwExceptionMatcher(terminals);
        }
        
        for (String terminal: terminals){
            
            if (currentToken.getToken().equals(terminal)){
                Token returnToken = currentToken;
                updateToken();
                return returnToken;
            }
        }
        
        throwException("Expected " + Arrays.toString(terminals));
        return null;
    }
    
    private void matcherNextToken(String name)
        throws IllegalTokenException,
               SyntacticErrorException {
        
    }
    
    /**
     * Update token
     * 
     * @throws IllegalTokenException lexical analyzer error
     */
    private void updateToken()
        throws IllegalTokenException {
        
        currentToken = nextToken;
        nextToken = lexical.nextToken();
        
        if (currentToken == null){
            setEOF(true);
        }
    }
    
    private void setEOF(boolean value){
        EOF = value;
    }
    
    private boolean getEOF(){
        return EOF;
    }
    
    /**
     * Determines if the current token is in set
     * 
     * @param set the token names in the set
     * @return true if the current token is in set
     */
    private boolean inSet(String ... set){
        
        if (currentToken == null){
            return false;
        }
        
        for (int i = 0;i<set.length;++i){
            if (set[i].equals(currentToken.getToken())){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines if the next token is in set
     * 
     * @param set the token names in the set
     * @return true if the next token is in set
     */
    private boolean inFutureSet(String ... set){
        
        if (nextToken == null){
            return false;
        }
        
        for (int i = 0;i<set.length;++i){
            if (set[i].equals(nextToken.getToken())){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines if the next token lexeme is in set
     * 
     * @param set the lexeme names in the set
     * @return true if the next token lexeme is in set 
     */
    private boolean inFutureLexemeSet(String ... set){
        
        if (nextToken == null){
            return false;
        }
        
        for (int i = 0;i<set.length;++i){
            if (set[i].equals(nextToken.getLexeme())){
                return true;
            }
        }
        return false;
    }
    
    public boolean program()
        throws SyntacticErrorException,
               IllegalTokenException,
               SemanticDeclarationException{
        
        if (inSet("class")){
            if (inFutureSet("idclass")){
                if (inFutureLexemeSet("Main")){
                    claseMain();
                    clase_();
                }
                else{
                    clase_();
                    claseMain();
                    clase_();
                }
            }
            else{
                throwException("In program: idclass token not found");
            }
        }
        else{
            throwException("In program: class token not found");
        }
        
        return getEOF();
    }
    
    private void clase_()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        if (inSet("class")){
            if (inFutureSet("idclass")){
                
                // En clase_ despues de claseMain, no admite otro lexema Main
                // Si el sintáctico debe aceptar igual, agregar un flag
                // cuando claseMain ya haya sido visitada
                if (inFutureLexemeSet("Main")){
                    return;
                }
                else{
                    clase();
                    clase_();
                }
            }
            else{
                throwException("idclass token not found");
            }
        }
        else{
            ;
        }
    }
    
    
    private void claseMain()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        try{
        
        matcher("class");
        Token id = matcher("idclass");
        Type type = new Type("Object");
        
        if (inSet("colon")){
            type = herencia();
        }
        
        symbolTable.newClass(id.getLexeme(), 
                             type,
                             id.getLine(),
                             id.getColumn());
        
        matcher("lbrace");
        miembro_(true);
        type = new Type(matcher("void"));
        id = matcherWithLexeme("id","main");
        
        symbolTable.newMethod(id.getLexeme(), 
                              type, 
                              true,
                              id.getLine(),
                              id.getColumn());
        
        matcher("lparent");
        matcher("rparent");
        bloqueMetodo(type);
        
        // Add main method
        symbolTable.addMethodEntry();
        
        metodoMain = true;
        miembro_(false);
        matcher("rbrace");
        claseMain = true;
        
        // Add Main class
        symbolTable.addClassEntry();
        
        }
        catch (SyntacticErrorException e){
            if (currentToken != null){
                throwException("In class Main: " + e.getMessage());
            }
            throwExceptionMatcher("EOF");
        }
    }
    
    /*
    private void claseMain()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("class");
        matcher("idclass");
        if (inSet("colon")){
            herencia();
        }
        matcher("lbrace");
        miembro_();
        matcher("void");
        matcherWithLexeme("id","main");
        matcher("lparent");
        matcher("rparent");
        bloqueMetodo();
        miembro_();
        matcher("rbrace");
    }*/
    
    private void clase()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        try{
            matcher("class");
            Token id = matcher("idclass");
            Type type = new Type("Object");
            if (inSet("colon")){
                type = herencia();
            }
            
            // New class
            symbolTable.newClass(id.getLexeme(), 
                                 type,
                                 id.getLine(),
                                 id.getColumn());
            
            matcher("lbrace");
            miembro_(false);
            matcher("rbrace");
            
            // Add class entry to table
            symbolTable.addClassEntry();
        }
        catch (SyntacticErrorException e){
            throwException("In class: " + e.getMessage());
        }
    }
    
    private Type herencia()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("colon")){
            matcher("colon");
            return tipo();
        }
        else{
            if (inSet("lbrace")){
                ;
            }
            else{
                throwException("Expected colon or lbrace"
                                + "but found " + currentToken.getToken());
            }
        }
        return null;
    }
    
    
    private void miembro_(boolean inClassMain)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        if (inSet("private","var","init","static","func")){
            miembro(inClassMain);
            miembro_(inClassMain);
        }
        else{
            if (inSet("rbrace")){
                ;
            }
            /*
            else{
                throwException("Method definition and rbrace not found");
            }*/
        }
    }
    
    /*
    private void miembro_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("private","var","init")){
            miembro();
            miembro_();
        }
        else{
            if (inSet("static")){
                matcher("static");
                matcher("func");
                if (inSet("void") && 
                    inFutureSet("id") &&
                    inFutureLexemeSet("main")){
                        return;
                }
                metodo();
                miembro_();
            }
            else{
                if (inSet("func")){
                    matcher("func");
                    metodo();
                    miembro_();
                }
                else{
                    if (inSet("rbrace")){
                        ;
                    }
                    else{
                        throwException("Members definition and rbrace not found");
                    }
                }
            }
        }
    }*/
    
    private void miembro(boolean inClassMain)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        if (inSet("private","var")){
            atributo();
        }
        else{
            if (inSet("init")){
                constructor();
            }
            else{
                if (inSet("func", "static")){
                    metodo(inClassMain);
                }
                else{
                    if (inSet("rbrace")){
                        ;
                    }
                }
            }
        }
    }
    
    /*
    private void miembro()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("private","var")){
            atributo();
        }
        else{
            if (inSet("init")){
                constructor();
            }
            else{
                if (inSet("rbrace")){
                    ;
                }
            }
        }
    }*/
    
    private void constructor()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        try{
            Token id = matcher("init");
            Type type = new Type(symbolTable.getNameCurrentClass());
            
            // New constructor
            symbolTable.newMethod(id.getLexeme(), 
                                  type, 
                                  false,
                                  id.getLine(),
                                  id.getColumn());
            
            argumentosFormales();
            bloqueMetodo(type);
            
            // Add constructor to table
            symbolTable.addMethodEntry();
        }
        catch (SyntacticErrorException e){
            throwException("constructor: " + e.getMessage());
        }
    }
    
    private void atributo()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        try{
            boolean isPrivate = visibilidad();
            matcher("var");
            Type type = tipo();
            listaDeclaracionVariables(isPrivate, type);
            matcher("semicolon");
        }
        catch (SyntacticErrorException e){
            throwException("attribute: " + e.getMessage());
        }
    }
    
    private void metodo(boolean inClassMain)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        try{
        if (inClassMain){
            if (inSet("static")){
                if (inFutureSet("func")){
                    matcher("static");
                    matcher("func");
                    if (inSet("void")){
                        if (inFutureLexemeSet("main")){
                            return;
                        }
                    }
                    Type type = tipoMetodo();
                    Token id = matcher("id");
                    
                    // New method entry
                    symbolTable.newMethod(id.getLexeme(), 
                                          type, 
                                          true,
                                          id.getLine(), 
                                          id.getColumn());
                    
                    argumentosFormales();
                    bloqueMetodo(type);
                    
                    // Add to symbol table
                    symbolTable.addMethodEntry();
                    return;
                }
            }
        }
        
        boolean isStatic = formaMetodo();
        matcher("func");
        Type type = tipoMetodo();
        Token id = matcher("id");
        
        // New method entry
        symbolTable.newMethod(id.getLexeme(), 
                              type, 
                              isStatic,
                              id.getLine(), 
                              id.getColumn());
        
        argumentosFormales();
        bloqueMetodo(type);
        
        // Add to symbol table
        symbolTable.addMethodEntry();
        
        }
        catch (SyntacticErrorException e){
            throwException("method: " + e.getMessage());
        }
    }
    
    /*
    private void metodo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        tipoMetodo();
        matcher("id");
        argumentosFormales();
        bloqueMetodo();        
    }*/
    
    /**
     * 
     * @return true if it's private
     * @throws IllegalTokenException
     * @throws SyntacticErrorException 
     */
    private boolean visibilidad()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("private")){
            matcher("private");
            return true;
        }
        
        return false;
        /*
        else{
            if (inSet("var")){
                ;
            }
        }*/
    }
    
    /**
     * 
     * @return true if is static
     * @throws IllegalTokenException
     * @throws SyntacticErrorException 
     */
    private boolean formaMetodo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("static")){
            matcher("static");
            return true;
        }
        
        return false;
        /*
        else{
            if (inSet("func")){
                ;
            }
        }*/
    }
    
    private void bloqueMetodo(Type type)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        matcher("lbrace");
        declVarLocales_();
        sentencia_(type);
        matcher("rbrace");
        
    }
    
    private void declVarLocales_()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        if (inSet("var")){
            declVarLocales();
            declVarLocales_();
        }
        
    }
    private void declVarLocales()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        matcher("var");
        Type type = tipo();
        listaDeclaracionVariables(type);
        matcher("semicolon");
        
    }
    
    /**
     * Available for local variables definition!
     * 
     * @param type the type of local variable
     * @throws IllegalTokenException
     * @throws SyntacticErrorException 
     */
    private void listaDeclaracionVariables(Type type)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        Token id = matcher("id");
        
        // Add atribute to class
        symbolTable.addLocal(id.getLexeme(), 
                             type,
                             "",
                             id.getLine(),
                             id.getColumn());
        
        listaDeclaracionVariablesF(type);
    }
    
    /**
     * Available only for local variables!
     * 
     * @param type the type of local variable
     * @throws IllegalTokenException
     * @throws SyntacticErrorException 
     */
    private void listaDeclaracionVariablesF(Type type)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        if (inSet("semicolon")){
            return;
        }
        matcher("comma");
        listaDeclaracionVariables(type);
    }
    
    /**
     * Available for attributes definition!
     * 
     * @param isPrivate the visibility of attribute
     * @param type the type of attribute
     * @throws IllegalTokenException
     * @throws SyntacticErrorException 
     */
    private void listaDeclaracionVariables(boolean isPrivate,
                                           Type type)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        Token id = matcher("id");
        
        // Add atribute to class
        symbolTable.addAttribute(id.getLexeme(), 
                                 type, 
                                 isPrivate,
                                 id.getLine(),
                                 id.getColumn());
        
        listaDeclaracionVariablesF(isPrivate, type);
    }
    
    /**
     * Available only for attributes!
     * 
     * @param isPrivate the visibility of attribute
     * @param type the type of attribute
     * @throws IllegalTokenException
     * @throws SyntacticErrorException 
     */
    private void listaDeclaracionVariablesF(boolean isPrivate,
                                            Type type)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        if (inSet("semicolon")){
            return;
        }
        matcher("comma");
        listaDeclaracionVariables(isPrivate, type);
    }
    
    private void argumentosFormales()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        matcher("lparent");
        argumentosFormalesF();
        
    }
    private void argumentosFormalesF()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        if (inSet("rparent")){
            matcher("rparent");
        }
        else{
            listaArgumentosFormales();
            matcher("rparent");
        }
    }
    
    private void listaArgumentosFormales()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        argumentoFormal();
        listaArgumentosFormalesF();
    }
    
    private void listaArgumentosFormalesF()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        if (inSet("rparent")){
            return;
        }
        matcher(",");
        listaArgumentosFormales();
    }
    
    private void argumentoFormal()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        Type type = tipo();
        Token id = matcher("id");
        
        // Add parameter
        symbolTable.addParameter(id.getLexeme(),
                                 type,
                                 id.getLine(),
                                 id.getColumn());
    }
    
    private Type tipoMetodo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("void")){
            return new Type(matcher("void"));
        }
        
        return tipo();        
    }
    
    private Type tipo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("array")){
            return tipoArray();
        }
        else{
            if (inSet("idclass")){
                return tipoReferencia();
            }
            else{
                if (inSet("bool","int","string","char")){
                    return tipoPrimitivo();
                }
                else{
                    throwException("Invalid type declaration");
                }
            }
        }
        return null;
    }
    
    private Type tipoPrimitivo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        return new Type(matcherSomeTerminal("bool","int","string","char"));
    }
    
    private Type tipoReferencia()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        return new Type(matcher("idclass"));
    }
    
    private Type tipoArray()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("array");
        Type type = tipoPrimitivo();
        
        // Is array
        type.setArray();
        return type;
    }
    
    private void sentencia_(Type type)
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("rbrace")){
            return;
        }
        sentencia(type);
        sentencia_(type);
    }
    private void sentencia(Type type)
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("semicolon")){
            matcher("semicolon");
        }
        else{
            if (inSet("id","self")){
                asignacion();
                matcher("semicolon");
            }
            else{
                if (inSet("lparent")){
                    sentenciaSimple();
                    matcher("semicolon");
                }
                else{
                    if (inSet("if")){
                        matcher("if");
                        matcher("lparent");
                        expresion();
                        matcher("rparent");
                        sentencia(type);
                        sentenciaF(type);
                    }
                    else{
                        if (inSet("while")){
                            matcher("while");
                            matcher("lparent");
                            expresion();
                            matcher("rparent");
                            sentencia(type);
                        }
                        else{
                            if (inSet("return")){
                                matcher("return");
                                returnNoTerminal(type);
                                matcher("semicolon");
                            }
                            else{
                                if (inSet("lbrace")){
                                    bloque(type);
                                }
                                else{
                                    throwException("Invalid sentence: " + 
                                                    currentToken.getToken() +
                                                    " found");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void sentenciaF(Type type)
        throws IllegalTokenException,
               SyntacticErrorException {
        /*
        if (inSet("semicolon","id","self", "lparent",
                    "if", "while", "lbrace", "return", "rbrace")){ 
            return;
        }

        matcher("else");
        sentencia(); */
        
        if (inSet("else")){
            matcher("else");
            sentencia(type);
        }
    }
    private void returnNoTerminal(Type type)
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("semicolon")){
            return;
        }
        expresion(); //podría poner un inSet(primeros(Return))
    }
    
    private void bloque(Type type)
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("lbrace");
        sentencia_(type);
        matcher("rbrace");
    }
    
    private void asignacion()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        try{
            if (inSet("id")){
                accesoVarSimple();
                matcher("assign");
                expresion();
            }
            else{
                if (inSet("self")){
                    accesoSelfSimple();
                    matcher("assign");
                    expresion();
                }
                else{
                    throwException("An assignment was expected but found " +
                                    currentToken.getToken());
                }
            }
        }
        catch (SyntacticErrorException e){
            
            // if it's necessary?
            if (currentToken != null){
                throwException("assignment: " + e.getMessage());
            }
            throwExceptionMatcher("EOF");
        }
    }
    
    private void accesoVarSimple()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("id");
        accesoVarSimpleF();
    }
    
    private void accesoVarSimpleF()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("lbracket")){
            matcher("lbracket");
            expresion();
            matcher("rbracket");
        }
        else{
            if (inSet("dot")){
                encadenadoSimple_();
            }
        }
    }
    
    private void accesoSelfSimple()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("self");
        encadenadoSimple_();
    }
    
    private void encadenadoSimple_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("assign")){
            return;
        }
        encadenadoSimple();
        encadenadoSimple_();
    }
    
    private void encadenadoSimple()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("dot");
        matcher("id");
    }
    
    private void sentenciaSimple()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("lparent");
        expresion();
        matcher("rparent");
    }
    
    private void expresion()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        expOr();
    }
    
    private void expOr()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        expAnd();
        expOr_();
    }
    
    /*
    private void expOr_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("semicolon","rparent","rbracket")){
            return;
        }
        else{
            if (inSet("or")){
                matcher("or");
                expAnd();
                expOr_();
            }
            else{
                throwException("decris");
            }
        }
    }*/
    
    private void expOr_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("or")){
            matcher("or");
            expAnd();
            expOr_();
        }
    }
    
    private void expAnd()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        expIgual();
        expAnd_();
    }
    
    /*
    private void expAnd_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("or","semicolon","rparent","rbracket")){
            return;
        }
        else{
            if (inSet("and")){
                matcher("and");
                expIgual();
                expAnd_();
            }
        }
    }*/
    
    private void expAnd_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("and")){
            matcher("and");
            expIgual();
            expAnd_();
        }
    }
    
    private void expIgual()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        expCompuesta();
        expIgual_();
    }
    
    /*
    private void expIgual_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("and","or","semicolon","rparent","rbracket")){
            return;
        }
        else{
            if (inSet("equal","noteq")){
                opIgual();
                expCompuesta();
                expIgual_();
            }
        }
    }*/
    
    private void expIgual_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("equal","noteq")){
            opIgual();
            expCompuesta();
            expIgual_();
        }
    }
    
    private void expCompuesta()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        expAd();
        expCompuestaF();
    }
    
    /*
    private void expCompuestaF()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("equal","noteq","and","or","semicolon","rparent","rbracket")){
            return;
        }
        else{
            if(inSet("less","greater","leq","geq")){
                opCompuesto();
                expAd();
            }
        }
    }*/
    
    private void expCompuestaF()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if(inSet("less","greater","leq","geq")){
            opCompuesto();
            expAd();
        }
    }
    
    private void expAd()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        expMul();
        expAd_();
    }
    
    /*
    private void expAd_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("less","greater","leq","geq","equal",
                    "noteq","and","or","semicolon","rparent","rbracket")){
            return;
        }
        else{
            if (inSet("plus","minus")){
                opAd();
                expMul();
                expAd_();
            }
        }
    }*/
    
    private void expAd_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("plus","minus")){
            opAd();
            expMul();
            expAd_();
        }
    }
    
    private void expMul()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        expUn();
        expMul_();
    }
    
    /*
    private void expMul_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("less","greater","leq","geq","equal","noteq",
                    "and","or","semicolon","rparent","rbracket","plus","minus")){
            return;
        }
        else{
            if (inSet("ast","div","mod")){
                opMul();
                expUn();
                expMul_();
            }
        }
    }*/
    
    private void expMul_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("ast","div","mod")){
            opMul();
            expUn();
            expMul_();
        }
    }
    
    private void expUn()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("plus","minus","not")){
            opUnario();
            expUn();
        }
        else{
            if (inSet("nil","true","false","intlit","stringlit","charlit",
                        "lparent","self","id","idclass","new")){
                operando();
            }
            else{
                throwException("Unindentified expresion");
            }
        }
    }
    
    private void opIgual()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcherSomeTerminal("equal","noteq");
    }
    
    private void opCompuesto()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcherSomeTerminal("less","greater","leq","geq");
    }
    
    private void opAd()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcherSomeTerminal("plus","minus");
    }
    
    private void opUnario()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcherSomeTerminal("plus","minus","not");
    }
    
    private void opMul()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcherSomeTerminal("ast","div","mod");
    }
    
    private void operando()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("nil","true","false","intlit","stringlit","charlit")){
            literal();
        }
        else{
            if (inSet("lparent","self","id","idclass","new")){
                primario();
                encadenado();
            }
            else{
                throwException("Unindentified operator");
            }
        }
    }
    
    private void literal()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcherSomeTerminal("nil","true","false","intlit","stringlit","charlit");
    }
    
    private void primario()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("lparent")){
            expresionParentizada();
        }
        else{
            if (inSet("self")){
                accesoSelf();
            }
            else{
                if (inSet("new")){
                    llamadaConstructor();
                }
                else{
                    if (inSet("id")){
                        matcher("id");
                        primarioId();
                    }
                    else{
                        if (inSet("idclass")){
                            llamadaMetodoEstatico();
                        }
                    }
                }
            }
        }
    }
    
    private void expresionParentizada()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("lparent");
        expresion();
        matcher("rparent");
        encadenado();
    }
    
    private void accesoSelf()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("self");
        encadenado();
    }
    
    private void primarioId()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("lparent")){
            llamadaMetodo();
        }
        else{
            if (inSet("dot", "lbracket")){
                accesoVar();
            }
            //else{ //colocar secundarios para reportar correctamente error
        }
    }
    
    private void accesoVar()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("dot")){
            encadenado();
        }
        else{
            if (inSet("lbracket")){
                matcher("lbracket");
                expresion();
                matcher("rbracket");
            }
        }
    }
    
    private void llamadaMetodo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        argumentosActuales();
        encadenado();
    }
    
    private void llamadaMetodoEstatico()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("idclass");
        matcher("dot");
        matcher("id");
        llamadaMetodo();
        encadenado();
    }
    
    private void llamadaConstructor()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("new");
        llamadaConstructorF();
    }
    
    private void llamadaConstructorF()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("idclass")){
            matcher("idclass");
            argumentosActuales();
            encadenado();
        }
        else{
            if (inSet("bool","int","string","char")){
                tipoPrimitivo();
                matcher("lbracket");
                expresion();
                matcher("rbracket");
            }
            else{
                throwException("unadescripcion");
            }
        }
    }
    
    private void argumentosActuales()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("lparent");
        argumentosActualesF();
    }
    
    private void argumentosActualesF()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("rparent")){
            matcher("rparent");
        }
        else{
            if (inSet("plus","minus","not","nil","true","false","intlit",
                        "stringlit","charlit","lparent","self","id","new")){
                listaExpresiones();
                matcher("rparent");
            }
            else{
                throwException("unaeDesp");
            }
        }
    }
    
    private void listaExpresiones()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        expresion();
        listaExpresionesF();
    }
    
    private void listaExpresionesF()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("comma")){
            matcher("comma");
            listaExpresiones();
        }
        else{
            if (inSet("rparent")){
                return;
            }
            else{
                throwException("unerror");
            }
        }
    }
    
    private void encadenado()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("dot")){
            matcher("dot");
            matcher("id");
            encadenadoF();
        }
    }
    
    private void encadenadoF() //nexttoken?
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("lparent")){
            llamadaMetodoEncadenado();
        }
        else{
            if (inSet("dot","lbracket")){
                accesoVariableEncadenado();
            }
        }
    }
    
    private void llamadaMetodoEncadenado()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        argumentosActuales();
        encadenado();
    }
    
    private void accesoVariableEncadenado()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("dot")){
            encadenado();
        }
        else{
            if (inSet("lbracket")){
                matcher("lbracket");
                expresion();
                matcher("rbracket");
            }
            else{
                throwException("Imposible acceder a variable");
            }
        }
    }
}
