package li2.plp.imperative2.declaration;

import java.util.List;

import li2.plp.expressions1.util.Tipo;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative2.util.TipoFuncao;

public class DefFuncao {

	private final ListaDeclaracaoParametro parametrosFormais;
	private final Comando comando;
	private final Tipo tipoRetorno;

	public DefFuncao(ListaDeclaracaoParametro parametrosFormais, Comando comando, Tipo tipoRetorno) {
		this.parametrosFormais = parametrosFormais == null ? new ListaDeclaracaoParametro() : parametrosFormais;
		this.comando = comando;
		this.tipoRetorno = tipoRetorno;
	}

	public ListaDeclaracaoParametro getParametrosFormais() {
		return parametrosFormais;
	}

	public Comando getComando() {
		return comando;
	}

	public Tipo getTipoRetorno() {
		return tipoRetorno;
	}

	public Tipo getTipo() {
		List<Tipo> tipoParametros = parametrosFormais.getTipos();
		return new TipoFuncao(tipoParametros, tipoRetorno);
	}
}

