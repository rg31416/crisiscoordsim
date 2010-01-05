/*
 * @(#) Population.java November 20, 2008
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
* Population (Ontological class associated to the Population schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @author <a> Rafael Gonzalez </a>
*/
public class Population implements Concept
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Population estimated number of civilians concept
	 */
	private int civilians;
	 
	/**
	 * Getter of the property <tt>civilians</tt>
	 *
	 * @return Returns the civilians.
	 * 
	 */
	public int getCivilians()
	{
		return civilians;
	}
	
	/**
	 * Setter of the property <tt>civilians</tt>
	 *
	 * @param civilians The estimated number of civilians to set.
	 *
	 */
	public void setCivilians(final int civilians)
	{
		this.civilians = civilians;
	}
	
	/**
	 * Population estimated number of victims concept
	 */
	private int victims;
	 
	/**
	 * Getter of the property <tt>victims</tt>
	 *
	 * @return Returns the victims.
	 * 
	 */
	public int getVictims()
	{
		return victims;
	}
	
	/**
	 * Setter of the property <tt>victims</tt>
	 *
	 * @param victims The estimated number of victims to set.
	 *
	 */
	public void setVictims(final int victims)
	{
		this.victims = victims;
	}
}
