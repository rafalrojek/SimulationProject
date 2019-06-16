package Simulation;

import hla.rti.RTIexception;
import model.Interaction;

import java.util.LinkedList;
import java.util.List;

public abstract class EventDrivenFederate extends Federate {
    @Override
    protected void runFederateLogic() throws RTIexception {
        while(!endOfSimulation) {
            advanceTime(1.0);
            if (!interactions.isEmpty()) {
                List<Interaction> interactionsToDelete = new LinkedList<>();
                for (int i = 0; i < interactions.size(); i++) {
                    Interaction interaction = interactions.get(i);
                    log(interaction.toString());
                    interaction.setTime(convertTime(fedamb.federateTime + fedamb.federateLookahead));
                    rtiamb.sendInteraction(interaction.getClassHandle(), interaction.getParams(), interaction.getTag(), interaction.getTime());
                    interactionsToDelete.add(interaction);
                }
                interactions.removeAll(interactionsToDelete);
            }
        }
    }

    protected void addInteraction(Interaction interaction){
        interactions.add(interaction);
    }
}
