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
            DistributorFederate fed = (DistributorFederate) federate;
            String interactionName = federate.rtiamb.getInteractionClassName(interactionClass);
            log("Received interaction: " + interactionName);
            for (int i = 0; i < theInteraction.size(); i++) {
                log("interakcja[" + i + "] : " + EncodingHelpers.decodeString(theInteraction.getValue(i)));
            }
            switch (interactionName) {
                case Interaction.NEW_CAR_AT_DISPENSER_QUEUE: {
                    int idCar = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    String oil = EncodingHelpers.decodeString(theInteraction.getValue(1));
                    boolean isWashing = Boolean.parseBoolean(EncodingHelpers.decodeString(theInteraction.getValue(2)));
                    fed.newCarAtDispenserQueue(idCar,oil,isWashing);
                }
                case Interaction.OCCUPY_DISPENSER: {
                    int idCar = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    String oil = EncodingHelpers.decodeString(theInteraction.getValue(1));
                    boolean isWashing = Boolean.parseBoolean(EncodingHelpers.decodeString(theInteraction.getValue(2)));
                    int idDistributor = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(3)));
                    fed.occupyDispenser(idCar, oil, isWashing, idDistributor);
                }
                case Interaction.PAYMENT_DONE: {
//                    int idCar = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
//                    int idCash = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(1)));
                    int idDistributor = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(2)));
//                    boolean isWashing = Boolean.parseBoolean(EncodingHelpers.decodeString(theInteraction.getValue(3)));
                    fed.paymentDone(idDistributor);
                }
            }

        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}
