package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * BinaryExpressionNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class BinaryExpressionNode extends ExpressionNode {
    private ExpressionNode leftOp;
    private ExpressionNode rightOp;
    private final OperatorNode operator;
    
    /**
     * BinaryExpressionNode constructor
     * 
     * @param operator the operator
     */
    public BinaryExpressionNode(OperatorNode operator){
        this.operator = operator;
    }
    
    /**
     * BinaryExpressionNode constructor
     * 
     * @param leftOp the left operand
     * @param rightOp the right operand
     * @param operator the operator
     */
    public BinaryExpressionNode(ExpressionNode leftOp,
                                ExpressionNode rightOp,
                                OperatorNode operator){
        this.leftOp = leftOp;
        this.rightOp = rightOp;
        this.operator = operator;
    }
    
    /**
     * Sets left operand
     * 
     * @param leftOp the left operand
     */
    public void setLeftOp(ExpressionNode leftOp){
        this.leftOp = leftOp;
    }
    
    /**
     * Sets right operand
     * 
     * @param rightOp the right operand
     */
    public void setRightOp(ExpressionNode rightOp){
        this.rightOp = rightOp;
    }
    
    /**
     * Actions:
     *          checks nodes type
     *          checks that operands are same type
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @throws SemanticSentenceException 
     */
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName)
        throws SemanticSentenceException {
        
        String errorMsg = "Incompatible operator "
                          + operator.getOperator()
                          + " with operand type ";
        
        operator.check(table, className, methodName);
        leftOp.check(table, className, methodName);
        rightOp.check(table, className, methodName);
        
        if (Node.equals(operator.getOperator(), "*", "/", "%", "+", "-")){ 
            if (!leftOp.getType().strongComparison("Int")){
                throwException(errorMsg + leftOp.getType(), 
                               operator.getLine());
            }
            if (!rightOp.getType().strongComparison("Int")){
                throwException(errorMsg + rightOp.getType(), 
                               operator.getLine());
            }
            
            // Set type of expression
            setType(new Type("Int"));
        }
        
        else if (Node.equals(operator.getOperator(), "<", ">", "<=", ">=")){ 
            if (!leftOp.getType().strongComparison("Int")){
                throwException(errorMsg + leftOp.getType(), 
                               operator.getLine());
            }
            if (!rightOp.getType().strongComparison("Int")){
                throwException(errorMsg + rightOp.getType(), 
                               operator.getLine());
            }
            
            // Set type of expression
            setType(new Type("Bool"));
        }
        
        else if (Node.equals(operator.getOperator(), "&&", "||")){
            
            if (!leftOp.getType().strongComparison("Bool")){
                throwException(errorMsg + leftOp.getType(), 
                               operator.getLine());
            }
            if (!rightOp.getType().strongComparison("Bool")){
                throwException(errorMsg + rightOp.getType(), 
                               operator.getLine());
            }
            
            setType(new Type("Bool"));
        }
        
        else { // ==
            
            Type leftType = leftOp.getType();
            if (!leftType.strongComparison("Bool", "Char", "String", "Int")){
                if (!leftType.strongComparison(rightOp.getType().toStringIfArray())){
                    throwException("Operands must be equals types", operator.getLine());
                }
            }
            
            setType(new Type("Bool"));
        }
        setLine(operator.getLine());
    }
    
    @Override
    public void check(SymbolTable table,
                      String className,
                      String methodName,
                      boolean self){}
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","BinaryExpressionNode");
        json.put("tipo",getType().toStringIfArray());
        json.put("operador", operator.toJSON());
        json.put("operando1", leftOp.toJSON());
        json.put("operando2", rightOp.toJSON());
        
        return json;
    }
}