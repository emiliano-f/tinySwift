package analizadorsemantico.symboltable;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collection;
import parser.json.JSONArray;
import parser.json.JSONObject;

import analizadorsemantico.SemanticDeclarationException;

/**
 * The Symbol Table for Semantic Analysis in TinySwift+
 * 
 * @author emiliano
 */
public class SymbolTable {
    
    // The current class
    private ClassStruct currentClass;
    // The current method 
    private MethodStruct currentMethod;
    // The list of classes
    private final HashMap<String, ClassStruct> classes;
    // The list of classes in Collection object
    private Collection<ClassStruct> classesLoaded;
    
    // Counters
    private int numClass;
    private int numMethod;
    private int numAttribute;
    private int numLocalVar;
    private int numParameter;
    
    /**
     * Constructor for SymbolTable
     * 
     * @throws SemanticDeclarationException
     */
    public SymbolTable() 
        throws SemanticDeclarationException {
        classes = new HashMap();
        addBase();
    }
    
    private void throwException(String description,
                                int row,
                                int col)
        throws SemanticDeclarationException {
        
        throw new SemanticDeclarationException(description,
                                         row,
                                         col);
    }
    
    /**
     * Add predefined classes: 
     *                          Object, 
     *                          IO, 
     *                          Array, 
     *                          String, 
     *                          Char, 
     *                          Bool, 
     *                          Int
     * to SymbolTable
     * 
     * @throws SemanticDeclarationException 
     */
    private void addBase()
        throws SemanticDeclarationException {
        
        // Object
        newClass("Object", new Type("Object"), 0, 0);
        addClassEntry();
        
        // IO
        newClass("IO", new Type("Object"), 0, 0);
        newMethod("out_string", new Type("void"), true, 0, 0);
        addParameter("s", new Type("String"), 0, 0);
        addMethodEntry();
        newMethod("out_int", new Type("void"), true, 0, 0);
        addParameter("i", new Type("Int"), 0, 0);
        addMethodEntry();
        newMethod("out_bool", new Type("void"), true, 0, 0);
        addParameter("b", new Type("Bool"), 0, 0);
        addMethodEntry();
        newMethod("out_char", new Type("void"), true, 0, 0);
        addParameter("c", new Type("Char"), 0, 0);
        addMethodEntry();
        newMethod("out_array", new Type("void"), true, 0, 0);
        addParameter("a", new Type("Array"), 0, 0);
        addMethodEntry();
        newMethod("in_string", new Type("String"), true, 0, 0);
        addMethodEntry();
        newMethod("in_int", new Type("Int"), true, 0, 0);
        addMethodEntry();
        newMethod("in_bool", new Type("Bool"), true, 0, 0);
        addMethodEntry();
        newMethod("in_char", new Type("Char"), true, 0, 0);
        addMethodEntry();
        addClassEntry();
        
        // Array
        newClass("Array", new Type("Object"), 0, 0);
        newMethod("length", new Type("Int"), false, 0, 0);
        addMethodEntry();
        addClassEntry();
        
        // String
        newClass("String", new Type("Object"), 0, 0);
        newMethod("length", new Type("Int"), false, 0, 0);
        addMethodEntry();
        newMethod("concat", new Type("String"), false, 0, 0);
        addParameter("s", new Type("String"), 0,0);
        addMethodEntry();
        newMethod("substr", new Type("String"), false, 0, 0);
        addParameter("i", new Type("Int"), 0,0);
        addParameter("l", new Type("Int"), 0,0);
        addMethodEntry();
        addClassEntry();
        
        // Bool
        newClass("Bool", new Type("Object"), 0, 0);
        addClassEntry();
        
        // Char
        newClass("Char", new Type("Object"), 0, 0);
        addClassEntry();
        
        // Int
        newClass("Int", new Type("Object"), 0, 0);
        addClassEntry();
    }
    
    /**
     * Creates a new class struct
     * 
     * @param nameClass the name of class
     * @param superClass the name (type) of superclass
     * @param row the line number in file
     * @param col the column number in file
     * @throws SemanticDeclarationException 
     */
    public void newClass(String nameClass,
                         Type superClass,
                         int row,
                         int col)
        throws SemanticDeclarationException {
        
        if (existClassName(nameClass)){
            throwException("Repeated class name", row, col);
        }
        
        if (circularInheritance(nameClass, superClass.toString())){
            throwException("Circular inheritance for " + nameClass +
                           " and " + superClass.toString(),
                           row,
                           col);
        }
        
        String notInh = inheritNotPermitted(superClass.toString());
        if (notInh != null){
            throwException(notInh + " class cannot be inherited",
                           row,
                           col);
        }
        
        
        currentClass = new ClassStruct(nameClass, 
                                       superClass, 
                                       numClass,
                                       row, 
                                       col);
        numClass += 1;
    }
    
    /**
     * Checks that superclass is String, Int, Char or Bool
     * 
     * @param superClass the name of superclass
     * @return the name of class that cannot be inherited
     */
    private String inheritNotPermitted(String superClass){
        String[] types = {"String", "Int", "Char", "Bool"};
        for (String e: types){
            if (e.equals(superClass)){
                return superClass;
            }
        }
        return null;
    }
    
    /**
     * Checks that exists name of class
     * 
     * @param nameClass the name of class
     * @return true if it exists
     */
    public boolean existClassName(String nameClass){
        return classes.containsKey(nameClass);
    }
    
    /**
     * Checks for circular inheritance
     * 
     * @param nameClass the name of class
     * @param superClass the name of superclass
     * @return true if there be circular inheritance
     */
    private boolean circularInheritance(String nameClass,
                                        String superClass){
        
        ClassStruct current = null;
        
        while (!superClass.equals("Object")){
            
            current = classes.get(superClass);
            if (current != null){
                superClass = current.getSuperClass().toString();
            
                if (nameClass.equals(superClass)){
                    return true;
                }
            }
            else{
                superClass = "Object";
            }
        }
        return false;
    }
    
    /**
     * Add the class struct to symbol table
     * 
     * @throws SemanticDeclarationException 
     */
    public void addClassEntry()
        throws SemanticDeclarationException {
        /*
        if (!currentClass.existsMethod("init")){
            throwException("Constructor not defined in class " 
                           + currentClass.getId(),
                           currentClass.getRow(),
                           currentClass.getCol());
        }*/
        
        classes.put(currentClass.getId(), currentClass);
        numAttribute = 0;
        numMethod = 0;
    }
    
    /**
     * Creates a new method struct
     * 
     * @param methodName the name of method
     * @param type the type
     * @param isStatic the method form
     * @param row the line number in file
     * @param col the column number in file
     * @throws SemanticDeclarationException 
     */
    public void newMethod(String methodName,
                          Type type,
                          boolean isStatic,
                          int row,
                          int col)
        throws SemanticDeclarationException{
        
        // Main must not have constructor, init
        if (currentClass.getId().equals("Main") 
            && methodName.equals("init")){
            throwException("Main must not have constructor",
                           row, col);
        }
        
        if (currentClass.existsMethod(methodName)){
            throwException("Repeated method name in class "
                           + currentClass.getId(),
                           row, col);
        }
        
        currentMethod = new MethodStruct(methodName,
                                         type,
                                         numMethod,
                                         row,
                                         col,
                                         isStatic);
        numMethod += 1;
    }
    
    /**
     * Add method struct to symbol table
     */
    public void addMethodEntry(){
        
        currentClass.addMethod(currentMethod);
        numLocalVar = 0;
        numParameter = 0;
    }
    
    /**
     * Add new attribute to symbol table
     * 
     * @param attributeName the name of attribute
     * @param type the type
     * @param row the line number in file
     * @param col the column number in file
     * @param isPrivate the visibility
     * @throws SemanticDeclarationException 
     */
    public void addAttribute(String attributeName,
                             Type type,
                             boolean isPrivate,
                             int row,
                             int col)
        throws SemanticDeclarationException {
        
        if (currentClass.existsAttribute(attributeName)){
            throwException("Repeated attribute name in class "
                           + currentClass.getId(),
                           row, col);
        }
        
        currentClass.addAttribute(new AttributeStruct(attributeName,
                                                      type,
                                                      numAttribute,
                                                      row,
                                                      col,
                                                      isPrivate));
        numAttribute += 1;
    }
    
    /**
     * Add new parameter to symbol table
     * 
     * @param parameterName the parameter name
     * @param type the type
     * @param row the line number in file
     * @param col the column number in file
     * @throws SemanticDeclarationException
     */
    public void addParameter(String parameterName,
                             Type type,
                             int row,
                             int col)
        throws SemanticDeclarationException {
        
        if (currentMethod.existsLocal(parameterName)){
            throwException("Repeated parameter name in method "
                           + currentMethod.getId() + ", class "
                           + currentClass.getId(),
                           row, col);
        }
        
        LocalStruct parameter = new LocalStruct(parameterName,
                                                type,
                                                numParameter,
                                                row,
                                                col);
        
        currentMethod.addParameter(parameter);
        numParameter += 1;
    }
    
    /**
     * Add new variable to symbol table
     * 
     * @param variableName the variable name
     * @param type the type
     * @param value the value of variable
     * @param row the line number in file
     * @param col the column number in file
     * @throws SemanticDeclarationException
     */
    public void addLocal(String variableName,
                         Type type,
                         String value,
                         int row,
                         int col)
        throws SemanticDeclarationException {
        
        if (currentMethod.existsLocal(variableName)){
            throwException("Repeated variable name in method "
                           + currentMethod.getId() + ", class "
                           + currentClass.getId(),
                           row, col);
        }
        
        LocalStruct variable = new LocalStruct(variableName,
                                               type,
                                               numLocalVar,
                                               row,
                                               col,
                                               value);
        currentMethod.addVariable(variable);
        numLocalVar += 1;
    }
    
    /**
     * Gets name of current class
     * 
     * @return the name of current class
     */
    public String getNameCurrentClass(){
        return currentClass.getId();
    }
    
    /**
     * Consolidates symbol table
     * 
     * @throws SemanticDeclarationException 
     */
    public void consolidation()
        throws SemanticDeclarationException {
        
        classesLoaded = this.classes.values();
        correctConstructors();
        existSuperClasses();
        attributesNames();
        methodsBody();
        
    }
    
    /**
     * Check that the constructor is well defined
     * 
     * @throws SemanticDeclarationException 
     */
    private void correctConstructors()
        throws SemanticDeclarationException {
        
        String name;
        
        for (ClassStruct e: classesLoaded){
            
            name = e.getId();
            
            // Not add init method to Int, Bool, Char and String (opc Array, Main)
            if (!(name.equals("Int")
                || name.equals("Bool")
                || name.equals("Char")
                || name.equals("String")
                || name.equals("Main")
                || name.equals("Array"))){
                // Get constructor (method called init)
                MethodStruct m = e.getHashMapMethods().get("init");

                if (m != null){
                    if (m.isStatic() || !m.getType().strongComparison(e.getId())){
                        throwException("Wrong definition in constructor, "
                                       + e.getId() + " class",
                                       m.getRow(), m.getCol());
                    }
                }
                // Add constructor
                else {
                    e.addMethod(new MethodStruct("init", 
                                                 new Type(e.getId()), 
                                                 0, 0, 0, 
                                                 false));
                }
            }
        }
    }
    
    /**
     * Checks that all super classes are well defined
     * 
     * @throws SemanticDeclarationException 
     */
    private void existSuperClasses()
        throws SemanticDeclarationException {
        
        for (ClassStruct e: classesLoaded){
            
            if (!classes.containsKey(e.getSuperClass())){
                throwException("Superclass " + e.getSuperClass() 
                               + " not found for " + e.getId(),
                               e.getRow(), e.getCol());
            }
        }
    }
    
    /**
     * Checks different names in attribute. Links the attributes tables
     * 
     * @throws SemanticDeclarationException 
     */
    private void attributesNames()
        throws SemanticDeclarationException {
        
        ClassStruct superClass;
        HashMap<String, AttributeStruct> attributesInSuperClass;
        
        for (ClassStruct e: classesLoaded){
            
            // Superclass ClassStruct for e
            superClass = this.classes.get(e.getSuperClass());
            // Get HashMap that contains attributes
            attributesInSuperClass = superClass.getHashMapAttributes();
            
            // Get all attributes in class
            Collection<AttributeStruct> attributesSubClass = e.getAttributes();
            
            for (AttributeStruct a: attributesSubClass){
                
                // Check if type exists
                if (!this.classes.containsKey(a.getType().toString())){
                    throwException("Type of attribute " + a.getId()
                                   + " not declared",
                                   a.getRow(),
                                   a.getCol());
                }
                
                // Check if has different name
                if (attributesInSuperClass.containsKey(a.getId())){
                    throwException("Same name for attributes is not permitted "
                                   + a.getId(),
                                   a.getRow(),
                                   a.getCol());
                }
            }
            
            // Add inherited attributes
            for (AttributeStruct a: attributesInSuperClass.values()){
                if (!a.isPrivate()){
                    e.addAttribute(a);
                }
            }
        }
    }
    
    /**
     * Checks names, signatures, return, parameters and local variable types.
     * Links the method tables to subclass
     * 
     * @throws SemanticDeclarationException 
     */
    private void methodsBody()
        throws SemanticDeclarationException {
        
        ClassStruct superClass;
        HashMap<String, MethodStruct> methodsInSuperClass;
        Collection<MethodStruct> methodsSubClass;
        MethodStruct supMethod;
        // Types of parameters
        LinkedHashMap<String, LocalStruct> parametersTypes, paramSup;
        // Types of local variables
        Collection<LocalStruct> localVariables;
        
        for (ClassStruct e: classesLoaded){
            
            // Superclass ClassStruct for e
            superClass = this.classes.get(e.getSuperClass());
            // Get HashMap that contains methods
            methodsInSuperClass = superClass.getHashMapMethods();
            
            // Get all methods in class
            methodsSubClass = e.getMethods();
            
            for (MethodStruct m: methodsSubClass){
                
                // Constructors can be "overriden" without restrictions
                if (m.getId().equals("init")){
                    continue;
                }
                
                // Check if exists type to return
                if (!m.getType().strongComparison("void")
                    && !classes.containsKey(m.getType().toString())){
                    
                    throwException("Type to return " + m.getType().toString()
                                   + " in method " + m.getId() + " not declared",
                                   m.getRow(), m.getCol());
                }
                
                // Check if types of parameters was declared
                parametersTypes = m.getParametersHashMap();
                for (Map.Entry<String, LocalStruct> iter : parametersTypes.entrySet()){
                    if (!classes.containsKey(iter.getValue().getType().toString())){
                        throwException("Type " + iter.getValue().getType().toString() 
                                       + " in parameter " + iter.getValue().getId()
                                       + " not declared",
                                       iter.getValue().getRow(),
                                       iter.getValue().getCol());
                    }
                }
                
                // Get method overriden
                supMethod = methodsInSuperClass.get(m.getId());
                
                if (supMethod != null){
                    
                    // Static methods cannot be overriden
                    if (m.isStatic() && supMethod.isStatic()){
                        throwException("Static methods " + m.getId()
                                       + " and " + supMethod.getId()
                                       + " cannot be overriden",
                                       m.getRow(), m.getCol());
                    }
                    
                    // Check signature
                    // Check return type
                    if (!m.getType().strongComparison(supMethod.getType().toStringIfArray())){
                        throwException("Method overriden: return type must be equal",
                                       m.getRow(), m.getCol());
                    }                    
                    
                    // Check number of parameters
                    if (m.getSizeParameters() != supMethod.getSizeParameters()){
                        throwException("Method overriden: number of parameters must be equal",
                                       m.getRow(), m.getCol());
                    }
                    
                    // Check type of parameters                    
                    if (m.getParametersType().equals(supMethod.getParametersType())){ //compara contenids?
                        throwException("Method overriden: types of parameters must be equal",
                                       m.getRow(), m.getCol());
                    }
                }
                
                // Check if types of local variables was declared
                localVariables = m.getVariables();
                for (LocalStruct local: localVariables){
                    if (!classes.containsKey(local.getType().toString())){
                        throwException("Type " + local.getType().toString() 
                                       + " of local variable " + local.getId()
                                       + " not declared",
                                       local.getRow(),
                                       local.getCol());
                    }
                }
            }
            
            // Add inherited methods
            for (MethodStruct m: methodsInSuperClass.values()){
                if (!m.getId().equals("init") && !m.isStatic()){
                    e.addMethod(m);
                }
            }
            
        }
    }
    
    public JSONObject toJSON(String nameOfFile){
        
        JSONObject table = new JSONObject();
        JSONArray classes = new JSONArray();
        
        table.put("nombre", nameOfFile);
        
        for (ClassStruct e: classesLoaded){
            classes.put(e.toJSON());
        }
        table.put("clases", classes);
        
        return table;
    }
    
    // Get methods (for AST)
    
    /**
     * 
     * @return 
     */
    public String getNameCurrentMethod(){
        return currentMethod.getId();
    }
    
    /**
     * Gets method type in specified class
     * 
     * @param classN the name of class
     * @param method the name of method
     * @return the type of method in class
     */
    public Type getTypeMethod(String classN,
                              String method){
        return classes.get(classN).getMethodType(method);
    }
    
    public boolean existsMethod(String classN,
                                String method){
        return classes.get(classN).existsMethod(method);
    }
    
    public Type getTypeVar(String className,
                           String methodName,
                           String id){
        
        return classes.get(className).getTypeVar(methodName, id);
    }
    
    public Type getTypeLocal(String className,
                             String methodName,
                             String id){
        return classes.get(className).getTypeLocal(methodName, id);
    }
    
    public Type getTypeAtt(String className,
                           String id){
        
        return classes.get(className).getTypeAtt(id);
    }
    
    /**
     * Gets list of parameters on method
     * 
     * @param className the name of class that contains the method
     * @param methodName the name of method to return parameters
     * @return the variables Collection
     */
    public Collection<LocalStruct> getParameters(String className,
                                                 String methodName){
        return classes.get(className).getParameters(methodName);
    }
    
    /**
     * Determines that type "sub" inherits from type "sup"
     * 
     * @param sup the potential superclass
     * @param sub the potential subclass
     * @return true when sub inherits from sup
     */
    public boolean polymorphism(Type sup,
                                Type sub){
        
        // Polymorphism is not necessary
        if (sup.strongComparison(sub)){
            return false;
        }
        
        String subb, supp;
        
        // Array. (assume array cannot be inherited)
        if (sup.isArray()){
            return sup.strongComparison(sub);
        }
        
        supp = sup.toStringIfArray();
        subb = sub.toStringIfArray();
        
        ClassStruct e;
        
        do{
            e = classes.get(subb); //dont works with Array Int -> there is capture Array
            if (e != null){
                if(e.getSuperClass().equals(supp)){
                    return true;
                }
                subb = e.getSuperClass();
            }
        } while (!subb.equals("Object"));
        
        return false;
    }
    
    public MethodStruct getMethod(String idClass, String idMethod){
        return classes.get(idClass).getMethod(idMethod);
    }
    
    public Collection<ClassStruct> getClasses(){
        return classes.values();
    }
    
    /*
     * Only for currentClass and currentMethod attributes
     */
    
    public void setCurrentClass(String className){
        currentClass = classes.get(className);
    }
    
    public void setCurrentMethod(String methodName){
        currentMethod = currentClass.getMethod(methodName);
    }
    
    public int getParameterPosition(String id){
        return currentMethod.getParameterPosition(id);
    }
    
    public int getVariablePosition(String id){
        return currentMethod.getVariablePosition(id);
    }
    
    public boolean isParameter(String id){
        return currentMethod.isParameter(id);
    }
    
    public boolean isVariable(String id){
        return currentMethod.isVariable(id);
    }
    
    public int getParametersCant(){
        return currentMethod.getSizeParameters();
    }
    
    public int getVariablesCant(){
        return currentMethod.getSizeVariables();
    }
    
    public Type getTypeVar(String id){
        return currentMethod.getTypeVar(id);
    }
    
    public Collection<AttributeStruct> getAttributesList(String className){
        return classes.get(className).getAttributes();
    }
}
