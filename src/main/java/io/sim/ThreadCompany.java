package io.sim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.JSONObject;

// Gerencia a comunicação car/company, analisa o estado do carro e faz pagamentos
public class ThreadCompany extends Thread{

    private Socket carroSocket;
    private Socket bancoSocket;
    private Company company;
    private String autoState = "";
    private String bateu1km = "";
    private String routeID;
    private String mensagem;
    private int contaDriver;
    private JSONObject texto = new JSONObject();
    private boolean on_off = true;

    
    public ThreadCompany (Socket carroSocket, Socket bancoSocket, Company company) { 
        this.carroSocket = carroSocket;
        this.bancoSocket = bancoSocket;
        this.company = company;

    }
    @Override
    public void run(){

            try {
                // conecta com o carro e tem acesso a conexão da company com o banco
                DataOutputStream saida = new DataOutputStream(carroSocket.getOutputStream());
                DataInputStream entrada = new DataInputStream(carroSocket.getInputStream());
                DataOutputStream saidaBanco = new DataOutputStream(bancoSocket.getOutputStream());

            
            while(on_off){

            // recebe o tamanho da mensagem, a lê, descriptografa, poe em JSON e identifica o estado do carro
                int tam = entrada.readInt();  
                byte[] cripto = entrada.readNBytes(tam);
                mensagem = Criptografia.decrypt(cripto);
                JSONObject obj = new JSONObject(mensagem);

                autoState = obj.getString("autoState");

                // Se estiver esperando rota e existir rotas nao executadas a thread envia uma rota criptografada
                if(autoState.equals("esperando")){
                    if(this.company.getRoutesNExe().size() > 0){
                    
                        Rota r = company.getRoute();
                        texto.put("IDRoute",r.getId());
                        texto.put("Edges",r.getEdge());
                        cripto = Criptografia.encrypt(texto.toString());
        
                        saida.writeInt(cripto.length);
                        saida.write(cripto);

                        System.out.println("Rota enviada");}

                    // caso nao exista rotas a serem executadas, indica ao carro para finalizar
                    else{
                        texto.put("RotasFinalizadas","true");

                        cripto = Criptografia.encrypt(texto.toString());
        
                        saida.writeInt(cripto.length);
                        saida.write(cripto);
                        
                    }
                }

                // quando um carro finaliza uma rota, indica a company para atualizar suas listas de rotas
                else if(autoState.equals("rotaFinalizada")){

                    routeID = obj.getString("routeId");
                    this.company.routeExecutada(routeID);
                    System.out.println("Rota " + routeID + " concluida");

                }
                // quando esta executando, confere se o carro bateu 1km e precisa ser pago
                // também recebe um DataDriving do carro e o adiciona ao arraylist da company
                else if(autoState.equals("executando")){

                
                    this.company.addRelatorio(JSON.stringToDrivingData(mensagem));
                    bateu1km = obj.getString("bateu1km");
                    contaDriver = obj.getInt("contaDriver");

                    if(bateu1km.equals("bateu1km")){

                        BotPayment bot = new BotPayment(saidaBanco,this.company.getConta().getId(), this.company.getConta().getLogin(), this.company.getConta().getSenha(), contaDriver,3.25);
                        bot.start();
                    }
                }
                // carro recebe info para fechar, e retorna confirmação
                else if(autoState.equals("finalizado")){
                    on_off = false; 
                    entrada.close();
                    saida.close();
  
                }


            }


        } catch (IOException ioe) {
            System.out.println("Erro: 1" + ioe.toString());
        } catch (Exception e) {

            e.printStackTrace();
        } 
        
        
    }

    
    
}
