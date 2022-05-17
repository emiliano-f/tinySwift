package analizadorsemantico;

import analizadorlexico.AnalizadorLexico;
import analizadorlexico.IllegalTokenException;
import analizadorlexico.Token;

import analizadorsintactico.AnalizadorSintactico;
import analizadorsintactico.SyntacticErrorException;

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
            AnalizadorSemantico semantic = new AnalizadorSemantico(syntactic);
            
            if(semantic.init()){
                System.out.println("CORRECTO: ANALISIS SEMANTICO - DECLARACIONES");
            }
            else{
                System.out.println("FINAL INESPERADO");
            }
        }
        
        catch (SemanticDeclarationException e){
            System.err.printf("ERROR: SEMANTICO - DECLARACIONES\n");
            System.err.printf("| %d | %d | %s |\n", e.getLine(),
                                                    e.getColumn(),
                                                    e.getMessage());
        }
        
        catch (SyntacticErrorException e){
            System.err.printf("ERROR: SINTACTICO\n");
            System.err.printf("| %d | %d | %s |\n", e.getLine(),
                                                    e.getColumn(),
                                                    e.getMessage());
            //e.printStackTrace();
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