package reader;

import expr.Base;
import expr.Cos;
import expr.Expr;
import expr.Factor;
import expr.Function;
import expr.Power;
import expr.Sin;
import expr.Term;

import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() throws FormatErrorException {
        Expr expr = new Expr();
        expr.addTerm(parseTerm());

        while ("+-".contains(lexer.peek())) {
            expr.addTerm(parseTerm());
        }
        return expr;
    }

    public Term parseTerm() throws FormatErrorException {
        Term term = new Term();
        term.multiCoefficient(parseNum());
        term.addFactor(parseFactor());

        while (lexer.peek().equals("*")) {
            lexer.next();
            term.multiCoefficient(parseNum());
            term.addFactor(parseFactor());
        }
        return term;
    }

    private BigInteger parseNum() throws FormatErrorException {
        BigInteger ans = BigInteger.ONE;
        if ("+-".contains(lexer.peek())) {
            String sign = lexer.peek();
            if (sign.equals("-")) {
                ans = ans.multiply(BigInteger.valueOf(-1));
            }
            lexer.next();
        }
        if (Character.isDigit(lexer.peek().charAt(0))) {
            ans = ans.multiply(new BigInteger(lexer.peek()));
            lexer.next();
        }
        return ans;
    }

    public Base parseFactor() throws FormatErrorException {
        if (lexer.peek().equals("(")) {
            lexer.next();
            Base expr = parseExpr();
            lexer.next();
            if (lexer.peek().equals("**")) {
                lexer.next();
                Integer repeat;
                try {
                    repeat = new Integer(lexer.peek());
                } catch (NumberFormatException e) {
                    throw new FormatErrorException();
                }
                lexer.next();
                Term multi = new Term();
                for (int i = 0; i < repeat; i++) {
                    multi.addFactor(expr);
                }
                return multi;
            } else {
                return expr;
            }
        } else {
            if ("xyz".contains(lexer.peek())) {
                Power p = new Power(lexer.peek());
                return parseDegree(p);
            } else if ("fgh".contains(lexer.peek())) {
                Function f = new Function(lexer.peek());
                lexer.next(); // At char '('
                lexer.next(); // After char '('
                f.addParameter(parseExpr().simplify());
                while (lexer.peek().equals(",")) {
                    lexer.next();
                    f.addParameter(parseExpr().simplify());
                }
                lexer.next();
                return f.substitute();
            } else if ("sin.cos".contains(lexer.peek())) {
                boolean isSin = lexer.peek().equals("sin");
                lexer.next(); // At char '('
                lexer.next(); // After char '('
                Factor s = isSin ? new Sin(parseExpr()) : new Cos(parseExpr());
                return parseDegree(s);
            } else if (lexer.peek().equals("d")) {
                lexer.next();
                if (!"xyz".contains(lexer.peek())) {
                    throw new FormatErrorException();
                }
                final Character variable = lexer.peek().charAt(0);
                lexer.next(); // At chat '('
                lexer.next(); // After char '('
                Expr expr = parseExpr().simplify();
                lexer.next();
                return expr.diff(variable);
            } else {
                return null;
            }
        }
    }

    private Base parseDegree(Factor f) throws FormatErrorException {
        lexer.next(); // After char ')'
        if (lexer.peek().equals("**")) {
            lexer.next();
            if (lexer.peek().equals("0")) {
                lexer.next();
                return new Term();
            } else {
                f.setDegree(new Integer(lexer.peek()));
            }
            lexer.next();
        }
        return f;
    }

    public static String stringPreProcess(String s) {
        return s.replaceAll("[ \r\t]", "")
                .replaceAll("-\\+", "-")
                .replaceAll("\\+-", "-")
                .replaceAll("--", "+")
                .replaceAll("\\+\\+", "+")
                .replaceAll("-\\+", "-")
                .replaceAll("\\+-", "-")
                .replaceAll("--", "+")
                .replaceAll("\\+\\+", "+")
                .replaceAll("\\*\\*\\+", "**");
    }
}
