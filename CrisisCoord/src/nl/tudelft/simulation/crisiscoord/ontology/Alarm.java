/*
 * @(#) Alarm.java October 23, 2008
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
* Alarm (Ontological class associated to the Alarm schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Alarm implements AgentAction 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Alarm Destination concept
	 */
	private Location adestination;
	 
	/**
	 * Getter of the property <tt>Destination</tt>
	 *
	 * @return Returns the Destination.
	 * 
	 */
	public Location getDestination()
	{
		return adestination;
	}
	
	/**
	 * Setter of the property <tt>Destination</tt>
	 *
	 * @param adestination The Destination to set.
	 *
	 */
	public void setDestination(final Location adestination)
	{
		this.adestination = adestination;
	}

	/**
	 * Alarm type primitive concept
	 */
	private String atype;
	 
	/**
	 * Getter of the property <tt>atype</tt>
	 *
	 * @return Returns the atype.
	 * 
	 */
	public String getType()
	{
		return atype;
	}
	
	/**
	 * Setter of the property <tt>atype</tt>
	 *
	 * @param atype The atype to set.
	 *
	 */
	public void setType(final String atype)
	{
		this.atype = atype;
	}

}
