package expr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Expr 类：用加号连接 Term 类的表达式。
 */
public class Expr extends Factor implements Base {
    private final ArrayList<Term> terms;
    private int degree = 1;

    public Expr() {
        this.terms = new ArrayList<>();
    }

    /**
     * 向 Expr 中添加一个新的 Term 项，并进行化简。
     * @param term 向 Expr 中添加的新项。
     */
    public void addTerm(Term term) {
        this.terms.add(term);
    }

    private Expr termsSimplify() {
        Iterator<Term> it = terms.iterator();
        Expr ans = new Expr();
        while (it.hasNext()) {
            Base result = it.next().expand();
            if (result instanceof Term) {
                ans.addTerm((Term) result);
            } else {
                Iterator<Term> it2 = ((Expr) result).terms.iterator();
                while (it2.hasNext()) {
                    ans.addTerm(it2.next());
                }
            }
        }
        it = ans.terms.iterator();
        while (it.hasNext()) {
            it.next().simplify();
        }
        return ans;
    }

    public Expr simplify() {
        // 括号展开，Term 化简
        Expr ans = termsSimplify();

        // 同类项合并
        HashMap<String, Term> map = new HashMap<>();
        Iterator<Term> it = ans.terms.iterator();
        while (it.hasNext()) {
            Term temp = (Term) it.next().multiply(new Term());
            String termType = temp.getType();
            if (map.containsKey(termType)) {
                temp.addCoefficient(map.get(termType).getCoefficient());
                map.put(
                        termType,
                        temp
                );
            } else {
                map.put(termType, temp);
            }
        }
        ans.terms.clear();
        map.forEach((String type, Term term) -> {
            if (!term.getCoefficient().equals(BigInteger.ZERO)) {
                ans.terms.add(term);
            }
        });
        return ans;
    }

    @Override
    public Base multiply(Base other) {
        Expr ans = new Expr();
        if (other instanceof Expr) {
            Iterator<Term> it1 = terms.iterator();
            while (it1.hasNext()) {
                Term target = it1.next();
                Iterator<Term> it2 = ((Expr) other).terms.iterator();
                while (it2.hasNext()) {
                    ans.addTerm((Term) target.multiply(it2.next()));
                }
            }
            return ans;
        } else if (other instanceof Factor || other instanceof Term) {
            Iterator<Term> it = terms.iterator();
            while (it.hasNext()) {
                Term target = it.next();
                ans.addTerm((Term) target.multiply(other));
            }
            return ans;
        } else {
            System.out.println("Multiply return null at Expr.java");
            return null;
        }
    }

    @Override
    public String toString() {
        if (terms.size() == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Term> it = terms.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
        }
        return sb.toString();
    }

    public int length() {
        return terms.size();
    }

    @Override
    public String getAlphabetic() {
        return null;
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
        return null;
    }

    @Override
    public Base diff(Character variable) {
        Expr ans = new Expr();
        terms.forEach(t -> {
            Expr b = (Expr) t.diff(variable);
            b.terms.forEach(term -> {
                ans.addTerm(term);
            });
        });
        return ans;
    }

}
