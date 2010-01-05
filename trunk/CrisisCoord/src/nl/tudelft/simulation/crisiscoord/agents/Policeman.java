/*
 * @(#) Policeman.java October 14, 2008
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.agents;

import jade.core.Agent;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;
import jade.content.onto.Ontology;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;

import nl.tudelft.simulation.crisiscoord.ontology.CrisisCoordOntology;


/**
* Policeman response agent.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 1.6 <br>
* @author <a> Rafael Gonzalez </a>
*/
public class Policeman extends Agent 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/** Content manager for the ontology */
	private ContentManager manager = (ContentManager) getContentManager();
	
	/** Content language codec for the ontology */
	private Codec codec = (Codec) new SLCodec();
	
	/** Instance of the CrisisCoordOntology */
	private Ontology ontology = CrisisCoordOntology.getInstance();
	
	/** Threaded Behavior Factory */
	private ThreadedBehaviourFactory tbf = null;
     
	/**
	 * Setup of the agent
	 */
	protected void setup()
	{
        /** Register language and ontology */
		manager.registerLanguage(codec);
		manager.registerOntology(ontology);
		
		tbf = new ThreadedBehaviourFactory();
        addBehaviour(tbf.wrap(new PolicemanBehaviour(this)));
	} // End of setup method

	/**
	 * Take down agent method
	 *///
	protected void takeDown() 
	{
		tbf.interrupt();
		doDelete();
	}
	
	/**
	 * FSMBehaviour internal class: PolicemanBehaviour
	 */
	class PolicemanBehaviour extends FSMBehaviour 
	{
		
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;		
		
		/** Constant for transition to exit PolicemanBehaviour */
		protected final int exit = 1;
		/** Constant for transition to stay within PolicemanBehaviour */
		protected final int notExit = 0;
		/** Constant for transition to stay within ResponseBehaviour */
		protected final int victimsORfire = 1;
		/** Constant for transition to end ResponseBehaviour */
		protected final int noVictimsORfire = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public PolicemanBehaviour(final Agent a)
		{
			super(a);
	        /** register state behaviours */
			registerFirstState(new StartBehaviour(myAgent), "Start");
			registerState(new GetToLocationBehaviour(myAgent), "GetToLocation");
			registerState(new NotifyArrivalBehaviour(myAgent, exit, notExit), "NotifyArrival");
			registerState(new AssessSituationBehaviour(myAgent), "AssessSituation");
			registerState(new InformAssessmentBehaviour(myAgent, victimsORfire, noVictimsORfire), "InformAssessment");
			registerState(new RespondBehaviour(myAgent), "Respond");
			registerState(new UpdateAssessmentBehaviour(myAgent), "UpdateAssessment");
			registerState(new InformResultBehaviour(myAgent, victimsORfire, noVictimsORfire), "InformResult");
			registerLastState(new ExitBehaviour(myAgent), "Exit");
			
			/** register transitions */
			registerDefaultTransition ("Start", "GetToLocation");
			registerDefaultTransition ("GetToLocation", "NotifyArrival");
			registerTransition ("NotifyArrival", "AssessSituation", notExit);
			registerTransition ("NotifyArrival", "Exit", exit);
			registerDefaultTransition ("AssessSituation", "InformAssessment");
			registerTransition ("InformAssessment", "Respond", victimsORfire);
			registerTransition ("InformAssessment", "NotifyArrival", noVictimsORfire);
			registerDefaultTransition ("Respond", "UpdateAssessment");
			registerDefaultTransition ("UpdateAssessment", "InformResult");
			/** new String [] {"Respond"} needs to be added because Repond sub-behavior is terminated and a new one needs to be restarted */
			registerTransition ("InformResult", "Respond", victimsORfire, new String [] {"Respond"});
			registerTransition ("InformResult", "GetToLocation", noVictimsORfire); 
			
			/** schedule first state */
			//scheduleFirst();
		}
		
		/**
		 * onEnd of PolicemanBehavior
		 * @return 0
		 */
		public int onEnd()
		{
			return 0;
		}
		    
	} //End internal class: PolicemanBehaviour
	
	/**
	 * FSMChildBehaviour internal class: StartBehaviour
	 */
	class StartBehaviour extends FSMChildBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/**
		 * Constructor
		 * @param a the agent
		 */
		public StartBehaviour(final Agent a)
		{
			super(a);
		}
		
		/**
		 * Action (must be implemented for FSMChildBehaviour
		 */
		public void action()
		{
			boolean alarm = false;
			while (!alarm)
			{
				ACLMessage msg = myAgent.blockingReceive(); 
			    String content = msg.getContent(); 
			    if (content.equals("alarm")) 
			    {
			        System.out.println("---received alarm---"); 
			        alarm = true;
			    } 
			    else 
			    {
			        System.out.println("message not understood"); 
			    } 
			}
			finished = true;
		}
	
		/**
		 * Reset (must be implemented for FSMChildBehaviour
		 */
		public void reset()
		{
			super.reset();
		}
	} //End internal class: StartBehaviour
	
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
			System.out.println("Getting to location");
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
		
		/** Constant for transition to exit PolicemanBehaviour */
		protected int exit = 1;
		/** Constant for transition to stay within PolicemanBehaviour */
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
			ACLMessage sendmsg = new ACLMessage(ACLMessage.INFORM);
			sendmsg.setContent("arrived");
			System.out.println("Notifying arrival");
			myAgent.send(sendmsg);
			ACLMessage rcvmsg = myAgent.blockingReceive(); 
		    String content = rcvmsg.getContent(); 
		    if (content.equals("go")) 
		    {
		    	System.out.println("---exit criteria not fulfilled---");
		    	onEndReturnValue = notExit;
		    }
		    else if (content.equals("exit")) 
		    {
		        System.out.println("---exit criteria fulfilled---"); 
		        onEndReturnValue = exit;

		    } 
		    else 
		    {
		        System.out.println("message not understood"); 
		    } 
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
			System.out.println("Assessing situation");
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
	 * FSMChildBehaviour internal class: InformAssessmentBehaviour
	 */
	class InformAssessmentBehaviour extends FSMChildBehaviour
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
			ACLMessage sendmsg = new ACLMessage(ACLMessage.INFORM);
			sendmsg.setContent("assessment");
			System.out.println("Informing assessment");
			myAgent.send(sendmsg);
			ACLMessage rcvmsg = myAgent.blockingReceive(); 
		    String content = rcvmsg.getContent(); 
		    if (content == null) 
		    {
		    	onEndReturnValue = noVictimsORfire;
		    }
		    else if (content.equals("victim")) 
		    {
		        System.out.println("---victims on site---"); 
		        onEndReturnValue = victimsORfire;

		    } 
		    else if (content.equals("fire")) 
		    {
		        System.out.println("---fire on site---"); 
		        onEndReturnValue = victimsORfire;

		    } 
		    else 
		    {
		        System.out.println("message not understood"); 
		        onEndReturnValue = noVictimsORfire;
		    } 
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
	 * FSMBehaviour internal class: RespondBehaviour
	 */
	class RespondBehaviour extends FSMBehaviour
	{
		/** Declares a serialVersionUID with no purpose */
		static final long serialVersionUID = 0;
		
		/** Constant for transition to GuideVictims */
		protected final int guideAssignment = 0;
		/** Constant for transition to BlockAndScreen */
		protected final int blockAssignment = 1;
		/** Constant for transition to ControlTraffic */
		protected final int controlAssignment = 2;

		/**
		 * Constructor
		 * @param a the agent
		 */
		public RespondBehaviour(final Agent a)
		{
			super(a);
	        /** register state behaviours */
			registerFirstState(new AssignmentBehaviour(myAgent, guideAssignment, blockAssignment, controlAssignment), "Assignment");
			registerLastState(new GuideVictimsBehaviour(myAgent), "GuideVictims");
			registerLastState(new BlockAndScreenBehaviour(myAgent), "BlockAndScreen");
			registerLastState(new ControlTrafficBehaviour(myAgent), "ControlTraffic");
			
			/** register transitions */
			registerTransition ("Assignment", "GuideVictims", guideAssignment);
			registerTransition ("Assignment", "BlockAndScreen", blockAssignment);
			registerTransition ("Assignment", "ControlTraffic", controlAssignment);
		}
		
		/**
		 * FSMChildBehaviour internal class: AssignmentBehaviour
		 */
		class AssignmentBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/** Constant for transition to GuideVictims */
			protected int guideAssignment = 0;
			/** Constant for transition to BlockAndScreen */
			protected int blockAssignment = 1;
			/** Constant for transition to ControlTraffic */
			protected int controlAssignment = 2;
			
			/**
			 * Constructor
			 * @param a the agent
			 * @param guideAssignment guide victims assignment parameter
			 * @param blockAssignment block and screen assignment parameter
			 * @param controlAssignment control traffic assignment parameter
			 */
			public AssignmentBehaviour(final Agent a, final int guideAssignment, final int blockAssignment, final int controlAssignment)
			{
				super(a);
				this.guideAssignment = guideAssignment;
				this.blockAssignment = blockAssignment;
				this.controlAssignment = controlAssignment;
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				ACLMessage msg = myAgent.blockingReceive(); 
			    String content = msg.getContent(); 
			    if (content.equals("guide")) 
			    {
			        System.out.println("---victim guidance assigned---"); 
			        onEndReturnValue = guideAssignment;
			    } 
			    else if (content.equals("block")) 
			    {
			        System.out.println("---block and screen assigned---"); 
			        onEndReturnValue = blockAssignment;
			    } 
			    else if (content.equals("control")) 
			    {
			        System.out.println("---control traffic assigned---"); 
			        onEndReturnValue = controlAssignment;
			    } 
			    else 
			    {
			        System.out.println("message not understood"); 
			    } 
		        finished = true;
			}
			
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
		} //End internal class: AssignmentBehaviour
		
		/**
		 * FSMChildBehaviour internal class: GuideVictimsBehaviour
		 */
		class GuideVictimsBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public GuideVictimsBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				System.out.println("Guiding victims");
				finished = true;
			}
			
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
		} //End internal class: GuideVictimsBehaviour
		
		/**
		 * FSMChildBehaviour internal class: BlockAndScreenBehaviour
		 */
		class BlockAndScreenBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public BlockAndScreenBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				System.out.println("Blocking and screening");
				finished = true;
			}
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
		} //End internal class: BlockAndScreenBehaviour
		
		/**
		 * FSMChildBehaviour internal class: ControlTrafficBehaviour
		 */
		class ControlTrafficBehaviour extends FSMChildBehaviour
		{
			/** Declares a serialVersionUID with no purpose */
			static final long serialVersionUID = 0;
			
			/**
			 * Constructor
			 * @param a the agent
			 */
			public ControlTrafficBehaviour(final Agent a)
			{
				super(a);
			}
			
			/**
			 * Action (must be implemented for FSMChildBehaviour
			 */
			public void action()
			{
				System.out.println("Controlling traffic");
				finished = true;
			}
			/**
			 * Reset (must be implemented for FSMChildBehaviour
			 */
			public void reset()
			{
				super.reset();
			}
		} //End internal class: ControlTrafficBehaviour
		
	} //End internal class: RespondBehaviour
	
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
			System.out.println("Updating assessment");
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
	 * FSMChildBehaviour internal class: UpdateAssessmentBehaviour
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
			ACLMessage sendmsg = new ACLMessage(ACLMessage.INFORM);
			sendmsg.setContent("result");
			System.out.println("Informing result");
			myAgent.send(sendmsg);
			ACLMessage rcvmsg = myAgent.blockingReceive(); 
		    String content = rcvmsg.getContent();  
		    if (content == null) 
		    {
		    	onEndReturnValue = noVictimsORfire;
		    }
		    else if (content.equals("victim")) 
		    {
		        System.out.println("---victims still on site---"); 
		        onEndReturnValue = victimsORfire;
		    } 
		    else if (content.equals("fire")) 
		    {
		        System.out.println("---fire still on site---"); 
		        onEndReturnValue = victimsORfire;
		    } 
		    else 
		    {
		        System.out.println("message not understood"); 
		        onEndReturnValue = noVictimsORfire;
		    } 
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
			System.out.println("Exiting");
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
}
