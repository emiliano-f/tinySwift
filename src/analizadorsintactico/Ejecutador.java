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
            AnalizadorSintactico syntactic = new AnalizadorSintactico(lexical);
            if (syntactic.program()){
                System.out.println("CORRECTO: ANALISIS SINTACTICO");
            }
            else{
                System.out.println("FINAL INESPERADO");
            }
        }
        catch (SyntacticErrorException e){
            System.err.printf("ERROR: SINTACTICO\n");
            System.err.printf("| %d | %d | %s |\n", e.getLine(),
                                                    e.getColumn(),
                                                    e.getMessage());
        }
        catch (IllegalTokenException e){
            System.err.printf("ERROR: LEXICO\n");
            System.err.printf("| %d | %d | %s |\n", e.getLine(),
                                                    e.getColumn(),
                                                    e.getMessage());
        }
        catch (Exception e){
            System.out.println(e.getClass());
            e.printStackTrace();
        }
    }
    
    /**
     * Prints tokens
     * @param lexical Lexical Analyzer for input file
     */
}