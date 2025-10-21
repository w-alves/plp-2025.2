# Implementação do Construtor `between` com Intervalos Inclusivos, Exclusivos e Passo na LI2

## 1. Título do Projeto

**Extensão da LI2 com o Construtor de Comparação Intervalar `between` (com suporte a limites inclusivos/exclusivos e passo opcional)**

---

## 2. Objetivo

Estender a **Linguagem Imperativa 2 (LI2)** com uma nova forma sintática para expressar comparações intervalares de forma **clara, expressiva e eficiente**, inspirada em linguagens como SQL e notações matemáticas de intervalo.

O novo construtor `between` permitirá expressar condições de pertencimento a intervalos numéricos, com suporte a:

* Limites **inclusivos** e/ou **exclusivos** (ex: `[0..10)`);
* **Expressões arbitrárias**, incluindo chamadas de funções com *side effects*;
* Um **passo (`step`)** opcional, indicando granularidade de validação numérica.

---

## 3. Linguagem-Base

Linguagem Imperativa 2 (LI2).

---

## 4. Motivação e Referências

Em SQL, a expressão `x BETWEEN a AND b` é equivalente a `(x >= a AND x <= b)` — uma forma legível e direta de representar uma faixa de valores.

Em notação matemática, é comum representar intervalos com colchetes e parênteses, indicando se os limites são inclusivos (`[ ]`) ou exclusivos (`( )`), como `[0,10)`.

A proposta combina essas duas ideias, trazendo para a LI2 uma **forma unificada e legível** de expressar comparações intervalares, **mantendo semântica rigorosa de avaliação única** (mesmo em expressões com *side effects*).

---

## 5. Especificação da Nova Sintaxe

### A. Forma Geral

```li2
<expressao> between <intervalo> [step <expressao>]
```

### B. Definição de Intervalo

```li2
<intervalo> ::= '[' <expressao> '..' <expressao> ']' 
              | '[' <expressao> '..' <expressao> ')' 
              | '(' <expressao> '..' <expressao> ']' 
              | '(' <expressao> '..' <expressao> ')'
```

### C. Exemplo de Uso

```li2
if (x between [0..10]) {
    write("x está entre 0 e 10 (inclusive)");
}

if (y between (0..10)) {
    write("y está entre 0 e 10 (exclusive)");
}

if (z between [0..10) step 2) {
    write("z está no intervalo [0,10) com passo 2");
}
```

---

## 6. Semântica Interna

A expressão `x between [a..b]` será traduzida internamente para:

| Notação            | Equivalência Interna    | Inclusividade                          |
| :----------------- | :---------------------- | :------------------------------------- |
| `x between [a..b]` | `(x >= a) and (x <= b)` | Ambos inclusivos                       |
| `x between [a..b)` | `(x >= a) and (x < b)`  | Inferior inclusivo, superior exclusivo |
| `x between (a..b]` | `(x > a) and (x <= b)`  | Inferior exclusivo, superior inclusivo |
| `x between (a..b)` | `(x > a) and (x < b)`   | Ambos exclusivos                       |

Quando usado com **`step`**, a expressão adiciona a condição adicional:

```
((x - a) % step == 0)
```

---

## 7. Avaliação e *Side Effects*

Assim como na comparação encadeada, **todas as expressões devem ser avaliadas exatamente uma vez** — incluindo chamadas de função ou procedimentos com efeitos colaterais.

### Exemplo Crítico:

```li2
{
    var count = 0;

    proc side_effect_func() {
        count := count + 1;
        return 5;
    }

    if (side_effect_func() between [0..10)) {
        write("Sucesso");
    } else {
        write("Falha");
    }

    write(count); // Deve imprimir 1
}
```

**Resultado Esperado:**
A função `side_effect_func` é chamada **uma única vez**, mesmo que seu valor participe de duas comparações.

---

## 8. Impacto Sintático e Semântico

### A. Léxico

* Adição das palavras reservadas:
  `between`, `step`
* Reconhecimento dos símbolos:
  `'['`, `']'`, `'('`, `')'`, `'..'`

### B. Gramática (BNF Simplificada)

```bnf
<comparacao> ::= <expressao> 'between' <intervalo> [ 'step' <expressao> ]

<intervalo> ::= '[' <expressao> '..' <expressao> ']' 
              | '[' <expressao> '..' <expressao> ')' 
              | '(' <expressao> '..' <expressao> ']' 
              | '(' <expressao> '..' <expressao> ')'
```

### C. Semântica de Execução

* Avaliar `Exp`, `Low`, `High` (e `Step`, se presente) **exatamente uma vez**.
* Converter a forma `between` para expressões equivalentes de comparação lógica.
* Implementar curto-circuito (`AND` lógico) na verificação do intervalo.
* Aplicar a condição de passo apenas se o operador `step` for especificado.

---

## 9. Critérios de Aceitação

O projeto será considerado **concluído com sucesso** se:

1. **Expressões `between`** com todos os tipos de intervalos inclusivos/exclusivos forem aceitas e traduzidas corretamente.
2. **Funções com efeitos colaterais** dentro das expressões forem avaliadas apenas uma vez.
3. O **operador `step`** for corretamente aplicado e respeitar a semântica de curto-circuito.
4. A nova sintaxe se integrar sem conflitos com as demais construções da LI2 (`if`, `while`, expressões compostas etc.).

---

## 10. Exemplo Completo de Teste

```li2
{
    var count = 0;

    proc get_val() {
        count := count + 1;
        return 5;
    }

    if (get_val() between [0..10) step 2) {
        write("No intervalo com passo 2");
    }

    write(count); // Deve imprimir 1
}
```

**Resultado Esperado:**

```
No intervalo com passo 2
1
```

---

## 11. Extensões Futuras

* Suporte a intervalos não numéricos (ex: caracteres, datas).
* Otimização da análise semântica via *short-circuit code generation*.
* Integração com expressões compostas: `if ((x between [0..10]) or (y between (20..30)))`.

