package li2.plp.expressions2.expression;

public class IntervaloSpec {

    private final Expressao lower;
    private final Expressao upper;
    private final boolean lowerInclusive;
    private final boolean upperInclusive;

    public IntervaloSpec(Expressao lower, Expressao upper, boolean lowerInclusive, boolean upperInclusive) {
        this.lower = lower;
        this.upper = upper;
        this.lowerInclusive = lowerInclusive;
        this.upperInclusive = upperInclusive;
    }

    public Expressao getLower() {
        return lower;
    }

    public Expressao getUpper() {
        return upper;
    }

    public boolean isLowerInclusive() {
        return lowerInclusive;
    }

    public boolean isUpperInclusive() {
        return upperInclusive;
    }
}


