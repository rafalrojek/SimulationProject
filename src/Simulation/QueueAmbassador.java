package Simulation;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import model.Car;
import model.Interaction;

public class QueueAmbassador extends Ambassador {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------


    public QueueAmbassador(QueueFederate federate) {
        this.federate = federate;
    }

    @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        try {
            QueueFederate fed = (QueueFederate) federate;
            String interactionName = federate.rtiamb.getInteractionClassName(interactionClass);
            log("Received interactioin : " + interactionName);

            switch(interactionName) {
                case Interaction.NEW_CAR_APPEARED : {
                    String oil = getParameterFromInteraction(theInteraction, Car.TANKS_CODE);
                    boolean isWashing = Boolean.parseBoolean(getParameterFromInteraction(theInteraction, Car.WASH_CODE));
                    int idCar = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CAR_CODE));
                    fed.newCarAppeared(idCar,oil,isWashing);
                    break;
                }
                case Interaction.DISPENSER_AVAILABLE : {
                    int idDistributor = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.DISTRIBUTOR_CODE));
                    fed.distributorAvailable(idDistributor);
                    break;
                }
                case Interaction.PUMPING_ENDED : {
                    int idDistributor = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.DISTRIBUTOR_CODE));
                    int idCar = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CAR_CODE));
                    boolean isWashing = Boolean.parseBoolean(getParameterFromInteraction(theInteraction, Car.WASH_CODE));
                    fed.pumpingEnded(idDistributor,idCar,isWashing);
                    break;
                }
                case Interaction.CASH_BOX_AVAILABLE : {
                    int idCash = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CASH_CODE));
                    fed.cashBoxAvailable(idCash);
                    break;
                }
                case Interaction.PAYMENT_DONE : {
                    int idCar = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CAR_CODE));
                    int idCash = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CASH_CODE));
                    int idDistributor = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.DISTRIBUTOR_CODE));
                    boolean isWashing = Boolean.parseBoolean(getParameterFromInteraction(theInteraction, Car.WASH_CODE));
                    fed.paymentDone(idCar,idCash,idDistributor,isWashing);
                    break;
                }
                case Interaction.DECLARE_NUMBER_OF_CARS : {
                    Integer numberOfCars = Integer.parseInt(EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    log("Number of cars to be handled: " + numberOfCars);
                    fed.setNumberOfCarsToBeHandled(numberOfCars);
                    break;
                }
                case Interaction.LEAVE_SIMULATION : {
                    log("Id of car leaving simulation: " + EncodingHelpers.decodeString(theInteraction.getValue(0)));
                    fed.anotherCarAppeared();
                    break;
                }
                case Interaction.CAR_WASH_RELEASED :
                case Interaction.CAR_WASH_AVAILABLE : {
                    fed.carWashAvailable();
                }
            }
        } catch (InteractionClassNotDefined | RTIinternalError | FederateNotExecutionMember | ArrayIndexOutOfBounds interactionClassNotDefined) {
            interactionClassNotDefined.printStackTrace();
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}
