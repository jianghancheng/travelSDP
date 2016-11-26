package myMediatorService;
import info.sswap.api.model.RDG;
import info.sswap.api.model.SSWAP;
import info.sswap.api.model.SSWAPGraph;
import info.sswap.api.model.SSWAPPredicate;
import info.sswap.api.model.SSWAPProperty;
import info.sswap.api.model.SSWAPResource;
import info.sswap.api.model.SSWAPSubject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


/**
 *
 * @author Administrator
 */
public class mediatorServer extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 23456787890L;

	public mediatorServer() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		int i=0;
		ArrayList p = new ArrayList();
		String serviceUrl = req.getParameter("url");
		
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
		System.out.println("predicate: "+predicate.getURI());
		String[] a  = predicate.getURI().toString().split("#", 2);
//		String[] b  =a[1].toString().split("/", 5);
		//System.out.println("predicate"+b[4]);
		p.add(a[1]);
		i++;
		
		}
		 resp.setContentType("text/html");
		
		 resp.setHeader("Access-Control-Allow-Origin", "*"); 
		 PrintWriter out = resp.getWriter();
		 String[] predi = new String[i];
		 
		 
		String input = "";
		for(int j=0;j<i;j++){
			input+=p.get(j)+ "<input type=\"text\" value=\"haha\" name=\""+p.get(j)+ "\"/><br>" ;}
			out.println( "<html>" +
			         "<body>" +
			         "<form action=\"Rig\" method=\"POST\">" +
			         input+
			          "url"+"<input type=\"text\" name=\"URL\" value=\"http://localhost:8080/sswapService/getService/\"/><br>"+
			          "<input type=\"submit\" value=\"submit\" />" +
			         
			         "</form>" +
			         "</body>" +
			         "</html>");
		

	}

}