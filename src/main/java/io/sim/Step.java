package io.sim;
import it.polito.appeal.traci.SumoTraciConnection;
public class Step extends Thread {
    private SumoTraciConnection sumo;
    public Step(SumoTraciConnection sumo){
        this.sumo = sumo;
    }

    @Override
    public void run(){
        while(Company.ativo){
				
            if(!sumo.isClosed()){
                try {
                        Thread.sleep(50);
                        this.sumo.do_timestep();
                    } catch (Exception e) {
                        //e.printStackTrace();
                        break;
                    }
            }
        }
        this.sumo.close();
    }
}
