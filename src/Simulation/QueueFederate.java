package Simulation;

import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import model.Car;
import model.Interaction;

import java.util.LinkedList;
import model.Interaction;

public class QueueFederate extends Federate  {

    private LinkedList<Car> distributorQueue = new LinkedList<>();
    private LinkedList<Car> cashQueue = new LinkedList<>();
    private LinkedList<Car> washQueue = new LinkedList<>();
    //add - to the end
    //poll - z poczatku usuwa i zwraca




    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public QueueFederate(RTIambassador rtiamb, String name, String federationName) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    @Override
    protected void setAmbassador() {
        fedamb = new QueueAmbassador(this);
    }

    @Override
    protected void runFederateLogic() {

    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_DISPENSER_QUEUE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.OCCUPY_DISPENSER));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_CASH_BOX_QUEUE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.OCCUPY_CASH_BOX));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_CAR_WASH_QUEUE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_OCCUPIED));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_RELEASED));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.LEAVE_SIMULATION));

        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_APPEARED));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.DISPENSER_AVAILABLE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.PUMPING_ENDED));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CASH_BOX_AVAILABLE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.PAYMENT_DONE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_AVAILABLE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_RELEASED));
    }

    private void sendInteraction(Car car, String interaction) throws RTIexception //typ interakcji (Arrival,Departue), typ obiektu (Station, Passing), id tramwaju i id stacji/mijanki
    {
        SuppliedParameters parameters =
                RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        final String interactionType = interaction;

        //car id, common for all interactions
        LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
        int classHandle = rtiamb.getInteractionClassHandle("InteractionRoot." + Interaction.NEW_CAR_APPEARED);
        byte[] carId = EncodingHelpers.encodeString( (car.getIdCar())+"" );
        int idCarHandle = rtiamb.getParameterHandle( "idCar", classHandle );
        parameters.add(idCarHandle, carId );

        byte[] tanks,wash,dispenser,cash;
        int idTanksHandle,idWashHandle,idDispenser,idCash;

        switch(interactionType){
            case Interaction.NEW_CAR_AT_DISPENSER_QUEUE :
                tanks = EncodingHelpers.encodeString( (car.getTanks()));
                        idTanksHandle = rtiamb.getParameterHandle( "tanks", classHandle );
                parameters.add(idTanksHandle, tanks );

                wash = EncodingHelpers.encodeString( (car.isWashing() + ""));
                idWashHandle = rtiamb.getParameterHandle( "washing", classHandle );
                parameters.add(idWashHandle, wash);
                break;

            case Interaction.OCCUPY_DISPENSER :
                tanks = EncodingHelpers.encodeString( (car.getTanks()));
                idTanksHandle = rtiamb.getParameterHandle( "tanks", classHandle );
                parameters.add(idTanksHandle, tanks );

                wash = EncodingHelpers.encodeString( (car.isWashing() + ""));
                idWashHandle = rtiamb.getParameterHandle( "washing", classHandle );
                parameters.add(idWashHandle, wash);

                dispenser = EncodingHelpers.encodeString( (car.isWashing() + ""));
                idDispenser = rtiamb.getParameterHandle( "idDispenser", classHandle );
                parameters.add(idDispenser, dispenser);
                break;

            case Interaction.NEW_CAR_AT_CASH_BOX_QUEUE :
                wash = EncodingHelpers.encodeString( (car.isWashing() + ""));
                idWashHandle = rtiamb.getParameterHandle( "washing", classHandle );
                parameters.add(idWashHandle, wash);

                dispenser = EncodingHelpers.encodeString( (car.isWashing() + ""));
                idDispenser = rtiamb.getParameterHandle( "idDispenser", classHandle );
                parameters.add(idDispenser, dispenser);
                break;

            case Interaction.OCCUPY_CASH_BOX :
                wash = EncodingHelpers.encodeString( (car.isWashing() + ""));
                idWashHandle = rtiamb.getParameterHandle( "washing", classHandle );
                parameters.add(idWashHandle, wash);

                dispenser = EncodingHelpers.encodeString( (car.isWashing() + ""));
                idDispenser = rtiamb.getParameterHandle( "idDispenser", classHandle );
                parameters.add(idDispenser, dispenser);

                cash = EncodingHelpers.encodeString( (car.isWashing() + ""));
                idCash = rtiamb.getParameterHandle( "idCash", classHandle );
                parameters.add(idCash, cash);
                break;

            case Interaction.NEW_CAR_AT_CAR_WASH_QUEUE :
            case Interaction.CAR_WASH_OCCUPIED :
            case Interaction.CAR_WASH_RELEASED :
            case Interaction.LEAVE_SIMULATION :
                break;
            default:
                throw new IllegalArgumentException("Wrong interaction sent to QueueFederate");
        }

        //////////////////////////
        // send the interaction //
        //////////////////////////
        //rtiamb.sendInteraction( classHandle, parameters, generateTag() );
        // if you want to associate a particular timestamp with the
        // interaction, you will have to supply it to the RTI. Here
        // we send another interaction, this time with a timestamp:
        rtiamb.sendInteraction(classHandle, parameters, generateTag(), time );
    }

    public void newCarAppeared(int carId, String tanks, boolean washing) throws RTIexception {
        Car car = new Car();
        car.setIdCar(carId);
        car.setWashing(washing);
        car.setTanks(tanks);
        distributorQueue.add(car);
        sendInteraction(car,Interaction.NEW_CAR_AT_DISPENSER_QUEUE);
    }

    public void distributorAvailable(int distributorId) throws RTIexception {
        if(!distributorQueue.isEmpty()){
            Car car = distributorQueue.poll();
            car.setDistributorId(distributorId);
            sendInteraction(car,Interaction.OCCUPY_DISPENSER);
        }
    }

    public void pumpingEnded(int distributorId, int carId, boolean washing) throws RTIexception {
        Car car = new Car();
        car.setIdCar(carId);
        car.setWashing(washing);
        car.setDistributorId(distributorId);
        distributorQueue.add(car);
        sendInteraction(car,Interaction.NEW_CAR_AT_CASH_BOX_QUEUE);
    }

    public void cashBoxAvailable(int cashId) throws RTIexception {
        if(!cashQueue.isEmpty()){
            Car car = distributorQueue.poll();
            car.setCashBox(cashId);
            sendInteraction(car,Interaction.OCCUPY_CASH_BOX);
        }
    }

    public void paymentDone(int carId, int cashId, int distributorId, boolean washing) throws RTIexception {
        if(washing){
            Car car = new Car();
            car.setIdCar(carId);
            car.setCashBox(cashId);
            car.setDistributorId(distributorId);
            car.setWashing(washing);
            washQueue.add(car);
            sendInteraction(car,Interaction.NEW_CAR_AT_CAR_WASH_QUEUE);
        }
        else{
            //leaves simulation in GUI
        }
    }

    public void carWashAvailable() throws RTIexception {
        if(!washQueue.isEmpty()){
            Car car = distributorQueue.poll();
            sendInteraction(car,Interaction.CAR_WASH_OCCUPIED);
        }
    }
}
