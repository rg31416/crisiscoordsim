/*
 * @(#) Responder.java April 8, 2008
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

import nl.tudelft.simulation.crisiscoord.CrisisCoordModel;
import nl.tudelft.simulation.crisiscoord.visualization.AnimatedObject;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;

/**
 * Responder.
 * <p>
 * (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @author <a> Rafael Gonzalez </a>
 */
public class ResponderProxy extends AnimatedObject 
{

	/** the number of objects created */
	private static int number = 0;
	
	/** Default constructor */
	public ResponderProxy()
	{
		//Do nothing
	}
	
	/**
	 * Constructor.
	 * 
	 * @param image URL of the image that represents the responder
	 * @param speed double value of speed for responder movement
	 * @param speedStdDev double value for speed standard deviation
	 * @param origin point of origin of the responder
	 * @param simulator simulator interface reference		
	 * @param size size of the responder as a point
	 * @param model reference to the model on which the responder is deployed
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs	
	 */
	public ResponderProxy(final URL image, final double speed, final double speedStdDev, 
			final DirectedPoint origin, final DEVSSimulatorInterface simulator, 
			final DirectedPoint size, final CrisisCoordModel model) 
			throws RemoteException, SimRuntimeException
	{
		super(image, speed, speedStdDev, origin, simulator, size, model);
		this.destination = origin;
		this.name = "Responder" + ResponderProxy.number;
		ResponderProxy.number++;
	}
	
	/**
	 * Reset object counter for restarting naming
	 */
	public static void resetCounter () 
	{
		ResponderProxy.number = 0;
	}
	
	/**
	 * Overrides detect method from AnimatedObject
	 * @return true if crash and false if not
	 */
	public boolean detect()
	{
		return false;
	}
}
