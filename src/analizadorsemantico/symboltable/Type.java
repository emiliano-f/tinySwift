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
    
    public Type(String type,
                boolean array){
        this.type = type;
        this.array = array;
    }
    
    @Override
    public String toString(){
        return type;
    }
    
    public String toStringIfArray(){
        return array? "Array " + type : type;
    }
    
    public boolean comparison(String ... type){
        for (String i: type){
            if (i.equals("Array " + this.type) || i.equals(this.type)){
                return true;
            }
        }
        return false;
    }
    
    public boolean comparison(Type anotherType){
        return type.equals(anotherType.toString());
    }
    
    public boolean strongComparison(String ... type){
        
        String thisType;
        if (array){
            thisType = "Array " + this.type;
        }
        else{
            thisType = this.type;
        }
        
        for (String i: type){
            if (thisType.equals(i)){
                return true;
            }
        }
        return false;
    }
    
    public boolean strongComparison(Type anotherType){
        boolean value;
        if (array){
            value = anotherType.toStringIfArray().equals("Array " + type);
        }
        else{
            value = anotherType.toStringIfArray().equals(type);
        }
        return value;
    }
    
    public static Type obtainLiteralType(String literal){
        String typ;
        if (literal.equals("nil")){
            typ = "nil"; //check this
        }
        
        else if (literal.equals("true") || literal.equals("false")){
            typ = "Bool";
        }
        
        else if (literal.equals("charlit")){
            typ = "Char";
        }
        
        else if (literal.equals("intlit")){
            typ = "Int";
        }
        
        else{ // only stringlit
            typ = "String";
        }
        
        return new Type(typ);
    }
    
    public void setArray(){
        array = true;
    }
    
    public boolean isArray(){
        return array;
    }
}
