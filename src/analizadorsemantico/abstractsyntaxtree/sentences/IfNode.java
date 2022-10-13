package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.abstractsyntaxtree.expressions.ExpressionNode;
import analizadorsemantico.symboltable.SymbolTable;
import parser.json.JSONObject;

/**
 * IfNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class IfNode extends Node {
    
    // The expression to evaluate
    private final ExpressionNode expNode;
    // Sentences
    private final SentencesNode sentNode;
    // Sentences on else
    private final SentencesNode elseSentNode;
    
    /**
     * IfNode constructor
     * 
     * @param expNode the expression to evaluate
     * @param sentNode the sentences on if
     * @param elseSentNode the sentences on else 
     */
    public IfNode(ExpressionNode expNode,
                  SentencesNode sentNode,
                  SentencesNode elseSentNode){
        this.expNode = expNode;
        this.sentNode = sentNode;
        this.elseSentNode = elseSentNode;
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
        
        // Check expression type
        expNode.check(table, className, methodName);
        
        if (!expNode.getType().strongComparison("Bool")){
            throwException("Expression on if sentence must be Bool but it is"
                           + expNode.getType().toStringIfArray(), 
                           expNode.getLine()); 
        }
        
        // Check sentences
        for (Node sentence: sentNode.getSentencesList()){
            sentence.check(table, className, methodName);
        }
        
        // Check else sentences
        for (Node sentence: elseSentNode.getSentencesList()){
            sentence.check(table, className, methodName);
        }
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","IfNode");
        json.put("expresion", expNode.toJSON());
        json.put("sentencias", sentNode.toJSON());
        json.put("sentencias_else", elseSentNode.toJSON());
        
        return json;
    }
}