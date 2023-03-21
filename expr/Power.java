package expr;

import java.math.BigInteger;

/**
 * Power 类：表示单个的幂函数表达式。
 * 此类还用于表示一些特殊的因子，例如：
 *  1. 变量因子（幂为 1）
 *  2. 常数因子（幂为 0）
 */
public class Power extends Factor implements Base {
    private int degree;
    private String alphabetic;

    public Power(String alpha) {
        this.alphabetic = alpha;
        this.degree = 1;
    }

    public Power(String alpha, int degree) {
        this.alphabetic = alpha;
        this.degree = degree;
    }

    @Override
    public String getAlphabetic() {
        return alphabetic;
    }

    @Override
    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    @Override
    public Factor changeDegree(int degree) {
        return new Power(alphabetic, degree);
    }

    @Override
    public String toString() {
        if (this.degree == 1) {
            return alphabetic.toString();
        } else {
            return alphabetic + "**" + String.valueOf(degree);
        }
    }

    @Override
    public Base multiply(Base other) {
        if (other instanceof Expr) {
            return ((Expr) other).multiply(this);
        } else if (other instanceof Factor) {
            Term term = new Term();
            term.addFactor(this);
            term.addFactor(other);
            return term;
        } else if (other instanceof Term) {
            return ((Term) other).multiply(this);
        } else {
            System.err.println("Multiply return null at Power.java");
            return null;
        }
    }

    @Override
    public Base diff(Character variable) {
        if (variable != alphabetic.charAt(0) || degree == 0) {
            return new Expr();
        }
        Term ans = new Term();
        ans.multiCoefficient(BigInteger.valueOf(degree));
        ans.addFactor(new Power(alphabetic, degree - 1));
        return ans;
    }
}
