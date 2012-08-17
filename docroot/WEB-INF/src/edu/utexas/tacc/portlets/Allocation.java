package edu.utexas.tacc.portlets;

/**
 * Allocation.java 
 * A datastructure to hold TG Allocation information. Sorts by Start Date
 * 
 * Created: Tues Feb 20 2008
 * Modified: Tues July 7 2009
 * @author mock@tacc.utexas.edu, praveen@tacc.utexas.edu
 * @version 1.0
 */

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.DateTime;

public class Allocation implements Comparable<Allocation> {

	private Date   startDate;
	private Date   endDate;
	private Date   now = new Date();
	private int    requestID;
	private int    piID;
	private String resource;
	private String baseAllocation;
	private String remainingAllocation;
	private String usedAllocation;
	private String type;
	private String state;
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private User[] users = null;
	private User[] deadUsers = null;
	private User[] allUsers = null;
	private HashMap<Integer, User> userHash = new HashMap<Integer, User>();
	private HashMap<Integer, Integer> popsPersonIds = new HashMap<Integer, Integer>();
	private HashMap<Integer, String> allEmailHash = new HashMap<Integer, String>();

	private static final NumberFormat df = new DecimalFormat("###,##0.0");
	private static final NumberFormat commasNoDecimal = new DecimalFormat("###,###");

	public int compareTo(Allocation o) {	
		Date d = o.getStartDate();
		//return this.startDate.compareTo(d); //this does oldest start date first
		return d.compareTo(this.startDate);   //this does newest start date first
	}

	//checking to see if the start date is in the future.
	public boolean isPending() {
		if(startDate.after(this.now)) {
			return true;
		}
		return false;
	}

	public boolean isExpired() {
		if(endDate.before(this.now)) {
			return true;
		}
		return false;
	}

	//gets the number of weeks into the allocation time at the current time
	public int getWeeksBurnt() {
		DateTime start = new DateTime(getStartDate());
		DateTime now   = new DateTime();
		DateTime tNow  = now;

		int weeksBurnt = 0;
		while(tNow.isAfter(start)) {
			tNow = tNow.minusWeeks(1);
			weeksBurnt++;
		}
		return weeksBurnt;
	}

	//total number of weeks in the allocation lifetime. this will usually be a year, but you never know.
	public int getWeeksTotal() {

		DateTime start = new DateTime(getStartDate());
		DateTime end   = new DateTime(getEndDate());

		int totalWeeks = 0;
		while(end.isAfter(start)) {
			end = end.minusWeeks(1);
			totalWeeks++;
		}
		return totalWeeks;
	}
	
	public int getDaysLeft() {
		DateTime end   = new DateTime(getEndDate());
		int days = 0;
		
		while(end.isAfterNow()) {
			days++;
			end = end.minusDays(1);
		}
		return days;
	}

	public double getBurnRate() {
		
		double SUs = getOverallUsedAllocation();
		double weeks = (double) getWeeksBurnt(); if(weeks == 0) { weeks = 1;}
		double rate = SUs / weeks;
		return rate;
	}
	
	public String getBurnRateFormatted() {
		return df.format(getBurnRate());
	}
	
	public double getIdealBurnRate() {
		double SUs = Double.parseDouble(getBaseAllocation());
		double weeks = (double) getWeeksTotal(); if(weeks == 0) { weeks = 1;}
		double rate = SUs / weeks;
		return rate;
	}
	
	public String getIdealBurnRateFormatted() {
		return commasNoDecimal.format(getIdealBurnRate());
	}
	
	public double getBurnRateRatio() {
		return getBurnRate() / getIdealBurnRate();
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public String getFormattedStartDate() {
		return formatter.format(startDate);
	}
	public String getFormattedEndDate() {
		return formatter.format(endDate);
	}

	public void setStartDate(String startDate) {
		try {
			this.startDate = formatter.parse(startDate);
		} catch (ParseException pe) {
			//todo something useful here
		}
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		try {
			this.endDate = formatter.parse(endDate);
		} catch (ParseException pe) {
			//todo - something useful here
		}
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getBaseAllocation() {
		return baseAllocation;
	}

	public String getBaseAllocationFormatted() {
		return NumberFormat.getIntegerInstance().format(Double.parseDouble(baseAllocation));
	}

	public void setBaseAllocation(String baseAllocation) {
		this.baseAllocation = baseAllocation;
	}

	public String getRemainingAllocation() {
		return remainingAllocation;
	}

	public String getRemainingAllocationFormatted() {
		return NumberFormat.getIntegerInstance().format(Double.parseDouble(remainingAllocation));
	}

	public void setRemainingAllocation(String remainingAllocation) {
		this.remainingAllocation = remainingAllocation;
	}

	public boolean isOverdrawn() {
		return Double.parseDouble(getRemainingAllocation()) < 0d;
	}

	/* 
	 * This is this user's individual used allocation amount
	 */
	public String getUsedAllocation() {
		return usedAllocation;
	}

	/* 
	 * This is this user's individual used allocation amount, formatted
	 */
	public String getUsedAllocationFormatted() {
		return df.format(Double.parseDouble(usedAllocation));
	}
	
	/*
	 * returns the amount of allocation used by all users on an allocation
	 */
	public double getOverallUsedAllocation () {
		return Double.parseDouble(getBaseAllocation()) - Double.parseDouble(getRemainingAllocation());
	}

	public void setUsedAllocation(String usedAllocation) {
		this.usedAllocation = usedAllocation;
	}

	public int getPercentRemainInt() {
		if (Double.parseDouble(getBaseAllocation()) > 0) {
			double rem = Double.parseDouble(getRemainingAllocation()) / Double.parseDouble(getBaseAllocation());
			return (int)(rem*100);
		} else {
			return 0;
		}		
	}

	public String getPercentRemain() {
		if (Double.parseDouble(getBaseAllocation()) > 0) {
			double rem = Double.parseDouble(getRemainingAllocation()) / Double.parseDouble(getBaseAllocation());
			return NumberFormat.getPercentInstance().format(rem < 0 ? 0 : rem);
		} else {
			return NumberFormat.getPercentInstance().format(0);
		}
	}

	public String getPercentUsed() {
		return NumberFormat.getPercentInstance().format(Double.parseDouble(getUsedAllocation()) / Double.parseDouble(getBaseAllocation()));
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DateFormat getFormatter() {
		return formatter;
	}

	public void setFormatter(DateFormat formatter) {
		this.formatter = formatter;
	}

	public User[] getUsers() {
		if(users == null) {
			return new User[0];
		}
		this.setUserHash(users);
		return users;
	}

	public void setUsers(User[] users) {
		this.users = users;
	}

	public User[] getDeadUsers() {
		if(deadUsers == null) {
			return new User[0];
		}
		return deadUsers;
	}

	public void setDeadUsers(User[] users) {
		this.deadUsers = users;
	}

	public User[] getAllUsers(){
		if(allUsers == null){
			return new User[0];
		}
		this.setAllEmailHash(allUsers);
		return allUsers;
	}

	public void setAllUsers(User[] users){
		this.allUsers = users;
	}

	//Hashes of users based
	public void setUserHash(User[] users) {
		for (int i=0; i < users.length; i++) {
			User u = users[i];
			int personId = u.getPersonID();
			userHash.put(new Integer(personId), u);
		}
	}

	//Hashes of allusers 
	public void setAllEmailHash(User[] users){
		for(int i=0; i< users.length; i++){
			User u = users[i];
			int personId = u.getPersonID();
			String email = u.getEmail();
			allEmailHash.put(personId, email);
		}
	}

	//Returns all user hash
	public HashMap<Integer, String> getAllEmailHash(){
		return this.allEmailHash;
	}

	public HashMap<Integer, User> getUserHash() {
		return this.userHash;
	}
	public void setPopsPersonIds(HashMap<Integer, Integer> popsIds){
		this.popsPersonIds = popsIds;
	}
	public HashMap<Integer, Integer> getPopsPersonIds(){
		return this.popsPersonIds;
	}
	public void setPiID(int piID){
		this.piID = piID;
	}
	public int getPiID(){
		return this.piID;
	}
}
