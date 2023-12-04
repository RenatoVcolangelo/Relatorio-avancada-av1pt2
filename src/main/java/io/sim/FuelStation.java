package io.sim;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import org.json.JSONObject;
    

// Classe do posto de combustivel, possui 2 bombas, limitado a 2 carros
// Obrigatorio usar semaphore 

public class FuelStation extends Thread{

    private static final int MAX_CARROS = 2;
    private Socket socket;
    private static Semaphore semaforo = new Semaphore(MAX_CARROS);
    private DataOutputStream saida; 
    private Conta conta;

    public FuelStation() {
        
    }

    @Override

    // Cria a conexão com o banco, mas como a fuel station nao faz pagamentos a conexão ja é fechada
    public void run(){
        // long t0 = System.nanoTime();
        // System.out.println("Inicio execucao Posto = " + t0);

        try {
            this.socket = new Socket("127.0.0.1",22222); // conexão com o banco
            this.conta = new Conta(10000, 1, "12345");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Posto on");
            
            // Envia mensagem indicando para fechar a conexão
            JSONObject obj = new JSONObject();
            obj.put("acabou","true");

            DataOutputStream saida;
            try{
                Thread.sleep(500);
                saida = new DataOutputStream(socket.getOutputStream());
                byte[] cripto = Criptografia.encrypt(obj.toString());
                saida.writeInt(cripto.length);
                saida.write(cripto);

                socket.close();
                
            } catch (Exception e){

            }
        System.out.println("Posto oFF");
        // long t1 = System.nanoTime();
        // System.out.println("Fim da execucao Posto = " + t1);


    }

    public Conta getConta(){
        return conta;
    }

    // Metodo de abastecimento, recebe no max 2 carros
    public static void abastecerCarro(Auto auto, double qtd) {
        try {
            semaforo.acquire(); // aceita a threda
            System.out.println(auto.getIdAuto() + " está sendo atendido.");
            Thread.sleep(5000); // Simula o tempo de abastecimento 2min
            auto.setFuelTank(qtd); // adciona combustivel no carro
            System.out.println(auto.getIdAuto() + " abastecido");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforo.release(); // libera o carro
            auto.setAbastecer(false); // indica que ja esta abastecido
        }
    }
}
