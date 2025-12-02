package li2.plp.imperative2.util;

import li2.plp.expressions2.expression.ExpAnd;
import li2.plp.expressions2.expression.ExpBinaria;
import li2.plp.expressions2.expression.ExpEquals;
import li2.plp.expressions2.expression.ExpOr;
import li2.plp.expressions2.expression.ExpSoma;
import li2.plp.expressions2.expression.ExpUnaria;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.imperative2.expression.ExpIntervaloInclusivo;

/**
 * Utility class to normalize (canonicalize) expression trees.
 * 
 * Normalization applies commutativity transformations to create a canonical
 * form for expressions, allowing semantic equivalence detection.
 * 
 * IMPORTANT: Commutativity is only applied to PURE expressions (no side
 * effects).
 * This preserves the evaluation order for expressions with side effects.
 */
public final class ExpressaoNormalizer {

    private ExpressaoNormalizer() {
    }

    /**
     * Normalizes an expression to its canonical form.
     * 
     * @param exp the expression to normalize
     * @return the normalized expression
     */
    public static Expressao normalizar(Expressao exp) {
        if (exp == null) {
            return null;
        }

        // Normalize commutative binary operators
        if (exp instanceof ExpSoma) {
            return normalizarComutativo((ExpSoma) exp);
        }
        if (exp instanceof ExpAnd) {
            return normalizarComutativo((ExpAnd) exp);
        }
        if (exp instanceof ExpOr) {
            return normalizarComutativo((ExpOr) exp);
        }
        if (exp instanceof ExpEquals) {
            return normalizarComutativo((ExpEquals) exp);
        }

        // Normalize other binary operators (recursively normalize children)
        if (exp instanceof ExpBinaria) {
            ExpBinaria binaria = (ExpBinaria) exp;
            // For non-commutative operators, just return as-is
            return binaria;
        }

        // Normalize unary operators
        if (exp instanceof ExpUnaria) {
            ExpUnaria unaria = (ExpUnaria) exp;
            Expressao childNorm = normalizar(unaria.getExp());
            if (childNorm != unaria.getExp()) {
                return unaria.clone();
            }
            return unaria;
        }

        // Normalize interval expressions
        if (exp instanceof ExpIntervaloInclusivo) {
            ExpIntervaloInclusivo intervalo = (ExpIntervaloInclusivo) exp;
            Expressao valorNorm = normalizar(intervalo.getValor());
            Expressao infNorm = normalizar(intervalo.getLimiteInferior());
            Expressao supNorm = normalizar(intervalo.getLimiteSuperior());

            if (valorNorm != intervalo.getValor() || infNorm != intervalo.getLimiteInferior()
                    || supNorm != intervalo.getLimiteSuperior()) {
                return intervalo.clone();
            }
            return intervalo;
        }

        // Default: return as-is
        return exp;
    }

    /**
     * Normalizes a commutative binary expression.
     * Only applies commutativity if both operands are pure.
     */
    private static ExpSoma normalizarComutativo(ExpSoma exp) {
        Expressao esq = normalizar(exp.getEsq());
        Expressao dir = normalizar(exp.getDir());

        // Only apply commutativity if both operands are pure
        if (PurityVerifier.isPure(esq) && PurityVerifier.isPure(dir)) {
            // Sort operands lexicographically by their string representation
            if (esq.toString().compareTo(dir.toString()) > 0) {
                // Swap: create new ExpSoma with swapped operands
                return new ExpSoma(dir, esq);
            }
        }

        // Return with normalized children (even if not swapped)
        if (esq != exp.getEsq() || dir != exp.getDir()) {
            return new ExpSoma(esq, dir);
        }
        return exp;
    }

    private static ExpAnd normalizarComutativo(ExpAnd exp) {
        Expressao esq = normalizar(exp.getEsq());
        Expressao dir = normalizar(exp.getDir());

        if (PurityVerifier.isPure(esq) && PurityVerifier.isPure(dir)) {
            if (esq.toString().compareTo(dir.toString()) > 0) {
                return new ExpAnd(dir, esq);
            }
        }

        if (esq != exp.getEsq() || dir != exp.getDir()) {
            return new ExpAnd(esq, dir);
        }
        return exp;
    }

    private static ExpOr normalizarComutativo(ExpOr exp) {
        Expressao esq = normalizar(exp.getEsq());
        Expressao dir = normalizar(exp.getDir());

        if (PurityVerifier.isPure(esq) && PurityVerifier.isPure(dir)) {
            if (esq.toString().compareTo(dir.toString()) > 0) {
                return new ExpOr(dir, esq);
            }
        }

        if (esq != exp.getEsq() || dir != exp.getDir()) {
            return new ExpOr(esq, dir);
        }
        return exp;
    }

    private static ExpEquals normalizarComutativo(ExpEquals exp) {
        Expressao esq = normalizar(exp.getEsq());
        Expressao dir = normalizar(exp.getDir());

        if (PurityVerifier.isPure(esq) && PurityVerifier.isPure(dir)) {
            if (esq.toString().compareTo(dir.toString()) > 0) {
                return new ExpEquals(dir, esq);
            }
        }

        if (esq != exp.getEsq() || dir != exp.getDir()) {
            return new ExpEquals(esq, dir);
        }
        return exp;
    }
}
