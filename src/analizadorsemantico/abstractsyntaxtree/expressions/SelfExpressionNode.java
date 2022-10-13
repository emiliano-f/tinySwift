package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * SelfExpressionNode definition for AST in Semantic Analyzer 
 * 
 * @author emiliano
 */
public class SelfExpressionNode extends ExpressionNode {
    
    private final String self;
    
    /**
     * SelfExpressionNode constructor
     * 
     * @param self the name of class
     * @param line the line number
     */
    public SelfExpressionNode(String self,
                              int line){
        super(new Type(self), line);
        this.self = self;
    }
    
    /**
     * No action to check 
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @throws SemanticSentenceException when expression type is not bool
     */
    public void check(SymbolTable table,
                      String className,
                      String methodName)
        throws SemanticSentenceException {
    }
    
    /**
     * No action to check 
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @param self 
     * @throws SemanticSentenceException when expression type is not bool
     */
    public void check(SymbolTable table,
                      String className,
                      String methodName,
                      boolean self)
        throws SemanticSentenceException {
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","SelfExpressionNode");
        json.put("self", self);
        json.put("tipo", getType().toStringIfArray());
        
        return json;
    }
    
    @Override
    public void getCode(){
        
    }
}