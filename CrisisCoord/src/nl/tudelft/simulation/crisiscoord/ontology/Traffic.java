/*
 * @(#) Traffic.java September 25, 2008
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
* Traffic Intensity (Ontological class associated to the Traffic schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Traffic implements Concept 
{

	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Traffic intensity primitive concept
	 */
	private String intensity;
	 
	/**
	 * Getter of the property <tt>intensity</tt>
	 *
	 * @return Returns the intensity.
	 * 
	 */
	public String getIntensity()
	{
		return intensity;
	}
	
	/**
	 * Setter of the property <tt>intensity</tt>
	 *
	 * @param intensity The intensity to set.
	 *
	 */
	public void setIntensity(final String intensity)
	{
		this.intensity = intensity;
	}

}
