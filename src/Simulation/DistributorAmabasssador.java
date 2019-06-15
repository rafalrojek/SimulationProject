package Simulation;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import model.Interaction;

public class DistributorAmabasssador extends Ambassador {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------


    public DistributorAmabasssador(DistributorFederate federate) {
        this.federate = federate;
    }

    @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        try {
            String interactionName = federate.rtiamb.getInteractionClassName(interactionClass);
            switch(interactionName) {
                case Interaction.NEW_CAR_AT_DISPENSER_QUEUE : {
                    int idCar = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    String oil = EncodingHelpers.decodeString(theInteraction.getValue(1));
                    boolean isWashing = Boolean.parseBoolean(EncodingHelpers.decodeString(theInteraction.getValue(2)));
                    //TODO: Wywołanie metody
                }
                case Interaction.OCCUPY_DISPENSER : {
                    int idCash = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    String oil = EncodingHelpers.decodeString(theInteraction.getValue(1));
                    boolean isWashing = Boolean.parseBoolean(EncodingHelpers.decodeString(theInteraction.getValue(2)));
                    int idDistributor = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(3)));
                    //TODO: Wywołanie metody

                }
                case Interaction.PAYMENT_DONE : {
                    int idCar = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    int idCash = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(1)));
                    int idDistributor = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(2)));
                    //TODO: Wywołanie metody

                }
            }

        } catch (InteractionClassNotDefined | RTIinternalError | FederateNotExecutionMember | ArrayIndexOutOfBounds interactionClassNotDefined) {
            interactionClassNotDefined.printStackTrace();
        }
    }
}
