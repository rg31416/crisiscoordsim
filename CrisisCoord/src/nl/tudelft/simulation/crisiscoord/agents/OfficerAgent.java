/*
 * @(#) OfficerAgent.java January 14, 2010
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import nl.tudelft.simulation.crisiscoord.CrisisCoordModel;
import nl.tudelft.simulation.crisiscoord.ontology.Alarm;
import nl.tudelft.simulation.crisiscoord.ontology.Awareness;
import nl.tudelft.simulation.crisiscoord.ontology.Element;
import nl.tudelft.simulation.crisiscoord.ontology.Grip;
import nl.tudelft.simulation.crisiscoord.ontology.Time;
import nl.tudelft.simulation.crisiscoord.ontology.Traffic;
import nl.tudelft.simulation.crisiscoord.ontology.Weather;
import nl.tudelft.simulation.crisiscoord.ontology.Fire;
import nl.tudelft.simulation.crisiscoord.ontology.Location;
import nl.tudelft.simulation.crisiscoord.ontology.Plan;
import nl.tudelft.simulation.crisiscoord.ontology.Population;
import nl.tudelft.simulation.crisiscoord.ontology.Resource;
import nl.tudelft.simulation.crisiscoord.ontology.Responder;
import nl.tudelft.simulation.crisiscoord.ontology.Strategy;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

import java.util.Date;
import java.util.Vector;


/**
* Officer Agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @author <a> Rafael Gonzalez </a>
*/
public class OfficerAgent extends ResponseAgent
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/** Experimental properties */
	/** Timeout for officers to wait for message replies */
	protected int responseWaitingTimeout;
	
	/** Agent IDs of responders led by this agent */
	protected AID [] myRespondersAIDs;
	
	/** Agent IDs of backup responders led by this agent */
	protected AID [] myBackupRespondersAIDs;
	
	/** Service description of responders led by this agent */
	protected String myRespondersService = new String();
	
	/** Service description of backup responders led by this agent */
	protected String myBackupRespondersService = new String();
	
	/** Situation awareness from responders */
	protected Awareness [] receivedAwarenessReports;
	
	/** Service description of officers of other disciplines */
	protected String peerOfficersService = new String();
	
	/** Agent IDs of peer officers */
	protected AID [] peerOfficersAIDs;
	
	/** Merged situation awareness */
	protected Awareness mergedAwareness = new Awareness();
	
	/** Containment Plan */
	protected Plan containmentPlan = new Plan();
	
	/** Backup called */
	protected boolean backupCalled = false;
	
	/** Counter of the number of replies received */
	private int assessmentRepliesReceived  = 0;
	

	/**
	 * FSMChildBehaviour internal class: NotifyArrivalBehaviour
	 */
	class NotifyArrivalBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public NotifyArrivalBehaviour(final Agent a)
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
	} //End internal class: NotifyArrivalBehaviour
	
	/**
	 * FSMChildBehaviour internal class: RequestAssessmentBehaviour
	 */
	class RequestAssessmentBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public RequestAssessmentBehaviour(final Agent a)
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
			ACLMessage querymsg = new ACLMessage(ACLMessage.QUERY_REF);
			querymsg.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
			querymsg.setLanguage(getCodec().getName());
			querymsg.setOntology(getOntology().getName());
			querymsg.setReplyByDate(currentDate);

			/** Prepare the content objects (enconding the message) */
			Awareness queryAwareness = new Awareness();
			
			/** Convert Java objects into strings for the message */
			try
			{
				getContentManager().fillContent(querymsg, queryAwareness);
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
			
			/** Determine receiver agents */
			myRespondersAIDs = getMyRespondersAIDs();
			for (int i = 0; i < myRespondersAIDs.length; i++)
			{
				querymsg.addReceiver(myRespondersAIDs[i]);
				if (myRespondersService.contains("Medic"))
				{
					CrisisCoordModel.countMessage("medical");
				}
				else if (myRespondersService.contains("Fire"))
				{
					CrisisCoordModel.countMessage("fire");
				}
			}
			myBackupRespondersAIDs = getMyBackupRespondersAIDs();
			for (int i = 0; i < myBackupRespondersAIDs.length; i++)
			{
				querymsg.addReceiver(myBackupRespondersAIDs[i]);
				if (myRespondersService.contains("Medic"))
				{
					CrisisCoordModel.countMessage("medical");
				}
				else if (myRespondersService.contains("Fire"))
				{
					CrisisCoordModel.countMessage("fire");
				}
			}
			
			if (myRespondersAIDs.length > 0)
			{
				/** Send message using AchieveREInitiator behaviour */
				myAgent.addBehaviour(new AchieveREInitiator(myAgent, querymsg) 
				{
					/** Declares a serialVersionUID with no purpose */
					static final long serialVersionUID = 0;
					
					/** array of replies */
					private Awareness [] replies = new Awareness[myRespondersAIDs.length + myBackupRespondersAIDs.length];
					
					/** 
			         * overrides handleAllResultNotifications
			         * @param resultNotifications the results received
			         */
					@SuppressWarnings("unchecked")
					protected void handleAllResultNotifications(final Vector resultNotifications)
					{
						for (int i = 0; i < resultNotifications.size(); i++)
						{
							/** Converting from String to Java objects (decoding the message) */
							try
							{
								ContentElement content = null;
								content = getContentManager().extractContent((ACLMessage) resultNotifications.get(i));
								Awareness receivedAwareness = (Awareness) content;
								this.replies[i] = receivedAwareness;
								
							} catch (Exception e)
							{
								e.printStackTrace();
							}
						}
						setAwarenessReports(replies);
						assessmentRepliesReceived = resultNotifications.size();
					}
					
					/** 
			         * overrides handleAllResponses
			         * @param responses the results received
			         */
					@SuppressWarnings("unchecked")
					protected void handleAllResponses(final Vector responses)
					{
						//do nothing
					}
				});
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
	} //End internal class: RequestAssessmentBehaviour
	
	/**
	 * FSMChildBehaviour internal class: MergeAssessmentsBehaviour
	 */
	class MergeAssessmentsBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Constant for transition skipping InformPlanStatusBehaviour */
		protected int firstAwareness = 0;
		/** Constant for transition into InformPlanStatusBehaviour */
		protected int otherAwareness = 1;
		
		/**
		 * Constructor
		 * @param a the agent
		 * @param firstAwareness indicates this behaviour results in first situation Awareness
		 * @param otherAwareness indicates this behaviour results in second or subsequent awareness
		 */
		public MergeAssessmentsBehaviour(final Agent a, final int firstAwareness, final int otherAwareness)
		{
			super(a);
			this.firstAwareness = firstAwareness;
			this.otherAwareness = otherAwareness;
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			/** Check if first or later awareness */
			if (mergedAwareness.getPopulation() != null)
			{
				onEndReturnValue = otherAwareness;
				/** if not first awareness, only update if replies received */
				if (assessmentRepliesReceived > 0)
				{
					mergeAssessments();
				}
			}
			else
			{
				onEndReturnValue = firstAwareness;
				System.out.println("THIS IS THE FIRST AWARNESS");
				mergeAssessments();
			}
			
			/** Print merged awareness (for debugging) */
			System.out.println (this.myAgent.getLocalName() + " " + assessmentRepliesReceived + " MERGED AWARENESS: ");
			System.out.println (" Victims: " + mergedAwareness.getPopulation().getVictims()
					+ " Fire size: " + mergedAwareness.getFire().getScope());
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
	} //End internal class: MergeAssessmentsBehaviour
	
	
	/**
	 * FSMBehaviour internal class: CommunicatePlanBehaviour
	 */
	class CommunicatePlanBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public CommunicatePlanBehaviour(final Agent a)
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
				Thread.sleep(CrisisCoordModel.OFFICER_SLEEP);
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
	} //End internal class: CommunicatePlanBehaviour
	
	/**
	 * Get AIDs of responder agents
	 * @return myRespondersAIDs
	 */
	protected AID [] getMyRespondersAIDs() 
	{
		AID [] responderAgentIDs = searchDF(myRespondersService);
    	if (responderAgentIDs.length == 0)
	    {
	    	return null;
	    }
	    else
	    {
	    	return responderAgentIDs;
	    }
	} // End of getMyRespondersAIDs method
	
	/**
	 * Get AIDs of backup responder agents
	 * @return myRespondersAIDs
	 */
	protected AID [] getMyBackupRespondersAIDs() 
	{
		AID [] responderAgentIDs = searchDF(myBackupRespondersService);
    	if (responderAgentIDs.length == 0)
	    {
	    	return responderAgentIDs;
	    }
	    else
	    {
	    	return responderAgentIDs;
	    }
	} // End of getMyRespondersAIDs method
	
	/**
	 * Set Awareness Reports
	 * @param awarenessReports array of awareness reports 
	 */
	protected void setAwarenessReports(final Awareness [] awarenessReports) 
	{
		this.receivedAwarenessReports = awarenessReports;
	} // End of setAwarenessReports method
	
	/**
	 * Get Awareness Reports
	 * @return awarenessReports array of awareness reports 
	 */
	protected Awareness [] getAwarenessReports() 
	{
		return this.receivedAwarenessReports;
	} // End of getAwarenessReports method
	
	/**
	 * Get AIDs of peer officer agents
	 * @return peerOfficersAIDs
	 */
	protected AID [] getPeerOfficersAIDs() 
	{
		AID [] peerOfficersAIDs = searchDF(peerOfficersService);
    	if (peerOfficersAIDs.length == 0)
	    {
	    	return null;
	    }
	    else
	    {
	    	return peerOfficersAIDs;
	    }
	} // End of getPeerOfficersAIDs method
	
	/**
	 * Plan containment strategy
	 */
	public void planContainment() 
	{
		/** Create strategy */
		Strategy plannedStrategy = new Strategy();
		plannedStrategy.setPriorities(new String(""));
		
		/** Determine assignment priority */
		/** results in number of responders assigned to victim rescue */
		/** this is # of victims - # of medics not exceeding half of firemen */
		int numRespondersAssignedToRescue = mergedAwareness.getPopulation().getVictims()
			- Integer.parseInt(mergedAwareness.getResponders().getStatus());
		if (numRespondersAssignedToRescue < 0)
		{
			numRespondersAssignedToRescue = 0;
		}
		AID [] firemanAgentIDs = searchDF("FiremanService");
		if (numRespondersAssignedToRescue > firemanAgentIDs.length / 2)
		{
			numRespondersAssignedToRescue = firemanAgentIDs.length / 2;
		}
		plannedStrategy.setPriorities(Integer.toString(numRespondersAssignedToRescue));
		
		/** Determine exit strategy */
		if (assessmentRepliesReceived > 0 && mergedAwareness.getFire().getScope().equals("0 X 0") 
				&& mergedAwareness.getPopulation().getVictims() == 0)
		{
			plannedStrategy.setExit("exit");
		}
		else
		{
			plannedStrategy.setExit("go");
		}
		
		
		/** Create plan */
		containmentPlan.setLocations(new Location());
		containmentPlan.setResources(new Resource());
		containmentPlan.getResources().setDestination(new Location());
		containmentPlan.getResources().setLocation(new Location());
		containmentPlan.getResources().setStatus(new String());
		containmentPlan.setResponders(new Responder());
		containmentPlan.getResponders().setLocation(new Location());
		containmentPlan.setStrategy(plannedStrategy);
		
	} // End of planContainment method
	
	/**
	 * Merge assessments method
	 */
	public void mergeAssessments()
	{
			/** --- Merge Elements awareness --- */
			Element mergedElements = new Element();
			
			/** --- Merge fire awareness --- */
			Location fireLocation = new Location();
			fireLocation.setType("Merged fire location");
			int xFirePosition = 0;
			int yFirePosition = 0;
			int xFireSize = 0;
			int yFireSize = 0;
			if (assessmentRepliesReceived > 0)
			{
				/** Add fire size until end of array or until null in case replies did not arrive */
				for (int i = 0; i < assessmentRepliesReceived && receivedAwarenessReports[i] != null; i++)
				{
					String x = new String();
					String y = new String();
					/** Convert fire location from string to x and y integers */
					int leftParenhtesis = receivedAwarenessReports[i].getFire().getLocation().getPosition().lastIndexOf('(');
					int comma = receivedAwarenessReports[i].getFire().getLocation().getPosition().lastIndexOf(',');
					int rightParenthesis = receivedAwarenessReports[i].getFire().getLocation().getPosition().lastIndexOf(')');
			        x = receivedAwarenessReports[i].getFire().getLocation().getPosition().substring(leftParenhtesis + 1, comma);
			        y = receivedAwarenessReports[i].getFire().getLocation().getPosition().substring(comma + 1, rightParenthesis);
			        xFirePosition += Integer.parseInt(x);
			        yFirePosition += Integer.parseInt(y);
					
			        /** Convert fire scope string to x and y integers */
					int by = receivedAwarenessReports[i].getFire().getScope().lastIndexOf('X');
			        x = receivedAwarenessReports[i].getFire().getScope().substring(0, by);
			        y = receivedAwarenessReports[i].getFire().getScope().substring(by + 1);
			        xFireSize += Integer.parseInt(x.trim());
					yFireSize += Integer.parseInt(y.trim());
				}
				/** Compute averages and set merged Fire values  */
				xFirePosition /= assessmentRepliesReceived;
				yFirePosition /= assessmentRepliesReceived;
				fireLocation.setPosition("(" + xFirePosition + "," + yFirePosition + ")");
				xFireSize /= assessmentRepliesReceived;
				yFireSize /= assessmentRepliesReceived;
			}
			Fire mergedFire = new Fire();
			mergedFire.setLocation(fireLocation);
			mergedFire.setScope(xFireSize + " X " + yFireSize);
			
			/** --- Merge Grip awareness --- */
			Grip mergedGrip = new Grip();
			
			/** --- Merge point of view awareness --- */
			Location mergedPointOfView = new Location();
			
			/** --- Merge population awareness (average) --- */
			Population mergedPopulation = new Population();
			int numCivilians = 0;
			int numVictims = 0;
			/** Add civilians and victims until end of array or until null in case replies did not arrive */
			if (assessmentRepliesReceived > 0)
			{
				for (int i = 0; i < assessmentRepliesReceived && receivedAwarenessReports[i] != null; i++)
				{
					numCivilians += receivedAwarenessReports[i].getPopulation().getCivilians();
					numVictims += receivedAwarenessReports[i].getPopulation().getVictims();
				}
				/** Compute averages and set merged population values */
				numCivilians /= assessmentRepliesReceived;
				numVictims /= assessmentRepliesReceived;
			}
			mergedPopulation.setCivilians(numCivilians);
			mergedPopulation.setVictims(numVictims);
			
			/** --- Merge resource awareness --- */
			Resource mergedResources = new Resource();
			
			/** --- Merge responder awareness --- */
			Responder mergedResponders = new Responder();
			/** in status determine the number of medics later used for priority in plan containment */
			AID [] medicAgentIDs = searchDF("MedicService");
			mergedResponders.setStatus(Integer.toString(medicAgentIDs.length));
			
			/** --- Merge time awareness --- */
			Time mergedTime = new Time();
			
			/** --- Merge traffic awareness --- */
			Traffic mergedTraffic = new Traffic();
			
			/** --- Merge weather awareness --- */
			Weather mergedWeather = new Weather();
			
			/** --- Set merged values --- */
			mergedAwareness.setElements(mergedElements);
			mergedAwareness.setFire(mergedFire);
			mergedAwareness.setGrip(mergedGrip);
			mergedAwareness.setPointOfView(mergedPointOfView);
			mergedAwareness.setPopulation(mergedPopulation);
			mergedAwareness.setResources(mergedResources);
			mergedAwareness.setResponders(mergedResponders);
			mergedAwareness.setTime(mergedTime);
			mergedAwareness.setTraffic(mergedTraffic);
			mergedAwareness.setWeather(mergedWeather);
	} // End of mergeAssessments method
	
	/**
	 * Determine GRIP level method
	 */
	public void determineGRIPlevel()
	{
		Grip gripLevel = new Grip();
		/** Check if there are still victims or fire (according to shared awareness) */
	    if (assessmentRepliesReceived > 0)
	    {	
	    	if (mergedAwareness.getPopulation().getVictims() == 0 && mergedAwareness.getFire().getScope().equals("0 X 0")) 
	    	{
		    	gripLevel.setLevel("0");
	    	} else
	    	{
		    	gripLevel.setLevel("1");
	    	}
	    }
	    /** otherwise, check directly on model for victims or fire */
	    else
	    {
			/** Read victims from model */
	    	int numVictims = 0;
	    	int x = 0;
		    int y = 0;
	    	try 
		    {
	    		for (int i = 0; i < getDSOLModel().getCivilians().length; i++)
				{
					/** Determine whether civilian is a victim or not */
					if (getDSOLModel().getCivilian(i).getState() > 0)
					{
						numVictims++;
					}
				}
				/** Read fire from model */
	    		x = (int) getDSOLModel().getFire().getSize().x;
		        y = (int) getDSOLModel().getFire().getSize().y;
		    } catch (Exception e)
		    {
		    	e.printStackTrace();
		    	numVictims = 0;
		    	x = 0; 
		    	y = 0;
		    }	
		    /** if victims or fire found, GRIP 1 */
		    if (numVictims > 0 || x > 0 || y > 0)
		    {
		    	gripLevel.setLevel("1");
		    }
		    else /** otherwise GRIP 0 */
		    {
		    	gripLevel.setLevel("0");
		    }
	    }
	    
		mergedAwareness.setGrip(gripLevel);
	} // End of mergeAssessments method
	
	/**
	 * SimpleBehaviour internal class: AlarmResponderBehaviour
	 */
	class AlarmResponderBehaviour extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** finished behaviour boolean value */
		private boolean finished = false;

		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public AlarmResponderBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for OneShotBehaviour
		 */
		public void action()
		{
			/** Prepare the message to accept or reject merged awareness as reply to proposal message from OvD */
			MessageTemplate template = MessageTemplate
            .and(AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST),
            MessageTemplate.and(MessageTemplate.MatchOntology(getOntology().getName()),
            MessageTemplate.MatchLanguage(getCodec().getName())));
		                
			ACLMessage request = myAgent.receive(template);
			if (request != null)
			{
				ACLMessage response = request.createReply();
				ContentElement content = null;
				Concept actionContent = null;
				
				/** Convert from String to Java objects (decoding the message) */
				try
				{
					content = myAgent.getContentManager().extractContent(request);
				} catch (Exception e)
				{
					/** if an exception occurs return not understood reply */
					e.printStackTrace();
					response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					response.setContent("Decoding failed");
					send(response);
				}
				
				/** If message received is Action */
				if (content instanceof Action)
				{
					/** Extract content of action */
					actionContent = ((Action) content).getAction();
					
					/** Handle Alarm request */
					if (actionContent instanceof Alarm)
					{
						/** Get content and Extract destination from alarm */
				        setAlarm((Alarm) actionContent);
				        setDestination(getAlarm().getDestination());
				        
				        /** Generate response (AGREE) */ 
				        response.setPerformative(ACLMessage.AGREE);
				        send(response);
				        
				        /** Set own status to alarmed and send message to self for unblocking agent */
				        setAlarmed(true);
				        ACLMessage alarmSelfmsg = new ACLMessage(ACLMessage.INFORM);
				        alarmSelfmsg.setContent(getResponderNumber() + "Alarmed");
				        alarmSelfmsg.addReceiver(getAID());
				        send(alarmSelfmsg);
					}
				} else /** for all other messages, reply with not understood */ 
				{
					response.setPerformative(ACLMessage.NOT_UNDERSTOOD);	
					send(response);
				}     
				finished = true;
				this.getParent().getDataStore().clear();
			} else 
			{
				block();
			}

		} // end of action method
		
		/**
		 * Done
		 * @return true when behaviour done
		 */
		public boolean done()
		{
			return finished;
		}
	} //End internal class: MessageResponderBehaviour
	
} // End of OfficerAgent class
