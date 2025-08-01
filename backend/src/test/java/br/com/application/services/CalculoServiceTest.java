package br.com.application.services;

import br.com.application.models.dtos.DetalhesEmprestimo;
import br.com.application.utils.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculoService Testes")
class CalculoServiceTest {

    @InjectMocks
    private CalculoService calculoService;

    private MockedStatic<DateUtils> mockedDateUtils;

    private final static int ESCALA_DECIMAL = 4;

    @BeforeEach
    void setUp() {
        mockedDateUtils = mockStatic(DateUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedDateUtils.close();
    }

    @Test
    @DisplayName("Deve calcular dias de pagamento quando o primeiro pagamento não for o último dia do mês e a última data gerada for anterior à data final")
    void calcularDiasDePagamento_naoUltimoDia_ultimaAnterior() {
        LocalDate dataPrimeiroPagamento = LocalDate.of(2024, 1, 15);
        LocalDate dataFinal = LocalDate.of(2024, 2, 20);

        mockedDateUtils.when(() -> DateUtils.isUltimoDiaDoMes(dataPrimeiroPagamento)).thenReturn(false);
        mockedDateUtils.when(() -> DateUtils.obterProximoDiaUtil(LocalDate.of(2024, 1, 15))).thenReturn(LocalDate.of(2024, 1, 15));
        mockedDateUtils.when(() -> DateUtils.obterProximoDiaUtil(LocalDate.of(2024, 2, 15))).thenReturn(LocalDate.of(2024, 2, 15));

        List<LocalDate> resultado = calculoService.calcularDiasDePagamento(dataPrimeiroPagamento, dataFinal);

        List<LocalDate> datasEsperadas = List.of(
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 2, 15),
                dataFinal
        );

        assertEquals(datasEsperadas, resultado);
    }


    @Test
    @DisplayName("Deve calcular dias de pagamento quando o primeiro pagamento for o último dia do mês e a última data gerada for posterior à data final")
    void calcularDiasDePagamento_ultimoDia_ultimaPosterior() {
        LocalDate dataPrimeiroPagamento = LocalDate.of(2024, 1, 31);
        LocalDate dataFinal = LocalDate.of(2024, 2, 20);

        mockedDateUtils.when(() -> DateUtils.isUltimoDiaDoMes(dataPrimeiroPagamento)).thenReturn(true);
        mockedDateUtils.when(() -> DateUtils.ultimoDiaDoMes(LocalDate.of(2024, 1, 31))).thenReturn(LocalDate.of(2024, 1, 31));
        mockedDateUtils.when(() -> DateUtils.obterProximoDiaUtil(LocalDate.of(2024, 1, 31))).thenReturn(LocalDate.of(2024, 1, 31));
        mockedDateUtils.when(() -> DateUtils.ultimoDiaDoMes(LocalDate.of(2024, 2, 29))).thenReturn(LocalDate.of(2024, 2, 29));
        mockedDateUtils.when(() -> DateUtils.obterProximoDiaUtil(LocalDate.of(2024, 2, 29))).thenReturn(LocalDate.of(2024, 2, 29));

        List<LocalDate> resultado = calculoService.calcularDiasDePagamento(dataPrimeiroPagamento, dataFinal);
        List<LocalDate> datasEsperadas = List.of(LocalDate.of(2024, 1, 31), dataFinal);

        assertEquals(datasEsperadas, resultado);
    }


    @Test
    @DisplayName("Deve calcular a provisão corretamente")
    void calcularProvisao_deveRetornarValorCorreto() {
        double taxaJuros = 0.05;
        long distanciaDias = 30;
        BigDecimal saldoAnterior = BigDecimal.valueOf(100000);
        BigDecimal jurosAcumuladoAnterior = BigDecimal.valueOf(1000);

        BigDecimal resultadoEsperado = BigDecimal.valueOf(100000 + 1000)
                .multiply(BigDecimal.valueOf(Math.pow(1.05, 30.0 / 360) - 1))
                .setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);

        BigDecimal resultado = calculoService.calcularProvisao(taxaJuros, distanciaDias, saldoAnterior, jurosAcumuladoAnterior);

        assertEquals(resultadoEsperado, resultado);
    }


    @Test
    @DisplayName("Deve montar o cálculo de juros de fim de mês corretamente")
    void montarCalculoJurosFimDoMes_deveCalcularCorretamente() {
        LocalDate dataCompetencia = LocalDate.of(2024, 1, 15);
        DetalhesEmprestimo detalhesAnterior = new DetalhesEmprestimo(
                dataCompetencia, ZERO, BigDecimal.valueOf(100000), "1/1", ZERO, ZERO, BigDecimal.valueOf(100000), ZERO, ZERO, ZERO
        );
        double taxaJuros = 0.05;

        mockedDateUtils.when(() -> DateUtils.ultimoDiaDoMes(dataCompetencia)).thenReturn(LocalDate.of(2024, 1, 31));
        mockedDateUtils.when(() -> DateUtils.calcularDiasEntreDatas(dataCompetencia, LocalDate.of(2024, 1, 31))).thenReturn(16L);

        DetalhesEmprestimo resultado = calculoService.montarCalculoJurosFimDoMes(detalhesAnterior, taxaJuros);
        DetalhesEmprestimo esperado = new DetalhesEmprestimo(LocalDate.of(2024, 1, 31), ZERO, BigDecimal.valueOf(100217.0805), "", ZERO.setScale(ESCALA_DECIMAL, RoundingMode.UP), ZERO, BigDecimal.valueOf(100000.0000).setScale(ESCALA_DECIMAL, RoundingMode.UP), BigDecimal.valueOf(217.0805), BigDecimal.valueOf(217.0805), ZERO);

        assertEquals(esperado, resultado);
    }


    @Test
    @DisplayName("Deve montar o cálculo de juros de data de pagamento corretamente")
    void montarCalculoJurosDataPagamento_deveCalcularCorretamente() {
        LocalDate dataCompetencia = LocalDate.of(2024, 1, 31);
        DetalhesEmprestimo detalhesAnterior = new DetalhesEmprestimo(LocalDate.of(2024, 1, 15), ZERO, BigDecimal.valueOf(102222.2222), "", ZERO, ZERO, BigDecimal.valueOf(100000), BigDecimal.valueOf(2222.2222), BigDecimal.valueOf(2222.2222), ZERO);
        BigDecimal valorEmprestimoInicial = BigDecimal.valueOf(100000);
        long qtdPrestacoesTotais = 12;
        int prestacaoAtual = 1;
        double taxaJuros = 0.05;

        mockedDateUtils.when(() -> DateUtils.calcularDiasEntreDatas(detalhesAnterior.dataCompetencia(), dataCompetencia)).thenReturn(16L);

        DetalhesEmprestimo resultado = calculoService.montarCalculoJurosDataPagamento(detalhesAnterior, dataCompetencia, valorEmprestimoInicial, qtdPrestacoesTotais, prestacaoAtual, taxaJuros);
        DetalhesEmprestimo esperado = new DetalhesEmprestimo(dataCompetencia, ZERO, BigDecimal.valueOf(91666.6667).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP), "1/12", BigDecimal.valueOf(10777.4600).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP), BigDecimal.valueOf(8333.3333333333).setScale(10, RoundingMode.HALF_UP), BigDecimal.valueOf(91666.6667).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP), BigDecimal.valueOf(221.9045).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP), ZERO, BigDecimal.valueOf(2444.1267));

        assertEquals(esperado, resultado);
    }

}