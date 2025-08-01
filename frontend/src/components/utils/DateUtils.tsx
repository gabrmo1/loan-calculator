export function formatarData(data: Date,
                             diasParaAdicionar: number | null = null,
                             mesesParaAdicionar: number | null = null,
                             anosParaAdicionar: number | null = null): string {
    const dataModificada = new Date(data);

    if (anosParaAdicionar !== null) dataModificada.setFullYear(dataModificada.getFullYear() + anosParaAdicionar);
    if (mesesParaAdicionar !== null) dataModificada.setMonth(dataModificada.getMonth() + mesesParaAdicionar);
    if (diasParaAdicionar !== null) dataModificada.setDate(dataModificada.getDate() + diasParaAdicionar);

    const year = dataModificada.getFullYear();
    const month = String(dataModificada.getMonth() + 1).padStart(2, '0');
    const day = String(dataModificada.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}