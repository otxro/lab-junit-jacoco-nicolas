package br.ufrgs.inf.lab.nivel2;

import static org.junit.jupiter.api.Assertions.*;

import br.ufrgs.inf.lab.nivel1.Conta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * NÍVEL 2 (semi-guiado): ServicoTransferencia.
 *
 * Aqui NÃO há os asserts: cada teste abaixo diz o CENÁRIO e o que deve
 * ser verificado. Você decide como escrever (assertEquals, assertTrue...).
 * Leia as regras R1..R5 em ServicoTransferencia.java.
 *
 * Remova o @Disabled e o fail(...) de cada teste à medida que o implementar.
 * Sinta-se livre para ACRESCENTAR testes (e usar @ParameterizedTest).
 */
class ServicoTransferenciaTest {

    private static final double DELTA = 0.001;

    private ServicoTransferencia servico;
    private Conta origem;
    private Conta destino;

    // Roda ANTES de cada teste: garante contas "limpas" (testes independentes)
    @BeforeEach
    void preparar() {
        servico = new ServicoTransferencia();
        origem = new Conta("Origem", 10_000.0);
        destino = new Conta("Destino", 0.0);
    }

    // ---------------------------------------------------------------
    // PRONTO (exemplo)
    // ---------------------------------------------------------------

    @Test
    void transferenciaPequenaDeveSerAceitaSemTarifa() {
        StatusTransferencia status = servico.transferir(origem, destino, 200.0);

        assertEquals(StatusTransferencia.OK, status);
        assertEquals(9_800.0, origem.getSaldo(), DELTA);
        assertEquals(200.0, destino.getSaldo(), DELTA);
    }

    // ---------------------------------------------------------------
    // PARA VOCÊ ESCREVER
    // ---------------------------------------------------------------

    @Test
    void transferenciaAcimaDe1000PagaTarifaDe1Porcento() {
        // DADO   origem com 10.000 e destino com 0
        // QUANDO transfere 2.000
        // ENTÃO  status = ?  | calcularTarifa(2000) = ?  | saldo da origem = ?  | saldo do destino = ?
        //        (calcule os valores esperados na mão ANTES de rodar!)
        StatusTransferencia status = servico.transferir(origem, destino, 2000.0);
        double tarifa = servico.calcularTarifa(2000.0);

        assertEquals(StatusTransferencia.OK, status);
        assertEquals(20.0, tarifa, DELTA);
        assertEquals(2000.0, destino.getSaldo(),DELTA);
        assertEquals(7980.0, origem.getSaldo(),DELTA);
    }

    @Test
    void transferenciaDeExatamente1000NaoPagaTarifa() {
        // Valor-limite da regra R4: "ACIMA de 1.000" ... e 1.000 exatos?
        // Verifique o status, a tarifa e os saldos.
        StatusTransferencia status = servico.transferir(origem, destino, 1000.0);
        double tarifa = servico.calcularTarifa(1000.0);

        assertEquals(StatusTransferencia.OK, status);
        assertEquals(0.0, tarifa, DELTA);
        assertEquals(1000.0, destino.getSaldo(),DELTA);
        assertEquals(9000.0, origem.getSaldo(),DELTA);
    }

    @Test
    void transferenciaAcimaDoLimiteDeveSerRecusada() {
        // Qual status? Qual o valor mínimo que ultrapassa o limite (R3)?
        // E um teste com EXATAMENTE o limite: o que deveria acontecer?
        // Verifique também que nenhuma conta foi alterada.
        StatusTransferencia statusAcimaLimite = servico.transferir(origem, destino, 5000.01);
        double tarifa = servico.calcularTarifa(5000.01);

        assertEquals(StatusTransferencia.ACIMA_DO_LIMITE, statusAcimaLimite);
        assertEquals(50.0, tarifa, DELTA);
        assertEquals(0.0, destino.getSaldo(),DELTA);
        assertEquals(10_000.0, origem.getSaldo(),DELTA);

        StatusTransferencia statusLimite = servico.transferir(origem, destino, 5000.0);
        assertEquals(StatusTransferencia.OK, statusLimite);
    }

    @Test
    void saldoInsuficienteParaValorMaisTarifaNaoAlteraNenhumaConta() {
        // DICA: crie uma conta de origem com saldo suficiente para o VALOR,
        //       mas NÃO suficiente para VALOR + TARIFA (ex.: saldo 2.000, valor 2.000).
        // Verifique o status e que origem E destino mantêm os saldos originais.
        Conta origemInsuficiente = new Conta ("Origem Insuficiente", 2_000.0);

        StatusTransferencia status = servico.transferir(origemInsuficiente, destino, 2000.0);
        double tarifa = servico.calcularTarifa(origemInsuficiente.getSaldo());

        assertEquals(StatusTransferencia.SALDO_INSUFICIENTE, status);
        assertEquals(2020.0, tarifa+origemInsuficiente.getSaldo(), DELTA);
        assertEquals(0.0, destino.getSaldo(),DELTA);
        assertEquals(2000.0, origemInsuficiente.getSaldo(),DELTA);

    }

    @Test
    void mesmaContaEValorInvalidoDevemSerRecusados() {
        // Cubra R1 (mesma conta) e R2 (valor zero e valor negativo).
        // Quantos testes/asserts você precisa para exercitar os DOIS lados de cada "if"?
        // Em todos os casos, confira o status E que os saldos não mudaram.
        StatusTransferencia statusMesmaConta = servico.transferir(origem, origem, 1000.0);

        assertEquals(StatusTransferencia.MESMA_CONTA, statusMesmaConta);
        assertEquals(10_000.0, origem.getSaldo());
        assertEquals(0.0, destino.getSaldo());

        StatusTransferencia statusValorInvalidoNegativo = servico.transferir(origem, destino, -100.0);
        assertEquals(StatusTransferencia.VALOR_INVALIDO, statusValorInvalidoNegativo);
        assertEquals(10_000.0, origem.getSaldo());
        assertEquals(0.0, destino.getSaldo());

        StatusTransferencia statusValorInvalidoZero = servico.transferir(origem, destino, -100.0);
        assertEquals(StatusTransferencia.VALOR_INVALIDO, statusValorInvalidoZero);
        assertEquals(10_000.0, origem.getSaldo());
        assertEquals(0.0, destino.getSaldo());
    }
}
