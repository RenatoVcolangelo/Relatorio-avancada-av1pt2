package io.sim;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONObject;

import it.polito.appeal.traci.SumoTraciConnection;

public class Driver extends Thread {
    
    private ArrayList<Rota> naoExecutadas;
    private Rota emExecucao; // apenas uma por vez
    private ArrayList<Rota> Executadas;
    private Auto auto;
    private Conta conta;
    private int id;
    private SumoTraciConnection sumo;

    private Socket socket;
    private DataOutputStream saida;


    public Driver(Auto auto, int id, SumoTraciConnection sumo) throws UnknownHostException, IOException{
        this.id = id;
        this.auto = auto;
        this.conta = new Conta(100, id+1, "12345");
        this.sumo = sumo;

        this.socket = new Socket("127.0.0.1",22222);
        
        
    }

    @Override
    public void run(){
        try {
            saida = new DataOutputStream(this.socket.getOutputStream());

            Thread a1 = new Thread(auto);
            a1.start();
            abastecer();

            JSONObject obj = new JSONObject();
            obj.put("acabou","true");

            byte[] cripto = Criptografia.encrypt(obj.toString());
			saida.writeInt(cripto.length);
			saida.write(cripto);

            saida.close();
            socket.close();

            System.out.println("Driver " + this.id + " off");

        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        }
                
      
    private void abastecer() throws IOException{
        try {
          while(!this.auto.getFinalizado()){

            naoExecutadas = this.auto.getNaoExecutadas();
            emExecucao = this.auto.getRota();
            Executadas = this.auto.getExecutadas();

                Thread.sleep(500);
            
                if(this.auto.getAbastecer()){ 
                    
                    // fuelStation.abastecerCarro(this.auto, 1000* this.conta.getSaldo()/5.87);
                    FuelStation.abastecerCarro(this.auto, 5000);
                                       
                    BotPay bot = new BotPay(saida, this.conta.getId(), this.conta.getLogin(),this.conta.getSenha(),1,30);
                    bot.start();
                                  
                    }
                
            }        

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }


    public Conta getConta(){
        return conta;
    }
    public Auto getAuto(){
        return auto;
    }
}
