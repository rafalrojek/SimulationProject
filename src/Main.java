import Simulation.*;
import hla.rti.RTIambassador;
import hla.rti.RTIinternalError;
import hla.rti.jlc.RtiFactoryFactory;

public class Main {
    public static void main(String[] args) {
        String federationName = "GasStationFederation";

        final int percentThatCarWillGoToWash = 100;
        final int numberOfCarsToGenerate = 2;
        final int numberOfCashboxes = 1;
        final int numberOfDistributors = 2;
        final int distributorQueueMaxSize = 5;

        final int timeBetweenGenerating = 7;
        final int timeOfPumping = 7;
        final int payingTime = 7;
        final int timeOfWashing = 7;

        if(timeBetweenGenerating < 3 || timeOfPumping < 3 || payingTime < 3 || timeOfWashing < 3)
            throw new IllegalArgumentException("Wrong starting parameters. Service times should be last minimum 3 seconds.");

        try
        {
            RTIambassador amb1 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek = new Thread(new CarGeneratorFederate(amb1,"CarGeneratorFederate", federationName,
                    percentThatCarWillGoToWash, numberOfCarsToGenerate, timeBetweenGenerating));
            watek.start();
            Thread.sleep(6000);

            RTIambassador amb2 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek2 = new Thread(new CashFederate(amb2,"CashFederate", federationName, numberOfCashboxes, payingTime));
            watek2.start();

            RTIambassador amb3 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek3 = new Thread(new DistributorFederate(amb3, "DistributorFederate", federationName, numberOfDistributors, timeOfPumping ));
            watek3.start();

            RTIambassador amb4 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek4 = new Thread(new QueueFederate(amb4, "QueueFederate", federationName, distributorQueueMaxSize));
            watek4.start();

            RTIambassador amb5 = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();
            Thread watek5 = new Thread(new WashFederate(amb5,"WashFederate", federationName, timeOfWashing));
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
