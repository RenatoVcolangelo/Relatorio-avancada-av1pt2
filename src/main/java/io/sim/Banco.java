package io.sim;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

// Classe do banco Alpha, servidor que gerencia as conexões com os clientes e a lista de clientes 
// salva as transações
public class Banco extends Thread{

	public static boolean ativo = true;
	private ArrayList<Conta> listaContas;
	private int porta;
	private ServerSocket serverSocket;
	public static ArrayList<Transacao> transacoes = new ArrayList<Transacao>();
	// Hashmap para conferir login e senha
	private HashMap<String,String> cadastro = new HashMap<String,String>();
	private ArrayList<ThreadBanco> threads = new ArrayList<ThreadBanco>();
	
	// Classe banco cria conexões com clientes e armazena as transações
	public Banco(int porta) throws IOException{
		this.porta = porta;
		listaContas = new ArrayList<Conta>();
		serverSocket = new ServerSocket(porta); // Porta do servidor
        System.out.println("Servidor Banco criado");

	}

	public void run(){
		try {
			// cria relatório das transações
			Relatorio.criaExcelTransacao();
			ExcelBanco excelBanco = new ExcelBanco(this);
			excelBanco.start();

			for(int i = 0; i < EnvSimulator.totalDrivers+2; i++) { // +2 pois company e fuel station
				
					Socket clienteSocket = serverSocket.accept(); // Espera por uma conexão

					ThreadBanco thread = new ThreadBanco(clienteSocket,this);
					threads.add(thread);		
					thread.start(); 
			}
			
			// espera conexões fecharem
			for(ThreadBanco t: threads){
				t.join();
			}

			ativo = false;
			excelBanco.join();

			System.out.println("BANCO OFF");

		} catch (IOException e) {

			e.printStackTrace();} catch (InterruptedException e) {
			e.printStackTrace();
		}

			    
	}

	public Conta getConta(int id){
		return listaContas.get(id);
	}

	public void addConta(int i, Conta c){
		listaContas.add(i, c);
		cadastro.put(c.getLogin(),c.getSenha());

	}
	public boolean confereConta(String login, String senha){
		return cadastro.get(login).equals(senha);
	}
	// adiciona Transação ao relatorio
	public static void addTransacao(Transacao transacao){
		transacoes.add(transacao);
	}


}