package Simulation;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import model.Interaction;

public class WashAmbassador extends Ambassador{

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------


    public WashAmbassador(WashFederate federate) {
        this.federate = federate;
    }

    @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        try {
            String interactionName = federate.rtiamb.getInteractionClassName(interactionClass);
            switch(interactionName) {
                case Interaction.NEW_CAR_AT_CAR_WASH_QUEUE : {
                    int idCar = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    //TODO: Wywołanie metody
                }
                case Interaction.CAR_WASH_OCCUPIED : {
                    int idCash = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    //TODO: Wywołanie metody

                }
            }

        } catch (InteractionClassNotDefined | RTIinternalError | FederateNotExecutionMember | ArrayIndexOutOfBounds interactionClassNotDefined) {
            interactionClassNotDefined.printStackTrace();
        }

    }
}
