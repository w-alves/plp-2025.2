package li2.plp.imperative2.util;

import java.util.ArrayList;
import java.util.List;

import li2.plp.expressions2.expression.ExpBinaria;
import li2.plp.expressions2.expression.ExpUnaria;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.ValorConcreto;
import li2.plp.imperative1.util.Lista;
import li2.plp.imperative2.command.ListaExpressao;
import li2.plp.imperative2.expression.ChamadaFuncao;
import li2.plp.imperative2.expression.ExpIntervaloInclusivo;

public final class ExpressaoComparator {

	private ExpressaoComparator() {
	}

	public static boolean estruturaIgual(Expressao a, Expressao b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if (a.getClass() != b.getClass()) {
			return false;
		}

		if (a instanceof ValorConcreto && b instanceof ValorConcreto) {
			return ((ValorConcreto<?>) a).valor().equals(((ValorConcreto<?>) b).valor());
		}
		if (a instanceof Id && b instanceof Id) {
			return ((Id) a).getIdName().equals(((Id) b).getIdName());
		}
		if (a instanceof ExpBinaria && b instanceof ExpBinaria) {
			ExpBinaria ea = (ExpBinaria) a;
			ExpBinaria eb = (ExpBinaria) b;
			return ea.getOperador().equals(eb.getOperador())
					&& estruturaIgual(ea.getEsq(), eb.getEsq())
					&& estruturaIgual(ea.getDir(), eb.getDir());
		}
		if (a instanceof ExpUnaria && b instanceof ExpUnaria) {
			ExpUnaria ea = (ExpUnaria) a;
			ExpUnaria eb = (ExpUnaria) b;
			return ea.getOperador().equals(eb.getOperador())
					&& estruturaIgual(ea.getExp(), eb.getExp());
		}
		if (a instanceof ChamadaFuncao && b instanceof ChamadaFuncao) {
			ChamadaFuncao fa = (ChamadaFuncao) a;
			ChamadaFuncao fb = (ChamadaFuncao) b;
			return estruturaIgual(fa.getNomeFuncao(), fb.getNomeFuncao())
					&& listasIguais(fa.getParametrosReais(), fb.getParametrosReais());
		}
		if (a instanceof ExpIntervaloInclusivo && b instanceof ExpIntervaloInclusivo) {
			ExpIntervaloInclusivo ea = (ExpIntervaloInclusivo) a;
			ExpIntervaloInclusivo eb = (ExpIntervaloInclusivo) b;
			return estruturaIgual(ea.getValor(), eb.getValor())
					&& estruturaIgual(ea.getLimiteInferior(), eb.getLimiteInferior())
					&& estruturaIgual(ea.getLimiteSuperior(), eb.getLimiteSuperior())
					&& ea.isIncluiInferior() == eb.isIncluiInferior()
					&& ea.isIncluiSuperior() == eb.isIncluiSuperior();
		}
		return a.equals(b);
	}

	private static boolean listasIguais(ListaExpressao listaA, ListaExpressao listaB) {
		List<Expressao> elementosA = listaParaListaJava(listaA);
		List<Expressao> elementosB = listaParaListaJava(listaB);
		if (elementosA.size() != elementosB.size()) {
			return false;
		}
		for (int i = 0; i < elementosA.size(); i++) {
			if (!estruturaIgual(elementosA.get(i), elementosB.get(i))) {
				return false;
			}
		}
		return true;
	}

	private static List<Expressao> listaParaListaJava(ListaExpressao lista) {
		List<Expressao> resultado = new ArrayList<Expressao>();
		Lista<Expressao> atual = lista;
		while (atual != null && atual.getHead() != null) {
			resultado.add(atual.getHead());
			Lista<Expressao> proximo = atual.getTail();
			if (proximo == null) {
				break;
			}
			if (proximo instanceof ListaExpressao) {
				atual = (ListaExpressao) proximo;
			} else {
				break;
			}
		}
		return resultado;
	}
}

