package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.abstractsyntaxtree.expressions.ExpressionNode;
import analizadorsemantico.symboltable.SymbolTable;
import parser.json.JSONObject;

/**
 * AssignmentNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class AssignmentNode extends Node {
    
    // Left access
    private final ChainingNode leftAsg;
    // Right expression
    private final ExpressionNode rightAsg;
    
    /**
     * AssignmentNode constructor
     * 
     * @param leftAsg the access 
     * @param rightAsg the expression
     */
    public AssignmentNode(ChainingNode leftAsg,
                          ExpressionNode rightAsg){
        
        this.leftAsg = leftAsg;
        this.rightAsg = rightAsg;
    }
    
    /**
     * Actions:
     *          checks their nodes
     *          checks that assignment and expression are same type 
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @throws SemanticSentenceException when expression type is not bool
     */
    public void check(SymbolTable table,
                      String className,
                      String methodName)
        throws SemanticSentenceException {
        
        leftAsg.check(table, className, methodName);
        rightAsg.check(table, className, methodName);
        
        // If leftAsg is ArrayNode, then array[Int] == (ExpressionType.type == Int)
        /*if (leftAsg.getNodeJavaClass().equals("ArrayNode")){
            if (leftAsg.getType().toString().equals(rightAsg.getType().toStringIfArray())){
                throwException("Incompatible types on assignment in array: was expected " 
                           + leftAsg.getType().toString() 
                           + " in array but found "
                           + rightAsg.getType().toStringIfArray(), 
                           leftAsg.getLine());
            }   
        }*/
        
        /*// Polymorphism
        boolean polym = table.polymorphism(leftAsg.getType(), rightAsg.getType());*/
        
        if (!rightAsg.getType().strongComparison("nil") &&
            !leftAsg.getType().strongComparison(rightAsg.getType()) &&
            !table.polymorphism(leftAsg.getType(), rightAsg.getType())){
            throwException("Incompatible types on assignment: was expected " 
                           + leftAsg.getType().toStringIfArray() 
                           + " but found "
                           + rightAsg.getType().toStringIfArray(), 
                           leftAsg.getLine());
        }
        
        /*// Assign alternative type to 
        if (polym){
            if (leftAsg.getNode() instanceof VarNode){
                ((VarNode) leftAsg.getNode()).setPolymorphism(rightAsg.getType());
            }
        }*/
        
        setType(leftAsg.getType());
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","AssignmentNode");
        json.put("tipo", getType().toStringIfArray());
        json.put("acceso", leftAsg.toJSON());
        json.put("expresion", rightAsg.toJSON());
        
        return json;
    }
}