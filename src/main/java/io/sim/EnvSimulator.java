package io.sim;

import java.io.IOException;
import java.util.ArrayList;

import de.tudresden.sumo.objects.SumoColor;
import it.polito.appeal.traci.SumoTraciConnection;

public class EnvSimulator extends Thread{

    private SumoTraciConnection sumo;
	private Company company;
	private Banco alpha;
	private FuelStation fuelStation;
	public static int totalDrivers = 100;
	

    public EnvSimulator(){
		
    }

    public void run(){
		/* SUMO */
		String sumo_bin = "sumo-gui";		
		String config_file = "map/map.sumo.cfg";


		// Sumo connection
		this.sumo = new SumoTraciConnection(sumo_bin, config_file);
		sumo.addOption("start", "1"); // auto-run on GUI show
		sumo.addOption("quit-on-end", "1"); // auto-close on end

	
		try {
			sumo.runServer(12345);
			Thread.sleep(5000);
			System.out.println("sumo on");

			// Pega as rotas 
			Itinerary i1= new Itinerary("data/dados.xml", "0");

			// Cria o banco
			this.alpha = new Banco(22222);
			this.alpha.start();

			// Cria o posto de combustível
			this.fuelStation = new FuelStation();
			this.fuelStation.start();

			// Cria a Company
			this.company = new Company(i1.getRoutes(), totalDrivers, 55555);
			this.company.start();

			// Cria a classe que faz a passagem de tempo no sumo
			Step passo = new Step(sumo);
			passo.start();

			this.fuelStation.join();

			// Adiciona as contas ao banco; Conta 0 Company, Conta 1 Fuel station, drivers - demais
			this.alpha.addConta(0,this.company.getConta());
			this.alpha.addConta(1,this.fuelStation.getConta());

			// Carros e drivers
			ArrayList<Driver> drivers = new ArrayList<Driver>();
		
			for(int i = 1; i <= totalDrivers; i++){

				int fuelType = 2;
				int fuelPreferential = 2;
				double fuelPrice = 5.87;
				int personCapacity = 1;
				int personNumber = 1;
				SumoColor green = new SumoColor(0, 255, 0, 126);

				// i + 1 pois as contas dos drivers começa em 2

				Auto a1 = new Auto(i+1, "Car " + Integer.toString(i), green,"D"+ i, sumo, 200, fuelType, fuelPreferential, fuelPrice, personCapacity, personNumber);

				Driver d = new Driver(a1,i);
				drivers.add(d);

				this.alpha.addConta(i+1,d.getConta());
				d.start();
				Thread.sleep(500);			
			}

		// aguarda o fim das Threads

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
