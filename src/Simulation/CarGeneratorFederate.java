package Simulation;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import model.Interaction;

public class CarGeneratorFederate extends Federate {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public CarGeneratorFederate(RTIambassador rtiamb, String name, String federationName) {
        this.rtiamb = rtiamb;
        this.name = name;
        this.federationName = federationName;
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    @Override
    protected void setAmbassador() { fedamb = new CarGeneratorAmbassador(this); }

    @Override
    protected void runFederateLogic() {

    }

    @Override
    protected void publishAndSubscribe() throws RTIexception {
        rtiamb.publishInteractionClass(rtiamb.getInteractionClassHandle(Interaction.NEW_CAR_APPEARED));
    }
}
