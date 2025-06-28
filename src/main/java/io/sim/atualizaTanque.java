package io.sim;

import de.tudresden.sumo.cmd.Vehicle;
import it.polito.appeal.traci.SumoTraciConnection;

public class atualizaTanque extends Thread{
    private Auto auto;
    private SumoTraciConnection sumo;

    // Classe que atualiza o tanque de combustivel
    // getFuelConsuption estava tirando muito, por isso padronizeie em 50ml/s

    public atualizaTanque(Auto auto, SumoTraciConnection sumo){
        this.auto = auto;
        this.sumo = sumo;
    
    }

    @Override
    public void run(){
        // long t0 = System.nanoTime();
        // System.out.println("run tanque = " + t0);
       
        confereTanque();       
    }

    private void confereTanque(){

        while(!this.auto.getFinalizado()){
            
            try {           
                Thread.sleep(2000);
                while(this.auto.isOn_off()){

                    System.out.print("");
                    // Caso nao precise abastecer, seta a vel e modo
                    if(!this.auto.getAbastecer()){
                        sumo.do_job_set(Vehicle.setSpeed(this.auto.getIdAuto(), 15));
                        sumo.do_job_set(Vehicle.setSpeedMode(this.auto.getIdAuto(), 31));
                    }

                    while(!this.auto.getAbastecer() && this.auto.isOn_off()){

                        this.auto.setFuelTank(-50);
                        // se menor que 3000 o carro para e espera abastecer
                        if(this.auto.getFuelTank() < 3000){

                            this.auto.setAbastecer(true);
                            System.out.println(this.auto.getIdAuto() + " parou para abastecer");
                            sumo.do_job_set(Vehicle.setSpeed(this.auto.getIdAuto(), 0));
                            
                        }                    
                        Thread.sleep(1000); // a cada um segundo retira 50ml
                        // long t0 = System.nanoTime();
                        // System.out.println("fim tanque = " + t0);
                        } 
                    }
                }
            
            catch (InterruptedException e) {
                e.printStackTrace();
                    } 
            catch (Exception e) {
                e.printStackTrace();
            }}
      
   }
    
}
