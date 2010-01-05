/*
 * @(#) CrisisCoordModel.java November 29, 2009
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.PlatformController;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import nl.tudelft.simulation.crisiscoord.agents.ServiceAgent;
import nl.tudelft.simulation.crisiscoord.model.Civilian;
import nl.tudelft.simulation.crisiscoord.model.Fire;
import nl.tudelft.simulation.crisiscoord.model.House;
import nl.tudelft.simulation.crisiscoord.model.ResponderProxy;
import nl.tudelft.simulation.crisiscoord.model.Vehicle;
import nl.tudelft.simulation.crisiscoord.visualization.AnimatedObject;
import nl.tudelft.simulation.crisiscoord.visualization.StaticObject;
import nl.tudelft.simulation.dsol.ModelInterface;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.devs.SimEvent;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import javax.vecmath.Point3d;
import nl.tudelft.simulation.language.io.URLResource;
import nl.tudelft.simulation.naming.context.ContextUtil;


/**
 * CrisisCoord Model.
 * <p>
 * (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @author <a> Rafael Gonzalez </a>
 */
public class CrisisCoordModel extends ServiceAgent implements ModelInterface 
{

	/** seriealVersionUID to prevent warning */
	static final long serialVersionUID = 0;
	
	/** Name of experimental log file */
	private String fileName = null;
	
	/** Main profile of JADE platform */
	private Profile profileMain;
	
	/** JADE agent controller */
	private AgentController rma;
	
	/** Main JADE container controller */
	private ContainerController mainController;
	
	/** JADE platform controller */
	private PlatformController platformController;
	
	/** Reference to simulator interface */
	private DEVSSimulatorInterface simulator;
	
	/** Array of civilians to be deployed in the model */
	private Civilian[] civilians;
	
	/**Fire Station depicted in the model */
	@SuppressWarnings("unused")
	private House fireStation;
	
	/**Hospital depicted in the model */
	@SuppressWarnings("unused")
	private House hospital;
	
	/**Array of houses to be deployed in the model */
	private House[] houses;
	
	/** Collection point object, to visualize location of victim collection */
	private StaticObject collectionPoint;
	
	/** CoPI visualization */
	private StaticObject copi;
	
	/**Array of vehicles to be deployed in the model */
	protected Vehicle[] vehicles;
	
	/** Array of firemen to be deployed in the model */
	private ResponderProxy[] firemen;
	
	/** Array of medics to be deployed in the model */
	private ResponderProxy[] medics;
	
	/** Agent ID of dispatcher agent */
	private AID dispatcherAID = new AID();
	
	/** Informed fire */
	private boolean informedFire = false;
	
	/** Fire */
	private Fire fire;
	
	/** Object size constants */
	/** Canvas size (real canvas size is actually twice this amount, because it is set for 4 cuadrants and this considers only one */
	public static final DirectedPoint CANVAS_SIZE = new DirectedPoint(35, 50, 0);
	/** Vehicle size */
	public static final DirectedPoint VEHICLE_SIZE = new DirectedPoint(10, 10, 0);
	/** Civilian size */
	public static final DirectedPoint CIVILIAN_SIZE = new DirectedPoint(5, 5, 0);
	/** Responder size */
	public static final DirectedPoint RESPONDER_SIZE = new DirectedPoint(5, 5, 0);
	/** House size */
	public static final DirectedPoint HOUSE_SIZE = new DirectedPoint(15, 15, 0);
	/** Fire station size */
	public static final DirectedPoint STATION_SIZE = new DirectedPoint(20, 17, 0);
	/** Hospital size */
	public static final DirectedPoint HOSPITAL_SIZE = new DirectedPoint(17, 17, 0);
	/** Water jet from hose size */
	public static final DirectedPoint WATER_SIZE = new DirectedPoint(8, 2, 0);
	/** Number of houses */
	public static final int NUM_HOUSES = 3;
	/** Number of vehicles */
	public static final int NUM_VEHICLES = 12;
	
	/** -- Initial experimental properties (defined in experiment crisiscoord.xml) -- */
	/** Number of civilians */
	private int numCivilians;
	/** Number of firemen */
	private int numFiremen;
	/** Number of backup firemen */
	private int numBackupFiremen;
	/** Number of medics */
	private int numMedics;
	/** Number of backup medics */
	private int numBackupMedics;
	/** Number of OvDs */
	private int numOvD;
	/** Number of OvD-Gs */
	private int numOvDG;
	/** Timeout for waiting for responses */
	private int timeout;
	/** Cycle time for agents */
	private int cycleT;
	/** Auto assignment boolean property */
	private String auto = new String();
	/** Assist mutual adjustment boolean property */
	private String mutualAdjustment = new String();
	
	/** Vehicle position */
	public static final int VEHICLE_POS = 30;
	/** Truck position */
	public static final DirectedPoint TRUCK_POS = new DirectedPoint(10, 0, 0);
	/** Crane position */
	public static final DirectedPoint CRANE_POS = new DirectedPoint(-10, 0, 0);
	/** Sarting House position */
	public static final DirectedPoint HOUSE_POS = new DirectedPoint(15, 15, 0);
	/** Fire station position */
	public static final DirectedPoint STATION_POS = new DirectedPoint(-80, 80, 0);
	/** Hospital position */
	public static final DirectedPoint HOSPITAL_POS = new DirectedPoint(10, 80, 0);
	/** Safety distance from fire for firemen */
	public static final int SAFETY_DISTANCE = 4;
	/** Safety distance from fire for gathering after alarm */
	public static final int ALARM_DISTANCE = 20;
	/** Maximum fire size */
	public static final DirectedPoint MAX_FIRE_SIZE = new DirectedPoint(35, 35, 0);
	
	/** -- Speed constants (HIGHER IS SLOWER, more time between interpolation steps) -- */
	/** Civilian speed */
	public static final int CIVILIAN_SPEED = 25;
	/** Civilian speed standard deviation */
	public static final int CIVILIAN_SPEED_STD_DEV = 2;
	/** Fireman speed */
	public static final int FIREMAN_SPEED = 10;
	/** Fireman speed standard deviation */
	public static final int FIREMAN_SPEED_STD_DEV = 1;
	/** Medic speed */
	public static final int MEDIC_SPEED = 15;
	/** Medic speed standard deviation */
	public static final int MEDIC_SPEED_STD_DEV = 1;
	/** Vehicle speed */
	public static final int VEHICLE_SPEED = 7;
	/** Vehicle speed standard deviation */
	public static final int VEHICLE_SPEED_STD_DEV = 4;
	/** Fire growth speed */
	public static final int FIRE_SPEED = 4;
	/** Fire growth speed standard deviation */
	public static final int FIRE_SPEED_STD_DEV = 1;
	/** Fire growth parameter (percentage in each time step, where 1 is 100%) */
	public static final double FIRE_GROWTH = 1.2;
	/** Fire fighting parameter (percentage in each time step) */
	public static final double FIRE_FIGHT = 12;
	/** Time from which truck catches fire to when it explodes */
	public static final double EXPLOSION_TIME = 15;
	
	/** Sleep constants */
	/** Sleep waiting time for responders */
	public static final long RESPONDER_SLEEP = 1000;
	/** Sleep waiting time for officers */
	public static final long OFFICER_SLEEP = 3000;

	/** -- Icon locations */
	/**  URL of truck icon */
	public static final URL TRUCK_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/truck.PNG");
	/**  URL of car1 icon */
	public static final URL CAR_ICON1 = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/car1.PNG");
	/**  URL of car2 icon */
	public static final URL CAR_ICON2 = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/car2.PNG");
	/**  URL of car3 icon */
	public static final URL CAR_ICON3 = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/car3.PNG");
	/**  URL of car4 icon */
	public static final URL CAR_ICON4 = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/car4.PNG");
	/**  URL of house icon */
	public static final URL HOUSE_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/house1.PNG");
	/**  URL of crane icon */
	public static final URL CRANE_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/excavator.PNG");
	/**  URL of civlian1 icon */
	public static final URL CIVILIAN_ICON1 = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/civilian1.PNG");
	/**  URL of civilian2 icon */
	public static final URL CIVILIAN_ICON2 = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/civilian2.PNG");
	/**  URL of civilian3 icon */
	public static final URL CIVILIAN_ICON3 = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/civilian3.PNG");
	/**  URL of OvD icon */
	public static final URL OVD_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/ovd.PNG");
	/**  URL of OvD-G icon */
	public static final URL OVDG_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/ovdg.PNG");
	/**  URL of fireman icon */
	public static final URL FIREMAN_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/fireman.PNG");
	/**  URL of medic icon */
	public static final URL MEDIC_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/medic.PNG");
	/**  URL of fire icon */
	public static final URL FIRE_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/fire.PNG");
	/**  URL of fire station icon */
	public static final URL FSTATION_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/firestation.PNG");
	/** URL of red cross icon for victim collection point */
	public static final URL REDCROSS_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/red-cross.PNG");
	/** URL of water icon for illustrating fire fighting */
	public static final URL WATER_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/water.PNG");
	/** URL of hospital icon */
	public static final URL HOSPITAL_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/hospital.PNG");
	/** URL of CoPI icon */
	public static final URL COPI_ICON = URLResource.getResource("/nl/tudelft/simulation/crisiscoord/images/copi.PNG");
	
	/** Reference to the DSOL model */
	protected CrisisCoordModel modelref;
	
	/** Static counter for recording msgs exchanged by agents */
	private static int messageCounter = 0;
	/** Static counter for recording number of victims */
	private static int victimCounter = 0;
	/** Static counter for recording number of assisted victims */
	private static int fatalityCounter = 0;
	
	/** termintaed replication boolean value */
	private boolean terminated = false;
	
	/**
	 * Setup of the agent
	 */
	protected void setup()
	{
		/** Attach model reference */
		Object [] args = getArguments();
		this.modelref = (CrisisCoordModel) args[0];
		
		/** Define and register the service provided by this agent*/
		ServiceDescription serviceDescription  = new ServiceDescription();
		serviceDescription.setType("ModelService");
		serviceDescription.setName(getLocalName());
        this.registerInDF(serviceDescription);
        
	    /** Determine service description of dispatcher */
        setDispatcherAID("DispatcherService");
        
        addBehaviour(new CallDispatchBehaviour(this));
        
	} // End of setup method

	/**
	 * Take down agent method
	 */
	protected void takeDown() 
	{
		doDelete();
	} // End of takeDown method
	
	/** Default constructor */
	public CrisisCoordModel() 
	{
		// Do nothing
	} // End of CrisisCoordModel method
	
	/**
	 * @see nl.tudelft.simulation.dsol.ModelInterface#constructModel(nl.tudelft.simulation.dsol.simulators.SimulatorInterface)
	 * @param simulator reference to simulator interface
	 * @throws RemoteException a remote exception is thrown if an error occurs
	 * @throws SimRuntimeException a simulation runtime exception is thrown if an error occurs	
	 */
	public void constructModel(final SimulatorInterface simulator)
			throws SimRuntimeException, RemoteException 
	{
		/** Reset objects for between experiments */
		resetObjects();
		
		/** Assign simulator from method parameter */
		this.simulator = (DEVSSimulatorInterface) simulator;
		
		/** set experimental properties */
		setExperimentalProperties();
		
		/** Create log file with headers */
		createLogFile();
		
		/** Instantiate objects (vehicles, houses, stations) in model */
		instantiateObjects();

		/** Instantiate civilian objects in model */
		instantiateCivilians();
		
		/** Instantiate responder objects in model */
		instantiateResponders();
		
		/** Launching agents */
	    launchAgents();
	    
		/** Initiate civilians movement */
		for (int i = 0; i < numCivilians; i++)
		{
			civilians[i].next();
		}
		
	    /** Instantiate fire object */
		try
		{
				fire = new Fire (FIRE_ICON, new DirectedPoint(0, 0, 0), (DEVSSimulatorInterface) simulator, new DirectedPoint(0, 0, 0), this);
	    } catch (Exception e) 
		{
			e.printStackTrace();
		}
	    
		/** Initiate vehicle movement */
	    try
		{
	    	this.simulator.scheduleEvent(new SimEvent(this.simulator.getSimulatorTime() + 10, this, this, "startVehicles", null));
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		/** schedule terminate event to force end replication if crisis not over at event time */
		try
		{
	    	this.simulator.scheduleEvent(new SimEvent(this.simulator.getSimulatorTime() + 400, this, this, "terminateReplication", null));
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	} // End of constructModel method

	/** 
	 * Initiate vehicle movement
	 */
	public void startVehicles()
	{
		for (int i = 0; i < NUM_VEHICLES; i++)
		{
			try
			{
				vehicles[i].next();
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	} // End of startVehicles() method
	
	/** 
	 * Instantiate model objects
	 */
	public void instantiateObjects()
	{
		try 
	    {
			/** Instantiation of houses */
			houses = new House[NUM_HOUSES];
			for (int i = 0; i < NUM_HOUSES; i++)
			{
				HOUSE_POS.y = (HOUSE_SIZE.y * (i + 1)) + HOUSE_SIZE.y;
				DirectedPoint housePosition = new DirectedPoint(HOUSE_POS);
				houses[i] = new House (HOUSE_ICON, housePosition, (DEVSSimulatorInterface) simulator, HOUSE_SIZE);
			}
			
			/** Instantiation of Fire station */
			fireStation = new House (FSTATION_ICON, STATION_POS, (DEVSSimulatorInterface) simulator, STATION_SIZE);
			
			/** Instantiation of Hospital */
			hospital = new House (HOSPITAL_ICON, HOSPITAL_POS, (DEVSSimulatorInterface) simulator, HOSPITAL_SIZE);
			
			/** Instantiation of accident vehicles */
			vehicles = new Vehicle[NUM_VEHICLES];
			vehicles[0] = new Vehicle (TRUCK_ICON, TRUCK_POS, (DEVSSimulatorInterface) simulator, VEHICLE_SIZE, this);
			vehicles[1] = new Vehicle (CRANE_ICON, CRANE_POS, (DEVSSimulatorInterface) simulator, VEHICLE_SIZE, this);
			
			/** Instantiation of other vehicles */
			for (int i = 0; i < (NUM_VEHICLES - 2) / 2; i++)
			{
				if (i % 2 == 0)
				{
					vehicles[2 + i] = new Vehicle (CAR_ICON1, new DirectedPoint(VEHICLE_POS + (VEHICLE_SIZE.x * 10) * (i + 1), 0, 0), 
							(DEVSSimulatorInterface) simulator, VEHICLE_SIZE, this);
					vehicles[NUM_VEHICLES - i - 1] = new Vehicle (CAR_ICON3, new DirectedPoint(-VEHICLE_POS - (VEHICLE_SIZE.x * 10) * (i + 1), 0, 0), 
							(DEVSSimulatorInterface) simulator, VEHICLE_SIZE, this);
				}
				else
				{
					vehicles[2 + i] = new Vehicle (CAR_ICON2, new DirectedPoint(VEHICLE_POS + (VEHICLE_SIZE.x * 10) * (i + 1), 0, 0), 
							(DEVSSimulatorInterface) simulator, VEHICLE_SIZE, this);
					vehicles[NUM_VEHICLES - i - 1] = new Vehicle (CAR_ICON4, new DirectedPoint(-VEHICLE_POS - (VEHICLE_SIZE.x * 10) * (i + 1), 0, 0), 
							(DEVSSimulatorInterface) simulator, VEHICLE_SIZE, this);
				}
			}
			
			/** Instantiation of collection point, though still with no size and temporary location */
			collectionPoint = new StaticObject(REDCROSS_ICON, new DirectedPoint (40, 40, 40), 
					(DEVSSimulatorInterface) simulator, new DirectedPoint (0, 0, 0));
			
			/** Instantiation of copi icon, though still with no size and temporary location */
			copi = new StaticObject(COPI_ICON, new DirectedPoint (0, 0, 0), 
	        		(DEVSSimulatorInterface) simulator, new DirectedPoint(0, 0, 0));
	    
	    } catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
		
	} // End of instantiateObjects() method
	
	/** 
	 * Instantiate civilian objects
	 */
	public void instantiateCivilians()
	{
		try 
	    {
			civilians = new Civilian[numCivilians];
			for (int i = 0; i < numCivilians; i++)
			{
				StreamInterface stream = simulator.getReplication().getStream("default");
				URL civilianImage;
				if (i % 3 == 0)
				{
					civilianImage = CIVILIAN_ICON1;
				}
				else if (i % 2 == 0)
				{
					civilianImage = CIVILIAN_ICON2;
				}
				else
				{
					civilianImage = CIVILIAN_ICON3;
				}
				/** Select range of random starting positions that stays out of roads */
				int xPos = 0;
				int yPos = 0;
				while (xPos < VEHICLE_SIZE.x && xPos > -VEHICLE_SIZE.x)
				{
					xPos = (int) -CANVAS_SIZE.x + stream.nextInt(0, (int) (CANVAS_SIZE.x * 2));
				}
				while (yPos < VEHICLE_SIZE.y && yPos > -(VEHICLE_SIZE.y + 2))
				{
					yPos = (int) -CANVAS_SIZE.y + stream.nextInt(0, (int) (CANVAS_SIZE.y * 2));
				}
				civilians[i] = new Civilian (civilianImage, new DirectedPoint(xPos, yPos, 0), 
						(DEVSSimulatorInterface) simulator, CIVILIAN_SIZE, this);
				
			}
	    } catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	} // End of instantiateCivilians() method
	
	/** 
	 * Instantiate responders objects
	 */
	public void instantiateResponders()
	{
		try 
	    {
			/** Initialize responder arrays */
			firemen = new ResponderProxy[numOvD + numFiremen + numBackupFiremen];
			medics = new ResponderProxy[numOvDG + numMedics];
			
			/** Instantiation of fire responders (animated objects) */
			for (int i = 0; i < numOvD; i++)
			{
				firemen[i] = new ResponderProxy (OVD_ICON, FIREMAN_SPEED, FIREMAN_SPEED_STD_DEV, STATION_POS, (DEVSSimulatorInterface) simulator, RESPONDER_SIZE, this);
			}
			for (int i = numOvD; i < numFiremen + numOvD; i++)
			{
				firemen[i] = new ResponderProxy (FIREMAN_ICON, FIREMAN_SPEED, FIREMAN_SPEED_STD_DEV, STATION_POS, (DEVSSimulatorInterface) simulator, RESPONDER_SIZE, this);
			}
			
			/** Istantiation of backup fire responders */
			for (int i = numFiremen + numOvD; i < numFiremen + numOvD + numBackupFiremen; i++)
			{
				firemen[i] = new ResponderProxy (FIREMAN_ICON, FIREMAN_SPEED, FIREMAN_SPEED_STD_DEV, STATION_POS, (DEVSSimulatorInterface) simulator, RESPONDER_SIZE, this);
			}
			
			/** Instantiation of medical responders (animated objects) */
			for (int i = 0; i < numOvDG; i++)
			{
				medics[i] = new ResponderProxy (OVDG_ICON, MEDIC_SPEED, MEDIC_SPEED_STD_DEV, HOSPITAL_POS, (DEVSSimulatorInterface) simulator, RESPONDER_SIZE, this);
			}
			for (int i = numOvDG; i < numMedics + numOvDG; i++)
			{
				medics[i] = new ResponderProxy (MEDIC_ICON, MEDIC_SPEED, MEDIC_SPEED_STD_DEV, HOSPITAL_POS, (DEVSSimulatorInterface) simulator, RESPONDER_SIZE, this);
			}
			
			/** Istantiation of backup medical responders */
			for (int i = numMedics + numOvDG; i < numMedics + numOvDG + numBackupMedics; i++)
			{
				medics[i] = new ResponderProxy (MEDIC_ICON, MEDIC_SPEED, MEDIC_SPEED_STD_DEV, HOSPITAL_POS, (DEVSSimulatorInterface) simulator, RESPONDER_SIZE, this);
			}
	    } catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	} // End of instantiateResponders() method
	
	/** 
	 * Launch agents
	 */
	public void launchAgents()
	{
	    
		try 
	    {
			profileMain = new ProfileImpl(null, 8888, null);
			mainController = Runtime.instance().createMainContainer(profileMain);
			rma = mainController.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
		    rma.start();

			/** Create object reference of this CrisisCoordModel as parameter for agents */
			Object [] args = new Object[1];
			args[0] = (Object) this;
			
			/** create a new dispatcher agent */
			mainController.createNewAgent("Dispatcher", "nl.tudelft.simulation.crisiscoord.agents.Dispatcher", args).start();
	        
			/** Create fire officer agent*/
			for (int i = 0; i < numOvD; i++) 
	        {
				mainController.createNewAgent("OvD" + i, "nl.tudelft.simulation.crisiscoord.agents.OvD", args).start();
	        }
			
			/** Create firemen agents */
	        for (int i = numOvD; i < numFiremen + numOvD; i++) 
	        {
	        	mainController.createNewAgent("Fireman" + i, "nl.tudelft.simulation.crisiscoord.agents.Fireman", args).start(); 
	        } 
	        
	        /** Create medical officer agent*/
			for (int i = 0; i < numOvDG; i++) 
	        {
				mainController.createNewAgent("OvDG" + i, "nl.tudelft.simulation.crisiscoord.agents.OvDG", args).start();
	        }
			
	        /** Create medic agents */
	        for (int i = numOvDG; i < numMedics + numOvDG; i++) 
	        {
	        	mainController.createNewAgent("Medic" + i, "nl.tudelft.simulation.crisiscoord.agents.Medic", args).start(); 
	        } 
	        
			/** create a new model agent */
	        mainController.createNewAgent("CrisisCoordModel", "nl.tudelft.simulation.crisiscoord.CrisisCoordModel", args).start();
	        
	    } catch (Exception e) 
		{
			e.printStackTrace();
		}
	} //End of launchAgents() method
	
	/** 
	 * Launch additional firemen
	 */
	public void launchAdditionalFiremen()
	{
	    
		try 
	    {
			/** Create object reference of this CrisisCoordModel as parameter for agents */
			Object [] args = new Object[2];
			args[0] = (Object) this;
			/** and indicate additional parameter for setting this as backup agent */
			args[1] = true;
			
			/** Create backup firemen agents */
	        for (int i = numFiremen + numOvD; i < numFiremen + numOvD + numBackupFiremen; i++) 
	        {
	        	mainController.createNewAgent("Fireman" + i, "nl.tudelft.simulation.crisiscoord.agents.Fireman", args).start(); 
	        } 
	        
	    } catch (Exception e) 
		{
			e.printStackTrace();
		}
	} //End of launchAdditionalFiremen() method
	
	/** 
	 * Returns a reference to the simulator of the model
	 * 
	 * @return simulator
	 */
	public SimulatorInterface getSimulator()
	{
		return simulator;
	}
	
	/** 
	 * Returns civilian in position i of civilian array
	 * @param i position in array
	 * @return civilian i
	 */
	public Civilian getCivilian(final int i)
	{
		return this.civilians[i];
	} 
	
	/** 
	 * Returns civilians array
	 * @return civilians 
	 */
	public Civilian [] getCivilians()
	{
		return this.civilians;
	} 

	/** 
	 * Returns responder in position i of responder array
	 * @param responderType string indicating type of responder, e.g. "Fireman"
	 * @param i position in array
	 * @return responder i
	 */
	public ResponderProxy getResponder(final String responderType, final int i)
	{
		if (responderType.contains("Medic") || responderType.contains("OvDG"))
		{
			return this.medics[i];
		}
		else if (responderType.contains("Fireman") || responderType.contains("OvD"))
		{
			return this.firemen[i];
		}
		else
		{
			return null;
		}
	}
	
	/** 
	 * Returns house in position i of house array
	 * @param i position in array
	 * @return house i
	 */
	public House getHouse(final int i)
	{
		return this.houses[i];
	}
	
	/** 
	 * Returns vehicle in position i of vehicle array
	 * @param i position in array
	 * @return vehicle i
	 */
	public Vehicle getVehicle(final int i)
	{
		return this.vehicles[i];
	}
	
	/** 
	 * Returns fire deployed in the model
	 * 
	 * @return fire
	 */
	public Fire getFire()
	{
		return this.fire;
	}
	
	/** 
	 * Returns fire deployed in the model
	 * @param location point at where the fire is set
	 * 
	 */
	public void setFire(final DirectedPoint location)
	{
		/* start the fire object growth and schedule the explosion */
		try
		{
			fire.setLocation(location);
			this.simulator.scheduleEvent(new SimEvent(this.simulator.getSimulatorTime() + EXPLOSION_TIME, this, this, "explode", null));
			fire.setSize(new DirectedPoint(4, 4, 0));
			fire.next();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/** 
	 * Returns collection point
	 * 
	 * @return collectionPoint
	 */
	public StaticObject getCollectionPoint()
	{
		return this.collectionPoint;
	}
	
	/** 
	 * Returns CoPI object
	 * 
	 * @return copi
	 */
	public StaticObject getCopi()
	{
		return this.copi;
	}
	
	/** 
	 * Explodes the existing fire by duplicating its size
	 * 
	 */
	public void explode()
	{
		Point3d tempSize = new Point3d();
		tempSize = this.fire.getSize();
		tempSize.scale(3);
		this.fire.setSize((DirectedPoint) tempSize);
	} // End of method explode
	
	/** 
	 * Terminates current experimental replication (run)
	 * 
	 */
	public void terminateReplication()
	{
		if (!terminated)
		{
			terminated = true;
			logExperiment();
			resetObjects();
			killAgents();
			try
			{
				this.simulator.getEventList().clear();
				this.simulator.stop();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	} // End of method terminateReplication
	
	/**
	 * Kill all agents to reset platform between experiments
	 */
	public void killAgents() 
	{
		try
		{
			if (mainController != null)
			{
				platformController = mainController.getPlatformController();
				platformController.kill();
				System.out.println("AGENT PLATFORM KILLED");
			}
	    } catch (Exception e) 
		{
			e.printStackTrace();
		}
	} // end of killAgents method
	
	/**
	 * Reset model objects method
	 */
	public void resetObjects() 
	{
		/** Reset vehicles */
		try
		{
    		for (int i = 0; this.vehicles != null && i < vehicles.length; i++)
		    {
	    		ContextUtil.unbindFromContext(this.vehicles[i].getRenderableImage());
	    		this.vehicles[i].setCrash(false);
	    		this.vehicles[i].setBurn(0);
	    		this.vehicles[i] = null;
		    }
	    	Vehicle.resetCounter();
	    	this.vehicles = null;
		} catch (Exception e)
        {
        	e.printStackTrace();
        }
		/** Reset fire */
	    if (this.fire != null)
	    {
	    	try
			{
	    		ContextUtil.unbindFromContext(this.fire.getRenderableImage());
		    	this.fire = null;
		    	Fire.resetCounter();
			} catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    /** Reset civilians */
	    try
		{
    		for (int i = 0; this.civilians != null && i < civilians.length; i++)
		    {
		    	ContextUtil.unbindFromContext(this.civilians[i].getRenderableImage());
		    	this.civilians[i].setSelected(false);
		    	this.civilians[i] = null;
		    }
	    	this.civilians = null;
	    	Civilian.resetCounter();
		} catch (Exception e)
        {
        	e.printStackTrace();
        }
	    /** Reset firemen */
		try
		{
    		for (int i = 0; this.firemen != null && i < firemen.length; i++)
		    {
	    		ContextUtil.unbindFromContext(this.firemen[i].getRenderableImage());
	    		this.firemen[i] = null;
		    }
	    	this.firemen = null;
		} catch (Exception e)
        {
        	e.printStackTrace();
        }
	    /** Reset Fire station */
	    if (this.fireStation != null)
	    {
	    	try
			{
	    		ContextUtil.unbindFromContext(this.fireStation.getRenderableImage());
		    	this.fireStation = null;
			} catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    /** Reset Hospital */
	    if (this.hospital != null)
	    {
	    	try
			{
	    		ContextUtil.unbindFromContext(this.hospital.getRenderableImage());
		    	this.hospital = null;
			} catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    /** Reset Collection point */
	    if (this.collectionPoint != null)
	    {
	    	try
			{
	    		ContextUtil.unbindFromContext(this.collectionPoint.getRenderableImage());
		    	this.collectionPoint = null;
			} catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    /** Reset copi icon */
	    if (this.copi != null)
	    {
	    	try
			{
	    		ContextUtil.unbindFromContext(this.copi.getRenderableImage());
		    	this.copi = null;
			} catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    /** Reset houses */
	    if (this.houses != null)
	    {
	    	try
			{
	    		for (int i = 0; i < houses.length; i++)
			    {
			    	ContextUtil.unbindFromContext(this.houses[i].getRenderableImage());
			    	this.houses[i] = null;
			    }
		    	this.houses = null;
		    	House.resetCounter();
			} catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	    }
	    /** Reset medics */
	    try
		{
    		for (int i = 0; this.medics != null && i < medics.length; i++)
		    {
	    		ContextUtil.unbindFromContext(this.medics[i].getRenderableImage());
	    		this.medics[i] = null;
		    }
	    	this.medics = null;
		} catch (Exception e)
        {
        	e.printStackTrace();
        }
	    /** reset counters and booleans */
	    StaticObject.resetCounter();
	    AnimatedObject.resetCounter();
	    ResponderProxy.resetCounter();
	    CrisisCoordModel.messageCounter = 0;
	    CrisisCoordModel.victimCounter = 0;
	    CrisisCoordModel.fatalityCounter = 0;
		informedFire = false;
		terminated = false;
		
		/** Free objects without reference and call garbage colllector */
		System.gc();
	} // End of reset method

	/**
	 * Set experimental properties for model
	 */
	public void setExperimentalProperties() 
	{
		try
		{
			Properties properties = this.simulator.getReplication().getTreatment().getProperties();
			this.numCivilians = new Integer(properties.getProperty("model.size.numcivilians"));
			this.numFiremen = new Integer(properties.getProperty("model.size.numfiremen"));
			this.numBackupFiremen = new Integer(properties.getProperty("model.size.numbackupfiremen"));
			this.numMedics = new Integer(properties.getProperty("model.size.nummedics"));
			this.numBackupMedics = new Integer(properties.getProperty("model.size.numbackupmedics"));
			this.numOvD = new Integer(properties.getProperty("model.size.numovd"));
			this.numOvDG = new Integer(properties.getProperty("model.size.numovdg"));
			this.timeout = new Integer(properties.getProperty("model.waiting.responseWaitingTimeout"));
			this.cycleT = new Integer(properties.getProperty("model.waiting.tickerCycleTime"));
			this.auto = new String(properties.getProperty("model.coordination.autoassignment"));
			this.mutualAdjustment = new String(properties.getProperty("model.coordination.mutualadjustment"));
		} catch (Exception e)
        {
        	System.err.println ("Error writing to file");
        	e.printStackTrace();
        }
	} // End setExperimentalProperties method
	
	/**
	 * Write experiment log into file
	 */
	public void createLogFile() 
	{
		/** Create experiment log file */
		if (fileName == null)
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH-mm-ss-");
	        Date date = new Date();
	        fileName = dateFormat.format(date) + "experimentLog.txt";
	        new File ("experiments/" + fileName);
		}
	} // End of createLogFile method
	
	/**
	 * Write experiment log into file
	 */
	public void logExperiment() 
	{
		FileOutputStream out; // declare a file output object
        PrintStream p; // declare a print stream object

        try
        {
        	// Create a new file output stream
        	out = new FileOutputStream("experiments/" + fileName, true);
            // Connect print stream to the output stream
            p = new PrintStream(out);

            /** Print experimental inputs to file */
            p.append (Integer.toString(numCivilians));
            p.append ("\t");
            p.append (Integer.toString(numFiremen));
            p.append ("\t");
            p.append (Integer.toString(numBackupFiremen));
            p.append ("\t");
            p.append (Integer.toString(numMedics));
            p.append ("\t");
            p.append (Integer.toString(numBackupMedics));
            p.append ("\t");
            p.append (Integer.toString(timeout));
            p.append ("\t");
    	    p.append (Integer.toString(cycleT));
    	    p.append ("\t");
    	    p.append (auto);
            p.append ("\t");
            p.append (mutualAdjustment);
            p.append ("\t");
            
            /** Print treatment ID to file */
            String treatment = this.simulator.getReplication().getTreatment().toString();
            /*** Get only treatment ID from treatment string */
            int leftLimit = treatment.indexOf('@');
    		int rightLimit = treatment.indexOf(';');
    	    String treatmentID = treatment.substring(leftLimit + 1, rightLimit);
    	    p.append (treatmentID);
    	    p.append ("\t");
            /** Print replication number to file */
    	    String replication = this.simulator.getReplication().toString();
            /*** Get only treatment ID from replication string */
            leftLimit = replication.indexOf(';');
    		rightLimit = replication.indexOf(';', leftLimit + 1);
    	    String replicationNumber = replication.substring(leftLimit + 1, rightLimit);
            p.append (replicationNumber);
            p.append ("\t");
            /** Print seed to file */
            /*** Get only seed from replication string */
            leftLimit = replication.lastIndexOf('[');
    		rightLimit = replication.indexOf(']');
    	    String seed = replication.substring(leftLimit + 1, rightLimit);
            p.append (seed);
            p.append ("\t");
            // Print time at end (total response time )
            p.append (Double.toString(this.simulator.getSimulatorTime()));
            p.append ("\t");
            // Print total number of messages exchanged in JADE
            p.append (Integer.toString(CrisisCoordModel.messageCounter));
            p.append ("\t");
            // Print total number of victims
            p.append (Integer.toString(CrisisCoordModel.victimCounter));
            p.append ("\t");
            // Print total number of fatal victims
            p.append (Integer.toString(CrisisCoordModel.fatalityCounter));
            p.println ("");

            p.close();
        } catch (Exception e)
        {
        	System.err.println ("Error writing to file");
        	e.printStackTrace();
        }
	} // End of logExperiment method
	
	/**
	 * Set AID of dispatcher agent
	 * @param  dispatcherService name of dispatcher service
	 */
	public void setDispatcherAID(final String dispatcherService) 
	{
		AID [] dispatchAgentIDs = searchDF(dispatcherService);
    	if (dispatchAgentIDs.length == 0)
	    {
	    	System.out.println(getLocalName() + " Didn´t find dispatcher");
	    	dispatcherAID = null;
	    }
	    else
	    {
	    	dispatcherAID = dispatchAgentIDs[0];
	    }
	} // End of setDispatcherAID method
	
	/** 
	 * Returns all animated objects inside the model
	 * 
	 * @return animatedObjects
	 */
	public AnimatedObject [] getAnimatedObjects()
	{
		if (firemen != null)
		{
			if (civilians != null)
			{
				if (vehicles != null)
				{
					AnimatedObject[] temp = new AnimatedObject[firemen.length + civilians.length + vehicles.length];
					System.arraycopy(civilians, 0, temp, 0, civilians.length);
					System.arraycopy(firemen, 0, temp, civilians.length, firemen.length);
					System.arraycopy(vehicles, 0, temp, civilians.length + firemen.length, vehicles.length);
					return temp;
				}
				else
				{
					AnimatedObject[] temp = new AnimatedObject[firemen.length + civilians.length];
					System.arraycopy(civilians, 0, temp, 0, civilians.length);
					System.arraycopy(firemen, 0, temp, civilians.length, firemen.length);
				}
			}
			else 
			{
				return firemen;
			}
		}
		else if (civilians != null)
		{
			return civilians;
		}
		return null;
	}
	
	/** 
	 * Returns all static objects inside the model
	 * 
	 * @return staticObjects
	 */
	public StaticObject [] getStaticObjects()
	{
		if (houses != null)
		{
			return houses;
		}
		return null;
	}
	
	/** 
	 * Returns all houses inside the model
	 * 
	 * @return houses
	 */
	public House [] getHouses()
	{
		return houses;
	}
	
	/** 
	 * Returns all vehicles inside the model
	 * 
	 * @return vehicles
	 */
	public Vehicle [] getVehicles()
	{
		return vehicles;
	}

	/** 
	 * Count messages sent by agents
	 */
	public static void countMessage()
	{
		CrisisCoordModel.messageCounter++;
	}
	
	/** 
	 * Count victims
	 */
	public static void countVictim()
	{
		CrisisCoordModel.victimCounter++;
	}
	
	/** 
	 * Count fatal victims
	 */
	public static void countFatality()
	{
		CrisisCoordModel.fatalityCounter++;
	}
	
	/**
	 * TickerBehaviour internal class: CallDispatchBehavioure
	 */
	class CallDispatchBehaviour extends TickerBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public CallDispatchBehaviour(final Agent a)
		{
			/** Created to check every interval tick */
			super(a, 1000);
		}
		
		/**
		 * onTick (must be implemented for TickerBehaviour
		 */
		protected void onTick() 
		{
			/** If a fire is detected and message has not been sent, then send fire message to "dispatcher" */
			if (modelref.getFire() != null && modelref.getFire().getSize().x > 0 && !modelref.informedFire)
			{
				/** Create the message */
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(dispatcherAID);
				/** Set message content and send */
				int x = 0;
				int y = 0;
				try
				{
					x = (int) modelref.getFire().getCenter().x; 
					y = (int) modelref.getFire().getCenter().y;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				/** add buffer to x and y positions for safety of responders */
				x -= ALARM_DISTANCE;
				y += ALARM_DISTANCE;
			    /** Set content and send */
				msg.setContent("(" + x + "," + y + ")");
			    send(msg);
				modelref.informedFire = true;
			}
			
			block();
			return;
	    }
		
	} // End internal class CallDispatchBehaviour
}
