package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;

public class FuncaoNaoDeclaradaException extends IdentificadorNaoDeclaradoException {

	private static final long serialVersionUID = 5366369729250547633L;

	public FuncaoNaoDeclaradaException(Id id) {
		super("Funcao " + id + " nao declarada.");
	}
}

