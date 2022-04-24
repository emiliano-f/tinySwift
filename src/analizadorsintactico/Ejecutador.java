/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package analizadorsintactico;

import analizadorlexico.AnalizadorLexico;
import analizadorlexico.IllegalTokenException;
import analizadorlexico.Token;

/**
 *
 * @author emiliano
 */
public class Ejecutador {
    
    /**
     * @param args the command line arguments
     * Valid input: java -jar etapa1.jar ARCHIVO_FUENTE [ARCHIVO_SALIDA]
     */
    public static void main(String[] args) {
        //System.out.println(String.valueOf((int) '¨'));
        //System.out.println(Arrays.toString(args));
        try{
            AnalizadorLexico lexical = new AnalizadorLexico(args);
            SyntacticAnalyzer syntactic = new SyntacticAnalyzer(lexical);
            if (syntactic.program()){
                System.out.println("CORRECTO: ANALISIS SINTACTICO");
            }
            else{
                System.out.println("FINAL INESPERADO");
            }
        }
        catch (SyntacticErrorException e){
            System.out.println("Columna Row  " + e.getColumn() + "  " + e.getLine());
            e.printStackTrace();   
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Prints tokens
     * @param lexical Lexical Analyzer for input file
     */
}