# Relatório do Laboratório: JUnit 5

**Dupla:** Nicolas Otero Gomes
**Link do fork:** _https://github.com/..._

## 1. Resultado final

Cole aqui o resumo que aparece no final de `java scripts/Rodar.java`:

```
(cole aqui)
```

## 2. Nível 1: `if`s de `Conta`

Para cada `if` de `Conta.java`, diga qual teste faz o `if` ser **verdadeiro** e qual faz ser **falso**
(escreva o nome do método de teste).

| `if` (condição) | Teste que torna **verdadeiro** | Teste que torna **falso** |
|---|---|---|
| saldo inicial negativo (construtor) |`contaNovaComSaldoInicialNegativoDeveSerZeroEGuardarTitular`|`contaNovaDeveGuardarTitularESaldoInicial`|
| valor do depósito `<= 0` |`depositoComValorNegativoDeveSerRecusado`|`depositoValidoDeveAumentarOSaldo`|
| valor do saque `<= 0` |`saqueMenorOuIgualAZeroDeveSerRecusado` | `saqueValidoDeveDiminuirOSaldo`|
| saque maior que o saldo |`saqueMaiorQueOSaldoDeveSerRecusado` | `saqueDoSaldoExatoDeveSerPermitidoEZerarAConta`|

## 3. Nível 2: regras de `ServicoTransferencia`

| Regra | Teste(s) que a exercita(m) |
|---|---|
| R1 mesma conta | `mesmaContaEValorInvalidoDevemSerRecusados`, `transferenciaPequenaDeveSerAceitaSemTarifa`|
| R2 valor <= 0 |`mesmaContaEValorInvalidoDevemSerRecusados`, `transferenciaPequenaDeveSerAceitaSemTarifa` |
| R3 acima do limite de 5.000 (e 5.000 exatos permitido) | `transferenciaAcimaDoLimiteDeveSerRecusada`|
| R4 saldo insuficiente para valor + tarifa (nada é alterado); tarifa de 1% acima de 1.000 (1.000 exatos sem tarifa) | `saldoInsuficienteParaValorMaisTarifaNaoAlteraNenhumaConta`,  `transferenciaAcimaDe1000PagaTarifaDe1Porcento`, `transferenciaDeExatamente1000NaoPagaTarifa`|
| R5 transferência OK: saldos da origem e do destino | `transferenciaPequenaDeveSerAceitaSemTarifa`, `transferenciaAcimaDe1000PagaTarifaDe1Porcento`, `transferenciaDeExatamente1000NaoPagaTarifa`|

## 4. Nível 3: regras de `AnalisadorEmprestimo`

| Regra | Cenário escolhido (entradas → decisão esperada) | Método de teste |
|---|---|---|
| 1. entradas inválidas → DADOS_INVALIDOS (cada uma das 6 condições) | 1: score negativo -> DADOS_INVALIDOS; 2: score maior que 1000 -> DADOS_INVALIDOS; 3: rendo igual a zero -> DADOS_INVALIDOS; 4: valor igual a zero -> DADOS_INVALIDOS; 5: parcela igual a zero -> DADOS_INVALIDOS; 6: parcela maior que 360 -> DADOS_INVALIDOS| `assertEquals`|
| 2. score < 300 → NEGADO | 1: score igual a 299 -> NEGADO; 2: score igual a 250 -> NEGADO|`assertEquals` |
| 3. taxa por faixa de score (800 / 600) | 1: score = 800 -> 0.12; 2: score = 1000 -> 0.12; 3: score = 799 -> diferente de 0.12; 4: score = 600 -> 0.24; 5: score = 799 -> 0.24; 6: score = 599 -> 0.36| `assertEquals`, `assertNotEquals`|
| 4. parcela (Price) |1: score=800, valor=1000, parcelas=10 -> 105.582; 2: score=799, valor=1000; parcela=10 -> diferente de 105.582; 5: score=600, valor=1000, parcela=10 -> 111.326; 4: score=799, valor=1000, parcela=10 -> 111.326; 5: score=599, valor=1000, parcela=10 -> diferente de 111.326; 6: score=599, valor=1000; parcela=10 -> 177.230|`assertEquals`, `assertNotEquals` |
| 5. comprometimento > 50% → NEGADO; > 30% → ANÁLISE MANUAL |1: score=800, renda=1000 valor=10000, parcela=12 -> NEGADO, comprometimento = 0.88849; 2: score=800, renda=2000, valor=10000, parcela=12 -> ANALISE_MANUAL, comprometimento = 0.44424;  | `assertEquals`|
| 6. comprometimento <= 30%: score >= 600 aprovado; abaixo, análise manual | 1: score=500, renda=5000, valor=10000, parcelas=36 -> ANALISE_MANUAL, comprometimento = 0.09161; 2: score=800, renda=3000, valor=10000, parcelas=12 -> APROVADO, comprometimento=0.29616; 3: score=600, renda=5000, valor=10000, parcelas=12 -> APROVADO, comprometimento=0.18912|`assertEquals` |

## 5. Reflexão (2 a 4 linhas cada)

1. **Os dois lados do `if`:** qual `if` ficou sem teste de um dos lados no começo? Que bug passaria
   despercebido por isso? 
   Sem as minhas adições seria: `if(saldoInicial < 0)`, do saque `if(valos <= 0)`. Nenhum bug passaria, pois esse coidado é feito no código apenas os testes inicias não cobriam.
2. **Valor-limite:** qual teste de fronteira (ex.: 1.000,00 / 5.000,00 / score 600) vocês acham
   mais importante? Que bug ele pegaria se o código usasse `>=` no lugar de `>`?
   Acredito que seja o score, os valores 1000 e 5000 usam `>` o que inclue eles próprio enquanto o 600 do score usa `>=`. Se fosse trocado, o teste pegaria uma taxa anual inválida para o score de 600, o correto 24%, iria para 36%.
3. **Nível 3:** como vocês decidiram quais cenários testar? (classes de equivalência, fronteiras,
   leitura das regras...)
   Tentei seguir a ordem das regras e aplicar os testes sempre na froteira.
4. **Teste que passa mas não protege:** descrevam um teste que executa o código e passa, mas não
   verificaria um erro (dica: um teste sem `assert`).
   O teste `contaNovaComSaldoInicialNegativoDeveSerZeroEGuardarTitular`, sem o assert e com entradas "Ana" e "-10", o teste passa.
5. **Divisão do trabalho:** quem escreveu o quê? (uma linha) Fiz sozinho, depois do horaŕio de entrega.
