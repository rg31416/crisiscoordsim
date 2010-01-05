/*
 * @(#) OvDG.java November 24, 2009, 2009
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.ProposeResponder;
import nl.tudelft.simulation.crisiscoord.CrisisCoordModel;
import nl.tudelft.simulation.crisiscoord.ontology.Awareness;
import nl.tudelft.simulation.crisiscoord.ontology.Copi;
import nl.tudelft.simulation.crisiscoord.ontology.Plan;
import jade.content.onto.basic.Action;
import jade.content.Concept;
import nl.tudelft.simulation.language.d3.DirectedPoint;

/**
* OvD-G (Officier van Dienst - Geneeskundige)- Medical Officer agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @author <a> Rafael Gonzalez </a>
*/
public class OvDG extends OfficerAgent 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/** Experimental properties */
	/** Number of medics */
	private int numMedics;
	/** Number of backup medics */
	private int numBackupMedics;

	/**
	 * Setup of the agent
	 */
	public void setup()
	{
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
		serviceDescription.setType("MedicalOfficerService");
		serviceDescription.setName(getLocalName());
        registerInDF(serviceDescription);
        
		/** Determine service description of officer and responders */
        //setMyOfficerAID("OperationalLeaderService");
        myRespondersService = "MedicService";
        myBackupRespondersService = "BackupMedicService";

        /** Initialize experimental properties */
		Properties properties;
		try
		{
			properties = getDSOLModel().getSimulator().getReplication().getTreatment().getProperties();
			this.numMedics = new Integer(properties.getProperty("model.size.nummedics"));
			this.numBackupMedics = new Integer(properties.getProperty("model.size.nummedics"));
			this.responseWaitingTimeout = new Integer(properties.getProperty("model.waiting.responseWaitingTimeout"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/** Initialize attributes */
		this.setAgentArrived(false);
		this.setAlarmed(false);
		pointOfOrigin = CrisisCoordModel.HOSPITAL_POS;
		this.setDestination(directedPointToLocation(pointOfOrigin));
		myRespondersAIDs = new AID[numMedics];
		myBackupRespondersAIDs = new AID[numBackupMedics];
		receivedAwarenessReports = new Awareness[numMedics + numBackupMedics];
		
		/** Start behaviour factory */
        //addBehaviour(new MessageResponderBehaviour(this));
		threadedBehaviourFactory = new ThreadedBehaviourFactory();
		addBehaviour(threadedBehaviourFactory.wrap(new AlarmResponderBehaviour(this)));
        addBehaviour(threadedBehaviourFactory.wrap(new OvDGBehaviour(this)));
        addBehaviour(threadedBehaviourFactory.wrap(new CheckArrivalStatus(this)));
        addBehaviour(threadedBehaviourFactory.wrap(new ProposalResponseBehaviour(this)));
        
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
	 * FSMBehaviour internal class: OvDBehaviour
	 */
	class OvDGBehaviour extends FSMBehaviour 
	{
		
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;		
		
		/** Constant for transition to exit Behaviour */
		protected final int exit = 1;
		/** Constant for transition to stay within Behaviour */
		protected final int notExit = 0;
		/** Constant for transition to stay within Behaviour */
		protected final int proposalAccepted = 1;
		/** Constant for transition to end Behaviour */
		protected final int proposalRejected = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public OvDGBehaviour(final Agent a)
		{
			super(a);
			/** register state behaviours */
			registerFirstState(new StartBehaviour(myAgent), "Start");
			registerState(new GetToLocationBehaviour(myAgent), "GetToLocation");
			registerState(new NotifyArrivalBehaviour(myAgent), "NotifyArrival");
			registerState(new AnalyzeSituationBehaviour(myAgent, exit, notExit), "AnalyzeSituation");
			registerState(new EstablishCoPIBehaviour(myAgent), "EstablishCoPI");
			registerState(new PlanContainmentBehaviour(myAgent, proposalAccepted,  proposalRejected), "PlanContainment");
			registerState(new CommunicatePlanBehaviour(myAgent), "CommunicatePlan");
			registerState(new DeployContainmentBehaviour(myAgent), "DeployContainment");
			registerState(new ExitBehaviour(myAgent), "Exit");
			registerLastState(new NotifyExitBehaviour(myAgent), "NotifyExit");
			
			/** register transitions */
			registerDefaultTransition ("Start", "GetToLocation");
			registerDefaultTransition ("GetToLocation", "NotifyArrival");
			/** new String [] {"AnalyzeSituation"} needs to be added because on second time sub-behavior is terminated and a new one needs to be restarted */
			registerDefaultTransition ("NotifyArrival", "AnalyzeSituation", new String [] {"AnalyzeSituation"});
			registerTransition ("AnalyzeSituation", "EstablishCoPI", notExit);
			registerTransition ("AnalyzeSituation", "Exit", exit);
			registerDefaultTransition ("EstablishCoPI", "PlanContainment");
			//registerTransition ("PlanContainment", "CommunicatePlan", proposalAccepted);
			//registerTransition ("PlanContainment", "PlanContainment", proposalRejected);
			registerDefaultTransition("PlanContainment", "CommunicatePlan");
			/** new String [] {"DeployContainment"} needs to be added because on second time sub-behavior is terminated and a new one needs to be restarted */
			registerDefaultTransition ("CommunicatePlan", "DeployContainment", new String [] {"DeployContainment"});
			registerDefaultTransition ("DeployContainment", "GetToLocation");
			registerDefaultTransition ("Exit", "NotifyExit");
		} // end of OvDGBehaviour constructor
		
		/**
		 * onEnd of OvDGBehaviour
		 * @return 0
		 */
		public int onEnd()
		{
			return 0;
		}
		    
	} //End internal class: OvDGBehaviour
	
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
			
			/** Determine number for accessing responder proxy */
	        setResponderNumber("OvDG");
			
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
	 * FSMBehaviour internal class: AnalyzeSituationBehaviour
	 */
	class AnalyzeSituationBehaviour extends FSMBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Constant for transition to exit OvDG behaviour */
		protected int exit = 1;
		/** Constant for transition to stay within OvDG behaviour */
		protected int notExit = 0;
		/** Constant for transition skipping InformPlanStatusBehaviour */
		protected int firstAwareness = 0;
		/** Constant for transition into InformPlanStatusBehaviour */
		protected int otherAwareness = 1;

		/**
		 * Constructor
		 * @param a the agent
		 * @param exit exit parameter
		 * @param notExit not exit parameter
		 */
		public AnalyzeSituationBehaviour(final Agent a, final int exit, final int notExit)
		{
			super(a);
			this.exit = exit;
			this.notExit = notExit;
			
	        /** register state behaviours */
			registerFirstState(new RequestAssessmentBehaviour(myAgent), "RequestAssessment");
			registerState(new MergeAssessmentsBehaviour(myAgent, firstAwareness, otherAwareness), "MergeAssessments");
			registerLastState(new MultidisciplinaryConsultationBehaviour(myAgent), "MultidisciplinaryConsultation");
			
			/** register transitions */
			registerDefaultTransition ("RequestAssessment", "MergeAssessments");
			//registerTransition ("MergeAssessments", "InformPlanStatus", otherAwareness);
			//registerTransition ("MergeAssessments", "MultidisciplinaryConsultation", firstAwareness);
			//registerDefaultTransition ("InformPlanStatus", "MultidisciplinaryConsultation");
			registerDefaultTransition ("MergeAssessments", "MultidisciplinaryConsultation");
		}
		
		/**
		 * FSMChildBehaviour internal class: MultidisciplinaryConsultationBehaviour
		 */
		class MultidisciplinaryConsultationBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public MultidisciplinaryConsultationBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				determineGRIPlevel();

				//addBehaviour(threadedBehaviourFactory.wrap(new ProposalResponseBehaviour(this.myAgent)));
				
				/** Check if GRIP = 0 to exit response */
				if (mergedAwareness.getGrip().getLevel().equals("0"))
				{
					onEndReturnValue = exit;
				}
				else
				{
					onEndReturnValue = notExit;
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
		} //End internal class: MultidisciplinaryConsultationBehaviour

	} // End internal class: AnalyzeSituationBehaviour
	
	/**
	 * FSMChildBehaviour internal class: EstablishCoPIBehaviour
	 */
	class EstablishCoPIBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public EstablishCoPIBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			
			//addBehaviour(threadedBehaviourFactory.wrap(new ProposalResponseBehaviour(this.myAgent)));
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
	} //End internal class: EstablishCoPIBehaviour
	
	/**
	 * FSMBehaviour internal class: PlanContainmentBehaviour
	 */
	class PlanContainmentBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Constant for transition to stay within ResponseBehaviour */
		protected int proposalAccepted = 1;
		/** Constant for transition to end ResponseBehaviour */
		protected int proposalRejected = 0;

		/**
		 * Constructor
		 * @param a the agent
		 * @param proposalAccepted continue parameter
		 * @param proposalRejected restart parameter
		 */
		public PlanContainmentBehaviour(final Agent a, final int proposalAccepted, final int proposalRejected)
		{
			super(a);
			this.proposalAccepted = proposalAccepted;
			this.proposalRejected = proposalRejected;
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			/** Establish collection point location and size */
			establishCollectionPoint();
			
			/** Plan containment strategy */
			planContainment();
			//addBehaviour(threadedBehaviourFactory.wrap(new ProposalResponseBehaviour(this.myAgent)));
			onEndReturnValue = proposalAccepted;
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
	} //End internal class: PlanContainmentBehaviour
	
	/**
	 * FSMBehaviour internal class: ProposalResponseBehaviour
	 */
	class ProposalResponseBehaviour extends OneShotBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public ProposalResponseBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for OneShotBehaviour
		 */
		public void action()
		{
			/** Prepare the message template for received porposals */
			MessageTemplate template = ProposeResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_PROPOSE);     
			
				/** Add responder behaviour to reply to proposal */
				myAgent.addBehaviour(new ProposeResponder(myAgent, template) 
				{
					/** Declares a serialVersionUID with no purpose */
					static final long serialVersionUID = 0;
					
					/** 
			         * overrides prepareResponse
			         * @param proposal the message
			         * @return the response
			         */
					protected ACLMessage prepareResponse(final ACLMessage proposal) 
					{
						ACLMessage informReply = proposal.createReply();
						ContentElement content = null;
						Concept actionContent = null;
		                
						/** Convert from String to Java objects (decoding the message) */
						try
						{
							content = getContentManager().extractContent(proposal);
						} catch (Exception e)
						{
							/** if an exception occurs return not understood reply */
							e.printStackTrace();
							informReply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
							informReply.setContent("Decoding failed");
							CrisisCoordModel.countMessage();
							return informReply;
						}
						
						/** If message received is Action */
						if (content instanceof Action)
						{
							/** Extract content of action */
							actionContent = ((Action) content).getAction();
							
							/** Handle CoPI proposal */
							if (actionContent instanceof Copi)
							{
								handleCopiProposal(informReply, actionContent);
								CrisisCoordModel.countMessage();
							}
							
							/** or handle containment plan proposal */
							else if (actionContent instanceof Plan)
							{
								handlePlanProposal(informReply, actionContent);
								CrisisCoordModel.countMessage();
							}
						}
						
						/** If message is not action, but awareness: Handle awareness proposal */
						else if (content != null && content.toString().contains("Awareness"))
						{
							handleAwarenessProposal(informReply, content);
							CrisisCoordModel.countMessage();
						}
						
						/** for all other messages, reply with not understood */
						else 
						{
							informReply.setPerformative(ACLMessage.NOT_UNDERSTOOD);	
							CrisisCoordModel.countMessage();
						}
						
						return informReply;
					}
				});
				this.getParent().getDataStore().clear();
		}
	} //End internal class: ProposalResponseBehaviour
	
	/**
	 * FSMBehaviour internal class: DeployContainmentBehaviour
	 */
	class DeployContainmentBehaviour extends FSMBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public DeployContainmentBehaviour(final Agent a)
		{
			super(a);
	        /** register state behaviours */
			registerFirstState(new AlarmAndGetToLocationBehaviour(myAgent), "AlarmAndGetToLocation");
			registerLastState(new DeployUnitsBehaviour(myAgent), "DeployUnits");
			
			/** register transitions */
			registerDefaultTransition ("AlarmAndGetToLocation", "DeployUnits");

		}
		
		/**
		 * FSMBehaviour internal class: AlarmAndGetToLocationBehaviour
		 */
		class AlarmAndGetToLocationBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;

			/**
			 * Constructor
			 * @param a the agent
			 */
			public AlarmAndGetToLocationBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{

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
		} //End internal class: AlarmAndGetToLocationBehaviour
		
		/**
		 * FSMBehaviour internal class: DeployUnitsBehaviour
		 */
		class DeployUnitsBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public DeployUnitsBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				if (!backupCalled)
				{
					//getDSOLModel().launchAdditionalMedics();
					
					/** Get system date for message replyby timeout */
					Date currentDate = new Date();
					currentDate.setTime(currentDate.getTime() + responseWaitingTimeout);
					
					/** Prepare the alarm message */
					ACLMessage requestmsg = new ACLMessage(ACLMessage.REQUEST);
					requestmsg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					requestmsg.setLanguage(getCodec().getName());
					requestmsg.setOntology(getOntology().getName());
					requestmsg.setReplyByDate(currentDate);

					/** Set sender ID and wrap content objects inside an Action */
					Action action = new Action(getAID(), (AgentAction) getAlarm());

					/** Convert Java objects into strings for the message */
					try
					{
						getContentManager().fillContent(requestmsg, action);
					} catch (Exception e)
					{
						e.printStackTrace();
					} 
					
					/** Determine receiver agents */
					myBackupRespondersAIDs = getMyBackupRespondersAIDs();
					for (int i = 0; i < myBackupRespondersAIDs.length; i++)
					{
						requestmsg.addReceiver(myBackupRespondersAIDs[i]);
					}
					
					if (myBackupRespondersAIDs.length > 0)
					{
						/** Send message using AchieveREInitiator behaviour */
						myAgent.addBehaviour(new AchieveREInitiator(myAgent, requestmsg) {
							
							/** Declares a serialVersionUID with no purpose */
							static final long serialVersionUID = 0;
							
							/** 
					         * overrides handleAllResponses
					         * @param responses the responses received
					         */
							@SuppressWarnings("unchecked")
							protected void handleAllResponses(final Vector responses)
							{
								System.out.println(myAgent.getLocalName() + " received " + responses.size() + " alarm responses");
							}
						});	
					}
					
					backupCalled = true;
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
		} //End internal class: DeployUnitsBehaviour
		
	} //End internal class: DeployContainmentBehaviour
	
	/**
	 * Handle Awareness Proposal Response
	 * @param informReply the proposal reply message
	 * @param content awareness proposal content
	 */
	public void handleAwarenessProposal(final ACLMessage informReply, final ContentElement content) 
	{
		/** Extract GRIP level from proposed shared awareness */
		String proposedGRIP = new String("");
		Awareness proposedAwareness = (Awareness) content;
		proposedGRIP = proposedAwareness.getGrip().getLevel();
		
		/** Determine acceptance if own GRIP is the same as proposed GRIP */
		if (mergedAwareness.getGrip() != null)
		{
			if (mergedAwareness.getGrip().getLevel().equals(proposedGRIP))
			{
				informReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				/** Convert Java objects into strings for the reply message */
				try
				{
					getContentManager().fillContent(informReply, mergedAwareness);
				} catch (Exception e)
				{
					e.printStackTrace();
				} 
			}
		}
		else
		{
			informReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
			/** Convert Java objects into strings for the reply message */
			try
			{
				getContentManager().fillContent(informReply, mergedAwareness);
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
		}
	} // End of handleAwarenessProposal method
	
	/**
	 * Handle Copi Proposal Response
	 * @param informReply the proposal reply message
	 * @param content Copi proposal content
	 */
	public void handleCopiProposal(final ACLMessage informReply, final Concept content) 
	{
		String gripLevel = new String();
		try
		{
			gripLevel = mergedAwareness.getGrip().getLevel();
		} catch (Exception e)
		{
			e.getMessage();
		} 
		/** Determine acceptance if GRIP = 1 */
		if (gripLevel.equals("1"))
		{
			informReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			/** Convert Java objects into strings for the reply message */
			try
			{
				getContentManager().fillContent(informReply, new Copi());
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
		}
		else
		{
			informReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
			/** Convert Java objects into strings for the reply message */
			try
			{
				getContentManager().fillContent(informReply, new Copi());
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
		}		
	} // End of handleCopiProposal method
	
	/**
	 * Handle Plan Proposal Response
	 * @param informReply the proposal reply message
	 * @param content Plan proposal content
	 */
	public void handlePlanProposal(final ACLMessage informReply, final Concept content) 
	{
		/**Extract plan from proposal action content */
		Plan proposedPlan = (Plan) content;
		/** Determine acceptance if exit criteria is agreed */
		if (proposedPlan.getStrategy().getExit().equals("exit"))
		{
			if (mergedAwareness.getPopulation().getVictims() == 0 
				&& mergedAwareness.getFire().getScope().equals("0 X 0"))
			{
				informReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				containmentPlan = proposedPlan;
				/** Convert Java objects into strings for the reply message */
				try
				{
					Action action = new Action(getAID(), (AgentAction) containmentPlan);
					getContentManager().fillContent(informReply, action);
				} catch (Exception e)
				{
					e.printStackTrace();
				} 
			}
			else
			{
				informReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				containmentPlan = proposedPlan;
				/** Convert Java objects into strings for the reply message */
				try
				{
					Action action = new Action(getAID(), (AgentAction) containmentPlan);
					getContentManager().fillContent(informReply, action);
				} catch (Exception e)
				{
					e.printStackTrace();
				} 
			}
		}
		else if (proposedPlan.getStrategy().getExit().equals("go"))
		{
			informReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			containmentPlan = proposedPlan;
			/** Convert Java objects into strings for the reply message */
			try
			{
				Action action = new Action(getAID(), (AgentAction) containmentPlan);
				getContentManager().fillContent(informReply, action);
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
		}
		else
		{
			informReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
			containmentPlan = proposedPlan;
			/** Convert Java objects into strings for the reply message */
			try
			{
				Action action = new Action(getAID(), (AgentAction) containmentPlan);
				getContentManager().fillContent(informReply, action);
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
		}
	} // End of handlePlanProposal method
	
	/**
	 * Establish victim collection point
	 */
	public void establishCollectionPoint() 
	{
		/** Only establish collection point once */
		try
		{
			/** Only establish collection point once */
			if (getDSOLModel().getCollectionPoint().getSize().x == 0)
			{
				/** set location dependent on fire size if known */
				int xFirePosition;
				int yFirePosition;
				if (this.mergedAwareness.getFire().getLocation().getPosition() != null)
				{
					String x = new String();
					String y = new String();
					/** Convert fire location from string to x and y integers */
					int leftParenhtesis = mergedAwareness.getFire().getLocation().getPosition().lastIndexOf('(');
					int comma = mergedAwareness.getFire().getLocation().getPosition().lastIndexOf(',');
					int rightParenthesis = mergedAwareness.getFire().getLocation().getPosition().lastIndexOf(')');
			        x = mergedAwareness.getFire().getLocation().getPosition().substring(leftParenhtesis + 1, comma);
			        y = mergedAwareness.getFire().getLocation().getPosition().substring(comma + 1, rightParenthesis);
			        /** Set positions plus safety distance */
			        xFirePosition = Integer.parseInt(x) + 40;
			        yFirePosition = Integer.parseInt(y) + 40;
			        getDSOLModel().getCollectionPoint().setLocation(new DirectedPoint(xFirePosition, yFirePosition, 0));
				}
				/** set size */
				getDSOLModel().getCollectionPoint().setSize(6, 6, 0);
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		} 
		
	} // End of establishCollectionPoint method
	
} // End of OvD agent
