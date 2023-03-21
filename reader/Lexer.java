package reader;

public class Lexer {
    private final String input;
    private int pos = 0;
    private String curToken;

    public Lexer(String input) throws FormatErrorException {
        this.input = input;
        this.next();
    }

    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;
        }

        return sb.toString();
    }

    public void next() throws FormatErrorException {
        if (pos == input.length()) {
            return;
        }

        char c = input.charAt(pos);
        if (Character.isDigit(c)) {
            curToken = getNumber();
        } else if ("xyz,fgh,d".contains(String.valueOf(c))) {
            // 幂函数的 x, y, z; 函数名 f, g, h; 以及函数参数中间的逗号; 以及微分符号 d
            curToken = String.valueOf(c);
            pos += 1;
        } else if ("sc".contains(String.valueOf(c))) {
            curToken = c == 's' ? "sin" : "cos";
            pos += 3;
        } else if ("+-*()".contains(String.valueOf(c))) {
            if (c == '*') {
                if (input.charAt(pos + 1) == '*') {
                    curToken = "**";
                    pos += 2;
                } else {
                    curToken = "*";
                    pos += 1;
                }
            } else {
                curToken = String.valueOf(c);
                pos += 1;
            }
        } else {
            throw new FormatErrorException();
        }
    }

    public String peek() {
        return this.curToken;
    }
}
