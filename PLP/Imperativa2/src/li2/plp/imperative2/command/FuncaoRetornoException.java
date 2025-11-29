package li2.plp.imperative2.command;

import li2.plp.expressions2.expression.Valor;

public class FuncaoRetornoException extends RuntimeException {

	private static final long serialVersionUID = -7081925834391989833L;

	private final Valor valor;

	public FuncaoRetornoException(Valor valor) {
		super("Retorno de funcao");
		this.valor = valor;
	}

	public Valor getValor() {
		return valor;
	}
}

