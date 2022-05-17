package analizadorsemantico;

import analizadorlexico.IllegalTokenException;

import analizadorsintactico.AnalizadorSintactico;
import analizadorsintactico.SyntacticErrorException;

import analizadorsemantico.symboltable.SymbolTable;

import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Semantic Analyzer definition for tinySwift+
 * 
 * @author D. Emiliano F.
 * @see ejecutador.Ejecutador
 */
public class AnalizadorSemantico {
    
    private AnalizadorSintactico syntactic;
    private SymbolTable symbolTable;
    
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
               SemanticDeclarationException {
        
        if (syntactic.program()){
            symbolTable = syntactic.getSymbolTable();
            symbolTable.consolidation();
            persistence();
            return true;
        }
        return false;
    }
    
    /**
     * Saves JSON file after consolidation
     */
    private void persistence(){
        JSONObject table = symbolTable.toJSON(syntactic.getNameOfFile());
        
        try(FileWriter file = new FileWriter(System.getProperty("user.dir") + "/"
                                             + syntactic.getNameOfFile() + ".json")) {
            
            file.write(table.toString());
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}