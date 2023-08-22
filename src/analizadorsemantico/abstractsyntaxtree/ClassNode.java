package analizadorsemantico.abstractsyntaxtree;

import java.util.ArrayList;
import parser.json.JSONArray;
import parser.json.JSONObject;

/**
 * ClassNode definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public class ClassNode {
    
    // Name of class
    private String className;
    // List of methods
    private ArrayList<MethodNode> methods = new ArrayList();
    
    /**
     * ClassNode constructor
     * 
     * @param name the name of class
     */
    public ClassNode(String name){
        this.className = name;
    }
    
    /**
     * Returns name of class
     * 
     * @return  the name of class
     */
    public String getName(){
        return className;
    }
    
    /**
     * Adds new method on ArrayList
     * 
     * @param method the new method to add
     */
    public void addMethod(MethodNode method){
        methods.add(method);
    }
    
    /**
     * Returns methods ArrayList
     * 
     * @return the arraylist
     */
    public ArrayList<MethodNode> getMethods(){
        return methods;
    }
    
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        JSONArray methods = new JSONArray();
        
        for (MethodNode e: this.methods){
            methods.put(e.toJSON());
        }
        
        json.put("clase", className);
        json.put("metodos", methods);
        
        return json;
    }
}