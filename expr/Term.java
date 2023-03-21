package expr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Term 类：用乘号连接多个因子的表达式。
 */
public class Term implements Base {
    private ArrayList<Base> bases;
    private BigInteger coefficient;

    public Term() {
        this.bases = new ArrayList<>();
        coefficient = BigInteger.ONE;
    }

    public void addFactor(Base base) {
        if (base == null) {
            return;
        }
        if (base instanceof Term) {
            // 防止乘积自身的递归嵌套
            Iterator<Base> it = ((Term) base).bases.iterator();
            multiCoefficient(((Term) base).getCoefficient());
            while (it.hasNext()) {
                bases.add(it.next());
            }
        } else {
            bases.add(base);
        }
    }

    public void multiCoefficient(BigInteger number) {
        this.coefficient = this.coefficient.multiply(number);
    }

    public void addCoefficient(BigInteger coe) {
        this.coefficient = this.coefficient.add(coe);
    }

    public BigInteger getCoefficient() {
        return this.coefficient;
    }

    public Base expand() {
        if (!brackets()) {
            return this;
        }
        Base ans = bases.get(0);
        if (ans instanceof Expr) {
            ans = ((Expr) ans).simplify();
        } else if (ans instanceof Unexpanded) {
            ((Unexpanded) ans).innerSimplify();
        }
        for (int i = 1; i < bases.size(); i++) {
            Base temp = bases.get(i);
            if (temp instanceof Expr) {
                temp = ((Expr) temp).simplify();
            } else if (temp instanceof Unexpanded) {
                ((Unexpanded) temp).innerSimplify();
            }
            ans = ans.multiply(temp);
        }
        Term temp = new Term();
        temp.coefficient = this.coefficient;
        return ans.multiply(temp);
    }

    private boolean mayEmbed(Base factor) {
        return factor instanceof Expr ||
                factor instanceof Cos ||
                factor instanceof Sin;
    }

    public void simplify() {
        if (bases.size() < 1) {
            return;
        }
        HashMap<String, Integer> degreeMap = new HashMap<>();
        HashMap<String, Factor> factorMap = new HashMap<>();
        Iterator<Base> it = bases.iterator();
        while (it.hasNext()) {
            Base temp = it.next();
            Factor factor = (Factor) temp;
            if (degreeMap.containsKey(factor.getAlphabetic())) {
                degreeMap.put(
                        factor.getAlphabetic(),
                        degreeMap.get(factor.getAlphabetic()) + factor.getDegree()
                );
            } else {
                degreeMap.put(factor.getAlphabetic(), factor.getDegree());
                factorMap.put(factor.getAlphabetic(), factor);
            }
        }

        bases.clear();
        degreeMap.forEach((String c, Integer i) -> {
            Factor f = factorMap.get(c);
            if (f.getDegree() != 0) {
                bases.add(f.changeDegree(i));
            }
        });
    }

    private Boolean brackets() {
        Iterator<Base> it  = bases.iterator();
        while (it.hasNext()) {
            if (mayEmbed(it.next())) {
                return true;
            }
        }
        return false;
    }

    public String getType() {
        StringBuilder sb = new StringBuilder();
        bases.sort((f1, f2) -> {
            return ((Factor) f1).getAlphabetic().compareTo(((Factor) f2).getAlphabetic());
        });
        bases.forEach(f -> {
            sb.append(((Factor) f).getAlphabetic());
            sb.append(((Factor) f).getDegree());
        });
        return sb.toString();
    }

    @Override
    public Base multiply(Base other) {
        if (other instanceof Term) {
            Term ans = new Term();
            ans.coefficient = coefficient.multiply(((Term) other).coefficient);
            ans.bases.addAll(this.bases);
            ans.bases.addAll(((Term) other).bases);
            return ans;
        } else if (other instanceof Expr) {
            return ((Expr) other).multiply(this);
        } else if (other instanceof Factor) {
            Term ans = new Term();
            ans.coefficient = this.coefficient;
            ans.bases.addAll(this.bases);
            ans.bases.add(other);
            return ans;
        } else {
            System.out.println("Multiply return null at Term.java");
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (bases.size() == 0) {
            // 常数项单独处理
            if (coefficient.compareTo(BigInteger.ZERO) > 0) {
                sb.append('+');
            }
            sb.append(coefficient);
            return sb.toString();
        }

        // 非常数项
        if (coefficient.compareTo(BigInteger.ZERO) > 0) {
            sb.append('+');
        }
        Boolean startMulti = true;
        if (coefficient.compareTo(BigInteger.valueOf(-1)) == 0) {
            sb.append('-');
            startMulti = false;
        } else if (coefficient.compareTo(BigInteger.ONE) != 0) {
            sb.append(coefficient);
        } else {
            startMulti = false;
        }

        Iterator<Base> it = bases.iterator();
        while (it.hasNext()) {
            if (startMulti) {
                sb.append('*');
            }
            Base base = it.next();
            if (base instanceof Expr) {
                sb.append('(');
            }
            sb.append(base.toString());
            if (base instanceof Expr) {
                sb.append(')');
            }
            startMulti = true;
        }
        return sb.toString();
    }

    public void addFactors(Term term) {
        bases.addAll(term.bases);
    }

    @Override
    public Base diff(Character variable) {
        Expr ans = new Expr();
        for (int i = 0; i < bases.size(); i++) {
            Term term = new Term();
            term.multiCoefficient(coefficient);
            term.addFactor(bases.get(i).diff(variable));
            for (int j = 0; j < bases.size(); j++) {
                if (j != i) {
                    term.addFactor(bases.get(j));
                }
            }
            ans.addTerm(term);
        }
        return ans;
    }

}
