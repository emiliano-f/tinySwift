package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.AttributeStruct;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import codegeneration.CodeGenerator;
import static codegeneration.CodeGenerator.body;
import static codegeneration.CodeGenerator.bodyD;
import static codegeneration.CodeGenerator.labelD;
import static codegeneration.CodeGenerator.table;
import parser.json.JSONObject;

/**
 * VarNode definition for AST in Semantic Analyzer
 * For attributes, local variables and parameters
 * 
 * @author emiliano
 */
public class VarNode extends ChainNode {
    
    private final String id;
    private boolean self;
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
        
        this.self = self;
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
    
    /**
     * Precondition: get code from right side on assignment first
     */
    @Override
    public void getCode(){
        // Type of id (currentMethod, currentClass)
        Type type = table.getTypeVar(id);
        
        if (self){
            getCodeObject("");
            CodeGenerator.putData();
        }
        else{
            // Name and position of variable
            int offset;
            
            // It is parameter
            if (table.isParameter(id)){
                // Offset calc
                offset = 4*(table.getVariablesCant() 
                         + table.getParametersCant() 
                         - table.getParameterPosition(id));
                
                if (type.strongComparison("Int", "Bool", "Char")){
                    body.append("\tmove $s0, $t0\n");
                    body.append("\tsw $s0, ").append(offset).append("($sp)\n");
                }
                else{
                    getCodeObject(offset, table.getNameCurrentMethod() + "_");
                }
            }

            // It is local variable
            else if (table.isVariable(id)){
                offset = 4*(table.getVariablesCant()-table.getVariablePosition(id));
                
                if (type.strongComparison("Int", "Bool", "Char")){
                    body.append("\tmove $s0, $t0\n");
                    body.append("\tsw $s0, ").append(offset).append("($sp)\n");
                }
                else{
                    getCodeObject(offset, table.getNameCurrentMethod() + "_");
                }
            }

            // It is attribute
            else{
                // me parece que hay que vincular lo que haya en $t0 que va a dar
                // la parte derecha de la asignacion
                // Los comentarios de arriba no estan contemplados en el codigo 
                // puesto aqui abajo
                getCodeObject("");
                CodeGenerator.putData();
            }
        }
        
    }
    
    @Deprecated
    private void getCodeStringVarNode(int offset, String at){
        // Label for .data
        labelD.append(table.getNameCurrentClass()).append('_');
        labelD.append(table.getNameCurrentMethod()).append('_').append(id);
        labelD.append("_CIR").append(at);
        
        // bodyD contains the literal string and ":\n"
        bodyD.insert(0, labelD);
        bodyD.append("\t.word String_vtable\n");
        
        // Code for save string in stack
        body.append("\tlw $s0, ").append(labelD).append('\n');
        body.append("\tsw $s0, ").append(offset).append("($sp)\n");
        CodeGenerator.putData();
    }
    
    private void getCodeObject(String method){
        
        // Name of class object to create
        String classObject = CodeGenerator.temp.toString();
        CodeGenerator.temp.setLength(0);
        
        // Label for .data
        labelD.append(table.getNameCurrentClass()).append('_');
        labelD.append(method).append(id).append("_CIR");
        
        // ??? see getCodeStringVarNode
        bodyD.insert(0, labelD);
        
        // No vtables for Char, Int, Bool 
        if (method.equals("")){
            bodyD.append("\t.word ");
            switch (classObject){
                case "Int":
                    bodyD.append("0\n");
                    break;
                case "Bool":
                    bodyD.append("1\n");
                    break;
                case "Char":
                    bodyD.append("0\n");
                    break;
            }
            body.append("\tla $t1, ").append(table.getNameCurrentClass());
            body.append('_').append(id).append("_CIR\n");
            body.append("\tsw $t0, ($t1)\n");
        }
        else{
            bodyD.append("\t.word ").append(classObject).append("_vtable\n");
        }
        
        Type type;
        
        // Creates body (from attributes) of label (.data) 
        for (AttributeStruct a: table.getAttributesList(classObject)){
            type = a.getType();
            
            if (type.isArray()){
                // Unnecesary block (Array not contains attributes)    
                System.out.println("Not");
            }
            else{
                if (type.strongComparison("Int")){
                    bodyD.append("\t.word 0\n");
                }
                else if (type.strongComparison("Bool")){
                    bodyD.append("\t.word 1\n");
                }
                else if (type.strongComparison("Char")){
                    bodyD.append("\t.word $zero\n");
                }
                else if (type.strongComparison("String")){
                    bodyD.append("\t.asciiz \"\"\n");
                    bodyD.append("\t.word String_vtable\n");
                }
                else{
                    bodyD.append("\t.word ").append(classObject).append('_');
                    bodyD.append(method).append(a.getId()).append("_CIR\n");
                    // tengo que crear una etiqueta para esto
                }
            }
        }
    }
    
    private void getCodeObject(int offset, String method){
        
        getCodeObject(method);
        
        // Code for save object in stack
        body.append("\tlw $s0, ").append(labelD).append('\n');
        body.append("\tsw $s0, ").append(offset).append("($sp)\n");
        CodeGenerator.putData();
    }
}