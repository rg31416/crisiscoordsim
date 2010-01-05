/*
 * @(#) AnimatedObject.java November 11, 2009
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
import javax.vecmath.Point3d;

import nl.tudelft.simulation.crisiscoord.CrisisCoordModel;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.animation.LocatableInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.animation.interpolation.InterpolationInterface;
import nl.tudelft.simulation.dsol.animation.interpolation.LinearInterpolation;
import nl.tudelft.simulation.dsol.formalisms.devs.SimEvent;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistNormal;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;

/**
 * Animated Object.
 * <p>
 * (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @author <a> Rafael Gonzalez </a>
 */
public class AnimatedObject implements LocatableInterface
{
		
		/** destination */
		protected DirectedPoint destination = new DirectedPoint();
		
		/** the URL of the image (icon) for the object */
		private URL image = null;
		
		/** the interpolator */
		protected InterpolationInterface interpolator = null;
		
		/** the location */
		private DirectedPoint location = new DirectedPoint();

		/** the model */
		protected CrisisCoordModel model;
		
		/** Movement distribution argument 1 */
		protected double mdarg1;
		
		/** Movement distribution argument 2 */
		protected double mdarg2;
		
		/** the name of the object */
		protected String name = "";

		/** the number of objects created */
		private static int number = 0;
		
		/** origin */
		protected DirectedPoint origin = new DirectedPoint();
		
		/** the simulator */
		protected DEVSSimulatorInterface simulator = null;
		
		/** object size */
		protected Point3d size;
		
		/** start time */
		protected double startTime = Double.NaN;

		/** stop time */
		protected double stopTime = Double.NaN;
		
		/** boolean value of whether object has reached latest destination */
		private boolean arrived = false;
		
		/** single image renderable */
		private SingleImageRenderable renderableImage;
		
		/** Default constructor */
		public AnimatedObject()
		{
			//Do nothing
		}
		
		/**
		 * Constructor.
		 * 
		 * @param image URL of the image that represents the responder
		 * @param mdarg1 movement distribution argument 1
		 * @param mdarg2 movement distribution argument 2
		 * @param origin point of origin of the responder
		 * @param simulator simulator interface reference		
		 * @param size size of the responder as a point
		 * @param model reference to the model on which the responder is deployed
		 * @throws RemoteException a remote exception is thrown if an error occurs
		 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs	
		 */
		public AnimatedObject(final URL image, final double mdarg1, final double mdarg2, 
				final DirectedPoint origin, final DEVSSimulatorInterface simulator, 
				final DirectedPoint size, final CrisisCoordModel model) 
				throws RemoteException, SimRuntimeException
		{
			this.image = image;
			this.origin = origin;
			this.location = origin;
			this.mdarg1 = mdarg1;
			this.mdarg2 = mdarg2;
			this.name = "AnimatedObject" + AnimatedObject.number;
			AnimatedObject.number++;
			this.simulator = simulator;
			this.size = size;
			this.renderableImage = new SingleImageRenderable(this, this.simulator, this.image);
			this.model = model;
		}
		
		/**
		 * Reset object counter for restarting naming
		 */
		public static void resetCounter () 
		{
			AnimatedObject.number = 0;
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
		 * Detect
		 * @return true if collision is detected, false if it is not
		 * @throws RemoteException a remote exception is thrown if an error occurs
		 */
		public boolean detect() throws RemoteException
		{
				boolean crash = true;
				double buffer = 1;
				
				/** Check for intersection with other AnimatedObjects */
				AnimatedObject [] animatedObjects = model.getAnimatedObjects();
				for (int i = 0; i < animatedObjects.length; i++)
				{
					if (!this.getName().equals(animatedObjects[i].getName()))
					{
						if (this.getCenter().x + this.getSize().x / 2 + buffer < animatedObjects[i].getCenter().x - animatedObjects[i].getSize().x / 2
					               || this.getCenter().x - this.getSize().x / 2 - buffer > animatedObjects[i].getCenter().x + animatedObjects[i].getSize().x / 2)
						{
					           crash = false;
						}
						else if (this.getCenter().y + this.getSize().y / 2 + buffer < animatedObjects[i].getCenter().y - animatedObjects[i].getSize().y / 2
					           || this.getCenter().y - this.getSize().y / 2 - buffer > animatedObjects[i].getCenter().y + animatedObjects[i].getSize().y / 2)
						{
							crash = false;
						}
						else
						{
							return true;
						}
				
					}
				}
				
				/** Check for intersection with StaticObjects */
				StaticObject [] staticObjects = model.getStaticObjects();
				for (int i = 0; i < staticObjects.length; i++)
				{
						if (this.getCenter().x + this.getSize().x / 2 + buffer < staticObjects[i].getCenter().x - staticObjects[i].getSize().x / 2
					               || this.getCenter().x - this.getSize().x / 2 - buffer > staticObjects[i].getCenter().x + staticObjects[i].getSize().x / 2)
						{
					           crash = false;
						}
						else if (this.getCenter().y + this.getSize().y / 2 + buffer < staticObjects[i].getCenter().y - staticObjects[i].getSize().y / 2
					           || this.getCenter().y - this.getSize().y / 2 - buffer > staticObjects[i].getCenter().y + staticObjects[i].getSize().y / 2)
						{
							crash = false;
						}
						else
						{
							return true;
						}
				}
				return crash;
		}
		
		/**
		 * Next move
		 * @throws RemoteException a remote exception is thrown if an error occurs
		 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs
		 */	
		public void next() throws RemoteException, SimRuntimeException
		{
			if (this.getCenter().distanceL1(this.getDestination()) > 2)
			{
				StreamInterface stream = this.simulator.getReplication().getStream("default");
				this.startTime = this.simulator.getSimulatorTime();
				this.stopTime = this.startTime + Math.abs(new DistNormal(stream, this.mdarg1, this.mdarg2).draw());
				this.interpolator = new LinearInterpolation(startTime, stopTime, origin, destination);
				this.simulator.scheduleEvent(new SimEvent(this.stopTime, this, this, "next", null));
			}
			else
			{
				setArrived(true);
			}
		}

		/** 
		 * Reverse movement
		 * 
		 * @throws RemoteException
		 *             a remote exception is thrown if an error occurs
		 */	
		public void reverse() throws RemoteException 
		{
			DirectedPoint temp = new DirectedPoint(0, 0, 0); 
			temp = this.destination;
			this.destination = this.origin;
			this.origin = temp;
		}
		
		/** Get Bounds implements from LocatableInterface
		 * @throws RemoteException a remote exception is thrown if an error occurs
		 * @return  bounds           
		 */	
		public Bounds getBounds() throws RemoteException 
		{
			DirectedPoint upper = new DirectedPoint(this.size.x, this.size.y, this.size.z);
			return new BoundingBox(new DirectedPoint(0, 0, 0), upper);
		}
		
		/** 
		 * Get current location
		 * 
		 * @throws RemoteException
		 *             a remote exception is thrown if an error occurs
		 * @return current location of animated object
		 */
		public DirectedPoint getLocation() throws RemoteException 
		{
			if (this.interpolator != null)
			{
				if (!detect())
				{
					this.location = this.interpolator.getLocation(this.simulator.getSimulatorTime());
				}
			}
			return this.location;
		}
		
		/** 
		 * Get center
		 * 
		 * @throws RemoteException
		 *             a remote exception is thrown if an error occurs
		 * @return center of animated object as directed point
		 */	
		public DirectedPoint getCenter() throws RemoteException 
		{
			return this.location;
		}
		
		/** 
		 * Get size
		 * 
		 * @return size of animated object as Point3d
		 */	
		public Point3d getSize()
		{
			return this.size;
		}
		
		/** 
		 * GEt model
		 * 
		 * @return reference to the model on which the object is deployed
		 */
		public CrisisCoordModel getModel()
		{
			return this.model;
		}

		/** 
		 * Get name
		 * 
		 * @return name
		 */
		public String getName()
		{
			return this.name;
		}
		
		/**
		 * Set destination
		 * @param destination the destination
		 */
		public void setDestination(final DirectedPoint destination)
		{
			this.destination = destination;
		}
		
		/**
		 * Set origin
		 * @param origin the origin
		 */
		public void setOrigin(final DirectedPoint origin)
		{
			this.origin = origin;
		}
		
		/**
		 * Set location
		 * @param location the location
		 */
		public void setLocation(final DirectedPoint location)
		{
			this.location = location;
		}
		
		/**
		 * Set size
		 * @param size the size
		 */
		public void setSize(final DirectedPoint size)
		{
			this.size = size;
		}
		
		/**
		 * Get destination
		 * @return destination
		 */
		public DirectedPoint getDestination()
		{
			return this.destination;
		}
		
		/** 
		 * Get origin
		 * @return origin of animated object as directed point
		 */	
		public DirectedPoint getOrigin() 
		{
			return this.origin;
		}
		
		/**
		 * Get arrived
		 * @return arrived
		 */
		public boolean getArrived()
		{
			return this.arrived;
		}
		
		/**
		 * Set arrived
		 * @param arrived arrived value
		 */
		public void setArrived(final boolean arrived)
		{
			this.arrived = arrived;
		}
		
	}
