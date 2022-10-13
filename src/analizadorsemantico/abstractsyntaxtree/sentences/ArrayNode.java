package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.abstractsyntaxtree.expressions.ExpressionNode;
import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import codegeneration.CodeGenerator;
import static codegeneration.CodeGenerator.body;
import static codegeneration.CodeGenerator.bodyD;
import static codegeneration.CodeGenerator.labelD;
import static codegeneration.CodeGenerator.table;
import parser.json.JSONObject;

/**
 * ArrayNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class ArrayNode extends ChainNode {
    
    private final ExpressionNode index;
    private final String id;
    private boolean self;
    
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
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName,
                      boolean self)
        throws SemanticSentenceException {
        
        // For code generation
        this.self = self;
        
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
    
    @Override
    public void getCode(){
        
        // Move value to save (later this only is used with Int, Char, Bool)
        body.append("\tmove $s0, $t0\n");
        index.getCode();
        
        if (self){
            // Load length
            body.append("\t#Check index\n\tla $t1, ").append(table.getNameCurrentClass());
            body.append('_').append(id).append("_CIR\n");
            getCodeArray("");
        }
        else{
            int offset;
            
            // It is parameter
            if (table.isParameter(id)){
                // Offset calc
                offset = 4*(table.getVariablesCant() 
                         + table.getParametersCant() 
                         - table.getParameterPosition(id));
                
                // Load length
                body.append("\t#Check index\n\tla $t1, ").append(offset);
                body.append("($sp)\n");
                getCodeArray(table.getNameCurrentMethod() + "_");
            }

            // It is local variable
            else if (table.isVariable(id)){
                offset = 4*(table.getVariablesCant()-table.getVariablePosition(id));
                
                // Load length
                body.append("\t#Check index\n\tla $t1, ").append(offset);
                body.append("($sp)\n");
                getCodeArray(table.getNameCurrentMethod() + "_");
            }

            // It is attribute
            else{
                // Load length
                body.append("\t#Check index\n\tla $t1, ").append(table.getNameCurrentClass());
                body.append('_').append(id).append("_CIR\n");
                getCodeArray("");
            }
        }
    }
    
    private void getCodeArray(String method){
        // This works only for Int? 
        
        // add -1 to length
        body.append("\tlw $t2, ($t1)\n\taddi $t2, $t2, -1\n");
        // if index($t0) > $t2 then 1 (true, exit) else 0 (false, continue)
        body.append("\tslt $t3, $t2, $t0\n");
        body.append("\tbnez $t3, Error_exit\n");
        // Add 1 word (4 bytes) jumping to .space
        body.append("\taddi $t0, $t0, 1\n");
        // Calculates spaces ($t0)
        body.append("\tli $t4, 4\n\tmul $t0, $t0, $t4\n");
        // Access to position 
        body.append("\tadd $t1, $t1, $t0\n");
        
        // Saving
        // It is String: create label
        if (!getType().strongComparison("Int", "Char", "Bool")){
            // Name of class object to create
            String classObject = CodeGenerator.temp.toString();
            CodeGenerator.temp.setLength(0);

            // Label for .data
            labelD.append(table.getNameCurrentClass()).append('_');
            labelD.append(method).append(CodeGenerator.counter);
            labelD.append('_').append(id).append("_CIR");
            CodeGenerator.counter += 1;

            
            bodyD.insert(0, labelD);
            bodyD.append("\t.word ").append("String").append("_vtable\n");
            
            // Code for save object in stack
            body.append("\tlw $s0, ").append(labelD).append('\n');
            CodeGenerator.putData();
        }
        // To Array
        body.append("\tsw $s0, ($t1)\n");
    }
}