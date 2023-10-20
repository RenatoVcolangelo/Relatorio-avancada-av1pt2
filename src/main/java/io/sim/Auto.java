package io.sim;

import de.tudresden.sumo.cmd.Vehicle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.json.JSONObject;
import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;
import de.tudresden.sumo.objects.SumoStringList;

public class Auto extends Vehicle implements Runnable{

	private String idAuto;
	private SumoColor colorAuto;
	private String driverID;
	private SumoTraciConnection sumo;
	private double relativo = 0;
	private double fuelTank = 3500; //ml
	private atualizaTanque at;
	private boolean abastecer = false;
	private Rota route;
	private JSONObject obj = new JSONObject();
	private boolean emrota;
	private boolean finalizado = false;
	private String autoState = "esperando";
	private String bateu1km = "0";
	private int porta = 55555;
	private int contaDriver;
	private double distanciaPercorrida;
	private double latinicial;
	private double longinicial;
	private double latAtual;
	private double longAtual;
	private ArrayList<Rota> naoExecutadas = new ArrayList<Rota>(); 
	private ArrayList<Rota> Executadas = new ArrayList<Rota>(); 

	private boolean on_off;
	private long acquisitionRate;
	private int fuelType; 			// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private int fuelPreferential; 	// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private double fuelPrice; 		// price in liters
	private int personCapacity;		// the total number of persons that can ride in this vehicle
	private int personNumber;		// the total number of persons which are riding in this vehicle

	private ArrayList<DrivingData> drivingRepport;
    private TransportService ts;
	private DataInputStream entrada;
    private DataOutputStream saida;
	private String recebe;
	
	public Auto(int contaDriver,String _idAuto, SumoColor _colorAuto, String _driverID, SumoTraciConnection _sumo, long _acquisitionRate,
			int _fuelType, int _fuelPreferential, double _fuelPrice, int _personCapacity, int _personNumber) throws Exception {
		this.contaDriver = contaDriver;
		this.idAuto = _idAuto;
		this.colorAuto = _colorAuto;
		this.driverID = _driverID;
		this.sumo = _sumo;
		this.acquisitionRate = _acquisitionRate;
		
		if((_fuelType < 0) || (_fuelType > 4)) {
			this.fuelType = 4;
		} else {
			this.fuelType = _fuelType;
		}
		
		if((_fuelPreferential < 0) || (_fuelPreferential > 4)) {
			this.fuelPreferential = 4;
		} else {
			this.fuelPreferential = _fuelPreferential;
		}

		this.fuelPrice = _fuelPrice;
		this.personCapacity = _personCapacity;
		this.personNumber = _personNumber;
		this.drivingRepport = new ArrayList<DrivingData>();

		
	}

	@Override
	public void run() {
		System.out.println(this.idAuto + " on");
        try {
			Socket socket = new Socket("127.0.0.1",porta);

            saida = new DataOutputStream(socket.getOutputStream());
            entrada = new DataInputStream(socket.getInputStream());
			atualizaTanque at  = new atualizaTanque(this, sumo); 
			
			//at.start();

            while (!finalizado) { 
				
				distanciaPercorrida = 0;
				// envia seu estado para a company

				obj.put("autoState", autoState);

				String dados = obj.toString();
				byte[] cripto = Criptografia.encrypt(dados);
				saida.writeInt(cripto.length);
				saida.write(cripto);
	
				Thread.sleep(1000);

				// recebe uma resposta -> se houver rotas disponiveis ele recebe uma
				//                     -> se não houver rotas o carro finaliza

				int tam = entrada.readInt();  
                cripto = entrada.readNBytes(tam);
                String recebe = Criptografia.decrypt(cripto);

				//recebe = entrada.readUTF();

				try{
					
					JSONObject obj = new JSONObject(recebe);

                	String str = obj.getString("RotasFinalizadas");

				if(str.equals("true")){
					this.finalizado = true;
					this.autoState = "finalizado";
					System.out.println("nao ha mais rotas");
					obj.put("autoState", autoState);
					dados = obj.toString();
					cripto = Criptografia.encrypt(dados);
			
					saida.writeInt(cripto.length);
					saida.write(cripto);

					saida.close();
					entrada.close();
					socket.close();
				
					break;
				}
				}
				 catch (Exception e)  {
	
				}

				
                route = string2Route(recebe);
	
                ts = new TransportService(this.idAuto,this,this.sumo,route);
                ts.start();

				synchronized(ts){
					ts.join();
				}

                System.out.println(this.idAuto + " está com a rota " + route.getId());
                String edgeFinal = this.getEdgeFinal(); 
                this.on_off = true;

				SumoStringList a = (SumoStringList)this.sumo.do_job_get(Vehicle.getIDList());
				while(!a.contains(this.idAuto)){
					Thread.sleep(200);
				}

				SumoPosition2D  posicaoInicial= (SumoPosition2D) sumo.do_job_get(Vehicle.getPosition(this.idAuto));

				double [] latlong = converteGeo(posicaoInicial.x,posicaoInicial.y);
				latinicial = latlong[0];
				longinicial = latlong[1];
				
                while(this.on_off){

					this.autoState = "executando";
					String edgeAtual = (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idAuto));
                    Thread.sleep(this.acquisitionRate);
					
					// testa se a rota ja acabou
					if(rotaAcabou(edgeAtual, edgeFinal))
					{
						System.out.println(this.idAuto + " acabou a rota.");
						this.on_off = false;
						break;
					}
					atualizaSensores();
					
                }
				// indica para a company que finalizou a rota
				obj.put("autoState", "rotaFinalizada");
				obj.put("routeId", this.route.getId());
				String str = obj.toString();

				cripto = Criptografia.encrypt(str);
        
				saida.writeInt(cripto.length);
				saida.write(cripto);
				
				//saida.writeUTF(str);

				System.out.println(this.idAuto + " esperando nova rota");

				if(finalizado){	
					at.join(); // finaliza a atualização do
					this.autoState = "finalizado";
				}
				else{
					this.autoState = "esperando";
				}
            }

            } catch (InterruptedException e) {
				this.on_off = false;
                e.printStackTrace();
            } catch (Exception e) {
				this.on_off = false;
                e.printStackTrace();
            }
			System.out.println(this.idAuto + " off");
		
	}

	private void atualizaSensores() {

		try {

			if (!this.getSumo().isClosed() ) {

				this.autoState = "executando";

				SumoPosition2D sumoPosition2D;
				sumoPosition2D = (SumoPosition2D) sumo.do_job_get(Vehicle.getPosition(this.idAuto));

				double [] latlong = converteGeo(sumoPosition2D.x,sumoPosition2D.y);
				latAtual = latlong[0];
				longAtual = latlong[1];
				atualizaDistanciaPercorrida();

				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timeStamp = timestamp.toString();

				DrivingData _repport = new DrivingData(this.bateu1km, this.autoState, this.contaDriver,
						this.idAuto, this.driverID, timeStamp, sumoPosition2D.x, sumoPosition2D.y, latAtual, longAtual,
						(String) this.sumo.do_job_get(Vehicle.getRoadID(this.idAuto)),
						(String) this.sumo.do_job_get(Vehicle.getRouteID(this.idAuto)),
						(double) sumo.do_job_get(Vehicle.getSpeed(this.idAuto)),
						(double) sumo.do_job_get(Vehicle.getDistance(this.idAuto)),
						(double) sumo.do_job_get(Vehicle.getFuelConsumption(this.idAuto)),
						1/*averageFuelConsumption (calcular)*/,
						this.fuelType, this.fuelPrice,
						(double) sumo.do_job_get(Vehicle.getCO2Emission(this.idAuto)),
						(double) sumo.do_job_get(Vehicle.getHCEmission(this.idAuto)),
						this.personCapacity,
						this.personNumber);

				this.drivingRepport.add(_repport);				
				obj = JSON.drivingData2JSON(_repport);

				String dados = obj.toString();

				byte[] cripto = Criptografia.encrypt(dados);
        
				saida.writeInt(cripto.length);
				saida.write(cripto);

				//saida.writeUTF(dados);
				this.bateu1km ="0";

			} else {
				System.out.println(" SUMO is closed...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	private Rota string2Route(String string)
	{   
        JSONObject obj;

		obj = new JSONObject(string);
		String Id = (String) obj.get("IDRoute");
		String rec = (String) obj.get("Edges");
		Rota route = new Rota(Id, rec);

		return route;
      
        
	}

    private String getEdgeFinal()
	{
		SumoStringList edge = new SumoStringList();
		edge.clear();
		String aux = this.route.getEdge();
		for(String e : aux.split(" "))
		{
			edge.add(e);
		}
		return edge.get(edge.size()-1);
	}

    private boolean rotaAcabou(String _edgeAtual, String _edgeFinal) throws Exception
	{
		SumoStringList lista = (SumoStringList) this.sumo.do_job_get(Vehicle.getIDList());
		lista.contains(idAuto);
		if(!lista.contains(idAuto) && (_edgeFinal.equals(_edgeAtual)))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private double[] converteGeo(double x, double y)  {

		double raioTerra = 6371000; // raio médio da Terra em metros

		double latRef = -22.986731;
		double lonRef = -43.217054;

		// Conversão de metros para graus
		double lat = latRef + (y / raioTerra) * (180 / Math.PI);
		double lon = lonRef + (x / raioTerra) * (180 / Math.PI) / Math.cos(latRef * Math.PI / 180);

		double[] coordGeo = new double[] { lat, lon };
		return coordGeo;
	}

	private double calculaDistancia(double lat1, double lon1, double lat2, double lon2) {
		double raioTerra = 6371000;
	
		// Diferenças das latitudes e longitudes
		double diferancaLat = Math.toRadians(lat2 - lat1);
		double diferancaLon = Math.toRadians(lon2 - lon1);
	
		// Fórmula de Haversine
		double a = Math.sin(diferancaLat / 2) * Math.sin(diferancaLat / 2) +
				   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				   Math.sin(diferancaLon / 2) * Math.sin(diferancaLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distancia = raioTerra * c;
	
		return distancia;
	}


	public void atualizaDistanciaPercorrida() {

		double distancia = calculaDistancia(latinicial,longinicial,latAtual,longAtual);

		if (distancia > (distanciaPercorrida + 100)) {
			System.out.println("Fazer pagamento para " + this.idAuto);
			distanciaPercorrida += 100;
			this.bateu1km = "bateu1km";
		}
	}

	public ArrayList<Rota> getNaoExecutadas(){
		return naoExecutadas;
	}

	public ArrayList<Rota> getExecutadas(){
		return Executadas;
	}
	public Rota getRota(){
		return route;
	}

	public boolean isOn_off() {
		return this.on_off;
	}

	public void setAutoState(String state){
		this.autoState = state;
	}

	public String getAutoState(){
		return autoState;
	}

	public void setBateu1km(String state){
		this.autoState = state;
	}

	public String getBateu1km(){
		return bateu1km;
	}

	public int getContaDriver(){
		return contaDriver;
	}

	public void setOn_off(boolean _on_off) {
		this.on_off = _on_off;
	}

	public void setEmRota(boolean a) {
		this.emrota = a;
	}

	public boolean getEmRota() {
		return this.emrota;
	}

	public void setAbastecer(boolean a){
		this.abastecer = a;
	}

	public boolean getAbastecer(){
		return this.abastecer;
	}

	public long getAcquisitionRate() {
		return this.acquisitionRate;
	}

	public void setAcquisitionRate(long _acquisitionRate) {
		this.acquisitionRate = _acquisitionRate;
	}

	public String getIdAuto() {
		return this.idAuto;
	}

	public SumoTraciConnection getSumo() {
		return this.sumo;
	}

	public int getFuelType() {
		return this.fuelType;
	}

	public void setFuelType(int _fuelType) {
		if((_fuelType < 0) || (_fuelType > 4)) {
			this.fuelType = 4;
		} else {
			this.fuelType = _fuelType;
		}
	}

	public double getFuelPrice() {
		return this.fuelPrice;
	}

	public void setFuelPrice(double _fuelPrice) {
		this.fuelPrice = _fuelPrice;
	}

	public void setFuelTank(double comb) {
		this.fuelTank += comb;
	}

	public SumoColor getColorAuto() {
		return this.colorAuto;
	}

	public int getFuelPreferential() {
		return this.fuelPreferential;
	}

	public void setFuelPreferential(int _fuelPreferential) {
		if((_fuelPreferential < 0) || (_fuelPreferential > 4)) {
			this.fuelPreferential = 4;
		} else {
			this.fuelPreferential = _fuelPreferential;
		}
	}

	public int getPersonCapacity() {
		return this.personCapacity;
	}

	public int getPersonNumber() {
		return this.personNumber;
	}
	public double getRelativo(){
		return relativo;
	}

	public boolean getFinalizado(){
		return finalizado;
	}

	public void setRelativo(double r){
		this.relativo = r;

	}

	public double getFuelTank(){
		return fuelTank;

	}

}