package li2.plp.expressions2.expression;

public final class StructuralEquality {
    private StructuralEquality() {}

    public static boolean sameSyntax(Expressao a, Expressao b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        // Conservative default for now; can be expanded to deep checks later
        if (!a.getClass().equals(b.getClass())) return false;
        // fallback: rely on equals if implemented meaningfully for some nodes
        return a.equals(b) || a.toString().equals(b.toString());
    }
}


