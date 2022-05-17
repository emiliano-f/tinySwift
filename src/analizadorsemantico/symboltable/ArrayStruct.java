package analizadorsemantico.symboltable;

/**
 *
 * @author emiliano
 */
public class ArrayStruct extends ReferenceStruct{
    
    private int length;
    private String[] elements;
    
    public ArrayStruct(String id,
                       Type type, // elements and array
                       int position,
                       int row,
                       int col,
                       String value,
                       Struct reference,
                       int length,
                       String[] elements){
        
        super(id, type, position, row, col, value, reference);
        this.length = length;
        this.elements = elements;
    }
}
