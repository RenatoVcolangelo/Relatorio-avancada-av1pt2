package io.sim;

import java.sql.Timestamp;

// Reune os dados de uma transação 
public class Transacao {

    private int contaPagador;
    private int contaRecebedor;
    private double valor;
    private Timestamp timestamp1;
    
    public Transacao(int contaPagador, int contaRecebedor, double valor, Timestamp timestamp){
        this.contaPagador = contaPagador;
        this.contaRecebedor = contaRecebedor;
        this.valor = valor;
        this.timestamp1 = timestamp;
    }

    public int getContaPagador(){
        return contaPagador;
    }

    public int getContaRecebedor(){
        return contaRecebedor;
    }

    public double getValor(){
        return valor;
    }

    public String getTimestamp(){
        return timestamp1.toString();
    }

}
