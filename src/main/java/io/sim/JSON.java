package io.sim;

//import org.json.simple.JSONObject;
import org.json.JSONObject;

public class JSON {

    public static String dadosTrasacao2JSON(String login, String senha, int conta_paga, int conta_recebe, double valor){
        JSONObject obj = new JSONObject();
        obj.put("login", login);
        obj.put("senha", senha);
        obj.put("pagador", conta_paga);
        obj.put("recebedor", conta_recebe);
        obj.put("valor", valor);
        return obj.toString();
    }

    public static JSONObject dadosJSON2Transacao(String transacao){
        JSONObject obj = new JSONObject(transacao);
        return obj;
    }

  

    public static JSONObject drivingData2JSON(DrivingData _DrivingData)
	{
        JSONObject obj = new JSONObject();
        obj.put("contaDriver", _DrivingData.getContaDriver());
		obj.put("bateu1km", _DrivingData.getBateu1km());		
		obj.put("autoState", _DrivingData.getAutoState());
        obj.put("AutoID",_DrivingData.getAutoID());
		obj.put("DriverID",_DrivingData.getDriverID());
		obj.put("TimeStamp",_DrivingData.getTimeStamp());
		obj.put("X_Position",_DrivingData.getX_Position());
		obj.put("Y_Position",_DrivingData.getY_Position());
        obj.put("latitude",_DrivingData.getLatitude());
        obj.put("longitude",_DrivingData.getLongitude());
		obj.put("RoadIDSUMO",_DrivingData.getRoadIDSUMO());
		obj.put("RouteIDSUMO",_DrivingData.getRouteIDSUMO());
		obj.put("Speed",_DrivingData.getSpeed());
		obj.put("Odometer",_DrivingData.getOdometer());
		obj.put("FuelConsumption",_DrivingData.getFuelConsumption());
		obj.put("AverageFuelConsumption",_DrivingData.getAverageFuelConsumption());
		obj.put("FuelType",_DrivingData.getFuelType());
		obj.put("FuelPrice",_DrivingData.getFuelPrice());
		obj.put("Co2Emission",_DrivingData.getCo2Emission());
		obj.put("HCEmission",_DrivingData.getHCEmission());
		obj.put("PersonCapacity",_DrivingData.getPersonCapacity());
		obj.put("PersonNumber",_DrivingData.getPersonNumber());

        return obj;
	}

    public static DrivingData stringToDrivingData(String _string)
	{
		JSONObject obj = new JSONObject(_string);

        String bateu1km = obj.getString("bateu1km");
        String autoState = obj.getString("autoState");
        int contaDriver= obj.getInt("contaDriver");
		String AutoID = obj.getString("AutoID");
        String DriverID = obj.getString("DriverID");
        String TimeStamp = obj.getString("TimeStamp");
        double X_Position = obj.getDouble("X_Position");
        double Y_Position = obj.getDouble("Y_Position");
        double latitude = obj.getDouble("latitude");
        double longitude = obj.getDouble("longitude");
        String RoadIDSUMO = obj.getString("RoadIDSUMO");
        String RouteIDSUMO = obj.getString("RouteIDSUMO");
        double Speed = obj.getDouble("Speed");
        double Odometer = obj.getDouble("Odometer");
        double FuelConsumption = obj.getDouble("FuelConsumption");
        double AverageFuelConsumption = obj.getDouble("AverageFuelConsumption");
        int FuelType = obj.getInt("FuelType");
        double FuelPrice = obj.getDouble("FuelPrice");
        double Co2Emission = obj.getDouble("Co2Emission");
        double HCEmission = obj.getDouble("HCEmission");
        int PersonCapacity = obj.getInt("PersonCapacity");
        int PersonNumber = obj.getInt("PersonNumber");

        DrivingData carRepport = new DrivingData(bateu1km,autoState,contaDriver, AutoID, DriverID, TimeStamp,X_Position, Y_Position,latitude,longitude, RoadIDSUMO,  RouteIDSUMO, Speed, Odometer, FuelConsumption, AverageFuelConsumption, FuelType, FuelPrice, Co2Emission, HCEmission, PersonCapacity, PersonNumber);

	return carRepport;}}
