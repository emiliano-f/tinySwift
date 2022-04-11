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
        System.out.println(Arrays.toString(args));
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
        
        System.out.printf("CORRECTO: ANALISIS LEXICO\n");
        System.out.printf("| %-10s | %-10s | %15s |\n", "TOKEN", "LEXEMA", "NUMERO DE LINEA");
        Token token;
        
        try {
            // Print tokens
            while (true){
                token = lexical.nextToken();
                System.out.printf("| %-10s | %-10s | %d |\n", token.getToken(),
                                                            token.getLexeme(),
                                                            token.getLine());
            }
        }
        catch (IllegalTokenException e) { // Invalid token
            System.err.printf("ERROR: LEXICO\n");
            System.err.printf("| %d | %d | %s |\n", e.getLine(),
                                                    e.getColumn(),
                                                    e.getMessage());
        }
        catch (NoSuchTokenException e){
            // End
        }
    }
}