package br.com.application.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalcularEmprestimoRequest(

        @NotNull
        LocalDate dataInicial,

        @NotNull
        LocalDate dataFinal,

        @NotNull
        LocalDate dataPrimeiroPagamento,

        @NotNull
        @Positive
        BigDecimal valorEmprestimo,

        @NotNull
        @Positive
        Integer taxaJuros

) {

    public CalcularEmprestimoRequest {
        if (dataFinal.isBefore(dataInicial))
            throw new IllegalArgumentException("A data final deve ser posterior à data inicial.");

        if (dataPrimeiroPagamento.isBefore(dataInicial) || dataPrimeiroPagamento.isAfter(dataFinal))
            throw new IllegalArgumentException("A data do primeiro pagamento deve estar entre a data inicial e a data final.");
    }

}
