package io.sim;

import java.util.List;

public class Estatistica {

    // Calcula a média dos valores
    public static double media(List<Double> dados) {
        if (dados == null || dados.isEmpty()) return Double.NaN;
        double soma = 0;
        for (double d : dados) {
            soma += d;
        }
        return soma / dados.size();
    }

    // Calcula o desvio padrão (amostral)
    public static double desvioPadrao(List<Double> dados) {
        if (dados == null || dados.size() < 2) return Double.NaN;
        double media = media(dados);
        double soma = 0;
        for (double d : dados) {
            soma += Math.pow(d - media, 2);
        }
        return Math.sqrt(soma / (dados.size() - 1));
    }

    // Calcula a polarização (bias) entre os dados e um valor de referência
    public static double polarizacao(List<Double> dados, double referencia) {
        if (dados == null || dados.isEmpty()) return Double.NaN;
        return media(dados) - referencia;
    }

    // Precisão pode ser interpretada como o desvio padrão neste contexto
    public static double precisao(List<Double> dados) {
        return desvioPadrao(dados);
    }

    // Incerteza combinada (desvio padrão / sqrt(n))
    public static double incerteza(List<Double> dados) {
        if (dados == null || dados.size() < 2) return Double.NaN;
        return desvioPadrao(dados) / Math.sqrt(dados.size());
    }

    // Converte uma lista de Double em um array primitivo (útil para interoperabilidade)
    public static double[] toArray(List<Double> lista) {
        double[] array = new double[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            array[i] = lista.get(i);
        }
        return array;
    }
}