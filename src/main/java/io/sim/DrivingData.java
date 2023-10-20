package io.sim;

import java.security.Timestamp;

public class DrivingData {

	/* SUMO's data */

	private String bateu1km;
	private String autoState;
	private int contaDriver;
	private String autoID;
	private String driverID;
	private double latitude;
	

	private double longitude;
	private String timeStamp; 			// System.currentTimeMillis()
	private double x_Position; 			// sumoPosition2D (x)
	private double y_Position; 			// sumoPosition2D (y)
	private String roadIDSUMO; 			// this.sumo.do_job_get(Vehicle.getRoadID(this.idAuto))
	private String routeIDSUMO; 		// this.sumo.do_job_get(Vehicle.getRouteID(this.idAuto))
	private double speed; 				// in m/s for the last time step
	private double odometer; 			// the distance in (m) that was already driven by this vehicle.
	private double fuelConsumption; 	// in mg/s for the last time step
	private double fuelPrice; 			// price in liters
	private int fuelType; 				// 1-diesel, 2-gasoline, 3-ethanol, 4-hybrid
	private double averageFuelConsumption;
	private int personCapacity;			// the total number of persons that can ride in this vehicle
	private int personNumber;			// the total number of persons which are riding in this vehicle
	private double co2Emission; 		// in mg/s for the last time step
	private double HCEmission; 			// in mg/s for the last time step

	public DrivingData( 

			String bateu1km, String autoState, int contaDriver, String _autoID, String _driverID, String  _timeStamp, double _x_Position, double _y_Position, double _lati, double _long,
			String _roadIDSUMO, String _routeIDSUMO, double _speed, double _odometer, double _fuelConsumption,
			double _averageFuelConsumption, int _fuelType, double _fuelPrice, double _co2Emission, double _HCEmission, int _personCapacity, int _personNumber) 
		{
		
		this.bateu1km = bateu1km;
		this.autoState = autoState;
		this.contaDriver = contaDriver;
		this.autoID = _autoID;
		this.driverID = _autoID;
		this.timeStamp = _timeStamp;
		this.x_Position = _x_Position;
		this.y_Position = _y_Position;
		this.latitude = _lati;
		this.longitude = _long;
		this.roadIDSUMO = _roadIDSUMO;
		this.routeIDSUMO = _routeIDSUMO;
		this.speed = _speed;
		this.odometer = _odometer;
		this.fuelConsumption = _fuelConsumption;
		this.averageFuelConsumption = _averageFuelConsumption;
		this.fuelType = _fuelType;
		this.fuelPrice = _fuelPrice;
		this.co2Emission = _co2Emission;
		this.HCEmission = _HCEmission;
		this.personCapacity = _personCapacity;
		this.personNumber = _personNumber;

	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude(){
		return longitude;
	}

	public String getAutoState(){
		return this.autoState;
	}

	public String getBateu1km(){
		return this.bateu1km;
	}

	public int getContaDriver(){
		return this.contaDriver;
	}

	public Double getSpeed() {
		return this.speed;
	}

	public Double getOdometer() {
		return this.odometer;
	}

	public double getFuelConsumption() {
		return this.fuelConsumption;
	}

	public double getCo2Emission() {
		return this.co2Emission;
	}

	public double getX_Position() {
		return this.x_Position;
	}

	public double getY_Position() {
		return this.y_Position;
	}

	public String getTimeStamp() {
		return this.timeStamp;
	}

	public String getRoadIDSUMO() {
		return this.roadIDSUMO;
	}

	public String getRouteIDSUMO() {
		return this.routeIDSUMO;
	}

	public String getAutoID() {
		return this.autoID;
	}

	public String getRouteID() {
		return this.roadIDSUMO;
	}

	public String getDriverID() {
		return this.driverID;
	}

	public double getHCEmission() {
		return this.HCEmission;
	}

	public double getFuelPrice() {
		return this.fuelPrice;
	}

	public int getFuelType() {
		return this.fuelType;
	}

	public int getPersonCapacity() {
		return this.personCapacity;
	}

	public int getPersonNumber() {
		return this.personNumber;
	}

	public double getAverageFuelConsumption() {
		return this.averageFuelConsumption;
	}

	public void setAverageFuelConsumption(double _averageFuelConsumption) {
		this.averageFuelConsumption = _averageFuelConsumption;
	}
}