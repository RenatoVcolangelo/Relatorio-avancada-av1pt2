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
            // long t1 = System.nanoTime();
       		// System.out.println("inicio loop = " + t1);
				
            if(!sumo.isClosed()){
                try {
                        Thread.sleep(100);
                        this.sumo.do_timestep();
                    } catch (Exception e) {

                        break;
                    }
            }
            // long t2 = System.nanoTime();
       		// System.out.println("fim loop = " + t2);
        }
        this.sumo.close();
    }
}
