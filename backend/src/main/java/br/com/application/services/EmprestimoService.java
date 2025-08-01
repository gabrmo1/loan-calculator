package br.com.application.services;

import br.com.application.models.dtos.CalcularEmprestimoRequest;
import br.com.application.models.dtos.DetalhesEmprestimo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.com.application.utils.DateUtils.ultimoDiaDoMes;
import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final CalculoService calculoService;

    public List<DetalhesEmprestimo> detalharEmprestimo(CalcularEmprestimoRequest request) {
        final var datasPagamento = calculoService.calcularDiasDePagamento(request.dataPrimeiroPagamento(), request.dataFinal());
        final var taxaJuros = (double) request.taxaJuros() / 100;
        return detalharPrestacoes(request.dataInicial(), datasPagamento, request.valorEmprestimo(), taxaJuros);
    }


    private List<DetalhesEmprestimo> detalharPrestacoes(LocalDate dataInicial, List<LocalDate> datasPagamento, BigDecimal valorTotal, double taxaJuros) {
        var listaDetalhes = new ArrayList<DetalhesEmprestimo>();

        listaDetalhes.add(new DetalhesEmprestimo(dataInicial, valorTotal, valorTotal, "", ZERO, ZERO, valorTotal, ZERO, ZERO, ZERO));

        var prestacaoAtual = 1;
        final var qtdPrestacoesTotais = datasPagamento.size();
        DetalhesEmprestimo detalhesCalculoAnterior = listaDetalhes.getFirst();

        for (LocalDate dataPagamento : datasPagamento) {

            if (!ultimoDiaDoMes(detalhesCalculoAnterior.dataCompetencia()).equals(detalhesCalculoAnterior.dataCompetencia()) && !ultimoDiaDoMes(detalhesCalculoAnterior.dataCompetencia()).isAfter(dataPagamento)) {
                listaDetalhes.add(calculoService.montarCalculoJurosFimDoMes(detalhesCalculoAnterior, taxaJuros));
                detalhesCalculoAnterior = listaDetalhes.getLast();
            }

            listaDetalhes.add(calculoService.montarCalculoJurosDataPagamento(detalhesCalculoAnterior, dataPagamento, valorTotal, qtdPrestacoesTotais, prestacaoAtual, taxaJuros));
            detalhesCalculoAnterior = listaDetalhes.getLast();
            prestacaoAtual++;
        }

        return listaDetalhes;
    }

}