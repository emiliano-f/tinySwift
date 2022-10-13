package analizadorsemantico.symboltable;

import parser.json.JSONObject;

/**
 *
 * @author emiliano
 */
public class AttributeStruct extends Struct{
    
    private boolean isPrivate;
    
    /**
     * Constructor for AttributeStruct
     * 
     * @param id the lexeme
     * @param type the type
     * @param position the number class in *.swift
     * @param row the line number in file
     * @param col the column number in file
     * @param isPrivate the visibility
     */
    public AttributeStruct(String id,
                           Type type,
                           int position,
                           int row,
                           int col,
                           boolean isPrivate){
        
        super(id, type, position, row, col);
        this.isPrivate = isPrivate;
    }

    /**
     * @return the isPrivate
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * @param isPrivate the isPrivate to set
     */
    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    
    @Override
    public JSONObject toJSON(){
        JSONObject attribute = new JSONObject();
        
        attribute.put("nombre", this.getId());
        attribute.put("tipo", this.getType().toStringIfArray());
        attribute.put("private", this.isPrivate());
        attribute.put("posicion", this.getPosition());
        
        return attribute;
    }
    
    @Deprecated
    public void getCode(StringBuilder label, StringBuilder body){
        label.append(getId());
        body.append(getId() + "\n:");
        if (!getType().isArray()){
            switch (getType().toString()){
                case "Int":
                case "Bool":
                    body.append("\t.word 4 \n");
                    break;
                case "Char":
                case "String":
                    body.append("\t.asciiz \"\"");
                    break;
                default:
                    body.append(".space 8");
            }
        }
    }
}
