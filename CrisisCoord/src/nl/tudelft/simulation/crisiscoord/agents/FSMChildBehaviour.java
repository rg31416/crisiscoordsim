/*
 * @(#) FSMChildBehaviour.java October 7, 2008
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

/**
* Fireman response agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class FSMChildBehaviour extends SimpleBehaviour 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;

	/**
	 * When this attribute becomes true, action method will not be executed
	 */
	protected boolean finished = false;
	
	/**
	 * This attribute holds the value returned by onEnd method
	 */
	protected int onEndReturnValue;
	
	/**
	 * Constructor
	 * @param a the agent
	 */
	public FSMChildBehaviour (final Agent a)
	{
		super(a);
	}
	
	/**
	 * Action (must be implemented for SimpleBehaviour
	 */
	public void action()
	{

	}

	/**
	 * Done (must be implemented for SimpleBehaviour to end)
	 * @return true if behaviour is finished, false otherwise
	 */
	public boolean done()
	{
		return finished;
	}
	
	/**
	 * On End
	 * @return value returned on end
	 */
	public int onEnd()
	{
		return onEndReturnValue;
	}
	
	/**
	 * Reset
	 */
	public void reset()
	{
		finished = false;
		onEndReturnValue = 0;
	}
}
