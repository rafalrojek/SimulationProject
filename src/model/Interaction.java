package model;

import hla.rti.LogicalTime;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;

import java.util.Arrays;

public class Interaction {
    public static final String NEW_CAR_APPEARED = "InteractionRoot.newCarAppeared";
    public static final String NEW_CAR_AT_DISPENSER_QUEUE = "InteractionRoot.newCarAtDispenserQueue";
    public static final String DISPENSER_AVAILABLE = "InteractionRoot.dispenserAvailable";
    public static final String OCCUPY_DISPENSER = "InteractionRoot.occupyDispenser";
    public static final String PUMPING_ENDED = "InteractionRoot.pumpingEnded";
    public static final String NEW_CAR_AT_CASH_BOX_QUEUE = "InteractionRoot.newCarAtCashBoxQueue";
    public static final String CASH_BOX_AVAILABLE = "InteractionRoot.cashBoxAvailable";
    public static final String OCCUPY_CASH_BOX = "InteractionRoot.occupyCashBox";
    public static final String PAYMENT_DONE = "InteractionRoot.paymentDone";
    public static final String NEW_CAR_AT_CAR_WASH_QUEUE = "InteractionRoot.newCarAtCarWashQueue";
    public static final String CAR_WASH_AVAILABLE = "InteractionRoot.carWashAvailable";
    public static final String CAR_WASH_OCCUPIED = "InteractionRoot.carWashOccupied";
    public static final String CAR_WASH_RELEASED = "InteractionRoot.carWasReleased";
    public static final String LEAVE_SIMULATION = "InteractionRoot.leaveSimulation";

    private SuppliedParameters params;
    private int classHandle;
    private byte[] tag;
    private LogicalTime time;

    public Interaction(SuppliedParameters params, int classHandle, byte[] tag){
        this.params = params;
        this.classHandle = classHandle;
        this.tag = tag;
    }

    public void setTime(LogicalTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Interaction{" +
                "params=" + params +
                ", classHandle=" + classHandle +
                ", tag=" + Arrays.toString(tag) +
                ", time=" + time +
                '}';
    }

    public SuppliedParameters getParams() {
        return params;
    }

    public int getClassHandle() {
        return classHandle;
    }

    public byte[] getTag() {
        return tag;
    }

    public LogicalTime getTime() {
        return time;
    }
}