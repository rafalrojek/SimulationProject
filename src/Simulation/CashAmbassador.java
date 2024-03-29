package Simulation;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import model.Car;
import model.Interaction;

public class CashAmbassador extends Ambassador {

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------


    public CashAmbassador(CashFederate federate) {
        this.federate = federate;
    }

    @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        try {
            CashFederate fed = (CashFederate) federate;
            String interactionName = federate.rtiamb.getInteractionClassName(interactionClass);
            log("Received interaction: " + interactionName);
            switch(interactionName) {
                case Interaction.NEW_CAR_AT_CASH_BOX_QUEUE : {
                    int idCar = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CAR_CODE));
                    boolean isWashing = Boolean.parseBoolean(getParameterFromInteraction(theInteraction, Car.WASH_CODE));
                    int idDistributor = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.DISTRIBUTOR_CODE));
                    fed.newCarAtCashBoxQueue(idCar,isWashing,idDistributor);
                    break;
                }
                case Interaction.OCCUPY_CASH_BOX : {
                    int idCar = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CAR_CODE));
                    int idCash = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.CASH_CODE));
                    boolean isWashing = Boolean.parseBoolean(getParameterFromInteraction(theInteraction, Car.WASH_CODE));
                    int idDistributor = Integer.parseInt(getParameterFromInteraction(theInteraction, Car.DISTRIBUTOR_CODE));
                    fed.occupyCashBox(idCash,idCar,idDistributor,isWashing);
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
