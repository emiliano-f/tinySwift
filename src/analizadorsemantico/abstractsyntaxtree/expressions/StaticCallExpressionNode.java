package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import codegeneration.CodeGenerator;
import parser.json.JSONObject;

/**
 * StaticCallExpressionNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class StaticCallExpressionNode extends ExpressionNode {
    
    private final String idClass;
    
    /**
     * StaticCallExpressionNode constructor
     * 
     * @param idClass the id of class that contains the static method
     * @param line the line number
     */
    public StaticCallExpressionNode(String idClass,
                                    int line){
        super(line);
        this.idClass = idClass;
    }
    
    /**
     * Actions:
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
        
        check(table, className, methodName, false);
    }
    
    /**
     * Actions:
     *          checks class declaration
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
        
        // Is class declared?
        if (!table.existClassName(idClass)){
            throwException("Class " + idClass + " not found",
                           getLine());
        }
            
        setType(new Type(idClass));
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","StaticCallExpressionNode");
        json.put("identificador", idClass);
        
        return json;
    }
    
    @Override
    public void getCode(){
        CodeGenerator.temp.append(idClass);
    }
}