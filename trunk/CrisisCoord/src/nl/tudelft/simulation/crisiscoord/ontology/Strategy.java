/*
 * @(#) Strategy.java September 25, 2008
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
* Strategy (Ontological class associated to the Strategy schema)
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Strategy implements Concept 
{

	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/**
	 * Strategy protocol concept
	 */
	private String protocol;
	 
	/**
	 * Getter of the property <tt>protocol</tt>
	 *
	 * @return Returns the protocol.
	 * 
	 */
	public String getProtocol()
	{
		return protocol;
	}
	
	/**
	 * Setter of the property <tt>protocol</tt>
	 *
	 * @param protocol The protocol to set.
	 *
	 */
	public void setProtocol(final String protocol)
	{
		this.protocol = protocol;
	}

	/**
	 * Admission criteria primitive concept
	 */
	private String admission;
	 
	/**
	 * Getter of the property <tt>admission</tt>
	 *
	 * @return Returns the admission.
	 * 
	 */
	public String getAdmission()
	{
		return admission;
	}
	
	/**
	 * Setter of the property <tt>admission</tt>
	 *
	 * @param admission The admission to set.
	 *
	 */
	public void setAdmission(final String admission)
	{
		this.admission = admission;
	}

	/**
	 * Screening degree primitive concept
	 */
	private String screening;
	 
	/**
	 * Getter of the property <tt>screening</tt>
	 *
	 * @return Returns the screening.
	 * 
	 */
	public String getScreening()
	{
		return screening;
	}
	
	/**
	 * Setter of the property <tt>screening</tt>
	 *
	 * @param screening The screening to set.
	 *
	 */
	public void setScreening(final String screening)
	{
		this.screening = screening;
	}

	/**
	 * Blockade closure primitive concept
	 */
	private String closure;
	 
	/**
	 * Getter of the property <tt>closure</tt>
	 *
	 * @return Returns the closure.
	 * 
	 */
	public String getClosure()
	{
		return closure;
	}
	
	/**
	 * Setter of the property <tt>closure</tt>
	 *
	 * @param closure The closure to set.
	 *
	 */
	public void setClosure(final String closure)
	{
		this.closure = closure;
	}

	/**
	 * priorities primitive concept
	 */
	private String priorities;
	 
	/**
	 * Getter of the property <tt>priorities</tt>
	 *
	 * @return Returns the priorities.
	 * 
	 */
	public String getPriorities()
	{
		return priorities;
	}
	
	/**
	 * Setter of the property <tt>priorities</tt>
	 *
	 * @param priorities The priorities to set.
	 *
	 */
	public void setPriorities(final String priorities)
	{
		this.priorities = priorities;
	}

	/**
	 * measures primitive concept
	 */
	private String measures;
	 
	/**
	 * Getter of the property <tt>measures</tt>
	 *
	 * @return Returns the measures.
	 * 
	 */
	public String getMeasures()
	{
		return measures;
	}
	
	/**
	 * Setter of the property <tt>measures</tt>
	 *
	 * @param measures The measures to set.
	 *
	 */
	public void setMeasures(final String measures)
	{
		this.measures = measures;
	}


	/** safety rules primitive concept */
	private String rules;
	 
	/**
	 * Getter of the property <tt>rules</tt>
	 *
	 * @return Returns the rules.
	 * 
	 */
	public String getRules()
	{
		return rules;
	}
	
	/**
	 * Setter of the property <tt>rules</tt>
	 *
	 * @param rules The rules to set.
	 *
	 */
	public void setRules(final String rules)
	{
		this.rules = rules;
	}
	
	/**
	 * Strategy exit criteria concept
	 */
	private String exit;
	 
	/**
	 * Getter of the property <tt>exit</tt>
	 *
	 * @return Returns the exit.
	 * 
	 */
	public String getExit()
	{
		return exit;
	}

	/**
	 * Setter of the property <tt>exit</tt>
	 *
	 * @param exit The exit to set.
	 *
	 */
	public void setExit(final String exit)
	{
		this.exit = exit;
	}
}
