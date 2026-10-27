package br.ufrgs.inf.lab.nivel3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//  *   Arrange (preparar)  ->  Act (agir)  ->  Assert (verificar)

public class AnalisadorEmprestimoTest {

    // Tolerância para comparar números decimais (double) com assertEquals
    private static final double DELTA = 0.001;

    AnalisadorEmprestimo analisador;

    // Arrange
    @BeforeEach 
    void prepara() {
        analisador = new AnalisadorEmprestimo();
    }
    
    @Test 
    void valoresInvalidosDevemSerRecusados() {

        // ============ Act ============ //

        // score < 0 -> INVALIDO
        DecisaoEmprestimo decisaoScoreMenor0 = analisador.analisar(-1, 1000.0, 500.0, 1);

        // score > 1000-> INVALIDO
        DecisaoEmprestimo decisaoScoreMaior1000 = analisador.analisar(1001, 1000.0, 500.0, 1);

        // renda <= 0 -> INVALIDO
        DecisaoEmprestimo decisaoRenda0 = analisador.analisar(200, 0, 500.0, 1);
        
        // valor <= 0 -> INVALIDO
        DecisaoEmprestimo decisaoValor0 = analisador.analisar(200, 1000.0, 0.0, 1);
        
        // parcela < 1 -> INVALIDO
        DecisaoEmprestimo decisaoParcelaMenor1 = analisador.analisar(200, 1000.0, 500.0, 0);
        
        // parcela > 360 -> INVALIDO
        DecisaoEmprestimo decisaoParcelaMaior360 = analisador.analisar(200, 1000.0, 500.0, 361);
        
        // ============ Assert ============ \\

        assertEquals(DecisaoEmprestimo.DADOS_INVALIDOS, decisaoScoreMenor0);
        assertEquals(DecisaoEmprestimo.DADOS_INVALIDOS, decisaoScoreMaior1000);
        assertEquals(DecisaoEmprestimo.DADOS_INVALIDOS, decisaoRenda0);
        assertEquals(DecisaoEmprestimo.DADOS_INVALIDOS, decisaoValor0);
        assertEquals(DecisaoEmprestimo.DADOS_INVALIDOS, decisaoParcelaMenor1);
        assertEquals(DecisaoEmprestimo.DADOS_INVALIDOS, decisaoParcelaMaior360);

    }

    @Test 
    void scoreMenor300DeveSerNegadoENao() {
        // ============ Act ============ //

        // score < 300 -> NEGADO
        DecisaoEmprestimo decisaoScore299 = analisador.analisar(299, 2000.0, 10000.0, 12);
        DecisaoEmprestimo decisaoScore250 = analisador.analisar(250, 5000.0, 10000.0, 24);

        // ============ Assert ============ \\
        assertEquals(DecisaoEmprestimo.NEGADO, decisaoScore299);
        assertEquals(DecisaoEmprestimo.NEGADO, decisaoScore250);
    }

    @Test 
    void score800OuMaiorTaxaJurosAnualDeveSer12PorCento(){
        // ============ Act ============ //
        // score == 800 -> 12%
        double taxaScore800 = analisador.taxaAnual(800);
        // score > 800 -> 12%
        double taxaScoreMaior1000 = analisador.taxaAnual(1000);
        // score < 800 -> diferente 12%
        double taxaScoreMenor800 = analisador.taxaAnual(799);

        // ============ Assert ============ \\
        assertEquals(0.12, taxaScore800, DELTA);
        assertEquals(0.12, taxaScoreMaior1000, DELTA);
        assertNotEquals(0.12, taxaScoreMenor800, DELTA);

    }

    @Test 
    void score600OuMaiorTaxaJurosAnualDeveSer24PorCento(){
        // ============ Act ============ //
        // score == 600 -> taxa anual de 24%
        double taxaScore600 = analisador.taxaAnual(600);
        // score > 600 -> taxa anual de 24%
        double taxaScoreMaior600 = analisador.taxaAnual(799);

        // ============ Assert ============ \\
        assertEquals(0.24, taxaScore600, DELTA);
        assertEquals(0.24, taxaScoreMaior600, DELTA);
        
    }

    @Test 
    void scoreMenor600TaxaJurosAnualDeveSer36PorCento(){
        // ============ Act ============ //
        // score < 600 -> 36%
        double taxaScoreMenor600 = analisador.taxaAnual(599);

        // ============ Assert ============ \\
        assertEquals(0.36, taxaScoreMenor600, DELTA);

    }

    @Test 
    void calculaParcelaComTaxaAnualDe12PorCentoValor1000E10Parcelas() {
        // ============ Arrange / Act ============ //
        // score == 800 -> taxa anual de 12% (1% ao mês)
        double taxaMensalScore800 = analisador.taxaAnual(800)/12;
        double parcelaScore800Taxa12 = analisador.calcularParcela(1000, taxaMensalScore800, 10);
        // score < 800 -> taxa anual de 24% (2% ao mês)
        double taxaMensalScoreMenor800 = analisador.taxaAnual(799)/12;
        double parcelaScoreMenor800 = analisador.calcularParcela(1000, taxaMensalScoreMenor800, 10);

        // ============ Assert ============ \\
        assertEquals(105.582,parcelaScore800Taxa12, DELTA);
        assertNotEquals(105.582, parcelaScoreMenor800, DELTA);
    }

    @Test 
    void calculaParcelaComTaxaAnualDe24PorCentoValor1000E10Parcelas() {
        // ============ Arrange / Act ============ //
        // Score >= 600 e < 800 -> taxa anual de 24% (2% ao mês)
        double taxaMensalScore600 = analisador.taxaAnual(600)/12;
        double parcelaScore600Taxa24 = analisador.calcularParcela(1000.0, taxaMensalScore600, 10);
        // Score > 600 e < 800 -> mesma faixa de taxa
        double taxaMensalScoreMaior600 = analisador.taxaAnual(799)/12;
        double parcelaScoreMaior600Taxa24 = analisador.calcularParcela(1000.0, taxaMensalScoreMaior600, 10);
        // Score < 600 -> taxa anual de 36% (3% ao mês)
        double taxaMensalScoreMenor600 = analisador.taxaAnual(599)/12;
        double parcelaScoreMenor600 = analisador.calcularParcela(1000.0, taxaMensalScoreMenor600, 10);

        // ============ Assert ============ \\
        assertEquals(111.326,parcelaScore600Taxa24, DELTA);
        assertEquals(111.326, parcelaScoreMaior600Taxa24, DELTA);
        assertNotEquals(111.326, parcelaScoreMenor600, DELTA);
    }

    @Test 
    void calculaParcelaComTaxaAnualDe36PorCentoValor1000E10Parcelas() {
        // ============ Arrange / Act ============ //
        // Score < 600> -> taxa anual de 36% (3% ao mês)
        double taxaMensalScoreMenor600 = analisador.taxaAnual(599)/12;
        double parcelaScoreMenor600Taxa36 = analisador.calcularParcela(1000.0, taxaMensalScoreMenor600, 10);

        // ============ Assert ============ \\
        assertEquals(117.230,parcelaScoreMenor600Taxa36, DELTA);
    }

    @Test 
    void comprometimentoMaior50PorCentoDeveNegarEmprestimo() {
        // ============ Arrange / Act ============ //
        DecisaoEmprestimo decisao = analisador.analisar(800, 1000.0, 10000.0, 12);

        double taxaMensal = analisador.taxaAnual(800) / 12;
        double parcela = analisador.calcularParcela(10_000.0, taxaMensal, 12);
        double comprometimento = parcela / 1000.0;

        // ============ Assert ============ \\
        assertEquals(DecisaoEmprestimo.NEGADO, decisao);
        assertEquals(0.88849, comprometimento, DELTA);
    }

    @Test
    void comprometimentoEntre30E50PorCentoDeveExigirAnaliseManual() {
        // ============ Arrange / Act ============ //
        DecisaoEmprestimo decisao = analisador.analisar(800, 2000.0, 10000.0, 12);

        double taxaMensal = analisador.taxaAnual(800) / 12.0;
        double parcela = analisador.calcularParcela(10000.0, taxaMensal, 12);
        double comprometimento = parcela / 2000.0;

        // ============ Assert ============ \\
        assertEquals(DecisaoEmprestimo.ANALISE_MANUAL, decisao);
        assertEquals(0.44424, comprometimento, DELTA);
    }

    @Test
    void comprometimentoAte30PorCentoEScoreMenor600DeveExigirAnaliseManual() {
        // ============ Arrange / Act ============ //
        DecisaoEmprestimo decisao = analisador.analisar(500, 5000.0, 10000.0, 36);

        double taxaMensal = analisador.taxaAnual(500) / 12.0;
        double parcela = analisador.calcularParcela(10000.0, taxaMensal, 36);
        double comprometimento = parcela / 5000.0;

        // ============ Assert ============ \\
        assertEquals(DecisaoEmprestimo.ANALISE_MANUAL, decisao);
        assertEquals(0.09161, comprometimento, DELTA);
    }


    @Test
    void comprometimentoAbaixo30PorCentoEScore800DeveAprovar() {
        // ============ Arrange / Act ============ //
        DecisaoEmprestimo decisao = analisador.analisar(800, 3000.0, 10000.0, 12);

        double taxaMensal = analisador.taxaAnual(800) / 12.0;
        double parcela = analisador.calcularParcela(10000.0, taxaMensal, 12);
        double comprometimento = parcela / 3000.0;

        // ============ Assert ============ \\
        assertEquals(DecisaoEmprestimo.APROVADO, decisao);
        assertEquals(0.29616, comprometimento, DELTA);
    }


    @Test
    void comprometimentoAbaixo30PorCentoEScore600DeveAprovar() {
        // ============ Arrange / Act ============ //
        DecisaoEmprestimo decisao = analisador.analisar(600, 5000.0, 10000.0, 12);

        double taxaMensal = analisador.taxaAnual(600) / 12.0;
        double parcela = analisador.calcularParcela(10000.0, taxaMensal, 12);
        double comprometimento = parcela / 5000.0;

        // ============ Assert ============ \\
        assertEquals(DecisaoEmprestimo.APROVADO, decisao);
        assertEquals(0.18912, comprometimento, DELTA);
    }


}
