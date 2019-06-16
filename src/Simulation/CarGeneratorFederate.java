package Simulation;
import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import model.Car;
import model.Interaction;

import java.util.Random;

public class CarGeneratorFederate extends Federate {

    private int carId = 1;
    private Random random = new Random();
    private int percentThatCarWillGoToWash;
    private int numberOfCarsToGenerate;
    private int numberOfCarsGenerated = 0;
    private int timeBetweenGenerating;
    private int lowerBound;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public CarGeneratorFederate(RTIambassador rtiamb, String name, String federationName,
                                int percentThatCarWillGoToWash, int numberOfCarsToGenerate, int timeBetweenGenerating) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
        this.numberOfCarsToGenerate = numberOfCarsToGenerate;
        this.timeBetweenGenerating = timeBetweenGenerating;
        this.percentThatCarWillGoToWash = percentThatCarWillGoToWash;
        this.lowerBound = timeBetweenGenerating - 3;
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    @Override
    protected void setAmbassador() {
        fedamb = new CarGeneratorAmbassador(this);
    }

    @Override
    protected void runFederateLogic() throws RTIexception{
        while(numberOfCarsGenerated++ < numberOfCarsToGenerate){
            Car car = generateCar();
            advanceTime(getTimeBetweenGenerating());
            sendInteraction(car);
        }
        showStatistics();
    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_APPEARED));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.LAST_GENERATED));
    }

    private Car generateCar(){
        Car car = new Car();
        car.setIdCar(carId++);
        double rand = random.nextDouble();
        if(rand < 0.33) car.setTanks(Car.GAS);
        else if(rand < 0.66) car.setTanks(Car.ON);
        else car.setTanks(Car.PETROL);
        car.setWashing(random.nextDouble() < ((double) percentThatCarWillGoToWash / 100));
        return car;
    }
//O G P
    private void sendInteraction(Car car) throws RTIexception
    {
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] carVal = EncodingHelpers.encodeString((Car.CAR_CODE + car.getIdCar()));
        byte[] tanksVal = EncodingHelpers.encodeString((Car.TANKS_CODE + car.getTanks()));
        byte[] washVal = EncodingHelpers.encodeString((Car.WASH_CODE + car.isWashing()));

        int classHandle = rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_APPEARED);
        int idCarHandle = rtiamb.getParameterHandle( "idCar", classHandle );
        int idTanksHandle = rtiamb.getParameterHandle( "tanks", classHandle );
        int idWashHandle = rtiamb.getParameterHandle( "washing", classHandle );

        // put the values into the collection
        parameters.add(idCarHandle, carVal );
        parameters.add(idTanksHandle, tanksVal );
        parameters.add(idWashHandle, washVal);

        //////////////////////////
        // send the interaction //
        //////////////////////////
        //rtiamb.sendInteraction( classHandle, parameters, generateTag() );
        // if you want to associate a particular timestamp with the
        // interaction, you will have to supply it to the RTI. Here
        // we send another interaction, this time with a timestamp:
        LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
        log("Sending generated car: " + car);
        rtiamb.sendInteraction( classHandle, parameters, generateTag(), time );
    }

    private void showStatistics() throws RTIexception {
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        int classHandle = rtiamb.getInteractionClassHandle(Interaction.LAST_GENERATED);
        LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
        log("Sending interaction: " + Interaction.LAST_GENERATED);
        rtiamb.sendInteraction(classHandle, parameters, generateTag(), time);
    }

    public int getTimeBetweenGenerating() {
        return random.nextInt((timeBetweenGenerating - lowerBound) + 1) + lowerBound;
    }
}
