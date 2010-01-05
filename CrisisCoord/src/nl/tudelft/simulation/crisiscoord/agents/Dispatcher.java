/*
 * @(#) Dispatcher.java November 20, 2009
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import java.util.Date;
import java.util.Vector;

import jade.content.AgentAction;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import nl.tudelft.simulation.crisiscoord.CrisisCoordModel;
import nl.tudelft.simulation.crisiscoord.ontology.Alarm;
import nl.tudelft.simulation.crisiscoord.ontology.Location;

/**
* Dispatcher agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @author <a> Rafael Gonzalez </a>
*/
public class Dispatcher extends ServiceAgent 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;

	/** Reference to the DSOL model */
	protected CrisisCoordModel model;
	
	/**
	 * Setup of the agent
	 */
	protected void setup() 
    {
		/** Attach model reference */
		Object [] args = getArguments();
		this.model = (CrisisCoordModel) args[0];
		
		/** Register language and ontology */
		setCodec();
		manager.registerLanguage(getCodec());
		setOntology();
		manager.registerOntology(getOntology());
		
		/** Define and register the service provided by this agent*/
		ServiceDescription serviceDescription  = new ServiceDescription();
		serviceDescription.setType("DispatcherService");
		serviceDescription.setName(getLocalName());
        registerInDF(serviceDescription);
        
        /** Add SendAlarm behaviour */
        addBehaviour(new SendAlarm(this));
	} // End of setup method
	
	/**
	 * Take down agent method
	 *///
	protected void takeDown() 
	{
		doDelete();
	} // End of takeDown method

	/**
	 * SimpleBehavior internal class: Send Alarm
	 */
	class SendAlarm extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** boolean value to finish behaviour */
		protected boolean finished = false;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public SendAlarm(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for SimpleBehaviour
		 */
		public void action()
		{
			/** Get all agents descriptions for broadcast */
			AMSAgentDescription [] agentDescriptions = null;
			try
			{
				SearchConstraints constraints = new SearchConstraints();
				constraints.setMaxResults (new Long(-1));
				agentDescriptions = AMSService.search(this.myAgent, new AMSAgentDescription (), constraints);
			} catch (Exception e) 
			{
				System.out.println("Problem searching AMS: " + e);
				e.printStackTrace();
			}
			
			/** Wait for fire message from self before sending the alarm */
			ACLMessage received = myAgent.blockingReceive();
			
			/** Get system date for message replyby timeout */
			Date currentDate = new Date();
			currentDate.setTime(currentDate.getTime() + 10000);
			
			/** Prepare the message */
			ACLMessage requestmsg = new ACLMessage(ACLMessage.REQUEST);
			requestmsg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			requestmsg.setLanguage(getCodec().getName());
			requestmsg.setOntology(getOntology().getName());
			requestmsg.setReplyByDate(currentDate);

			/** Prepare the content objects (enconding the message) */
			Location location = new Location();
			location.setPosition(received.getContent());
			location.setType("Accident location");
			Alarm alarm = new Alarm();
			alarm.setType("Accident");
			alarm.setDestination(location);
			
			/** Set sender ID and wrap content objects inside an Action */
			Action action = new Action(getAID(), (AgentAction) alarm);

			/** Convert Java objects into strings for the message */
			try
			{
				getContentManager().fillContent(requestmsg, action);
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
			
			/** Determine receiver agents */
			for (int i = 0; i < agentDescriptions.length; i++)
			{
				requestmsg.addReceiver(agentDescriptions[i].getName());
			}
			
			/** Send message using AchieveREInitiator behaviour */
			myAgent.addBehaviour(new AchieveREInitiator(myAgent, requestmsg) {
				
				/** Declares a serialVersionUID with no purpose */
				static final long serialVersionUID = 0;
				
				@SuppressWarnings("unchecked")
				protected void handleAllResponses(final Vector responses)
				{
					System.out.println(myAgent.getLocalName() + " received " + responses.size() + " Alarm responses");
					
				}
			});	
			finished = true;
		} // end of Action method
		
		/**
		 * Done (must be implemented for SimpleBehaviour to end)
		 * @return true if behaviour is finished, false otherwise
		 */
		public boolean done()
		{
			return finished;
		}
	} //End internal class: SendAlarm
	
}
