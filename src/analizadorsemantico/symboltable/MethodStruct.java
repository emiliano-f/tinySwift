package analizadorsemantico.symboltable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collection;
import parser.json.JSONArray;
import parser.json.JSONObject;

/**
 *
 * @author emiliano
 */
public class MethodStruct extends Struct {
    
    private final boolean isStatic;
    private final LinkedHashMap<String, LocalStruct> parameters;
    private final HashMap<String, LocalStruct> variables;
    
    /**
     * Constructor for MethodStruct
     * 
     * @param id the lexeme
     * @param type the type
     * @param position the number class in *.swift
     * @param row the line number in file
     * @param col the column number in file
     * @param isStatic the method form (static, nonstatic)
     */
    public MethodStruct(String id,
                        Type type,
                        int position,
                        int row,
                        int col,
                        boolean isStatic){
        
        super(id, type, position, row, col);
        parameters = new LinkedHashMap();
        variables = new HashMap();
        this.isStatic = isStatic;
    }
    
    /**
     * Return static form
     * 
     * @return true if it is static
     */
    public boolean isStatic(){
        return isStatic;
    }
    
    /**
     * Add new parameter to method
     * 
     * @param parameter the parameter to add
     */
    public void addParameter(LocalStruct parameter){
        parameters.put(parameter.getId(), parameter);
    }
    
    /**
     * Add new variable to method
     * 
     * @param variable the variable to add
     */
    public void addVariable(LocalStruct variable){
        variables.put(variable.getId(), variable);
    }
    
    /**
     * Check if a variable of parameter name already exists
     * 
     * @param local the name of variable or parameter
     * @return true if it exists
     */
    public boolean existsLocal(String local){
        return parameters.containsKey(local) || variables.containsKey(local);
    }
    
    /**
     * Returns the number of parameters
     * 
     * @return the number of parameters
     */
    public int getSizeParameters(){
        return parameters.size();
    }
    
    /**
     * Returns the number of variables
     * 
     * @return the number of variables
     */
    public int getSizeVariables(){
        return variables.size();
    }
    
    /**
     * Return the types of parameters in order
     * 
     * @return the String array with types of parameters in order
     */
    public String[] getParametersType(){
        
        Collection<LocalStruct> values = parameters.values();
        String[] types = new String[values.size()];
        int i = 0;
        
        for (LocalStruct e: values){
            types[i] = e.getType().toStringIfArray();
            i++;
        }
        return types;
    }
    
    /**
     * Gets list of parameters
     * 
     * @return the parameters LinkedHashMap
     */
    public LinkedHashMap<String, LocalStruct> getParametersHashMap(){
        return parameters;
    }
    
    /**
     * Gets list of variables
     * 
     * @return the variables Collection
     */
    public Collection<LocalStruct> getVariables(){
        return variables.values();
    }
    
    /**
     * Gets list of parameters
     * 
     * @return the variables Collection
     */
    public Collection<LocalStruct> getParameters(){
        return parameters.values();
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject method = new JSONObject();
        JSONArray parameters = new JSONArray();
        JSONArray variables = new JSONArray();
        
        // Fields
        method.put("nombre", this.getId());
        method.put("static", this.isStatic());
        method.put("retorno", this.getType().toStringIfArray());
        method.put("posicion", this.getPosition());
        
        // Values in HashMap
        Collection<LocalStruct> parametersValues = this.parameters.values();
        Collection<LocalStruct> variablesValues = this.variables.values();
        
        // Creating Array
        for (LocalStruct e: parametersValues){
            parameters.put(e.toJSON());
        }
        method.put("parametros", parameters);
        
        for (LocalStruct e: variablesValues){
            variables.put(e.toJSON());
        }
        method.put("variables", variables);
        
        return method;
    }
    
    // Gets for AST
    
    /**
     * Returns type of parameter or local variable
     * 
     * @param id the identifier 
     * @return the type of id
     */
    public Type getTypeVar(String id){
        
        Type typeVar = null;
        
        // Search in parameters
        LocalStruct var = parameters.get(id);
        if (var != null){
            typeVar = var.getType();
        }
        else{
            // Search in variables
            var = variables.get(id);
            if (var != null) {
                typeVar = var.getType();
            }
        }
        
        return typeVar;
    }
}
