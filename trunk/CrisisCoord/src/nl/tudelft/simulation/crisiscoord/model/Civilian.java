/*
 * @(#) Civilian.java November 25, 2009
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
import nl.tudelft.simulation.dsol.animation.interpolation.LinearInterpolation;
import nl.tudelft.simulation.dsol.formalisms.devs.SimEvent;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.language.io.URLResource;
import nl.tudelft.simulation.naming.context.ContextUtil;

/**
 * Civilian.
 * <p>
 * (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @author <a> Rafael Gonzalez </a>
 */
public class Civilian extends AnimatedObject 
{
	/** the name of the civilian */
	private String name = "";

	/** the number of civilians created */
	private static int number = 0;
	
	/**Degree of burns in civilian 0= none, 1= low, 2= high, 3 = dead */
	private int burnState = 0;
	
	/** selected attribute as shared data space for responders */
	private boolean selected = false;
	
	/** assisted boolean value to prevent death after assistance */
	private boolean assisted = false;
	
	/**  URL of victim on fire icon */
	public static final URL VICTIM_FIRE_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/victimfire.PNG");

	/**  URL of fatality icon */
	public static final URL FATALITY_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/fatality.PNG");

	/** Default constructor */
	public Civilian()
	{
		//Do nothing
	}
	
	/**
	 * Constructor.
	 * 
	 * @param image URL of the image that represents the civilian
	 * @param origin point of origin of the civilian
	 * @param simulator simulator interface reference		
	 * @param size size of the civilian as a point
	 * @param model reference to the model on which the civilian is deployed
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs	
	 */
	public Civilian(final URL image, final DirectedPoint origin, final DEVSSimulatorInterface simulator, 
			final DirectedPoint size, final CrisisCoordModel model) 
			throws RemoteException, SimRuntimeException
	{
		super(image, CrisisCoordModel.CIVILIAN_SPEED, CrisisCoordModel.CIVILIAN_SPEED_STD_DEV, origin, simulator, size, model);
		this.name = "Civilian" + Civilian.number;
		Civilian.number++;
		StreamInterface stream = simulator.getReplication().getStream("default");
		DirectedPoint destination = new DirectedPoint(-CrisisCoordModel.CANVAS_SIZE.x + stream.nextInt(0, (int) CrisisCoordModel.CANVAS_SIZE.y * 2), 
				-CrisisCoordModel.CANVAS_SIZE.x + stream.nextInt(0, (int) CrisisCoordModel.CANVAS_SIZE.y * 2), 0);
		this.setDestination (destination);
		this.setArrived(true);
	}
	
	/**
	 * Reset object counter for restarting naming
	 */
	public static void resetCounter () 
	{
		Civilian.number = 0;
	}
	
	/**
	 * Burn
	 */	
	public void burn ()
	{
		if (burnState < 1 && !assisted)
		{
			CrisisCoordModel.countVictim();
			burnState++;
			ContextUtil.unbindFromContext(this.getRenderableImage());
			this.setRenderableImage(VICTIM_FIRE_ICON);
			/** schedule death if not assisted after 60 minutes */
			try
			{
				this.simulator.scheduleEvent(new SimEvent(this.simulator.getSimulatorTime() + 60, this, this, "die", null));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		else if (burnState < 2 && !assisted)
		{
			burnState++;
		}
		else if (burnState < 3 && !assisted)
		{
			die();
		}
	}
	
	/**
	 * Die
	 */	
	public void die ()
	{
		if (burnState != 3 && !assisted)
		{
			burnState = 3; //amounts to death
			ContextUtil.unbindFromContext(this.getRenderableImage());
			this.setRenderableImage(FATALITY_ICON);
			CrisisCoordModel.countFatality();
		}
		
	}
	/**
	 * Assist
	 */	
	public void assist ()
	{
		burnState = 0;
		assisted = true;
		System.out.println(name + " Assisted");
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
	 * @overrides next method from AnimatedObject to provide random movement
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs
	 */	
	public void next() throws RemoteException, SimRuntimeException
	{
		if (!getArrived())
		{
			if (this.getCenter().distanceL1(this.getDestination()) > 1)
			{
				StreamInterface stream = this.simulator.getReplication().getStream("default");
				this.startTime = this.simulator.getSimulatorTime();
				this.stopTime = this.startTime + Math.abs(new DistNormal(stream, this.mdarg1, this.mdarg2).draw());
				this.interpolator = new LinearInterpolation(startTime, stopTime, origin, destination);
				this.simulator.scheduleEvent(new SimEvent(this.stopTime, this, this, "next", null));
			}
			else
			{
				reverse();
				this.next();
			}
		}
		
	} // end of next method
	
	/**
	 * Get selected 
	 * @return selected value
	 */
	public boolean getSelected()
	{
		return selected;
	}
	
	/**
	 * Set selected 
	 * @param selected value
	 */
	public void setSelected(final boolean selected)
	{
		this.selected = selected;
	}
	

}
