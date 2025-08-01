export const formatCurrency = (value: number | undefined): string => {
    if (value === undefined || value === null) {
        return '';
    }
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    }).format(value);
};