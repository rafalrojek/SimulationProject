package Simulation;

import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import model.Interaction;

public class DistributorFederate extends Federate {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public DistributorFederate(RTIambassador rtiamb, String name, String federationName) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    @Override
    protected void setAmbassador() { fedamb = new DistributorAmabasssador(this); }

    @Override
    protected void runFederateLogic() {

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
