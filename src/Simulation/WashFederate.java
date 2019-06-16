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

public class WashFederate extends Federate {

    private int idCarBeingWashed = -1;
    private double carWashOccupiedTill = -1.0;
    private int timeOfWashing;
    private int washingTimeLowerBound;
    private Random random = new Random();
    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public WashFederate(RTIambassador rtiamb, String name, String federationName, int timeOfWashing) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
        this.timeOfWashing = timeOfWashing;
        washingTimeLowerBound = timeOfWashing - 3;
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    @Override
    protected void setAmbassador() { fedamb = new WashAmbassador(this); }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_AVAILABLE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_RELEASED));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.LEAVE_SIMULATION));

        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_CAR_WASH_QUEUE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_OCCUPIED));
    }

    protected void runFederateLogic() throws RTIexception {
        while(!endOfSimulation) {
            advanceTime(1.0);
            checkIfSomeoneIsntFinishingtheWashing();
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

    private void checkIfSomeoneIsntFinishingtheWashing() throws RTIexception {
        if(idCarBeingWashed != -1)
        {
            if((carWashOccupiedTill) == fedamb.federateTime){
                registerCarWashReleasedInteraction(idCarBeingWashed);
                resisterLeaveSimulationInteraction(idCarBeingWashed);
                carWashOccupiedTill = -1.0;
                idCarBeingWashed = -1;
            }
        }
    }

    private void resisterLeaveSimulationInteraction(int carId) throws RTIexception{
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        byte[] idCar = EncodingHelpers.encodeString(Car.CAR_CODE + carId);
        int classHandle = rtiamb.getInteractionClassHandle(Interaction.LEAVE_SIMULATION);
        int idCarHandle = rtiamb.getParameterHandle( "idCar", classHandle );
        parameters.add(idCarHandle, idCar);
        log("Sending interation : " + Interaction.LEAVE_SIMULATION);
        addInteraction(new Interaction(parameters, classHandle, generateTag()));
    }

    private void registerCarWashReleasedInteraction(int carId) throws RTIexception{
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();

        byte[] idCar = EncodingHelpers.encodeString(Car.CAR_CODE + carId);

        int classHandle = rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_RELEASED);
        int idCarHandle = rtiamb.getParameterHandle( "idCar", classHandle );
        parameters.add(idCarHandle, idCar);

        log("Sending interation : " + Interaction.CAR_WASH_RELEASED);
        addInteraction(new Interaction(parameters, classHandle, generateTag()));
    }

    private void registerCarWashAvailableInteraction() throws RTIexception{
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        int classHandle = rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_AVAILABLE);
        log("Sending interation : " + Interaction.CAR_WASH_AVAILABLE);
        addInteraction(new Interaction(parameters, classHandle, generateTag()));
    }

    public void newCarAtCarWashQueue() throws RTIexception {
        if(idCarBeingWashed == -1){
            registerCarWashAvailableInteraction();
        }
    }

    public void carWashOccupied(int carId) throws RTIexception {
        idCarBeingWashed = carId;
        carWashOccupiedTill = fedamb.federateTime + getTimeOfWashing();
    }

    public int getTimeOfWashing() {
        return random.nextInt((timeOfWashing - washingTimeLowerBound) + 1) + washingTimeLowerBound;
    }
}
