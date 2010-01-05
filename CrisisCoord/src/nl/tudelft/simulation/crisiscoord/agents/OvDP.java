/*
 * @(#) OvDP.java July 16, 2008
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;

/**
* OvDP (Officier van Dienst Politie)- Police Officer agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 1.6 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class OvDP extends Policeman
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;

	/**
	 * Constructor
	 */
	public OvDP() 
	{
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Setup of the agent
	 */
	public void setup()
	{
        SequentialBehaviour leadBlocking = new SequentialBehaviour(this);
        SequentialBehaviour leadTrafficControl = new SequentialBehaviour(this);
        leadBlocking.addSubBehaviour(new RequestInfrastructureInfo(this));
        leadBlocking.addSubBehaviour(new PlanBlocking(this));
        leadBlocking.addSubBehaviour(new CommunicateBlockingPlan(this));
        leadBlocking.addSubBehaviour(new GetBlockingResources(this));
        leadBlocking.addSubBehaviour(new DeployBlockingResources(this));
        leadBlocking.addSubBehaviour(new InformBlockingSituation(this));
        leadBlocking.addSubBehaviour(new IssueBlockingMeasures(this));
        leadTrafficControl.addSubBehaviour(new RequestTrafficInfo(this));
        leadTrafficControl.addSubBehaviour(new PlanTraffic(this));
        leadTrafficControl.addSubBehaviour(new CommunicateTrafficPlan(this));
        leadTrafficControl.addSubBehaviour(new GetTrafficResources(this));
        leadTrafficControl.addSubBehaviour(new DeployTrafficResources(this));
        leadTrafficControl.addSubBehaviour(new InformPublic(this));
	} // End of setup method

	/**
	 * SimpleBehaviour internal class: RequestInfrastructureInfo
	 * Send broadcast msg to Policemen asking for information on objects and Civilians around incident site
	 */
	class RequestInfrastructureInfo extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public RequestInfrastructureInfo(final Agent a)
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
			return true;
		}
	
	} //End internal class: RequestInfrastructureInfo
	
	/**
	 * SimpleBehaviour internal class: PlanBlocking
	 * Assign Police teams and blocking elements to specific sites
	 */
	class PlanBlocking extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public PlanBlocking(final Agent a)
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
			return true;
		}
	
	} //End internal class: PlanBlocking
	
	/**
	 * SimpleBehaviour internal class: CommunicateBlockingPlan
	 * Send msg to OvDs indicating blocking plan
	 */
	class CommunicateBlockingPlan extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public CommunicateBlockingPlan(final Agent a)
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
			return true;
		}
	
	} //End internal class: CommunicateBlockingPlan
	
	/**
	 * SimpleBehaviour internal class: GetBlockingResources
	 * If necessary request additional Policemen or blocking elements (influcnes GRIP level)
	 */
	class GetBlockingResources extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public GetBlockingResources(final Agent a)
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
			return true;
		}
	
	} //End internal class: GetBlockingResources
	
	/**
	 * SimpleBehaviour internal class: DeployBlockingResources
	 * Send msg to Policemen indicating blocking plan
	 */
	class DeployBlockingResources extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public DeployBlockingResources(final Agent a)
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
			return true;
		}
	
	} //End internal class: DeployBlockingResources
	
	/**
	 * SimpleBehaviour internal class: InformBlockingSituation
	 * Send msg to OvDs incidating status of blocking situation
	 */
	class InformBlockingSituation extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public InformBlockingSituation(final Agent a)
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
			return true;
		}
	
	} //End internal class: InformBlockingSituation
	
	/**
	 * SimpleBehaviour internal class: IssueBlockingMeasures
	 */
	class IssueBlockingMeasures extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public IssueBlockingMeasures(final Agent a)
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
			return true;
		}
	
	} //End internal class: IssueBlockingMeasures
	
	/**
	 * SimpleBehaviour internal class: RequestTrafficInfo
	 */
	class RequestTrafficInfo extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public RequestTrafficInfo(final Agent a)
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
			return true;
		}
	
	} //End internal class: RequestTrafficInfo
	
	/**
	 * SimpleBehaviour internal class: PlanTraffic
	 */
	class PlanTraffic extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public PlanTraffic(final Agent a)
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
			return true;
		}
	
	} //End internal class: PlanTraffic
	
	/**
	 * SimpleBehaviour internal class: CommunicateTrafficPlan
	 */
	class CommunicateTrafficPlan extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public CommunicateTrafficPlan(final Agent a)
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
			return true;
		}
	
	} //End internal class: CommunicateTrafficPlan
	
	/**
	 * SimpleBehaviour internal class: GetTrafficResources
	 */
	class GetTrafficResources extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public GetTrafficResources(final Agent a)
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
			return true;
		}
	
	} //End internal class: GetTrafficResources	
	
	/**
	 * SimpleBehaviour internal class: DeployTrafficResources
	 */
	class DeployTrafficResources extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public DeployTrafficResources(final Agent a)
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
			return true;
		}
	
	} //End internal class: DeployTrafficResources
	
	/**
	 * SimpleBehaviour internal class: InformPublic
	 */
	class InformPublic extends SimpleBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public InformPublic(final Agent a)
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
			return true;
		}
	
	} //End internal class: InformPublic
}
