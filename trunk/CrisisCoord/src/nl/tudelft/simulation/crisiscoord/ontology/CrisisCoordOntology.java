/*
 * @(#) CrisisCoordOntology.java May 19, 2009
 * 
 * Copyright (c) 2008 Delft University of Technology Jaffalaan 5, 2628 BX
 * Delft, the Netherlands All rights reserved.
 * 
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package nl.tudelft.simulation.crisiscoord.ontology;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.ReflectiveIntrospector;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;

/**
* Crisis Coordination Ontology.
* <p>
* (c) copyright 2008 <a href="http://www.simulation.tudelft.nl">Delft
* University of Technology </a>, the Netherlands. <br>
* License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
* General Public License (LGPL) </a>, no warranty.
* 
* @version 2.0 <br>
* @author <a> Rafael Gonzalez </a>
*/
public final class CrisisCoordOntology extends Ontology 
{
	/** Declares a serialVersionUID with no purpose */
	static final long serialVersionUID = 0;
	
	/** The name (symbolic constant) identifying this ontology */
	public static final String ONTOLOGY_NAME = "CRISISCOORD_ONTOLOGY";
	
	/** --- CONCEPTS --- */
	/** Civilian */
	public static final String CIVILIAN = "Civilian";
	/** Civilian location */
	public static final String CIVILIAN_LOCATION = "location";
	/** Civilian status */
	public static final String CIVILIAN_STATUS = "status";
	
	/** Estimated Population */
	public static final String POPULATION = "Population";
	/** Estimated number of Population who are civilians */
	public static final String POPULATION_CIVILIANS = "civilians";
	/** Estimated number of Population who are victims */
	public static final String POPULATION_VICTIMS = "victims";
	/** Estimated Population point of view location */
	public static final String POPULATION_LOCATION = "location";
	
	/** Fire */
	public static final String FIRE = "Fire";
	/** Fire location */
	public static final String FIRE_LOCATION = "location";
	/** Fire nature */
	public static final String FIRE_NATURE = "nature";
	/** Fire scope */
	public static final String FIRE_SCOPE = "scope";
	/** Fire expected evolution */
	public static final String FIRE_EVOLUTION = "evolution";

	/** GRIP */
	public static final String GRIP = "Grip";
	/** GRIP level */
	public static final String GRIP_LEVEL = "level";

	/** Infrastructure element */
	public static final String ELEMENT = "Element";
	/** Infrastructure element location */
	public static final String ELEMENT_LOCATION = "location";
	/** Infrastructure element extension */
	public static final String ELEMENT_EXTENSION = "extension";
	/** Infrastructure element accessibility */
	public static final String ELEMENT_ACCESS = "access";
	/** Infrastructure element condition */
	public static final String ELEMENT_CONDITION = "condition";
	
	/** Location */
	public static final String LOCATION = "Location";
	/** Location type */
	public static final String LOCATION_TYPE = "type";
	/** Location position */
	public static final String LOCATION_POSITION = "position";
	/** Location size */
	public static final String LOCATION_SIZE = "size";
	
	/** Material Resource */
	public static final String RESOURCE = "Resource";
	/** Material Resource type */
	public static final String RESOURCE_TYPE = "type";
	/** Material Resource last known location */
	public static final String RESOURCE_LOCATION = "location";	
	/** Material Resource destination */
	public static final String RESOURCE_DESTINATION = "destination";
	/** Material Resource transport mode */
	public static final String RESOURCE_TRANSPORT = "transport";
	/** Material Resource estimated time of arrival */
	public static final String RESOURCE_ETA = "eta";
	/** Material Resource status */
	public static final String RESOURCE_STATUS = "status";
	
	/** Responder */
	public static final String RESPONDER = "Responder";
	/** Responder Type */
	public static final String RESPONDER_TYPE = "type";
	/** Responder last known location */
	public static final String RESPONDER_LOCATION = "location";
	/** Responder status */
	public static final String RESPONDER_STATUS = "status";
	
	/** Strategy */
	public static final String STRATEGY = "Strategy";
	/** Strategy safety rules */
	public static final String STRATEGY_RULES = "rules";
	/** Strategy safety measures */
	public static final String STRATEGY_MEASURES = "measures";
	/** Strategy priorities */
	public static final String STRATEGY_PRIORITIES = "priorities";
	/** Strategy treatment protocol */
	public static final String STRATEGY_PROTOCOL = "protocol";
	/** Strategy Blockade closure */
	public static final String STRATEGY_CLOSURE = "closure";
	/** Strategy screening degree */
	public static final String STRATEGY_SCREENING = "screening";
	/** Strategy admission criteria */
	public static final String STRATEGY_ADMISSION = "admission";
	/** Strategy exit criteria */
	public static final String STRATEGY_EXIT = "exit";
	
	/** Traffic */
	public static final String TRAFFIC = "Traffic";
	/** Traffic intensity */
	public static final String TRAFFIC_INTENSITY = "intensity";

	/** Weather */
	public static final String WEATHER = "Weather";
	/** Weather state */
	public static final String WEATHER_STATE = "state";
	
	/** Time */
	public static final String TIME = "Time";
	/** Timestamp */
	public static final String TIME_STAMP = "timestamp";
	
	/** --- PREDICATES --- */
	/** Situation Awareness */
	public static final String AWARENESS = "Awareness";
	/** Situation Awareness Population */
	public static final String AWARENESS_POPULATION = "population";
	/** Situation Awareness Fire */
	public static final String AWARENESS_FIRE = "fire";
	/** Situation Awareness GRIP level */
	public static final String AWARENESS_GRIP = "grip";
	/** Situation Awareness Infrastructure elements */
	public static final String AWARENESS_ELEMENTS = "elements";
	/** Situation Awareness Infrastructure locations */
	public static final String AWARENESS_POINTOFVIEW = "pointofview";
	/** Situation Awareness Infrastructure resources */
	public static final String AWARENESS_RESOURCES = "resources";
	/** Situation Awareness Infrastructure RESPONDERS */
	public static final String AWARENESS_RESPONDERS = "responders";
	/** Situation Awareness Traffic */
	public static final String AWARENESS_TRAFFIC = "traffic";
	/** Situation Awareness Weather */
	public static final String AWARENESS_WEATHER = "weather";
	/** Situation Awareness Time */
	public static final String AWARENESS_TIME = "time";

	/** --- ACTIONS --- */
	/** Alarm */
	public static final String ALARM = "Alarm";
	/** Alarm type */
	public static final String ALARM_TYPE = "type";
	/** Alarm destination */
	public static final String ALARM_DESTINATION = "destination";
	
	/** Plan */
	public static final String PLAN = "Plan";
	/** Plan strategy */
	public static final String PLAN_STRATEGY = "strategy";
	/** Plan locations */
	public static final String PLAN_LOCATIONS = "locations";
	/** Plan responders */
	public static final String PLAN_RESPONDERS = "responders";
	/** Plan resources */
	public static final String PLAN_RESOURCES = "resources";
	/** Plan exit criteria */
	public static final String PLAN_EXIT = "exit";
	
	/** CoPI */
	public static final String COPI = "Copi";
	/** CoPI location */
	public static final String COPI_LOCATION = "location";
	
	/** The singleton instance of this ontology */
	private static Ontology theInstance = new CrisisCoordOntology();
	
	/**
	 * Get Instance (method to access the singleton ontology object)
	 * @return the instance of the ontology
	 */
	public static Ontology getInstance() 
	{
		return theInstance;
	}
	
	/**
	 * Private Constructor
	 */
	private CrisisCoordOntology()
	{
		/** This ontology extends the basic ontology */
		super(ONTOLOGY_NAME, BasicOntology.getInstance(), new ReflectiveIntrospector());
		
		try 
		{
			
			/** --- CONCEPTS --- */
			/** String schema */
			PrimitiveSchema stringSchema = (PrimitiveSchema) getSchema(BasicOntology.STRING);
			
			/** Integer schema */
			PrimitiveSchema integerSchema = (PrimitiveSchema) getSchema(BasicOntology.INTEGER);
			
			/** Double schema */
			PrimitiveSchema doubleSchema = (PrimitiveSchema) getSchema(BasicOntology.FLOAT);
			
			/** Location schema */
			ConceptSchema locationSchema = new ConceptSchema(LOCATION);
			locationSchema.add(LOCATION_TYPE, stringSchema, ObjectSchema.OPTIONAL);
			locationSchema.add(LOCATION_POSITION, stringSchema, ObjectSchema.OPTIONAL);
			locationSchema.add(LOCATION_SIZE, stringSchema, ObjectSchema.OPTIONAL);
			
			/** Civilian schema */
			ConceptSchema civilianSchema = new ConceptSchema(CIVILIAN);
			civilianSchema.add(CIVILIAN_LOCATION, locationSchema, ObjectSchema.OPTIONAL);
			civilianSchema.add(CIVILIAN_STATUS, stringSchema, ObjectSchema.OPTIONAL);
			
			/** Population schema */
			ConceptSchema populationSchema = new ConceptSchema(POPULATION);
			populationSchema.add(POPULATION_CIVILIANS, integerSchema, ObjectSchema.OPTIONAL);
			populationSchema.add(POPULATION_VICTIMS, integerSchema, ObjectSchema.OPTIONAL);
			
			/** Fire schema */
			ConceptSchema fireSchema = new ConceptSchema(FIRE);
			fireSchema.add(FIRE_LOCATION, locationSchema, ObjectSchema.OPTIONAL);
			fireSchema.add(FIRE_NATURE, stringSchema, ObjectSchema.OPTIONAL);
			fireSchema.add(FIRE_SCOPE, stringSchema, ObjectSchema.OPTIONAL);
			fireSchema.add(FIRE_EVOLUTION, stringSchema, ObjectSchema.OPTIONAL);
			
			/** GRIP schema */
			ConceptSchema gripSchema = new ConceptSchema(GRIP);
			gripSchema.add(GRIP_LEVEL, stringSchema, ObjectSchema.OPTIONAL);
			
			/** Infrastructure Element schema */
			ConceptSchema elementSchema = new ConceptSchema(ELEMENT);
			elementSchema.add(ELEMENT_LOCATION, locationSchema, ObjectSchema.OPTIONAL);
			elementSchema.add(ELEMENT_EXTENSION, stringSchema, ObjectSchema.OPTIONAL);
			elementSchema.add(ELEMENT_ACCESS, stringSchema, ObjectSchema.OPTIONAL);
			elementSchema.add(ELEMENT_CONDITION, stringSchema, ObjectSchema.OPTIONAL);
			
			/** Material resource schema */
			ConceptSchema resourceSchema = new ConceptSchema(RESOURCE);
			resourceSchema.add(RESOURCE_TYPE, stringSchema, ObjectSchema.OPTIONAL);
			resourceSchema.add(RESOURCE_LOCATION, locationSchema, ObjectSchema.OPTIONAL);
			resourceSchema.add(RESOURCE_DESTINATION, locationSchema, ObjectSchema.OPTIONAL);
			resourceSchema.add(RESOURCE_TRANSPORT, stringSchema, ObjectSchema.OPTIONAL);
			resourceSchema.add(RESOURCE_ETA, stringSchema, ObjectSchema.OPTIONAL);
			resourceSchema.add(RESOURCE_STATUS, stringSchema, ObjectSchema.OPTIONAL);
			
			/** Responder schema */
			ConceptSchema responderSchema = new ConceptSchema(RESPONDER);
			responderSchema.add(RESPONDER_TYPE, stringSchema, ObjectSchema.OPTIONAL);
			responderSchema.add(RESPONDER_LOCATION, locationSchema, ObjectSchema.OPTIONAL);
			responderSchema.add(RESPONDER_STATUS, stringSchema, ObjectSchema.OPTIONAL);
			
			/** Strategy schema */
			ConceptSchema strategySchema = new ConceptSchema(STRATEGY);
			strategySchema.add(STRATEGY_RULES, stringSchema, ObjectSchema.OPTIONAL);
			strategySchema.add(STRATEGY_MEASURES, stringSchema, ObjectSchema.OPTIONAL);
			strategySchema.add(STRATEGY_PRIORITIES, stringSchema, ObjectSchema.OPTIONAL);
			strategySchema.add(STRATEGY_PROTOCOL, stringSchema, ObjectSchema.OPTIONAL);
			strategySchema.add(STRATEGY_CLOSURE, stringSchema, ObjectSchema.OPTIONAL);
			strategySchema.add(STRATEGY_SCREENING, stringSchema, ObjectSchema.OPTIONAL);
			strategySchema.add(STRATEGY_ADMISSION, stringSchema, ObjectSchema.OPTIONAL);
			strategySchema.add(STRATEGY_EXIT, stringSchema, ObjectSchema.OPTIONAL);
			
			/** Traffic schema */
			ConceptSchema trafficSchema = new ConceptSchema(TRAFFIC);
			trafficSchema.add(TRAFFIC_INTENSITY, stringSchema, ObjectSchema.OPTIONAL);

			/** Weather schema */
			ConceptSchema weatherSchema = new ConceptSchema(WEATHER);
			weatherSchema.add(WEATHER_STATE, stringSchema, ObjectSchema.OPTIONAL);
			
			/** Time schema */
			ConceptSchema timeSchema = new ConceptSchema(TIME);
			timeSchema.add(TIME_STAMP, doubleSchema, ObjectSchema.OPTIONAL);

			/** Adding concept schemas to the ontology and linking them to their respective Java class */
			add(locationSchema, Location.class);
			add(civilianSchema, Civilian.class);
			add(populationSchema, Population.class);
			add(fireSchema, Fire.class);
			add(gripSchema, Grip.class);
			add(elementSchema, Element.class);
			add(resourceSchema, Resource.class);
			add(responderSchema, Responder.class);
			add(strategySchema, Strategy.class);
			add(trafficSchema, Traffic.class);
			add(weatherSchema, Weather.class);
			add(timeSchema, Time.class);


			/** --- PREDICATES --- */
			/** Situation Awareness schema */
			PredicateSchema awarenessSchema = new PredicateSchema (AWARENESS);
			awarenessSchema.add(AWARENESS_POPULATION, (ConceptSchema) getSchema(POPULATION), ObjectSchema.OPTIONAL);
			awarenessSchema.add(AWARENESS_FIRE, (ConceptSchema) getSchema(FIRE), ObjectSchema.OPTIONAL);
			awarenessSchema.add(AWARENESS_GRIP, (ConceptSchema) getSchema(GRIP), ObjectSchema.OPTIONAL);
			awarenessSchema.add(AWARENESS_ELEMENTS, (ConceptSchema) getSchema(ELEMENT), ObjectSchema.OPTIONAL);
			awarenessSchema.add(AWARENESS_POINTOFVIEW, (ConceptSchema) getSchema(LOCATION), ObjectSchema.OPTIONAL);
			awarenessSchema.add(AWARENESS_RESOURCES, (ConceptSchema) getSchema(RESOURCE), ObjectSchema.OPTIONAL);
			awarenessSchema.add(AWARENESS_RESPONDERS, (ConceptSchema) getSchema(RESPONDER), ObjectSchema.OPTIONAL);
			awarenessSchema.add(AWARENESS_TRAFFIC, (ConceptSchema) getSchema(TRAFFIC), ObjectSchema.OPTIONAL);
			awarenessSchema.add(AWARENESS_WEATHER, (ConceptSchema) getSchema(WEATHER), ObjectSchema.OPTIONAL);
			awarenessSchema.add(AWARENESS_TIME, (ConceptSchema) getSchema(TIME), ObjectSchema.OPTIONAL);
				
			/** Adding predicate schemas to the ontology */
			add(awarenessSchema, Awareness.class);
			
			/** --- ACTIONS --- */
			/** Alarm schema */
			AgentActionSchema alarmSchema = new AgentActionSchema(ALARM);
			alarmSchema.add(ALARM_TYPE, stringSchema, ObjectSchema.OPTIONAL);
			alarmSchema.add(ALARM_DESTINATION, locationSchema, ObjectSchema.OPTIONAL);
			
			/** Plan */
			AgentActionSchema planSchema = new AgentActionSchema (PLAN);
			planSchema.add(PLAN_STRATEGY, (ConceptSchema) getSchema(STRATEGY));
			planSchema.add(PLAN_LOCATIONS, (ConceptSchema) getSchema(LOCATION));
			planSchema.add(PLAN_RESPONDERS, (ConceptSchema) getSchema(RESPONDER));
			planSchema.add(PLAN_RESOURCES, (ConceptSchema) getSchema(RESOURCE));
			
			/** Copi schema */
			AgentActionSchema copiSchema = new AgentActionSchema(COPI);
			copiSchema.add(COPI_LOCATION, (ConceptSchema) getSchema(LOCATION), ObjectSchema.OPTIONAL);
			
			/** Adding action schemas to the ontology */
			add(alarmSchema, Alarm.class);
			add(planSchema, Plan.class);
			add(copiSchema, Copi.class);
			
		} catch (OntologyException oe)
		{
			oe.printStackTrace();
		}
	}
}
