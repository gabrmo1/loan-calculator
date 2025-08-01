import React, {useState} from 'react';
import {AmortizationEntry, LoanParameters} from '../types';
import './LoanInputForm.scss';
import {formatarData} from "./utils/DateUtils";

interface LoanInputFormProps {
    onCalcular: (params: LoanParameters, schedule: AmortizationEntry[]) => void;
}

export const LoanInputForm: React.FC<LoanInputFormProps> = ({onCalcular}) => {
    const dataAtual = new Date();
    const [dataInicial, setDataInicial] = useState<string>(formatarData(dataAtual));
    const [dataFinal, setDataFinal] = useState<string>(formatarData(dataAtual, null, 120, null));
    const [primeiroPagamento, setPrimeiroPagamento] = useState<string>(formatarData(dataAtual, 45, null, null));
    const [valorEmprestimo, setValorEmprestimo] = useState<number>(140000);
    const [taxaJuros, setTaxaJuros] = useState<number>(7);

    const [erros, setErros] = useState<{ [key: string]: string | null }>({});
    const [carregando, setCarregando] = useState<boolean>(false);
    const [erroApi, setErroApi] = useState<string | null>(null);

    const formularioValido = () => {
        let errosFormulario: { [key: string]: string | null } = {};

        const dataInicio = new Date(dataInicial);
        const dataFim = new Date(dataFinal);
        const dataPrimeiroPagamento = new Date(primeiroPagamento);

        if (valorEmprestimo <= 0) errosFormulario.valorEmprestimo = 'Valor do Empréstimo deve ser positivo.';
        if (taxaJuros <= 0) errosFormulario.taxaJuros = 'Taxa de Juros deve ser positiva.';

        if (dataInicial && dataFinal && dataFim <= dataInicio)
            errosFormulario.dataFinal = 'A Data Final deve ser maior que a Data Inicial.';
        else
            errosFormulario.dataFinal = null;

        if (dataInicial && dataFinal && primeiroPagamento) {
            if (dataPrimeiroPagamento < dataInicio || dataPrimeiroPagamento > dataFim)
                errosFormulario.primeiroPagamento = 'O Primeiro Pagamento deve estar entre a Data Inicial e a Data Final.';
            else
                errosFormulario.primeiroPagamento = null;
        }

        setErros(errosFormulario);

        return Object.values(errosFormulario).every(error => error === null);
    };

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setErroApi(null);
        setErros({});

        if (!formularioValido()) return;

        const params: LoanParameters = {
            dataInicial,
            dataFinal,
            dataPrimeiroPagamento: primeiroPagamento,
            valorEmprestimo,
            taxaJuros: taxaJuros
        };

        setCarregando(true);

        try {
            const response = await fetch('http://localhost:8080/v1/emprestimos/calcular-prestacoes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(params),
            });

            if (!response.ok) {
                const errorData = await response.json();
                const errorMessage = errorData.message || `Erro no servidor: ${response.status} ${response.statusText}`;
                setErroApi(errorMessage);
                console.error('Erro na requisição:', errorMessage);
                return;
            }

            const schedule: AmortizationEntry[] = await response.json();
            onCalcular(params, schedule);
        } catch (error) {
            console.error('Erro ao conectar com o backend:', error);
            setErroApi('Não foi possível conectar ao servidor. Por favor, tente novamente mais tarde.');
        } finally {
            setCarregando(false);
        }
    };

    return (
        <div className="loan-input-form">
            <h2>Parâmetros do Empréstimo</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="dataInicial">Data Inicial:</label>
                        <input type="date" id="dataInicial" value={dataInicial}
                               onChange={(e) => setDataInicial(e.target.value)} required/>
                        {erros.dataInicial && <p className="error-message">{erros.dataInicial}</p>}
                    </div>
                    <div className="form-group">
                        <label htmlFor="dataFinal">Data Final:</label>
                        <input type="date" id="dataFinal" value={dataFinal}
                               onChange={(e) => setDataFinal(e.target.value)} required/>
                        {erros.dataFinal && <p className="error-message">{erros.dataFinal}</p>}
                    </div>
                    <div className="form-group">
                        <label htmlFor="primeiroPagamento">Primeiro Pagamento:</label>
                        <input type="date" id="primeiroPagamento" value={primeiroPagamento}
                               onChange={(e) => setPrimeiroPagamento(e.target.value)} required/>
                        {erros.primeiroPagamento && <p className="error-message">{erros.primeiroPagamento}</p>}
                    </div>
                    <div className="form-group">
                        <label htmlFor="valorEmprestimo">Valor do Empréstimo (R$):</label>
                        <input type="number" id="valorEmprestimo" value={valorEmprestimo}
                               onChange={(e) => setValorEmprestimo(parseFloat(e.target.value))} step="0.01" required
                               min="0.01"/>
                        {erros.valorEmprestimo && <p className="error-message">{erros.valorEmprestimo}</p>}
                    </div>
                    <div className="form-group">
                        <label htmlFor="taxaJuros">Taxa de Juros (%):</label>
                        <input type="number" id="taxaJuros" value={taxaJuros}
                               onChange={(e) => setTaxaJuros(parseFloat(e.target.value))} step="0.0001" required
                               min="1"/>
                        {erros.taxaJuros && <p className="error-message">{erros.taxaJuros}</p>}
                    </div>
                </div>
                {erroApi && <p className="api-error-message">{erroApi}</p>}
                <button type="submit" disabled={carregando}>
                    {carregando ? 'Calculando...' : 'Calcular Cronograma'}
                </button>
            </form>
        </div>
    );
};