package br.com.application.services;

import br.com.application.models.dtos.CalcularEmprestimoRequest;
import br.com.application.models.dtos.DetalhesEmprestimo;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmprestimoService Testes")
class EmprestimoServiceTest {

    @Mock
    private CalculoService calculoService;

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Test
    @DisplayName("Deve detalhar o empréstimo e incluir o cálculo de fim de mês no loop")
    void deveDetalharEmprestimo_comCalculoDeFimDeMes() {
        final var valorEmprestimo = BigDecimal.valueOf(140000);
        final var request = Instancio.of(CalcularEmprestimoRequest.class)
                .set(field("dataInicial"), LocalDate.of(2024, 1, 1))
                .set(field("dataFinal"), LocalDate.of(2024, 3, 1))
                .set(field("dataPrimeiroPagamento"), LocalDate.of(2024, 2, 1))
                .set(field("valorEmprestimo"), valorEmprestimo)
                .set(field("taxaJuros"), 7)
                .create();

        final var datasPagamentoEsperadas = List.of(
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 3, 1)
        );
        final var detalhesFimDeMes = new DetalhesEmprestimo(LocalDate.of(2024, 1, 31), ZERO, valorEmprestimo, "", ZERO, ZERO, valorEmprestimo, ZERO, ZERO, ZERO);
        final var detalhesPagamento = new DetalhesEmprestimo(LocalDate.of(2024, 2, 1), valorEmprestimo, valorEmprestimo, "1/2", ZERO, ZERO, valorEmprestimo, ZERO, ZERO, ZERO);

        when(calculoService.calcularDiasDePagamento(request.dataPrimeiroPagamento(), request.dataFinal()))
                .thenReturn(datasPagamentoEsperadas);
        when(calculoService.montarCalculoJurosFimDoMes(any(DetalhesEmprestimo.class), any(Double.class)))
                .thenReturn(detalhesFimDeMes);
        when(calculoService.montarCalculoJurosDataPagamento(any(DetalhesEmprestimo.class), any(LocalDate.class), any(BigDecimal.class), any(Long.class), any(Integer.class), any(Double.class)))
                .thenReturn(detalhesPagamento);

        final List<DetalhesEmprestimo> resultado = emprestimoService.detalharEmprestimo(request);

        verify(calculoService, times(2)).montarCalculoJurosFimDoMes(any(DetalhesEmprestimo.class), any(Double.class));
        verify(calculoService, times(2)).montarCalculoJurosDataPagamento(any(DetalhesEmprestimo.class), any(LocalDate.class), any(BigDecimal.class), any(Long.class), any(Integer.class), any(Double.class));

        assertEquals(5, resultado.size());
    }

}