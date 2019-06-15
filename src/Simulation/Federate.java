package Simulation;

import hla.rti.*;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class Federate {
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    /** The sync point all federates will sync up on before starting */
    public static final String READY_TO_RUN = "ReadyToRun";

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    public RTIambassador rtiamb;
    public Ambassador fedamb;
    protected String name;
    protected boolean endOfSimulation = false;
    protected String federationName;

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    /**
     * This is just a helper method to make sure all logging it output in the same form
     */
    protected void log( String message )
    {
        System.out.println( "GUIFederate   : " + message );
    }

    /**
     * This method will block until the user presses enter
     */
    protected void waitForUser() {
        log( " >>>>>>>>>> Press Enter to Continue <<<<<<<<<<" );
        BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
        try {
            reader.readLine();
        }
        catch( Exception e ) {
            log( "Error while waiting for user input: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    /**
     * As all time-related code is Portico-specific, we have isolated it into a
     * single method. This way, if you need to move to a different RTI, you only need
     * to change this code, rather than more code throughout the whole class.
     */
    protected LogicalTime convertTime(double time ) {
        // PORTICO SPECIFIC!!
        return new DoubleTime( time );
    }

    /**
     * Same as for {@link #convertTime(double)}
     */
    protected LogicalTimeInterval convertInterval(double time ) throws RestoreInProgress, TimeConstrainedAlreadyEnabled, ConcurrentAccessAttempted, EnableTimeConstrainedPending, SaveInProgress, FederateNotExecutionMember, RTIinternalError, TimeAdvanceAlreadyInProgress {
        // PORTICO SPECIFIC!!
        return new DoubleTimeInterval(time);
    }

    protected void enableTimePolicy() throws RTIexception {
        // NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
        //       Portico specific. You will have to alter this if you move to a
        //       different RTI implementation. As such, we've isolated it into a
        //       method so that any change only needs to happen in a couple of spots
        LogicalTime currentTime = convertTime( fedamb.federateTime );
        LogicalTimeInterval lookahead = convertInterval( fedamb.federateLookahead );

        ////////////////////////////
        // enable time regulation //
        ////////////////////////////
		/*this.rtiamb.enableTimeRegulation( currentTime, lookahead );

		// tick until we get the callback
		while( fedamb.isRegulating == false )
		{
			rtiamb.tick();
		}
		*/
        /////////////////////////////
        // enable time constrained //
        /////////////////////////////
        this.rtiamb.enableTimeConstrained();

        // tick until we get the callback
        while( fedamb.isConstrained == false )
        {
            rtiamb.tick();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////// Main Simulation Method /////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This is the main simulation loop. It can be thought of as the main method of
     * the federate. For a description of the basic flow of this federate, see the
     * class level comments
     */
    public abstract void runFederate() throws RTIexception;

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Helper Methods //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    /**
     * This method will attempt to enable the various time related properties for
     * the federate
     */


    protected byte[] generateTag()
    {
        return (""+System.currentTimeMillis()).getBytes();
    }

    protected void advanceTime( double timestep ) throws RTIexception
    {
        // request the advance
        fedamb.isAdvancing = true;
        LogicalTime newTime = convertTime( fedamb.federateTime + timestep );
        rtiamb.timeAdvanceRequest( newTime );

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while( fedamb.isAdvancing )
        {
            rtiamb.tick();
        }
    }

}
