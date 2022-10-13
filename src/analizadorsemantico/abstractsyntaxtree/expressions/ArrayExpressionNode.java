package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * ArrayExpressionNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class ArrayExpressionNode extends ExpressionNode {
    
    // Identifier
    private String id;
    // Expression index
    private final ExpressionNode index;
    // Type (elements)
    private Type elementsType;
    
    /**
     * ArrayExpressionNode constructor
     * 
     * @param index the index in array
     * @param elementsType the type of elements
     * @param line the line number
     */ 
    public ArrayExpressionNode(ExpressionNode index,
                               Type elementsType,
                               int line){
        
        super(new Type("Array "+elementsType), line);
        this.elementsType = elementsType;
        this.index = index;
    }
    
    /**
     * ArrayExpressionNode constructor
     * 
     * @param id the identifier of array
     * @param index the index in array
     * @param line the line number
     */ 
    public ArrayExpressionNode(String id,
                               ExpressionNode index,
                               int line){
        
        super(line);
        this.id = id;
        this.index = index;
    }
    
    /**
     * ArrayExpressionNode constructor
     * 
     * @param id the identifier of array
     * @param index the index in array
     * @param elementsType the type of elements
     * @param line the line number
     */    
    public ArrayExpressionNode(String id,
                               ExpressionNode index,
                               Type elementsType,
                               int line){
        
        super(new Type("Array "+elementsType), line);
        this.id = id;
        this.elementsType = elementsType;
        this.index = index;
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    /**
     * Actions:
     *          
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @throws SemanticSentenceException when expression type is not bool
     */
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName)
        throws SemanticSentenceException {
        check(table, className, methodName,false);
    }
    
    /**
     * Actions:
     *          checks id in symbol table
     *          verify that type of expression is Int
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @param self
     * @throws SemanticSentenceException when expression type is not bool
     */
    @Override
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
            throwException(id + " is not an Array",
                           getLine());
        }
        
        setType(new Type(type.toString()));
        index.check(table, className, methodName);
        
        if (!index.getType().strongComparison("Int")){
            throwException("Expression on index array must be Int type but it is "
                           + index.getType().toStringIfArray(), 
                           getLine());
        } 
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","ArrayExpressionNode");
        json.put("identificador", id);
        json.put("tipo", getType().toStringIfArray());
        json.put("expresion", index.toJSON());
        
        return json;
    }
}