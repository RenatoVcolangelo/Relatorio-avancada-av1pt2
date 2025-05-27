package io.sim;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe Banco (AlphaBank)
 * 
 * Responsável por gerenciar contas, autenticar usuários, processar transações e
 * gerar relatórios financeiros. Funciona como um servidor que recebe conexões
 * da Company, FuelStation e Drivers.
 */
public class Banco extends Thread {

    // Indica se o banco está ativo (usado pelas outras classes como ExcelBanco)
    public static boolean ativo = true;

    // Lista com todas as contas cadastradas no sistema
    private ArrayList<Conta> listaContas;

    // Porta de conexão do servidor
    private int porta;

    // Socket do servidor que aceita conexões
    private ServerSocket serverSocket;

    // Lista estática com todas as transações realizadas (acessada por outras threads)
    public static ArrayList<Transacao> transacoes = new ArrayList<Transacao>();

    // HashMap com login e senha para autenticação
    private HashMap<String, String> cadastro = new HashMap<>();

    // Lista de threads de atendimento a cada cliente (só acabe quando as transações finalizarem)
    private ArrayList<ThreadBanco> threads = new ArrayList<>();

    /**
     * Construtor da classe Banco.
     * Cria o socket do servidor e prepara a estrutura para contas e autenticação.
     */
    public Banco(int porta) throws IOException {
        this.porta = porta;
        listaContas = new ArrayList<>();
        serverSocket = new ServerSocket(porta); // Abre a porta do servidor
        System.out.println("Servidor Banco criado");
    }

    /**
     * Método principal da thread Banco.
     * Aceita conexões dos clientes (Company, FuelStation e Drivers), cria threads para
     * atendê-los e inicia a geração de relatórios em tempo real.
     */
    public void run() {
        try {
            // Criação inicial do relatório Excel de transações
            Relatorio.criaExcelTransacao();

            // Inicia a thread que escreve continuamente no Excel com base nas transações
            ExcelBanco excelBanco = new ExcelBanco(this);
            excelBanco.start();

            // Espera conexões de todos os clientes (Company, FuelStation e N Drivers)
            for (int i = 0; i < EnvSimulator.totalDrivers + 2; i++) { // +2 por conta da Company e do Posto
                Socket clienteSocket = serverSocket.accept(); // Espera por conexão
                ThreadBanco thread = new ThreadBanco(clienteSocket, this); // Cria thread para cada cliente
                threads.add(thread);
                thread.start(); // Inicia o atendimento
            }

            // Aguarda o encerramento de todas as conexões
            for (ThreadBanco t : threads) {
                t.join();
            }

            // Após o fim das conexões, desativa o banco
            ativo = false;

            // Aguarda o término da thread que grava o Excel
            excelBanco.join();

            System.out.println("BANCO OFF");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna a conta correspondente ao ID fornecido.
     */
    public Conta getConta(int id) {
        return listaContas.get(id);
    }

    /**
     * Adiciona uma conta ao banco, tanto na lista quanto no mapa de autenticação.
     */
    public void addConta(int i, Conta c) {
        listaContas.add(i, c);
        cadastro.put(c.getLogin(), c.getSenha()); // Salva login e senha para autenticação
    }

    /**
     * Verifica se o login e senha informados estão corretos.
     */
    public boolean confereConta(String login, String senha) {
        return cadastro.get(login).equals(senha);
    }

    /**
     * Adiciona uma transação à lista estática (usada pelo ExcelBanco).
     */
    public static void addTransacao(Transacao transacao) {
        transacoes.add(transacao);
    }
}
