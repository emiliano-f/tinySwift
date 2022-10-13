package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import parser.json.JSONObject;

/**
 * ChainingExpressionNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class ChainingExpressionNode extends ExpressionNode {
    
    private final ExpressionNode expression;
    private ChainingExpressionNode chain;
    
    /**
     * ChainingExpressionNode constructor
     * 
     * @param expression the expression node
     * @param chain the other node to chaining
     */
    public ChainingExpressionNode(ExpressionNode expression,
                                  ChainingExpressionNode chain){
        this.expression = expression;
        this.chain = chain;
    }
    
    /**
     * ChainingExpressionNode constructor
     * 
     * @param expression the expression node
     */
    public ChainingExpressionNode(ExpressionNode expression){
        this.expression = expression;
    }
    
    /**
     * Returns expression node
     * 
     * @return the expression node
     */
    public ExpressionNode getExpressionNode(){
        return expression;
    }
    
    /**
     * Returns the next node 
     * 
     * @return the chain node
     */
    public ChainingExpressionNode getChain(){
        return chain;
    }
    
    /**
     * Returns the name of class (Java) in node
     * different to ChainingNode
     * 
     * @return the name of class
     */
    public String getNodeJavaClass(){
        
        String name = null;
        
        // Rec search node != ChainingNode
        if (expression.getClass().getSimpleName().equals("ChainingExpressionNode")){
            name = ((ChainingExpressionNode) expression).getNodeJavaClass();
        }
        // Node != ChainingNode
        else{
            name = expression.getClass().getSimpleName();
        }
        return name;
    }
    
    @Override
    public boolean isExpressionCallNode(){
        return expression.isExpressionCallNode();
    }
    
    /**
     * Actions:
     *          
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
     *          check type of expression node
     *          send type to next expression node
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
        
        if (self){
            expression.check(table, className, methodName, true);
        }
        else{
            expression.check(table, className, methodName);
        }
        
        setType(expression.getType());
        
        // Next node
        if (chain != null){
            
            // Void 
            if (getType().comparison("void")){
                throwException("The type void has no methods or attributes associated",
                               getLine());
            }
            
            // Is array?
            String newClassName;
            if (getType().isArray()){
                newClassName = "Array";
            }
            else{
                newClassName = getType().toString();
            }
            
            // Next Expression is CallExpressionNode?
            if (chain.isExpressionCallNode()){
                CallExpressionNode e = (CallExpressionNode) chain.getExpressionNode();
                // Set if current expression is Static or instance
                if (expression instanceof StaticCallExpressionNode){
                    e.setInstance(false);
                }
                else{ // only IdExpressionNode and SelfExpressionNode
                    e.setInstance(true);
                }
            }
            
            /**
             * self=true because only has access to class members
             * (*.id | *.id() returns an object)
             * */
            chain.check(table, newClassName, methodName, true);
            
            // the true type
            setType(chain.getType());
        }
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","ChainingExpressionNode");
        json.put("tipo", getType().toStringIfArray());
        json.put("expresion", expression.toJSON());
        if (chain != null){
            json.put("encadenado", chain.toJSON());
        }
        else{
            json.put("encadenado", "{}");
        }
        
        return json;
    }
    
    @Override
    public void getCode(){
        expression.getCode();
        if (chain != null){
            chain.getCode();
        }
    }
}