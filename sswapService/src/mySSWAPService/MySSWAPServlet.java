package mySSWAPService;


import info.sswap.api.servlet.SimpleSSWAPServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;




public class MySSWAPServlet extends SimpleSSWAPServlet{
private static final long serialVersionUID= 1L;
private ServletContext servletContext;
private static String rootPath;

@Override
public void init(ServletConfig servletConfig) throws ServletException{
// always do this
super.init(servletConfig);
servletContext = servletConfig.getServletContext();
rootPath = servletContext.getRealPath("/");

// do anything else here that needs to be done once, on servlet load
}
public static String path(){
	
	return rootPath;
}

@SuppressWarnings("unchecked")
@Override
public <T> Class<T> getServiceClass() {
return (Class<T>) SSWAPService.class;
}
}