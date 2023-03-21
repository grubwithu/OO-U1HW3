package expr;

public abstract class Factor implements Base {
    public abstract String getAlphabetic();

    public abstract int getDegree();

    public abstract void setDegree(int degree);

    public abstract Factor changeDegree(int degree);
}
