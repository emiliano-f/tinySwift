package analizadorsemantico.symboltable;

import org.json.JSONObject;

/**
 * LocalStruct definition for SymbolTable in TinySwift+
 * 
 * @author emiliano
 */
public class LocalStruct extends Struct{
    
    private String value;
    
    /**
     * Constructor for LocalStruct
     * 
     * @param id the lexeme
     * @param type the type
     * @param position the number class in *.swift
     * @param row the line number in file
     * @param col the column number in file
     */
    public LocalStruct(String id,
                       Type type,
                       int position,
                       int row,
                       int col){
        
        this(id, type, position, row, col, null);
    }
    
    public LocalStruct(String id,
                       Type type,
                       int position,
                       int row,
                       int col,
                       String value){
        
        super(id, type, position, row, col);
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject local = new JSONObject();
        
        local.put("nombre", this.getId());
        local.put("tipo", this.getType().toStringIfArray());
        local.put("posicion", this.getPosition());
        
        return local;
    }
}
