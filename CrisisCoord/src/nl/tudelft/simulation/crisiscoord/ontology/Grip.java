/*
 * @(#) Grip.java September 25, 2008
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
* Grip (Ontological class associated to the Grip schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Grip implements Concept 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * GRIP level concept
	 */
	private String level;
	 
	/**
	 * Getter of the property <tt>level</tt>
	 *
	 * @return Returns the level.
	 * 
	 */
	public String getLevel()
	{
		return level;
	}
	
	/**
	 * Setter of the property <tt>level</tt>
	 *
	 * @param level The level to set.
	 *
	 */
	public void setLevel(final String level)
	{
		this.level = level;
	}

}
