package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.abstractsyntaxtree.expressions.ExpressionNode;
import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * ArrayNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class ArrayNode extends ChainNode {
    
    private final ExpressionNode index;
    private final String id;
    /**
     * ArrayNode constructor
     * 
     * @param id the identifier of array
     * @param index the index expression
     * @param line the line number
     */
    public ArrayNode(String id,
                     ExpressionNode index,
                     int line){
        super(line);
        this.id = id;
        this.index = index;
    }
    
    /**
     * Actions:
     *          checks that id is an array
     *          checks that expression index is Int
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @throws SemanticSentenceException 
     */
    public void check(SymbolTable table,
                      String className,
                      String methodName)
        throws SemanticSentenceException { // recuperar tipo y asignarlo con setType en la segunda pasada
        
        check(table, className, methodName, false);
    }
    
    /**
     * Actions:
     *          checks that id is an array
     *          checks that expression index is Int
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @param self 
     * @throws SemanticSentenceException 
     */
    public void check(SymbolTable table,
                      String className,
                      String methodName,
                      boolean self)
        throws SemanticSentenceException {
        
        Type type = null;
        
        if (self){
            type = table.getTypeAtt(className, id);
        }
        else{
            type = table.getTypeVar(className, methodName, id);
        }
        
        if (!type.isArray()){
            throwException(id + "is not an Array",
                           getLine());
        }
        
        //setType(type);
        setType(new Type(type.toString()));
        
        index.check(table, className, methodName);
        
        if (!index.getType().strongComparison("Int")){
            throwException("Expression on index array must be Int type but it is "
                           + index.getType().toStringIfArray(),
                           + index.getLine());
        } 
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","ArrayNode");
        json.put("identificador", id);
        json.put("tipo", getType().toStringIfArray());
        json.put("expresion", index.toJSON());
        
        return json;
    }
}