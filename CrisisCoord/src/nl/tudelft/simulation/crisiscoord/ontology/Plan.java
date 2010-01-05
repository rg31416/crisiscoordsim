/*
 * @(#) Plan.java June 19, 2009
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.ontology;

import jade.content.AgentAction;

/**
* Plan (Ontological class associated to the Plan schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Plan implements AgentAction 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Locations concept
	 */
	private Location locations;
	 
	/**
	 * Getter of the property <tt>locations</tt>
	 *
	 * @return Returns the locations.
	 * 
	 */
	public Location getLocations()
	{
		return locations;
	}
	
	/**
	 * Setter of the property <tt>locations</tt>
	 *
	 * @param locations The locations to set.
	 *
	 */
	public void setLocations(final Location locations)
	{
		this.locations = locations;
	}
	
	/**
	 * Resources concept
	 */
	private Resource resources;
	 
	/**
	 * Getter of the property <tt>resources</tt>
	 *
	 * @return Returns the resources.
	 * 
	 */
	public Resource getResources()
	{
		return resources;
	}
	
	/**
	 * Setter of the property <tt>resources</tt>
	 *
	 * @param resources The resources to set.
	 *
	 */
	public void setResources(final Resource resources)
	{
		this.resources = resources;
	}
	
	/**
	 * Responders concept
	 */
	private Responder responders;
	 
	/**
	 * Getter of the property <tt>responders</tt>
	 *
	 * @return Returns the responders.
	 * 
	 */
	public Responder getResponders()
	{
		return responders;
	}
	
	/**
	 * Setter of the property <tt>responders</tt>
	 *
	 * @param responders The responders to set.
	 *
	 */
	public void setResponders(final Responder responders)
	{
		this.responders = responders;
	}
	
	/**
	 * Strategy concept
	 */
	private Strategy strategy;
	 
	/**
	 * Getter of the property <tt>strategy</tt>
	 *
	 * @return Returns the strategy.
	 * 
	 */
	public Strategy getStrategy()
	{
		return strategy;
	}
	
	/**
	 * Setter of the property <tt>strategy</tt>
	 *
	 * @param strategy The strategy to set.
	 *
	 */
	public void setStrategy(final Strategy strategy)
	{
		this.strategy = strategy;
	}
	
}