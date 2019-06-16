package Simulation;

import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import model.Car;
import model.Interaction;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CashFederate extends Federate {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------
    private Car[] cashboxes;
    private double[] cashboxesOccupiedTill;
    private int numberOfCashboxes;
    private int payingTime;
    private int payingTimeLowerBound;
    private Random random = new Random();

    public CashFederate(RTIambassador rtiamb, String name, String federationName, int numberOfCashboxes, int payingTime) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
        this.numberOfCashboxes = numberOfCashboxes;
        this.payingTime = payingTime;
        this.payingTimeLowerBound = payingTime - 3;

        cashboxes = new Car[numberOfCashboxes];
        cashboxesOccupiedTill = new double[numberOfCashboxes];
        for (int i = 0; i < numberOfCashboxes; i++) {
            cashboxesOccupiedTill[i] = -1.0;
        }
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------
    @Override
    protected void setAmbassador() { fedamb = new CashAmbassador(this); }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CASH_BOX_AVAILABLE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.PAYMENT_DONE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.LEAVE_SIMULATION));

        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_CASH_BOX_QUEUE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.OCCUPY_CASH_BOX));
    }

    protected void runFederateLogic() throws RTIexception {
        while(!endOfSimulation) {
            advanceTime(1.0);
            checkIfSomeoneHasntFinishedPaying();
            if (!interactions.isEmpty()) {
                List<Interaction> interactionsToDelete = new LinkedList<>();
                for (int i = 0; i < interactions.size(); i++) {
                    Interaction interaction = interactions.get(i);
                    rtiamb.sendInteraction(interaction.getClassHandle(), interaction.getParams(), interaction.getTag(), interaction.getTime());
                    interactionsToDelete.add(interaction);
                }
                interactions.removeAll(interactionsToDelete);
            }
        }
    }

    private void checkIfSomeoneHasntFinishedPaying() throws RTIexception {
        for (int i = 0; i < numberOfCashboxes; i++) {
            if(cashboxesOccupiedTill[i] != -1.0  )
            {
                if((cashboxesOccupiedTill[i]) == fedamb.federateTime){
                    registerPaymentDoneInteraction(cashboxes[i]);
                    registerLeaveSimulation(cashboxes[i].getIdCar());
                    cashboxesOccupiedTill[i] = -1.0;
                    cashboxes[i] = null;
                }
            }
        }
    }

    private void registerPaymentDoneInteraction(Car car) throws RTIexception
    {
        SuppliedParameters parameters =
                RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] carId = EncodingHelpers.encodeString(Car.CAR_CODE + car.getIdCar());
        byte[] idDispenser = EncodingHelpers.encodeString(Car.DISTRIBUTOR_CODE + car.getDistributorId());
        byte[] wash = EncodingHelpers.encodeString(Car.WASH_CODE + car.isWashing());
        byte[] cash = EncodingHelpers.encodeString(Car.CASH_CODE + car.getCashBox());

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

        addInteraction(new Interaction(parameters, classHandle, generateTag()));
    }

    private void registerCashBoxAvailableInteraction(int cashId) throws RTIexception{
        SuppliedParameters parameters =
                RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] idCash = EncodingHelpers.encodeString(Car.CASH_CODE + cashId);

        int classHandle = rtiamb.getInteractionClassHandle(Interaction.CASH_BOX_AVAILABLE);
        int idCashHandle = rtiamb.getParameterHandle( "idCash", classHandle );

        parameters.add(idCashHandle, idCash );

        addInteraction(new Interaction(parameters, classHandle, generateTag()));
    }

    private void registerLeaveSimulation(int carId) throws RTIexception{
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        byte[] idCar = EncodingHelpers.encodeString(Car.CAR_CODE + carId);
        int classHandle = rtiamb.getInteractionClassHandle(Interaction.LEAVE_SIMULATION);
        int idCarHandle = rtiamb.getParameterHandle( "idCar", classHandle );
        parameters.add(idCarHandle, idCar );
        addInteraction(new Interaction(parameters, classHandle, generateTag()));
    }

    public void newCarAtCashBoxQueue(int carId, boolean washing, int distributorId) throws RTIexception {
        int freeCashbox = getFreeCashbox();
        if(freeCashbox != -1){
            registerCashBoxAvailableInteraction(freeCashbox);
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
        cashboxesOccupiedTill[cashId] = fedamb.federateTime + getPayingTime();
    }

    public int getPayingTime() {
        return random.nextInt((payingTime - payingTimeLowerBound) + 1) + payingTimeLowerBound;
    }
}
