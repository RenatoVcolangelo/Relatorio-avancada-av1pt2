package io.sim;
import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;


public class TransportService extends Thread {

	private String idTransportService;
	private SumoTraciConnection sumo;
	private Auto auto;
	private Rota route;

	public TransportService(String _idTransportService, Auto _auto,
			SumoTraciConnection _sumo, Rota route) {


		this.idTransportService = _idTransportService;
		this.auto = _auto;
		this.sumo = _sumo;
		this.route = route;

	}

	@Override
	public void run() {

            synchronized(this){
                try {
                    this.initializeRoutes();	
                    Thread.sleep(this.auto.getAcquisitionRate());
                    notify();
                } catch (InterruptedException e) {
                    
                    e.printStackTrace();
                }	}					
			
	}

	private void initializeRoutes() {

		SumoStringList edge = new SumoStringList();
		edge.clear();
		String aux = this.route.getEdge();

		for (String e : aux.split(" ")) {
			edge.add(e);
		}

		try {
			sumo.do_job_set(Route.add(this.route.getId(), edge));
			
			sumo.do_job_set(Vehicle.addFull(this.auto.getIdAuto(), 				//vehID
											this.route.getId(), 	            //routeID 
											"DEFAULT_VEHTYPE", 					//typeID 
											"now", 								//depart  
											"0", 								//departLane 
											"0", 								//departPos 
											"0",								//departSpeed
											"current",							//arrivalLane 
											"max",								//arrivalPos 
											"current",							//arrivalSpeed 
											"",									//fromTaz 
											"",									//toTaz 
											"", 								//line 
											this.auto.getPersonCapacity(),		//personCapacity 
											this.auto.getPersonNumber())		//personNumber
					);
			
			sumo.do_job_set(Vehicle.setColor(this.auto.getIdAuto(), this.auto.getColorAuto()));
			
			
		} catch (Exception e1) {
			System.out.println("Erro aqui");
			//e1.printStackTrace();
		}

	}


	public String getIdTransportService() {
		return this.idTransportService;
	}

	public SumoTraciConnection getSumo() {
		return this.sumo;
	}

	public Auto getAuto() {
		return this.auto;
	}

	public Rota getRoute(){
		return this.route;
	}


}