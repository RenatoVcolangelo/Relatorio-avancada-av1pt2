package io.sim;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Banco extends Thread{

	private static ArrayList<Transacao> transacoes = new ArrayList<Transacao>();
	private HashMap<String,String> cadastro = new HashMap<String,String>();
	public static boolean ativo = true;
	private ArrayList<Conta> listaContas;
	private int porta;
	private ServerSocket serverSocket;
	private boolean on_ff = true;
	public ArrayList<ThreadBanco> threads = new ArrayList<ThreadBanco>();

	public Banco(int porta) throws IOException{
		this.porta = porta;
		listaContas = new ArrayList<Conta>();
		serverSocket = new ServerSocket(porta); // Porta do servidor
        System.out.println("Servidor Banco criado");

	}

	public void run(){
		try {
			for(int i = 0; i < EnvSimulator.totalDrivers+2; i++) {
				
					Socket clienteSocket = serverSocket.accept(); // Espera por uma conexão

					ThreadBanco thread = new ThreadBanco(clienteSocket,this);
					threads.add(thread);		
					thread.start(); 
			}


			System.out.println("Banco off");
		} catch (IOException e) {

			e.printStackTrace();}
		// } catch (InterruptedException e) {
		// 	e.printStackTrace();
		// } 
			    
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
	
	public static void addTransacao(Transacao transacao){
		transacoes.add(transacao);
	}



}