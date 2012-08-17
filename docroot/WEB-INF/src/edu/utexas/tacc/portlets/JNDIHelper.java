package edu.utexas.tacc.portlets;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/*
 * class contains JNDI helper methods
 */
public class JNDIHelper {
    
    public static String USERNAME_OVERRIDE = "java:comp/env/username.override";
    
    /*
     * get the JNDI Context 
     *
     * @return JNDI Context object
     */
    public Context getContext() {
	Context context = null;
	try {
	    context = new InitialContext();
	    if (context == null) {
		throw new Exception ("Uh oh -- no JNDI context!");
	    }

	} catch(NamingException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	} 
	return context;
    }
    
    /*
     * get a JNDI environment var
     *
     * @param var JNDI variable name to lookup
     * @return String value of specified variable name
     */
    public String getVariable(String var) {
	String value = null;
	try {
	    value = (String)getContext().lookup(var);
	} catch (NamingException e) {
	    e.printStackTrace();
	}
	
	return value;
    }
    
}
