package expr;

import reader.FormatErrorException;
import reader.FunctionDefine;
import reader.Lexer;
import reader.Parser;

import java.util.ArrayList;

public class Function extends Factor implements Base {
    private static ArrayList<FunctionDefine> defines = new ArrayList<>();
    private final String functionName;
    private final ArrayList<Expr> parameters;

    private int degree = 1;

    public static void addDefine(FunctionDefine fd) {
        defines.add(fd);
    }

    public Function(String name) {
        functionName = name;
        this.parameters = new ArrayList<>();
    }

    public void addParameter(Expr parameter) {
        this.parameters.add(parameter);
    }

    public Base substitute() throws FormatErrorException {
        Expr ans = new Expr();
        String model = null;
        ArrayList<Character> formatParameters = new ArrayList<>();
        for (int i = 0; i < defines.size(); i++) {
            if (defines.get(i).getName().equals(functionName)) {
                model = defines.get(i).getModel().toString();
                formatParameters.addAll(defines.get(i).getParameters());
            }
        }
        for (int  i = 0; i < formatParameters.size(); i++) {
            char para = (char) ('k' + i);
            model = model.replaceAll(
                    formatParameters.get(i).toString(),
                    String.valueOf(para)
            );
            formatParameters.set(i, para);
        }
        for (int i = 0; i < formatParameters.size(); i++) {
            model = model.replaceAll(
                    formatParameters.get(i).toString(),
                    "(" + parameters.get(i).toString() + ")"
            );
        }
        //System.out.println(model);
        Lexer lexer = new Lexer(Parser.stringPreProcess(model));
        Parser parser = new Parser(lexer);
        return parser.parseExpr();
    }

    @Override
    public Base multiply(Base other) {
        return null;
    }

    @Override
    public String getAlphabetic() {
        return null;
    }

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public int getDegree() {
        return degree;
    }

    @Override
    public void setDegree(int degree) {
        this.degree = degree;
    }

    @Override
    public Factor changeDegree(int degree) {
        return new Function(functionName);
    }

    @Override
    public String toString() {
        try {
            return substitute().toString();
        } catch (FormatErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Base diff(Character variable) {
        System.err.println("Untouchable Function!");
        return null;
    }
}
