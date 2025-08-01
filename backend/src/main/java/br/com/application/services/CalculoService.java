package br.com.application.services;

import br.com.application.models.dtos.DetalhesEmprestimo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.com.application.utils.DateUtils.*;
import static java.math.BigDecimal.ZERO;

@Service
public class CalculoService {

    private static final int BASE_DIAS = 360;
    private static final int ESCALA_DECIMAL = 4;

    public List<LocalDate> calcularDiasDePagamento(LocalDate dataPrimeiroPagamento, LocalDate dataFinal) {
        var datasPagamento = new ArrayList<LocalDate>();
        var dataPagamentoAtual = dataPrimeiroPagamento;

        while (!dataPagamentoAtual.isAfter(dataFinal)) {

            if (isUltimoDiaDoMes(dataPrimeiroPagamento)) {
                LocalDate ultimoDiaDoMes = ultimoDiaDoMes(dataPagamentoAtual);
                LocalDate dataPagamentoAjustada = obterProximoDiaUtil(ultimoDiaDoMes);
                datasPagamento.add(dataPagamentoAjustada);
            } else {
                datasPagamento.add(obterProximoDiaUtil(dataPagamentoAtual));
            }

            dataPagamentoAtual = dataPagamentoAtual.plusMonths(1);
        }

        final var ultimaDataGerada = datasPagamento.getLast();

        if (ultimaDataGerada.isAfter(dataFinal))
            datasPagamento.set(datasPagamento.size() - 1, dataFinal);
        else if (ultimaDataGerada.isBefore(dataFinal))
            datasPagamento.add(dataFinal);

        return datasPagamento;
    }


    public BigDecimal calcularProvisao(double taxaJuros, long distanciaDias, BigDecimal saldoAnterior, BigDecimal jurosAcumuladoAnterior) {
        return BigDecimal.valueOf(Math.pow(taxaJuros + 1, (double) distanciaDias / BASE_DIAS))
                .subtract(BigDecimal.ONE)
                .multiply(saldoAnterior.add(jurosAcumuladoAnterior))
                .setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);
    }

    public DetalhesEmprestimo montarCalculoJurosFimDoMes(DetalhesEmprestimo detalhesCalculoAnterior, double taxaJuros) {
        final var ultimoDiaDoMes = ultimoDiaDoMes(detalhesCalculoAnterior.dataCompetencia());
        final var distanciaDias = calcularDiasEntreDatas(detalhesCalculoAnterior.dataCompetencia(), ultimoDiaDoMes);
        final var amortizacao = ZERO;
        final var provisao = calcularProvisao(taxaJuros, distanciaDias, detalhesCalculoAnterior.saldo(), detalhesCalculoAnterior.acumulado());
        final var pago = ZERO;
        final var total = amortizacao.add(pago).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);
        final var saldo = detalhesCalculoAnterior.saldo().subtract(amortizacao).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);
        final var acumulado = detalhesCalculoAnterior.acumulado().add(provisao).subtract(pago).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);
        final var saldoDevedor = saldo.add(acumulado).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);

        return new DetalhesEmprestimo(ultimoDiaDoMes, ZERO, saldoDevedor, "", total, amortizacao, saldo, provisao, acumulado, pago);
    }


    public DetalhesEmprestimo montarCalculoJurosDataPagamento(DetalhesEmprestimo detalhesCalculoAnterior, LocalDate dataCompetencia,
                                                              BigDecimal valorEmprestimoInicial, long qtdPrestacoesTotais,
                                                              int prestacaoAtual, double taxaJuros) {
        final long distanciaDias = calcularDiasEntreDatas(detalhesCalculoAnterior.dataCompetencia(), dataCompetencia);
        final var amortizacao = valorEmprestimoInicial.divide(BigDecimal.valueOf(qtdPrestacoesTotais), 10, RoundingMode.HALF_UP);
        final var consolidada = prestacaoAtual + "/" + qtdPrestacoesTotais;
        final var provisao = calcularProvisao(taxaJuros, distanciaDias, detalhesCalculoAnterior.saldo(), detalhesCalculoAnterior.acumulado());
        final var pago = detalhesCalculoAnterior.acumulado().add(provisao).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);
        final var total = amortizacao.add(pago).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);
        final var saldo = detalhesCalculoAnterior.saldo().subtract(amortizacao).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);
        final var acumulado = ZERO;
        final var saldoDevedor = saldo.add(acumulado).setScale(ESCALA_DECIMAL, RoundingMode.HALF_UP);

        return new DetalhesEmprestimo(dataCompetencia, ZERO, saldoDevedor, consolidada, total, amortizacao, saldo, provisao, acumulado, pago);
    }

}
