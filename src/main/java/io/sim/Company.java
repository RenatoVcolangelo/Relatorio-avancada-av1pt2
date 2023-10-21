package io.sim;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.json.JSONObject;

//Classe da Mobility Company responsável por criar conexao com os carros e o banco

public class Company extends Thread{

    public static boolean ativo = true;

    private ArrayList<Rota> naoExecutadas; 
    private ArrayList<Rota> emExecucao;
    private ArrayList<Rota> Executadas;
    private Conta conta;
    private int totalDrivers;
    private int porta;
    private ServerSocket serverSocket;
    private Socket bancoSocket;
    public static ArrayList<DrivingData> reportData = new ArrayList<DrivingData>(0);
    public ArrayList<ThreadCompany> threads = new ArrayList<ThreadCompany>();

    public Company(ArrayList<Rota> rotasNexe,int totalDrivers, int porta) throws IOException{

        this.naoExecutadas = rotasNexe;
        this.totalDrivers = totalDrivers;
        this.porta = porta;

        emExecucao = new ArrayList<Rota>();
        Executadas = new ArrayList<Rota>();
        conta = new Conta(500000,0,"12345");

        this.serverSocket = new ServerSocket(porta);  // Porta do servidor
              
    }

    @Override
    public void run(){

        System.out.println("Servidor Company criado");
        // Faz o relatorio em tempo real dos dados dos carros
        Relatorio.criaExcelAuto();
        ExcelCompany excel = new ExcelCompany(this);
        excel.start();
   
        try {

            this.bancoSocket = new Socket("127.0.0.1",22222); // Conexao com o banco

            for(int i = 0; i < totalDrivers; i++){

                Socket  socketCarro = serverSocket.accept(); // Espera por uma conexão com auto
        
                ThreadCompany thread = new ThreadCompany(socketCarro, bancoSocket, this);
                threads.add(thread);
                thread.start();
                
           }
           
            
           for(ThreadCompany t:threads){
            t.join();
            }
            
            // Informa a thread de conexão do banco para fechar JSON/Criptografia
            DataOutputStream saida = new DataOutputStream(bancoSocket.getOutputStream());
            JSONObject obj = new JSONObject();
            obj.put("acabou","true");
            byte[] cripto = Criptografia.encrypt(obj.toString());
			saida.writeInt(cripto.length);
			saida.write(cripto);
        

            this.serverSocket.close();
        
            ativo = false; 
            excel.join();
        
            System.out.println("COMPANY OFF");

         } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
             

    }


    public synchronized void addRelatorio(DrivingData d){
        reportData.add(d);
    }

    public Conta getConta(){
        return conta;
    }

    // retorna uma rota de nExecutada e também a adc emExecucao
    public synchronized Rota getRoute(){

        Rota rota = naoExecutadas.remove(0);
        emExecucao.add(rota);
        return rota;
    }

    // quando auto finaliza uma rota, manda o id da route e ela é movida para Executadas
    public synchronized void routeExecutada(String i){
        for(Rota rota: emExecucao){
            if(rota.getId().equals(i)){
                Executadas.add(rota);
                emExecucao.remove(rota);
                return;
            }
        }
        
    }

    public ArrayList<Rota> getRoutesNExe(){
        return naoExecutadas;
    }

    public ArrayList<Rota> getRoutesExe(){
        return emExecucao;
    }




    
}
