
## 1\. Título do Projeto

**Implementação da Sintaxe de Comparação Encadeada na LI2 (e.g., `A < B < C`)**

## 2\. Objetivo

Estender a **Linguagem Imperativa 2 (LI2)** para suportar comparações sequenciais em uma única expressão. O principal desafio é garantir que a expressão intermediária (`B` em `A < B < C`), que pode ser uma **chamada de procedimento com efeitos colaterais**, seja **avaliada rigorosamente uma única vez**.

## 3\. Linguagem-Base

Linguagem Imperativa 2 (LI2).

-----

## 4\. Referência em Outras Linguagens (Python)

Em linguagens como Python, a comparação encadeada é uma sintaxe válida que é expandida internamente (syntactic sugar). O comportamento é definido como:

| Expressão em Python | Equivalência Lógica Interna | Regra de Avaliação |
| :--- | :--- | :--- |
| `0 < x <= 10` | `(0 < x) and (x <= 10)` | `x` é avaliado apenas uma vez. |

**Exemplo de Garantia de Avaliação Única em Python:**

```python
# Função com side effect (altera o estado global 'count')
count = 0
def get_val():
    global count
    count += 1
    return 5

# Teste: a função é chamada apenas uma vez
if 0 < get_val() < 10:
    pass

# Resultado: count é 1, não 2
print(count) 
# Output: 1
```

A implementação na LI2 deve replicar exatamente esse comportamento: avaliar a expressão intermediária uma única vez no *runtime* da LI2, independentemente de ela conter um procedimento com *side effects*.

-----

## 5\. Escopo Técnico e Implementação

O projeto exigirá modificações significativas no analisador sintático e na fase de avaliação semântica para lidar com a complexidade procedural da LI2.

### A. Análise Sintática (Parser)

  * **Expansão da Gramática:** Modificar a BNF para permitir o encadeamento de dois ou mais operadores relacionais consecutivos (ex: `Expressao Op Relacional Expressao Op Relacional Expressao`).
  * **Precedência:** A nova sintaxe deve se integrar corretamente com a precedência de outros operadores, especialmente comandos de fluxo de controle da LI2 (`while`, `if`).

### B. Análise Semântica e Runtime 

  * **Transformação Lógica:** A expressão encadeada deve ser transformada em uma lógica `AND`: `(Exp1 Op1 Exp2) AND (Exp2 Op2 Exp3)`.
  * **Garantia de Avaliação Única (Core do Projeto):** O interpretador deve implementar um mecanismo para:
    1.  Identificar a expressão intermediária (`Exp2`).
    2.  **Avaliar `Exp2` e todos os seus *side effects*** (incluindo chamadas de procedimentos/funções) **apenas uma vez** e armazenar o resultado.
    3.  Usar o valor armazenado nas duas sub-comparações, respeitando o curto-circuito do `AND`. Se a primeira comparação falhar, a segunda não é executada, mas a avaliação única de `Exp2` já ocorreu.

## 6\. Critérios de Aceitação (Teste Crítico de Side Effect)

O projeto será aceito se for capaz de executar corretamente o seguinte cenário, demonstrando que a função com *side effect* é chamada apenas uma vez:

```
{
    var count = 0;
    
    proc side_effect_func() {
        count := count + 1;
        return 5;
    }
    
    if (0 < side_effect_func() < 10) {
        write("Sucesso");
    } else {
        write("Falha");
    }

    write(count); // Deve imprimir 1
}
```

**Resultado Esperado:** A variável `count` deve terminar com o valor **1**.
