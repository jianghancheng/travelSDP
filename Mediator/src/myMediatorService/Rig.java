package myMediatorService;

import info.sswap.api.http.HTTPProvider;
import info.sswap.api.model.RDG;
import info.sswap.api.model.RIG;
import info.sswap.api.model.RRG;
import info.sswap.api.model.SSWAP;
import info.sswap.api.model.SSWAPGraph;
import info.sswap.api.model.SSWAPIndividual;
import info.sswap.api.model.SSWAPObject;
import info.sswap.api.model.SSWAPPredicate;
import info.sswap.api.model.SSWAPProperty;
import info.sswap.api.model.SSWAPResource;
import info.sswap.api.model.SSWAPSubject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Rig extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 23456787890L;

	public Rig() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		int i=0;
		ArrayList p = new ArrayList();
		String serviceUrl = req.getParameter("URL");
		
		System.out.println(serviceUrl);
		CloseableHttpClient client = HttpClients.createDefault();  
        //URI uri = new URI(Url);
        HttpGet getRDG= new HttpGet(serviceUrl);
        HttpResponse responseRDG = null;
		RDG rdg = null;
		try {
			responseRDG = client.execute(getRDG);
		} catch (IOException e) {
			System.out.println("Error executing httpGet: " + e);
		}
		URI uri = null;
		try {
			uri = new URI(serviceUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		rdg = SSWAP.getResourceGraph(responseRDG.getEntity().getContent(),
				RDG.class, uri);

		SSWAPResource resource = rdg.getResource();
		System.out.println("Resourcename: " + resource.getName());
		System.out.println("Resourceonelinedescription: "
				+ resource.getOneLineDescription());
		SSWAPGraph graph = resource.getGraph();
		SSWAPSubject subject = graph.getSubject();
		Iterator<SSWAPProperty> iterator = subject.getProperties().iterator();
		
		
		while (iterator.hasNext()) {
		SSWAPProperty property = iterator.next();
		SSWAPPredicate predicate = rdg.getPredicate(property.getURI());
		//System.out.println("predicate"+predicate.getURI());
		String[] a  = predicate.getURI().toString().split("#", 2);
		
		System.out.println("predicate"+a[1]);
		p.add(a[1]);
		i++;
		
		}
		 resp.setContentType("text/html");
		
		 resp.setHeader("Access-Control-Allow-Origin", "*"); 
		 PrintWriter out = resp.getWriter();
		 int k=0;
//		    for(int j=0;j<i;j++){	
//			System.out.println( p.get(j)+req.getParameter((String) p.get(j)));}
		 Iterator<SSWAPProperty> iteratorinput = subject.getProperties().iterator();
			while (iteratorinput.hasNext()) {
				SSWAPProperty property = iteratorinput.next();
				SSWAPPredicate predicate = rdg.getPredicate(property.getURI());
				//System.out.println("predicate"+predicate.getURI());
				subject.setProperty(predicate, req.getParameter((String) p.get(k)));
				System.out.println("input"+req.getParameter((String) p.get(k)));
				k++;
				
				}
		
			graph.setSubject(subject);
			resource.setGraph(graph);
			RIG rig= resource.getRDG().getRIG();
			HTTPProvider.RRGResponse response = rig.invoke();
			//rrg
			RRG rrg= response.getRRG();
			SSWAPResource resourceRRG = rrg.getResource();
			SSWAPGraph graphRRG = resourceRRG.getGraph();
			SSWAPSubject subjectRRG = graphRRG.getSubject();
			Iterator<SSWAPObject> iteratorObjects=  subjectRRG.getObjects().iterator();
			
			int count = 0;
			while (iteratorObjects.hasNext()) {
			SSWAPObject object = iteratorObjects.next();
			
			recursive(object,count);
			count++;
			System.out.println("\n\n\n");
			}
	}
	private void recursive(SSWAPIndividual sswapIndividual,int count) {
		
		
		Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
		
		while (iteratorProperties.hasNext()) {
			SSWAPProperty property = iteratorProperties.next();
			if (property.getValue().isLiteral()) {
				String lookupName = getStrName(property.getURI());
				String lookupValue = property.getValue().asString();
				System.out.println("value is Literal: Name: " + count +" "+lookupName+" value: "+lookupValue);
				
				} 
			else if (property.getValue().isIndividual()) {
				
				//System.out.println("<----*** Property value is Individual:***---->");
//				System.out.println("Property value is Individual:");
				String lookupName = property.getURI().toString();
//				System.out.println("" + lookupName);
				SSWAPIndividual ind = property.getValue().asIndividual();
				recursive(ind,count);
			}
		}
	}

	

	private String getStrValue(SSWAPIndividual sswapIndividual,
			SSWAPPredicate sswapPredicate) {
		String value = null;
		SSWAPProperty sswapProperty = sswapIndividual
				.getProperty(sswapPredicate);
		if (sswapProperty != null) {
			value = sswapProperty.getValue().asString();
			if (value.isEmpty()) {
				value = null;
			}
		}
		return value;
	}

	private String getStrName(URI uri) {
		String[] a = uri.toString().split("#", 2);
        return a[1];
	}

}