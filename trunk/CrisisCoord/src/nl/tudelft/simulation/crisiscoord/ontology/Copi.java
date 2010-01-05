/*
 * @(#) Copi.java May 19, 2009
 * 
 * Copyright (c) 2009 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.ontology;

import jade.content.AgentAction;

/**
* CoPI (Ontological class associated to the Copi schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @author <a> Rafael Gonzalez </a>
*/
public class Copi implements AgentAction 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * CoPI Location concept
	 */
	private Location location;
	 
	/**
	 * Getter of the property <tt>Location</tt>
	 *
	 * @return Returns the Copi Location.
	 * 
	 */
	public Location getLocation()
	{
		return location;
	}
	
	/**
	 * Setter of the property <tt>Location</tt>
	 *
	 * @param location The Location to set.
	 *
	 */
	public void setLocation(final Location location)
	{
		this.location = location;
	}

}
