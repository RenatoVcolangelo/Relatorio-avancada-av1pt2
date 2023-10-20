package io.sim;

import de.tudresden.sumo.cmd.Vehicle;
import it.polito.appeal.traci.SumoTraciConnection;

public class atualizaTanque extends Thread{
    private Auto auto;
    private SumoTraciConnection sumo;

    public atualizaTanque(Auto auto, SumoTraciConnection sumo){
        this.auto = auto;
        this.sumo = sumo;
    
    }

    @Override
    public void run(){
       
        confereTanque();

        
    }

    private void confereTanque(){

        while(!this.auto.getFinalizado()){
          
            try {           
                
                while(this.auto.isOn_off()){
                    if(!this.auto.getAbastecer()){
                        sumo.do_job_set(Vehicle.setSpeed(this.auto.getIdAuto(), 15));
                        sumo.do_job_set(Vehicle.setSpeedMode(this.auto.getIdAuto(), 31));}

                    while(!this.auto.getAbastecer() && this.auto.isOn_off()){

                        this.auto.setFuelTank(-30);
                        if(this.auto.getFuelTank() < 3000){
                            this.auto.setAbastecer(true);
                            sumo.do_job_set(Vehicle.setSpeed(this.auto.getIdAuto(), 0));
                            
                        }                    
                        Thread.sleep(1000);
                        } 
            }
        }
            
            catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {

                e.printStackTrace();
            }}
    
        
   }
    
}
