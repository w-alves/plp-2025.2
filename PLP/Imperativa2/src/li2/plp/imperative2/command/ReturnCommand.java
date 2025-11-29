package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative1.memory.ErroTipoEntradaException;

public class ReturnCommand implements Comando {

	private final Expressao expressao;
	private final Tipo tipoRetorno;

	public ReturnCommand(Expressao expressao, Tipo tipoRetorno) {
		this.expressao = expressao;
		this.tipoRetorno = tipoRetorno;
	}

	@Override
	public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException, EntradaVaziaException, ErroTipoEntradaException {
		Valor valor = expressao.avaliar(ambiente);
		throw new FuncaoRetornoException(valor);
	}

	@Override
	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException, EntradaVaziaException {
		if (!expressao.checaTipo(ambiente)) {
			return false;
		}
		Tipo tipoExpressao = expressao.getTipo(ambiente);
		return tipoRetorno.eIgual(tipoExpressao);
	}
}

