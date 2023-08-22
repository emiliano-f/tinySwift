package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import static codegeneration.CodeGenerator.body;
import static codegeneration.CodeGenerator.table;
import parser.json.JSONObject;
import codegeneration.CodeGenerator;
/**
 * IdExpressionNode definition for AST in Semantic Analyzer
 * Uses: parameters, attributes and local variables
 * 
 * @author emiliano
 */
public class IdExpressionNode extends ExpressionNode{
    
    // Identifier
    private final String id;
    // For code generation
    private boolean self;
    
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
        
        this.self = self;
        
        Type type = null;
        
        boolean isStatic = table.getMethod(className, methodName).isStatic();
        
        
        // Only attributes
        if (self){
            
            if (isStatic){
            throwException(methodName + " method is static and cannot "
                           + "call instances variables", getLine());
            }
            
            type = table.getTypeAtt(className, id);
            if (type == null){
                throwException(id + ", attribute, not declared",
                               getLine());
            }
            
            // Check method static
            
        }
        // Variables, paremeters and attributes
        else{
            type = table.getTypeLocal(className, methodName, id);
            if (type == null){
                
                if (isStatic){
                    throwException(methodName + " method is static and cannot "
                           + "call instances variables", getLine());
                }
                
                type = table.getTypeAtt(className, id);
                if (type == null){
                    throwException(id + ", variable or attribute, not declared",
                               getLine());
                }
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
    
    @Override
    public void getCode(){
        int pos;
        int offset;
        // In attribute
        if (self){
            getCodeCIR("");
        }
        else{
            // In variable
            if (table.isVariable(id)){
                pos = table.getVariablePosition(id);
                offset = 4*(table.getParametersCant()+pos+1);
                // si es un entero, bool, char entonces mover, sino ..?
                // no contemplo arrays aun
                if (getType().strongComparison("Bool", "Char", "Int")){
                    body.append("\tlw $t0, ").append(offset).append("($sp)\n");
                }
                else{
                    getCodeCIR(table.getNameCurrentMethod() + "_");
                }
            }
            // In parameter
            else if (table.isParameter(id)){
                pos = table.getParameterPosition(id);
                offset = 4*(pos+1);
                
                // type of data
                if (getType().strongComparison("Bool", "Char", "Int")){
                    body.append("\tlw $t0, ").append(offset).append("($sp)\n");
                }
                else{
                    getCodeCIR(table.getNameCurrentMethod() + "_");
                }
            }
            // In attribute
            else{
                getCodeCIR("");
            }
            
        }
    }
    
    private void getCodeCIR(String method){
        
        CodeGenerator.idObject.append(method).append(id);
        
        body.append("\tlw $t1, ").append(table.getNameCurrentClass());
        body.append('_').append(table.getNameCurrentMethod()).append('_');
        body.append(id).append('_').append("CIR").append('\n');
        
        // Aqui se puede generalizar salteando la tabla virtual 
        // contando la cantidad de metodos que puede tener
        /*if (table.getNameCurrentClass().equals("String")){
            body.append("\tla $t0, ($t1)\n");
            body.append("\t");
        }*/
        body.append("\tla $t0, ($t1)\n");
        //body.append("\t");
    }
}