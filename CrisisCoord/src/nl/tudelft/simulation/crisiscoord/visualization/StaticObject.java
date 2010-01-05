/*
 * @(#) StaticObject.java November 11, 2009
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.visualization;

import java.net.URL;
import java.rmi.RemoteException;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.Bounds;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.animation.LocatableInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;

/**
 * Static Object.
 * <p>
 * (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @author <a> Rafael Gonzalez </a>
 */
public class StaticObject implements LocatableInterface 
{

	/** the URL of the image (icon) for the object */
	private URL image = null;;

	/** the name of the object */
	private String name = "";
	
	/** the number of objects created */
	private static int number = 0;
	
	/** origin */
	private DirectedPoint origin = new DirectedPoint();
	
	/** the simulator */
	private DEVSSimulatorInterface simulator = null;
	
	/** size */
	private DirectedPoint size = new DirectedPoint();
	
	/**Degree of burns in static object 0= none, 1= low, 2= high */
	protected int burnState = 0;
	
	/** single image renderable */
	private SingleImageRenderable renderableImage;

	/**
	 * Default Constructor
	 */
	public StaticObject() 
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
	public StaticObject(final URL image, final DirectedPoint origin, final DEVSSimulatorInterface simulator, final DirectedPoint size) 
		throws RemoteException, SimRuntimeException 
	{
		this.image = image;
		this.name = "StaticObject" + StaticObject.number;
		StaticObject.number++;
		this.origin = origin;
		this.simulator = simulator;
		this.size = size;
		this.renderableImage = new SingleImageRenderable(this, this.simulator, this.image);
	}

	/**
	 * Reset object counter for restarting naming
	 */
	public static void resetCounter () 
	{
		StaticObject.number = 0;
	}
	
	/**
	 * Set renderable image
	 * @param imageURL URL location of the image
	 */
	public void setRenderableImage(final URL imageURL) 
	{
		this.renderableImage = new SingleImageRenderable(this, this.simulator, imageURL);
	}
	
	/**
	 * Get renderable image
	 * @return renderableImage renderable image of the animated object
	 */
	public SingleImageRenderable getRenderableImage() 
	{
		return this.renderableImage;
	}
	
	/**
	 * Burn.
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
	
	/** 
	 * @see nl.tudelft.simulation.crisiscoord.visualization.StaticObject#getBounds()
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @return bound of the static object
	 */
	public Bounds getBounds() throws RemoteException 
	{
		DirectedPoint upper = new DirectedPoint(this.size.x, this.size.y, this.size.z);
		return new BoundingBox(new DirectedPoint(0, 0, 0), upper);
	}

	/**
	 * @see nl.tudelft.simulation.crisiscoord.visualization.StaticObject#getLocation()
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @return location of the static object
	 */
	public DirectedPoint getLocation() throws RemoteException 
	{
		return this.origin;
	}
	
	/**
	 * @see nl.tudelft.simulation.crisiscoord.visualization.StaticObject#setLocation()
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @param location of the static object
	 */
	public void setLocation(final DirectedPoint location) throws RemoteException 
	{
		this.origin = location;
	}
	
	/**
	 * Returns center of object
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @return center of static object
	 */
	public DirectedPoint getCenter() throws RemoteException 
	{
		return this.origin;
	}
	
	/**
	 * Returns size of object
	 * @return size of the static object
	 */	
	public DirectedPoint getSize()
	{
		return this.size;
	}
	
	/**
	 * Sets size of object
	 * @param x size of x dimension
	 * @param y size of x dimension
	 * @param z size of x dimension
	 */	
	public void setSize(final int x, final int y, final int z)
	{
		this.size.x = x;
		this.size.y = y;
		this.size.y = y;
	}
	
	/**
	 * Returns state of object
	 * @return state of the static object
	 */	
	public int getState()
	{
		return this.burnState;
	}

}
