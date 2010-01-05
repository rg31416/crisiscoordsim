/*
 * @(#) Fire.java September 15, 2008
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
* Fire (Ontological class associated to the Fire schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Fire implements Concept 
{

	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Fire location concept
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
	 * Fire expected evolution primitive concept
	 */
	private String evolution;
	 
	/**
	 * Getter of the property <tt>evolution</tt>
	 *
	 * @return Returns the evolution.
	 * 
	 */
	public String getEvolution()
	{
		return evolution;
	}
	
	/**
	 * Setter of the property <tt>evolution</tt>
	 *
	 * @param evolution The evolution to set.
	 *
	 */
	public void setEvolution(final String evolution)
	{
		this.evolution = evolution;
	}
	
	/**
	 * Fire scope primitive concept
	 */
	private String scope;
	 
	/**
	 * Getter of the property <tt>scope</tt>
	 *
	 * @return Returns the scope.
	 * 
	 */
	public String getScope()
	{
		return scope;
	}
	
	/**
	 * Setter of the property <tt>scope</tt>
	 *
	 * @param scope The scope to set.
	 *
	 */
	public void setScope(final String scope)
	{
		this.scope = scope;
	}
	
	/**
	 * Fire nature primitive concept
	 */
	private String nature;
	 
	/**
	 * Getter of the property <tt>nature</tt>
	 *
	 * @return Returns the nature.
	 * 
	 */
	public String getNature()
	{
		return nature;
	}
	
	/**
	 * Setter of the property <tt>nature</tt>
	 *
	 * @param nature The nature to set.
	 *
	 */
	public void setNature(final String nature)
	{
		this.nature = nature;
	}

}
