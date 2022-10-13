package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * VarNode definition for AST in Semantic Analyzer
 * For attributes, local variables and parameters
 * 
 * @author emiliano
 */
public class VarNode extends ChainNode {
    
    private final String id;
    
    /**
     * VarNode constructor
     * 
     * @param id the name of var
     * @param line the line number
     */
    public VarNode(String id,
                   int line){
        super(line);
        this.id = id;
    }
    
    /**
     * Actions:
     *          checks that variable, parameter or attribute exists
     *          save type
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     */
    public void check(SymbolTable table, 
                      String className, 
                      String methodName)
        throws SemanticSentenceException {
        
        check(table, className, methodName, false);
    }
    
    /**
     * Actions:
     *          checks that variable, parameter or attribute exists
     *          save type
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @param self 
     */
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
        
        json.put("nodo","VarNode");
        json.put("identificador", id);
        json.put("tipo", getType().toStringIfArray());
        
        return json;
    }
}