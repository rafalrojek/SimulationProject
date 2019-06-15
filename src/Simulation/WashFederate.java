package Simulation;

import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import model.Car;
import model.Interaction;

public class WashFederate extends Federate {

    private Car carBeingWashed = null;
    private final int timeOfWashing = 7;
    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public WashFederate(RTIambassador rtiamb, String name, String federationName) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    @Override
    protected void setAmbassador() { fedamb = new WashAmbassador(this); }

    @Override
    protected void runFederateLogic() throws RTIexception {
        advanceTime(1.0);
    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_AVAILABLE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_RELEASED));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.LEAVE_SIMULATION));

        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_CAR_WASH_QUEUE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_OCCUPIED));
    }

    private void sendInteraction(int carId) throws RTIexception{
        SuppliedParameters parameters =
                RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] idCar = EncodingHelpers.encodeString( carId+"");

        int classHandle = rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_RELEASED);
        int idCarHandle = rtiamb.getParameterHandle( "idCar", classHandle );

        // put the values into the collection
        parameters.add(idCarHandle, idCar);

        LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
        rtiamb.sendInteraction( classHandle, parameters, generateTag(), time );
    }

    //@TODO czy dziala bez parametrow?
    private void sendInteraction() throws RTIexception{
        int classHandle = rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_AVAILABLE);
        LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
        rtiamb.sendInteraction( classHandle, null, generateTag(), time );
    }

    public void newCarAtCarWashQueue() throws RTIexception {
        System.out.println("Doszlo do wash");
        if(carBeingWashed==null){
            sendInteraction();
        }
    }

    public void carWashOccupied(int carId) throws RTIexception {
        Car car = new Car();
        car.setIdCar(carId);
        carBeingWashed = car;
        for (int i = 0; i < timeOfWashing; i++) {
            advanceTime(1.0);
        }
        carBeingWashed = null;
        sendInteraction(car.getIdCar());
    }
}
