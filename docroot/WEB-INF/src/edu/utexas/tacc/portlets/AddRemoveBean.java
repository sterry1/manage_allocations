package edu.utexas.tacc.portlets;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class AddRemoveBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String resource;
	String origResource;
	String chargeNumber;
	List<String> userList;

	public AddRemoveBean(){
		this.resource = "";
		this.chargeNumber = "";
		this.userList = new LinkedList<String>();
	}
	public AddRemoveBean(String resource, String chargeNum, List<String> userList) {
		this.resource = resource;
		this.chargeNumber = chargeNum;
		this.userList = userList;
	}

	public void setUserList(List<String> userList) {
		this.userList = userList;
	}
	public List<String> getUserList() {
		return this.userList;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getResource() {
		return this.resource;
	}

	public void setOrigResource(String origResource) {
		this.origResource = origResource;
	}
	public String getOrigResource() {
		return this.origResource;
	}


	public void setChargeNumber(String chargeNumber) {
		this.chargeNumber = chargeNumber;
	}
	public String getChargeNumber() {
		return this.chargeNumber;
	}
}
