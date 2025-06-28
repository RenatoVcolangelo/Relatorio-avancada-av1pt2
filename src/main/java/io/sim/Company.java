package io.sim;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.json.JSONObject;

// Classe da Mobility Company responsável por criar conexao com os carros e o banco
public class Company extends Thread {

    public static boolean ativo = true; // Indica se a Company ainda está ativa

    private ArrayList<Rota> naoExecutadas; // Rotas disponíveis, ainda não atribuídas
    private ArrayList<Rota> emExecucao;    // Rotas que já foram atribuídas a carros e estão em andamento
    private ArrayList<Rota> Executadas;    // Rotas concluídas
    private Conta conta;                   // Conta da empresa (Company)
    private int totalDrivers;             // Número total de carros/motoristas
    private int porta;                    // Porta do servidor da Company
    private ServerSocket serverSocket;    // Socket que aguarda conexão dos carros
    private Socket bancoSocket;           // Socket usado para se comunicar com o banco
    public static ArrayList<DrivingData> reportData = new ArrayList<DrivingData>(0); // Lista de dados de condução enviados pelos carros
    public ArrayList<ThreadCompany> threads = new ArrayList<ThreadCompany>(); // Lista de threads de comunicação com os carros

    public Company(ArrayList<Rota> rotasNexe, int totalDrivers, int porta) throws IOException {

        this.naoExecutadas = rotasNexe; // recebe um arraylist de rotas Itinerary i1.getRoutes()
        this.totalDrivers = totalDrivers;
        this.porta = porta;

        emExecucao = new ArrayList<Rota>();
        Executadas = new ArrayList<Rota>();
        conta = new Conta(500000, 0, "12345"); // Cria a conta da Company com saldo inicial

        this.serverSocket = new ServerSocket(porta);  // Porta do servidor
    }

    @Override
    public void run() {
        // long t0 = System.nanoTime();
        // System.out.println("Fila company = " + t0);

        System.out.println("Servidor Company criado");

        // Faz o relatorio em tempo real dos dados dos carros
        Relatorio.criaExcelAuto();

        ExcelCompany excel = new ExcelCompany(this);
        // long t0 = System.nanoTime();
        // System.out.println("start excel = " + t0);
        excel.start();

        try {

            this.bancoSocket = new Socket("127.0.0.1", 22222); // Conexao com o banco

            for (int i = 0; i < totalDrivers; i++) {

                Socket socketCarro = serverSocket.accept(); // Espera por uma conexão com auto

                ThreadCompany thread = new ThreadCompany(socketCarro, bancoSocket, this);
                threads.add(thread);
                // long t0 = System.nanoTime();
                // System.out.println("start thread" + i + " = " + t0);
                thread.start(); // Inicia a thread que gerencia a comunicação com o carro
            }

            // long t1 = System.nanoTime();
            // System.out.println("fim company = " + t1);

            // espera as conexões fecharem
            for (ThreadCompany t : threads) {
                t.join(); // Aguarda cada thread encerrar
            }

            // Informa a thread de conexão do banco para fechar JSON/Criptografia
            DataOutputStream saida = new DataOutputStream(bancoSocket.getOutputStream());
            JSONObject obj = new JSONObject();
            obj.put("acabou", "true");
            byte[] cripto = Criptografia.encrypt(obj.toString());
            saida.writeInt(cripto.length);
            saida.write(cripto);

            this.serverSocket.close();

            ativo = false; 
            excel.join(); // Aguarda a thread que salva os dados no Excel encerrar

            System.out.println("COMPANY OFF");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adiciona um objeto DrivingData à lista de dados para relatório
    public synchronized void addRelatorio(DrivingData d) {
        reportData.add(d);
    }

    public Conta getConta() {
        return conta;
    }

    // remove uma rota de naoExecutadas e a retorna; também a adiciona em emExecucao
    public synchronized Rota getRoute() {
        Rota rota = naoExecutadas.remove(0);
        // Rota rota = naoExecutadas.get(0);
        emExecucao.add(rota);
        return rota;
    }

    // quando auto finaliza uma rota, manda o id da route e ela é movida para Executadas
    public synchronized void routeExecutada(String i) {
        for (Rota rota : emExecucao) {
            if (rota.getId().equals(i)) {
                Executadas.add(rota);
                emExecucao.remove(rota);
                return;
            }
        }
    }

    public ArrayList<Rota> getRoutesNExe() {
        return naoExecutadas;
    }

    public ArrayList<Rota> getRoutesExe() {
        return emExecucao;
    }

}
