package io.sim;
import it.polito.appeal.traci.SumoTraciConnection;
public class Step extends Thread {

    private SumoTraciConnection sumo;
    // Faz a passagem do tempo no SUMO
    // é inicada no EnvSimulator e termina quando a Company fechar
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

                        break;
                    }
            }
        }
        this.sumo.close();
    }
}
