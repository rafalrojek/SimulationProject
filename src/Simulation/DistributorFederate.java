package Simulation;

import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import model.Car;
import model.Interaction;

public class DistributorFederate extends EventDrivenFederate {

    private final int numberOfDistributors = 4;
    private final int timeOfPumping = 10;

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
    protected void setAmbassador() { fedamb = new DistributorAmabassador(this); }


    private void registerPumpingEndedInteraction(Car car) throws RTIexception
    {
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] carVal = EncodingHelpers.encodeString(Car.CAR_CODE + car.getIdCar());
        byte[] dispenserVal = EncodingHelpers.encodeString(Car.DISTRIBUTOR_CODE + car.getDistributorId());
        byte[] washVal = EncodingHelpers.encodeString(Car.WASH_CODE + car.isWashing());

        int classHandle = rtiamb.getInteractionClassHandle(Interaction.PUMPING_ENDED);
        int idDispenserHandle = rtiamb.getParameterHandle( "idDispenser", classHandle );
        int idCarHandle = rtiamb.getParameterHandle( "idCar", classHandle );
        int idWashHandle = rtiamb.getParameterHandle( "washing", classHandle );

        // put the values into the collection
        parameters.add(idDispenserHandle, dispenserVal );
        parameters.add(idCarHandle, carVal );
        parameters.add(idWashHandle, washVal);

        log("Sending interaction: " + Interaction.PUMPING_ENDED);
        addInteraction(new Interaction(parameters, classHandle, generateTag(), getTimeOfPumping()));
    }

    private void registerDispenserAvailableInteraction(int dispenserId) throws RTIexception{
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] idDispenser = EncodingHelpers.encodeString(Car.DISTRIBUTOR_CODE + dispenserId);
        int classHandle = rtiamb.getInteractionClassHandle(Interaction.DISPENSER_AVAILABLE);
        int idDispenserHandle = rtiamb.getParameterHandle( "idDispenser", classHandle );
        parameters.add(idDispenserHandle, idDispenser );

        log("Sending interaction: " + Interaction.DISPENSER_AVAILABLE);
        addInteraction(new Interaction(parameters, classHandle, generateTag()));
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
            registerDispenserAvailableInteraction(freeDispenser);
        }
    }

    public void occupyDispenser(int carId, String tank, boolean wash, int distributorId ) throws RTIexception {
        Car car = new Car();
        car.setWashing(wash);
        car.setDistributorId(distributorId);
        car.setIdCar(carId);
        car.setTanks(tank);
        distributors[distributorId] = car;
        registerPumpingEndedInteraction(distributors[distributorId]);
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

    public int getTimeOfPumping() {
        return timeOfPumping;
    }
}
