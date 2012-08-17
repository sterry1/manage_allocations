package edu.utexas.tacc.portlets;
/**
 * Project.java 
 * A datastructure to hold TG Project and Allocation information. 
 * Sorts by Start Date
 * 
 * Created: Tues Feb 20 2008
 * Updated: Tues June 7, 2011
 * 
 * @author mock@tacc.utexas.edu
 * @version 2.0
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Project implements Comparable {

	private Map<String,Allocation> allocations = new LinkedHashMap<String, Allocation>();
	private String  chargeNumber;
	private String  title;
	private String  grantNumber;
	private boolean isPI;
	private boolean coPI;
	private boolean allocationMgr;
	private int     accountID;
	private int     piId;
	private String  piFirstName;
	private String  piLastName;
	private static  String ACTIVE = "active";
	private static  String INACTIVE = "inactive";
	
	
	public Allocation[] getAllocations() {
		Allocation[] sorted = new Allocation[allocations.size()];
		allocations.values().toArray(sorted);
		Arrays.sort(sorted);
		return sorted;
	}
	
	public Allocation[] getActiveAllocations() {
		List<Allocation> active = new ArrayList<Allocation>();
		for (Allocation a:allocations.values()) {
			if(a.getState().equalsIgnoreCase(ACTIVE)) {
				active.add(a);
			}
		}
		Allocation[] sorted = new Allocation[active.size()];
		active.toArray(sorted);
		return sorted;
	}

	public Allocation[] getActiveNonExpiredAllocations() {
		List<Allocation> active = new ArrayList<Allocation>();
		for (Allocation a:allocations.values()) {
			if(a.getState().equalsIgnoreCase(ACTIVE) && (!a.isExpired())) {
				active.add(a);
			}
		}
		Allocation[] sorted = new Allocation[active.size()];
		active.toArray(sorted);
		return sorted;
	}	
	
	public Allocation[] getInactiveAllocations() {
		List<Allocation> inactive = new ArrayList<Allocation>();
		for (Allocation a:allocations.values()) {
			if (a.getState().equalsIgnoreCase(INACTIVE)) {
				inactive.add(a);
			}
		}
		Allocation[] sorted = new Allocation[inactive.size()];
		inactive.toArray(sorted);
		return sorted;
	}
	
	public void addAllocation(Allocation a) {
		allocations.put(a.getRequestID() + a.getResource(), a);
		
	}
	
	public String getChargeNumber() {
		return chargeNumber;
	}

	public void setChargeNumber(String chargeNumber) {
		this.chargeNumber = chargeNumber;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getGrantNumber() {
		return grantNumber;
	}


	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}

	public void setPiId(int id) {
		this.piId = id;
	}
	
	public int getPiId() {
		return piId;
	}

	public boolean getIsPI() {
		return isPI;
	}


	public void setIsPI(boolean isPI) {
		this.isPI = isPI;
	}
	
	public boolean isCoPI() {
		return coPI;
	}
	
	public void setCoPI(boolean coPI) {
		this.coPI = coPI;
	}
	
	public boolean isAllocationMgr() {
		return allocationMgr;
	}
	
	public void setAllocationMgr(boolean allocationMgr) {
		this.allocationMgr = allocationMgr;
	}


	public int getAccountID() {
		return accountID;
	}


	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

/*
    // todo added by me to stub functionality
    public void setAllocations(Map<String,Allocation> allocations) {
        this.allocations = allocations;
    }
*/

    //sorting done by looking at the first allocation in each project
	public int compareTo(Object o) {
		Project other = (Project)o;
		Allocation[] otherAlloc = other.getAllocations();
		Allocation[] myAlloc    = this.getAllocations();
		
		if (myAlloc.length > 0 && otherAlloc.length > 0) {
			return myAlloc[0].compareTo(otherAlloc[0]);
		} else if (myAlloc.length == 0 && otherAlloc.length == 0) {
			return 0;
		} else if (myAlloc.length > 0 && otherAlloc.length == 0) {
			return 1;
		} else {
			return -1;
		}
	}

	public String getPiFirstName() {
		return piFirstName;
	}

	public void setPiFirstName(String piFirstName) {
		this.piFirstName = piFirstName;
	}

	public String getPiLastName() {
		return piLastName;
	}

	public void setPiLastName(String piLastName) {
		this.piLastName = piLastName;
	}

}
