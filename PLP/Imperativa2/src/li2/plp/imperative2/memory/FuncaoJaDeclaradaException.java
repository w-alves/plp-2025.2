package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;

public class FuncaoJaDeclaradaException extends IdentificadorJaDeclaradoException {

	private static final long serialVersionUID = -1576923184920839836L;

	public FuncaoJaDeclaradaException(Id id) {
		super("Funcao " + id + " ja declarada.");
	}
}

