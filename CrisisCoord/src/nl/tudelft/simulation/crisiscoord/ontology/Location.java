/*
 * @(#) Location.java September 25, 2008
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
* Location (Ontological class associated to the Location schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Location implements Concept 
{

	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Location size primitive concept
	 */
	private String size;
	 
	/**
	 * Getter of the property <tt>size</tt>
	 *
	 * @return Returns the size.
	 * 
	 */
	public String getSize()
	{
		return size;
	}
	
	/**
	 * Setter of the property <tt>size</tt>
	 *
	 * @param size The size to set.
	 *
	 */
	public void setSize(final String size)
	{
		this.size = size;
	}
	
	/**
	 * Location position primitive concept
	 */
	private String position;
	 
	/**
	 * Getter of the property <tt>position</tt>
	 *
	 * @return Returns the position.
	 * 
	 */
	public String getPosition()
	{
		return position;
	}
	
	/**
	 * Setter of the property <tt>position</tt>
	 *
	 * @param position The position to set.
	 *
	 */
	public void setPosition(final String position)
	{
		this.position = position;
	}
	
	/**
	 * Location type primitive concept
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
