package Simulation;

import hla.rti.EventRetractionHandle;
import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;

public class GUIAmbassador extends Ambassador {
    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------


    public GUIAmbassador(GUIFederate federate) {
        this.federate = federate;
    }

    @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {

    }
}
