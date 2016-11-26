package mySSWAPService;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import info.sswap.api.model.RIG;
import info.sswap.api.model.SSWAPIndividual;
import info.sswap.api.model.SSWAPObject;
import info.sswap.api.model.SSWAPPredicate;
import info.sswap.api.model.SSWAPProperty;
import info.sswap.api.model.SSWAPSubject;
import info.sswap.api.servlet.MapsTo;

public class SSWAPService extends MapsTo {
	// initialize variables here
	RIG rigGraph;
	String flightPrice,inBDuration,outBDuration;
	public SSWAPPredicate VCpredicate;
	public SSWAPPredicate VDpredicate;
	public SSWAPPredicate ARpredicate;
	public SSWAPPredicate ATpredicate;
	public SSWAPPredicate ANpredicate;
	public SSWAPPredicate AIpredicate;
	public SSWAPPredicate AFpredicate;
	public SSWAPPredicate HRpredicate;
	public SSWAPPredicate HIpredicate;
	public SSWAPPredicate HNpredicate;
	public SSWAPPredicate HLngpredicate;
	public SSWAPPredicate HLowpredicate;
	public SSWAPPredicate HLatpredicate;
	public SSWAPPredicate HPpredicate;
	public SSWAPPredicate FODTpredicate;
	public SSWAPPredicate FOATpredicate;
	public SSWAPPredicate FOOpredicate;
	public SSWAPPredicate FODpredicate;
	public SSWAPPredicate FIDTpredicate;
	public SSWAPPredicate FIATpredicate;
	public SSWAPPredicate FIOpredicate;
	public SSWAPPredicate FIDpredicate;
	public SSWAPPredicate BudgetPredicate;
	public SSWAPPredicate FlightDurationPredicate;
	public SSWAPPredicate FlightLegPredicate;
	public SSWAPPredicate FlightPricePredicate;
	public URI FlightLegUri;
	JSONObject obj;
	ArrayList<HashMap<String, String>> hotelList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> vaccinationList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> attractionList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> inboundList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> outboundList = new ArrayList<HashMap<String, String>>();
	ArrayList<SSWAPIndividual> outBoundFlightIndList = new ArrayList<SSWAPIndividual>();

	@Override
	protected void initializeRequest(RIG rig) {
		rigGraph = rig;

		// if we need to check service parameters we could start here
	}

	@Override
	protected void mapsTo(SSWAPSubject translatedSubject) throws Exception {

		SSWAPSubject subject = translatedSubject;
		SSWAPObject object = translatedSubject.getObject();

		HashMap<String, String> paraValue = new HashMap<String, String>();
		Iterator<SSWAPProperty> iterator = subject.getProperties().iterator();

		while (iterator.hasNext()) {
			SSWAPProperty property = iterator.next();
			SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());
			String[] a = predicate.getURI().toString().split("#", 2);
			System.out.println("value" + subject.getProperty(predicate).getValue().asString());
			paraValue.put(a[1], subject.getProperty(predicate).getValue().asString());

		}
		// URIBuilder builder;
		String backendUrl = "http://limitless-lowlands-64274.herokuapp.com/sdp?";
	    //builder = new URIBuilder(backendUrl);
	    for(String key :paraValue.keySet())
	    {
	        String temp = key.toLowerCase() + "="+paraValue.get(key)+ "&";
	        backendUrl += temp;
	        //builder.addParameter(key.toLowerCase(), paraValue.get(key));
	        
	    }
	    obj = null;
	    try{
	    backendUrl = backendUrl.substring(0, backendUrl.length()-1);
	    System.out.println(backendUrl);
	    URL url = new URL(backendUrl);        
	    InputStream stream = url.openStream();
	    InputStreamReader reader = new InputStreamReader(stream,StandardCharsets.UTF_8);
	    JSONParser jsonParser = new JSONParser();
	    obj = (JSONObject)jsonParser.parse(reader);
	    //JSONObject obj = (JSONObject)JSONValue.parse(reader);
	    System.out.println(obj);
	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }

		JSONArray array;
		JSONArray vaccines;
		JSONArray attractions;

		try {
			System.out.println("Inside json parsing");
			JSONObject jsonObject =  obj;
			System.out.println(jsonObject);
			array = (JSONArray) jsonObject.get("results");
			int i;
			System.out.println("ARRAY SIZE: "+array.size());
			for (i = 0; i < array.size(); ++i) {

				JSONObject result = (JSONObject) array.get(i);
				// vaccines
				vaccines = (JSONArray) result.get("Vaccinations");
				for (int v = 0; v < vaccines.size(); ++v) {
					JSONObject vaccine = (JSONObject) vaccines.get(v);
					String vac = (String) vaccine.get("category");
					String desc = (String) vaccine.get("description");
					HashMap<String, String> tempVac = new HashMap<String, String>();
					tempVac.put("category", vac);
					tempVac.put("description", desc);
					vaccinationList.add(tempVac);

				}
				// attractions
				attractions = (JSONArray) result.get("Attractions");
				for (int a = 0; a < attractions.size(); ++a) {
					JSONObject attraction = (JSONObject) attractions.get(a);
					String name = (String) attraction.get("name");
					String add = (String) attraction.get("formatted_address");
					String index = Long.toString((Long) attraction.get("index"));
					String rate;
					if (attraction.get("rating").getClass().getName().contains("Long")) {
						rate = Long.toString((Long) attraction.get("rating"));
					} else {
						rate = Double.toString((Double) attraction.get("rating"));
					}

					String types = (String) attraction.get("types").toString();
					HashMap<String, String> attractMap = new HashMap<String, String>();
					attractMap.put("name", name);
					attractMap.put("formatted_address", add);
					attractMap.put("rating", rate);
					attractMap.put("types", types);
					attractMap.put("index", index);
					attractionList.add(attractMap);
				}
				// hotels
				JSONObject hotel = (JSONObject) result.get("hotel");
				String h_name = (String) hotel.get("name");
				String h_lat = (String) hotel.get("latitude");
				String h_lng = (String) hotel.get("longitude");
				String h_rank = Long.toString((Long) hotel.get("ranking"));
				String h_room_price = Long.toString((Long) hotel.get("room_price"));
				String h_id = Long.toString((Long) hotel.get("id"));
				String h_low_rate = (String) hotel.get("latitude");

				HashMap<String, String> hotelMap = new HashMap<String, String>();
				hotelMap.put("name", h_name);
				hotelMap.put("latitude", h_lat);
				hotelMap.put("longitude", h_lng);
				hotelMap.put("ranking", h_rank);
				hotelMap.put("room_price", h_room_price);
				hotelMap.put("id", h_id);
				hotelMap.put("low_rate", h_low_rate);
				hotelList.add(hotelMap);
				// flights
				JSONObject flight = (JSONObject) result.get("flight");
				JSONArray flightInfoArray = (JSONArray) flight.get("flight_info");
				
				JSONObject price = (JSONObject)flight.get("price");
				
				flightPrice = price.get("Price").toString();
				
				JSONObject outBound = (JSONObject) flightInfoArray.get(0);
				outBDuration = outBound.get("duration").toString();
				JSONArray outBoundTransits = (JSONArray) outBound.get("info");
				// flightMap.put("count",
				// Integer.toString(outBoundTransits.size()));
				for (int ft = 0; ft < outBoundTransits.size(); ft++) {
					JSONObject transits = (JSONObject) outBoundTransits.get(ft);
					HashMap<String, String> outBoundTransit = new HashMap<String, String>();
					outBoundTransit.put("arrival_time", transits.get("arrivalTime").toString());
					outBoundTransit.put("departure_time", transits.get("departureTime").toString());
					outBoundTransit.put("destination", transits.get("destination").toString());
					outBoundTransit.put("origin", transits.get("origin").toString());

					outboundList.add(outBoundTransit);
				}

				JSONObject inBound = (JSONObject) flightInfoArray.get(1);
				inBDuration = inBound.get("duration").toString();
				JSONArray inBoundTransits = (JSONArray) inBound.get("info");
				// flightMap
				// .put("count", Integer.toString(inBoundTransits.size()));
				for (int ft = 0; ft < inBoundTransits.size(); ft++) {
					JSONObject transits = (JSONObject) inBoundTransits.get(ft);
					HashMap<String, String> inBoundTransitMap = new HashMap<String, String>();
					inBoundTransitMap.put("arrival_time", transits.get("arrivalTime").toString());
					inBoundTransitMap.put("departure_time", transits.get("departureTime").toString());
					inBoundTransitMap.put("destination", transits.get("destination").toString());
					inBoundTransitMap.put("origin", transits.get("origin").toString());

					inboundList.add(inBoundTransitMap);
				}
				if (i == 0) {
					recursive(object, "start");
					hotelList.clear();
					vaccinationList.clear();
					attractionList.clear();
					inboundList.clear();
					outboundList.clear();
				} else {
					// add more objects
					SSWAPObject sswapObject = null;
					sswapObject = assignObject(subject);
					
					//System.out.println("Test inboundList count: " + inboundList.size());
					//System.out.println("Test outBoundList count: " + outboundList.size());
					
					SSWAPIndividual inbdFlight = rigGraph.createIndividual();
					inbdFlight.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Inbound")));
					//addInBoundFlight(inbdFlight, inboundList);
					inbdFlight.addProperty(FlightDurationPredicate, inBDuration);
					
					SSWAPIndividual obdFlight = rigGraph.createIndividual();
					obdFlight.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Outbound")));
					//addOutBoundFlight(obdFlight, outboundList);
					obdFlight.addProperty(FlightDurationPredicate, outBDuration);
					
					SSWAPIndividual indFlight = rigGraph.createIndividual();
					indFlight.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Flight")));
										
					SSWAPPredicate outbPredicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#hasOutbound"));
					//indFlight.addProperty(outbPredicate, obdFlight);
					indFlight.addProperty(outbPredicate, addOutBoundFlight());
					
					SSWAPPredicate inbPredicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#hasInbound"));
					indFlight.addProperty(inbPredicate, addInBoundFlight());
					
					indFlight.addProperty(FlightPricePredicate, flightPrice);

					SSWAPIndividual ind = rigGraph.createIndividual();
					ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Package")));
					SSWAPPredicate predicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#hasPackage"));
					SSWAPPredicate predicateF = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#hasFlight"));
					ind.addProperty(predicateF, indFlight);
					addHotel(ind, hotelList);
					addAttraction(ind, attractionList, 0);
					addVaccination(ind, vaccinationList, 0);
					sswapObject.addProperty(predicate, ind);
					subject.addObject(sswapObject);
					hotelList.clear();
					vaccinationList.clear();
					attractionList.clear();
					inboundList.clear();
					outboundList.clear();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addVaccination(SSWAPIndividual object, ArrayList<HashMap<String, String>> vaccinationList, int from)
			throws URISyntaxException {
		for (int v = from; v < vaccinationList.size(); v++) {
			SSWAPIndividual ind = rigGraph.createIndividual();
			ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Vaccination")));
			ind.addProperty(VCpredicate, (String) vaccinationList.get(v).get("category"));
			ind.addProperty(VDpredicate, (String) vaccinationList.get(v).get("description"));
			SSWAPPredicate predicate = rigGraph
					.getPredicate(new URI("http://localhost:8080/travel-ontology#hasVaccination"));
			object.addProperty(predicate, ind);
		}
	}

	private void addAttraction(SSWAPIndividual object, ArrayList<HashMap<String, String>> attractionList, int from)
			throws URISyntaxException {
		for (int a = from; a < attractionList.size(); ++a) {
			SSWAPIndividual ind = rigGraph.createIndividual();
			ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Attraction")));
			ind.addProperty(ARpredicate, (String) attractionList.get(a).get("rating"));
			ind.addProperty(ATpredicate, (String) attractionList.get(a).get("types"));
			ind.addProperty(ANpredicate, (String) attractionList.get(a).get("name"));
			ind.addProperty(AIpredicate, (String) attractionList.get(a).get("index"));
			ind.addProperty(AFpredicate, (String) attractionList.get(a).get("formatted_address"));
			SSWAPPredicate predicate = rigGraph
					.getPredicate(new URI("http://localhost:8080/travel-ontology#hasAttraction"));
			object.addProperty(predicate, ind);
		}
	}

	private void addHotel(SSWAPIndividual object, ArrayList<HashMap<String, String>> hotelList)
			throws URISyntaxException {

		SSWAPIndividual ind = rigGraph.createIndividual();
		ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Hotel")));
		ind.addProperty(HRpredicate, (String) hotelList.get(0).get("ranking"));
		ind.addProperty(HIpredicate, (String) hotelList.get(0).get("id"));
		ind.addProperty(HNpredicate, (String) hotelList.get(0).get("name"));
		ind.addProperty(HLngpredicate, (String) hotelList.get(0).get("longitude"));
		ind.addProperty(HLowpredicate, (String) hotelList.get(0).get("low_rate"));
		ind.addProperty(HLatpredicate, (String) hotelList.get(0).get("latitude"));
		ind.addProperty(HPpredicate, (String) hotelList.get(0).get("room_price"));
		SSWAPPredicate predicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#hasHotel"));
		object.addProperty(predicate, ind);

	}

	private SSWAPIndividual addOutBoundFlight()
			throws URISyntaxException {

		SSWAPIndividual ind = rigGraph.createIndividual();
		ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Outbound")));
		
		for(int ob=0;ob<outboundList.size();ob++){
			SSWAPIndividual legInd = rigGraph.createIndividual();
			legInd.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#FlightLeg")));
			
			legInd.addProperty(FODTpredicate, (String) outboundList.get(ob).get("departure_time"));
			legInd.addProperty(FOATpredicate, (String) outboundList.get(ob).get("arrival_time"));
			legInd.addProperty(FOOpredicate, (String) outboundList.get(ob).get("origin"));
			legInd.addProperty(FODpredicate, (String) outboundList.get(ob).get("destination"));
			SSWAPPredicate predicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#hasFlightLeg"));
			
			ind.addProperty(predicate, legInd);
		}
		return ind;
	}

	private SSWAPIndividual addInBoundFlight()
			throws URISyntaxException {

		SSWAPIndividual ind = rigGraph.createIndividual();
		ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Inbound")));
		
		for(int ib=0;ib<inboundList.size();ib++){
			SSWAPIndividual legInd = rigGraph.createIndividual();
			legInd.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#FlightLeg")));
			
			legInd.addProperty(FIDTpredicate, (String) inboundList.get(ib).get("departure_time"));
			legInd.addProperty(FIATpredicate, (String) inboundList.get(ib).get("arrival_time"));
			legInd.addProperty(FIOpredicate, (String) inboundList.get(ib).get("origin"));
			legInd.addProperty(FIDpredicate, (String) inboundList.get(ib).get("destination"));
			SSWAPPredicate predicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#hasFlightLeg"));
			
			ind.addProperty(predicate, legInd);
		}
		return ind;

	}

	private void recursive(SSWAPIndividual sswapIndividual, String indName) {
		Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();

		while (iteratorProperties.hasNext()) {

			SSWAPProperty property = iteratorProperties.next();
			SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());
			
			if (predicate.isObjectPredicate()) {
				  
				System.out.println("PROPERTY IS INDIVIDUAL:------->: " + indName);
				SSWAPIndividual ind = property.getValue().asIndividual();
				indName = getStrName(property.getURI()).toLowerCase();
								
				if (indName.equals("hasvaccination")) {
					
					System.out.println("------->: " + indName);
					for(int v =0;v<vaccinationList.size();v++){
						if(v==0){
							sswapIndividual.setProperty(predicate, setVaccination(ind,v));
						}else{
							sswapIndividual.addProperty(predicate, setVaccination(ind,v));
						}
					}
					
					
				} else if (indName.equals("hasattraction")) {
					try{
					
						System.out.println("------->: " + indName);
						for (int a = 0; a < attractionList.size(); a++) {
							if(a==0){
								sswapIndividual.setProperty(predicate, setAtraction(ind,a));
							}else{
								sswapIndividual.addProperty(predicate, setAtraction(ind,a));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}else if(indName.equals("hashotel")){
					
					System.out.println("------->: " + indName);
					sswapIndividual.setProperty(predicate, setHotel(ind));
					
					
				}else if(indName.equals("hasflight")){
					
					System.out.println("------->: " + indName);
						
					SSWAPIndividual flightInd = null;
					Iterator<SSWAPProperty> iteratorPropertiesFlight = ind.getProperties().iterator();
					
					while (iteratorPropertiesFlight.hasNext()) {
						
						SSWAPProperty propertyFlight = iteratorPropertiesFlight.next();
						SSWAPPredicate predicateFlight = rigGraph.getPredicate(propertyFlight.getURI());
						 flightInd = propertyFlight.getValue().asIndividual();
						indName = getStrName(propertyFlight.getURI()).toLowerCase();
										
							 if(indName.equals("hasoutbound")){
									 ind.setProperty(predicateFlight, setOutBound(flightInd));
								}
							 else if(indName.equals("hasinbound")){
									ind.setProperty(predicateFlight, setInBound(flightInd));
								}
							else if(indName.equals("flight_price")){
									
									FlightPricePredicate = predicateFlight;
									ind.setProperty(FlightPricePredicate, flightPrice);
									
								}
							 
					}
										
					sswapIndividual.setProperty(predicate, ind);	
									
				}
				System.out.println("indName: " + indName);

				System.out.println("<---- END OF Property value is Individual");
				if(indName.equals("haspackage")){
					recursive(ind, indName);
				}
			}
		}
	}

	private SSWAPIndividual setAtraction(SSWAPIndividual sswapIndividual,int a)throws URISyntaxException {
		
		if(a==0){
		//		SSWAPIndividual tempInd = sswapIndividual
					Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
			while (iteratorProperties.hasNext()) {
				SSWAPProperty property = iteratorProperties.next();
				SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());

				String lookupName = getStrName(property.getURI());
				// SSWAPPredicate predicate =
				// rigGraph.getPredicate(property.getURI());
				if (lookupName.equals("rating")) {
					String value = (String) attractionList.get(a).get("rating");
					sswapIndividual.setProperty(predicate, value);
					ARpredicate = predicate;
					
				}
				if (lookupName.equals("types")) {
					String value = (String) attractionList.get(a).get("types");
					sswapIndividual.setProperty(predicate, value);
					ATpredicate = predicate;
				}
				if (lookupName.equals("name")) {
					String value = (String) attractionList.get(a).get("name");
					sswapIndividual.setProperty(predicate, value);
					ANpredicate = predicate;
				}
				if (lookupName.equals("index")) {
					String value = (String) attractionList.get(a).get("index");
					sswapIndividual.setProperty(predicate, value);
					AIpredicate = predicate;
				}
				if (lookupName.equals("formatted_address")) {
					String value = (String) attractionList.get(a).get("formatted_address");
					sswapIndividual.setProperty(predicate, value);
					AFpredicate = predicate;
				}
				}
				return sswapIndividual;
			}
		else{

				SSWAPIndividual ind = rigGraph.createIndividual();
				URI IndUri = sswapIndividual.getURI();
				ind.addType(rigGraph.getType(IndUri));
				ind.addProperty(ARpredicate, (String) attractionList.get(a).get("rating"));
				ind.addProperty(ATpredicate, (String) attractionList.get(a).get("types"));
				ind.addProperty(ANpredicate, (String) attractionList.get(a).get("name"));
				ind.addProperty(AIpredicate, (String) attractionList.get(a).get("index"));
				ind.addProperty(AFpredicate, (String) attractionList.get(a).get("formatted_address"));
				return ind;
			}
	}
	private SSWAPIndividual setHotel(SSWAPIndividual sswapIndividual) {
		Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
		while (iteratorProperties.hasNext()) {
		SSWAPProperty property = iteratorProperties.next();
		SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());
		String lookupName = getStrName(property.getURI());
		predicate = rigGraph.getPredicate(property.getURI());
		if(lookupName.equals("ranking")){
			 String value=(String) hotelList.get(0).get("ranking");
             sswapIndividual.setProperty(predicate, value);
             HRpredicate=predicate;
		}
		if(lookupName.equals("id")){
			 String value=(String) hotelList.get(0).get("id");
             sswapIndividual.setProperty(predicate, value);
             HIpredicate=predicate;
		}
		if(lookupName.equals("name")){
			 String value=(String) hotelList.get(0).get("name");
             sswapIndividual.setProperty(predicate, value);
             HNpredicate=predicate;
		}
		if(lookupName.equals("longitude")){
			 String value=(String) hotelList.get(0).get("longitude");
             sswapIndividual.setProperty(predicate, value);
             HLngpredicate=predicate;
		}
		if(lookupName.equals("low_rate")){
			 String value=(String) hotelList.get(0).get("low_rate");
			 System.out.println("hotel_low_rate: "+value);
             sswapIndividual.setProperty(predicate, value);
             HLowpredicate=predicate;
		}
		if(lookupName.equals("latitude")){
			 String value=(String) hotelList.get(0).get("latitude");
             sswapIndividual.setProperty(predicate, value);
             HLatpredicate=predicate;
		}
		if(lookupName.equals("room_price")){
			 String value=(String) hotelList.get(0).get("room_price");
             sswapIndividual.setProperty(predicate, value);
             HPpredicate=predicate;
		}
		}
		return sswapIndividual;
	}
	
	private SSWAPIndividual setOutBound(SSWAPIndividual sswapIndividual){
		
			Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
			SSWAPPredicate predicate;
			while (iteratorProperties.hasNext()) {
				SSWAPProperty property = iteratorProperties.next();
				predicate = rigGraph.getPredicate(property.getURI());
				String lookupName = getStrName(property.getURI());
				predicate = rigGraph.getPredicate(property.getURI());
				if(lookupName.equals("duration")){
					 sswapIndividual.setProperty(predicate, outBDuration);
					 FlightDurationPredicate = predicate;
				}
				else{
					FlightLegPredicate = predicate;
					SSWAPIndividual legInd = property.getValue().asIndividual();
					FlightLegUri = legInd.getURI();
					
					int ob = 0;
					Iterator<SSWAPProperty> legIteratorProperties = legInd.getProperties().iterator();
					while (legIteratorProperties.hasNext()) {
						SSWAPProperty legProperty = legIteratorProperties.next();
						SSWAPPredicate legPredicate = rigGraph.getPredicate(legProperty.getURI());
						String legLookupName = getStrName(legProperty.getURI());
				
							 if(legLookupName.equals("departure_time")){
								 String value=(String) outboundList.get(ob).get("departure_time");
								 legInd.setProperty(legPredicate, value);
				                 FODTpredicate=legPredicate;
							}
							else if(legLookupName.equals("arrival_time")){
								 String value=(String) outboundList.get(ob).get("arrival_time");
								 legInd.setProperty(legPredicate, value);
				                 FOATpredicate=legPredicate;
							}
							else if(legLookupName.equals("origin")){
								 String value=(String) outboundList.get(ob).get("origin");
								//String value ="K E B E T E *->";
								legInd.setProperty(legPredicate, value);
								 FOOpredicate=legPredicate;
							}
							else if(legLookupName.equals("destination")){
								 String value=(String) outboundList.get(ob).get("destination");
								 legInd.setProperty(legPredicate, value);
					             FODpredicate=legPredicate;
							}
					
				}
			sswapIndividual.setProperty(predicate, legInd);
			for(ob=1;ob<outboundList.size();ob++){
									
					legInd = rigGraph.createIndividual();
					legInd.addType(rigGraph.getType(FlightLegUri));
					
					legInd.addProperty(FODTpredicate, (String) outboundList.get(ob).get("departure_time"));
					legInd.addProperty(FOATpredicate, (String) outboundList.get(ob).get("arrival_time"));
					legInd.addProperty(FOOpredicate, (String) outboundList.get(ob).get("origin"));
					legInd.addProperty(FODpredicate, (String) outboundList.get(ob).get("destination"));
					
					sswapIndividual.addProperty(FlightLegPredicate, legInd);
					
									
					}
			
	}}
			return sswapIndividual;
}
	private SSWAPIndividual setInBound(SSWAPIndividual sswapIndividual){
	 
		
		Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
			SSWAPPredicate predicate;
			while (iteratorProperties.hasNext()) {
				SSWAPProperty property = iteratorProperties.next();
				predicate = rigGraph.getPredicate(property.getURI());
			String lookupName = getStrName(property.getURI());
			predicate = rigGraph.getPredicate(property.getURI());
			if(lookupName.equals("duration")){
				 sswapIndividual.setProperty(predicate, inBDuration);
				 FlightDurationPredicate = predicate;
			}
			else{
				int ib = 0;
				FlightLegPredicate = predicate;
				SSWAPIndividual legInd = property.getValue().asIndividual();
				Iterator<SSWAPProperty> legIteratorProperties = legInd.getProperties().iterator();
				FlightLegUri = legInd.getURI();
				while (legIteratorProperties.hasNext()) {
					SSWAPProperty legProperty = legIteratorProperties.next();
					SSWAPPredicate legPredicate = rigGraph.getPredicate(legProperty.getURI());
					String legLookupName = getStrName(legProperty.getURI());
				
					if(legLookupName.equals("departure_time")){
						 String value=(String) inboundList.get(ib).get("departure_time");
						 legInd.setProperty(legPredicate, value);
				         FIDTpredicate=legPredicate;
					}
					if(legLookupName.equals("arrival_time")){
						 String value=(String) inboundList.get(ib).get("arrival_time");
						 legInd.setProperty(legPredicate, value);
				         FIATpredicate=legPredicate;
					}
					if(legLookupName.equals("origin")){
						 String value=(String) inboundList.get(ib).get("origin");
						 legInd.setProperty(legPredicate, value);
				         FIOpredicate=legPredicate;
					}
					if(legLookupName.equals("destination")){
						 String value=(String) inboundList.get(ib).get("destination");
						 legInd.setProperty(legPredicate, value);
				         FIDpredicate=legPredicate;
					}	
				}
				sswapIndividual.setProperty(predicate, legInd);
				
			for( ib=1;ib<inboundList.size();ib++){
						
					legInd = rigGraph.createIndividual();
					legInd.addType(rigGraph.getType(FlightLegUri));
					
					legInd.addProperty(FIDTpredicate, (String) inboundList.get(ib).get("departure_time"));
					legInd.addProperty(FIATpredicate, (String) inboundList.get(ib).get("arrival_time"));
					legInd.addProperty(FIOpredicate, (String) inboundList.get(ib).get("origin"));
					legInd.addProperty(FIDpredicate, (String) inboundList.get(ib).get("destination"));
					
					sswapIndividual.addProperty(FlightLegPredicate, legInd);
			
				}
			}
		}
			return sswapIndividual;
	}
	private SSWAPIndividual setVaccination(SSWAPIndividual sswapIndividual,int v) {
		if(v==0){
			Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
			while (iteratorProperties.hasNext()) {
				SSWAPProperty property = iteratorProperties.next();
				SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());

				String lookupName = getStrName(property.getURI());
				// for(int v=0;v<vaccinationList.size();v++){
				// SSWAPPredicate predicate =
				// rigGraph.getPredicate(property.getURI());
				if (lookupName.equals("category")) {
					String value = (String) vaccinationList.get(v).get("category");
					sswapIndividual.setProperty(predicate, value);
					VCpredicate = predicate;
				} else {
					String value = (String) vaccinationList.get(v).get("description");
					sswapIndividual.setProperty(predicate, value);
					VDpredicate = predicate;
				}
			}
			return sswapIndividual;
	}else{
		SSWAPIndividual ind = rigGraph.createIndividual();
		URI IndUri = sswapIndividual.getURI();
		ind.addType(rigGraph.getType(IndUri));
		
		ind.addProperty(VCpredicate, (String) vaccinationList.get(v).get("category"));
		ind.addProperty(VDpredicate, (String) vaccinationList.get(v).get("description"));
		
		return ind;
	}
}
	

	private String getStrName(URI uri) {
		String[] a = uri.toString().split("#", 2);

		System.out.println("predicate: " + a[1]);
		return a[1];
	}

}
