package br.ufrgs.inf.lab.nivel2;

import br.ufrgs.inf.lab.nivel1.Conta;

/**
 * Transferência entre contas (NÍVEL 2).
 *
 * transferir(origem, destino, valor) devolve um StatusTransferencia, conforme as regras
 * (verificadas nesta ordem):
 *
 *  R1. origem e destino são a MESMA conta           -> MESMA_CONTA
 *  R2. valor menor ou igual a zero                  -> VALOR_INVALIDO
 *  R3. valor acima de R$ 5.000,00                   -> ACIMA_DO_LIMITE
 *      (exatamente R$ 5.000,00 é permitido)
 *  R4. a origem precisa ter saldo para (valor + tarifa), senão -> SALDO_INSUFICIENTE
 *      e NENHUMA das duas contas é alterada.
 *      Tarifa: transferências ACIMA de R$ 1.000,00 pagam 1% do valor
 *      (exatamente R$ 1.000,00 NÃO paga tarifa). A tarifa sai da conta de origem.
 *  R5. caso contrário -> OK: a origem perde (valor + tarifa) e o destino recebe só o valor.
 *
 * calcularTarifa(valor) devolve a tarifa de um valor (0.0 quando não há tarifa).
 */
public class ServicoTransferencia {

    public static final double LIMITE = 5000.00;
    public static final double FAIXA_ISENTA = 1000.00;
    public static final double PERCENTUAL_TARIFA = 0.01;

    public StatusTransferencia transferir(Conta origem, Conta destino, double valor) {
        if (origem == destino) {
            return StatusTransferencia.MESMA_CONTA;
        }
        if (valor <= 0) {
            return StatusTransferencia.VALOR_INVALIDO;
        }
        if (valor > LIMITE) {
            return StatusTransferencia.ACIMA_DO_LIMITE;
        }

        double total = valor + calcularTarifa(valor);
        if (!origem.sacar(total)) {
            return StatusTransferencia.SALDO_INSUFICIENTE;
        }
        destino.depositar(valor);
        return StatusTransferencia.OK;
    }

    public double calcularTarifa(double valor) {
        if (valor > FAIXA_ISENTA) {
            return valor * PERCENTUAL_TARIFA;
        }
        return 0.0;
    }
}
