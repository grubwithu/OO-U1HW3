package reader;

import expr.Expr;

import java.util.ArrayList;

public class FunctionDefine {
    private Expr model;
    private String name;
    private ArrayList<Character> parameters;

    public FunctionDefine(String defineLine) throws FormatErrorException {
        String[] split = Parser.stringPreProcess(defineLine)
                .split("=");
        if (split.length != 2) {
            throw new FormatErrorException();
        }
        Lexer l = new Lexer(split[1]);
        Parser p = new Parser(l);
        model = p.parseExpr().simplify();
        name = split[0].substring(0, 1);
        String[] parameters = split[0].substring(2, split[0].length()).split(",");
        this.parameters = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            this.parameters.add(parameters[i].charAt(0));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("(");
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i));
            if (i != parameters.size() - 1) {
                sb.append(',');
            }
        }
        sb.append(")=");
        sb.append(model.toString());
        return sb.toString();
    }

    public Expr getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Character> getParameters() {
        return parameters;
    }
}
