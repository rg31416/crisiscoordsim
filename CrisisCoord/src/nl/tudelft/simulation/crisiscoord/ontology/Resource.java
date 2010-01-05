/*
 * @(#) Resource.java September 25, 2008
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.ontology;

import jade.content.Concept;

/**
* Material Resource (Ontological class associated to the Resource schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Resource implements Concept 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Material resource status concept
	 */
	private String status;
	 
	/**
	 * Getter of the property <tt>status</tt>
	 *
	 * @return Returns the status.
	 * 
	 */
	public String getStatus()
	{
		return status;
	}
	
	/**
	 * Setter of the property <tt>status</tt>
	 *
	 * @param status The status to set.
	 *
	 */
	public void setStatus(final String status)
	{
		this.status = status;
	}
	
	/**
	 * Material resource estimated time of arrival concept
	 */
	private String eta;
	 
	/**
	 * Getter of the property <tt>eta</tt>
	 *
	 * @return Returns the eta.
	 * 
	 */
	public String getEta()
	{
		return eta;
	}
	
	/**
	 * Setter of the property <tt>eta</tt>
	 *
	 * @param eta The eta to set.
	 *
	 */
	public void setEta(final String eta)
	{
		this.eta = eta;
	}
	
	/**
	 * Material resource transport mode concept
	 */
	private String transport;
	 
	/**
	 * Getter of the property <tt>transport</tt>
	 *
	 * @return Returns the transport.
	 * 
	 */
	public String getTransport()
	{
		return transport;
	}
	
	/**
	 * Setter of the property <tt>transport</tt>
	 *
	 * @param transport The transport to set.
	 *
	 */
	public void setTransport(final String transport)
	{
		this.transport = transport;
	}
	
	/**
	 * Material resource destination concept
	 */
	private Location destination;
	 
	/**
	 * Getter of the property <tt>destination</tt>
	 *
	 * @return Returns the destination.
	 * 
	 */
	public Location getDestination()
	{
		return destination;
	}
	
	/**
	 * Setter of the property <tt>destination</tt>
	 *
	 * @param destination The destination to set.
	 *
	 */
	public void setDestination(final Location destination)
	{
		this.destination = destination;
	}
	
	/**
	 * Material resource last known location concept
	 */
	private Location location;
	 
	/**
	 * Getter of the property <tt>location</tt>
	 *
	 * @return Returns the location.
	 * 
	 */
	public Location getLocation()
	{
		return location;
	}
	
	/**
	 * Setter of the property <tt>location</tt>
	 *
	 * @param location The location to set.
	 *
	 */
	public void setLocation(final Location location)
	{
		this.location = location;
	}
	
	/**
	 * Material resource type concept
	 */
	private String type;
	 
	/**
	 * Getter of the property <tt>type</tt>
	 *
	 * @return Returns the type.
	 * 
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Setter of the property <tt>type</tt>
	 *
	 * @param type The type to set.
	 *
	 */
	public void setType(final String type)
	{
		this.type = type;
	}

}
