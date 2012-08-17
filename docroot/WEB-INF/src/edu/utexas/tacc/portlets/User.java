package edu.utexas.tacc.portlets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String firstName;
	private String lastName;
	private String username;
	private String usedAllocation;
	private int personID;
	private int popsPersonID;
	private String email = "Not Available";

    private String status;  // added by me
	private Map<String,String> roles = new HashMap<String,String>();
	private Map<String,String> localUsernames = new HashMap<String, String>();
	
	public User(String first, String last, String used, int personID) {
		this.firstName = first;
		this.lastName  = last;
		this.usedAllocation = used;
		this.personID = personID;
	}

	public User(String first, String last, int personID) {
		this.firstName = first;
		this.lastName  = last;
		this.personID = personID;
	}
	
	public User(String first, String last, String used) {
		this.firstName = first;
		this.lastName  = last;
		this.usedAllocation = used;		
	}
	
	public User(String first, String last, String email, String used, int personID) {
		this.firstName = first;
		this.lastName  = last;
		this.email  = email;
		this.usedAllocation = used;
		this.personID = personID;
	}
	
	public User(String first, String last, String email, String used, int personID, int popsPersonID) {
		this.firstName = first;
		this.lastName  = last;
		this.email  = email;
		this.usedAllocation = used;
		this.personID = personID;
		this.popsPersonID = popsPersonID;
	}
	
	public User(String first, String last, String email, String used, int personID, int popsPersonID, String role) {
		this.firstName = first;
		this.lastName  = last;
		this.email  = email;
		this.usedAllocation = used;
		this.personID = personID;
		this.popsPersonID = popsPersonID;
		this.roles.put(role,role);
	}
	
	public User(String first, String last, String username, String email, String used, int personID, int popsPersonID, String role) {
		this.firstName = first;
		this.lastName  = last;
		this.username = username;
		this.email  = email;
		this.usedAllocation = used;
		this.personID = personID;
		this.popsPersonID = popsPersonID;
		this.roles.put(role,role);
	}

    public User(String first, String last, String username, String email, String used, int personID, String role, String status) {
        this.firstName = first;
        this.lastName  = last;
        this.username = username;
        this.email  = email;
        this.usedAllocation = used;
        this.personID = personID;
        this.popsPersonID = popsPersonID;
        this.roles.put(role,role);
        this.status = status;
    }


    public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
	
	public String getFullNameLastFirst() {
		return this.lastName + ", " + this.firstName;
	}
	public String getUsedAllocation() {
		return usedAllocation;
	}
	public void setUsedAllocation(String usedAllocation) {
		this.usedAllocation = usedAllocation;
	}

	public int getPersonID() {
		return personID;
	}

	public void setPersonID(int personID) {
		this.personID = personID;
	}

	public int getPopsPersonID() {
		return popsPersonID;
	}

	public void setPopsPersonID(int popsPersonID) {
		this.popsPersonID = popsPersonID;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String eMail) {
		this.email = eMail;
	}
	
	public List<String> getRoles() {
		return new ArrayList<String>(roles.values());
	}
	
	public void addRole(String role) {
		this.roles.put(role,role);
	}
	
	public Map<String,String> getLocalUsernames() {
		return localUsernames;
	}
	
	public String getLocalUsername(String resourceName) {
		return localUsernames.get(resourceName);
	}
	
	public void setLocalUsername(String resourceName, String localUsername) {
		localUsernames.put(resourceName, localUsername);
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
