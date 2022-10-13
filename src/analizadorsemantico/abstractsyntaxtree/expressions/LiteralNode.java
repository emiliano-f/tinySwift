package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import codegeneration.CodeGenerator;
import static codegeneration.CodeGenerator.body;
import static codegeneration.CodeGenerator.bodyD;
import static codegeneration.CodeGenerator.labelD;
import static codegeneration.CodeGenerator.table;
import parser.json.JSONObject;

/**
 * LiteralNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class LiteralNode extends ExpressionNode {
    
    private final String literal;
    
    /**
     * LiteralNode constructor
     * 
     * @param literal the literal
     * @param type the type of literal
     * @param row the line number
     */
    public LiteralNode(String literal,
                       Type type,
                       int row){
        super(type, row);
        this.literal = literal;
    }
    
    /**
     * Returns literal
     * 
     * @return the literal
     */
    public String getLiteral(){
        return literal;
    }
    
    /**
     * No action to check
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     */
    @Override
    public void check(SymbolTable table, String className, String methodName){}
    
    /**
     * No action to check
     */
    @Override
    public void check(SymbolTable table, 
                      String className, 
                      String methodName,
                      boolean self){}
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","LiteralNode");
        json.put("literal", literal);
        json.put("tipo", getType().toStringIfArray());
        
        return json;
    }
    
    @Override
    public void getCode(){
        //bodyD.append(labelD).append(":\n");
        String type = getType().toString();
        switch (type){
            case "Int":
                body.append("\tli $t0, ").append(literal).append('\n');
                CodeGenerator.temp.append("Int");
                break;
            case "Bool":
                body.append("\tli $t0, ");
                if (literal.equals("true")) body.append("0\n");
                else body.append("1\n");
                CodeGenerator.temp.append("Bool");
                break;
            case "Char":
                body.append("\tli $t0, '").append(literal).append("'\n");
                CodeGenerator.temp.append("Char");
                break;
            case "String":
                // Body for .data
                bodyD.append(labelD).append(":\n");
                bodyD.append("\t.asciiz ");
                bodyD.append('"').append(literal).append("\"\n");
                // See getCodeObject() for String in VarNode
                CodeGenerator.temp.append("String");
        }
    }
}