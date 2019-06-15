package Simulation;

import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import model.Interaction;

public class CashFederate extends Federate {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public CashFederate(RTIambassador rtiamb, String name, String federationName) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------
    @Override
    protected void setAmbassador() { fedamb = new CashAmbassador(this); }

    @Override
    protected void runFederateLogic() {

    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.CASH_BOX_AVAILABLE));
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.PAYMENT_DONE));

        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_AT_CASH_BOX_QUEUE));
        rtiamb.subscribeInteractionClass(rtiamb.getInteractionClassHandle(Interaction.OCCUPY_CASH_BOX));
    }
}
