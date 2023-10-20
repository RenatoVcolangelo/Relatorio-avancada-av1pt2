package io.sim;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import org.json.JSONObject;
    



public class FuelStation extends Thread{

    private static final int MAX_CARROS = 2;
    private DataOutputStream saida;
    private Socket socket;
    private static Semaphore semaforo = new Semaphore(MAX_CARROS);
    private Conta conta;


    public FuelStation() {
        
    }

    @Override
    public void run(){

        try {
            this.socket = new Socket("127.0.0.1",22222);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.conta = new Conta(10000, 1, "12345");
        System.out.println("Posto on");

        JSONObject obj = new JSONObject();
            obj.put("acabou","true");

            DataOutputStream saida;
            try{
                saida = new DataOutputStream(socket.getOutputStream());
                 byte[] cripto = Criptografia.encrypt(obj.toString());
                saida.writeInt(cripto.length);
                saida.write(cripto);
            } catch (Exception e){

            }

		System.out.println("Posto off");	

    }

    public Conta getConta(){
        return conta;
    }

    public static void abastecerCarro(Auto auto, double qtd) {
        try {
            semaforo.acquire();
            System.out.println(auto.getIdAuto() + " está sendo atendido.");
            Thread.sleep(10000); // Simula o tempo de abastecimento
            auto.setFuelTank(qtd);
            System.out.println(auto.getIdAuto() + " abastecido");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforo.release();
            auto.setAbastecer(false);
        }
    }
}
