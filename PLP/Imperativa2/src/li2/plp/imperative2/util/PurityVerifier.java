package li2.plp.imperative2.util;

import li2.plp.expressions2.expression.ExpBinaria;
import li2.plp.expressions2.expression.ExpUnaria;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.ValorConcreto;
import li2.plp.imperative2.expression.ChamadaFuncao;
import li2.plp.imperative2.expression.ExpIntervaloInclusivo;

/**
 * Utility class to verify if an expression is pure (side-effect free).
 * 
 * A pure expression is one that:
 * - Does not contain function calls (conservative approach)
 * - Only consists of literals, variables, and operators
 * 
 * This is used to determine if commutativity transformations are safe.
 */
public final class PurityVerifier {

    private PurityVerifier() {
    }

    /**
     * Checks if an expression is pure (side-effect free).
     * 
     * @param exp the expression to check
     * @return true if the expression is pure, false otherwise
     */
    public static boolean isPure(Expressao exp) {
        if (exp == null) {
            return true;
        }

        // Literals and variables are pure
        if (exp instanceof ValorConcreto || exp instanceof Id) {
            return true;
        }

        // Function calls are impure (conservative approach)
        if (exp instanceof ChamadaFuncao) {
            return false;
        }

        // Binary expressions are pure if both operands are pure
        if (exp instanceof ExpBinaria) {
            ExpBinaria binaria = (ExpBinaria) exp;
            return isPure(binaria.getEsq()) && isPure(binaria.getDir());
        }

        // Unary expressions are pure if the operand is pure
        if (exp instanceof ExpUnaria) {
            ExpUnaria unaria = (ExpUnaria) exp;
            return isPure(unaria.getExp());
        }

        // Interval expressions are pure if all sub-expressions are pure
        if (exp instanceof ExpIntervaloInclusivo) {
            ExpIntervaloInclusivo intervalo = (ExpIntervaloInclusivo) exp;
            return isPure(intervalo.getValor())
                    && isPure(intervalo.getLimiteInferior())
                    && isPure(intervalo.getLimiteSuperior());
        }

        // Default: assume impure for unknown expression types
        return false;
    }
}
