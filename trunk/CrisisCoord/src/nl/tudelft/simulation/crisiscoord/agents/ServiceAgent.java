/*
 * @(#) ServiceAgent.java November 11, 2008
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import nl.tudelft.simulation.crisiscoord.ontology.CrisisCoordOntology;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
* Service providing agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @author <a> Rafael Gonzalez </a>
*/
public class ServiceAgent extends Agent
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/** Content language codec for the ontology */
	private Codec codec;
	
	/** Instance of the CrisisCoordOntology */
	private Ontology ontology;
	
	/** Content manager for the ontology */
	protected ContentManager manager = (ContentManager) getContentManager();
	
	/**
	 * Setup of the agent
	 */
	protected void setup()
	{

	} // End of setup method

	/**
	 * Take down agent method
	 */
	protected void takeDown() 
	{
		doDelete();
	} // End of takeDown method
	
	/**
	 * Delete agent method
	 */
	public void doDelete()
	{
		try 
		{
			DFAgentDescription agentDescription = new DFAgentDescription();
			agentDescription.setName(getAID());
			DFService.deregister(this, agentDescription);
			System.out.println("[Deregistering successful]");
		} catch (Exception e) 
		{
			System.out.println("[Problems deregistering agent]");
		}
	} // End of doDelete method
	
	/**
	 * registerInDF (registers agent services in DF)
	 * @param serviceDescription service description of service to register
	 */
	public void registerInDF(final ServiceDescription serviceDescription) 
	{
		DFAgentDescription agentDescription = new DFAgentDescription();
		agentDescription.setName(getAID());
		agentDescription.addServices(serviceDescription);

		try 
		{  
			/** First check for previous registration */
			DFAgentDescription [] agentDescriptions = DFService.search(this, agentDescription);
			if (agentDescriptions.length > 0)
			{
            	System.out.println("Already found... deregister");
				DFService.deregister(this);
			}
			
			/** Then register */
			DFService.register(this, agentDescription);  
        } catch (FIPAException fe) 
		{
			System.out.println("FIPA Exception: " + fe.getMessage()); 
		}
	} //End of registerInDF method

	/**
	 * Search the Directory Facilitator
	 * @param service is the service to look for
	 * @return list of agent IDs providing the service (as registered on DF)
	 */
	public AID [] searchDF(final String service)
	{
		DFAgentDescription agentDescription = new DFAgentDescription();
   		ServiceDescription serviceDescription = new ServiceDescription();
   		serviceDescription.setType(service);
   		agentDescription.addServices(serviceDescription);
		
		SearchConstraints constraints = new SearchConstraints();
		constraints.setMaxResults(new Long(-1));

		try
		{
			DFAgentDescription[] agentDescriptions = DFService.search(this, agentDescription);
			AID[] agentIDs = new AID[agentDescriptions.length];
			for (int i = 0; i < agentDescriptions.length; i++)
			{
				agentIDs[i] = agentDescriptions[i].getName();
			}
			return agentIDs;
		} catch (FIPAException fe) 
		{ 
			System.out.println("FIPA Exception: " + fe.getMessage()); 
		}
      	return null;

	} //End of searchDF method	
	
	/**
	 * Get Codec method
	 * @return codec 
	 */
	public Codec getCodec() 
	{
	    return codec;
	} // End of getCodec method
	
	/**
	 * Set Codec method
	 */
	public void setCodec() 
	{
		this.codec = (Codec) new SLCodec();
	} // End of setCodec method
	
	/**
	 * Get Ontology method
	 * @return ontology 
	 */
	public Ontology getOntology() 
	{
	    return ontology;
	} // End of getOntology method
	
	/**
	 * Set Ontology method
	 */
	public void setOntology() 
	{
		this.ontology = CrisisCoordOntology.getInstance();
	} // End of setOntology method
	
}