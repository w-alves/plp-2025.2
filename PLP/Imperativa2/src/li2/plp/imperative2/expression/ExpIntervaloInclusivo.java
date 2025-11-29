package li2.plp.imperative2.expression;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorBooleano;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.expressions2.memory.AmbienteCompilacao;
import li2.plp.expressions2.memory.AmbienteExecucao;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative2.util.ExpressaoComparator;

public class ExpIntervaloInclusivo implements Expressao {

	private Expressao valor;
	private Expressao limiteInferior;
	private Expressao limiteSuperior;
	private final boolean incluiInferior;
	private final boolean incluiSuperior;

	public ExpIntervaloInclusivo(Expressao valor, Expressao limiteInferior, Expressao limiteSuperior,
			boolean incluiInferior, boolean incluiSuperior) {
		this.valor = valor;
		this.limiteInferior = limiteInferior;
		this.limiteSuperior = limiteSuperior;
		this.incluiInferior = incluiInferior;
		this.incluiSuperior = incluiSuperior;
	}

	@Override
	public Valor avaliar(AmbienteExecucao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Valor valorPrincipal = valor.avaliar(ambiente);
		int numeroPrincipal = extrairInteiro(valorPrincipal, valor);

		Valor valorInferior = obterLimiteComMemo(limiteInferior, valorPrincipal, ambiente);
		int numeroInferior = extrairInteiro(valorInferior, limiteInferior);

		boolean inferiorOk = incluiInferior ? numeroInferior <= numeroPrincipal : numeroInferior < numeroPrincipal;
		if (!inferiorOk) {
			return new ValorBooleano(false);
		}

		Valor valorSuperior = obterLimiteComMemo(limiteSuperior, valorPrincipal, ambiente);
		int numeroSuperior = extrairInteiro(valorSuperior, limiteSuperior);
		boolean superiorOk = incluiSuperior ? numeroPrincipal <= numeroSuperior : numeroPrincipal < numeroSuperior;
		return new ValorBooleano(superiorOk);
	}

	private Valor obterLimiteComMemo(Expressao limite, Valor valorMemoizado, AmbienteExecucao ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		if (ExpressaoComparator.estruturaIgual(valor, limite)) {
			return valorMemoizado;
		}
		return limite.avaliar(ambiente);
	}

	private int extrairInteiro(Valor valor, Expressao origem) {
		if (!(valor instanceof ValorInteiro)) {
			throw new RuntimeException("Expressao " + origem + " deve avaliar para inteiro no operador in.");
		}
		return ((ValorInteiro) valor).valor();
	}

	@Override
	public boolean checaTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		boolean tiposOk = valor.checaTipo(ambiente) && limiteInferior.checaTipo(ambiente)
				&& limiteSuperior.checaTipo(ambiente);
		if (!tiposOk) {
			return false;
		}
		Tipo tipoValor = valor.getTipo(ambiente);
		Tipo tipoInferior = limiteInferior.getTipo(ambiente);
		Tipo tipoSuperior = limiteSuperior.getTipo(ambiente);
		return tipoValor.eIgual(TipoPrimitivo.INTEIRO) && tipoInferior.eIgual(TipoPrimitivo.INTEIRO)
				&& tipoSuperior.eIgual(TipoPrimitivo.INTEIRO);
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		return TipoPrimitivo.BOOLEANO;
	}

	@Override
	public Expressao reduzir(AmbienteExecucao ambiente) {
		valor = valor.reduzir(ambiente);
		limiteInferior = limiteInferior.reduzir(ambiente);
		limiteSuperior = limiteSuperior.reduzir(ambiente);
		return this;
	}

	@Override
	public Expressao clone() {
		return new ExpIntervaloInclusivo(valor.clone(), limiteInferior.clone(), limiteSuperior.clone(),
				incluiInferior, incluiSuperior);
	}

	public Expressao getValor() {
		return valor;
	}

	public Expressao getLimiteInferior() {
		return limiteInferior;
	}

	public Expressao getLimiteSuperior() {
		return limiteSuperior;
	}

	public boolean isIncluiInferior() {
		return incluiInferior;
	}

	public boolean isIncluiSuperior() {
		return incluiSuperior;
	}
}

