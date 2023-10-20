package io.sim;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

import it.polito.appeal.traci.SumoTraciConnection;

public class Company extends Thread{

    public static boolean ativo = true;

    private ArrayList<Rota> naoExecutadas; 
    private ArrayList<Rota> emExecucao;
    private ArrayList<Rota> Executadas;
    private Conta conta;
    private SumoTraciConnection sumo;
    private boolean on_off;
    private int totalDrivers;
    private int porta = 55555;
    private ServerSocket serverSocket;
    private Socket bancoSocket;
    public static ArrayList<DrivingData> reportData = new ArrayList<DrivingData>(0);
    public ArrayList<ThreadCompany> threads = new ArrayList<ThreadCompany>();
    private static String fileName = "Relatorio1.xlsx";

   

    public Company(ArrayList<Rota> rotasNexe,SumoTraciConnection sumo, int totalDrivers) throws IOException{

        this.naoExecutadas = rotasNexe;
        this.sumo = sumo;
        this.on_off = true;
        this.totalDrivers = totalDrivers;

        emExecucao = new ArrayList<Rota>();
        Executadas = new ArrayList<Rota>();
        conta = new Conta(500000,0,"12345");

        this.serverSocket = new ServerSocket(porta); // Porta do servidor
        
        
       
    }

    @Override
    public void run(){
        System.out.println("Servidor Company criado");
           
        try {
            this.bancoSocket = new Socket("127.0.0.1",22222); // Conexao com o banco
            for(int i = 0; i < totalDrivers;i++){

                Socket  socketCarro = serverSocket.accept(); // Espera por uma conexão
        
                ThreadCompany thread = new ThreadCompany(socketCarro, bancoSocket, this);
                threads.add(thread);
                thread.start();

                
           }

           
            
           for(ThreadCompany t:threads){
            t.join();
            
            JSONObject obj = new JSONObject();
            obj.put("acabou","true");

            DataOutputStream saida = new DataOutputStream(bancoSocket.getOutputStream());

            byte[] cripto = Criptografia.encrypt(obj.toString());
			saida.writeInt(cripto.length);
			saida.write(cripto);
        }

        this.serverSocket.close();
        
        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        

        System.out.println("Company off");

        // Relatorio re = new Relatorio(reportData);
        // re.start();
        ativo = false;

    }

    public static synchronized String getFilename(){
        return fileName;
    }

    public synchronized void addRelatorio(DrivingData d){
        reportData.add(d);
    }

    public Conta getConta(){
        return conta;
    }

    public synchronized Rota getRoute(){

        Rota rota = naoExecutadas.remove(0);
        emExecucao.add(rota);
        return rota;
    }

    public synchronized void routeExecutada(String i){
        for(Rota rota: emExecucao){
            if(rota.getId().equals(i)){
                Executadas.add(rota);
                emExecucao.remove(rota);
                return;
            }
        }
        
    }

     public  Rota getRota(int i){
        return naoExecutadas.get(i);
    }

    public ArrayList<Rota> getRoutesNExe(){
        return naoExecutadas;
    }

    public ArrayList<Rota> getRoutesExe(){
        return emExecucao;
    }
    public static boolean getAtivo(){
        return ativo;
    }



    
}
