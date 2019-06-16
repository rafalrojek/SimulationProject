package Simulation;

import hla.rti.LogicalTime;
import hla.rti.RTIexception;
import model.Interaction;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class EventDrivenFederate extends Federate {
    Random random = new Random();
    @Override
    protected void runFederateLogic() throws RTIexception {
        while(!endOfSimulation) {
            advanceTime(1.0);
            if (!interactions.isEmpty()) {
                List<Interaction> interactionsToDelete = new LinkedList<>();
                for (int i = 0; i < interactions.size(); i++) {
                    Interaction interaction = interactions.get(i);
                    interaction.setTime(setTime(interaction.getServiceTimeUpperBound()));
                    rtiamb.sendInteraction(interaction.getClassHandle(), interaction.getParams(), interaction.getTag(), interaction.getTime());
                    interactionsToDelete.add(interaction);
                }
                interactions.removeAll(interactionsToDelete);
            }
        }
    }

    private LogicalTime setTime(Integer serviceTimeUpperBound){
        int serviceTime = 0;
        int minTimeOfService = 4;
        if(serviceTimeUpperBound != null){
            serviceTime = random.nextInt((serviceTimeUpperBound - minTimeOfService) + 1) + minTimeOfService;
        }
        return convertTime(fedamb.federateTime + fedamb.federateLookahead + serviceTime);
    }
}
