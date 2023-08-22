package analizadorsemantico.abstractsyntaxtree;

import analizadorsemantico.abstractsyntaxtree.sentences.SentencesNode;
import parser.json.JSONObject;

/**
 * MethodNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class MethodNode {
    
    // Name of method
    private final String methodName;
    // Sentences node
    private final SentencesNode sentences = new SentencesNode();
    
    /**
     * MethodNode constructor
     * 
     * @param methodName the name of method
     */
    public MethodNode(String methodName){
        this.methodName = methodName;
    }
    
    /**
     * Return the name of method
     * 
     * @return the method name
     */
    public String getName(){
        return methodName;
    }
    
    /**
     * Return SentencesNode 
     * 
     * @return the SentencesNode
     */
    public SentencesNode getSentences(){
        return sentences;
    }
    
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("metodo", methodName);
        json.put("bloque", sentences.toJSON());
        
        return json;
    }
}