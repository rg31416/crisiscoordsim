/*
 * @(#) Civilian.java September 25, 2008
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
* Civilian (Ontological class associated to the Civilian schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @author <a> Rafael Gonzalez </a>
*/
public class Civilian implements Concept 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Civilian status concept
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
	 * Civilian location concept
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

}
