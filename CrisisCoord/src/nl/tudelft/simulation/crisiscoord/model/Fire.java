/*
 * @(#) Fire.java November 26, 2009
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
import nl.tudelft.simulation.dsol.formalisms.devs.SimEvent;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;


/**
 * Fire.
 * <p>
 * (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @version 1.5 <br>
 * @author <a> Rafael Gonzalez </a>
 */
public class Fire extends AnimatedObject
{
	
	/** the name of the object */
	private String name = "";

	/** the number of objects created */
	private static int number = 0;
	
	/** Growth rate of fire */
	private double growthRate = CrisisCoordModel.FIRE_GROWTH;
	
	/** Default constructor */
	public Fire() 
	{
		// Do nothing
	}
	
	/**
	 * Constructor.
	 * 
	 * @param image URL of the image that represents the fire
	 * @param origin point of origin of the fire
	 * @param simulator simulator interface reference		
	 * @param size size of the fire as a point
	 * @param model reference to the model on which the fire is deployed
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs	
	 */
	public Fire(final URL image, final DirectedPoint origin, final DEVSSimulatorInterface simulator, 
			final DirectedPoint size, final CrisisCoordModel model) 
			throws RemoteException, SimRuntimeException
	{
		super(image, CrisisCoordModel.FIRE_SPEED, CrisisCoordModel.FIRE_SPEED_STD_DEV, origin, simulator, size, model);
		this.name = "Fire" + Fire.number;
		Fire.number++;
		
		/** Schedule die down after 5 hours */
		try
		{
			simulator.scheduleEvent(new SimEvent(simulator.getSimulatorTime() + 300, this, this, "dieDown", null));
	    } catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Reset object counter for restarting naming
	 */
	public static void resetCounter () 
	{
		Fire.number = 0;
	}
	
	/**
	 * @see nl.tudelft.simulation.crisiscoord.visualization.AnimatedObject#detect()
	 * @return true if collision is detected, false if it is not
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 */
	public boolean detect() throws RemoteException
	{
		boolean crash = true;
		double buffer = 1;
				
		/** Check for intersection with civilians */
		for (int i = 0; i < model.getCivilians().length; i++)
		{
			if (this.getCenter().x + this.getSize().x / 2 + buffer < model.getCivilian(i).getCenter().x - model.getCivilian(i).getSize().x / 2
		               || this.getCenter().x - this.getSize().x / 2 - buffer > model.getCivilian(i).getCenter().x + model.getCivilian(i).getSize().x / 2)
			{
		           crash = false;
			}
			else if (this.getCenter().y + this.getSize().y / 2 + buffer < model.getCivilian(i).getCenter().y - model.getCivilian(i).getSize().y / 2
		           || this.getCenter().y - this.getSize().y / 2 - buffer > model.getCivilian(i).getCenter().y + model.getCivilian(i).getSize().y / 2)
			{
				crash = false;
			}
			else if (this.getCenter().z + this.getSize().z / 2 + buffer < model.getCivilian(i).getCenter().z - model.getCivilian(i).getSize().z / 2
		           || this.getCenter().z - this.getSize().z / 2 - buffer > model.getCivilian(i).getCenter().z + model.getCivilian(i).getSize().z / 2)
			{
				crash = false;
			}
			else
			{
		        model.getCivilian(i).burn();   
		        crash = true;
			}
		}
		
		/** Check for intersection with houses */
		for (int i = 0; i < model.getHouses().length; i++)
		{
			if (this.getCenter().x + this.getSize().x / 2 + buffer < model.getHouse(i).getCenter().x - model.getHouse(i).getSize().x / 2
	                || this.getCenter().x - this.getSize().x / 2 - buffer > model.getHouse(i).getCenter().x + model.getHouse(i).getSize().x / 2)
			{
	            crash = false;
			}
			else if (this.getCenter().y + this.getSize().y / 2 + buffer < model.getHouse(i).getCenter().y - model.getHouse(i).getSize().y / 2
	            || this.getCenter().y - this.getSize().y / 2 - buffer > model.getHouse(i).getCenter().y + model.getHouse(i).getSize().y / 2)
			{
				crash = false;
			}
			else if (this.getCenter().z + this.getSize().z / 2 + buffer < model.getHouse(i).getCenter().z - model.getHouse(i).getSize().z / 2
	            || this.getCenter().z - this.getSize().z / 2 - buffer > model.getHouse(i).getCenter().z + model.getHouse(i).getSize().z / 2)
			{
				crash = false;
			}
	        else
	        {
	        	model.getHouse(i).burn();
	        	crash = true;
	        }
		}
		
		/**Check for intersection with vehicles*/
		for (int i = 0; i < model.getVehicles().length; i++)
		{
			if (!this.name.equals(model.getVehicle(i).getName()))
			{
				if (this.getCenter().x + this.getSize().x / 2 + buffer < model.getVehicle(i).getCenter().x - model.getVehicle(i).getSize().x / 2
			                || this.getCenter().x - this.getSize().x / 2 - buffer > model.getVehicle(i).getCenter().x + model.getVehicle(i).getSize().x / 2)
				{
			            crash = false;
				}
				else if (this.getCenter().y + this.getSize().y / 2 + buffer < model.getVehicle(i).getCenter().y - model.getVehicle(i).getSize().y / 2
			            || this.getCenter().y - this.getSize().y / 2 - buffer > model.getVehicle(i).getCenter().y + model.getVehicle(i).getSize().y / 2)
				{
						crash = false;
				}
				else if (this.getCenter().z + this.getSize().z / 2 + buffer < model.getVehicle(i).getCenter().z - model.getVehicle(i).getSize().z / 2
			            || this.getCenter().z - this.getSize().z / 2 - buffer > model.getVehicle(i).getCenter().z + model.getVehicle(i).getSize().z / 2)
				{
						crash = false;
				}
				else
				{
					model.getVehicle(i).burn();
			        crash = true;
			    }
			}
		}
		return crash;
	}
	
	/**
	 * Growth in the size of the fire (overrides next method from AnimatedObject
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs
	 */	
	public void next() throws RemoteException, SimRuntimeException
	{
		if (this.size.x > 0)
		{
			StreamInterface stream = this.simulator.getReplication().getStream("default");
			this.startTime = this.simulator.getSimulatorTime();
			this.stopTime = this.startTime + Math.abs(new DistNormal(stream, this.mdarg1, this.mdarg2).draw());
			
			/** growth is limited by a maximum fire size */
			if (this.size.x < CrisisCoordModel.MAX_FIRE_SIZE.x && this.size.y < CrisisCoordModel.MAX_FIRE_SIZE.y)
			{
				this.size.scale(this.growthRate);
			}
			
			/** check whether fire now has touched other objects */
			this.detect();
			this.simulator.scheduleEvent(new SimEvent(this.stopTime, this, this, "next", null));
		}
	}
	
	/**
	 * Fight the fire
	 * @param intensity an integer indicating intensity impact on size (0-100)
	 */	
	public void fight(final double intensity)
	{
		double scale = 1 - (intensity / 100);
		if (this.size.x >= 1)
		{
			this.size.scale(scale);
		} else if (this.size.x < 1 && this.size.x > 0) /** If this threshold is surpassed, the fire dies completely */
		{
			this.size = new DirectedPoint(0, 0, 0);
			this.mdarg1 = 0;
			this.mdarg2 = 0;
		}
	}
	
	/**
	 * Fire dies down
	 */	
	public void dieDown()
	{
		this.growthRate = 0.8;
		this.size.scale(this.growthRate);
	}
	
}
