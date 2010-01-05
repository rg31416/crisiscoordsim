/*
 * @(#) Element.java September 25, 2008
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
* Infrastructure Element (Ontological class associated to the Element schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Element implements Concept 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Element condition concept
	 */
	private String condition;
	 
	/**
	 * Getter of the property <tt>condition</tt>
	 *
	 * @return Returns the condition.
	 * 
	 */
	public String getCondition()
	{
		return condition;
	}
	
	/**
	 * Setter of the property <tt>condition</tt>
	 *
	 * @param condition The condition to set.
	 *
	 */
	public void setCondition(final String condition)
	{
		this.condition = condition;
	}
	
	/**
	 * Element accessibility concept
	 */
	private String access;
	 
	/**
	 * Getter of the property <tt>access</tt>
	 *
	 * @return Returns the access.
	 * 
	 */
	public String getAccess()
	{
		return access;
	}
	
	/**
	 * Setter of the property <tt>access</tt>
	 *
	 * @param access The access to set.
	 *
	 */
	public void setAccess(final String access)
	{
		this.access = access;
	}
	
	/**
	 * Element extension concept
	 */
	private String extension;
	 
	/**
	 * Getter of the property <tt>extension</tt>
	 *
	 * @return Returns the extension.
	 * 
	 */
	public String getExtension()
	{
		return extension;
	}
	
	/**
	 * Setter of the property <tt>extension</tt>
	 *
	 * @param extension The extension to set.
	 *
	 */
	public void setExtension(final String extension)
	{
		this.extension = extension;
	}
	
	/**
	 * Element location concept
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
