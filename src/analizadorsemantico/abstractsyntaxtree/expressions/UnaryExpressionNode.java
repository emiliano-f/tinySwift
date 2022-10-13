package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.symboltable.SymbolTable;
import parser.json.JSONObject;

/**
 * UnaryExpressionNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class UnaryExpressionNode extends ExpressionNode {
    
    // Operator
    private final OperatorNode opUnario;
    // Literal or Primary
    private final ExpressionNode operand;
    
    /**
     * UnaryExpressionNode constructor 
     * 
     * @param opUnario the operator
     * @param operand the literal or primary
     */
    public UnaryExpressionNode(OperatorNode opUnario,
                               ExpressionNode operand) {
        
        super(operand.getType());
        this.opUnario = opUnario;
        this.operand = operand;
    }
    
    /**
     * Actions:
     *          checks operand and operator types
     *          checks compatibility of types
     * 
     * @param table the symbol table
     * @param className the class name
     * @param methodName the method name
     * @throws SemanticSentenceException 
     */
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName)
        throws SemanticSentenceException {
        
        String errorMsg = "Incompatible unary operator " 
                          + opUnario.getOperator() 
                          + " with operand "
                          + operand.getType().toStringIfArray();
        
        opUnario.check(table, className, methodName);
        operand.check(table, className, methodName);
        
        if (Node.equals(opUnario.getOperator(), "+", "-")){
            if (!operand.getType().strongComparison("Int")){
                throwException(errorMsg, opUnario.getLine());
            }
        }
        else {  // ! -> not
            if (!operand.getType().strongComparison("Bool")){
                throwException(errorMsg, opUnario.getLine());
            }
        }
        
        setType(operand.getType());
    }
    
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName,
                      boolean self){}
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","UnaryExpressionNode");
        json.put("tipo", getType().toStringIfArray());
        json.put("operador", opUnario.toJSON());
        json.put("operando", operand.toJSON());
        
        return json;
    }
}