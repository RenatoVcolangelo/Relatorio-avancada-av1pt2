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
	private double fuelTank = 10000; //ml
	private atualizaTanque at;
	private boolean abastecer = false;
	private Rota route;
	private JSONObject obj = new JSONObject();
	private boolean finalizado = false;
	private String autoState = "esperando";
	private String bateu1km = "0";
	private int porta = 55555;
	private int contaDriver;
	private double distanciaPercorrida; // para 1km
	private double distancia; // geral
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

    private TransportService ts;
	private DataInputStream entrada;
    private DataOutputStream saida;
	
	// Classe Auto-> gera dados para company, indica ao driver para abastecer

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

		
	}

	@Override
	public void run() {

		// long t00 = System.nanoTime();
        // System.out.println("run auto = " + t00);
		//Relatorio.criaExcelReconciliacao();
		System.out.println(this.idAuto + " on");

        try {
			Socket socket = new Socket("127.0.0.1",porta); // conexão com a company

            saida = new DataOutputStream(socket.getOutputStream()); 
            entrada = new DataInputStream(socket.getInputStream());

			// cria instacia para atualizar o tanque e parar quando < 3L
			atualizaTanque at  = new atualizaTanque(this, sumo); 
			// long ta = System.nanoTime();
            // System.out.println("start tanque = " + ta);
			//at.start();
            while (!finalizado) { 
				
				// reinicia distancia percorrida
				distanciaPercorrida = 0;
				distancia = 0;
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

				try{
					// confere se as rotas foram finalizadas
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

				// caso ainda exista rotas, ele recebe uma
                route = string2Route(recebe);

	
                ts = new TransportService(this.idAuto,this,this.sumo,route);
				// long to = System.nanoTime();
            	// System.out.println("start ts = " + to);
                ts.start();
				ts.setPriority(10);
				// espera o TransportService adicionar o carro ao SUMO
				synchronized(ts){
					ts.join();
				}

                System.out.println(this.idAuto + " está com a rota " + route.getId());

                String edgeFinal = this.getEdgeFinal(); 
                this.on_off = true;
				// Verifica se o carro esta no SUMO
				// Dupla verificação pois os erros ocorrem quando o programa tenta acessar um carro e ele nao está no sumo ou está trocando de rota
				SumoStringList lista = (SumoStringList)this.sumo.do_job_get(Vehicle.getIDList());
				while(!lista.contains(this.idAuto)){
					Thread.sleep(200);
				}

				// pega latitude e longitude iniciais
				SumoPosition2D  posicaoInicial= (SumoPosition2D) sumo.do_job_get(Vehicle.getPosition(this.idAuto));
				double [] latlong = converteGeo(posicaoInicial.x,posicaoInicial.y);
				latinicial = latlong[0];
				longinicial = latlong[1];

				// Arraylists para guardar os tempos e distancias parciais, as edges de monitoramento 
				// e definir as velocidades médias

				ArrayList<Double> tempos = new ArrayList<Double>();
				ArrayList<Double> distancias = new ArrayList<Double>();
    			ArrayList<String> edges = new ArrayList<String>();
				ArrayList<Double> velmed = new ArrayList<Double>();
				
				edges.add("72145316#5");
				edges.add("30149869#3");
				edges.add("30149869#5");
				edges.add("30149869#6");

				velmed.add(8.25);
				velmed.add(8.25);
				velmed.add(7.85);
				velmed.add(7.322);				
				velmed.add(7.65);
				
				// marca o tempo 0
				long t0= System.currentTimeMillis();
				long tantigo = t0;
				int i = 1;
				double parcial = 0;
				double disantiga=0;
				double difDist = 0;

				// Seta a primeira velocidade parcial
				sumo.do_job_set(Vehicle.setSpeed(this.idAuto, velmed.remove(0)));
            	sumo.do_job_set(Vehicle.setSpeedMode(this.idAuto, 0));

                while(this.on_off){

					this.autoState = "executando";
					String edgeAtual = (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idAuto));
					
					//Quando acontece troca de edge para uma de monitoramento, é realizada a medição
					// de tempo e distancia

					if(!edges.isEmpty()){          
						if(edgeAtual.equals(edges.get(0))){

							// seta velocidade parcial
							sumo.do_job_set(Vehicle.setSpeed(this.idAuto, velmed.remove(0)));
            				sumo.do_job_set(Vehicle.setSpeedMode(this.idAuto, 0));

							edges.remove(0);
							long ti = System.currentTimeMillis();
							parcial = (ti-tantigo);
							parcial = parcial/1000;
							tantigo = ti;
							System.out.println("parcial t" +i + " " + parcial);
							i++;
							difDist = distancia - disantiga;
							System.out.println("dist" + i+ " " + difDist);
							distancias.add(difDist);
							disantiga = distancia;
							tempos.add(parcial);
						}
					}
                    Thread.sleep(this.acquisitionRate);
					
					
					// testa se a rota ja acabou
					if(rotaAcabou(edgeAtual, edgeFinal))
					{
						this.on_off = false;
						break;
					}

						atualizaSensores();
						// long t11 = System.nanoTime();
						// System.out.println("fim car = " + t11);
                }

				// pega o ultimo tempo parcial e total, assim como distancia 
				long t1= System.currentTimeMillis();
				parcial = t1- tantigo;
				parcial = parcial/1000;
				tempos.add(parcial);
				difDist = distancia - disantiga;
				distancias.add(difDist);
				System.out.println("parcial t" +i + " " + parcial);
				System.out.println("Dist" +i + " " + difDist);
				double total = t1 - t0;
				total = total/1000;
				tempos.add(total);
				distancias.add(distancia);
				System.out.println("Total = " + total);
				System.out.println("Dist Total = " + distancia); 
				// escreve no relatorio
				//Relatorio.manipulaExcelRec(tempos, distancias);

				System.out.println(this.idAuto + " acabou a rota.");

				this.Executadas.add(route);
				this.autoState = "esperando";
				// indica para a company que finalizou a rota
				obj.put("autoState", "rotaFinalizada");
				obj.put("routeId", this.route.getId());
				String str = obj.toString();
				cripto = Criptografia.encrypt(str);
				saida.writeInt(cripto.length);
				saida.write(cripto);
				

				System.out.println(this.idAuto + " esperando nova rota");
				

            }

            // espera o atualiza tanque finalizar
			at.join();
			System.out.println(this.idAuto + " off");

			} catch (InterruptedException e) {
				this.on_off = false;
                e.printStackTrace();
            } catch (Exception e) {
				this.on_off = false;
                e.printStackTrace();
            }
		
	}

	private void atualizaSensores() {

		try {

			if (!this.getSumo().isClosed() ) {

				this.autoState = "executando";
				SumoPosition2D sumoPosition2D;
				sumoPosition2D = (SumoPosition2D) sumo.do_job_get(Vehicle.getPosition(this.idAuto));
				// pega as lat e long atuais e atualiza a distancia percorrida
				double [] latlong = converteGeo(sumoPosition2D.x,sumoPosition2D.y);
				latAtual = latlong[0];
				longAtual = latlong[1];
				atualizaDistanciaPercorrida();
				latinicial = latAtual;
				longinicial = longAtual;

				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timeStamp = timestamp.toString();

				DrivingData _repport = new DrivingData(this.bateu1km, this.autoState, this.contaDriver,
						this.idAuto, this.driverID, timeStamp, sumoPosition2D.x, sumoPosition2D.y, latAtual, longAtual,
						(String) sumo.do_job_get(Vehicle.getRoadID(this.idAuto)),
						route.getId(),
						(double) sumo.do_job_get(Vehicle.getSpeed(this.idAuto)),
						(double) sumo.do_job_get(Vehicle.getDistance(this.idAuto)),
						(double) sumo.do_job_get(Vehicle.getFuelConsumption(this.idAuto)),
						1/*averageFuelConsumption (calcular)*/,
						this.fuelType, this.fuelPrice,
						(double) sumo.do_job_get(Vehicle.getCO2Emission(this.idAuto)),
						(double) sumo.do_job_get(Vehicle.getHCEmission(this.idAuto)),
						this.personCapacity,
						this.personNumber,
						this.distancia,
						(double) sumo.do_job_get(Vehicle.getLanePosition(this.idAuto)));
				
				// Envia o DataDriving para a company
				obj = JSON.drivingData2JSON(_repport);
				String dados = obj.toString();
				byte[] cripto = Criptografia.encrypt(dados);

				saida.writeInt(cripto.length);
				saida.write(cripto);

				this.bateu1km ="0";

			} else {
				System.out.println(" SUMO is closed...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// transforma uma mensagem recebida em Rota
	private Rota string2Route(String string)
	{   
        JSONObject obj;

		obj = new JSONObject(string);
		String Id = obj.getString("IDRoute");
		String rec = obj.getString("Edges");
		Rota route = new Rota(Id, rec);

		return route;
      
        
	}

	public double getDistacia(){
		return distancia;
	}
	// similar ao que é feito na transport service, mas para pegar somente a edge final
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

	// A rota finaliza quando a edge atual for igual a edge final, uma segunda verificação para ver se o carro 
	// saiu da lista de carros em execução
    private boolean rotaAcabou(String edgeAtual, String edgeFinal) throws Exception
	{
		SumoStringList lista = (SumoStringList) this.sumo.do_job_get(Vehicle.getIDList());
		lista.contains(idAuto);
		if(!lista.contains(idAuto) && (edgeFinal.equals(edgeAtual)))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private double[] converteGeo(double x, double y)  {

		double raioTerra = 6371000; // raio da Terra
		// referencia de X,Y = 0,0 no SUMO
		double latRef = -22.986731;
		double lonRef = -43.217054;

		// Conversão de metros para graus
		double lat = latRef + (y / raioTerra) * (180 / Math.PI);
		double lon = lonRef + (x / raioTerra) * (180 / Math.PI) / Math.cos(latRef * Math.PI / 180);

		double[] latlong = new double[] { lat, lon };
		return latlong;
	}

	private double calculaDeslocamento(double lat1, double lon1, double lat2, double lon2) {
		double raioTerra = 6371000;
	
		// Diferenças das latitudes e longitudes
		double diferancaLat = Math.toRadians(lat2 - lat1);
		double diferancaLon = Math.toRadians(lon2 - lon1);
	
		// Fórmula de Haversine // https://forum.maparadar.com/viewtopic.php?t=5181
		double a = Math.sin(diferancaLat / 2) * Math.sin(diferancaLat / 2) +
				   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				   Math.sin(diferancaLon / 2) * Math.sin(diferancaLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distancia = raioTerra * c;
	
		return distancia;
	}


	public void atualizaDistanciaPercorrida() {

		distancia = distancia + calculaDeslocamento(latinicial,longinicial,latAtual,longAtual);

		if (distancia - distanciaPercorrida >= 1000) {
			System.out.println("Fazer pagamento para " + this.idAuto);
			distanciaPercorrida = distancia;
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


	public boolean getFinalizado(){
		return finalizado;
	}


	public double getFuelTank(){
		return fuelTank;

	}

}