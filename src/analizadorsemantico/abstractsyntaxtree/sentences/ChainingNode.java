package analizadorsemantico.abstractsyntaxtree.sentences;

import analizadorsemantico.SemanticSentenceException;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.symboltable.SymbolTable;
import parser.json.JSONObject;

/**
 * ChainingNode definition for AST in Semantic Analyzer
 * Works as a linkedlist node 
 * 
 * @author emiliano
 */
public class ChainingNode extends ChainNode {
    
    private ChainNode node;
    private ChainingNode chain = null;
    
    /**
     * ChainingNode constructor
     * 
     * @param node the node in current ChainingNode
     */
    public ChainingNode(ChainNode node){
        this(node, null);
    }
    
    /**
     * ChainingNode constructor
     * 
     * @param node the node in current ChainingNode
     * @param chain the next node in chain
     */
    public ChainingNode(ChainNode node,
                        ChainingNode chain){
        
        super(node.getLine());
        this.node = node;
        this.chain = chain;
    }
    
    /**
     * Returns node
     * 
     * @return the node
     */
    public Node getNode(){
        return node;
    }
    
    /**
     * Returns the next node
     * 
     * @return the chain
     */
    public ChainingNode getChaining(){
        return chain;
    }
    
    /**
     * Returns the name of class (Java) in node
     * different to ChainingNode
     * 
     * @return the name of class
     */
    public String getNodeJavaClass(){
        
        String name = null;
        
        // Rec search node != ChainingNode
        if (node.getClass().getSimpleName().equals("ChainingNode")){
            name = ((ChainingNode) node).getNodeJavaClass();
        }
        // Node != ChainingNode
        else{
            name = node.getClass().getSimpleName();
        }
        return name;
    }
    
    /**
     * Actions:
     *          checks node type
     *          save type
     *          checks next node
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @throws SemanticSentenceException 
     */
    public void check(SymbolTable table,
                      String className,
                      String methodName)
        throws SemanticSentenceException {
        
        check(table, className, methodName, false);
    }
    
    /**
     * Actions:
     *          checks node type
     *          save type
     *          checks next node
     * 
     * @param table the symbol table
     * @param className the class that contains this object
     * @param methodName the method that contains this object
     * @param self the flag to only search in object (if it is true)
     * @throws SemanticSentenceException 
     */
    public void check(SymbolTable table,
                      String className,
                      String methodName,
                      boolean self)
        throws SemanticSentenceException {
        
        if (self){
            node.check(table, className, methodName, true);
        }
        else{
            node.check(table, className, methodName);
        }
        setType(node.getType());
        
        // Next node
        if (chain != null){
            
            // Is array?
            if (getType().isArray()){
                className = "Array";
            }
            else{
                className = getType().toString();
            }
            // self=true because only has access to class members
            chain.check(table, className, "", true);
        }
    }
    
    
    
    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        
        json.put("nodo","ChainingNode");
        json.put("contenido", node.toJSON());
        if (chain != null){ 
            json.put("encadenado", chain.toJSON());
        }
        else {
            json.put("encadenado", "{}");
        }
        
        return json;
    }
}