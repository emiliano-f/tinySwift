package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.abstractsyntaxtree.expressions.ExpressionNode;
import analizadorsemantico.symboltable.SymbolTable;
import parser.json.JSONObject;

/**
 * WhileNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class WhileNode extends Node {
    
    // The expression to evaluate
    private final ExpressionNode expNode;
    // List of sentences
    private final SentencesNode sentNode;
    
    /**
     * WhileNode constructor
     * 
     * @param expNode the expression in while
     * @param sentNode the sentences list
     */
    public WhileNode(ExpressionNode expNode,
                     SentencesNode sentNode){
        this.expNode = expNode;
        this.sentNode = sentNode;
    }
    
    /**
     * Actions:
     *          checks that expression type is bool
     *          checks their sentences
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
        
        // Check expression
        expNode.check(table, className, methodName);
        
        if (!expNode.getType().strongComparison("Bool")){
            throwException("Expression on while sentence must be Bool but it is "
                           + expNode.getType().toStringIfArray(), 
                           expNode.getLine()); // arreglar numero de liena
        }
        
        // Check sentences
        for (Node sentence: sentNode.getSentencesList()){
            sentence.check(table, className, methodName);
        }
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","WhileNode");
        json.put("expresion", expNode.toJSON());
        json.put("sentencias", sentNode.toJSON());
        
        return json;
    }
}