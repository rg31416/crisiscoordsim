/*
 * @(#) ResponseAgent.java December 1, 2009
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import java.util.Properties;

import nl.tudelft.simulation.crisiscoord.CrisisCoordModel;
import nl.tudelft.simulation.crisiscoord.model.ResponderProxy;
import nl.tudelft.simulation.crisiscoord.ontology.Alarm;
import nl.tudelft.simulation.crisiscoord.ontology.Awareness;
import nl.tudelft.simulation.crisiscoord.ontology.Element;
import nl.tudelft.simulation.crisiscoord.ontology.Fire;
import nl.tudelft.simulation.crisiscoord.ontology.Grip;
import nl.tudelft.simulation.crisiscoord.ontology.Location;
import nl.tudelft.simulation.crisiscoord.ontology.Population;
import nl.tudelft.simulation.crisiscoord.ontology.Resource;
import nl.tudelft.simulation.crisiscoord.ontology.Responder;
import nl.tudelft.simulation.crisiscoord.ontology.Strategy;
import nl.tudelft.simulation.crisiscoord.ontology.Time;
import nl.tudelft.simulation.crisiscoord.ontology.Traffic;
import nl.tudelft.simulation.crisiscoord.ontology.Weather;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

/**
* Response Agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 1.5 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class ResponseAgent extends ServiceAgent
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/** Experimental properties */
	/** Number of civilians */
	//private int numCivilians;
	/** On tick interval (time between ticker behaviours) */
	private int tickerCycleTime; 
	/** Mutual adjustment rescue */
	private boolean mutualAdjustment;
	
	/** Point of Origin of officer */
	protected DirectedPoint pointOfOrigin = new DirectedPoint();
	
	/** Number of the response agent to access agent proxy in DSOL model */
	private int myAgentNumber;

	/** Agent ID of officer for this agent */
	private AID myOfficerAID = new AID();
	
	/** Control value to exit Ticker Checking behaviour action */
	private boolean agentArrived;
	
	/** Reference to the DSOL model */
	private CrisisCoordModel model;
	
	/** ---Objects to instantiate the ontology locally (Knowledge of the agent) --- */
	/** Alarm object */
	private Alarm alarm = new Alarm();
	
	/** Location (destination) object */
	private Location destination;
	
	/** Strategy object*/
	private Strategy strategy = new Strategy();
	
	/** Awareness object */
	protected Awareness awareness = new Awareness();
	/** -- End of ontology objects --*/
	
	/** Alarmed boolean status */
	protected boolean alarmed = false;
	
	/** Integer identifying chosen victim, initialized at -1 for none */
	private int chosenVictim = -1;
	
	/** Service description of officer */
	protected String myOfficerService = new String();
	
	/** Threaded Behavior Factory */
	protected ThreadedBehaviourFactory threadedBehaviourFactory = null;
	
	/** Assignment value for responding to victims or fire */
	private String assignment = new String();
	
	/**
	 * Get Mutual Adjustment
	 * @return mutual adjustment if true
	 */
	public boolean getMutualAdjustment() 
	{
	    return mutualAdjustment;
	} 

	/**
	 * Get Responder Number method
	 * @return responder number
	 */
	public int getResponderNumber() 
	{
	    return myAgentNumber;
	} // End of getResponderName method
	
	/**
	 * Set Responder Number method
	 * @param agentType type of agent to obtain last letter to index number
	 */
	public void setResponderNumber(final String agentType) 
	{
		/** Get last letter of agentType */
		char typeLetter = agentType.charAt(agentType.length() - 1);

		/** We extract the responder number from the agent name */
		/** We get the name */
		String responderName = getName();
		/** We index the last letter in the Responder, i.e. 'm' for Fireman */ 
		int lastLetter = responderName.indexOf(typeLetter);
		/** We index the @ character contained in all agent names */
		int atCharacter = responderName.indexOf('@');
		/** We get the number between n and @ */
	    String strResponderNumber = responderName.substring(lastLetter + 1, atCharacter);
	    /** We convert the number from string to int and return it */
	    myAgentNumber = Integer.parseInt(strResponderNumber);
	} // End of setResponderName method
	
	/**
	 * Get Awareness method
	 * @return awareness ontology object for awareness
	 */
	public Awareness getAwareness() 
	{
	    return this.awareness;
	} // End of getAwareness method
	
	/**
	 * Get Agent Arrived method
	 * @return agent arrived boolean value
	 */
	public boolean getAgentArrived() 
	{
	    return this.agentArrived;
	} // End of getAgentArrived method
	
	/**
	 * Set Agent Arrived Number method
	 * @param arrived true or false arrival value
	 */
	public void setAgentArrived(final boolean arrived) 
	{
		this.agentArrived = arrived;
	} // End of setAgentArrived method
	
	/**
	 * Get AID of officer agent
	 * @return myOfficerAID
	 */
	public AID getMyOfficerAID() 
	{
		return myOfficerAID;
	} // End of getMyOfficerAID method
	
	/**
	 * Set AID of officer agent
	 * @param  myOfficerService name of officer service
	 */
	public void setMyOfficerAID(final String myOfficerService) 
	{
		AID [] officerAgentIDs = searchDF(myOfficerService);
    	if (officerAgentIDs.length == 0)
	    {
	    	System.out.println(getLocalName() + " Didn´t find officer");
	    	myOfficerAID = null;
	    }
	    else
	    {
	    	myOfficerAID = officerAgentIDs[0];
	    }
	} // End of setMyOfficerAID method
	
	/**
	 * Get reference to DSOL model
	 * @return model reference to DSOL model
	 */
	public CrisisCoordModel getDSOLModel() 
	{
	    return model;
	} // End of getDSOLModel method
	
	/**
	 * Set reference to DSOL model
	 * @param model reference to CrisisCoordMode (DSOL model)
	 */
	public void setDSOLModel(final CrisisCoordModel model) 
	{
		this.model = model;
		
		/** Initialize experimental properties */
		Properties properties;
		try
		{
			properties = getDSOLModel().getSimulator().getReplication().getTreatment().getProperties();
			//this.numCivilians = new Integer(properties.getProperty("model.size.numcivilians"));
			this.tickerCycleTime = new Integer(properties.getProperty("model.waiting.tickerCycleTime"));
			this.mutualAdjustment = new Boolean(properties.getProperty("model.coordination.mutualadjustment"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	} // End of setDSOLModel method
	
	/**
	 * Get Alarm method
	 * @return alarm alarm ontology object
	 */
	public Alarm getAlarm() 
	{
	    return alarm;
	} // End of getAlarm method
	
	/**
	 * Set Alarm method
	 * @param alarm alarm ontology object
	 */
	public void setAlarm(final Alarm alarm) 
	{
		this.alarm = alarm;
	} // End of setAlarm method
	
	/**
	 * Get Alarmed method
	 * @return alarmed 
	 */
	public boolean getAlarmed() 
	{
	    return alarmed;
	} // End of getAlarmed method
	
	/**
	 * Set Alarm method
	 * @param alarmed boolean alarmed value
	 */
	public void setAlarmed(final boolean alarmed) 
	{
		this.alarmed = alarmed;
	} // End of setAlarmed method
	
	/**
	 * Get Strategy method
	 * @return strategy 
	 */
	public Strategy getStrategy() 
	{
	    return strategy;
	} // End of getStrategy method
	
	/**
	 * Get Destination destination
	 * @return destination 
	 */
	public Location getDestination() 
	{
	    return destination;
	} // End of getDestination method
	
	/**
	 * Set Destination method
	 * @param destination location of detination
	 */
	public void setDestination(final Location destination) 
	{
		this.destination = destination;
	} // End of setDestination method
	
	/**
	 * Get Assignment method
	 * @return assignment string
	 */
	public String getAssignment() 
	{
	    return assignment;
	} // End of getAssignment method
	
	/**
	 * Set Assignment method
	 * @param assignment assignment string
	 */
	public void setAssignment(final String assignment) 
	{
	    this.assignment = assignment;
	} // End of getAssignment method
	
	/**
	 * Transform DirectedPoint to Location (ontology concept)
	 * @param directedPoint DirectedPoint to transform
	 * @return location
	 */
	public Location directedPointToLocation(final DirectedPoint directedPoint)
	{
        Location location = new Location();
		int x = (int) directedPoint.x;
        int y = (int) directedPoint.y;
        location.setPosition("(" + x + "," + y + ")");
        return location;
	} // End of directedPointToLocation method
	
	/**
	 * Transform Location (ontology concept) to DirectedPoint
	 * @param location Location to transform
	 * @return dpLocation DirectedPoint location
	 */
	public DirectedPoint locationToDirectedPoint(final Location location)
	{
		int leftParenhtesis = location.getPosition().lastIndexOf('(');
		int comma = location.getPosition().lastIndexOf(',');
		int rightParenthesis = location.getPosition().lastIndexOf(')');
	    String xPosition = location.getPosition().substring(leftParenhtesis + 1, comma);
	    String yPosition = location.getPosition().substring(comma + 1, rightParenthesis);
	    DirectedPoint dpDestination = new DirectedPoint(Integer.parseInt(xPosition), Integer.parseInt(yPosition), 0);
        return dpDestination;
	} // End of locationToDirectedPoint method
	
	/**
	 * Gets responder proxy from DSOL model
	 * @return reference to proxy of agernt inside DSOL model
	 */
	public ResponderProxy getResponderProxy()
	{
		return getDSOLModel().getResponder(getLocalName(), getResponderNumber());
	}
	
	/**
	 * Assess situation method
	 * keep in mind that assessment is the observation and situation awareness the result
	 */
	public void assessSituation() 
	{
		/** Reads environment by accessing the DSOL model */
		/**Read infrastructure elements */
		Element [] elements = new Element[getDSOLModel().getHouses().length + getDSOLModel().getVehicles().length];
		
		/** Read houses */
		for (int i = 0; i < getDSOLModel().getHouses().length; i++)
		{
			/** Transforms house i's location from DirectedPoint to Location */
	        Location houseLocation = new Location();
	        houseLocation.setType("House location");
	        int x = 0;
	        int y = 0;
	        try 
	        {
		        x = (int) getDSOLModel().getHouse(i).getCenter().x;
		        y = (int) getDSOLModel().getHouse(i).getCenter().y;
	        } catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	        houseLocation.setPosition("(" + x + "," + y + ")");
			/** Create new element */
	        elements[i] = new Element();
	        /** Sets house i location in ontological terms for own agent's knowledge */
			elements[i].setLocation(houseLocation);
			/** Check house status and set in own object */
			elements[i].setCondition("Burn state " + getDSOLModel().getHouse(i).getState());
		}
		
		/** Read vehicles */
		for (int i = getDSOLModel().getHouses().length, j = 0; i < getDSOLModel().getHouses().length + getDSOLModel().getVehicles().length; i++, j++)
		{
			/** Transforms vehicle i's location from DirectedPoint to Location */
	        Location vehicleLocation = new Location();
	        vehicleLocation.setType("Vehicle location");
	        int x = 0;
	        int y = 0;
	        try 
	        {
		        x = (int) getDSOLModel().getVehicle(j).getCenter().x;
		        y = (int) getDSOLModel().getVehicle(j).getCenter().y;
	        } catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	        vehicleLocation.setPosition("(" + x + "," + y + ")");
	        /** Create new element */
	        elements[i] = new Element();
	        /** Sets vehicle i location in ontological terms for own agent's knowledge */
	        elements[i].setLocation(vehicleLocation);
			/** Check vehicle status and set in own object */
			elements[i].setCondition("Burn state " + getDSOLModel().getVehicle(j).getState());
		}
		getAwareness().setElements(elements[0]);
		
		/** Read population */
		Population population = new Population();
		int numCivilians = 0;
		int numVictims = 0;
		/** Read civilians from model */
		for (int i = 0; i < getDSOLModel().getCivilians().length; i++)
		{
			/** Determine whether civilian is a victim or not */
			if (getDSOLModel().getCivilian(i).getState() > 0)
			{
				numVictims++;
			} else
			{
				numCivilians++;
			}
		}
		population.setCivilians(numCivilians);
		population.setVictims(numVictims);
		getAwareness().setPopulation(population);
		
		/** Establish point of view */
		Location pointOfView = new Location();
		try
		{
			pointOfView = directedPointToLocation(getResponderProxy().getCenter());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		pointOfView.setType("Point of view");
		getAwareness().setPointOfView(pointOfView);
		
		
		/** Read fire */
		Fire fire = new Fire();
		/** Transforms fire´s location from DirectedPoint to Location */
	    Location fireLocation = new Location();
	    fireLocation.setType("Fire location");
	    int x = 0;
	    int y = 0;
	    try 
	    {
	        x = (int) getDSOLModel().getFire().getCenter().x;
	        y = (int) getDSOLModel().getFire().getCenter().y;
	    } catch (Exception e)
	    {
	    	e.printStackTrace();
	    }	
	    fireLocation.setPosition("(" + x + "," + y + ")");
	    fire.setLocation(fireLocation);
	    fire.setNature("Vehicle fire");
	    /** Transforms fire´s size from Point3D to String */
	    try 
	    {
	        x = (int) getDSOLModel().getFire().getSize().x;
	        y = (int) getDSOLModel().getFire().getSize().y;
	    } catch (Exception e)
	    {
	    	e.printStackTrace();
	    	x = 0; 
	    	y = 0;
	    }	
	    fire.setScope(x + " X " + y);
	    getAwareness().setFire(fire);
		
		/** Set time of awareness */
		Time time = new Time();
		time.setTimestamp(0);
		try
		{
			time.setTimestamp(getDSOLModel().getSimulator().getSimulatorTime());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		getAwareness().setTime(time);
		
		/** Fill in all other awareness concepts blankly (for now) */
		getAwareness().setGrip(new Grip());
		getAwareness().setResources(new Resource());
		getAwareness().setResponders(new Responder());
		getAwareness().setTraffic(new Traffic());
		getAwareness().setWeather(new Weather());
		
		/** Wait a while while assessing */
		try
		{
			Thread.sleep(CrisisCoordModel.RESPONDER_SLEEP);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	} // End of assessSituation method
	
	/**
	 * TickerBehaviour internal class: CheckArrivalStatus
	 */
	class CheckArrivalStatus extends TickerBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public CheckArrivalStatus(final Agent a) 
	    {
			super(a, tickerCycleTime);
	    }
	   
		/**
		 * onTick (must be implemented for TickerBehaviour
		 */
		protected void onTick() 
		{
			/** If the agent proxy has arrived and the agent arrival is not yet true, then set it to true */
			if (getResponderProxy().getArrived() && !getAgentArrived())
			{
				ACLMessage sendmsg = new ACLMessage(ACLMessage.INFORM);
				sendmsg.setContent(getResponderNumber() + "Arrived");
				sendmsg.addReceiver(this.myAgent.getAID());
				myAgent.send(sendmsg);
				setAgentArrived(true);
				return;
			}
			
			this.getParent().getDataStore().clear();
	    }
	} //End internal class: CheckArrivalStatus
	
	/**
	 * FSMChildBehaviour internal class: GetToLocationBehaviour
	 */
	class GetToLocationBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public GetToLocationBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			try 
			{

				/** Change the destination of the Responder in DSOL Model to the alarm, and direct it towards new location */
				if (getResponderProxy().getCenter() != locationToDirectedPoint(getAlarm().getDestination()))
				{
					setAgentArrived(false);
					getResponderProxy().setArrived(false);
					getResponderProxy().setOrigin(getResponderProxy().getCenter());
					getResponderProxy().setDestination(locationToDirectedPoint(getAlarm().getDestination()));
					getResponderProxy().next();
				}
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			this.getParent().getDataStore().clear();
			finished = true;
		}
	
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
		
	} //End internal class: GetToLocationBehaviour
	
	/**
	 * FSMChildBehaviour internal class: NotifyArrivalBehaviour
	 */
	class NotifyArrivalBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Constant for transition to exit */
		protected int exit = 1;
		/** Constant for transition to stay */
		protected int notExit = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 * @param exit exit parameter
		 * @param notExit parameter
		 */
		public NotifyArrivalBehaviour(final Agent a, final int exit, final int notExit)
		{
			super(a);
			this.exit = exit;
			this.notExit = notExit;
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			/** Wait for message from self indicating arrival */
	        while (!getAgentArrived())
	        {
	        	myAgent.blockingReceive();
	        }
			
			/** Check exit criteria */
		    if (getStrategy().getExit().equals("go")) 
		    {
		    	onEndReturnValue = notExit;
		    }
		    else if (getStrategy().getExit().equals("exit")) 
		    {
		        onEndReturnValue = exit;

		    } 
		    this.getParent().getDataStore().clear();
			finished = true;
		}
		
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: NotifyArrivalBehaviour
	
	/**
	 * FSMChildBehaviour internal class: AssessSituationBehaviour
	 */
	class AssessSituationBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public AssessSituationBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			assessSituation();
			this.getParent().getDataStore().clear();
			finished = true;
		}
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: AssessSituationBehaviour
	
	/**
	 * FSMBehaviour internal class: AssistVictimBehaviour
	 */
	class AssistVictimBehaviour extends FSMBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Constant for transition to GetToVictim */
		protected final int found = 1;
		/** Constant for transition to NotifyMoved */
		protected final int notFound = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public AssistVictimBehaviour(final Agent a)
		{
			super(a);
	        /** register state behaviours */
			registerFirstState(new SearchVictimBehaviour(myAgent, found, notFound), "SearchVictim");
			registerState(new GetToVictimBehaviour(myAgent), "GetToVictim");
			registerState(new NotifyAtVictimBehaviour(myAgent), "NotifyAtVictim");
			registerState(new MoveVictimBehaviour(myAgent), "MoveVictim");
			registerLastState(new NotifyMovedBehaviour(myAgent), "NotifyMoved");
			
			/** register transitions */
			/** new String [] {"GetToVictim"} needs to be added because on second time sub-behavior is terminated and a new one needs to be restarted */
			registerTransition ("SearchVictim", "GetToVictim", found, new String [] {"GetToVictim"});
			/** new String [] {"NotifyMoved"} needs to be added because on second time sub-behavior is terminated and a new one needs to be restarted */
			registerTransition ("SearchVictim", "NotifyMoved", notFound, new String [] {"NotifyMoved"});
			/** new String [] {"NotifyAtVictim"} needs to be added because on second time sub-behavior is terminated and a new one needs to be restarted */
			registerDefaultTransition ("GetToVictim", "NotifyAtVictim", new String [] {"NotifyAtVictim"});
			/** new String [] {"MoveVictim"} needs to be added because on second time sub-behavior is terminated and a new one needs to be restarted */
			registerDefaultTransition ("NotifyAtVictim", "MoveVictim", new String [] {"MoveVictim"});
			/** new String [] {"NotifyMoved"} needs to be added because on second time sub-behavior is terminated and a new one needs to be restarted */
			registerDefaultTransition ("MoveVictim", "NotifyMoved", new String [] {"NotifyMoved"});
			
		}
		
		/**
		 * FSMChildBehaviour internal class: SearchVictimBehaviour
		 */
		class SearchVictimBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/** Constant for transition to GetToVictim */
			protected int found = 1;
			/** Constant for transition to NotifyMoved */
			protected int notFound = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 * @param found parameter
			 * @param notFound parameter
			 */
			public SearchVictimBehaviour(final Agent a, final int found, final int notFound)
			{
				super(a);
				this.found = found;
				this.notFound = notFound;
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				chosenVictim = searchVictim();
				
				/** If a victim is found then arrival status is set to false */
				/** Both on the response agent and the responder proxy */
				/** Also victim is tagged as selected */
				if (chosenVictim != -1)
				{
					getResponderProxy().setArrived(false);
					setAgentArrived(false);
					getDSOLModel().getCivilian(chosenVictim).setArrived(true);
					
					/** only use selected when mutual adjustment false **/
					/** and in that case add messagecounter */
					if (!mutualAdjustment)
					{
						getDSOLModel().getCivilian(chosenVictim).setSelected(true);
						CrisisCoordModel.countMessage();
					}
					
					onEndReturnValue = found;
				}
				else
				{
					onEndReturnValue = notFound;
				}
				this.getParent().getDataStore().clear();
				finished = true;
			}
			
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
			
		} //End internal class: SearchVictimBehaviour
		
		/**
		 * FSMChildBehaviour internal class: GetToVictimBehaviour
		 */
		class GetToVictimBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public GetToVictimBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				try
				{
					getResponderProxy().setOrigin(getResponderProxy().getCenter());
					getResponderProxy().setDestination(getDSOLModel().getCivilian(chosenVictim).getCenter());
					getResponderProxy().next();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				this.getParent().getDataStore().clear();
				finished = true;
			}
			
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
			
		} //End internal class: GetToVictimBehaviour
		
		/**
		 * FSMChildBehaviour internal class: NotifyAtVictimBehaviour
		 */
		class NotifyAtVictimBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public NotifyAtVictimBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				/** Wait for message from self indicating arrival */
		        while (!getAgentArrived())
		        {
		        	myAgent.blockingReceive();
		        }

		        /** Because arrival means another movement together with victim */
	        	/** Arrival of agent and its proxy is again set to false */
				getResponderProxy().setArrived(false);
				setAgentArrived(false);
				this.getParent().getDataStore().clear();
				finished = true;
			}
			
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
			
		} //End internal class: NotifyAtVictimBehaviour
		
		/**
		 * FSMChildBehaviour internal class: MoveVictimBehaviour
		 */
		class MoveVictimBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public MoveVictimBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{

		        /** Move Victim to Collection Point */
				try
				{
					/** if victim has not been moved yet **/
					if (getDSOLModel().getCivilian(chosenVictim).getLocation().equals(getResponderProxy().getLocation()))
					{
						/** set destination to victim collection point */
				        DirectedPoint dpDestination = new DirectedPoint();
				        dpDestination = getDSOLModel().getCollectionPoint().getLocation();
						
				        /** Move victim proxy (disappear to mimic picking up) and set arrived to true to impede movement*/
				        getDSOLModel().getCivilian(chosenVictim).getLocation().x += 1;
				        getDSOLModel().getCivilian(chosenVictim).setSize(new DirectedPoint(0, 0, 0));
				        getDSOLModel().getCivilian(chosenVictim).setArrived(true);
				        getDSOLModel().getCivilian(chosenVictim).setOrigin(dpDestination);
				        getDSOLModel().getCivilian(chosenVictim).setDestination(dpDestination);
				        
				        /** assist victim */
				        getDSOLModel().getCivilian(chosenVictim).assist();
				        
				        /** unselect victims if applicable */
				        if (!mutualAdjustment)
						{
							getDSOLModel().getCivilian(chosenVictim).setSelected(false);
							CrisisCoordModel.countMessage();
						}
						
				        /** Move responder proxy */
				        getResponderProxy().setOrigin(getResponderProxy().getCenter());
				        getResponderProxy().setDestination(dpDestination);
				        getResponderProxy().next();
				        
					} else // do not move otherwise
					{
						getResponderProxy().setArrived(true);
						setAgentArrived(true);
						/** unselect victims if applicable */
						if (!mutualAdjustment)
						{
							getDSOLModel().getCivilian(chosenVictim).setSelected(false);
							CrisisCoordModel.countMessage();
						}
						chosenVictim = -1;
					}
					
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				this.getParent().getDataStore().clear();
				finished = true;
			}
			
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
			
		} //End internal class: MoveVictimBehaviour
		
		/**
		 * FSMChildBehaviour internal class: NotifyMovedBehaviour
		 */
		class NotifyMovedBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public NotifyMovedBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				/** If closes victim was found i.e. different from -1 */
				if (chosenVictim != -1)
				{
					/** Wait for message from self indicating arrival */
			        while (!getAgentArrived())
			        {
			        	ACLMessage arrivalmsg = myAgent.blockingReceive();
			        	/** If message is from self and indicates arrival, set down victim and reset closestVictim */
			        	if (arrivalmsg.getContent().equals(getResponderNumber() + "Arrived"))
			        	{
			        		try
			        		{
			        			getDSOLModel().getCivilian(chosenVictim).setLocation(getResponderProxy().getCenter());
			        			getDSOLModel().getCivilian(chosenVictim).setSize(CrisisCoordModel.CIVILIAN_SIZE);
			        			/** set closestVictim back to -1 to search for new victim */
			        			chosenVictim = -1;
			        		} catch (Exception e)
							{
								e.printStackTrace();
							}
			        		
			        	}
			        }
				}
				this.getParent().getDataStore().clear();
		        finished = true;
			}
			
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
			
		} //End internal class: NotifyMovedBehaviour
		
	} //End internal class: AssistVictimBehaviour
	
	/**
	 * FSMChildBehaviour internal class: InformAssessmentBehaviour
	 */
	class InformAssessmentBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Constant for transition to stay within Response Behaviour */
		protected int victimsORfire = 1;
		/** Constant for transition to end Response behaviour */
		protected int noVictimsORfire = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 * @param victimsORfire continue parameter
		 * @param noVictimsORfire return parameter
		 */
		public InformAssessmentBehaviour(final Agent a, final int victimsORfire, final int noVictimsORfire)
		{
			super(a);
			this.victimsORfire = victimsORfire;
			this.noVictimsORfire = noVictimsORfire;
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			/** Prepare the message to inform assessment as reply to query from officer */
			MessageTemplate mt =
				AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_QUERY);
				/** Add responder behaviour to reply to query */
				myAgent.addBehaviour(new AchieveREResponder(myAgent, mt) 
				{
					/** Declares a serialVersionUID with no purpose */
					static final long serialVersionUID = 0;
					
					/** 
			         * overrides handleRequest
			         * @param query the query message
			         * @return the response
			         */
			        protected ACLMessage handleRequest(final ACLMessage msg) 
			        {
			        	return null;
			        }
			        
			        /** 
			         * overrides prepareResultNotification
			         * @param query the query message
			         * @return the response sent in handleRequest before if at all
			         */
					protected ACLMessage prepareResultNotification(final ACLMessage query, final ACLMessage response) 
					{
						ACLMessage informReply = query.createReply();
						informReply.setPerformative(ACLMessage.INFORM);
						/** Convert Java objects into strings for the message */
						try
						{
							getContentManager().fillContent(informReply, getAwareness());
							CrisisCoordModel.countMessage();
						} catch (Exception e)
						{
							e.printStackTrace();
						} 						
						return informReply;
					}
				});
			
			/** Check if there are still victims or fire (according to own awareness) */
		    if (getAwareness().getPopulation().getVictims() == 0 && getAwareness().getFire().getScope().equals("0 X 0")) 
		    {
		    	getStrategy().setExit("exit");
		    	onEndReturnValue = noVictimsORfire;
		    }
		    else
		    {
		        onEndReturnValue = victimsORfire;

		    } 
		    this.getParent().getDataStore().clear();
		    finished = true;
		}
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: InformAssessmentBehaviour
	
	/**
	 * FSMChildBehaviour internal class: UpdateAssessmentBehaviour
	 */
	class UpdateAssessmentBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public UpdateAssessmentBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			assessSituation();
			this.getParent().getDataStore().clear();
			finished = true;
		}
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: UpdateAssessmentBehaviour
	
	/**
	 * FSMChildBehaviour internal class: InformResultBehaviour
	 */
	class InformResultBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Constant for transition to stay within ResponseBehaviour */
		protected int victimsORfire = 1;
		/** Constant for transition to end ResponseBehaviour */
		protected int noVictimsORfire = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 * @param victimsORfire continue parameter
		 * @param noVictimsORfire return parameter
		 */
		public InformResultBehaviour(final Agent a, final int victimsORfire, final int noVictimsORfire)
		{
			super(a);
			this.victimsORfire = victimsORfire;
			this.noVictimsORfire = noVictimsORfire;
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			/** Prepare and send the result message */
	        ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
	        msg.addReceiver(getMyOfficerAID());
			send(msg);
			
			/** Check if there are still victims or fire (according to own awareness) */
		    if (getAwareness().getPopulation().getVictims() == 0 && getAwareness().getFire().getScope().equals("0 X 0")) 
		    {
		    	getStrategy().setExit("exit");
		    	onEndReturnValue = noVictimsORfire;
		    }
		    else
		    {
		        onEndReturnValue = victimsORfire;

		    } 
		    this.getParent().getDataStore().clear();
			finished = true;
		}
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: InformResultBehaviour
	
	/**
	 * FSMChildBehaviour internal class: ExitBehaviour
	 */
	class ExitBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public ExitBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			try
			{
				setAgentArrived(false);
				getResponderProxy().setArrived(false);
				getResponderProxy().setOrigin(getResponderProxy().getCenter());
				getResponderProxy().setDestination(pointOfOrigin);
				getResponderProxy().next();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
			this.getParent().getDataStore().clear();
			finished = true;
		}
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: ExitBehaviour
	
	/**
	 * FSMChildBehaviour internal class: NotifyExitBehaviour
	 */
	class NotifyExitBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public NotifyExitBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			/** Wait for message from self indicating arrival */
	        while (!getAgentArrived())
	        {
	        	myAgent.blockingReceive();
	        }
	        
	        /** Delete agent and responder proxy from model */
			this.getParent().getDataStore().clear();
			takeDown();
			
			finished = true;
		}
		
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: NotifyExitBehaviour
	
	/**
	 * OneShotBehaviour internal class: MessageResponderBehaviour
	 */
	class MessageResponderBehaviour extends OneShotBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public MessageResponderBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for OneShotBehaviour
		 */
		public void action()
		{
			/** Prepare the message to accept or reject alarm or assignment requests */
			MessageTemplate template = MessageTemplate
            .and(AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST),
            MessageTemplate.and(MessageTemplate.MatchOntology(getOntology().getName()),
            MessageTemplate.MatchLanguage(getCodec().getName())));
		                
				/** Add responder behaviour to reply to request */
				myAgent.addBehaviour(new AchieveREResponder(myAgent, template) 
				{
					/** Declares a serialVersionUID with no purpose */
					static final long serialVersionUID = 0;
					
					/** 
			         * overrides handleRequest
			         * @param request the query message
			         * @return the response
			         */
			        protected ACLMessage handleRequest(final ACLMessage request) 
			        {
			        	ACLMessage response = request.createReply();
						ContentElement content = null;
						Concept actionContent = null;
		               
						/** If message is not action, but assignment: Handle assignment request */
						if (request.getContent().contains("rescueAssigned"))
						{
							handleAssignmentRequest(response, content);
							CrisisCoordModel.countMessage();
						} else
						{
							/** Convert from String to Java objects (decoding the message) */
							try
							{
								content = myAgent.getContentManager().extractContent(request);
							} catch (Exception e)
							{
								/** if an exception occurs return not understood reply */
								e.printStackTrace();
								response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
								response.setContent("Decoding failed");
								CrisisCoordModel.countMessage();
								return response;
							}
							
							/** If message received is Action */
							if (content instanceof Action)
							{
								/** Extract content of action */
								actionContent = ((Action) content).getAction();
								
								/** Handle Alarm request */
								if (actionContent instanceof Alarm)
								{
									handleAlarmRequest(response, actionContent);
									CrisisCoordModel.countMessage();
								}
							} else /** for all other messages, reply with not understood */ 
							{
								response.setPerformative(ACLMessage.NOT_UNDERSTOOD);	
								CrisisCoordModel.countMessage();
							}
						}
						return response;
			        }
			        
			        /** 
			         * overrides prepareResultNotification
			         * @param request the request message
			         * @return the response sent in handleRequest before if at all
			         */
					protected ACLMessage prepareResultNotification(final ACLMessage request, final ACLMessage response) 
					{
						return null;
					}
				});
				this.getParent().getDataStore().clear();
		}
	} //End internal class: MessageResponderBehaviour
	
	/**
	 * Handle Alarm request Response
	 * @param response the request response message
	 * @param content alarm request content
	 */
	public void handleAlarmRequest(final ACLMessage response, final Concept content) 
	{
		/** Get content and Extract destination from alarm */
        setAlarm((Alarm) content);
        setDestination(getAlarm().getDestination());
        
        /** Generate response (AGREE) */ 
        response.setPerformative(ACLMessage.AGREE);
        send(response);
        
        /** Set own status to alarmed and send message to self for unblocking agent */
        setAlarmed(true);
        ACLMessage alarmSelfmsg = new ACLMessage(ACLMessage.INFORM);
        alarmSelfmsg.setContent(getResponderNumber() + "Alarmed");
        alarmSelfmsg.addReceiver(getAID());
        send(alarmSelfmsg);
	} // End of handleAlarmRequest method
	
	/**
	 * Handle Assignment request Response
	 * @param response the request response message
	 * @param content assignment request content
	 */
	public void handleAssignmentRequest(final ACLMessage response, final ContentElement content) 
	{
		setAssignment("victims");
        /** Generate response (AGREE) */ 
        response.setPerformative(ACLMessage.AGREE);
        send(response);
	} // End of handleAlarmRequest method
	
	/**
	 * Handle Assessment request Response
	 * @param response the request response message
	 * @param content alarm request content
	 */
	public void handleAssessmentRequest(final ACLMessage response, final Concept content) 
	{
		/** Get content and Extract destination from alarm */
        setAlarm((Alarm) content);
        setDestination(getAlarm().getDestination());
        
        /** Generate response (AGREE) */ 
        response.setPerformative(ACLMessage.AGREE);
        send(response);
        
        /** Set own status to alarmed and send message to self for unblocking agent */
        setAlarmed(true);
        ACLMessage alarmSelfmsg = new ACLMessage(ACLMessage.INFORM);
        alarmSelfmsg.setContent(getResponderNumber() + "Alarmed");
        alarmSelfmsg.addReceiver(getAID());
        send(alarmSelfmsg);
        
	} // End of handleAssessmentRequest method
	
	/**
	 * Search for victim
	 * @return victim found as index
	 */
	public int searchVictim() 
	{
		double distance; // distance between responder and victim
		double smallestdistance = 100; //smallest current distance between responder and victim
		int closestVictim = -1; // index for keeping closestVictim, last of which is returned
		
		try
		{
			/** Search for closest victim */
			for (int i = 0; i < getDSOLModel().getCivilians().length; i++)
			{
				/** If civilian is victim and not selected */
				if (getDSOLModel().getCivilian(i).getState() > 0 && !getDSOLModel().getCivilian(i).getSelected())
				{
					distance = getResponderProxy().getCenter().distance(getDSOLModel().getCivilian(i).getCenter());
					/** if smaller than current smallestDistance */
					if (distance < smallestdistance)
					{
						smallestdistance = distance;
						closestVictim = i;
					}
				}
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return closestVictim;
	} // end of searchVictim method
} // End of ResponseAgent class
