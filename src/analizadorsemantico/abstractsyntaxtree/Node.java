package analizadorsemantico.abstractsyntaxtree;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import parser.json.JSONObject;

/**
 * Node definition for AST in Semantic Analyzer
 * 
 * @author emiliano
 */
public abstract class Node {
    
    // All nodes contains a type
    private Type type;
    // All nodes contains line in file
    private int line;
    
    public Node(){
        this(null, 0);
    }
    
    public Node(int line){
        this(null, line);
    }
    
    public Node(Type type){
        this(type, 0);
    }
    
    /**
     * Node constructor
     * 
     * @param type the type of node
     * @param line the line of file
     */
    public Node(Type type, int line){
        this.type = type;
        this.line = line;
    }
    
    /**
     * Returns the type of the node
     * 
     * @return Type object
     */
    public Type getType(){
        return type;
    }
    
    /**
     * Sets the type of node
     * 
     * @param type the new type of node
     */
    public void setType(Type type){
        this.type = type;
    }
    
    /**
     * Returns line of file
     * 
     * @return the number line
     */
    public int getLine(){
        return line;
    }
    
    /**
     * Sets the line number
     * 
     * @param line the line number
     */
    public void setLine(int line){
        this.line = line;
    }
    
    /**
     * For check nodes
     * 
     * @param table the symbol table
     * @param className the current class name
     * @param methodName the current method name
     * @throws SemanticSentenceException 
     */
    public abstract void check(SymbolTable table,
                               String className,
                               String methodName) 
        throws SemanticSentenceException;
    
    /**
     * Throws SemanticSentenceException object 
     * 
     * @param description the error description
     * @param row the number line
     * @throws SemanticSentenceException 
     */
    protected void throwException(String description,
                                  int row)
        throws SemanticSentenceException {
            throw new SemanticSentenceException(description, row);
    }
    
    /**
     * Compares lexeme with other lexemes in String[]
     * 
     * @param lexeme the lexeme
     * @param values the others lexemes
     * @return true when one is equal
     * @see analizadorsemantico.abstractsyntaxtree.expression.BinaryExpressionNode
     */
    static protected boolean equals(String lexeme, String ... values){
        for (String i: values){
            if (lexeme.equals(i)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Converts the object to JSONObject
     * 
     * @return the JSONObject
     */
    abstract public JSONObject toJSON();
}