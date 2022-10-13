package analizadorsemantico.abstractsyntaxtree.expressions;

import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.symboltable.SymbolTable;
import parser.json.JSONObject;

/**
 * OperatorNode definition for AST in Semantic Analyzer
 *
 * @author emiliano
 */
public class OperatorNode extends Node {

    private final String operator;

    /**
     * OperatorNode constructor
     *
     * @param operator the operator (lexeme)
     * @param line the line number in file
     */
    public OperatorNode(String operator,
                        int line) {
        super(line);
        this.operator = operator;
    }

    /**
     * Returns operator
     *
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }
    
    /**
     * No action to check
     */
    @Override
    public void check(SymbolTable table, String className, String methodName){}

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("nodo","OperatorNode");
        json.put("operator", operator);
        
        return json;
    }
}
