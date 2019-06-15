package Simulation;

import hla.rti.*;

public class CarGeneratorAmbassador extends Ambassador {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------


    public CarGeneratorAmbassador(CarGeneratorFederate federate) {
        this.federate = federate;
    }

    @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        //Ta klasa nie ma żadnych subksypcji
    }
}
