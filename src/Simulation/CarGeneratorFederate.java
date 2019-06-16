package Simulation;
import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import model.Car;
import model.Interaction;

import java.util.Random;

public class CarGeneratorFederate extends Federate {

    private int carId = 1;
    private Random random = new Random();
    private final int percentThatCarWillGoToWash = 100;
    private final int numberOfCarsToGenerate = 10;
    private int numberOfCarsGenerated = 0;
    private int timeBetweenGenerating = 10;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public CarGeneratorFederate(RTIambassador rtiamb, String name, String federationName) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
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
            advanceTime(timeBetweenGenerating);
            sendInteraction(car);
        }
    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_APPEARED));
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
}
