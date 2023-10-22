package io.sim;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import org.json.JSONObject;

// Classe que possui o carro, gerencia abastecimento 
public class Driver extends Thread {
    
    private ArrayList<Rota> naoExecutadas;
    private Rota emExecucao; // apenas uma por vez
    private ArrayList<Rota> Executadas;
    private Auto auto;
    private Conta conta;
    private int id;
    private Socket socket;
    private DataOutputStream saida;


    public Driver(Auto auto, int id) throws IOException{
        this.id = id;
        this.auto = auto;
        this.conta = new Conta(100, id+1, "12345"); // cria conta
        this.socket = new Socket("127.0.0.1",22222); // conexao com banvo

    }

    @Override
    public void run(){
        try {
            // Objeto de comunicação com o banco
            saida = new DataOutputStream(this.socket.getOutputStream());

            Thread a1 = new Thread(auto);
            a1.start();
            abastecer();
            // quando carro finalizar, indica ao banco para finalizar a thread de comunicação
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
                    
                    FuelStation.abastecerCarro(this.auto, 7000);
                                       
                    BotPayment bot = new BotPayment(saida, this.conta.getId(), this.conta.getLogin(),this.conta.getSenha(),1,7*this.auto.getFuelPrice());
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
