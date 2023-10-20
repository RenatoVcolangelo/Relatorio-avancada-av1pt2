package io.sim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.JSONObject;


public class ThreadCompany extends Thread{
    public static boolean acabou = true;
    private Socket carroSocket;
    private Socket bancoSocket;
    private Company company;
    String timestamp;
    String AutoID;
    String RouteID;
    String Speed;
    String odometro;
    String autoState = "";
    String bateu1km = "";
    String routeID;
    String mensagem;

    private int contaDriver;
    private JSONObject saidarotas = new JSONObject();

    private boolean on_off = true;

    
    public ThreadCompany (Socket carroSocket, Socket bancoSocket, Company company) { 
        this.carroSocket = carroSocket;
        this.bancoSocket = bancoSocket;
        this.company = company;

    }
    @Override
    public void run(){

            try {

                DataOutputStream saida = new DataOutputStream(carroSocket.getOutputStream());
                DataInputStream entrada = new DataInputStream(carroSocket.getInputStream());
                DataOutputStream saidaBanco = new DataOutputStream(bancoSocket.getOutputStream());

            
            while(on_off){

                int tam = entrada.readInt();  
                byte[] cripto = entrada.readNBytes(tam);
                String mensagem = Criptografia.decrypt(cripto);

            
                JSONObject obj = new JSONObject(mensagem);

                autoState = obj.getString("autoState");


                if(autoState.equals("esperando")){
                    if(this.company.getRoutesNExe().size() > 0){
                    
                        Rota r = company.getRoute();
                        saidarotas.put("IDRoute",r.getId());
                        saidarotas.put("Edges",r.getEdge());
                        cripto = Criptografia.encrypt(saidarotas.toString());
        
                        saida.writeInt(cripto.length);
                        saida.write(cripto);

                        //saida.writeUTF(saidarotas.toString());
                        System.out.println("Rota enviada");}

                    else{
                        saidarotas.put("RotasFinalizadas","true");

                        cripto = Criptografia.encrypt(saidarotas.toString());
        
                        saida.writeInt(cripto.length);
                        saida.write(cripto);
                        
                    }
                }


                else if(autoState.equals("rotaFinalizada")){

                    routeID = obj.getString("routeId");
                    this.company.routeExecutada(routeID);
                    System.out.println("Rota " + routeID + " concluida");

                }

                else if(autoState.equals("executando")){

                
                    this.company.addRelatorio(JSON.stringToDrivingData(mensagem));
                    bateu1km = obj.getString("bateu1km");
                    contaDriver = obj.getInt("contaDriver");

                    if(bateu1km.equals("bateu1km")){

                        BotPay bot = new BotPay(saidaBanco,this.company.getConta().getId(), this.company.getConta().getLogin(), this.company.getConta().getSenha(), contaDriver,3.25);
                        bot.start();
                    }
                }
                else if(autoState.equals("finalizado")){
                    on_off = false; 
                    entrada.close();
                    saida.close();
  
                }


            }

            System.out.println("Thread company finalizada");



        } catch (IOException ioe) {
            System.out.println("Erro: 1" + ioe.toString());
        } catch (Exception e) {

            e.printStackTrace();
        } 
        
        
    }

    
    
}
