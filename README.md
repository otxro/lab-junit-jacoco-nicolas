# Laboratório de Testes com JUnit 5

**Duração:** 60 minutos · **Formato:** duplas · **Linguagem:** Java 17+

Neste laboratório você vai escrever testes automatizados com **JUnit 5** para um pequeno
sistema bancário. Tudo já está configurado: o JUnit vem dentro do projeto, em **um único
arquivo `.jar`** (pasta `lib/`). Você só precisa rodar, observar e escrever testes.

---

## 0. Antes de começar

### 0.1 O que você precisa

- **JDK 17 ou superior** (o JDK, não só o JRE). Confira no terminal:

  ```bash
  java -version
  javac -version
  ```

  Os dois comandos devem mostrar a versão 17 ou maior.

- Um editor de texto qualquer (VS Code, IntelliJ, Notepad++, ...).

### 0.2 Fork e clone (feito em dupla: **um** de vocês faz o fork)

1. Na página do repositório no GitHub, clique em **Fork** (canto superior direito) →
   **Create fork**. O fork fica na **sua** conta.
2. No **seu fork**: *Settings → Collaborators → Add people* e adicione a sua dupla.
3. Aba **Actions**: clique em **I understand my workflows, enable them**. Isso liga o CI
   (*Java with Maven*), que roda todos os testes a cada push.
4. Clone o **seu fork** (não o repositório original):

   ```bash
   git clone https://github.com/<SEU-USUARIO>/<NOME-DO-REPO>.git
   cd <NOME-DO-REPO>
   ```
### 0.3 Rode pela primeira vez

Na pasta do projeto:

```bash
java scripts/Rodar.java
```

O script compila o código e os testes e roda o JUnit. Você deve ver, no final, um resumo
parecido com:

```
[         3 tests successful      ]
[         9 tests skipped         ]
[         0 tests failed          ]
```

Os testes *skipped* (pulados) são os que **você** vai completar. 

**Rodar só um teste ou uma classe** (útil quando você está no meio de um nível):

```bash
java scripts/Rodar.java --select-class br.ufrgs.inf.lab.nivel1.ContaTest
java scripts/Rodar.java --select-method br.ufrgs.inf.lab.nivel1.ContaTest#saqueValidoDeveDiminuirOSaldo
```

**Como ler a saída:** cada teste aparece numa árvore, com uma marca algo como `[OK]` (passou),
`[X]` (falhou, com a mensagem *expected / but was*) ou `[S]` (pulado). Em erros de compilação,
a primeira linha de cada erro traz o arquivo e o número da linha.

---

## 1. Conceitos em 2 minutos

**JUnit 5** é a biblioteca que executa testes. Os blocos que você vai usar:

| Elemento | Para que serve |
|---|---|
| `@Test` | marca um método como teste |
| `@BeforeEach` | roda antes de cada teste (prepara o cenário) |
| `@Disabled` | pula o teste (**remova** quando for implementar) |
| `assertEquals(esperado, obtido)` | compara valores (em `double`, passe uma tolerância: `assertEquals(1.5, x, 0.001)`) |
| `assertTrue(cond)` / `assertFalse(cond)` | verifica uma condição (ex.: o método devolveu `true` ou `false`) |
| `@ParameterizedTest` + `@CsvSource` | roda o mesmo teste com vários dados |

**Padrão AAA** (use em todo teste): **A**rrange (preparar) → **A**ct (executar) → **A**ssert (verificar).

**Cada decisão tem dois lados.** Todo `if` do código pode dar verdadeiro ou falso. Um bom
conjunto de testes faz o `if` ser **verdadeiro em pelo menos um teste e falso em pelo menos
outro**. Testar só o caminho feliz deixa metade do código sem verificação. Neste lab, você vai
conferir isso **à mão**, `if` por `if`.

---

## 2. O projeto

```
lib/                  ← o JUnit (um único .jar)
scripts/Rodar.java    ← compila e roda os testes
src/main/java/br/ufrgs/inf/lab/
  nivel1/  Conta                                     ← código a testar (Nível 1)
  nivel2/  ServicoTransferencia, StatusTransferencia ← código a testar (Nível 2)
  nivel3/  AnalisadorEmprestimo, DecisaoEmprestimo   ← código a testar (Nível 3)
src/test/java/br/ufrgs/inf/lab/
  nivel1/  ContaTest                 ← 2 testes prontos + 4 para completar
  nivel2/  ServicoTransferenciaTest  ← 1 teste pronto + 5 para escrever
  nivel3/  (vazio)                   ← você cria AnalisadorEmprestimoTest
```

**Não altere** o código em `src/main`. Só mexa em `src/test`.

---

## 3. NÍVEL 1: Guiado (≈ 15 min)

Arquivo: `src/test/java/.../nivel1/ContaTest.java`

Antes de tudo, abra `Conta.java` e leia as regras no comentário da classe. Depois leia os dois
testes prontos (`contaNovaDeve...` e `depositoValido...`). Em seguida, complete os 4 testes
marcados com `@Disabled`, **um de cada vez**.

### Receita para cada teste

1. Leia o comentário `PASSO 1, 2, 3` dentro do método.
2. Escreva o código dos passos.
3. **Apague** a linha `@Disabled(...)` **e** a linha `fail("Complete o teste ...")`.
4. Rode só aquele teste:

   ```bash
   java scripts/Rodar.java --select-method br.ufrgs.inf.lab.nivel1.ContaTest#depositoComValorNegativoDeveSerRecusado
   ```

5. Faça o teste **falhar de propósito** uma vez (troque um valor esperado), veja a mensagem
   do JUnit, e conserte. *Um teste que você nunca viu falhar não é confiável.*

### Exemplo resolvido (N1-A, para você entender a forma)

```java
@Test
void depositoComValorNegativoDeveSerRecusado() {
    Conta conta = new Conta("Ana", 100.0);        // Arrange

    boolean depositou = conta.depositar(-10.0);   // Act

    assertFalse(depositou);                        // Assert: o depósito foi recusado
    assertEquals(100.0, conta.getSaldo(), DELTA);  // Assert: o saldo não mudou
}
```

### Os outros três (monte você, seguindo os passos do arquivo)

| Teste | Ideia | Asserts principais |
|---|---|---|
| N1-B `saqueValido...` | sacar 30 de 100 | `assertTrue(sacou)` e `assertEquals(70.0, conta.getSaldo(), DELTA)` |
| N1-C `saqueMaiorQueOSaldo...` | sacar 100,01 de 100 | `assertFalse(sacou)` e saldo intacto (100.0) |
| N1-D `saqueDoSaldoExato...` | sacar 100 de 100 (**valor-limite**) | `assertTrue(sacou)` e `assertEquals(0.0, ...)` |

### ✅ Checkpoint do Nível 1

```bash
java scripts/Rodar.java
```

- Todos os testes de `ContaTest` passam e **nenhum** está pulado.
- **Caça aos `if`s:** conte os `if` de `Conta.java` (são 4). Para cada um, preencha no
  `RELATORIO.md` qual teste faz o `if` ser **verdadeiro** e qual faz ser **falso**. Os `if`
  sem teste de algum dos lados pedem **testes extras**: escreva-os no `ContaTest`.

---

## 4. NÍVEL 2: Semi-guiado (≈ 15 min)

Arquivo: `src/test/java/.../nivel2/ServicoTransferenciaTest.java`

Aqui **não há asserts prontos**. Cada teste `@Disabled` traz só o **cenário** (Dado/Quando/Então).
Você decide *quais* asserts usar.

Como trabalhar:

1. Leia as regras **R1 a R5** no comentário de `ServicoTransferencia.java`.
2. Para cada teste, **calcule os valores esperados na mão** (papel/calculadora) *antes* de rodar.
3. Escreva o teste, remova `@Disabled` e `fail(...)`, rode.
4. Atenção aos **valores-limite**: 1.000,00 / 1.000,01 / 5.000,00 / 5.000,01.
5. Pergunte-se em cada teste: *"além do retorno, o que mais mudou (ou deveria NÃO ter mudado)?"*
   (saldos das duas contas!)

### ✅ Checkpoint do Nível 2

- Todos os testes passam e nenhum está pulado.
- No `RELATORIO.md`, marque qual teste exercita cada regra **R1 a R5**. Alguma regra ficou
  sem teste? Escreva mais um.

---

## 5. NÍVEL 3: Desafio, do zero (≈ 15 min)

Classe: `src/main/java/.../nivel3/AnalisadorEmprestimo.java` (leia as regras 1 a 6 no comentário).

**Não existe nenhum teste.** Crie o arquivo
`src/test/java/br/ufrgs/inf/lab/nivel3/AnalisadorEmprestimoTest.java`.

Você decide tudo: quais cenários, quais valores, quais asserts.

- Teste **o que o código deveria fazer** (as regras do comentário), não apenas o que ele faz.

---

## 6. Verificação final (≈ 5 min)

```bash
java scripts/Rodar.java
```

- **0 tests failed** e **0 tests skipped**.
- Nenhum `@Disabled` nem `fail("Complete...")` sobrando (`grep -rn "Disabled\|Complete o teste" src/test` não deve achar nada).
- Nenhum arquivo de `src/main` alterado.

---

## 7. Entrega

1. Preencha o **`RELATORIO.md`**: nomes da dupla, tabelas dos `if`s e das regras, cole a saída
   final do `java scripts/Rodar.java` e responda às perguntas de reflexão.
2. No GitHub, faça o commit dos arquivos alterados no **seu fork**. 
3. No GitHub, abra o seu fork, aba **Actions**, e espere o círculo ficar verde ✔ (1 a 2 minutos).
4. Envie **o link do seu fork** (`https://github.com/<usuario>/<repo>`) para a professora, conforme
   combinado em aula.

**Checklist antes de enviar:**

- [ ] `java scripts/Rodar.java`: 0 failed, 0 skipped
- [ ] Nenhum arquivo de `src/main` foi alterado
- [ ] `RELATORIO.md` preenchido (tabelas, saída colada, reflexões)
- [ ] O fork está **público** (ou a professora foi adicionada como colaboradora)
- [ ] O **Actions** do fork está verde ✔

---

## 8. Problemas comuns

| Sintoma | Solução |
|---|---|
| `Nao achei lib/...jar` | O arquivo do JUnit não está na pasta `lib/`: veja `lib/LEIAME.md`. |
| `Erro de compilacao nos testes` | Leia a primeira linha de cada erro (arquivo:linha). Costuma ser `import` faltando, `;` ou parêntese, ou o nome de um método escrito diferente do código. |
| Teste com `double` falha por `0.30000000000000004` | Passe a tolerância: `assertEquals(esperado, obtido, 0.001)`. |
| Meu teste não aparece na saída | Confira o `@Test` (pacote `org.junit.jupiter.api`) e se removeu o `@Disabled`. |
| Aba Actions pede para habilitar | Clique em **I understand my workflows, enable them**. |
