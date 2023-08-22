package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;

/**
 *
 * @author emiliano
 */
public abstract class ExpressionNode extends Node {
    
    protected boolean isCallNode = false;
    
    public ExpressionNode(){
    }
    
    public ExpressionNode(Type type){
        super(type);
    }
    
    public ExpressionNode(int line){
        super(line);
    }
    
    public ExpressionNode(Type type,
                          int line){
        super(type, line);
    }
    
    public boolean isExpressionCallNode(){
        return isCallNode;
    }
    
    public void setExpressionCallNode(){
        isCallNode = true;
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