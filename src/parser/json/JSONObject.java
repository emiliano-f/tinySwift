package parser.json;

/**
 * JSON Object parser definition
 * 
 * @author emiliano
 */
public class JSONObject extends JSON {
    
    private final StringBuilder toPersist;
    private boolean empty = true;
    
    public JSONObject(){
        toPersist = new StringBuilder(3000);
        toPersist.append("{");
    }
    
    private void putDescription(String description){
        
        // If it is empty, not add comma.
        if (empty) {
            empty = false;
        } 
        else {
            toPersist.append(",");
        }
        
        toPersist.append("\"");
        toPersist.append(description);
        toPersist.append("\"");
        toPersist.append(":");
    }
    
    public void put(String description,
                    String value){
        
        putDescription(description);
        toPersist.append("\"");
        toPersist.append(value);
        toPersist.append("\"");
    }
    
    public void put(String description,
                    int value){
        
        putDescription(description);
        toPersist.append(value);
    }
    
    public void put(String description,
                    boolean value){
        
        putDescription(description);
        toPersist.append(value);
    }
    
    public void put(String description, 
                    JSON json){
        
        putDescription(description);
        toPersist.append(json.toString());
    }
    
    @Override
    public String toString(){
        toPersist.append("}");
        return toPersist.toString();
    }
}