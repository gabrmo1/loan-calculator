import React, { useState } from 'react';
import './App.scss';
import { LoanInputForm } from './components/LoanInputForm';
import { AmortizationTable } from './components/AmortizationTable';
import { LoanParameters, AmortizationEntry } from './types';

function App() {
  const [loanParameters, setLoanParameters] = useState<LoanParameters | null>(null);
  const [amortizationSchedule, setAmortizationSchedule] = useState<AmortizationEntry[]>([]);

  const handleCalculate = (params: LoanParameters, schedule: AmortizationEntry[]) => {
    setLoanParameters(params);
    setAmortizationSchedule(schedule);
  };

  return (
      <div className="app-container">
        <header className="app-header">
          <h1>Calculadora de Empréstimos</h1>
        </header>
        <main className="app-main">
          <LoanInputForm onCalcular={handleCalculate} />
          {amortizationSchedule.length > 0 && (
              <AmortizationTable schedule={amortizationSchedule} />
          )}
        </main>
      </div>
  );
}

export default App;