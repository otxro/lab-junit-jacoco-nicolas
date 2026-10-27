package br.ufrgs.inf.lab.nivel1;

/**
 * Conta bancária simples (NÍVEL 1).
 *
 * Regras:
 *  - Saldo inicial negativo não existe: a conta começa com saldo 0.
 *  - depositar(valor): só aceita valor maior que zero.
 *      Retorna true se depositou; retorna false (e não muda nada) se o valor for inválido.
 *  - sacar(valor): só aceita valor maior que zero e que não supere o saldo.
 *      Retorna true se sacou; retorna false (e não muda nada) caso contrário.
 *      Sacar exatamente o saldo inteiro é permitido.
 */
public class Conta {

    private final String titular;
    private double saldo;

    public Conta(String titular, double saldoInicial) {
        this.titular = titular;
        if (saldoInicial < 0) {
            this.saldo = 0;
        } else {
            this.saldo = saldoInicial;
        }
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public boolean depositar(double valor) {
        if (valor <= 0) {
            return false;
        }
        saldo += valor;
        return true;
    }

    public boolean sacar(double valor) {
        if (valor <= 0) {
            return false;
        }
        if (valor > saldo) {
            return false;
        }
        saldo -= valor;
        return true;
    }
}
