package analizadorsemantico.symboltable;

import org.json.JSONObject;

/**
 * Struct definition for symbol table objects.
 * 
 * @author emiliano
 */
public abstract class Struct {
    
    // Id: lexeme
    private String id;
    // Type
    private Type type;
    // Position for class, method, attribute, ...
    private int position;
    // Line in file
    private int row;
    // Column in file
    private int col;
    
    /**
     * Constructor for Struct
     * 
     * @param id the lexeme
     * @param type the type
     * @param position the position 
     * @param row the line number
     * @param col the column number
     */
    public Struct(String id,
                  Type type,
                  int position,
                  int row,
                  int col){
        
        this.id = id;
        this.type = type;
        this.position = position;
        this.row = row;
        this.col = col;
    }
    
    /**
     * Exports the object as JSON
     * 
     * @return the JSONObject
     */
    public abstract JSONObject toJSON();

    /**
     * @return the id (lexeme)
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public Type getType(){
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }
    
    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the row to set
     */
    public void setPosition(int position) {
        this.position = position;
    }
    
    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @param row the row to set
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return the col
     */
    public int getCol() {
        return col;
    }

    /**
     * @param col the col to set
     */
    public void setCol(int col) {
        this.col = col;
    }

}
