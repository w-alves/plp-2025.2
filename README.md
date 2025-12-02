## 1\. Título do Projeto

**Implementação do Operador de Intervalo 'in' na LI2 (e.g., `Exp in [A..B)`)**

## 2\. Objetivo

Estender a **Linguagem Imperativa 2 (LI2)** para suportar um **operador de verificação de intervalo (`in`)**. O principal desafio é garantir a **avaliação única** de expressões com **efeitos colaterais (side effects)**, especialmente em casos tautológicos (e.g., `f() in [f()..B)`), onde a expressão (`f()`) deve ser avaliada rigorosamente uma única vez e seu resultado reutilizado.

## 3\. Linguagem-Base

Linguagem Imperativa 2 (LI2).

## 3.1\. Funções com Retorno (Pré-requisito)

O uso de `Exp in [A..B)` pressupõe que expressões possam invocar operações com valor de retorno (e.g., `f()` dentro de uma soma). A LI2 atual expõe apenas procedimentos (`proc`) utilizados como comandos via `call`, o que impede chamadas em contexto de expressão. Antes de evoluir o operador `in`, precisamos introduzir **funções com retorno** na linguagem.

- **Sintaxe sugerida**

  ```
  func Id ( [ ListaDeclaracaoParametro ] ) : Tipo {
      Comando*            // corpo com efeitos colaterais opcionais
      return Expressao;   // valor final
  }

  ExpressaoPrimaria ::= ... | Id "(" [ ListaExpressao ] ")"
  ```
  
Com essa extensão, chamadas com efeitos colaterais podem participar de qualquer expressão e ser reutilizadas pelo operador `in` sem reexecução desnecessária.


-----

## 4\. Definição da Nova Sintaxe: Operador 'in'

A nova feature introduz o operador `in` para verificação de intervalo, substituindo a sintaxe de comparação encadeada. A sintaxe permite definir intervalos abertos ou fechados em ambas as extremidades, com a seguinte semântica:

| Sintaxe LI2 | Equivalência Lógica | Definição (Limite A) | Definição (Limite B) |
| :--- | :--- | :--- | :--- |
| `Exp in (A..B)` | $(A < \text{Exp}) \text{ AND } (\text{Exp} \le B)$ | `(` Exclusivo | `)` Exclusivo |
| `Exp in (A..B]` | $(A < \text{Exp}) \text{ AND } (\text{Exp} < B)$ | `(` Exclusivo | `]` Inclusivo |
| `Exp in [A..B)` | $(A \le \text{Exp}) \text{ AND } (\text{Exp} \le B)$ | `[` Inclusivo | `)` Exclusivo |
| `Exp in [A..B]` | $(A \le \text{Exp}) \text{ AND } (\text{Exp} < B)$ | `[` Inclusivo | `]` Inclusivo |

O desafio central é que `Exp`, `A` e `B` podem ser chamadas de procedimentos com efeitos colaterais, e a semântica de avaliação deve ser rigorosa.

-----

## 5\. Escopo Técnico e Implementação

O projeto exigirá modificações no analisador sintático e na fase de avaliação semântica para lidar com a nova sintaxe e suas regras de avaliação.

### A. Análise Sintática (Parser)

  * **Expansão da Gramática (BNF):** Modificar a BNF para incluir a produção `Expressao "in" Intervalo`. A precedência deste operador deve ser similar à dos operadores relacionais (`<`, `>`, `==`).

    ```bnf
    Expressao ::= Valor
                | ExpUnaria 
                | ExpBinaria 
                | Id
                | ChamadaFuncao
                | Expressao "in" Intervalo

    Intervalo ::= DelimEsq Expressao ".." Expressao DelimDir

    DelimEsq ::= "(" | "["
    DelimDir ::= ")" | "]"

    Comando ::= Atribuicao
            | ComandoDeclaracao
            | While
            | IfThenElse
            | IO
            | Comando ";" Comando
            | Skip
            | ChamadaProcedimento
            | Return

    Return ::= "return" Expressao

    Declaracao ::= DeclaracaoVariavel
                | DeclaracaoProcedimento
                | DeclaracaoComposta
                | DeclaracaoFuncao

    DeclaracaoFuncao ::= "func" Id "(" [ ListaDeclaracaoParametro ] ")"
                        ":" Tipo
                        "{" Comando* "return" Expressao "}"

    ChamadaFuncao ::= Id "(" [ ListaExpressao ] ")"
    ```

### B. Análise Semântica e Runtime (Garantia de Avaliação Única)

A expressão `Exp in <Intervalo>` deve ser transformada em uma lógica `AND` (duas comparações). O desafio central é a ordem e a memoização (armazenamento) da avaliação para garantir a corretude com *side effects*.

O interpretador deve implementar o seguinte mecanismo:

1.  Identificar as três expressões: `Exp` (o valor a ser testado), `BoundA` (limite inferior) e `BoundB` (limite superior).
2.  **Avaliar `Exp` e todos os seus *side effects* rigorosamente uma única vez** e armazenar seu resultado (`val_exp`).
3.  **Consultar o Resultado de `Exp` (Core da Feature):** Se a expressão `BoundA` for *sintaticamente idêntica* a `Exp` (e.g., a mesma chamada de função `f()`), o interpretador **não deve re-executá-la**. Ele deve, em vez disso, usar o valor já armazenado (`val_exp`) como `val_bound_a`. O mesmo se aplica a `BoundB`.
4.  Avaliar `BoundA` e `BoundB` (apenas se *não* forem idênticas a `Exp` e, portanto, não "consultadas" no passo 3) e armazenar seus resultados.
5.  Executar as duas comparações lógicas (e.g., para `[A..B]`, seria `(val_bound_a <= val_exp) AND (val_exp <= val_bound_b)`), respeitando o curto-circuito do `AND`.

Esta abordagem garante que uma expressão tautológica como `f() in [f()..10)` execute `f()` apenas uma vez, usando seu resultado tanto como o valor a ser testado (`Exp`) quanto como o limite inferior (`BoundA`).

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
    // Usando a sintaxe [A..B), que equivale a A <= x < B.
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

-----

## 7\. Melhorias Implementadas: Canonicalização e Equivalência Semântica

Além da implementação básica do operador `in` com memoização estrutural, foram adicionadas melhorias para detectar **equivalência semântica** entre expressões, permitindo otimizações mais avançadas.

### 7.1\. Problema: Comutatividade não Detectada

A implementação inicial usava **comparação estrutural estrita** (`ExpressaoComparator.estruturaIgual`), que compara a árvore sintática exatamente como foi parseada. Isso significa que:

- `a + b` ≠ `b + a` (estruturalmente diferentes)
- `x == y` ≠ `y == x` (estruturalmente diferentes)

**Impacto:** Expressões semanticamente equivalentes não eram reconhecidas, perdendo oportunidades de otimização.

### 7.2\. Solução: Normalização (Canonicalização) da Árvore Sintática

Implementamos um sistema de **canonicalização** que transforma expressões em uma forma normal antes da comparação, aplicando transformações matemáticas válidas.

#### A. Análise de Pureza (`PurityVerifier.java`)

Antes de aplicar qualquer transformação, verificamos se a expressão é **pura** (livre de efeitos colaterais):

```java
public static boolean isPure(Expressao exp) {
    // Literais e variáveis são puros
    if (exp instanceof ValorConcreto || exp instanceof Id) return true;
    
    // Chamadas de função são IMPURAS (conservador)
    if (exp instanceof ChamadaFuncao) return false;
    
    // Operadores são puros se os operandos forem puros
    if (exp instanceof ExpBinaria) {
        return isPure(esq) && isPure(dir);
    }
    ...
}
```

**Critério Conservador:** Assumimos que **todas as funções têm efeitos colaterais**, garantindo que nunca reordenamos expressões que possam alterar o comportamento do programa.

#### B. Normalização Comutativa (`ExpressaoNormalizer.java`)

Para expressões puras, aplicamos **comutatividade** (reordenação de operandos):

```java
public static Expressao normalizar(Expressao exp) {
    if (exp instanceof ExpSoma) {
        return normalizarComutativo((ExpSoma) exp);
    }
    // Também para: ExpAnd, ExpOr, ExpEquals
    ...
}

private static ExpSoma normalizarComutativo(ExpSoma exp) {
    Expressao esq = normalizar(exp.getEsq());
    Expressao dir = normalizar(exp.getDir());
    
    // Só aplica comutatividade se AMBOS forem puros
    if (PurityVerifier.isPure(esq) && PurityVerifier.isPure(dir)) {
        // Ordena lexicograficamente
        if (esq.toString().compareTo(dir.toString()) > 0) {
            return new ExpSoma(dir, esq);  // Swap!
        }
    }
    return new ExpSoma(esq, dir);
}
```

**Operadores Suportados:**
- `+` (soma)
- `and` (conjunção lógica)
- `or` (disjunção lógica)
- `==` (igualdade)

#### C. Integração com o Comparador

O `ExpressaoComparator` foi atualizado para normalizar antes de comparar:

```java
public static boolean estruturaIgual(Expressao a, Expressao b) {
    // Normaliza ANTES de comparar
    Expressao aNorm = ExpressaoNormalizer.normalizar(a);
    Expressao bNorm = ExpressaoNormalizer.normalizar(b);
    
    // Agora compara as versões normalizadas
    if (aNorm.getClass() != bNorm.getClass()) return false;
    ...
}
```

### 7.3\. Exemplos de Equivalência Detectada

| Expressão Original | Forma Normalizada | Detecta Equivalência? |
|-------------------|-------------------|----------------------|
| `a + b` vs `b + a` | Ambas viram `a + b` | ✅ Sim (puras) |
| `x == y` vs `y == x` | Ambas viram `x == y` | ✅ Sim (puras) |
| `f() + g()` vs `g() + f()` | Mantém ordem original | ❌ Não (impuras) |
| `(a + b) in [(b + a)..10]` | Reconhece tautologia | ✅ Sim (otimiza) |

### 7.4\. Garantias de Corretude

**Preservação de Semântica:**
- Comutatividade **só é aplicada a expressões puras**
- Expressões com funções **nunca são reordenadas**
- Ordem de avaliação é preservada quando há efeitos colaterais

**Exemplo Crítico:**
```java
// f() e g() têm side effects
(f() + g()) in [(g() + f())..10]
```

**Comportamento:**
1. `PurityVerifier.isPure(f())` → `false`
2. Normalização **não aplica comutatividade**
3. `f() + g()` ≠ `g() + f()` (estruturalmente)
4. `f()` e `g()` são executados **duas vezes cada** (correto!)

### 7.5\. Limitações e Trabalhos Futuros

**Limitações Atuais:**
1. **Análise de Pureza Conservadora:** Todas as funções são assumidas impuras, mesmo que sejam matematicamente puras.
2. **Apenas Comutatividade:** Não implementa associatividade (`(a+b)+c ≡ a+(b+c)`) ou identidades (`x+0 ≡ x`).
3. **Comparação Baseada em String:** Usa `toString()` para ordenação, que pode ser instável.

**Possíveis Extensões:**
1. **Anotação de Pureza:** Permitir marcar funções como `pure func` para habilitar otimizações.
2. **Simplificação Algébrica:** Implementar regras como `x + 0 → x`, `x * 1 → x`, `not (not x) → x`.
3. **Associatividade:** Achatar árvores de operadores associativos (`a + b + c`).
4. **Constant Folding:** Avaliar sub-expressões constantes em tempo de compilação (`2 + 3 → 5`).

### 7.7\. Arquivos Implementados

- **`PurityVerifier.java`**: Análise de pureza de expressões
- **`ExpressaoNormalizer.java`**: Transformações de canonicalização
- **`ExpressaoComparator.java`** (modificado): Integração com normalização
