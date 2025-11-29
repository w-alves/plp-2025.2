package li2.plp.imperative2.command;

import li2.plp.expressions2.expression.Id;

public class FuncaoSemRetornoException extends RuntimeException {

	private static final long serialVersionUID = -7995442853596245923L;

	public FuncaoSemRetornoException(Id id) {
		super("Funcao " + id + " terminou sem executar comando return.");
	}
}

