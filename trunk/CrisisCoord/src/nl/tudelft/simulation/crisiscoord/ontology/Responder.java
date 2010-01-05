/*
 * @(#) Responder.java September 25, 2008
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
* Responder (Ontological class associated to the Responder schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Responder implements Concept 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Responder status concept
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
	 * Responder last known location concept
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
	 * Responder type concept
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
