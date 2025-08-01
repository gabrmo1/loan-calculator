export interface LoanParameters {
    dataInicial: string;
    dataFinal: string;
    dataPrimeiroPagamento: string;
    valorEmprestimo: number;
    taxaJuros: number;
}

export interface AmortizationEntry {
    dataCompetencia: string;
    valorEmprestimo?: number;
    saldoDevedor: number;
    consolidada?: string;
    total?: number;
    amortizacao?: number;
    saldo?: number;
    provisao?: number;
    acumulado?: number;
    pago?: number;
}