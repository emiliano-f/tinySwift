package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.LocalStruct;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import codegeneration.CodeGenerator;
import static codegeneration.CodeGenerator.body;
import static codegeneration.CodeGenerator.bodyD;
import static codegeneration.CodeGenerator.labelD;
import static codegeneration.CodeGenerator.table;
import java.util.ArrayList;
import java.util.Collection;
import parser.json.JSONArray;
import parser.json.JSONObject;

/**
 * NewExpressionNode definition for AST in Semantic Analyzer
 *
 * @author emiliano
 */
public class NewExpressionNode extends ExpressionNode {
    
    private final String idClass;
    private ExpressionNode expression = null;
    private ArrayList<ExpressionNode> args = null;
    
    /**
     * NewExpressionNode constructor: method mode
     * 
     * @param idClass the class of the object
     * @param args the arguments of constructor
     * @param line the line number
     */
    public NewExpressionNode(String idClass,
                             ArrayList<ExpressionNode> args,
                             int line){
        super(line);
        this.idClass = idClass;
        this.args = args;
    }
    
    /**
     * NewExpressionNode constructor: array mode
     * 
     * @param idClass the Primitive Type
     * @param expression the expression index
     * @param line the line number
     */
    public NewExpressionNode(String idClass,
                             ExpressionNode expression,
                             int line){
        super(line);
        this.idClass = idClass;
        this.expression = expression;
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
        
        // Check name
        if (!table.existClassName(idClass)){
            throwException("Class " + idClass + " not found", getLine());
        }
        
        // Array
        if (args == null){
            setType(new Type(idClass, true));
            expression.check(table, className, methodName);
            
            // Index must be LiteralNode
            if (!(expression instanceof LiteralNode)){
                throwException("Index expression must be int literal", 
                               expression.getLine());
            }
            
            if (!expression.getType().strongComparison("Int")){
                throwException("Expression on index array must be Int type but it is "
                               + expression.getType().toStringIfArray(),
                               expression.getLine());
            }
            
            if (Integer.valueOf(((LiteralNode) expression).getLiteral()) < 0){
                throwException("Index must be >= 0",
                               expression.getLine());
            }
        }
        // New Object
        else{
            setType(new Type(idClass));
            ExpressionNode arg;
            Collection<LocalStruct> parameters = table.getParameters(idClass, "init");
            
            // Check number of arguments and parameters
            if (parameters.size() != args.size()){
                throwException("In " + idClass
                               + " constructor, wrong number of arguments to match",
                               getLine());
            }
            
            int i = 0;
            
            for (LocalStruct parameter: parameters){
                arg = args.get(i);
                
                // Is argument defined
                arg.check(table, className, methodName);
                
                // Check types
                if (!arg.getType().strongComparison(parameter.getType())){
                    throwException("In " + idClass + " constructor, the type in argument " 
                                   + arg.getType().toStringIfArray()
                                   + " not match with type of parameter "
                                   + parameter.getType(),
                                   getLine());
                }
                
                i++;
            }
        }
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
                      String methodName,
                      boolean self)
        throws SemanticSentenceException {}
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","NewExpressionNode");
        json.put("tipo", getType().toStringIfArray());
        if (args == null){
            json.put("expresion", expression.toJSON());
        }
        else{
            JSONArray array = new JSONArray();
            for (ExpressionNode e: args){
                array.put(e.toJSON());
            }
            json.put("argumentos", array);
        }
        
        return json;
    }
    
    @Override
    public void getCode(){
        
        Type type;
        if (args == null){
            CodeGenerator.temp.append("Array");
            // It is LiteralNode
            int index = Integer.valueOf(((LiteralNode) expression).getLiteral());
            // Add to data
            bodyD.append(":\n\t.word ").append(index);
            bodyD.append("\n\t.space ").append(4*index).append('\n');
        }
        else{
            // See VarNode
            CodeGenerator.temp.append(idClass);
            body.append("\t#New Object\n");
            
            // Push in next frame (parameters)
            // -4: fp y ra 
            int offset = -4-4*(table.getMethod(idClass, "init").getSizeParameters());
            for (ExpressionNode e: args){
                e.getCode();
                body.append("\tsw $t0, ").append(offset).append("($sp)\n");
                offset += 4;
            }
            body.append("\tjal ").append(idClass).append("_init\n");
        }
    }
}