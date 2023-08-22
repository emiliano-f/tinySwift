package analizadorsintactico;

import analizadorlexico.AnalizadorLexico;
import analizadorlexico.Token;
import analizadorlexico.IllegalTokenException;

import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import analizadorsemantico.IncompleteSymbolTableException;
import analizadorsemantico.SemanticDeclarationException;
import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.AbstractSyntaxTree;
import analizadorsemantico.abstractsyntaxtree.expressions.ArrayExpressionNode;
import analizadorsemantico.abstractsyntaxtree.expressions.BinaryExpressionNode;
import analizadorsemantico.abstractsyntaxtree.expressions.CallExpressionNode;
import analizadorsemantico.abstractsyntaxtree.expressions.ChainingExpressionNode;
import analizadorsemantico.abstractsyntaxtree.expressions.ExpressionNode;
import analizadorsemantico.abstractsyntaxtree.expressions.IdExpressionNode;
import analizadorsemantico.abstractsyntaxtree.expressions.LiteralNode;
import analizadorsemantico.abstractsyntaxtree.expressions.NewExpressionNode;
import analizadorsemantico.abstractsyntaxtree.expressions.OperatorNode;
import analizadorsemantico.abstractsyntaxtree.expressions.SelfExpressionNode;
import analizadorsemantico.abstractsyntaxtree.expressions.StaticCallExpressionNode;
import analizadorsemantico.abstractsyntaxtree.expressions.UnaryExpressionNode;
import analizadorsemantico.abstractsyntaxtree.sentences.ArrayNode;
import analizadorsemantico.abstractsyntaxtree.sentences.AssignmentNode;
import analizadorsemantico.abstractsyntaxtree.sentences.ChainingNode;
import analizadorsemantico.abstractsyntaxtree.sentences.IfNode;
import analizadorsemantico.abstractsyntaxtree.sentences.ReturnNode;
import analizadorsemantico.abstractsyntaxtree.sentences.SelfNode;
import analizadorsemantico.abstractsyntaxtree.sentences.SentencesNode;
import analizadorsemantico.abstractsyntaxtree.sentences.VarNode;
import analizadorsemantico.abstractsyntaxtree.sentences.WhileNode;
import java.util.ArrayList;

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
    private final AbstractSyntaxTree ast;
    
    public AnalizadorSintactico(AnalizadorLexico lexical)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException {
        
        symbolTable = new SymbolTable();
        ast = new AbstractSyntaxTree();
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
     * Gets AST when reading from the file is complete
     * 
     * @return the AST
     */
    public AbstractSyntaxTree getAST()
        throws IncompleteSymbolTableException {
        
        if (!getEOF()){
            throw new IncompleteSymbolTableException("Unexpected error: not end of file");
        }
        
        return ast;
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
               SemanticDeclarationException,
               SemanticSentenceException {
        
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
               SemanticDeclarationException,
               SemanticSentenceException {
        
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
               SemanticDeclarationException,
               SemanticSentenceException {
        
        try{
        
        matcher("class");
        Token id = matcher("idclass");
        Type type = new Type("Object");
        
        if (inSet("colon")){
            type = herencia();
        }
        
        // New class in symbol table
        symbolTable.newClass(id.getLexeme(), 
                             type,
                             id.getLine(),
                             id.getColumn());
        // New class in AST
        ast.newClass(id.getLexeme());
        
        matcher("lbrace");
        miembro_(true);
        type = new Type(matcher("void"));
        id = matcherWithLexeme("id","main");
        
        // New method in symbol table
        symbolTable.newMethod(id.getLexeme(), 
                              type, 
                              true,
                              id.getLine(),
                              id.getColumn());
        // New method in AST
        ast.newMethod("main");
        
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
    
    private void clase()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException,
               SemanticSentenceException {
        
        try{
            matcher("class");
            Token id = matcher("idclass");
            Type type = new Type("Object");
            if (inSet("colon")){
                type = herencia();
            }
            
            // New class in symbol table
            symbolTable.newClass(id.getLexeme(), 
                                 type,
                                 id.getLine(),
                                 id.getColumn());
            // New class in AST
            ast.newClass(id.getLexeme());
            
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
        else if (inSet("lbrace")){
            ;
        }
        else{
            throwException("Expected colon or lbrace"
                                + "but found " + currentToken.getToken());
        }
        return null;
    }
    
    
    private void miembro_(boolean inClassMain)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException,
               SemanticSentenceException {
        
        if (inSet("private","var","init","static","func")){
            miembro(inClassMain);
            miembro_(inClassMain);
        }
        else if (inSet("rbrace")){
            ;
        }
    }
    
    private void miembro(boolean inClassMain)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException,
               SemanticSentenceException {
        
        if (inSet("private","var")){
            atributo();
        }
        else if (inSet("init")){
            constructor();
        }
        else if (inSet("func", "static")){
            metodo(inClassMain);
        }
        else if (inSet("rbrace")){
            ;
        }
    }
    
    private void constructor()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException,
               SemanticSentenceException {
        
        try{
            Token id = matcher("init");
            Type type = new Type(symbolTable.getNameCurrentClass());
            
            // New constructor in symbol table
            symbolTable.newMethod(id.getLexeme(), 
                                  type, 
                                  false,
                                  id.getLine(),
                                  id.getColumn());
            // New constructor in AST
            ast.newMethod(id.getLexeme());
            
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
               SemanticDeclarationException,
               SemanticSentenceException {
        
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
                    
                    // New method entry in symbol table
                    symbolTable.newMethod(id.getLexeme(), 
                                          type, 
                                          true,
                                          id.getLine(), 
                                          id.getColumn());
                    // New method in AST
                    ast.newMethod(id.getLexeme());
                    
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
        
        // New method entry in symbol table
        symbolTable.newMethod(id.getLexeme(), 
                              type, 
                              isStatic,
                              id.getLine(), 
                              id.getColumn());
        // New method in AST
        ast.newMethod(id.getLexeme());
                    
        argumentosFormales();
        bloqueMetodo(type);
        
        // Add to symbol table
        symbolTable.addMethodEntry();
        
        }
        catch (SyntacticErrorException e){
            throwException("method: " + e.getMessage());
        }
    }
    
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
    }
    
    private void bloqueMetodo(Type type)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticDeclarationException,
               SemanticSentenceException {
        
        matcher("lbrace");
        declVarLocales_();
        sentencia_(ast.getSentencesList());
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
        matcher("comma");
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
        else if (inSet("idclass")){
            return tipoReferencia();
        }
        else if (inSet("bool","int","string","char")){
            return tipoPrimitivo();
        }
        else{
            throwException("Invalid type declaration");
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
    
    private void sentencia_(SentencesNode currentScope)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        if (inSet("rbrace")){
            return;
        }
        sentencia(currentScope);
        sentencia_(currentScope);
    }
    private void sentencia(SentencesNode currentScope)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        if (inSet("semicolon")){
            matcher("semicolon");
        }
        
        else if (inSet("id","self")){
            
            // Add AssignmentNode to current list of sentences
            currentScope.addSentence(asignacion());
            matcher("semicolon");
        }
        
        else if (inSet("lparent")){
            currentScope.addSentence(sentenciaSimple());
            matcher("semicolon");
        }
        
        else if (inSet("if")){
            
            matcher("if");
            matcher("lparent");
            ExpressionNode exp = expresion();
            matcher("rparent");
            
            // List of sentences on if
            SentencesNode ifSentences = new SentencesNode();
            sentencia(ifSentences);
            
            // List of sentences on else
            SentencesNode elseSentences = new SentencesNode();
            sentenciaF(elseSentences);
            
            // Add IfNode to current list of sentences
            currentScope.addSentence(new IfNode(exp,
                                                ifSentences, 
                                                elseSentences));
        }
        
        else if (inSet("while")){
            
            matcher("while");
            matcher("lparent");
            ExpressionNode exp = expresion();
            matcher("rparent");
            
            // List of sentences
            SentencesNode whileSentences = new SentencesNode();
            sentencia(whileSentences);
            
            // Add WhileNode to current list of sentences
            currentScope.addSentence(new WhileNode(exp, 
                                                   whileSentences));
        }
        
        else if (inSet("return")){
            matcher("return");
            currentScope.addSentence(returnNoTerminal());
            matcher("semicolon");
        }
        
        else if (inSet("lbrace")){
            bloque(currentScope);
        }
        
        else {
            throwException("Invalid sentence: " + 
                           currentToken.getToken() +
                           " found");
        }
    }
    
    private void sentenciaF(SentencesNode currentScope)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {

        if (inSet("else")){
            matcher("else");
            sentencia(currentScope);
        }
    }
    private ReturnNode returnNoTerminal()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        if (inSet("semicolon")){
            return null;
        }
        
        ExpressionNode exp = expresion();
        return new ReturnNode(exp, exp.getLine());
    }
    
    private void bloque(SentencesNode currentScope)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        matcher("lbrace");
        sentencia_(currentScope);
        matcher("rbrace");
    }
    
    private AssignmentNode asignacion()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        AssignmentNode node = null;
        
        try{
            if (inSet("id")){
                ChainingNode chain = accesoVarSimple();
                matcher("assign");
                ExpressionNode expNode = expresion();
                node = new AssignmentNode(chain, expNode);
            }
            
            else if (inSet("self")){
                ChainingNode chain = accesoSelfSimple();
                matcher("assign");
                ExpressionNode expNode = expresion();
                node = new AssignmentNode(chain, expNode);
            }
            
            else{
                throwException("An assignment was expected but found " +
                               currentToken.getToken());
            }
        }
        catch (SyntacticErrorException e){
            
            // if it's necessary?
            if (currentToken != null){
                throwException("assignment: " + e.getMessage());
            }
            throwExceptionMatcher("EOF");
        }
        
        return node;
    }
    
    private ChainingNode accesoVarSimple()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        Token token = matcher("id");
        // Send lexeme and row of id (accesoVarSimpleF creates the Node)
        return accesoVarSimpleF(token.getLexeme(), token.getLine());
    }
    
    private ChainingNode accesoVarSimpleF(String id, int line)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ChainingNode chain = null;
        
        if (inSet("lbracket")){
            matcher("lbracket");
            ExpressionNode expNode = expresion();
            matcher("rbracket");
            
            // New node for AST
            chain = new ChainingNode(new ArrayNode(id,
                                                   expNode,
                                                   line));
        }
        
        else if (inSet("dot", "assign")){
                ChainingNode post = encadenadoSimple_();
                chain = new ChainingNode(new VarNode(id,
                                                     line),
                                         post);
        }
        
        return chain;
    }
    
    private ChainingNode accesoSelfSimple()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        Token token = matcher("self");
        ChainingNode chain = encadenadoSimple_();
        
        // Saves the name of current class for self reference
        return new ChainingNode(new SelfNode(symbolTable.getNameCurrentClass(),
                                             new Type(symbolTable.getNameCurrentClass()),
                                             token.getLine()),
                                chain);
    }
    
    private ChainingNode encadenadoSimple_()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        if (inSet("assign")){
            return null;
        }
        
        Token token = encadenadoSimple();
        ChainingNode chain = encadenadoSimple_();
        
        return new ChainingNode(new VarNode(token.getLexeme(),
                                            token.getLine()),
                                chain);
    }
    
    private Token encadenadoSimple()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("dot");
        return matcher("id");
    }
    
    private ExpressionNode sentenciaSimple()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        matcher("lparent");
        ExpressionNode exp = expresion();
        matcher("rparent");
        return exp;
    }
    
    private ExpressionNode expresion()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        return expOr();
    }
    
    private ExpressionNode expOr()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = expAnd();
        ExpressionNode right = expOr_();
        
        if (right != null){
            exp = new BinaryExpressionNode(exp, 
                                           right, 
                                           new OperatorNode("||", exp.getLine()));
        }
        
        return exp;
    }
    
    private ExpressionNode expOr_()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = null;
        
        if (inSet("or")){
            matcher("or");
            exp = expAnd();
            ExpressionNode right = expOr_();
            
            if (right != null){
                exp = new BinaryExpressionNode(exp, 
                                               right, 
                                               new OperatorNode("||",exp.getLine()));
            }
        }
        
        return exp;
    }
    
    private ExpressionNode expAnd()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = expIgual();
        ExpressionNode right = expAnd_();
        
        if (right != null){
            exp = new BinaryExpressionNode(exp,
                                           right,
                                           new OperatorNode("&&", exp.getLine()));
        }
        
        return exp;
    }
    
    private ExpressionNode expAnd_()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = null;
        
        if (inSet("and")){
            matcher("and");
            exp = expIgual();
            ExpressionNode right = expAnd_();
            
            if (right != null){
                exp = new BinaryExpressionNode(exp,
                                               right, 
                                               new OperatorNode("&&",exp.getLine()));
            }
        }
        
        return exp;
    }
    
    private ExpressionNode expIgual()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = expCompuesta();
        BinaryExpressionNode node = expIgual_();
        
        if (node != null){
            node.setLeftOp(exp);
            exp = node;
        }
        
        return exp;
    }
    
    private BinaryExpressionNode expIgual_()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        BinaryExpressionNode exp = null;
        
        if (inSet("equal","noteq")){
            OperatorNode op = opIgual();
            ExpressionNode temp = expCompuesta();
            BinaryExpressionNode node = expIgual_();
            
            exp = new BinaryExpressionNode(op);

            if (node != null){
                node.setLeftOp(temp);
                exp.setRightOp(node);
            }
            else {
                exp.setRightOp(temp);
            }
        }
        
        return exp;
    }
    
    private ExpressionNode expCompuesta()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = expAd();
        BinaryExpressionNode node = expCompuestaF();
        
        if (node != null){
            node.setLeftOp(exp);
            exp = node;
        }
        
        return exp;
    }
    
    private BinaryExpressionNode expCompuestaF()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        BinaryExpressionNode exp = null;
        
        if(inSet("less","greater","leq","geq")){
            OperatorNode op = opCompuesto();
            ExpressionNode temp = expAd();
            exp = new BinaryExpressionNode(op);
            exp.setRightOp(temp);
        }
        
        return exp;
    }
    
    private ExpressionNode expAd()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = null;
        
        exp = expMul();
        BinaryExpressionNode node = expAd_();
        
        if (node != null){
            node.setLeftOp(exp);
            exp = node;
        }
        
        return exp;
    }
    
    private BinaryExpressionNode expAd_()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        BinaryExpressionNode exp = null;
        
        if (inSet("plus","minus")){
            OperatorNode op = opAd();
            ExpressionNode temp = expMul();
            BinaryExpressionNode node = expAd_();
            
            exp = new BinaryExpressionNode(op);

            if (node != null){
                node.setLeftOp(temp);
                exp.setRightOp(node);
            }
            else {
                exp.setRightOp(temp);
            }
        }
        
        return exp;
    }
    
    private ExpressionNode expMul()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = null;
        
        exp = expUn();
        BinaryExpressionNode node = expMul_();
        
        if (node != null){
            node.setLeftOp(exp);
            exp = node;
        }
        
        return exp;
    }
    
    private BinaryExpressionNode expMul_()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        BinaryExpressionNode exp = null;
        
        if (inSet("ast","div","mod")){
            OperatorNode op = opMul();
            ExpressionNode temp = expUn();
            BinaryExpressionNode node = expMul_();
            
            exp = new BinaryExpressionNode(op);

            if (node != null){
                node.setLeftOp(temp);
                exp.setRightOp(node);
            }
            else {
                exp.setRightOp(temp);
            }
        }
        
        return exp;
    }
    
    private ExpressionNode expUn()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = null;
        
        if (inSet("plus","minus","not")){
            OperatorNode op = opUnario();
            ExpressionNode operand = expUn();
            exp = new UnaryExpressionNode(op, operand);
        }
        else{
            if (inSet("nil","true","false","intlit","stringlit","charlit",
                        "lparent","self","id","idclass","new")){
                exp = operando();
            }
            else{
                throwException("Unindentified expresion");
            }
        }
        
        return exp;
    }
    
    private OperatorNode opIgual()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        Token token = matcherSomeTerminal("equal","noteq");
        return new OperatorNode(token.getLexeme(), token.getLine());
    }
    
    private OperatorNode opCompuesto()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        Token token = matcherSomeTerminal("less","greater","leq","geq");
        return new OperatorNode(token.getLexeme(), token.getLine());
    }
    
    private OperatorNode opAd()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        Token token = matcherSomeTerminal("plus","minus");
        return new OperatorNode(token.getLexeme(), token.getLine());
    }
    
    private OperatorNode opUnario()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        Token token = matcherSomeTerminal("plus","minus","not");
        return new OperatorNode(token.getLexeme(), token.getLine());
    }
    
    private OperatorNode opMul()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        Token token = matcherSomeTerminal("ast","div","mod");
        return new OperatorNode(token.getLexeme(), token.getLine());
    }
    
    private ExpressionNode operando()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ExpressionNode exp = null;
        
        if (inSet("nil","true","false","intlit","stringlit","charlit")){
            exp = literal();
        }
        
        else if (inSet("lparent","self","id","idclass","new")){
            
            /*
             * Grammar modification (see report etapa4)
             * 
            ExpressionNode primary = primario();
            ChainingExpressionNode chain = encadenado();
            exp = new ChainingExpressionNode(primary, chain); */
            
            exp = primario();
        }
        
        else{
            throwException("Unindentified operator");
        }
        
        return exp;
    }
    
    private LiteralNode literal()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        Token token = matcherSomeTerminal("nil","true","false","intlit",
                                                        "stringlit","charlit");
        return new LiteralNode(token.getLexeme(),
                               Type.obtainLiteralType(token.getToken()),
                               token.getLine());
    }
    
    private ChainingExpressionNode primario()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ChainingExpressionNode exp = null;
        
        if (inSet("lparent")){
            exp = expresionParentizada();
        }
        
        else if (inSet("self")){
            exp = accesoSelf();
        }
        
        else if (inSet("new")){
            exp = llamadaConstructor();
        }
        
        else if (inSet("id")){
            Token token = matcher("id");
            exp = primarioId(token.getLexeme(), token.getLine());
        }
        else if (inSet("idclass")){
            exp = llamadaMetodoEstatico();
        }
        
        return exp;
    }
    
    private ChainingExpressionNode expresionParentizada()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        matcher("lparent");
        ExpressionNode expression = expresion();
        matcher("rparent");
        ChainingExpressionNode chain = encadenado();
        
        return new ChainingExpressionNode(expression,
                                          chain);
    }
    
    private ChainingExpressionNode accesoSelf()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        int line = matcher("self").getLine();
        ChainingExpressionNode chain = encadenado();
        
        return new ChainingExpressionNode(
                    new SelfExpressionNode(symbolTable.getNameCurrentClass(),
                                           line),
                    chain);
    }
    
    private ChainingExpressionNode primarioId(String id, int line)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ChainingExpressionNode chain = null;
        
        if (inSet("lparent")){
            chain = llamadaMetodo(id, line);
        }
        else if (inSet("dot", "lbracket")){
            chain = accesoVar(id, line);
        }
        else{
            chain = new ChainingExpressionNode(new IdExpressionNode(id, line));
        }

        return chain;
    }
    
    private ChainingExpressionNode accesoVar(String id, int line)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ChainingExpressionNode chain = null;
        
        if (inSet("dot")){
            ChainingExpressionNode postChain = encadenado();
            chain = new ChainingExpressionNode(
                        new IdExpressionNode(id, line),
                        postChain);
        }
        
        else if (inSet("lbracket")){
            matcher("lbracket");
            ExpressionNode exp = expresion();
            matcher("rbracket");
            chain = new ChainingExpressionNode(
                        new ArrayExpressionNode(id, exp, line));
        }
        
        return chain;
    }
    
    private ChainingExpressionNode llamadaMetodo(String id, int line)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException{
        
        ArrayList<ExpressionNode> args = argumentosActuales();
        ChainingExpressionNode chain = encadenado();
        
        return new ChainingExpressionNode(
                    new CallExpressionNode(id, args, 
                                           symbolTable.getNameCurrentClass(),line),
                    chain); 
    }
    
    private ChainingExpressionNode llamadaMetodoEstatico()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        Token idClass = matcher("idclass");
        matcher("dot");
        Token id = matcher("id");
        ChainingExpressionNode chain = llamadaMetodo(id.getLexeme(), id.getLine());
        encadenado(); // Ignore
        
        return new ChainingExpressionNode(
                    new StaticCallExpressionNode(idClass.getLexeme(), 
                                                 idClass.getLine()),
                    chain);
    }
    
    private ChainingExpressionNode llamadaConstructor()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        matcher("new");
        return llamadaConstructorF();
    }
    
    private ChainingExpressionNode llamadaConstructorF()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ChainingExpressionNode newConstructor = null;
        
        if (inSet("idclass")){
            Token idClass = matcher("idclass");
            ArrayList<ExpressionNode> args = argumentosActuales();
            ChainingExpressionNode chain = encadenado();
            newConstructor = new ChainingExpressionNode(
                                new NewExpressionNode(idClass.getLexeme(),
                                                      args,
                                                      idClass.getLine()),
                                chain);
        }
        else if (inSet("bool","int","string","char")){
            Type type = tipoPrimitivo();
            matcher("lbracket");
            ExpressionNode exp = expresion();
            matcher("rbracket");
            newConstructor = new ChainingExpressionNode(
                                new NewExpressionNode(type.toString(),
                                                      exp,
                                                      exp.getLine()));
        }
        else{
            throwException("Error");
        }
        
        return newConstructor;
    }
    
    private ArrayList<ExpressionNode> argumentosActuales()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        matcher("lparent");
        return argumentosActualesF();
    }
    
    private ArrayList<ExpressionNode> argumentosActualesF()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ArrayList<ExpressionNode> args = new ArrayList();
        
        if (inSet("rparent")){
            matcher("rparent");
        }
        else if (inSet("plus","minus","not","nil","true","false","intlit",
                        "stringlit","charlit","lparent","self","id","new")){
            listaExpresiones(args);
            matcher("rparent");
        }
        else{
            throwException("Error");
        }
        
        return args;
    }
    
    private void listaExpresiones(ArrayList<ExpressionNode> args)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        args.add(expresion());
        listaExpresionesF(args);
    }
    
    private void listaExpresionesF(ArrayList<ExpressionNode> args)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        if (inSet("comma")){
            matcher("comma");
            listaExpresiones(args);
        }
        else if (inSet("rparent")){
            return;
        }
        else{
            throwException("unerror");
        }
    }
    
    private ChainingExpressionNode encadenado()
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ChainingExpressionNode chain = null;
        
        if (inSet("dot")){
            matcher("dot");
            Token token = matcher("id");
            chain = encadenadoF(token.getLexeme(), token.getLine());
        }
        
        return chain;
    }
    
    private ChainingExpressionNode encadenadoF(String id, int line) //nexttoken?
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ChainingExpressionNode chain = null;
        
        if (inSet("lparent")){
            chain = llamadaMetodoEncadenado(id, line);
        }
        else if (inSet("dot","lbracket")){
            chain = accesoVariableEncadenado(id, line);
        }
        else{
            chain = new ChainingExpressionNode(new IdExpressionNode(id, line));
        }
        
        return chain;
    }
    
    private ChainingExpressionNode llamadaMetodoEncadenado(String id, int line)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ArrayList<ExpressionNode> args = argumentosActuales();
        ChainingExpressionNode chain = encadenado();
        
        return new ChainingExpressionNode(
                    new CallExpressionNode(id, args, 
                                           symbolTable.getNameCurrentClass(), line),
                    chain);
    }
    
    private ChainingExpressionNode accesoVariableEncadenado(String id, int line)
        throws IllegalTokenException,
               SyntacticErrorException,
               SemanticSentenceException {
        
        ChainingExpressionNode chain = null;
        
        if (inSet("dot")){
            ChainingExpressionNode postChain = encadenado();
            chain = new ChainingExpressionNode(
                        new IdExpressionNode(id, line),
                        postChain);
        }
        else if (inSet("lbracket")){
            matcher("lbracket");
            ExpressionNode exp = expresion();
            matcher("rbracket");
            chain = new ChainingExpressionNode(
                        new ArrayExpressionNode(id, exp, line));
        }
        else{
            throwException("Imposible acceder a variable");
        }
        
        return chain;
    }
}
