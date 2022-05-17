package analizadorsemantico.symboltable;

/**
 *
 * @author emiliano
 */
public class ReferenceStruct extends LocalStruct {
    
    private Struct reference;
    
    public ReferenceStruct(String id,
                           Type type,
                           int position,
                           int row,
                           int col,
                           String value,
                           Struct reference){
        
        super(id, type, position, row, col, value);
        this.reference = reference;
    }

    /**
     * @return the reference
     */
    public Struct getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(Struct reference) {
        this.reference = reference;
    }
}
