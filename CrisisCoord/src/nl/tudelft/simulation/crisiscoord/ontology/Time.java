/*
 * @(#) Time.java November 18, 2008
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
* Time (Ontological class associated to the Time schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @author <a> Rafael Gonzalez </a>
*/
public class Time implements Concept 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Timestamp primitive concept
	 */
	private double timestamp;
	 
	/**
	 * Getter of the property <tt>timestamp</tt>
	 *
	 * @return Returns the timestamp.
	 * 
	 */
	public double getTimestamp()
	{
		return timestamp;
	}
	
	/**
	 * Setter of the property <tt>timestamp</tt>
	 *
	 * @param timestamp The timestamp to set.
	 *
	 */
	public void setTimestamp(final double timestamp)
	{
		this.timestamp = timestamp;
	}

}

