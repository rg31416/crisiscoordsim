/*
 * @(#) House.java December 12, 2008
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.model;

import java.net.URL;
import java.rmi.RemoteException;

import nl.tudelft.simulation.crisiscoord.visualization.StaticObject;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;

/**
 * House.
 * <p>
 * (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @version 1.5 <br>
 * @author <a> Rafael Gonzalez </a>
 */
public class House extends StaticObject 
{

	/** the name of the house */
	private String name = "";
	
	/** the number of houses created */
	private static int number = 0;
	
	/**
	 * Default Constructor
	 */
	public House() 
	{
		// Do nothing
	}

	/**
	 * Constructor.
	 * 
	 * @param image image
	 * @param origin origin
	 * @param simulator simulator
	 * @param size size
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs	
	 */
	public House(final URL image, final DirectedPoint origin, final DEVSSimulatorInterface simulator, final DirectedPoint size) 
		throws RemoteException, SimRuntimeException 
	{
		super(image, origin, simulator, size);
		this.name = "House" + House.number;
		House.number++;
	}
	
	/**
	 * Reset object counter for restarting naming
	 */
	public static void resetCounter () 
	{
		House.number = 0;
	}
	
	/**
	 * Overrides Burn.
	 * 
	 */	
	public void burn()
	{
		if (burnState < 1)
		{
			burnState++;		
			System.out.println(this.name + " burning");
		}	
	}
}
