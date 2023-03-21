package expr;

public interface Base {
    Base multiply(Base other);

    Base diff(Character variable);
}
