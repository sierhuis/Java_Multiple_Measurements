

import gov.nasa.arc.brahms.common.data.Agent;
import gov.nasa.arc.brahms.vm.api.jagt.AbstractExternalAgent;
import gov.nasa.arc.brahms.vm.api.common.*;
import gov.nasa.arc.brahms.vm.api.exceptions.*;
import gov.nasa.arc.brahms.vm.api.JAPI;
import java.lang.String;

/**
 * Java_Agent_Service
 * 
 * This is an External Brahms Agent that gets loaded into the BVM by the agent:
 * 	file: Java_Agent_Service.b
 * 		external agent Java_Agent_Service;
 * 
 * To include this agent in the BVM execution of the Test Model do the following
 * 	1. Compile the Brahms_Multiple_Measurements model that includes the Java_Agent_Service.b file
 * 	2. Compile this Java file
 * 	3. Create a .jar file that includes the .class file of this compiled file
 * 	4. Move the .jar file to the ../Agents directory in the $BRAHMS_HOME directory
 * 	5. Run the Brahms_Multiple_Measurements model with the following parameters
 * 		BVM params: -ui -mode rt -no_auto_stop
 * 		(Note: you can leave out the "-ui" param, but you will have to ctrl-C the BVM to stop
 * 
 * 	NOTE: There is a (wrong?) behavior if you don't include the -no_auto_stop parameter.
 * 		  My hypothesis is that is you include this parameter, the BVM doesn't stop, and 
 *        continues to process all events that are on the central event queue. This means that 
 *        the BVM scheduler process the second beliefs from the second assert and it puts them on the 
 *        Patient agent's event queue, and they get processed.
 *        However, if the -no_auto_stop is not added, the BVM stops immediately when ALL agent events are processed
 *        and there are no more events to process on any agent's event queue, thus BEFORE the central BVM 
 *        scheduler can distribute the beliefs to the Patient agent.
 *        A good thing is that the Ejenta Brahms model runs with the -no_auto_stop parameter, so it never stops.
 */

public class Java_Agent_Service extends AbstractExternalAgent {

    public void doWork() {

		System.out.println("Java_Agent_Service: doWork() called\n");
		
		try {
			// this is the current Brahms model and is used to get the patient's agent reference
			IModel model = JAPI.getModel(); 

			// Patient is the agent in the Brahms_Multiple_Measurements test model
			// In Ejenta's Agent Service, this is the patient's PA in Brahms
			// First, you need the name of the Patient as a Java String
			final String patientName = "Patient";
			
			// Now you can get a reference to the Patient agent
			final IAgent patient = model.getAgent(patientName);
			
			// The attribute in the Patient agent that is used for evaluating multiple measurements
			// In the Ejenta Brahms model this is the attribute "latestBloodPressureFeature"
			// This attribute should be changed to a Map type attribute, instead of a Java type
			final String m_Measurements = "m_Measurements";
			
			// Get a reference to the attribute in the Patient agent
			final IAttribute mapMeasurements = patient.getAttribute(m_Measurements);
	
			// These are the test objects in the test Brahms_Multiple_Measurements Brahms model
			// In the Ejenta Agent Service Java code (KaiserUser.java) this is the "latestNewBloodPressure" attribute containing the latest measurement
			// There are 4 measurement objects in the test model to simulate that 4 measurements (of any type) come in at the same time
			String measAttrName = "Measurement_1";
			final IObject measur1 = model.getObject(measAttrName);
			measAttrName = "Measurement_2";
			final IObject measur2 = model.getObject(measAttrName);
			measAttrName = "Measurement_3";
			final IObject measur3 = model.getObject(measAttrName);
			measAttrName = "Measurement_4";
			final IObject measur4 = model.getObject(measAttrName);

			// Now create the 4 beliefs for the 4 measurements
			// Each belief is an index (0, 1, 2, 3) in the Map attribute for the Patient agent)
			IBelief belief1, belief2, belief3, belief4;
			belief1 = BeliefFactory.createBelief(patient, mapMeasurements, 0, IRelationalOperator.EQUALS, measur1);
			belief2 = BeliefFactory.createBelief(patient, mapMeasurements, 1, IRelationalOperator.EQUALS, measur2);
			belief3 = BeliefFactory.createBelief(patient, mapMeasurements, 2, IRelationalOperator.EQUALS, measur3);
			belief4 = BeliefFactory.createBelief(patient, mapMeasurements, 3, IRelationalOperator.EQUALS, measur4);

			// Add all the beliefs to an array of IBelief so that we can assert all 4 at the same time
			IBelief[] measurements = {belief1, belief2, belief3, belief4};

			// Now assert the beliefs to the Patient agent
			patient.assertBeliefs(measurements, (IContext) this.getContext());

			// As an alternative you can use the send communication activity to communicate, instead of assert, the beliefs to the agent
			//this.send(measurements, patient);

			// tell this agent to sleep or not sleep
			boolean please_sleep = true;

			// Make the agent sleep so that the Patient agent can evaluate the measurements
			try {
				// Just sleep for a little bit if told to
				if (please_sleep == true) {
					System.out.print("\nSleep #1\n\n");
					// NOTE: After some testing, it seems that if you do a sleep at 125 msecs, the model runs ok in SIM and RT mode,
					// 		 but if you lower the sleep time to LESS THAN 125msecs, you get a race condition, because this agent
					// 		 overwrites the beliefs while the Patient agent is executing the WFI (workframe instance) and
					// 		 retracts the belief BEFORE it can be processed again. 
					// Another Note: this is on my slow (old) Mac. On newer, faster Macs it might be different. It basically depends
					//		 	   	 on how fast the CPU is and how fast the CPU let's the Patient agent execute its WFI's
					Thread.sleep(125L);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// tell this agent to change the Map attribute indexes to see what happens
			boolean change_index = false;

			// if to change the indexes now don't reuse the map indexes
			if (change_index == true) {
				System.out.print("\nChanges Indexes\n\n");
				belief1 = BeliefFactory.createBelief(patient, mapMeasurements, 4, IRelationalOperator.EQUALS, measur4);
				belief2 = BeliefFactory.createBelief(patient, mapMeasurements, 5, IRelationalOperator.EQUALS, measur3);
				belief3 = BeliefFactory.createBelief(patient, mapMeasurements, 6, IRelationalOperator.EQUALS, measur2);
				belief4 = BeliefFactory.createBelief(patient, mapMeasurements, 7, IRelationalOperator.EQUALS, measur1);				
			}

			// Add all the beliefs to an array of IBelief so that we can assert all 4 at the same time
			// But, change the order around to show that the ordering of evaluation in the Patient model is different
			IBelief[] measurements2 = {belief4, belief3, belief2, belief1};

			// Now communicate the same beliefs with the same measurements again
			// This is to test that you can re-use the Map attribute again
			// This should work because the test model retracts the beliefs after it is finished evaluating
			// If a new set of measurement comes in BEFORE all the other ones are evaluated, then this won't work
			// This will happen if you take the Thread.sleep(1000L) out
			// Then, you need to increase the map index so that the previous ones are not overwritten
			patient.assertBeliefs(measurements2, (IContext) this.getContext());

			// As an alternative you can use the send communication activity to communicate, instead of assert, the beliefs to the agent
			//this.send(measurements, patient);
		
		} catch (ExternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    } //doWork

	public void initialize()  {
		System.out.println("Java_Agent_Service: initialize() called\n");

	}

	public void onAssert(IFact arg0)  {
		System.out.println("Java_Agent_Service: onAssert() called\n");
		
	}

	public void onReceive(IBelief arg0) {
		System.out.println("Java_Agent_Service: onReceive() called\n");
		
	}

	public void onRetract(IFact arg0) {
		System.out.println("Java_Agent_Service: onRetract() called\n");
		
	}

	public void pause() {
		System.out.println("Java_Agent_Service: pause() called\n");
		
	}

	public void reset() {
		System.out.println("Java_Agent_Service: reset() called\n");
		
	}

	public void resume() {
		System.out.println("Java_Agent_Service: resume() called\n");
		
	}

	public void start()  {
		System.out.println("Java_Agent_Service: start() called\n");
		
	}

	public void stop() throws ExternalException {
		System.out.println("Java_Agent_Service: stop() called\n");
		
	}

} //Java_Agent_Service
