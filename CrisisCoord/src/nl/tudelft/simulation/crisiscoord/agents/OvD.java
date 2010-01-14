/*
 * @(#) OvD.java November 29, 2009
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
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.proto.ProposeInitiator;
import nl.tudelft.simulation.crisiscoord.CrisisCoordModel;
import nl.tudelft.simulation.crisiscoord.ontology.Awareness;
import nl.tudelft.simulation.crisiscoord.ontology.Plan;
import nl.tudelft.simulation.crisiscoord.ontology.Copi;
import nl.tudelft.simulation.crisiscoord.ontology.Location;

/**
* OvD (Officier van Dienst)- Fire Officer agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 1.6 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class OvD extends OfficerAgent 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;

	/** Experimental properties */
	/** Number of firemen */
	private int numFiremen;
	/** Number of OvD-Gs */
	private int numOvDG;
	/** Number of backup firemen */
	private int numBackupFiremen;
	/** Autonomous assignment or not of choice between fire fight and victim rescue */
	private boolean autoAssign;
	
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
		
		/** Define and register the services provided by this agent*/
		ServiceDescription serviceDescription  = new ServiceDescription();
		serviceDescription.setType("FireOfficerService");
		serviceDescription.setName(getLocalName());
        registerInDF(serviceDescription);

		/** Determine service description of officer, peer officer and responders */
        //setMyOfficerAID("OperationalLeaderService");
        peerOfficersService = "MedicalOfficerService";
        myRespondersService = "FiremanService";
        myBackupRespondersService = "BackupFiremanService";
        
        /** Initialize experimental properties */
		Properties properties;
		try
		{
			properties = getDSOLModel().getSimulator().getReplication().getTreatment().getProperties();
			this.numFiremen = new Integer(properties.getProperty("model.size.numfiremen"));
			this.numOvDG = new Integer(properties.getProperty("model.size.numovdg"));
			this.numBackupFiremen = new Integer(properties.getProperty("model.size.numbackupfiremen"));
			this.responseWaitingTimeout = new Integer(properties.getProperty("model.waiting.responseWaitingTimeout"));
			this.autoAssign = new Boolean(properties.getProperty("model.coordination.autoassignment"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/** Initialize attributes */
		this.setAgentArrived(false);
		this.setAlarmed(false);
		pointOfOrigin = CrisisCoordModel.STATION_POS;
		this.setDestination(directedPointToLocation(pointOfOrigin));
		myRespondersAIDs = new AID[numFiremen];
		peerOfficersAIDs = new AID[numOvDG];
		myBackupRespondersAIDs = new AID[numBackupFiremen];
		receivedAwarenessReports = new Awareness[numFiremen + numBackupFiremen];
        
		/** Start behaviour factory */
		threadedBehaviourFactory = new ThreadedBehaviourFactory();
		addBehaviour(threadedBehaviourFactory.wrap(new AlarmResponderBehaviour(this)));
        addBehaviour(threadedBehaviourFactory.wrap(new OvDBehaviour(this)));
        addBehaviour(threadedBehaviourFactory.wrap(new CheckArrivalStatus(this)));
        
	} // End of setup method
	
	/**
	 * Take down agent method
	 *///
	public void takeDown() 
	{
		threadedBehaviourFactory.interrupt();
		doDelete();
	}

	/**
	 * FSMBehaviour internal class: OvDBehaviour
	 */
	class OvDBehaviour extends FSMBehaviour 
	{
		
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;		
		
		/** Constant for transition to exit FiremanBehaviour */
		protected final int exit = 1;
		/** Constant for transition to stay within FiremanBehaviour */
		protected final int notExit = 0;
		/** Constant for transition to stay within ResponseBehaviour */
		protected final int proposalAccepted = 1;
		/** Constant for transition to end ResponseBehaviour */
		protected final int proposalRejected = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public OvDBehaviour(final Agent a)
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
		} // end of OvDBehaviour constructor
		
		/**
		 * onEnd of OvDBehavior
		 * @return 0
		 */
		public int onEnd()
		{
			return 0;
		}
		    
	} //End internal class: OvDBehaviour
	
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
	        setResponderNumber("OvD");
	        
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
		
		/** Constant for transition to exit OvD behaviour */
		protected int exit = 1;
		/** Constant for transition to stay within OvD behaviour */
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
			
			/** Counter of the number of replies received */
			private int acceptancesReceived  = 0;
			
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
				
				/** Get system date for message replyby timeout */
				Date currentDate = new Date();
				currentDate.setTime(currentDate.getTime() + responseWaitingTimeout);
				
				/** Prepare the message */
				ACLMessage proposemsg = new ACLMessage(ACLMessage.PROPOSE);
				proposemsg.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
				proposemsg.setLanguage(getCodec().getName());
				proposemsg.setOntology(getOntology().getName());
				proposemsg.setReplyByDate(currentDate);

				/** Convert Java objects into strings for the message */
				try
				{
					getContentManager().fillContent(proposemsg, mergedAwareness);
				} catch (Exception e)
				{
					e.printStackTrace();
				} 
				
				/** Determine receiver agents */
				peerOfficersAIDs = getPeerOfficersAIDs();
				if (peerOfficersAIDs != null)
				{
					for (int i = 0; i < peerOfficersAIDs.length; i++)
					{
							proposemsg.addReceiver(peerOfficersAIDs[i]);
							CrisisCoordModel.countMessage("interdisciplinary");
					}
				}
				
				if (peerOfficersAIDs.length > 0)
				{
					/** Send message using ProposeInitiator behaviour */
					myAgent.addBehaviour(new ProposeInitiator(myAgent, proposemsg) {
						/** Declares a serialVersionUID with no purpose */
						static final long serialVersionUID = 0;
						
						/** 
				         * overrides handleAllResponses
				         * @param responses the responses received
				         */
						@SuppressWarnings("unchecked")
						protected void handleAllResponses(final Vector responses)
						{
							acceptancesReceived = 0;
							
							for (int i = 0; i < responses.size(); i++)
							{
								ACLMessage responsemsg = (ACLMessage) responses.get(i);
								if (responsemsg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
								{
									acceptancesReceived++;
								}
								else if (responsemsg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
								{
									/** Extract reply content to adjust own awareness */
									/** Converting from String to Java objects (decoding the message) */
									try
									{
										ContentElement content = null;
										content = getContentManager().extractContent(responsemsg);
										Awareness receivedAwareness = (Awareness) content;
										
										/** Adjust own GRIP level with reply */
										mergedAwareness.setGrip(receivedAwareness.getGrip());
									} catch (Exception e)
									{
										e.printStackTrace();
									}
								}
								else if (responsemsg.getPerformative() == ACLMessage.NOT_UNDERSTOOD)
								{
									System.out.println(myAgent.getLocalName() + " received not understood reply");
								}
							}
							System.out.println(myAgent.getLocalName() + " received " + acceptancesReceived + " Shared awareness proposal acceptances");
						}
					});
				}
				
				/** Check if GRIP = 0 to exit response */
				if (mergedAwareness.getGrip().getLevel().equals("0"))
				{
					onEndReturnValue = exit;
					/** Erase CoPI icon */
					try
					{
						getDSOLModel().getCopi().setSize(0, 0, 0);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
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
		
		/** Counter of the number of replies received */
		private int acceptancesReceived  = 0;
		
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
			/** Get system date for message replyby timeout */
			Date currentDate = new Date();
			currentDate.setTime(currentDate.getTime() + responseWaitingTimeout);
			
			/** Prepare the message */
			ACLMessage proposemsg = new ACLMessage(ACLMessage.PROPOSE);
			proposemsg.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
			proposemsg.setLanguage(getCodec().getName());
			proposemsg.setOntology(getOntology().getName());
			proposemsg.setReplyByDate(currentDate);

			/** Prepare the content objects (enconding the message) */
			Location copiLocation = new Location();
			copiLocation.setPosition(getAlarm().getDestination().getPosition());
			copiLocation.setType("CoPI Location");
			Copi proposeCopi = new Copi();
			proposeCopi.setLocation(copiLocation);
			
			/** Set sender ID and wrap content objects inside an Action */
			Action action = new Action(getAID(), (AgentAction) proposeCopi);
			
			/** Convert Java objects into strings for the message */
			try
			{
				getContentManager().fillContent(proposemsg, action);
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
			
			/** Determine receiver agents */
			peerOfficersAIDs = getPeerOfficersAIDs();
			for (int i = 0; i < peerOfficersAIDs.length; i++)
			{
					proposemsg.addReceiver(peerOfficersAIDs[i]);
					CrisisCoordModel.countMessage("interdisciplinary");
			}
			
			if (peerOfficersAIDs.length > 0)
			{
				/** Send message using ProposeInitiator behaviour */
				myAgent.addBehaviour(new ProposeInitiator(myAgent, proposemsg) {
					/** Declares a serialVersionUID with no purpose */
					static final long serialVersionUID = 0;
					
					/** 
			         * overrides handleAllResponses
			         * @param responses the responses received
			         */
					@SuppressWarnings("unchecked")
					protected void handleAllResponses(final Vector responses)
					{
						acceptancesReceived = 0;
						
						for (int i = 0; i < responses.size(); i++)
						{
							ACLMessage responsemsg = (ACLMessage) responses.get(i);
							if (responsemsg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
							{
								acceptancesReceived++;
							}
							else if (responsemsg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
							{
								System.out.println("OvD received proposal rejection");
							}
							else if (responsemsg.getPerformative() == ACLMessage.NOT_UNDERSTOOD)
							{
								System.out.println("OvD received not understood message");
							}
						}
						System.out.println("OvD received " + acceptancesReceived + " CoPI proposal acceptances");
					}
				});
			}
			
			/** If proposal accepted by more than half the officers, visualize CoPI icon */
	        if (acceptancesReceived > (peerOfficersAIDs.length) / 2)
	        {
	        	try
				{
	        		getDSOLModel().getCopi().setLocation(getResponderProxy().getCenter());
	        		getDSOLModel().getCopi().setSize((int) CrisisCoordModel.HOUSE_SIZE.x, (int) CrisisCoordModel.HOUSE_SIZE.y, 0);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
	        } else 
	        {
	        	/** Erase CoPI icon */
				try
				{
					getDSOLModel().getCopi().setSize(0, 0, 0);
				} catch (Exception e)
				{
					e.printStackTrace();
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
	} //End internal class: EstablishCoPIBehaviour
	
	/**
	 * FSMBehaviour internal class: PlanContainmentBehaviour
	 */
	class PlanContainmentBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Counter of the number of proposal acceptances received */
		private int acceptancesReceived  = 0;
		
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
			/** Plan containment strategy */
			planContainment();
			
			/** Get system date for message replyby timeout */
			Date currentDate = new Date();
			currentDate.setTime(currentDate.getTime() + responseWaitingTimeout);
			
			/** Prepare the message */
			ACLMessage proposemsg = new ACLMessage(ACLMessage.PROPOSE);
			proposemsg.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
			proposemsg.setLanguage(getCodec().getName());
			proposemsg.setOntology(getOntology().getName());
			proposemsg.setReplyByDate(currentDate);

			/** Set sender ID and wrap content objects inside an Action */
			Action action = new Action(getAID(), (AgentAction) containmentPlan);
			
			/** Convert Java objects into strings for the message */
			try
			{
				getContentManager().fillContent(proposemsg, action);
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
			
			/** Determine receiver agents */
			peerOfficersAIDs = getPeerOfficersAIDs();
			for (int i = 0; i < peerOfficersAIDs.length; i++)
			{
					proposemsg.addReceiver(peerOfficersAIDs[i]);
					CrisisCoordModel.countMessage("interdisciplinary");
			}
			
			if (peerOfficersAIDs.length > 0)
			{
				/** Send message using ProposeInitiator behaviour */
				myAgent.addBehaviour(new ProposeInitiator(myAgent, proposemsg) {
					/** Declares a serialVersionUID with no purpose */
					static final long serialVersionUID = 0;
					
					/** 
			         * overrides handleAllResponses
			         * @param responses the responses received
			         */
					@SuppressWarnings("unchecked")
					protected void handleAllResponses(final Vector responses)
					{
						acceptancesReceived = 0;
						
						for (int i = 0; i < responses.size(); i++)
						{
							ACLMessage responsemsg = (ACLMessage) responses.get(i);
							if (responsemsg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
							{
								acceptancesReceived++;
							}
							else if (responsemsg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
							{
								try
								{
									/** Extract reply content */
									ContentElement content = null;
									content = myAgent.getContentManager().extractContent(responsemsg);
									/** If message content received is not an Action */
				                    if (content == null || !(content instanceof Action))
				                    {
				                    	System.err.println("Message body was not an action");
				                        throw new Exception();
				                    }
				                    /** Extract action content */
									Concept actionContent;
				                    actionContent = ((Action) content).getAction();
				                    /** If action content received is not a Plan */
				                    if (actionContent == null || !(actionContent instanceof Plan))
				                    {
				                    	System.err.println("Message body was not a Plan");
				                        throw new Exception();
				                    }
				                    
				                    /** Get plan content */
				                    Plan receivedPlan = (Plan) actionContent;
									
									/** Adjust strategy with reply */
									containmentPlan.setStrategy(receivedPlan.getStrategy());
								} catch (Exception e)
								{
									e.printStackTrace();
								}
							}
							else if (responsemsg.getPerformative() == ACLMessage.NOT_UNDERSTOOD)
							{
								System.out.println("OvD received not understood message");
							}
						}
						System.out.println("OvD received " + acceptancesReceived + " Plan proposal acceptances");
						
						/** Determine return value based on proposal acceptance */
						if (responses.size() == acceptancesReceived)
						{
							onEndReturnValue = proposalAccepted;
						}
						else 
						{
							onEndReturnValue = proposalRejected;
						}
					}
				});
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
	} //End internal class: PlanContainmentBehaviour
	
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
				/** Get system date for message replyby timeout */
				Date currentDate = new Date();
				currentDate.setTime(currentDate.getTime() + responseWaitingTimeout);
				
				/** Prepare the alarm message */
				ACLMessage requestmsg = new ACLMessage(ACLMessage.REQUEST);
				requestmsg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				requestmsg.setLanguage(getCodec().getName());
				requestmsg.setOntology(getOntology().getName());
				requestmsg.setContent("rescueAssigned");
				requestmsg.setReplyByDate(currentDate);

				/** Determine receiver agents */
				myRespondersAIDs = getMyRespondersAIDs();
				int assignedRescue = Integer.parseInt(containmentPlan.getStrategy().getPriorities());
				/** if assignment is autonomous, then do not send request */
				if (autoAssign)
				{
					assignedRescue = 0;
				}
				System.out.println("Sending rescue assignment to " + assignedRescue + " firemen");
				for (int i = 0; i < assignedRescue; i++)
				{
					requestmsg.addReceiver(myRespondersAIDs[i]);
					CrisisCoordModel.countMessage("fire");
				}
				
				if (assignedRescue > 0)
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
							System.out.println(myAgent.getLocalName() + " received " + responses.size() + " assignment responses");
						}
					});	
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
					getDSOLModel().launchAdditionalFiremen();
					
					try
					{
						Thread.sleep(CrisisCoordModel.OFFICER_SLEEP);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					
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
			} //end of action method
			
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
			
			/**
			 * Done
			 * @return true when behaviour done
			 */
			public boolean done()
			{
				return finished;
			}
		} //End internal class: DeployUnitsBehaviour
		
	} //End internal class: DeployContainmentBehaviour
	
	/**
	 * FSMChildBehaviour internal class: NotifyExitBehaviour
	 */
	class NotifyExitBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public NotifyExitBehaviour(final Agent a)
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

	        /** Sleep a while to allow responders to arrive if they have not */
	        try
			{
				Thread.sleep(20000);
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			
	        /** terminate current experimental replication */
			getDSOLModel().terminateReplication();
			
	        /** Delete agent and responder proxy from model */
			this.getParent().getDataStore().clear();
			takeDown();
			
			finished = true;
		}
		
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: NotifyExitBehaviour
	
} // End of OvD agent
