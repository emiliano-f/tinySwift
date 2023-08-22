package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * SelfNode definition for AST in SemanticAnalyzer
 * 
 * @author emiliano
 */
public class SelfNode extends ChainNode {
    
    private final String self;
    
    /**
     * SelfNode constructor
     * 
     * @param self the name of class
     * @param type the type of class (same name)
     * @param line the line number
     */
    public SelfNode(String self,
                    Type type,
                    int line){
        super(type, line);
        this.self = self;
    }
    
    /**
     * No action to check
     * 
     * @param table
     * @param className
     * @param methodName 
     */
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName){
    }
    
    /**
     * No action to check
     * 
     * @param table
     * @param className
     * @param methodName 
     * @param self
     */
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName,
                      boolean self){
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","SelfNode");
        json.put("self", self);
        json.put("tipo", getType().toStringIfArray());
        
        return json;
    }
    
    @Override
    public void getCode(){
        
    }
}