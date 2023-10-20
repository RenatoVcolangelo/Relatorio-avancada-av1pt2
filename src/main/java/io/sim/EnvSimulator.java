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
	public static int totalDrivers = 4;
	

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
			Itinerary i1= new Itinerary("data/dados2.xml", "0");

			this.alpha = new Banco(22222);
			this.alpha.start();

			this.fuelStation = new FuelStation();
			this.fuelStation.start();

			this.company = new Company(i1.getRoutes(), sumo,totalDrivers);
			this.company.start();

		
			Step passo = new Step(sumo);
			passo.start();


			this.fuelStation.join();
			// Adiciona as contas ao banco; Conta 0 Company, Conta 1 Fuel station, drivers - demais
			this.alpha.addConta(0,this.company.getConta());
			this.alpha.addConta(1,this.fuelStation.getConta());

			ArrayList<Driver> drivers = new ArrayList<Driver>();
			// Carros e drivers
			for(int i = 1; i <= totalDrivers; i++){

				int fuelType = 2;
				int fuelPreferential = 2;
				double fuelPrice = 5.87;
				int personCapacity = 1;
				int personNumber = 1;
				SumoColor green = new SumoColor(0, 255, 0, 126);

				Auto a1 = new Auto(i+1, "Car " + Integer.toString(i), green,"D"+ i, sumo, 200, fuelType, fuelPreferential, fuelPrice, personCapacity, personNumber);

				Driver d = new Driver(a1,i,sumo);
				drivers.add(d);
				this.alpha.addConta(i+1,d.getConta());
				d.start();
				Thread.sleep(500);
				
			}
		passo.join();
		//this.alpha.join();
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
