package Simulation;

import hla.rti.*;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public abstract class Federate implements Runnable{
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
    protected void log( String message ) { System.out.println( "GUIFederate   : " + message ); }

    /**
     * This method will block until the user presses enter
     */
    protected void waitForUser() {
        log( " >>>>>>>>>> Press Enter to Continue <<<<<<<<<<" );
        BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
        try {
            reader.readLine();
        } catch( Exception e ) {
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
        while(!fedamb.isConstrained) rtiamb.tick();
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////// Main Simulation Method /////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This is the main simulation loop. It can be thought of as the main method of
     * the federate. For a description of the basic flow of this federate, see the
     * class level comments
     */
    public void runFederate() throws RTIexception {
        /////////////////////////////////
        // 1. create the RTIambassador //
        /////////////////////////////////
        //rtiamb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();

        //////////////////////////////
        // 2. create the federation //
        //////////////////////////////
        // create
        // NOTE: some other federate may have already created the federation,
        //       in that case, we'll just try and join it
        try {
            File fom = new File( "tramfom.fed" );
            rtiamb.createFederationExecution( federationName,
                    fom.toURI().toURL() );
            log( "Created Federation" );
        }
        catch( FederationExecutionAlreadyExists exists ) {
            log( "Didn't create federation, it already existed" );
        } catch( MalformedURLException urle ) {
            log( "Exception processing fom: " + urle.getMessage() );
            urle.printStackTrace();
            return;
        }

        ////////////////////////////
        // 3. join the federation //
        ////////////////////////////
        // create the federate ambassador and join the federation
        setAmbassador();
        rtiamb.joinFederationExecution( name, federationName, fedamb );
        log( "Joined Federation as " + name );

        ////////////////////////////////
        // 4. announce the sync point //
        ////////////////////////////////
        // announce a sync point to get everyone on the same page. if the point
        // has already been registered, we'll get a callback saying it failed,
        // but we don't care about that, as long as someone registered it
        rtiamb.registerFederationSynchronizationPoint( READY_TO_RUN, null );
        // wait until the point is announced
        while(!fedamb.isAnnounced) rtiamb.tick();

        // WAIT FOR USER TO KICK US OFF
        // So that there is time to add other federates, we will wait until the
        // user hits enter before proceeding. That was, you have time to start
        // other federates.
        waitForUser();

        ///////////////////////////////////////////////////////
        // 5. achieve the point and wait for synchronization //
        ///////////////////////////////////////////////////////
        // tell the RTI we are ready to move past the sync point and then wait
        // until the federation has synchronized on
        rtiamb.synchronizationPointAchieved( READY_TO_RUN );
        log( "Achieved sync point: " +READY_TO_RUN+ ", waiting for federation..." );
        while(!fedamb.isReadyToRun) rtiamb.tick();

        /////////////////////////////
        // 6. enable time policies //
        /////////////////////////////
        // in this section we enable/disable all time policies
        // note that this step is optional!
        enableTimePolicy();
        log( "Time Policy Enabled" );

        //////////////////////////////
        // 7. publish and subscribe //
        //////////////////////////////
        // in this section we tell the RTI of all the data we are going to
        // produce, and all the data we want to know about
        publishAndSubscribe();
        log( "Published and Subscribed" );

        runFederateLogic();

        ////////////////////////////////////
        // 11. resign from the federation //
        ////////////////////////////////////
        rtiamb.resignFederationExecution( ResignAction.NO_ACTION );
        log( "Resigned from Federation" );


        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ////////////////////////////////////////
        // 12. try and destroy the federation //
        ////////////////////////////////////////
        // NOTE: we won't die if we can't do this because other federates
        //       remain. in that case we'll leave it for them to clean up
        try {
            rtiamb.destroyFederationExecution( federationName );
            log( "Destroyed Federation" );
        }
        catch( FederationExecutionDoesNotExist dne ) {
            log( "No need to destroy federation, it doesn't exist" );
        } catch( FederatesCurrentlyJoined fcj ) {
            log( "Didn't destroy federation, federates still joined" );
        }
    }

    protected abstract void setAmbassador();
    protected abstract void runFederateLogic();
    protected abstract void publishAndSubscribe() throws RTIexception;

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

    protected void advanceTime( double timestep ) throws RTIexception {
        // request the advance
        fedamb.isAdvancing = true;
        LogicalTime newTime = convertTime( fedamb.federateTime + timestep );
        rtiamb.timeAdvanceRequest( newTime );

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while( fedamb.isAdvancing ) rtiamb.tick();
    }

    @Override
    public void run()
    {
        try {
            runFederate();
        } catch (RTIexception e) {
            e.printStackTrace();
        }
    }

}
