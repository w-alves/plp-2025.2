package li2.plp.imperative2.expression;

import java.util.List;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.AmbienteCompilacao;
import li2.plp.expressions2.memory.AmbienteExecucao;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative1.memory.ErroTipoEntradaException;
import li2.plp.imperative1.memory.ListaValor;
import li2.plp.imperative2.command.FuncaoRetornoException;
import li2.plp.imperative2.command.FuncaoSemRetornoException;
import li2.plp.imperative2.command.ListaExpressao;
import li2.plp.imperative2.declaration.DefFuncao;
import li2.plp.imperative2.declaration.ListaDeclaracaoParametro;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.memory.FuncaoNaoDeclaradaException;
import li2.plp.imperative2.util.TipoFuncao;

public class ChamadaFuncao implements Expressao {

	private final Id nomeFuncao;
	private final ListaExpressao parametrosReais;

	public ChamadaFuncao(Id nomeFuncao, ListaExpressao parametrosReais) {
		this.nomeFuncao = nomeFuncao;
		this.parametrosReais = parametrosReais == null ? new ListaExpressao() : parametrosReais;
	}

	@Override
	public Valor avaliar(AmbienteExecucao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		AmbienteExecucaoImperativa2 amb = (AmbienteExecucaoImperativa2) ambiente;
		DefFuncao funcao = obterFuncao(amb);
		amb.incrementa();
		try {
			bindParameters(amb, funcao.getParametrosFormais());
			try {
				funcao.getComando().executar(amb);
			} catch (FuncaoRetornoException retorno) {
				return retorno.getValor();
			} catch (EntradaVaziaException | ErroTipoEntradaException e) {
				throw new RuntimeException(e);
			} catch (li2.plp.expressions2.memory.IdentificadorJaDeclaradoException e) {
				throw new VariavelJaDeclaradaException(nomeFuncao);
			} catch (li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException e) {
				throw new VariavelNaoDeclaradaException(nomeFuncao);
			}
			throw new FuncaoSemRetornoException(nomeFuncao);
		} finally {
			amb.restaura();
		}
	}

	private DefFuncao obterFuncao(AmbienteExecucaoImperativa2 ambiente) throws VariavelNaoDeclaradaException {
		try {
			return ambiente.getFuncao(nomeFuncao);
		} catch (FuncaoNaoDeclaradaException e) {
			throw new VariavelNaoDeclaradaException(nomeFuncao);
		}
	}

	private AmbienteExecucaoImperativa2 bindParameters(AmbienteExecucaoImperativa2 ambiente,
			ListaDeclaracaoParametro parametrosFormais)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		ListaValor listaValor = parametrosReais.avaliar(ambiente);
		ListaDeclaracaoParametro parametros = parametrosFormais;
		while (listaValor.length() > 0 && parametros.getHead() != null) {
			ambiente.map(parametros.getHead().getId(), listaValor.getHead());
			parametros = (ListaDeclaracaoParametro) parametros.getTail();
			listaValor = (ListaValor) listaValor.getTail();
		}
		return ambiente;
	}

	@Override
	public boolean checaTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		AmbienteCompilacaoImperativa ambienteImperativa = (AmbienteCompilacaoImperativa) ambiente;
		Tipo tipo = ambienteImperativa.get(nomeFuncao);
		if (!(tipo instanceof TipoFuncao)) {
			return false;
		}
		TipoFuncao tipoFuncao = (TipoFuncao) tipo;
		List<Tipo> tiposParametros = parametrosReais.getTipos(ambienteImperativa);
		return tipoFuncao.aceitaArgumentos(tiposParametros);
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Tipo tipo = ambiente.get(nomeFuncao);
		if (tipo instanceof TipoFuncao) {
			return ((TipoFuncao) tipo).getTipoRetorno();
		}
		return tipo;
	}

	@Override
	public Expressao reduzir(AmbienteExecucao ambiente) {
		return this;
	}

	@Override
	public Expressao clone() {
		return new ChamadaFuncao(nomeFuncao, parametrosReais);
	}

	public Id getNomeFuncao() {
		return nomeFuncao;
	}

	public ListaExpressao getParametrosReais() {
		return parametrosReais;
	}
}

