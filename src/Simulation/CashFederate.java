package Simulation;

import hla.rti.LogicalTime;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import model.Car;
import model.Interaction;

public class CashFederate extends Federate {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------
    private Car[] cashboxes;
    private final int numberOfCashboxes = 2;
    private final int payingTime = 4;

    public CashFederate(RTIambassador rtiamb, String name, String federationName) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
        cashboxes = new Car[numberOfCashboxes];
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------
    @Override
    protected void setAmbassador() { fedamb = new CashAmbassador(this); }

    @Override
    protected void runFederateLogic() throws RTIexception {
        while(!endOfSimulation){
            advanceTime(1.0);
        }
    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CASH_BOX_AVAILABLE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.PAYMENT_DONE));

        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_CASH_BOX_QUEUE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.OCCUPY_CASH_BOX));
    }

    private void sendInteraction(Car car) throws RTIexception
    {
        SuppliedParameters parameters =
                RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] carId = EncodingHelpers.encodeString( (car.getIdCar())+"" );
        byte[] idDispenser = EncodingHelpers.encodeString( (car.getDistributorId()+""));
        byte[] wash = EncodingHelpers.encodeString( (car.isWashing() + ""));
        byte[] cash = EncodingHelpers.encodeString( (car.getCashBox() + ""));

        int classHandle = rtiamb.getInteractionClassHandle(Interaction.PAYMENT_DONE);
        int idCarHandle = rtiamb.getParameterHandle( "idCar", classHandle );
        int idDispenserHandle = rtiamb.getParameterHandle( "idDispenser", classHandle );
        int idWashHandle = rtiamb.getParameterHandle( "washing", classHandle );
        int idCashHandle = rtiamb.getParameterHandle( "idCash", classHandle );

        // put the values into the collection
        parameters.add(idCashHandle, cash);
        parameters.add(idCarHandle, carId );
        parameters.add(idDispenserHandle, idDispenser );
        parameters.add(idWashHandle, wash);


        LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
        rtiamb.sendInteraction( classHandle, parameters, generateTag(), time );
    }

    private void sendInteraction(int cashId) throws RTIexception{
        SuppliedParameters parameters =
                RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] idCash = EncodingHelpers.encodeString( cashId+"");

        int classHandle = rtiamb.getInteractionClassHandle(Interaction.CASH_BOX_AVAILABLE);
        int idCashHandle = rtiamb.getParameterHandle( "idCash", classHandle );

        // put the values into the collection
        parameters.add(idCashHandle, idCash );

        LogicalTime time = convertTime( fedamb.federateTime + fedamb.federateLookahead );
        rtiamb.sendInteraction( classHandle, parameters, generateTag(), time );
    }

    public void newCarAtCashBoxQueue(int carId, boolean washing, int distributorId) throws RTIexception {
        int freeCashbox = getFreeCashbox();
        if(freeCashbox != -1){
            sendInteraction(freeCashbox);
        }
    }

    private int getFreeCashbox(){
        for (int i = 0; i < numberOfCashboxes; i++) {
            if (cashboxes[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void occupyCashBox(int cashId, int carId, int distributorId, boolean washing) throws RTIexception {
        Car car = new Car();
        car.setIdCar(carId);
        car.setDistributorId(distributorId);
        car.setWashing(washing);
        cashboxes[cashId] = car;
        for (int i = 0; i < payingTime; i++) {
            advanceTime(1.0);
        }
        cashboxes[cashId] = null;
        sendInteraction(car);
    }
}
