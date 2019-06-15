package Simulation;

import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import model.Interaction;

public class QueueFederate extends Federate  {

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
    protected void setAmbassador() { fedamb = new QueueAmbassador(this); }

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
}
