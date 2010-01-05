/*
 * @(#) Vehicle.java November 26, 2009
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
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.language.d3.DirectedPoint;

/**
 * Vehicle.
 * <p>
 * (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @version 1.5 <br>
 * @author <a> Rafael Gonzalez </a>
 */

public class Vehicle extends AnimatedObject
{
	
	/** the name of the object */
	private String name = "";

	/** the number of objects created */
	private static int number = 0;
	
	/**Degree of burns in civilian 0= none, 1= low, 2= high */
	private int burnState = 0;
	
	/** Crash status */
	private boolean crash = false;
	
	/** Default constructor */
	public Vehicle()
	{
		//Do nothing	
	}
	
	/**
	 * Constructor.
	 * 
	 * @param image URL of the image that represents the vehicle
	 * @param origin point of origin of the vehicle
	 * @param simulator simulator interface reference		
	 * @param size size of the vehicle as a point
	 * @param model reference to the model on which the vehicle is deployed
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs	
	 */
	public Vehicle(final URL image, final DirectedPoint origin, final DEVSSimulatorInterface simulator, 
			final DirectedPoint size, final CrisisCoordModel model) 
			throws RemoteException, SimRuntimeException
	{
		super(image, CrisisCoordModel.VEHICLE_SPEED, CrisisCoordModel.VEHICLE_SPEED_STD_DEV, origin, simulator, size, model);
		this.name = "Vehicle" + Vehicle.number;
		Vehicle.number++;
		DirectedPoint destination = new DirectedPoint(0, 0, 0);
		this.setDestination (destination);
	}
	
	/**
	 * Reset object counter for restarting naming
	 */
	public static void resetCounter () 
	{
		Vehicle.number = 0;
	}
	
	/**
	 * Burn.
	 * 
	 */	
	public void burn()
	{
		burnState++;		
	}
	
	/**
	 * Get Burn state
	 * @return burn state
	 */
	public int getState()
	{
		return burnState;
	}
	
	/**
	 * Set Crash state
	 * @param crash state
	 */
	public void setCrash(final boolean crash)
	{
		this.crash = crash;
	}
	
	/**
	 * Set Burn state
	 * @param burn state
	 */
	public void setBurn(final int burn)
	{
		this.burnState = burn;
	}
	
	/**
	 * Overrides detect method from AnimatedObject
	 * @return true if crash and false if not
	 */
	public boolean detect()
	{
		if (!crash)
		{
			try
			{
				crash = super.detect();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
			if (crash && this.name.equals("Vehicle0") && burnState < 1)
			{
				try
				{
					this.model.setFire(this.getCenter());
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				this.burn();
			}
			return crash;
		}
		return true;
	}
	
}
