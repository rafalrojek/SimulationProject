import Simulation.*;
import hla.rti.RTIambassador;
import hla.rti.RTIinternalError;
import hla.rti.jlc.RtiFactoryFactory;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String federationName = "GasStationFederation";
        int tramsToSimulate = 12;

        try
        {
            //ArrayList<LaneObject> lane = createLane();
            //Statistics stats = new Statistics(lane, tramsToSimulate);

            RTIambassador amb1 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek = new Thread(new CarGeneratorFederate(amb1,"CarGeneratorFederate", federationName));
            watek.start();
            Thread.sleep(8000);

            RTIambassador amb2 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek2 = new Thread(new CashFederate(amb2,"CashFederate", federationName));
            watek2.start();

            RTIambassador amb3 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek3 = new Thread(new DistributorFederate(amb3, "DistributorFederate", federationName));
            watek3.start();

            RTIambassador amb4 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek4 = new Thread(new QueueFederate(amb4, "QueueFederate", federationName));
            watek4.start();

            RTIambassador amb5 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek5 = new Thread(new WashFederate(amb5,"WashFederate", federationName));
            watek5.start();


        } catch (RTIinternalError e)
        {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
