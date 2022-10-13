package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.LocalStruct;
import analizadorsemantico.symboltable.MethodStruct;
import analizadorsemantico.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.Collection;
import parser.json.JSONArray;
import parser.json.JSONObject;

/**
 * CallExpressionNode definition for AST in Semantic Analyzer
 *
 * @author emiliano
 */
public class CallExpressionNode extends ExpressionNode {
    
    private final String id;
    private final String originClass;
    private final ArrayList<ExpressionNode> args;
    
    private boolean instance = true;
    
    public CallExpressionNode(String id,
                              ArrayList<ExpressionNode> args,
                              String originClass,
                              int line){
        super(line);
        super.isCallNode = true;
        this.id = id;
        this.args = args;
        this.originClass = originClass;
    }
    
    public ArrayList<ExpressionNode> getArgs(){
        return args;
    }
    
    public void setInstance(boolean flag){
        instance = flag;
    }
    
    /**
     * Actions:
     *          checks method declaration
     *          checks arguments declaration
     *          correspondence between arguments and parameters
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
     *          checks method declaration
     *          checks arguments declaration
     *          correspondence between arguments and parameters
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @throws SemanticSentenceException when expression type is not bool
     */
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName,
                      boolean self)
        throws SemanticSentenceException {
        
        // Is method declared?
        if (!table.existsMethod(className, id)){
            throwException("Method " + id + " not declared in "
                           + className + " class",
                           getLine());
        }
        
        MethodStruct method = table.getMethod(className, id);
        
        if (instance && method.isStatic()){
            throwException("Static method " + id + " can only be called by the class",
                           getLine());
        }
        
        if (!instance && !method.isStatic()){
            System.out.println(instance);
            System.out.println(method.isStatic());
            throwException(id + " is a instance method and cannot be referenced by a class",
                           getLine());
        }
            
        setType(table.getTypeMethod(className, id));
            
        ExpressionNode arg;
        Collection<LocalStruct> parameters = table.getParameters(className, id);
            
        // Check number of arguments and parameters
        if (parameters.size() != args.size()){
            throwException("In method " + id
                           + ", wrong number of arguments to match",
                           getLine());
        }
            
        int i = 0;
            
        for (LocalStruct parameter: parameters){
            arg = args.get(i);
                
            // Is argument defined
            // originClass contains the scope class of the argument
            arg.check(table, originClass, methodName);
                
            // Check types
            if (!arg.getType().strongComparison(parameter.getType())
                && !table.polymorphism(parameter.getType(), arg.getType())){
                throwException("In method " + id + ", the type in argument " 
                               + arg.getType().toStringIfArray()
                               + " not match with type of parameter "
                               + parameter.getType(),
                               getLine());
            }
                
            i++;
        }
        // When it is invoked by itself
        setInstance(true);
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        
        json.put("nodo","CallExpressionNode");
        json.put("identificador", id);
        json.put("tipo", getType().toStringIfArray());
        
        for (ExpressionNode e: args){
            array.put(e.toJSON());
        }
        json.put("argumentos", array);
        
        return json;
    }
}