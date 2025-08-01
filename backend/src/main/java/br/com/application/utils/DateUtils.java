package br.com.application.utils;

import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@UtilityClass
public class DateUtils {

    public static boolean isUltimoDiaDoMes(LocalDate date) {
        return date.getDayOfMonth() == date.lengthOfMonth();
    }


    public static LocalDate obterProximoDiaUtil(LocalDate date) {
        LocalDate dataRetorno = date;
        DayOfWeek diaDaSemana = dataRetorno.getDayOfWeek();

        while (diaDaSemana == DayOfWeek.SATURDAY || diaDaSemana == DayOfWeek.SUNDAY || isFeriado(dataRetorno)) {
            dataRetorno = dataRetorno.plusDays(1);
            diaDaSemana = dataRetorno.getDayOfWeek();
        }

        return dataRetorno;
    }


    private static final Set<MonthDay> FERIADOS_FIXOS = Set.of(
            MonthDay.of(1, 1),
            MonthDay.of(4, 21),
            MonthDay.of(5, 1),
            MonthDay.of(9, 7),
            MonthDay.of(10, 12),
            MonthDay.of(11, 2),
            MonthDay.of(11, 15),
            MonthDay.of(12, 25)
    );


    public static boolean isFeriado(LocalDate date) {
        if (FERIADOS_FIXOS.contains(MonthDay.from(date))) {
            return true;
        }

        int ano = date.getYear();
        LocalDate pascoa = calcularPascoa(ano);
        LocalDate sextaSanta = pascoa.minusDays(2);
        LocalDate carnaval = pascoa.minusDays(47);
        LocalDate corpusChristi = pascoa.plusDays(60);

        return date.equals(sextaSanta) ||
                date.equals(carnaval) ||
                date.equals(corpusChristi);
    }


    private static LocalDate calcularPascoa(int ano) {
        int cicloMetonico = ano % 19;
        int seculo = ano / 100;
        int restoSeculo = ano % 100;

        int ajusteSecular = seculo / 4;
        int restoAjusteSecular = seculo % 4;

        int correcaoAlemanha = (seculo + 8) / 25;
        int correcaoGregorian = (seculo - correcaoAlemanha + 1) / 3;

        int epacta = (19 * cicloMetonico + seculo - ajusteSecular - correcaoGregorian + 15) % 30;

        int bissextoSeculo = restoSeculo / 4;
        int restoBissexto = restoSeculo % 4;

        int diasSemana = (32 + 2 * restoAjusteSecular + 2 * bissextoSeculo - epacta - restoBissexto) % 7;

        int fatorCorrecao = (cicloMetonico + 11 * epacta + 22 * diasSemana) / 451;

        int mes = (epacta + diasSemana - 7 * fatorCorrecao + 114) / 31;
        int dia = ((epacta + diasSemana - 7 * fatorCorrecao + 114) % 31) + 1;

        return LocalDate.of(ano, mes, dia);
    }


    public static LocalDate ultimoDiaDoMes(LocalDate data) {
        return data.withDayOfMonth(data.lengthOfMonth());
    }


    public long calcularDiasEntreDatas(LocalDate data1, LocalDate data2) {
        if (data1.isAfter(data2))
            return -ChronoUnit.DAYS.between(data1, data2);
        return ChronoUnit.DAYS.between(data1, data2);
    }


}
