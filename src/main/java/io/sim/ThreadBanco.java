package io.sim;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.JSONObject;
import java.sql.Timestamp;

/**
 * Classe ThreadBanco
 * 
 * Responsável por atender um cliente (Driver, Company ou FuelStation) conectado ao Banco.
 * Recebe dados via socket, valida login/senha e processa transações financeiras entre contas.
 */
public class ThreadBanco extends Thread {

    private Socket socket;           // Conexão com o cliente
    private Conta pagador;           // Conta que paga
    private Conta recebedor;         // Conta que recebe
    private double valor;            // Valor da transação
    private Banco banco;             // Referência ao banco principal
    private boolean on_ff;           // Flag de controle da thread

    /**
     * Construtor da thread que trata uma conexão individual com o banco.
     */
    public ThreadBanco(Socket s, Banco banco) { 
        this.socket = s;
        this.banco = banco;
        this.on_ff = true; 
    }

    /**
     * Método principal da thread que aguarda transações criptografadas.
     * Cada transação é verificada, autenticada e registrada.
     */
    @Override
    public void run() {
        try {
            // Cria o canal de entrada para receber mensagens do cliente
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            while (on_ff) {
                // Recebe dados criptografados
                int tam = entrada.readInt();  
                byte[] cripto = entrada.readNBytes(tam);

                // Descriptografa e converte em JSON
                String mensagem = Criptografia.decrypt(cripto);
                JSONObject obj = JSON.dadosJSON2Transacao(mensagem);

                try {
                    // Se a mensagem contiver sinal de encerramento, finaliza a thread
                    if (obj.getString("acabou").equals("true")) {
                        on_ff = false;
                        break;
                    }
                } catch (Exception e) {
                    // Pode não conter o campo "acabou", ignora erro
                }

                // Recupera dados da transação a partir do JSON
                pagador = banco.getConta(obj.getInt("pagador"));
                recebedor = banco.getConta(obj.getInt("recebedor"));
                valor = obj.getDouble("valor");

                // Verifica autenticação com login e senha
                String login = obj.getString("login");
                String senha = obj.getString("senha");

                // Se autenticação for válida, processa pagamento
                if (this.banco.confereConta(login, senha)) {
                    pagamento();
                } else {
                    System.out.println("senha incorreta");
                }
            }

        } catch (IOException ioe) {
            System.out.println("Erro:2 " + ioe.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifica se há saldo suficiente e realiza a transação entre as contas.
     * Retorna true se a operação for possível, false caso contrário.
     */
    public boolean transacao() {
        if (pagador.getSaldo() < valor) {
            return false; // Saldo insuficiente
        } else {
            // Atualiza saldos das contas
            pagador.setSaldo(pagador.getSaldo() - valor);
            recebedor.setSaldo(recebedor.getSaldo() + valor);
            return true;
        }
    }

    /**
     * Executa a operação de pagamento, registra o log e armazena a transação no banco.
     */
    public void pagamento() {
        if (transacao()) {
            // Imprime saldos atualizados
            System.out.println("Saldo conta " + recebedor.getId() + " " + recebedor.getSaldo());
            System.out.println("Saldo conta " + pagador.getId() + " " + pagador.getSaldo());

            // Cria objeto de transação com timestamp atual
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Transacao transacao = new Transacao(pagador.getId(), recebedor.getId(), valor, timestamp);

            // Registra a transação na lista de transações do banco
            Banco.addTransacao(transacao);
        } else {
            System.out.println("Saldo insuficienete");
        }
    }

    /**
     * Permite encerrar a execução da thread externamente.
     */
    public void setOn_off(boolean on_ff) {
        this.on_ff = on_ff;
    }
}
