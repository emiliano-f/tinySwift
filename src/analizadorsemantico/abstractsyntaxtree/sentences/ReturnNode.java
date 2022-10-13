package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.abstractsyntaxtree.expressions.ExpressionNode;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * ReturnNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class ReturnNode extends Node{
    
    private ExpressionNode expNode = null;
    
    /**
     * Constructor for ReturnNode
     * 
     * @param expNode the ExpressionNode 
     * @param line the line number
     */
    public ReturnNode(ExpressionNode expNode,
                      int line){
        super(expNode.getType(), line);
        this.expNode = expNode;
    }
    
    /**
     * Constructor for ReturnNode that receives only line number
     * (return; sentence)
     * 
     * @param line the number of line in file
     */
    public ReturnNode(int line){
        super(new Type("void"), line);
    }
    
    /**
     * Actions:
     *          capture type of expression
     *          checks that expression and method type are same
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @throws SemanticSentenceException 
     */
    public void check(SymbolTable table,
                      String className,
                      String methodName)
        throws SemanticSentenceException {
        
        // Check expression
        if (expNode != null){
            expNode.check(table, className, methodName);
            setType(expNode.getType());
        }
        else{
            // When: return;
            setType(new Type("void"));
        }
        
        // Get type of method
        Type type = table.getTypeMethod(className, methodName);
        
        // Compare types
        if (!type.strongComparison(this.getType())){
            throwException("Return type "
                           + this.getType().toStringIfArray() 
                           + "is not equal to method type "
                           + type.toStringIfArray(),
                           this.getLine());
        }
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo", "ReturnNode");
        json.put("tipo", getType().toStringIfArray());
        if (expNode != null){
            json.put("retorno", expNode.toJSON());
        }
        
        return json;
    }
}