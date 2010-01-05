/*
 * @(#) Fireman.java November 26, 2009
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import java.util.Properties;

import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import nl.tudelft.simulation.crisiscoord.CrisisCoordModel;
import nl.tudelft.simulation.language.d3.DirectedPoint;

import nl.tudelft.simulation.crisiscoord.visualization.StaticObject;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

/**
* Fireman response agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @author <a> Rafael Gonzalez </a>
*/
public class Fireman extends ResponseAgent
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/** Experimental properties */
	/** Autonomous assignment or not of choice between fire fight and victim rescue */
	private boolean autoAssign;
	
	
	/**
	 * Setup of the agent
	 */
	protected void setup()
	{
		/** Initialize attributes */
		this.setAgentArrived(false);
		this.setAlarmed(false);
		pointOfOrigin = CrisisCoordModel.STATION_POS;
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
		serviceDescription.setType("FiremanService");
		serviceDescription.setName(getLocalName());
        
        /** Determine if fireman is backup and set service accordingly */
		if (args.length == 2) 
		{
			serviceDescription.setType("BackupFiremanService");
			serviceDescription.setName(getLocalName());
		}
		registerInDF(serviceDescription);
        
        /** Determine service description of officer */
        setMyOfficerAID("FireOfficerService");

        /** Initialize experimental properties */
		Properties properties;
		try
		{
			properties = getDSOLModel().getSimulator().getReplication().getTreatment().getProperties();
			this.autoAssign = new Boolean(properties.getProperty("model.coordination.autoassignment"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/** Start behaviour factory */
        //addBehaviour(new MessageResponderBehaviour(this));
        threadedBehaviourFactory = new ThreadedBehaviourFactory();
        addBehaviour(threadedBehaviourFactory.wrap(new MessageResponderBehaviour(this)));
        addBehaviour(threadedBehaviourFactory.wrap(new FiremanBehaviour(this)));
        addBehaviour(threadedBehaviourFactory.wrap(new CheckArrivalStatus(this)));
        
	} // End of setup method

	/**
	 * Take down agent method
	 */
	protected void takeDown() 
	{
		threadedBehaviourFactory.interrupt();
		doDelete();
	} // End of takeDown method
	
	
	
	/**
	 * FSMBehaviour internal class: FiremanBehaviour
	 */
	class FiremanBehaviour extends FSMBehaviour 
	{
		
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;		
		
		/** Constant for transition to exit FiremanBehaviour */
		protected final int exit = 1;
		/** Constant for transition to stay within FiremanBehaviour */
		protected final int notExit = 0;
		/** Constant for transition to stay within ResponseBehaviour */
		protected final int victimsOrFire = 1;
		/** Constant for transition to end ResponseBehaviour */
		protected final int noVictimsOrFire = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public FiremanBehaviour(final Agent a)
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
			
		} // end of FiremanBEhaviour constructor
		
		/**
		 * onEnd of FiremanBehavior
		 * @return 0
		 */
		public int onEnd()
		{
			return 0;
		}
		    
	} //End internal class: FiremanBehaviour
	
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
	        setResponderNumber("Fireman");
			
			/** By default every fireman starts assigned to fire containment, not victim rescue */
	        setAssignment("fire");
			
			/** Wait for message from self indicating alarm received */
	        while (!getAlarmed())
	        {
	        	ACLMessage alarmmsg = myAgent.blockingReceive();
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
			registerLastState(new ContainFireBehaviour(myAgent), "ContainFire");
			registerLastState(new AssistVictimBehaviour(myAgent), "AssistVictim");
			
			/** register transitions */
			
			/** new String [] {"ContainFire"} needs to be added because on second time sub-behavior is terminated and a new one needs to be restarted */
			registerTransition ("Assignment", "ContainFire", fireAssignment, new String [] {"ContainFire"});
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
				/** IF assignment is autonomous, make choice individually */
				if (autoAssign)
				{
					boolean cointoss = Math.random() > 0.5;
					/** if fireman sees victims then 50% chance to go to rescue */
					if (awareness.getPopulation().getVictims() > 0 && cointoss)
			        {
						setAssignment("victims");
			        	onEndReturnValue = victimAssignment;
			        }
					/** otherwise choose fire containment */
			        else
			        {
			        	setAssignment("fire");
			        	onEndReturnValue = fireAssignment;
			        }
				}
				/** If assignment is not autonomous, use value assigned by officer */
				else
				{
					if (getAssignment().equals("fire")) 
				    {
				        onEndReturnValue = fireAssignment;
				    } 
				    else if (getAssignment().equals("victims")) 
				    {
				        onEndReturnValue = victimAssignment;
				    } 
					/** by default, assume fire containment */
				    else
				    {
				    	onEndReturnValue = fireAssignment;
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
		} //End internal class: AssignmentBehaviour
		
		/**
		 * FSMBehaviour internal class: ContainFireBehaviour
		 */
		class ContainFireBehaviour extends FSMBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public ContainFireBehaviour(final Agent a)
			{
				super(a);
				
		        /** register state behaviours */
				registerFirstState(new GetToFireBehaviour(myAgent), "GetToFire");
				registerState(new NotifyAtFireBehaviour(myAgent), "NotifyAtFire");
				registerLastState(new FightFireBehaviour(myAgent), "FightFire");
				
				registerDefaultTransition ("GetToFire", "NotifyAtFire");
				registerDefaultTransition ("NotifyAtFire", "FightFire");
			}
			
			/**
			 * FSMChildBehaviour internal class: GetToFireBehaviour
			 */
			class GetToFireBehaviour extends FSMChildBehaviour
			{
				/** Declares a serialVersionUID with no purpose */
				static final long serialVersionUID = 0;
				
				/**
				 * Constructor
				 * @param a the agent
				 */
				public GetToFireBehaviour(final Agent a)
				{
					super(a);
				}
				
				/**
				 * Action (must be implemented for FSMChildBehaviour
				 */
				public void action()
				{
					/** Transform the fire edge location into a DirectedPoint */
					DirectedPoint dpDestination = new DirectedPoint();
					dpDestination = locationToDirectedPoint(getAwareness().getFire().getLocation());
					/** Convert fire scope string to x and y integers */
					int by = getAwareness().getFire().getScope().lastIndexOf('X');
					String x = new String();
					int xFireSize = 0;
					x = getAwareness().getFire().getScope().substring(0, by);
			        xFireSize += Integer.parseInt(x.trim());
					/** Offset destination to edge of fire plus safety distance */
			        dpDestination.x -= (xFireSize + CrisisCoordModel.SAFETY_DISTANCE);
			        dpDestination.y += CrisisCoordModel.SAFETY_DISTANCE;

			        /** set new origin and destination and move to adjust to fire size */
			        try
					{
			        	/** First, set arrived to false */
						setAgentArrived(false);
						getResponderProxy().setArrived(false);
						/** Change origin and destination */
						getResponderProxy().setOrigin(getResponderProxy().getCenter());
						getResponderProxy().setDestination(dpDestination);
				        /** start moving */
						getResponderProxy().next();
					} catch (Exception e)
					{
						e.printStackTrace();
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
				
			} //End internal class: GetToFireBehaviour
			
			/**
			 * FSMChildBehaviour internal class: NotifyAtFireBehaviour
			 */
			class NotifyAtFireBehaviour extends FSMChildBehaviour
			{
				/** Declares a serialVersionUID with no purpose */
				static final long serialVersionUID = 0;
				
				/**
				 * Constructor
				 * @param a the agent
				 */
				public NotifyAtFireBehaviour(final Agent a)
				{
					super(a);
				}
				
				/**
				 * Action (must be implemented for FSMChildBehaviour
				 */
				public void action()
				{
					/** Wait for message from self indicating arrival */
			        while (!getAgentArrived())
			        {
			        	myAgent.blockingReceive();
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
				
			} //End internal class: NotifyAtFireBehaviour
			
			/**
			 * FSMChildBehaviour internal class: FightFireBehaviour
			 */
			class FightFireBehaviour extends FSMChildBehaviour
			{
				/** Declares a serialVersionUID with no purpose */
				static final long serialVersionUID = 0;
				
				/**
				 * Constructor
				 * @param a the agent
				 */
				public FightFireBehaviour(final Agent a)
				{
					super(a);
				}
				
				/**
				 * Action (must be implemented for FSMChildBehaviour
				 */
				public void action()
				{
					
					try
					{
						if (getDSOLModel().getFire().getSize().x > 0)
						{
							/** Draw extinguisher being poured into 
							 * fire starting at Fireman proxy icon edge */
							DirectedPoint dpWaterLocation = new DirectedPoint(0, 0, 0);
							dpWaterLocation.x = getResponderProxy().getCenter().x + (CrisisCoordModel.RESPONDER_SIZE.x / 3) + (CrisisCoordModel.WATER_SIZE.x / 2);
							dpWaterLocation.y = getResponderProxy().getCenter().y;
							StaticObject water = new StaticObject(CrisisCoordModel.WATER_ICON, dpWaterLocation, 
									(DEVSSimulatorInterface) getDSOLModel().getSimulator(), new DirectedPoint(CrisisCoordModel.WATER_SIZE));
							getDSOLModel().getFire().fight(CrisisCoordModel.FIRE_FIGHT);
							/** Wait a while */
							try
							{
								Thread.sleep(CrisisCoordModel.RESPONDER_SLEEP);
							} catch (Exception e) 
							{
								e.printStackTrace();
							}
							
							/** Remove water icon */
							water.setSize(0, 0, 0);
							water = null;
						}
						
					} catch (Exception e)
					{
						e.printStackTrace();
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
				
			} //End internal class: FightFireBehaviour
			
		} //End internal class: ContainFireBehaviour
		
	} //End internal class: RespondBehaviour
	
} // End of Fireman agent
