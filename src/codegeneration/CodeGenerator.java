package codegeneration;

import analizadorsemantico.abstractsyntaxtree.AbstractSyntaxTree;
import analizadorsemantico.abstractsyntaxtree.ClassNode;
import analizadorsemantico.abstractsyntaxtree.MethodNode;
import analizadorsemantico.abstractsyntaxtree.Node;
import analizadorsemantico.symboltable.AttributeStruct;
import analizadorsemantico.symboltable.ClassStruct;
import analizadorsemantico.symboltable.MethodStruct;
import analizadorsemantico.symboltable.SymbolTable;
import analizadorsemantico.symboltable.Type;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * CodeGenerator definition for TinySwift+
 * MIPS Architecture
 * 
 * @author emiliano
 */
public class CodeGenerator {
    
    // Symbol Table
    static public SymbolTable table;
    // AbstractSintaxTree
    static public AbstractSyntaxTree ast;
    // FileName
    static public String filename;
    // .text in asm
    static public LinkedHashMap<String, String> text = new LinkedHashMap();
    // .data in asm
    static public LinkedHashMap<String, String> data = new LinkedHashMap();
    // temp StringBuilder for .text section
    static public StringBuilder label = new StringBuilder();
    static public StringBuilder body = new StringBuilder();
    // StringBuilders for .data section
    static public StringBuilder labelD = new StringBuilder();
    static public StringBuilder bodyD = new StringBuilder();
    // temp StringBuilder 
    static public StringBuilder temp = new StringBuilder();
    // counter of Strings in Arrays
    static public int counter = 0;
    // String and other classes (no Int, Char, Bool)
    static public StringBuilder idObject = new StringBuilder();
    
    private CodeGenerator(){}
    
    /**
     * Sets SymbolTable, AbstractSyntaxTree and filename
     * 
     * @param table the symbol table
     * @param ast the abstract syntax tree
     * @param filename the name of file
     */
    public static void setAttributes(SymbolTable table,
                                     AbstractSyntaxTree ast,
                                     String filename){
        CodeGenerator.table = table;
        CodeGenerator.ast = ast;
        CodeGenerator.filename = filename;
    }
    
    /**
     * Generates .asm file
     */
    private static void toASM(){
        
        StringBuilder temp1 = new StringBuilder("\n\n.text\n\n");
        StringBuilder temp2 = new StringBuilder(".data\n\n");
        
        String main = text.get("Main_main");
        text.remove("Main_main");
        temp1.append(main);
        
        for (String e: text.values()){
            temp1.append(e);
        }
        
        for (String e: data.values()){
            temp2.append(e);
        }
        
        try{
            String dir = System.getProperty("user.dir") + "/";
            
            FileWriter fileASM = new FileWriter(dir + filename + ".asm");
            fileASM.write(temp2.toString());
            fileASM.write(temp1.toString());
            fileASM.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Generates .text and .data code
     */
    public static void generate(){
        
        generatePredefined();
        generateVTables();
        generateFromAST();
        toASM();
    }
    
    /**
     * Puts the current content in label and body (.text) in LinkedHashMap
     * Deletes content in label and body
     */
    public static void putText(){
        text.put(label.toString(), body.toString());
        label.setLength(0);
        body.setLength(0);
    }
    
    /**
     * Puts the current content in label and body (.data) in LinkedHashMap
     * Deletes content in label and body
     */
    public static void putData(){
        if (!labelD.isEmpty()){
            data.put(labelD.toString(), bodyD.toString());
            labelD.setLength(0);
            bodyD.setLength(0);
        }
    }
    
    /**
     * Generates the virtual tables for all classes
     */
    private static void generateVTables(){
        
        String pref;
        for (ClassStruct c: table.getClasses()){
            
            // Virtual Tables labels
            labelD.append(c.getId()).append("_vtable");
            bodyD.append(c.getId()).append("_vtable:\n");
            pref = c.getId() + "_";
            for (MethodStruct m: c.getMethods()){
                
                // Create labels of methods
                bodyD.append("\t.word ").append(pref);
                bodyD.append(m.getId()).append("\n");
            }
            putData();
        }
    }
    
    private static void generateFromAST(){
        
        String pref;
        // Get class
        for (ClassNode c: ast.getClasses()){
            pref = c.getName();
            
            // Set param on symbol table (currentClass)
            table.setCurrentClass(c.getName());
            
            // Get Method
            for (MethodNode m: c.getMethods()){
                
                // Set param on symbol table (currentMethod)
                table.setCurrentMethod(m.getName());
                
                // Label and body
                label.append(pref).append("_").append(m.getName());
                body.append(pref).append("_").append(m.getName()).append(":\n");
                
                // Save context
                body.append("""
                            \t# Save context
                            \tsw $ra, ($sp)
                            \tsw $fp, -4($sp)
                            \tmove $fp, $sp
                            """);
                
                // Reserve space for parameters and local variables
                MethodStruct tempM = table.getMethod(c.getName(), m.getName());
                body.append("\taddiu $sp, $sp, -");
                body.append(8+4*(tempM.getSizeParameters()+tempM.getSizeVariables()));
                body.append('\n');
                
                for (Node n: m.getSentences().getSentencesList()){
                    n.getCode();
                    // Registers cleaning
                    CodeGenerator.temp.setLength(0);
                }
                
                // End of program
                if (m.getName().equals("main") && c.getName().equals("Main")){
                    body.append("""
                                \t# End
                                \tli $v0, 10
                                \tsyscall\n
                                """);
                    
                    /*
                    // CIR for Main
                    // Label for .data
                    labelD.append("Main_CIR");
                    
                    //bodyD.insert(0, labelD);
                    bodyD.append(labelD);
                    bodyD.append(":\n\t.word Main_vtable\n");

                    Type type;
                    // Creates body (from attributes) of label (.data) 
                    for (AttributeStruct a: table.getAttributesList("Main")){
                        type = a.getType();

                        if (type.isArray()){
                            // Unnecesary block (Array not contains attributes)    
                            System.out.println("Not");
                        }
                        else{
                            if (type.strongComparison("Int")){
                                bodyD.append("\t.word 0\n");
                            }
                            else if (type.strongComparison("Bool")){
                                bodyD.append("\t.word 1\n");
                            }
                            else if (type.strongComparison("Char")){
                                bodyD.append("\t.word $zero\n");
                            }
                            else if (type.strongComparison("String")){
                                bodyD.append("\t.asciiz \"\"\n");
                                bodyD.append("\t.word String_vtable\n");
                            }
                            else{
                                bodyD.append("\t.word Main_").append(a.getId()).append("_CIR\n");
                                // tengo que crear una etiqueta para esto
                            }
                        }
                    }
                    putData();*/
                }
                
                // End of method
                else{ 
                    body.append("""
                                \t# Restore context
                                \tmove $sp, $fp
                                \tlw $ra, ($fp)
                                \tlw $fp, -4($fp)\n
                                \tjr $ra\n
                                """);
                }
                putText();
            }
        }
    }
    
    private static void generatePredefined(){
        
        String init = """
                      \t# Save context
                      \tsw $ra, ($sp)
                      \tsw $fp, -4($sp)
                      \tmove $fp, $sp
                      """;
        String end = """
                     \t# Restore context
                     \tmove $sp, $fp
                     \tlw $ra, ($fp)
                     \tlw $fp, -4($fp)
                     \tjr $ra\n
                     """; 
        String blank = """
                       \tli $a0, '\\n'
                       \tli $v0, 11
                       \tsyscall
                       """;
        
        // Object_init
        label.append("Object_init");
        body.append("Object_init:\n");
        
        // IO_init
        label.append("IO_init");
        body.append("IO_init:\n");
        
        // IO_out_string
        label.append("IO_out_string");
        body.append("IO_out_string:\n");
        body.append(init).append("\taddiu $sp, $sp, -12\n");
        body.append("""
                    \tla $a0, 4($sp)
                    \tli $v0, 4
                    \tsyscall
                    """);
        body.append(blank);
        body.append(end);
        putText();
        
        // IO_out_int
        label.append("IO_out_int");
        body.append("IO_out_int:\n");
        body.append(init).append("\taddiu $sp, $sp, -12\n");
        body.append("""
                    \tlw $a0, 4($sp)
                    \tli $v0, 1
                    \tsyscall
                    """);
        body.append(blank);
        body.append(end);
        putText();
        
        // IO_out_bool
        label.append("IO_out_bool");
        body.append("IO_out_bool:\n");
        body.append(init).append("\taddiu $sp, $sp, -12\n");
        body.append("""
                    \tlw $t0, 4($sp)
                    \tbeq $t0, 0, IO_out_bool_true
                    \tj IO_out_bool_false
                    """);
        body.append(blank);
        body.append(end);
        putText();
        
        label.append("IO_out_bool_false");
        body.append("IO_out_bool_false:\n");
        body.append("""
                    \tla $a0, falsestring
                    \tli $v0, 4
                    \tsyscall
                    """);
        body.append(end);
        putText();
        labelD.append("falsestring");
        bodyD.append("falsestring:\n");
        bodyD.append("\t.asciiz \"false\"\n");
        putData();
        
        label.append("IO_out_bool_true");
        body.append("IO_out_bool_true:\n");
        body.append("""
                    \tla $a0, truestring
                    \tli $v0, 4
                    \tsyscall
                    """);
        body.append(end);
        putText();
        labelD.append("truestring");
        bodyD.append("truestring:\n");
        bodyD.append("\t.asciiz \"true\"\n");
        putData();
        
        // IO_out_char
        label.append("IO_out_char");
        body.append("IO_out_char:\n");
        body.append(init).append("\taddiu $sp, $sp, -12\n");
        body.append("""
                    \tla $a0, 4($sp)
                    \tli $v0, 11
                    \tsyscall
                    """);
        body.append(blank);
        body.append(end);
        putText();
        
        // IO_out_array
        label.append("IO_out_array");
        body.append("IO_out_array:\n");
        body.append(init).append("\taddiu $sp, $sp, -12\n");
        body.append("""
                    
                    """);
        body.append(blank);
        body.append(end);
        putText();
        
        // IO_in_string
        label.append("IO_in_string");
        body.append("IO_in_string:\n");
        body.append(init).append("\taddiu $sp, $sp, -8\n");
        body.append("""
                    \tli $a1, 32
                    \tli $v0, 8
                    \tsyscall
                    """);
        body.append(end);
        putText();
        
        // IO_in_int
        label.append("IO_in_int");
        body.append("IO_in_int:\n");
        body.append(init).append("\taddiu $sp, $sp, -8\n");
        body.append("""
                    \tli $v0, 5
                    \tsyscall
                    """);
        body.append(end);
        putText();
        
        // IO_in_bool
        label.append("IO_in_bool");
        body.append("IO_in_bool:\n");
        body.append(init).append("\taddiu $sp, $sp, -8\n");
        body.append("""
                    \tli $v0, 5
                    \tsyscall
                    """);
        body.append(end);
        putText();
        
        // IO_in_char
        label.append("IO_in_char");
        body.append("IO_in_char:\n");
        body.append(init).append("\taddiu $sp, $sp, -8\n");
        body.append("""
                    \tli $v0, 12
                    \tsyscall
                    """);
        body.append(end);
        putText();
        
        // String_length
        label.append("String_length");
        body.append("String_length:\n");
        body.append(init).append("\taddiu $sp, $sp, -8\n");
        body.append("""
                    \tmove $t0, $a0 # Pointer in string
                    \tli $t1, 0  # Init long 
                                         
                    String_length_loop:
                    \tlb $t2, ($t0)  # Read a character
                    \tbeq $t2, $zero, String_length_end # End of string ('\0'=0)
                    \taddi $t1, $t1, 1 # Update counter
                    \taddi $t0, $t0, 1 # Pointer to next character
                    \tj String_length_loop #or jump
                    
                    String_length_end:
                    \tmove $v0, $t1 # To return
                    """);
        body.append(end);
        putText();
        
        // String_concat
        label.append("String_concat");
        body.append("String_concat:\n");
        body.append(init).append("\taddiu $sp, $sp, -12\n");
        body.append("""
                    
                    """);
        body.append(end);
        putText();
        
        // String_substr
        label.append("String_substr");
        body.append("String_substr:\n");
        body.append(init).append("\taddiu $sp, $sp, -16\n");
        body.append(end);
        putText();
        
        //Array_length
        label.append("Array_length");
        body.append("Array_length:\n");
        body.append(init).append("\taddiu $sp, $sp, -8\n");
        body.append("""
                    \tlw $v0, 0($t9)
                    """);
        body.append(end);
        putText();
        
        // Error_exit
        label.append("Error_exit");
        body.append("Error_exit:\n");
        body.append("""
                    \t# Error exit
                    \tli $v0, 10
                    \tsyscall\n
                    """);
        putText();
    }
}