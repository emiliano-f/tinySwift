package analizadorsemantico;

import analizadorlexico.IllegalTokenException;
import analizadorsemantico.abstractsyntaxtree.AbstractSyntaxTree;

import analizadorsintactico.AnalizadorSintactico;
import analizadorsintactico.SyntacticErrorException;

import analizadorsemantico.symboltable.SymbolTable;

import java.io.FileWriter;
import java.io.IOException;
import parser.json.JSONObject;

/**
 * Semantic Analyzer definition for tinySwift+
 * 
 * @author D. Emiliano F.
 * @see ejecutador.Ejecutador
 */
public class AnalizadorSemantico {
    
    private AnalizadorSintactico syntactic;
    private SymbolTable symbolTable;
    private AbstractSyntaxTree ast;
    
    /**
     * Constructor for AnalizadorSemantico
     * 
     * @param syntactic Syntactic Analyzer for TinySwift+
     * @throws IllegalTokenException for lexical errors
     * @throws SyntacticErrorException for syntax errors
     * @throws IncompleteSymbolTableException for symbol table error
     */
    public AnalizadorSemantico(AnalizadorSintactico syntactic)
        throws IllegalTokenException,
               SyntacticErrorException,
               IncompleteSymbolTableException {
        
        this.syntactic = syntactic;
    }
    
    /**
     * Starts the semantic analysis
     * 
     * @throws IllegalTokenException for lexical errors
     * @throws SyntacticErrorException for syntax errors
     * @throws IncompleteSymbolTableException for symbol table error
     * @throws SemanticDeclarationException for semantic errors
     */
    public boolean init()
        throws IllegalTokenException,
               SyntacticErrorException,
               IncompleteSymbolTableException,
               SemanticDeclarationException,
               SemanticSentenceException {
        
        if (syntactic.program()){
            symbolTable = syntactic.getSymbolTable();
            ast = syntactic.getAST();
            symbolTable.consolidation();
            ast.inference(symbolTable);
            persistence();
            return true;
        }
        return false;
    }
    
    /**
     * Returns AST
     *  
     * @return the ast
     */
    public AbstractSyntaxTree getAST(){
        return ast;
    }
    
    /**
     * Returns Symbol Table
     * 
     * @return the symbol table
     */
    public SymbolTable getST(){
        return symbolTable;
    }
    
    /**
     * Saves JSON file after consolidation and types inference
     */
    private void persistence(){
        JSONObject table = symbolTable.toJSON(syntactic.getNameOfFile());
        JSONObject tree = ast.toJSON(syntactic.getNameOfFile());
        
        try{
            String dir = System.getProperty("user.dir") + "/";
            
            // Symbol table
            FileWriter fileTS = new FileWriter(dir + syntactic.getNameOfFile() + ".ts.json");
            fileTS.write(table.toString());
            fileTS.close();
            
            // AST
            FileWriter fileAST = new FileWriter(dir + syntactic.getNameOfFile() + ".ast.json");
            fileAST.write(tree.toString());
            fileAST.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}