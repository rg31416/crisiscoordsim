/*
 * @(#) Medic.java December 1, 2009
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import jade.core.Agent;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import nl.tudelft.simulation.crisiscoord.CrisisCoordModel;

/**
* Medic response agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 1.6 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Medic extends ResponseAgent
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Setup of the agent
	 */
	protected void setup()
	{
		/** Initialize attributes */
		this.setAgentArrived(false);
		this.setAlarmed(false);
		pointOfOrigin = CrisisCoordModel.HOSPITAL_POS;
		this.setDestination(directedPointToLocation(pointOfOrigin));
		
		/** Register language and ontology */
		setCodec();
		manager.registerLanguage(getCodec());
		setOntology();
		manager.registerOntology(getOntology());
		
		/** Attach model reference */
		Object [] args = getArguments();
		setDSOLModel((CrisisCoordModel) args[0]);
		
		/** Define and register the service provided by this agent*/
		ServiceDescription serviceDescription  = new ServiceDescription();
		serviceDescription.setType("MedicService");
		serviceDescription.setName(getLocalName());
        registerInDF(serviceDescription);
        
        //setResponderNumber("Medic");
        /** Determine service description of officer for this agent */
        setMyOfficerAID("MedicalOfficerService");
        
        /** Start behaviour factory */
        //addBehaviour(new MessageResponderBehaviour(this));
        threadedBehaviourFactory = new ThreadedBehaviourFactory();
        addBehaviour(threadedBehaviourFactory.wrap(new MessageResponderBehaviour(this)));
        addBehaviour(threadedBehaviourFactory.wrap(new MedicBehaviour(this)));
        addBehaviour(threadedBehaviourFactory.wrap(new CheckArrivalStatus(this)));
        
	} // End of setup method

	/**
	 * Take down agent method
	 *///
	protected void takeDown() 
	{
		threadedBehaviourFactory.interrupt();
		doDelete();
	}
	
	/**
	 * FSMBehaviour internal class: MedicBehaviour
	 */
	class MedicBehaviour extends FSMBehaviour 
	{
		
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;		
		
		/** Constant for transition to exit MedicBehaviour */
		protected final int exit = 1;
		/** Constant for transition to stay within MedicBehaviour */
		protected final int notExit = 0;
		/** Constant for transition to stay within AssistVictimBehaviour */
		protected final int victimsOrFire = 1;
		/** Constant for transition to end AssistVictimBehaviour */
		protected final int noVictimsOrFire = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public MedicBehaviour(final Agent a)
		{
			super(a);
	        /** register state behaviours */
			registerFirstState(new StartBehaviour(myAgent), "Start");
			registerState(new GetToLocationBehaviour(myAgent), "GetToLocation");
			registerState(new NotifyArrivalBehaviour(myAgent, exit, notExit), "NotifyArrival");
			registerState(new AssessSituationBehaviour(myAgent), "AssessSituation");
			registerState(new InformAssessmentBehaviour(myAgent, victimsOrFire, noVictimsOrFire), "InformAssessment");
			registerState(new RespondBehaviour(myAgent), "Respond");
			registerState(new UpdateAssessmentBehaviour(myAgent), "UpdateAssessment");
			registerState(new InformResultBehaviour(myAgent, victimsOrFire, noVictimsOrFire), "InformResult");
			registerState(new ExitBehaviour(myAgent), "Exit");
			registerLastState(new NotifyExitBehaviour(myAgent), "NotifyExit");
			
			/** register transitions */
			registerDefaultTransition ("Start", "GetToLocation");
			registerDefaultTransition ("GetToLocation", "NotifyArrival");
			registerTransition ("NotifyArrival", "AssessSituation", notExit);
			registerTransition ("NotifyArrival", "Exit", exit);
			registerDefaultTransition ("AssessSituation", "InformAssessment");
			registerTransition ("InformAssessment", "Respond", victimsOrFire);
			registerTransition ("InformAssessment", "GetToLocation", noVictimsOrFire);
			registerDefaultTransition ("Respond", "UpdateAssessment");
			registerDefaultTransition ("UpdateAssessment", "InformResult");
			/** new String [] {"Respond"} needs to be added because Respond sub-behavior is terminated and a new one needs to be restarted */
			registerTransition ("InformResult", "Respond", victimsOrFire, new String [] {"Respond"});
			registerTransition ("InformResult", "GetToLocation", noVictimsOrFire); 
			registerDefaultTransition ("Exit", "NotifyExit");
		}
		
		/**
		 * onEnd of MedicBehaviour
		 * @return 0
		 */
		public int onEnd()
		{
			return 0;
		}
		    
	} //End internal class: MedicBehaviour
	
	/**
	 * FSMChildBehaviour internal class: StartBehaviour
	 */
	class StartBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public StartBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			/** Initialize some values */
			getStrategy().setExit("go");
			
			/** Set responder number to access responder proxy in DSOL model responder proxy array */
	        setResponderNumber("Medic");
	        
			/** Wait for message from self indicating alarm received */
	        while (!getAlarmed())
	        {
	        	ACLMessage alarmmsg = myAgent.blockingReceive();
	        	///** If message is from self and indicates Alarmed, then simply continue */
	        	/** If it is a different message resend (just to self, in case it is a broadcast)
	        	 * that way it doesn´t get erroneously ignored 
	        	 */
	        	if (!alarmmsg.getContent().contains("Alarmed"))
	        	{
	        		alarmmsg.clearAllReceiver();
	        		alarmmsg.addReceiver(myAgent.getAID());
	        		send(alarmmsg);
	        	}
	        }
	        this.getParent().getDataStore().clear();
			finished = true;
		}
	
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: StartBehaviour
	
	/**
	 * FSMBehaviour internal class: RespondBehaviour
	 */
	class RespondBehaviour extends FSMBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Constant for transition to ContainFirehaviour */
		protected final int fireAssignment = 1;
		/** Constant for transition to AssistVictim */
		protected final int victimAssignment = 0;

		/**
		 * Constructor
		 * @param a the agent
		 */
		public RespondBehaviour(final Agent a)
		{
			super(a);
	        /** register state behaviours */
			registerFirstState(new AssignmentBehaviour(myAgent, fireAssignment, victimAssignment), "Assignment");
			registerLastState(new AssistVictimBehaviour(myAgent), "AssistVictim");
			
			/** register transitions */
			
			/** new String [] {"AssistVictim"} needs to be added because on second time sub-behavior is terminated and a new one needs to be restarted */
			registerTransition ("Assignment", "AssistVictim", victimAssignment, new String [] {"AssistVictim"});
			
		}
		
		/**
		 * FSMChildBehaviour internal class: AssignmentBehaviour
		 */
		class AssignmentBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/** Constant for transition to ContainFirehaviour */
			protected int fireAssignment = 1;
			/** Constant for transition to MoveVictim */
			protected int victimAssignment = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 * @param fireAssignment fire assignment parameter
			 * @param victimAssignment victim assignment parameter
			 */
			public AssignmentBehaviour(final Agent a, final int fireAssignment, final int victimAssignment)
			{
				super(a);
				this.fireAssignment = fireAssignment;
				this.victimAssignment = victimAssignment;
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
			    /** All medics go to victim rescue */
				onEndReturnValue = victimAssignment;
				this.getParent().getDataStore().clear();
				finished = true;
			}
			
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
		} //End internal class: AssignmentBehaviour
		
	} //End internal class: RespondBehaviour
	
	/**
	 * Search for victim (re-implemented to replace that from ResponseAgent)
	 * @return furthest victim from fire as index
	 */
	public int searchVictim() 
	{
		// if mutual adjustment is false, then follow regular search, otherwise, far from fire
		if (!getMutualAdjustment())
		{
			return super.searchVictim();
		}
		
		double distance; // distance between fire and victim
		double longestDistance = 0; //longest current distance between fire and victim
		int furthestVictim = -1; // index for keeping furthestVictim, last of which is returned
		
		try
		{
			/** Search for victim furthest from fire */
			for (int i = 0; i < getDSOLModel().getCivilians().length; i++)
			{
				/** If civilian is victim and not selected */
				if (getDSOLModel().getCivilian(i).getState() > 0 && !getDSOLModel().getCivilian(i).getSelected())
				{
					distance = getDSOLModel().getFire().getCenter().distance(getDSOLModel().getCivilian(i).getCenter());
					/** if longer than current longestDistance and not so close to medics to prevent freeze */
					if (distance > longestDistance 
							&& getResponderProxy().getCenter().distance(getDSOLModel().getCivilian(i).getCenter()) > 2)
					{
						longestDistance = distance;
						furthestVictim = i;
					}
				}
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return furthestVictim;
	} // end of searchVictim method
	
}

