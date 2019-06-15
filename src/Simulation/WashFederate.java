package Simulation;

import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import model.Interaction;

public class WashFederate extends Federate {

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
    protected void runFederateLogic() {

    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_AVAILABLE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_RELEASED));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.LEAVE_SIMULATION));

        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_CAR_WASH_QUEUE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CAR_WASH_OCCUPIED));
    }
}
