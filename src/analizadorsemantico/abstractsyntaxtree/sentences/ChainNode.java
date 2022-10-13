package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;

/**
 * ChainNode definition, Wrapper for sentences in AST 
 * 
 * @author emiliano
 */
public abstract class ChainNode extends Node {
    public ChainNode(){
    }
    
    public ChainNode(Type type){
        super(type);
    }
    
    public ChainNode(int line){
        super(line);
    }
    
    public ChainNode(Type type,
                     int line){
        super(type, line);
    }
    
    /**
     * For check nodes
     * 
     * @param table the symbol table
     * @param className the current class name
     * @param methodName the current method name
     * @param self the flag to only search in object (if it is true)
     * @throws SemanticSentenceException 
     */
    public abstract void check(SymbolTable table,
                               String className,
                               String methodName,
                               boolean self) 
        throws SemanticSentenceException;
}