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
            WashFederate fed = (WashFederate) federate;
            String interactionName = federate.rtiamb.getInteractionClassName(interactionClass);
            log("Received interactioin : " + interactionName);
            for (int i = 0; i < theInteraction.size(); i++) {
                log("interakcja[" + i + "] : " + EncodingHelpers.decodeString(theInteraction.getValue(i)));
            }
            switch(interactionName) {
                case Interaction.NEW_CAR_AT_CAR_WASH_QUEUE : {
                    //int idCar = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    fed.newCarAtCarWashQueue();
                }
                case Interaction.CAR_WASH_OCCUPIED : {
                    int idCar = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    fed.carWashOccupied(idCar);
                }
            }

        } catch (InteractionClassNotDefined | RTIinternalError | FederateNotExecutionMember | ArrayIndexOutOfBounds interactionClassNotDefined) {
            interactionClassNotDefined.printStackTrace();
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}
