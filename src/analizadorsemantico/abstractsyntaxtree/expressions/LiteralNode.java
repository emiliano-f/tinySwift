package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * LiteralNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class LiteralNode extends ExpressionNode {
    
    private final String literal;
    
    /**
     * LiteralNode constructor
     * 
     * @param literal the literal
     * @param type the type of literal
     * @param row the line number
     */
    public LiteralNode(String literal,
                       Type type,
                       int row){
        super(type, row);
        this.literal = literal;
    }
    
    /**
     * Returns literal
     * 
     * @return the literal
     */
    public String getLiteral(){
        return literal;
    }
    
    /**
     * No action to check
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     */
    @Override
    public void check(SymbolTable table, String className, String methodName){}
    
    /**
     * No action to check
     */
    @Override
    public void check(SymbolTable table, 
                      String className, 
                      String methodName,
                      boolean self){}
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","LiteralNode");
        json.put("literal", literal);
        json.put("tipo", getType().toStringIfArray());
        
        return json;
    }
}