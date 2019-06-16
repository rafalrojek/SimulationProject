package Simulation;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import model.Car;
import model.Interaction;

public class DistributorAmabassador extends Ambassador {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------


    public DistributorAmabassador(DistributorFederate federate) {
        this.federate = federate;
    }

    @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        try {
            DistributorFederate fed = (DistributorFederate) federate;
            String interactionName = federate.rtiamb.getInteractionClassName(interactionClass);
            log("Received interaction: " + interactionName);
            switch (interactionName) {
                case Interaction.NEW_CAR_AT_DISPENSER_QUEUE: {
                    int idCar = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CAR_CODE));
                    String oil = getParameterFromInteraction(theInteraction, Car.TANKS_CODE);
                    boolean isWashing = Boolean.parseBoolean(getParameterFromInteraction(theInteraction, Car.WASH_CODE));
                    fed.newCarAtDispenserQueue(idCar,oil,isWashing);
                    break;
                }
                case Interaction.OCCUPY_DISPENSER: {
                    int idCar = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CAR_CODE));
                    String oil = getParameterFromInteraction(theInteraction, Car.TANKS_CODE);
                    boolean isWashing = Boolean.parseBoolean(getParameterFromInteraction(theInteraction, Car.WASH_CODE));
                    int idDistributor = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.DISTRIBUTOR_CODE));
                    fed.occupyDispenser(idCar, oil, isWashing, idDistributor);
                    break;
                }
                case Interaction.PAYMENT_DONE: {
                    int idDistributor = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.DISTRIBUTOR_CODE));
                    fed.paymentDone(idDistributor);
                    break;
                }
            }

        } catch (InteractionClassNotDefined | RTIinternalError | FederateNotExecutionMember | ArrayIndexOutOfBounds interactionClassNotDefined) {
            interactionClassNotDefined.printStackTrace();
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}
