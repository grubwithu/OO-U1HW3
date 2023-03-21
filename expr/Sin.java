package expr;

import java.math.BigInteger;

public class Sin extends Factor implements Base, Unexpanded {
    private Factor inner;

    private int degree;

    public Sin(Factor inner) {
        this.inner = inner;
        this.degree = 1;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    @Override
    public Factor changeDegree(int degree) {
        return new Sin(inner, degree);
    }

    public Sin(Factor inner, int degree) {
        this.inner = inner;
        this.degree = degree;
    }

    @Override
    public Base multiply(Base other) {
        if (other instanceof Expr) {
            return other.multiply(this);
        } else if (other instanceof Factor) {
            Term ans = new Term();
            ans.addFactor(this);
            ans.addFactor(other);
            return ans;
        } else if (other instanceof Term) {
            return other.multiply(this);
        } else {
            System.out.println("Multiply return null at Sin.java");
            return null;
        }
    }

    @Override
    public String getAlphabetic() {
        return "s(" + inner.toString() + ")";
    }

    @Override
    public int getDegree() {
        return degree;
    }

    @Override
    public String toString() {
        String ans = "sin((";
        ans = ans + inner.toString() + "))";
        if (degree != 1) {
            ans += "**" + String.valueOf(degree);
        }
        return ans;
    }

    @Override
    public void innerSimplify() {
        if (inner instanceof Expr) {
            inner = ((Expr) inner).simplify();
        }
    }

    @Override
    public Base diff(Character variable) {
        Term ans = new Term();
        // 外层幂函数
        ans.multiCoefficient(BigInteger.valueOf(degree));
        ans.addFactor(new Sin(inner, degree - 1));
        // 内层三角函数
        ans.addFactor(new Cos(inner));
        ans.addFactor(inner.diff(variable));
        return ans;
    }

}
