import expr.Expr;
import expr.Function;
import reader.FunctionDefine;
import reader.Parser;
import reader.Lexer;
import reader.FormatErrorException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FormatErrorException {

        Scanner scanner = new Scanner(System.in);
        int n = new Integer(scanner.nextLine());
        for (int i = 0; i < n; i++) {
            Function.addDefine(new FunctionDefine(scanner.nextLine()));
        }

        String input = scanner.nextLine();
        input = Parser.stringPreProcess(input);

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Expr expr = parser.parseExpr();
        Expr simp = expr.simplify();
        System.out.println(
                simp.toString().
                        replaceAll("\\+", " + ").
                        replaceAll("-", " - ")
        );

        //System.out.println(stringPreProcess("+-2*x+3"));
    }

}
