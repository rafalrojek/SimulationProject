package Simulation;

import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import model.Car;
import model.Interaction;

public class DistributorFederate extends Federate {

    private static final int numberOfDistributors = 4;
    private static final int timeOfPumping = 10;

    private Car[] distributors;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public DistributorFederate(RTIambassador rtiamb, String name, String federationName) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
        distributors = new Car[numberOfDistributors];
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    @Override
    protected void setAmbassador() { fedamb = new DistributorAmabasssador(this); }

    @Override
    protected void runFederateLogic() throws RTIexception {
        while(!endOfSimulation){
            advanceTime(1.0);
        }
    }

    private void sendInteraction(Car car) throws RTIexception
    {
        SuppliedParameters parameters =
                RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] carId = EncodingHelpers.encodeString( (car.getIdCar())+"" );
        byte[] idDispenser = EncodingHelpers.encodeString( (car.getDistributorId()+""));
        byte[] wash = EncodingHelpers.encodeString( (car.isWashing() + ""));

        int classHandle = rtiamb.getInteractionClassHandle(Interaction.PUMPING_ENDED);
        int idDispenserHandle = rtiamb.getParameterHandle( "idDispenser", classHandle );
        int idCarHandle = rtiamb.getParameterHandle( "idCar", classHandle );
        int idWashHandle = rtiamb.getParameterHandle( "washing", classHandle );

        // put the values into the collection
        parameters.add(idDispenserHandle, idDispenser );
        parameters.add(idCarHandle, carId );
        parameters.add(idWashHandle, wash);

        LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
        log("Sending interaction: " + Interaction.PUMPING_ENDED);
        rtiamb.sendInteraction( classHandle, parameters, generateTag(), time );
    }

    private void sendInteraction(int dispenserId) throws RTIexception{
        SuppliedParameters parameters =
                RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] idDispenser = EncodingHelpers.encodeString( dispenserId+"");

        int classHandle = rtiamb.getInteractionClassHandle(Interaction.DISPENSER_AVAILABLE);
        int idDispenserHandle = rtiamb.getParameterHandle( "idDispenser", classHandle );

        // put the values into the collection
        parameters.add(idDispenserHandle, idDispenser );

        LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
        log("Sending interaction: " + Interaction.DISPENSER_AVAILABLE);
        rtiamb.sendInteraction( classHandle, parameters, generateTag(), time );
    }

    private int isAnyDispenserFree(){
        for (int i = 0; i < numberOfDistributors; i++) {
            if(distributors[i] == null) return i;
        }
        return -1;
    }


    public void newCarAtDispenserQueue(int carId, String fuel, boolean washing) throws RTIexception {
        int freeDispenser = isAnyDispenserFree();
        if(freeDispenser != -1){
            sendInteraction(freeDispenser);
        }
    }

    public void occupyDispenser(int carId, String tank, boolean wash, int distributorId ) throws RTIexception {
        Car car = new Car();
        car.setWashing(wash);
        car.setDistributorId(distributorId);
        car.setIdCar(carId);
        car.setTanks(tank);
        distributors[distributorId] = car;
        for (int i = 0; i < timeOfPumping; i++) {
            advanceTime(1.0);
        }
        sendInteraction(distributors[distributorId]);
    }

    public void paymentDone(int distributorId){
        distributors[distributorId] = null;
    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.DISPENSER_AVAILABLE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.PUMPING_ENDED));

        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_DISPENSER_QUEUE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.OCCUPY_DISPENSER));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.PAYMENT_DONE));
    }
}
