package io.sim;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import org.json.JSONObject;

/**
 * Classe FuelStation
 *
 * Representa o posto de combustível da simulação.
 * Possui duas "bombas" (limite de atendimento simultâneo = 2), controladas por um `Semaphore`.
 * Realiza apenas o abastecimento de veículos, sem transações financeiras diretas.
 */
public class FuelStation extends Thread {

    private static final int MAX_CARROS = 2;                // Número máximo de carros atendidos simultaneamente
    private static Semaphore semaforo = new Semaphore(MAX_CARROS); // Controle de acesso às bombas
    private Socket socket;                                  // Conexão com o banco (somente para notificar encerramento)
    private DataOutputStream saida;                         // Canal de saída de dados
    private Conta conta;                                    // Conta do posto (com saldo alto)

    /**
     * Construtor padrão.
     */
    public FuelStation() {
        // Inicialização ocorre no run()
    }

    /**
     * Executa a thread da FuelStation.
     * Conecta ao banco apenas para registrar a existência da conta.
     * Fecha imediatamente após informar o encerramento.
     */
    @Override
    public void run() {
        try {
            this.socket = new Socket("127.0.0.1", 22222);        // Conecta-se ao banco
            this.conta = new Conta(10000, 1, "12345");           // Cria a conta do posto com saldo alto
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Posto on");

        // Cria uma mensagem JSON apenas para fechar a conexão com o banco
        JSONObject obj = new JSONObject();
        obj.put("acabou", "true");

        try {
            Thread.sleep(500); // Aguarda brevemente para garantir que o banco esteja pronto
            saida = new DataOutputStream(socket.getOutputStream());

            byte[] cripto = Criptografia.encrypt(obj.toString()); // Criptografa o JSON
            saida.writeInt(cripto.length);
            saida.write(cripto);

            socket.close(); // Encerra conexão com o banco
        } catch (Exception e) {
            // Exceções ignoradas intencionalmente
        }

        System.out.println("Posto oFF");
    }

    /**
     * Retorna a conta do posto (usada pelo banco).
     */
    public Conta getConta() {
        return conta;
    }

    /**
     * Método estático responsável pelo abastecimento de carros.
     * Usa o `Semaphore` para limitar a quantidade de carros sendo abastecidos ao mesmo tempo.
     * Simula o tempo de abastecimento e atualiza o tanque do carro.
     *
     * @param auto Carro a ser abastecido
     * @param qtd  Quantidade de combustível a ser adicionada (em ml)
     */
    public static void abastecerCarro(Auto auto, double qtd) {
        try {
            semaforo.acquire(); // Aguarda disponibilidade de bomba
            System.out.println(auto.getIdAuto() + " está sendo atendido.");
            
            Thread.sleep(120000); // Simula o tempo de abastecimento (2 minutos)

            auto.setFuelTank(qtd); // Abastece o carro
            System.out.println(auto.getIdAuto() + " abastecido");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restaura status da thread em caso de interrupção
        } finally {
            semaforo.release();          // Libera bomba para outro carro
            auto.setAbastecer(false);    // Informa que carro não precisa mais abastecer
        }
    }
}
