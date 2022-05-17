package analizadorsemantico.symboltable;

import java.util.HashMap;
import java.util.Collection;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * ClassStruct definition for SymbolTable in TinySwift+
 * 
 * @author emiliano
 */
public class ClassStruct extends Struct {

    // Methods in the class
    private HashMap<String, MethodStruct> methods;
    // Attributes in the class
    private HashMap<String, AttributeStruct> attributes;
    
    private boolean existsConstructor = false;
    
    /**
     * Constructor for ClassStruct
     * 
     * @param id the lexeme
     * @param superClass the superclass from inherit
     * @param position the number class in *.swift
     * @param row the line number in file
     * @param col the column number in file
     */
    public ClassStruct(String id,
                       Type superClass,
                       int position,
                       int row,
                       int col){
        
        super(id, superClass, position, row, col);
        methods = new HashMap();
        attributes = new HashMap();
    }
    
    /**
     * Add method to list of methods
     * 
     * @param method the method to add
     */
    public void addMethod(MethodStruct method){
        methods.put(method.getId(), method);
    }
    
    /**
     * Add attribute to list of attributes
     * 
     * @param attribute the attribute to add
     */
    public void addAttribute(AttributeStruct attribute){
        attributes.put(attribute.getId(), attribute);
    }
    
    /**
     * Check if the method name already exists
     * 
     * @param methodName the method name
     * @return true if it exists
     */
    public boolean existsMethod(String methodName){
        return methods.containsKey(methodName);
    }
    
    /**
     * Check if the attribute name already exists
     * 
     * @param attribute the attribute name
     * @return true if it exists
     */
    public boolean existsAttribute(String attribute){
        return attributes.containsKey(attribute);
    }
    
    /**
     * Check if the local name already exists
     * 
     * @param method the method to search
     * @param local the variable or parameter name
     * @return true if it exists
     */
    public boolean existsLocal(String method,
                               String local){
        
        return methods.get(method).existsLocal(local);
    }
    
    /**
     * Gets the super class
     * 
     * @return the type of superclass (name)
     */
    public String getSuperClass(){
        return this.getType().toString();
    }
    
    /**
     * Gets list of attributes
     * 
     * @return the attributes HashMap
     */
    public HashMap<String, AttributeStruct> getHashMapAttributes(){
        return attributes;
    }
    
    /**
     * Gets list of methods
     * 
     * @return the methods HashMap
     */
    public HashMap<String, MethodStruct> getHashMapMethods(){
        return methods;
    }
    
    /**
     * Gets list of attributes
     * 
     * @return the attributes Collection<AttributeStruct>
     */
    public Collection<AttributeStruct> getAttributes(){
        return attributes.values();
    }
    
    /**
     * Gets list of methods
     * 
     * @return the methods Collection<MethodStruct>
     */
    public Collection<MethodStruct> getMethods(){
        return methods.values();
    }
    
    @Override
    public JSONObject toJSON(){
        
        // Objects
        JSONArray methods = new JSONArray();
        JSONArray attributes = new JSONArray();
        JSONObject theClass = new JSONObject();
        
        // Values in HashMap
        Collection<MethodStruct> methodValues = this.methods.values();
        Collection<AttributeStruct> attributeValues = this.attributes.values();
        
        // Saving
        theClass.put("nombre", this.getId());
        theClass.put("heredaDe", this.getSuperClass());
        
        for (AttributeStruct e: attributeValues){
            attributes.put(e.toJSON());
        }
        theClass.put("atributos", attributes);
        
        for (MethodStruct e: methodValues){
            methods.put(e.toJSON());
        }
        theClass.put("metodos", methods);
        theClass.put("posicion", this.getPosition());
        
        return theClass;
    }
}
