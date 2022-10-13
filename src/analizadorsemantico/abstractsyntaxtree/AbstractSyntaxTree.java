package analizadorsemantico.abstractsyntaxtree;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.sentences.SentencesNode;
import analizadorsemantico.symboltable.SymbolTable;
import java.util.ArrayList;
import parser.json.JSONArray;
import parser.json.JSONObject;

/**
 * Abstract Syntax Tree definition for Semantic Analyzer in TinySwift+
 * 
 * @author emiliano
 */
public class AbstractSyntaxTree {
    
    // List of classes
    private final ArrayList<ClassNode> classes = new ArrayList();
    // Current class
    private ClassNode currentClass;
    // Current method
    private MethodNode currentMethod;
    
    /**
     * Adds new class to AST
     * 
     * @param name the name of class
     */
    public void newClass(String name){
        currentClass = new ClassNode(name);
        classes.add(currentClass);
    }
    
    /**
     * Adds new method to AST
     * 
     * @param name the name of method
     */
    public void newMethod(String name){
        currentMethod = new MethodNode(name);
        currentClass.addMethod(currentMethod);
    }
    
    /**
     * Gets list of sentences in method, captured from SentencesNode
     * 
     * @return the list of sentences
     */
    public SentencesNode getSentencesList(){
        return currentMethod.getSentences();
    }
    
    /**
     * Check types inference AST
     * precondition: symbol table consolidation
     * 
     * @param table the symbol table
     * @throws SemanticSentenceException 
     */
    public void inference(SymbolTable table)
        throws SemanticSentenceException {
        
        // Classes in AST
        for (ClassNode clss: classes){
            // Methods in class
            for (MethodNode method: clss.getMethods()){
                // List of sentences in method
                for (Node sentence: method.getSentences().getSentencesList()){
                    sentence.check(table, clss.getName(), method.getName());
                }
            }
        }
    }
    
    public JSONObject toJSON(String fileName){
        JSONObject json = new JSONObject();
        JSONArray classes = new JSONArray();
        
        for (ClassNode e: this.classes){
            classes.put(e.toJSON());
        }
        
        json.put("nombre",fileName);
        json.put("clases",classes);
        
        return json;
    }
}