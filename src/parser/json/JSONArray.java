package parser.json;

/**
 * JSON Array parser definition.
 * 
 * @author emiliano
 */
public class JSONArray extends JSON {
    
    private final StringBuilder toPersist;
    private boolean empty = true;
    
    public JSONArray(){
        toPersist = new StringBuilder(2000);
        toPersist.append("[");
    }
    
    /**
     * If it is empty, not add comma.
     */
    private void first(){
        if (empty) {
            empty = false;
        } 
        else {
            toPersist.append(",");
        }
    }
    
    public void put(String value){
        
        first();
        toPersist.append("\"");
        toPersist.append(value);
        toPersist.append("\"");
    }
    
    public void put(int value){
        
        first();
        toPersist.append(value);
    }
    
    public void put(boolean value){
        
        first();
        toPersist.append(value);
    }
    
    public void put(JSONObject value){
        
        first();
        toPersist.append(value.toString());
    }
    
    @Override
    public String toString(){
        toPersist.append("]");
        return toPersist.toString();
    }
}