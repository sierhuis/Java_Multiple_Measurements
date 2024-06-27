

import gov.nasa.arc.brahms.vm.api.jagt.AbstractExternalAgent;
import gov.nasa.arc.brahms.vm.api.common.*;
import gov.nasa.arc.brahms.vm.api.exceptions.*;
import gov.nasa.arc.brahms.vm.api.JAPI;
import java.lang.String;

/**
 * Java_Agent_Service
 */

public class Java_Agent_Service extends AbstractExternalAgent {

    public void doWork() {

		System.out.println("Java_Agent_Service: doWork() called\n");
		
		IModel model = JAPI.getModel();

		final String patientName = "Patient";
		final IAgent patient = model.getAgent(patientName);
		final String m_Measurements = "m_Measurements";
		final IAttribute mapMeasurements = patient.getAttribute(m_Measurements);

		String measAttrName = "Measurement1";
		final IObject measur1 = model.getObject(measAttrName);
		measAttrName = "Measurement2";
		final IObject measur2 = model.getObject(measAttrName);
		measAttrName = "Measurement3";
		final IObject measur3 = model.getObject(measAttrName);
		measAttrName = "Measurement4";
		final IObject measur4 = model.getObject(measAttrName);
		IConcept[] measurements = {measur1, measur2, measur3, measur4};	

		IBelief belief1, belief2, belief3, belief4;
		belief1 = BeliefFactory.createBelief(patient, mapMeasurements, 0, measur1);
		belief2 = BeliefFactory.createBelief(patient, mapMeasurements, 1, measur2);
		belief3 = BeliefFactory.createBelief(patient, mapMeasurements, 2, measur3);
		belief4 = BeliefFactory.createBelief(patient, mapMeasurements, 3, measur4);

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
