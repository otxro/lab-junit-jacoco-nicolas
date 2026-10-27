package br.ufrgs.inf.lab.nivel3;

/**
 * Análise de pedido de empréstimo (NÍVEL 3: sem nenhum teste pronto!).
 *
 * ENTRADAS: score (0..1000), renda mensal, valor solicitado, número de parcelas (1..360).
 *
 * REGRAS (nesta ordem):
 *  1. Entradas inválidas -> DADOS_INVALIDOS
 *       score fora de 0..1000, renda <= 0, valor <= 0, parcelas < 1 ou > 360.
 *  2. score < 300 -> NEGADO (nem calcula parcela).
 *  3. Taxa de juros ANUAL conforme o score:
 *       score >= 800 -> 12%   |   score >= 600 -> 24%   |   demais -> 36%
 *  4. Parcela mensal (tabela Price) com taxa mensal = anual / 12:
 *       parcela = valor * i / (1 - (1 + i)^(-n))
 *  5. comprometimento = parcela / renda
 *       comprometimento > 0,50 -> NEGADO
 *       comprometimento > 0,30 -> ANALISE_MANUAL
 *  6. Caso contrário (comprometimento <= 0,30):
 *       score >= 600 -> APROVADO
 *       score <  600 -> ANALISE_MANUAL
 */
public class AnalisadorEmprestimo {

    public DecisaoEmprestimo analisar(int score, double renda, double valor, int parcelas) {
        if (!dadosValidos(score, renda, valor, parcelas)) {
            return DecisaoEmprestimo.DADOS_INVALIDOS;
        }

        if (score < 300) {
            return DecisaoEmprestimo.NEGADO;
        }

        double taxaMensal = taxaAnual(score) / 12.0;
        double parcela = calcularParcela(valor, taxaMensal, parcelas);
        double comprometimento = parcela / renda;

        if (comprometimento > 0.50) {
            return DecisaoEmprestimo.NEGADO;
        }
        if (comprometimento > 0.30) {
            return DecisaoEmprestimo.ANALISE_MANUAL;
        }
        return score >= 600 ? DecisaoEmprestimo.APROVADO : DecisaoEmprestimo.ANALISE_MANUAL;
    }

    /** Taxa anual (em fração, ex.: 0.12 = 12%) conforme o score. */
    double taxaAnual(int score) {
        if (score >= 800) {
            return 0.12;
        }
        if (score >= 600) {
            return 0.24;
        }
        return 0.36;
    }

    /** Parcela da tabela Price. */
    double calcularParcela(double valor, double taxaMensal, int parcelas) {
        return valor * taxaMensal / (1 - Math.pow(1 + taxaMensal, -parcelas));
    }

    private boolean dadosValidos(int score, double renda, double valor, int parcelas) {
        if (score < 0 || score > 1000) {
            return false;
        }
        if (renda <= 0) {
            return false;
        }
        if (valor <= 0) {
            return false;
        }
        if (parcelas < 1 || parcelas > 360) {
            return false;
        }
        return true;
    }
}
