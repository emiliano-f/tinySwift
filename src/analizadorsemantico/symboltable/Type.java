package analizadorsemantico.symboltable;

import analizadorlexico.Token;

/**
 *
 * @author emiliano
 */
public class Type {
    
    private String type;
    private boolean array = false;
    
    public Type(Token type){
        
        this.type = type.getLexeme();
    }
    
    public Type(String type){
        this.type = type;
    }
    
    @Override
    public String toString(){
        return type;
    }
    
    public String toStringIfArray(){
        return array? "Array " + type : type;
    }
    
    public boolean compare(String type){
        return type.equals("Array " + this.type) || type.equals(this.type);
    }
    
    public void setArray(){
        array = true;
    }
    
    public boolean isArray(){
        return array;
    }
}
