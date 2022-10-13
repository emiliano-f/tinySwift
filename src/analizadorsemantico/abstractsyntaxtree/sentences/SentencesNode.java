package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.abstractsyntaxtree.Node;
import java.util.ArrayList;
import parser.json.JSONArray;
import parser.json.JSONObject;

/**
 * SentencesNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class SentencesNode {
    
    private final ArrayList<Node> sentences = new ArrayList();
    
    /**
     * Adds new sentence to ArrayList
     * 
     * @param sentence the new sentence to add
     */
    public void addSentence(Node sentence){

        // Sentence is null when <Sentence> ::= ';'
        if (sentence != null){
            sentences.add(sentence);
        }
    }
    
    /**
     * Returns ArrayList with sentences
     * 
     * @return the ArrayList
     */
    public ArrayList<Node> getSentencesList(){
        return sentences;
    }
    
    public JSONObject toJSON(){
        
        JSONObject json = new JSONObject();
        JSONArray sentences = new JSONArray();
        
        for (Node sent: this.sentences){
            sentences.put(sent.toJSON());
        }
        
        json.put("nodo", "SentencesNode");
        json.put("sentences", sentences);
        
        return json;
    }
}
