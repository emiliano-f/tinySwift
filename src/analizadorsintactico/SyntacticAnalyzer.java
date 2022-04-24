package analizadorsintactico;

import analizadorlexico.AnalizadorLexico;
import analizadorlexico.Token;
import analizadorlexico.IllegalTokenException;
import java.util.Arrays;

public class SyntacticAnalyzer {
    
    private AnalizadorLexico lexical;
    private Token currentToken;
    private Token nextToken;
    private boolean EOF = false;
    
    public SyntacticAnalyzer(AnalizadorLexico lexical)
        throws IllegalTokenException,
               SyntacticErrorException {
        
        this.lexical = lexical;
        currentToken = lexical.nextToken();
        nextToken = lexical.nextToken();
        
        if (currentToken == null){
            setEOF(true);
        }
    }
    
    private void throwException(String description)
        throws SyntacticErrorException {
        
        throw new SyntacticErrorException(currentToken.getLine(),
                                          currentToken.getColumn(),
                                          description);
    }
    
    /**
     * Match name with current token name
     * 
     * @param name the name to match
     * @throws IllegalTokenException lexical analyzer error
     * @throws SyntacticErrorException no match the expected token name 
     */
    private void matcher(String name)
        throws IllegalTokenException,
               SyntacticErrorException{
        
        if (currentToken.getToken().equals(name)){
            System.out.println(currentToken.getToken() + "  " + name);
            updateToken();
            return;
        }
        
        throwException("Error parsing: expected " + name +
                                    " but found " + currentToken.getToken());
    }
    
    /**
     * Match token comparing the lexemes
     * 
     * @param name the name of the token
     * @param lexeme the lexeme contained in the token
     * @throws IllegalTokenException lexical analyzer error
     * @throws SyntacticErrorException no match the expected token name 
     */
    private void matcherWithLexeme(String name, String lexeme)
        throws IllegalTokenException,
               SyntacticErrorException{
        
        if (currentToken.getToken().equals(name) &&
            currentToken.getLexeme().equals(lexeme)){
                
            updateToken();
            return;
        }
        
        throwException("Error parsing: expected " + lexeme +
                                    " but found " + currentToken.getLexeme());
    }
    
    /**
     * Match some token in array
     * 
     * @param terminals the array of tokens
     * @throws IllegalTokenException lexical analyzer error
     * @throws SyntacticErrorException no match any token
     */
    private void matcherSomeTerminal(String ... terminals)
        throws IllegalTokenException,
               SyntacticErrorException{
        
        for (int i=0; i<terminals.length;++i){
            
            if (currentToken.getToken().equals(terminals[i])){
                updateToken();
                return;
            }
        }
        
        throwException("Error parsing: expected " + Arrays.toString(terminals));
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
        for (int i = 0;i<set.length;++i){
            if (set[i].equals(nextToken.getLexeme())){
                return true;
            }
        }
        return false;
    }
    
    public boolean program()
        throws SyntacticErrorException,
               IllegalTokenException {
        
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
                throwException("idclass token not found");
            }
        }
        else{
            throwException("class token not found");
        }
        
        return getEOF();
    }
    
    /**
     * 
     * 
     * @throws IllegalTokenException
     * @throws SyntacticErrorException 
     */
    private void clase_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
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
        miembro_(); // controlar que no pise static func void main
        matcher("static");
        matcher("func");
        matcher("void");
        matcherWithLexeme("id","main");
        matcher("lparent");
        matcher("rparent");
        bloqueMetodo();
        miembro_();
        matcher("rbrace");
    }*/
    
    private void claseMain()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("class");
        matcher("idclass");
        if (inSet("colon")){
            herencia();
        }
        matcher("lbrace");
        miembro_(); // controlar que no pise static func void main
        matcher("void");
        matcherWithLexeme("id","main");
        matcher("lparent");
        matcher("rparent");
        bloqueMetodo();
        miembro_();
        matcher("rbrace");
    }
    
    private void clase()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("class");
        matcher("idclass");
        if (inSet("colon")){
            herencia();
        }
        matcher("lbrace");
        miembro_();
        matcher("rbrace");
    }
    
    private void herencia()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("colon")){
            matcher("colon");
            tipo();
        }
        else{
            if (inSet("lbrace")){
                ;
            }
            else{
                throwException("Error parsing: expected colon or lbrace"
                                + "but found " + currentToken.getToken());
            }
        }
        
    }
    
    /*
    private void miembro_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("private","var","init","static","func")){
            miembro();
        }
        else{
            if (inSet("rbrace")){
                ;
            }
            else{
                throwException("Method definition and rbrace not found");
            }
        }
    }*/
    
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
    }
    
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
    }
    
    private void constructor()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("init");
        argumentosFormales();
        bloqueMetodo();
    }
    
    private void atributo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        visibilidad();
        matcher("var");
        tipo();
        listaDeclaracionVariables();
        matcher("semicolon");
        
    }
    
    private void metodo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        tipoMetodo();
        matcher("id");
        argumentosFormales();
        bloqueMetodo();        
    }
    
    private void visibilidad()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("private")){
            matcher("private");
        }
        else{
            if (inSet("var")){
                ;
            }
        }
    }
    
    private void formaMetodo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("static")){
            matcher("static");
        }
        else{
            if (inSet("func")){
                ;
            }
        }
    }
    
    private void bloqueMetodo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("lbrace");
        declVarLocales_();
        sentencia_();
        matcher("rbrace");
        
    }
    
    private void declVarLocales_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("var")){
            declVarLocales();
            declVarLocales_();
        }
        
    }
    private void declVarLocales()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("var");
        tipo();
        listaDeclaracionVariables();
        matcher("semicolon");
        
    }
    
    private void listaDeclaracionVariables()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("id");
        listaDeclaracionVariablesF();
    }
    
    private void listaDeclaracionVariablesF()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("semicolon")){
            return;
        }
        matcher("comma");
        listaDeclaracionVariables();
    }
    private void argumentosFormales()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("lparent");
        argumentosFormalesF();
        
    }
    private void argumentosFormalesF()
        throws IllegalTokenException,
               SyntacticErrorException {
        
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
               SyntacticErrorException {
        
        argumentoFormal();
        listaArgumentosFormalesF();
    }
    
    private void listaArgumentosFormalesF()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("rparent")){
            return;
        }
        matcher(",");
        listaArgumentosFormales();
    }
    
    private void argumentoFormal()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        tipo();
        matcher("id");
    }
    private void tipoMetodo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("void")){
            matcher("void");
        }
        else{
            tipo();
        }
        
    }
    private void tipo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("array")){
            tipoArray();
        }
        else{
            if (inSet("idclass")){
                tipoReferencia();
            }
            else{
                if (inSet("bool","int","string","char")){
                    tipoPrimitivo();
                }
                else{
                    throwException("Invalid type declaration");
                }
            }
        }
    }
    private void tipoPrimitivo()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcherSomeTerminal("bool","int","string","char");
    }
    private void tipoReferencia()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("idclass");
    }
    private void tipoArray()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("array");
        tipoPrimitivo();
    }
    
    private void sentencia_()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("rbrace")){
            return;
        }
        sentencia();
        sentencia_();
    }
    private void sentencia()
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
                        sentencia();
                        sentenciaF();
                    }
                    else{
                        if (inSet("while")){
                            matcher("while");
                            matcher("lparent");
                            expresion();
                            matcher("rparent");
                            sentencia();
                        }
                        else{
                            if (inSet("return")){
                                matcher("return");
                                returnNoTerminal();
                                matcher("semicolon");
                            }
                            else{
                                if (inSet("lbrace")){
                                    bloque();
                                }
                                else{
                                    throwException("Invalid sentence");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void sentenciaF()
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
            sentencia();
        }
    }
    private void returnNoTerminal()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("semicolon")){
            return;
        }
        expresion(); //podría poner un inSet(primeros(Return))
    }
    
    private void bloque()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcher("lbrace");
        sentencia_();
        matcher("rbrace");
    }
    
    private void asignacion()
        throws IllegalTokenException,
               SyntacticErrorException {
        
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
                throwException("descripciónjj");
            }
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
        
        if (inSet("plus","minus","excmark")){
            opUnario();
            expUn();
        }
        else{
            if (inSet("null","true","false","intlit","stringlit","charlit",
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
        
        matcherSomeTerminal("plus","minus","excmark");
    }
    
    private void opMul()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        matcherSomeTerminal("ast","div","mod");
    }
    
    private void operando()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        if (inSet("null","true","false","intlit","stringlit","charlit")){
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
        
        matcherSomeTerminal("null","true","false","intlit","stringlit","charlit");
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
            if (inSet("dot")){
                accesoVar();
            }
            //else{ //colocar secundarios para reportar correctamente error
        }
    }
    
    private void accesoVar()
        throws IllegalTokenException,
               SyntacticErrorException {
        
        encadenado();
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
            if (inSet("plus","minus","excmark","null","true","false","intlit",
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
            else{
                throwException("Imposible encadenar");
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
