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

		// Normalize expressions before comparison to handle semantic equivalence
		// (e.g., a+b == b+a for pure expressions)
		Expressao aNorm = ExpressaoNormalizer.normalizar(a);
		Expressao bNorm = ExpressaoNormalizer.normalizar(b);

		if (aNorm.getClass() != bNorm.getClass()) {
			return false;
		}

		if (aNorm instanceof ValorConcreto && bNorm instanceof ValorConcreto) {
			return ((ValorConcreto<?>) aNorm).valor().equals(((ValorConcreto<?>) bNorm).valor());
		}
		if (aNorm instanceof Id && bNorm instanceof Id) {
			return ((Id) aNorm).getIdName().equals(((Id) bNorm).getIdName());
		}
		if (aNorm instanceof ExpBinaria && bNorm instanceof ExpBinaria) {
			ExpBinaria ea = (ExpBinaria) aNorm;
			ExpBinaria eb = (ExpBinaria) bNorm;
			return ea.getOperador().equals(eb.getOperador())
					&& estruturaIgual(ea.getEsq(), eb.getEsq())
					&& estruturaIgual(ea.getDir(), eb.getDir());
		}
		if (aNorm instanceof ExpUnaria && bNorm instanceof ExpUnaria) {
			ExpUnaria ea = (ExpUnaria) aNorm;
			ExpUnaria eb = (ExpUnaria) bNorm;
			return ea.getOperador().equals(eb.getOperador())
					&& estruturaIgual(ea.getExp(), eb.getExp());
		}
		if (aNorm instanceof ChamadaFuncao && bNorm instanceof ChamadaFuncao) {
			ChamadaFuncao fa = (ChamadaFuncao) aNorm;
			ChamadaFuncao fb = (ChamadaFuncao) bNorm;
			return estruturaIgual(fa.getNomeFuncao(), fb.getNomeFuncao())
					&& listasIguais(fa.getParametrosReais(), fb.getParametrosReais());
		}
		if (aNorm instanceof ExpIntervaloInclusivo && bNorm instanceof ExpIntervaloInclusivo) {
			ExpIntervaloInclusivo ea = (ExpIntervaloInclusivo) aNorm;
			ExpIntervaloInclusivo eb = (ExpIntervaloInclusivo) bNorm;
			return estruturaIgual(ea.getValor(), eb.getValor())
					&& estruturaIgual(ea.getLimiteInferior(), eb.getLimiteInferior())
					&& estruturaIgual(ea.getLimiteSuperior(), eb.getLimiteSuperior())
					&& ea.isIncluiInferior() == eb.isIncluiInferior()
					&& ea.isIncluiSuperior() == eb.isIncluiSuperior();
		}
		return aNorm.equals(bNorm);
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
