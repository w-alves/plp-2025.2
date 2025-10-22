## 1\. Título do Projeto

**Implementação do Operador de Intervalo 'in' na LI2 (e.g., `Exp in [A..B)`)**

## 2\. Objetivo

Estender a **Linguagem Imperativa 2 (LI2)** para suportar um **operador de verificação de intervalo (`in`)**. O principal desafio é garantir a **avaliação única** de expressões com **efeitos colaterais (side effects)**, especialmente em casos tautológicos (e.g., `f() in [f()..B)`), onde a expressão (`f()`) deve ser avaliada rigorosamente uma única vez e seu resultado reutilizado.

## 3\. Linguagem-Base

Linguagem Imperativa 2 (LI2).

-----

## 4\. Definição da Nova Sintaxe: Operador 'in'

A nova feature introduz o operador `in` para verificação de intervalo, substituindo a sintaxe de comparação encadeada. A sintaxe permite definir intervalos abertos ou fechados em ambas as extremidades, com a seguinte semântica:

| Sintaxe LI2 | Equivalência Lógica | Definição (Limite A) | Definição (Limite B) |
| :--- | :--- | :--- | :--- |
| `Exp in (A..B)` | $(A < \text{Exp}) \text{ AND } (\text{Exp} < B)$ | `(` Exclusivo | `)` Exclusivo |
| `Exp in (A..B]` | $(A < \text{Exp}) \text{ AND } (\text{Exp} \le B)$ | `(` Exclusivo | `]` Inclusivo |
| `Exp in [A..B)` | $(A \le \text{Exp}) \text{ AND } (\text{Exp} < B)$ | `[` Inclusivo | `)` Exclusivo |
| `Exp in [A..B]` | $(A \le \text{Exp}) \text{ AND } (\text{Exp} \le B)$ | `[` Inclusivo | `]` Inclusivo |

O desafio central é que `Exp`, `A` e `B` podem ser chamadas de procedimentos com efeitos colaterais, e a semântica de avaliação deve ser rigorosa.


-----

## 5\. Escopo Técnico e Implementação

O projeto exigirá modificações no analisador sintático e na fase de avaliação semântica para lidar com a nova sintaxe e suas regras de avaliação.

### A. Análise Sintática

  * **Expansão da Gramática (BNF):** Modificar a BNF para incluir a produção `Expressao "in" Intervalo`. A precedência deste operador deve ser similar à dos operadores relacionais (`<`, `>`, `==`).

    ```bnf
    ExpBinaria ::= Expressao "+" Expressao
               | Expressao "-" Expressao
               | Expressao "and" Expressao
               | Expressao "or" Expressao
               | Expressao "==" Expressao
               | Expressao "++" Expressao
               | Expressao "in" Intervalo

    Intervalo ::= DelimitadorEsq Expressao ".." Expressao DelimitadorDir

    DelimitadorEsq ::= "(" | "["
    DelimitadorDir ::= ")" | "]"
    ```

### B. Análise Semântica 

A `ExpBinaria` da forma `Expressao "in" Intervalo` deve ser transformada em uma lógica AND (duas comparações). O desafio central é a ordem e a memoização (armazenamento) da avaliação para garantir a corretude com *side effects*.

O interpretador deve implementar o seguinte mecanismo:

1.  Identificar as três sub-expressões envolvidas:
    * A `Expressao` à esquerda do "in" (o valor a ser testado).
    * A primeira `Expressao` dentro da definição de `Intervalo` (o limite inferior).
    * A segunda `Expressao` dentro da definição de `Intervalo` (o limite superior).
2.  Avaliar a `Expressao` à esquerda do "in" e todos os seus *side effects* rigorosamente uma única vez e armazenar seu resultado (vamos chamá-lo `val_exp`).
3.  **Consultar o Resultado de `val_exp`:** Se a `Expressao` do limite inferior (de dentro do `Intervalo`) for sintaticamente idêntica à `Expressao` à esquerda do "in" (e.g., a mesma `ChamadaProcedimento` `call f()`), o interpretador não deve re-executá-la. Ele deve, em vez disso, usar o valor já armazenado (`val_exp`) como o valor do limite inferior (`val_bound_a`). O mesmo se aplica à `Expressao` do limite superior (`val_bound_b`).
4.  Avaliar as expressões de limite inferior e superior (apenas se não forem sintaticamente idênticas à `Expressao` principal e, portanto, não "consultadas" no passo 3) e armazenar seus resultados.
5.  Executar as duas comparações lógicas (cuja semântica exata depende dos `DelimEsq` e `DelimDir` do `Intervalo`), respeitando o curto-circuito do AND. (Por exemplo, para `[A..B)`, a lógica seria `(val_bound_a <= val_exp) AND (val_exp < val_bound_b)`).

Esta abordagem garante que uma `ExpBinaria` como `call f() in [call f()..10)` execute a `ChamadaProcedimento` `call f()` apenas uma vez, usando seu resultado tanto como o valor a ser testado (`val_exp`) quanto como o limite inferior (`val_bound_a`).

## 6\. Critérios de Aceitação (Teste Crítico de Tautologia e Side Effect)

O projeto será aceito se for capaz de executar corretamente o seguinte cenário, demonstrando que a função com *side effect* é chamada apenas uma vez no caso tautológico.

```
{
    var count = 0;
    
    proc side_effect_func() {
        count := count + 1;
        return 5;
    }
    
    // Teste Crítico: Tautologia e Avaliação Única
    // Usando a sintaxe [A..B), que equivale a A <= x <= B.
    // A expressão 'side_effect_func()' é Exp e também BoundA.
    // Ela deve ser avaliada APENAS UMA VEZ.
    
    if (side_effect_func() in [side_effect_func()..10)) {
        write("Sucesso");
    } else {
        write("Falha");
    }

    write(count); // Deve imprimir 1
}
```

**Resultado Esperado:** A variável `count` deve terminar com o valor **1**. Se o valor for 2, a implementação falhou em reutilizar o resultado da expressão `Exp` para o limite `BoundA`.
