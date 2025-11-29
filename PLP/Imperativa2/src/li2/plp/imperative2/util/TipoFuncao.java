package li2.plp.imperative2.util;

import static li2.plp.expressions1.util.ToStringProvider.listToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import li2.plp.expressions1.util.Tipo;

public class TipoFuncao implements Tipo {

	private final List<Tipo> tiposParametros = new ArrayList<Tipo>();
	private final Tipo tipoRetorno;

	public TipoFuncao(List<Tipo> tiposParametros, Tipo tipoRetorno) {
		if (tiposParametros != null) {
			this.tiposParametros.addAll(tiposParametros);
		}
		this.tipoRetorno = tipoRetorno;
	}

	public List<Tipo> getTiposParametros() {
		return Collections.unmodifiableList(tiposParametros);
	}

	public Tipo getTipoRetorno() {
		return tipoRetorno;
	}

	public boolean aceitaArgumentos(List<Tipo> tiposReais) {
		if (tiposReais.size() != tiposParametros.size()) {
			return false;
		}
		for (int i = 0; i < tiposParametros.size(); i++) {
			if (!tiposParametros.get(i).eIgual(tiposReais.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean eBooleano() {
		return false;
	}

	@Override
	public boolean eIgual(Tipo tipo) {
		if (tipo instanceof TipoFuncao) {
			TipoFuncao outro = (TipoFuncao) tipo;
			return outro.tiposParametros.equals(this.tiposParametros)
					&& outro.tipoRetorno.eIgual(this.tipoRetorno);
		}
		return tipo.eIgual(this);
	}

	@Override
	public boolean eInteiro() {
		return false;
	}

	@Override
	public boolean eString() {
		return false;
	}

	@Override
	public boolean eValido() {
		boolean valido = tipoRetorno != null && tipoRetorno.eValido();
		for (Tipo tipo : tiposParametros) {
			valido &= tipo.eValido();
		}
		return valido;
	}

	@Override
	public String getNome() {
		return "func" + listToString(tiposParametros, "(", ")", ",") + ":" + tipoRetorno.getNome();
	}

	@Override
	public Tipo intersecao(Tipo outroTipo) {
		if (outroTipo.eIgual(this)) {
			return this;
		}
		return null;
	}
}

