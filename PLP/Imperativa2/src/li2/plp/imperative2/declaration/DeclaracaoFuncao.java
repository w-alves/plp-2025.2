package li2.plp.imperative2.declaration;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.declaration.Declaracao;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

public class DeclaracaoFuncao extends Declaracao {

	private final Id id;
	private final DefFuncao defFuncao;

	public DeclaracaoFuncao(Id id, DefFuncao defFuncao) {
		this.id = id;
		this.defFuncao = defFuncao;
	}

	@Override
	public AmbienteExecucaoImperativa elabora(AmbienteExecucaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException, EntradaVaziaException {
		((AmbienteExecucaoImperativa2) ambiente).mapFuncao(id, defFuncao);
		return ambiente;
	}

	@Override
	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException, EntradaVaziaException {
		boolean resposta;

		ambiente.map(id, defFuncao.getTipo());

		ListaDeclaracaoParametro parametrosFormais = defFuncao.getParametrosFormais();
		if (parametrosFormais.checaTipo(ambiente)) {
			ambiente.incrementa();
			ambiente = parametrosFormais.elabora(ambiente);
			resposta = defFuncao.getComando().checaTipo(ambiente);
			ambiente.restaura();
		} else {
			resposta = false;
		}
		return resposta;
	}
}

