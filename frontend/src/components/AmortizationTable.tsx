import React from 'react';
import { AmortizationEntry } from '../types';
import './AmortizationTable.scss';
import {formatCurrency} from "./utils/TextFormatter";

interface AmortizationTableProps {
    schedule: AmortizationEntry[];
}

export const AmortizationTable: React.FC<AmortizationTableProps> = ({ schedule }) => {
    return (
        <div className="amortization-table-container">
            <h2>Cronograma de Amortização</h2>
            <div className="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>Data Competência</th>
                        <th>Valor de Empréstimo</th>
                        <th>Saldo Devedor</th>
                        <th>Consolidada</th>
                        <th>Total</th>
                        <th>Amortização</th>
                        <th>Saldo</th>
                        <th>Provisão</th>
                        <th>Acumulado</th>
                        <th>Pago</th>
                    </tr>
                    </thead>
                    <tbody>
                    {schedule.map((entry, index) => (
                        <tr key={index}>
                            <td>{entry.dataCompetencia}</td>
                            <td>{formatCurrency(entry.valorEmprestimo)}</td>
                            <td>{formatCurrency(entry.saldoDevedor)}</td>
                            <td>{entry.consolidada || ''}</td>
                            <td>{formatCurrency(entry.total)}</td>
                            <td>{formatCurrency(entry.amortizacao)}</td>
                            <td>{formatCurrency(entry.saldo)}</td>
                            <td>{formatCurrency(entry.provisao)}</td>
                            <td>{formatCurrency(entry.acumulado)}</td>
                            <td>{formatCurrency(entry.pago)}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};