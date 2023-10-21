package io.sim;

import java.io.DataOutputStream;
import java.io.IOException;


public class BotPayment extends Thread{
    
    private double valor;
    private int conta_paga;
    private int conta_recebe;
    private DataOutputStream saida;
    private String login;
    private String senha;

    // Classe que reune dados de transação e as envia para o servidor do banco
    public BotPayment(DataOutputStream saida, int conta_paga, String login, String senha, int conta_recebe,  double valor){

        this.valor = valor;
        this.saida = saida;
        this.conta_paga = conta_paga;
        this.login = login;
        this.senha = senha;
        this.conta_recebe = conta_recebe;

    }
    @Override
    public void run(){
      try {
        
        String dadosTransacao = JSON.dadosTrasacao2JSON(login, senha, conta_paga, conta_recebe, valor);
        
        byte[] cripto = Criptografia.encrypt(dadosTransacao);
        
        saida.writeInt(cripto.length);
        saida.write(cripto);

      } catch (IOException e) {
          e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
    }
 
    }
    
}

