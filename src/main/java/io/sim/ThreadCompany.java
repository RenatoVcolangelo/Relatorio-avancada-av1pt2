package io.sim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.JSONObject;

// Gerencia a comunicação car/company, analisa o estado do carro e faz pagamentos
public class ThreadCompany extends Thread {

    private Socket carroSocket;     // Conexão com o socket do carro (Auto)
    private Socket bancoSocket;     // Conexão com o banco (compartilhado entre todos os carros)
    private Company company;        // Referência à instância da Company
    private String autoState = "";  // Estado atual do carro ("esperando", "executando", etc.)
    private String bateu1km = "";   // Flag de batida de 1km (para pagamento)
    private String routeID;         // ID da rota que o carro está executando
    private String mensagem;        // Mensagem recebida do carro (em texto)
    private int contaDriver;        // Conta do motorista (para receber pagamento)
    private JSONObject texto = new JSONObject(); // Objeto JSON para envio de dados ao carro
    private boolean on_off = true;  // Flag de controle do loop da thread

    // Construtor: recebe os sockets de comunicação e a referência da empresa
    public ThreadCompany(Socket carroSocket, Socket bancoSocket, Company company) { 
        this.carroSocket = carroSocket;
        this.bancoSocket = bancoSocket;
        this.company = company;
    }

    @Override
    public void run() {
        // long t0 = System.nanoTime();
        // System.out.println("run thread = " + t0);

        try {
            // Cria os canais de comunicação com o carro e o banco
            DataOutputStream saida = new DataOutputStream(carroSocket.getOutputStream());
            DataInputStream entrada = new DataInputStream(carroSocket.getInputStream());
            DataOutputStream saidaBanco = new DataOutputStream(bancoSocket.getOutputStream());

            while (on_off) {

                // Recebe mensagem do carro, descriptografa e transforma em JSON
                int tam = entrada.readInt();  
                byte[] cripto = entrada.readNBytes(tam);
                mensagem = Criptografia.decrypt(cripto);
                JSONObject obj = new JSONObject(mensagem);

                // Lê o estado atual do carro (esperando, executando, etc.)
                autoState = obj.getString("autoState");

                // --- Carro solicitando rota ---
                if (autoState.equals("esperando")) {

                    // Se houver rotas disponíveis
                    if (this.company.getRoutesNExe().size() > 0) {
                        Rota r = company.getRoute(); // Pega rota da fila
                        texto.put("IDRoute", r.getId());
                        texto.put("Edges", r.getEdge());
                        cripto = Criptografia.encrypt(texto.toString());

                        saida.writeInt(cripto.length);
                        saida.write(cripto);

                        System.out.println("Rota enviada");
                    } else {
                        // Caso não haja rotas, envia aviso para finalizar
                        texto.put("RotasFinalizadas", "true");
                        cripto = Criptografia.encrypt(texto.toString());

                        saida.writeInt(cripto.length);
                        saida.write(cripto);
                    }
                }

                // --- Carro sinaliza que finalizou uma rota ---
                else if (autoState.equals("rotaFinalizada")) {
                    routeID = obj.getString("routeId");
                    this.company.routeExecutada(routeID); // Atualiza lista de rotas
                    System.out.println("Rota " + routeID + " concluida");
                }

                // --- Carro está executando ---
                else if (autoState.equals("executando")) {

                    // Adiciona dados de direção ao relatório da Company
                    this.company.addRelatorio(JSON.stringToDrivingData(mensagem));

                    // Verifica se percorreu 1km e requer pagamento
                    bateu1km = obj.getString("bateu1km");
                    contaDriver = obj.getInt("contaDriver");

                    if (bateu1km.equals("bateu1km")) {
                        // Inicia thread de pagamento automático
                        BotPayment bot = new BotPayment(
                            saidaBanco,
                            this.company.getConta().getId(),
                            this.company.getConta().getLogin(),
                            this.company.getConta().getSenha(),
                            contaDriver,
                            3.25 // valor fixo de pagamento
                        );
                        bot.start();
                    }
                }

                // --- Carro encerrando conexão ---
                else if (autoState.equals("finalizado")) {
                    on_off = false; 
                    entrada.close();
                    saida.close();
                }

                // long t1 = System.nanoTime();
                // System.out.println("fim thread = " + t1);
            }

        } catch (IOException ioe) {
            System.out.println("Erro: 1" + ioe.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
