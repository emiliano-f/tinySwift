package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * IdExpressionNode definition for AST in Semantic Analyzer
 * Uses: parameters, attributes and local variables
 * 
 * @author emiliano
 */
public class IdExpressionNode extends ExpressionNode{
    
    // Identifier
    private final String id;
    
    /**
     * ArrayExpressionNode constructor
     * 
     * @param id the identifier of array
     * @param line the line number
     */
    public IdExpressionNode(String id,
                            int line){
        super(line);
        this.id = id;
    }
    
    /**
     * Actions:
     *          checks node type
     *          save type
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
        
        check(table, className, methodName, false);
    }
    
    /**
     * Actions:
     *          checks node type
     *          save type
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @param self
     * @throws SemanticSentenceException 
     */
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName,
                      boolean self)
        throws SemanticSentenceException {
        
        Type type = null;
        
        // Only attributes
        if (self){
            type = table.getTypeAtt(className, id);
            if (type == null){
                throwException(id + ", attribute, not declared",
                               getLine());
            }
        }
        // Variables, paremeters and attributes
        else{
            type = table.getTypeVar(className, methodName, id);
            if (type == null){
                throwException(id + ", variable or attribute, not declared",
                               getLine());
            }
        }
        
        this.setType(type);
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","IdExpressionNode");
        json.put("identificador", id);
        json.put("tipo", getType().toStringIfArray());
        
        return json;
    }
    
}