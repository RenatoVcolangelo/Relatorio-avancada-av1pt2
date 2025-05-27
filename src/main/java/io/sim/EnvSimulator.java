package io.sim;

import java.io.IOException;
import java.util.ArrayList;

import de.tudresden.sumo.objects.SumoColor;
import it.polito.appeal.traci.SumoTraciConnection;

public class EnvSimulator extends Thread {

    // Conexão com o simulador SUMO
    private SumoTraciConnection sumo;
    // Objetos principais do ambiente
    private Company company;
    private Banco alpha;
    private FuelStation fuelStation;
    // Quantidade de drivers e carros na simulação
    public static int totalDrivers = 100;

    // Construtor padrão
    public EnvSimulator() {}

    @Override
    public void run() {
        /* Configurações do SUMO */
        String sumo_bin = "sumo-gui";               // Caminho do binário do SUMO (modo gráfico)
        String config_file = "map/map.sumo.cfg";    // Arquivo de configuração do mapa do SUMO

        // Instancia a conexão com o SUMO
        this.sumo = new SumoTraciConnection(sumo_bin, config_file);
        sumo.addOption("start", "1");               // Inicia automaticamente a simulação ao abrir a GUI
        sumo.addOption("quit-on-end", "1");         // Fecha a GUI automaticamente ao terminar

        try {
            // Inicia o servidor do SUMO
            sumo.runServer(12345);
            Thread.sleep(5000); // Espera 5 segundos para garantir que SUMO esteja totalmente iniciado
            System.out.println("sumo on");

            // Carrega o conjunto de rotas do arquivo XML
            Itinerary i1 = new Itinerary("data/dados2.xml", "0");

            // Cria o banco e inicia sua thread
            this.alpha = new Banco(22222);
            this.alpha.start();

            // Cria e inicia o posto de combustível (FuelStation)
            this.fuelStation = new FuelStation();
            this.fuelStation.start();

            // Cria e inicia a empresa que irá gerenciar os motoristas e as rotas
            this.company = new Company(i1.getRoutes(), totalDrivers, 55555);
            this.company.start();

            // Cria e inicia a thread que controla o tempo do SUMO
            Step passo = new Step(sumo);
            passo.start();

            // Aguarda o término da inicialização do posto
            this.fuelStation.join();

            // Registra contas no banco:
            // Conta 0 = Company, Conta 1 = FuelStation
            this.alpha.addConta(0, this.company.getConta());
            this.alpha.addConta(1, this.fuelStation.getConta());

            // Lista de drivers para referência
            ArrayList<Driver> drivers = new ArrayList<Driver>();

            // Criação de 100 carros e respectivos motoristas
            for (int i = 1; i <= totalDrivers; i++) {
                int fuelType = 2;              // Tipo de combustível (2 = gasolina)
                int fuelPreferential = 2;      // Preferência por tipo de combustível
                double fuelPrice = 5.87;       // Preço por litro
                int personCapacity = 1;        // Capacidade de passageiros
                int personNumber = 1;          // Quantidade atual de passageiros
                SumoColor green = new SumoColor(0, 255, 0, 126); // Cor do carro

                // Cria um novo carro (Auto) e um novo motorista (Driver)
                Auto a1 = new Auto(i + 1, "Car " + i, green, "D" + i, sumo,
                                  300, fuelType, fuelPreferential, fuelPrice,
                                  personCapacity, personNumber);

                Driver d = new Driver(a1, i);
                drivers.add(d);

                // Registra a conta do driver no banco (inicia a partir da posição 2)
                this.alpha.addConta(i + 1, d.getConta());

                // Inicia a thread do driver
                d.start();

                // Pequeno delay entre cada criação para evitar sobrecarga
                Thread.sleep(200);
            }

            // Aguarda o término de todas as threads principais
            passo.join();
            this.alpha.join();
            this.company.join();
            this.fuelStation.join();

            System.out.println("Final de simulação");

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
