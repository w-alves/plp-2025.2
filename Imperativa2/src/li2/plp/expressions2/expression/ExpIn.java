package li2.plp.expressions2.expression;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;
import li2.plp.expressions2.memory.AmbienteCompilacao;
import li2.plp.expressions2.memory.AmbienteExecucao;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;

public class ExpIn implements Expressao {

    private Expressao expr;
    private Expressao lower;
    private Expressao upper;
    private boolean lowerInclusive;
    private boolean upperInclusive;

    public ExpIn(Expressao expr, Expressao lower, Expressao upper, boolean lowerInclusive, boolean upperInclusive) {
        this.expr = expr;
        this.lower = lower;
        this.upper = upper;
        this.lowerInclusive = lowerInclusive;
        this.upperInclusive = upperInclusive;
    }

    public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        ValorInteiro exprVal = (ValorInteiro) expr.avaliar(amb);

        // Evaluate lower and compare according to inclusivity
        ValorInteiro lowerVal = (ValorInteiro) lower.avaliar(amb);
        int x = exprVal.valor();
        int a = lowerVal.valor();
        boolean lowerOk = lowerInclusive ? (a <= x) : (a < x);
        if (!lowerOk) {
            return new ValorBooleano(false);
        }

        ValorInteiro upperVal = (ValorInteiro) upper.avaliar(amb);
        int b = upperVal.valor();
        boolean upperOk = upperInclusive ? (x <= b) : (x < b);
        return new ValorBooleano(upperOk);
    }

    public boolean checaTipo(AmbienteCompilacao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        boolean result = expr.checaTipo(amb) && lower.checaTipo(amb) && upper.checaTipo(amb);
        if (result) {
            result = expr.getTipo(amb).eInteiro() && lower.getTipo(amb).eInteiro() && upper.getTipo(amb).eInteiro();
        }
        return result;
    }

    public Tipo getTipo(AmbienteCompilacao amb) {
        return TipoPrimitivo.BOOLEANO;
    }

    public Expressao reduzir(AmbienteExecucao ambiente) {
        this.expr = this.expr.reduzir(ambiente);
        this.lower = this.lower.reduzir(ambiente);
        this.upper = this.upper.reduzir(ambiente);
        return this;
    }

    public ExpIn clone() {
        return new ExpIn(expr.clone(), lower.clone(), upper.clone(), lowerInclusive, upperInclusive);
    }
}


