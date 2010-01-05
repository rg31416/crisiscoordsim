/*
 * @(#) Awareness.java November 20, 2008
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.ontology;

import jade.content.Predicate;

/**
* Situation Awareness (Ontological class associated to the Awareness schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Awareness implements Predicate 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Resources concept
	 */
	private Resource resources;
	 
	/**
	 * Getter of the property <tt>resources</tt>
	 *
	 * @return Returns the resources.
	 * 
	 */
	public Resource getResources()
	{
		return resources;
	}
	
	/**
	 * Setter of the property <tt>resources</tt>
	 *
	 * @param resources The resource to set.
	 *
	 */
	public void setResources(final Resource resources)
	{
		this.resources = resources;
	}

	/**
	 * Weather concept
	 */
	private Weather weather;
	 
	/**
	 * Getter of the property <tt>weather</tt>
	 *
	 * @return Returns the weather.
	 * 
	 */
	public Weather getWeather()
	{
		return weather;
	}
	
	/**
	 * Setter of the property <tt>weather</tt>
	 *
	 * @param weather The weather to set.
	 *
	 */
	public void setWeather(final Weather weather)
	{
		this.weather = weather;
	}
	
	/**
	 * Traffic concept
	 */
	private Traffic traffic;
	 
	/**
	 * Getter of the property <tt>traffic</tt>
	 *
	 * @return Returns the traffic.
	 * 
	 */
	public Traffic getTraffic()
	{
		return traffic;
	}
	
	/**
	 * Setter of the property <tt>traffic</tt>
	 *
	 * @param traffic The traffic to set.
	 *
	 */
	public void setTraffic(final Traffic traffic)
	{
		this.traffic = traffic;
	}
	
	/**
	 * Responders concept
	 */
	private Responder responders;
	 
	/**
	 * Getter of the property <tt>responders</tt>
	 *
	 * @return Returns the responders.
	 * 
	 */
	public Responder getResponders()
	{
		return responders;
	}
	
	/**
	 * Setter of the property <tt>responders</tt>
	 *
	 * @param responders The responders to set.
	 *
	 */
	public void setResponders(final Responder responders)
	{
		this.responders = responders;
	}
	
	/**
	 * Point of view concept
	 */
	private Location pointofview;
	 
	/**
	 * Getter of the property <tt>pointofview</tt>
	 *
	 * @return Returns the pointofview.
	 * 
	 */
	public Location getPointOfView()
	{
		return pointofview;
	}
	
	/**
	 * Setter of the property <tt>pointofview</tt>
	 *
	 * @param pointofview The pointofview to set.
	 *
	 */
	public void setPointOfView(final Location pointofview)
	{
		this.pointofview = pointofview;
	}
	
	/**
	 * Elements concept
	 */
	private Element elements;
	 
	/**
	 * Getter of the property <tt>elements</tt>
	 *
	 * @return Returns the elements.
	 * 
	 */
	public Element getElements()
	{
		return elements;
	}
	
	/**
	 * Setter of the property <tt>elements</tt>
	 *
	 * @param elements The elements to set.
	 *
	 */
	public void setElements(final Element elements)
	{
		this.elements = elements;
	}
	
	/**
	 * Grip concept
	 */
	private Grip grip;
	 
	/**
	 * Getter of the property <tt>grip</tt>
	 *
	 * @return Returns the grip.
	 * 
	 */
	public Grip getGrip()
	{
		return grip;
	}
	
	/**
	 * Setter of the property <tt>grip</tt>
	 *
	 * @param grip The grip to set.
	 *
	 */
	public void setGrip(final Grip grip)
	{
		this.grip = grip;
	}
	
	/**
	 * Fire concept
	 */
	private Fire fire;
	 
	/**
	 * Getter of the property <tt>fire</tt>
	 *
	 * @return Returns the fire.
	 * 
	 */
	public Fire getFire()
	{
		return fire;
	}
	
	/**
	 * Setter of the property <tt>fire</tt>
	 *
	 * @param fire The fire to set.
	 *
	 */
	public void setFire(final Fire fire)
	{
		this.fire = fire;
	}
	
	/**
	 * Estimated Population concept
	 */
	private Population population;
	 
	/**
	 * Getter of the property <tt>population</tt>
	 *
	 * @return Returns the population.
	 * 
	 */
	public Population getPopulation()
	{
		return population;
	}
	
	/**
	 * Setter of the property <tt>population</tt>
	 *
	 * @param population The population to set.
	 *
	 */
	public void setPopulation(final Population population)
	{
		this.population = population;
	}
	
	/**
	 * Time concept
	 */
	private Time time;
	 
	/**
	 * Getter of the property <tt>time</tt>
	 *
	 * @return Returns the time.
	 * 
	 */
	public Time getTime()
	{
		return time;
	}
	
	/**
	 * Setter of the property <tt>time</tt>
	 *
	 * @param time The time to set.
	 *
	 */
	public void setTime(final Time time)
	{
		this.time = time;
	}
	
}