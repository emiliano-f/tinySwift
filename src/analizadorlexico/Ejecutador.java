package analizadorlexico;

import java.util.Arrays;

/**
 * Launcher for etapa1. TinySwift+
 * 
 * @author D. Emiliano F.
 * @see analizadorlexico.AnalizadorLexico
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
            showTokens(lexical);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Prints tokens
     * @param lexical Lexical Analyzer for input file
     */
    private static void showTokens(AnalizadorLexico lexical){
        
        String toPrint = String.format("CORRECTO: ANALISIS LEXICO\n| %-10s | "
                                       + "%-10s | %-15s |\n", "TOKEN", "LEXEMA",
                                       "NUMERO DE LINEA");
        Token token;
        
        try {
            // Print tokens
            do{
                token = lexical.nextToken();
                if (token != null){
                toPrint += String.format("| %-10s | %-10s | %-15d |\n", token.getToken(),
                                                                        token.getLexeme(),
                                                                        token.getLine());
                }
            }while (token != null);
            System.out.println(toPrint);
        }
        catch (IllegalTokenException e) { // Invalid token
            System.err.printf("ERROR: LEXICO\n");
            System.err.printf("| %d | %d | %s |\n", e.getLine(),
                                                    e.getColumn(),
                                                    e.getMessage());
        }
    }
}