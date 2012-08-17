package edu.utexas.tacc.portlets;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import edu.utexas.tacc.portlets.beans.SystemBean;
/**
 * Gateway interface
 * 
 * @author ericrobe, mock
 * 
 */
public class PersonGatewayXSEDE implements PersonGateway {

	private Connection conn = null;
	private static final Logger logger = Logger.getLogger(PersonGatewayXSEDE.class);
	
	/*
	 * default constructor
	 */
	public PersonGatewayXSEDE() {
		initConnection();
	}

	/*
	 * takes a DB Connection object and sets this objects connection to it
	 *
	 * @deprecated in favor of a more encapsulated form of constructor.  Use the default constructor instead.
	 */
	public PersonGatewayXSEDE(Connection conn) {
		this.conn = conn;
	}

	/*
	 * get a new DB connection from the JNDI data source.
	 */
	@Override
    public void initConnection() {
		try {
			Context context = new InitialContext();

			// TODO: extract the jndi string to a config file
			DataSource dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/TGCDB-portal");

			if (dataSource == null) {
				throw new Exception("Data source not found!");
			}
			conn = dataSource.getConnection();
		} catch(NamingException e) {
			logger.error("Error initiating connection", e);
		} catch (Exception e) {
			logger.error("Error initiating connection", e);
		}   
	}

	/*
	 * close the connection.  This method should be called after a client
	 * is done with this gateway object.
	 */
	@Override
    public void close() throws SQLException {
		conn.close();
	}
	
	@Override
    public boolean checkUsername(String username) {
		boolean exists = false;
		
		String sql = "SELECT count(a.username) FROM (" +
			"SELECT username FROM portal.tgup_user_info WHERE NOT is_purged " +
			"UNION ALL " +
			"SELECT username FROM portal.people" +
			") AS a " +
			"WHERE a.username = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					exists = true;
				}
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			logger.error("Error", e);
		}
		
		return exists;
	}

	@Override
    public int findPersonId(String username) {
		int personId = 0;
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("SELECT person_id FROM portal.people WHERE username = ?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();

			if(rs.next())
				personId = rs.getInt(1);
			
			rs.close();
			ps.close();

		} catch (SQLException e) {
			logger.error("Error", e);
		} 
		return personId;
	}

	@Override
    public ResultSet findName(int personId) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT first_name, last_name FROM acct.people WHERE person_id = ?");
			ps.setInt(1, personId);
			rs = ps.executeQuery();
		} catch (SQLException e) {
			logger.error("Error", e);
		} 
		return rs;
	}

	/**
	 * Find all projects for this username
	 * 
	 * @param username
	 *            for a person
	 * @return a ResultSet containing the charge number and project title
	 */
	@Override
    public Project[] findProjects(String username) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		List<Project> projects = new ArrayList<Project>();
		try {
			ps = conn.prepareStatement(
					"SELECT DISTINCT " +
					"a.request_id, a.account_id, a.alloc_type, a.project_title, a.alloc_resource_name, " +
					"a.base_allocation, a.remaining_allocation, a.used_allocation, a.first_name, " +
					"a.last_name, a.grant_number, a.charge_number, a.start_date, a.end_date, r.role, " +
					"a.proj_state, a.pi_last_name, a.pi_first_name, a.pi_person_id " +
					"FROM portal.acctv a " +
					"LEFT JOIN portal.account_roles r ON r.account_id = a.account_id AND r.person_id = a.person_id " +
					"WHERE a.person_id = ? AND a.acct_state <> 'dead' " +
					"ORDER BY a.account_id, a.start_date DESC"
				);
			int person_id = findPersonId(username);
			ps.setInt(1, person_id);
			rs = ps.executeQuery();
			Project project = null;
			while(rs.next()) {
				if (project == null || project.getAccountID() != rs.getInt("account_id")) {
					project = new Project();
					project.setGrantNumber(rs.getString("grant_number"));
					project.setTitle(rs.getString("project_title"));
					project.setChargeNumber(rs.getString("charge_number"));
					project.setAccountID(rs.getInt("account_id"));
					project.setPiFirstName(rs.getString("pi_first_name"));
					project.setPiLastName(rs.getString("pi_last_name"));
					project.setPiId(rs.getInt("pi_person_id"));
					projects.add(project);
				}
				String role = rs.getString("role");
				if (role != null) {
					if (role.equals("pi")) {
						project.setIsPI(true);
					} else if (role.equals("co_pi")) {
						project.setCoPI(true);
					} else if (role.equals("allocation_manager")) {
						project.setAllocationMgr(true);
					}
				}
				Allocation a = new Allocation();
				a.setRequestID(rs.getInt("request_id"));
				a.setStartDate(rs.getString("start_date"));
				a.setEndDate(rs.getString("end_date"));
				a.setResource(rs.getString("alloc_resource_name"));
				a.setBaseAllocation(rs.getString("base_allocation"));
				a.setRemainingAllocation(rs.getString("remaining_allocation"));
				a.setUsedAllocation(rs.getString("used_allocation"));
				a.setState(rs.getString("proj_state"));
				a.setType(rs.getString("alloc_type"));
				a.setPiID(project.getPiId());
				project.addAllocation(a);
			}
			rs.close();
		} catch (SQLException e) {
			Logger.getLogger(PersonGatewayXSEDE.class).error("Error looking up projects for user", e);
		}
		
		Project[] parr = new Project[projects.size()];
		parr = projects.toArray(parr);
		Arrays.sort(parr);
		
		return parr;
	}

	/**
	 * Find all system accounts for this personId
	 * 
	 * @param personId TeraGrid person id
	 *            
	 * @return a Map containing all resource names and associated
	 *         usernames for a given portal username
	 */
	@Override
    public SystemBean[] findSystemAccounts(String personId) {

		ResultSet rs = null;
		PreparedStatement ps = null;
		//Map sysAccountsMap = null;
		SystemBean[] sysBeans = null;
		try {
//			long one = System.currentTimeMillis();
			ps = conn.prepareStatement(
					"select username, resource_name " +
					"from portal.sav " +
					"where tg_username = ? and is_active"
				);
			ps.setString(1, personId);
			rs = ps.executeQuery();
//			long two = System.currentTimeMillis();
			//sysAccountsMap = createSystemAccountsDAO(rs);
			sysBeans = createSystemAccountsDAO(rs);
//			long three = System.currentTimeMillis();
			ps.close();
			rs.close();
//			System.out.println(new java.util.Date() + " - PersonGateway#findSystemAccounts (Query only): " + (two - one));
//			System.out.println(new java.util.Date() + " - PersonGateway#findSystemAccounts: " + (three - one));
		} catch (SQLException e) {
			logger.error("Error", e);
		} finally {
			try {
				rs.close();
			} catch (Exception e) { /* already closed */
			}
			try {
				ps.close();
			} catch (Exception e) { /* already closed */
			}
		}

		//return sysAccountsMap;
		return sysBeans;
	}
	
	@Override
    public SystemBean findSystemAccount(String personId, String resourceName) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT username, resource_name FROM portal.sav WHERE tg_username = ? and resource_name = ? and is_active");
			ps.setString(1, personId);
			ps.setString(2, resourceName);
			ResultSet rs = ps.executeQuery();
			SystemBean[] systemBean = createSystemAccountsDAO(rs);
			rs.close();
			ps.close();
			return systemBean[0];
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}
	
	/*
	 * I know, I know, generally bad practice to rely on functional side effects,
	 * but that's what we're doing here - mrhanlon :)
	 */
	@Override
    public void populateUsers(final Project p, final Allocation a) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					"SELECT a.first_name, a.last_name, ui.username, a.person_id, a.request_id, a.alloc_resource_name, sav.username AS local_username, a.used_allocation, a.acct_state, ui.email, p.pops_person_id, a.role " +
					"FROM (" +
					"	SELECT a.first_name, a.last_name, a.person_id, a.request_id, a.alloc_resource_name, a.used_allocation, a.acct_state, a.pi_person_id, r.role " +
					"	FROM portal.acctv a " +
					" LEFT OUTER JOIN portal.account_roles r ON r.person_id = a.person_id AND r.account_id = a.account_id " +
					") a " +
					"JOIN portal.user_info ui on ui.person_id = a.person_id " +
					"JOIN portal.pops_person_id p on p.person_id = ui.person_id " +
					"LEFT OUTER JOIN portal.sav ON sav.person_id = a.person_id AND sav.resource_name = a.alloc_resource_name " +
					"WHERE a.pi_person_id = ? AND a.request_id = ? and a.alloc_resource_name = ? " +
					"ORDER BY a.last_name"
				);
			ps.setInt(1, p.getPiId());
			ps.setInt(2, a.getRequestID());
			ps.setString(3, a.getResource());
			
			Map<Integer,User> users = new LinkedHashMap<Integer, User>();
			Map<Integer,User> deadUsers = new LinkedHashMap<Integer, User>();
			Map<Integer, User> allUsers = new LinkedHashMap<Integer, User>();
			HashMap<Integer,Integer> popsHash = new HashMap<Integer, Integer>();
			
			rs = ps.executeQuery();
			NumberFormat nf = new DecimalFormat("#0.0");
			while (rs.next()) {
				String popsId = rs.getString("pops_person_id");
				int pops_person_id = 0;
				try {
					pops_person_id = Integer.parseInt(popsId);
				} catch (Exception e) {
					logger.error(e);
				}
				User u = new User(
						rs.getString("first_name"),
						rs.getString("last_name"),
						rs.getString("username"),
						rs.getString("email"),
						nf.format(Double.parseDouble(rs.getString("used_allocation"))),
						rs.getInt("person_id"),
						pops_person_id,
						rs.getString("role")
					);
				u.setLocalUsername(a.getResource(), rs.getString("local_username"));
				popsHash.put(u.getPersonID(), u.getPopsPersonID());
				
				boolean dead = rs.getString("acct_state").equals("dead");
				if (dead) {
					if (deadUsers.containsKey(u.getPersonID())) {
						deadUsers.get(u.getPersonID()).addRole(u.getRoles().get(0));
						deadUsers.get(u.getPersonID()).getLocalUsernames().putAll(u.getLocalUsernames());
					} else {
						deadUsers.put(u.getPersonID(), u);
					}
				} else {
					if (users.containsKey(u.getPersonID())) {
						users.get(u.getPersonID()).addRole(u.getRoles().get(0));
						users.get(u.getPersonID()).getLocalUsernames().putAll(u.getLocalUsernames());

					} else {
						users.put(u.getPersonID(), u);
					}
				}
			}
			
			a.setUsers(users.values().toArray(new User[users.size()]));
			a.setDeadUsers(deadUsers.values().toArray(new User[deadUsers.size()]));
			a.setPopsPersonIds(popsHash);
			
			// all users for adding users from other allocations to this one
			ps = conn.prepareStatement(
					"SELECT DISTINCT a.person_id, a.first_name, a.last_name, ui.email, ui.username, p.pops_person_id " +
					"FROM portal.acctv a " +
					"JOIN portal.user_info ui ON ui.person_id = a.person_id " +
					"JOIN portal.pops_person_id p on p.person_id = a.person_id " +
					"WHERE pi_person_id = ? AND acct_state != 'dead' ORDER BY last_name");
			ps.setInt(1, p.getPiId());
			rs = ps.executeQuery();
			while (rs.next()) {
				User u = new User(rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), rs.getString("username"), rs.getInt("person_id"), rs.getInt("pops_person_id"));
				allUsers.put(u.getPersonID(), u);
			}
			a.setAllUsers(allUsers.values().toArray(new User[allUsers.size()]));
			a.setAllEmailHash(allUsers.values().toArray(new User[allUsers.size()]));
			
			ps.close();
		} catch (SQLException e) {
			logger.error("Error populating users for allocation", e);
		}
	}


	@Override
    public boolean insertDN(String username, String dn) {
		boolean success = false;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
			"SELECT portal.add_dn(?,?)");
			ps.setString(1,username);
			ps.setString(2,dn);
			success = ps.execute();
			ps.close();
		} catch (SQLException e) {
			logger.error("Error", e);
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				logger.error("Error", e);
			}
		} 
		return success;
	} 

	@Override
    public boolean removeDN(String username, String dn) {
		boolean success = false;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
			"SELECT portal.remove_dn(?,?)");
			ps.setString(1,username);
			ps.setString(2,dn);
			success = ps.execute();
			ps.close();
		} catch (SQLException e) {
			logger.error("Error", e);
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				logger.error("Error", e);
			}
		} 
		return success;
	} 

	@Override
    public Map findDNs(String username) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Map dnMap = null;
		try {
			ps = conn.prepareStatement(
			"SELECT dn, state FROM portal.dns WHERE username = ?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			dnMap = createDistinguishedNameDAO(rs);
		} catch (SQLException e) {
			logger.error("Error", e);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				logger.error("Error", e);
			}
			try {
				ps.close();
			} catch (Exception e) {
				logger.error("Error", e);
			}
		}

		return dnMap;
	}

	@Override
    public int insertPiAddRemoveRequest(String status, String chargeNum, String resource, int personId, int popsPersonId, String firstName, String lastName, String piPortalUsername, String action){

		int result = 0;
		PreparedStatement ps = null;
	
		try {
	      ps = conn.prepareStatement(
	      		"INSERT INTO portal.pi_add_remove_requests(status, charge_number, resource, " +
						"person_id, pops_person_id, first_name, last_name, " +
           	"pi_portal_username, action) " + 
           	"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
           );
	      ps.setString(1,status);
	      ps.setString(2,chargeNum);
	      ps.setString(3,resource);
	      ps.setInt(4,personId);
	      ps.setInt(5,popsPersonId);
	      ps.setString(6,firstName);
	      ps.setString(7,lastName);
	      ps.setString(8,piPortalUsername);
	      ps.setString(9,action);
	
	      result = ps.executeUpdate();
	
	    } catch (SQLException e) {
	      logger.error("", e);
	    } finally {
	      try {
	      	ps.close();
	    	} catch (Exception e) {
	    		logger.error("", e);
	    	}
	    }
	
	    return result;
	}

	/**
	 * Maps data from the rsSysAccounts ResultSet, it creates a TreeMap 
	 * with the instution name as keys and the value as an ArrayList 
	 * of SystemBeans objects.
	 * 
	 * @param rsSysAccounts  ResultSet from system accounts query
	 * @return a Map object containing the data from rsSysAccounts
	 */
	private SystemBean[] createSystemAccountsDAO(ResultSet rsSysAccounts) {

		String resourceName, userName, inst;
		
		Vector sysBeans = new Vector();

		Map results = new TreeMap();
		WeakHashMap resourceMap = DBUtil.getResourceMap();
		WeakHashMap institutionMap = DBUtil.getInstitutionMap();
		WeakHashMap hash = DBUtil.getShortNameMap();

		try{
			while(rsSysAccounts.next()) {
				String origResName = rsSysAccounts.getString("resource_name");
				
				resourceName = (String)resourceMap.get(origResName);
				
				//remove resource name from hash, that way we know if there are any left where user has no account
				resourceMap.remove(origResName);

				if (resourceName == null) {
					continue;
				} else if (resourceName.equals(DBUtil.DEAD)) {
					continue;
				} else {

					//add username and resource host to new system bean	
					SystemBean sb = new SystemBean();
					sb.setResourceName(resourceName);
					sb.setOldName(origResName);
					userName = rsSysAccounts.getString("username");
					sb.setUserName(userName);
					inst = (String)institutionMap.get(origResName);
					sb.setInstitution(inst);
					sb.setShortName((String)hash.get(sb.getOldName()));

					//get current array list that belongs to institution or create a new one
					ArrayList instList = null;
					if(inst != null && !inst.equals("")) {
						instList = (ArrayList)results.get(inst);
					} 

					if(instList == null) {
						instList = new ArrayList();
					}
					//add system bean to array for that institution and add to results hash
					instList.add(sb);
					results.put(inst, instList);	
					sysBeans.add(sb);
				}
			}

			//Go through and make sure we haven't missed any systems, if we have list no account
			Iterator it = resourceMap.keySet().iterator();
			while (it.hasNext()) {
				// Get resource name & hostname, ensure not dead, if not add own account
				String key = (String)it.next();
				resourceName = (String)resourceMap.get(key);
				
				if(resourceName.equals(DBUtil.DEAD)){
					continue;
				}else if(resourceName != null){
					//Add null for username and resource host for new system bean
					SystemBean sb = new SystemBean();
					sb.setResourceName(resourceName);
					sb.setOldName(key);
					sb.setUserName(null);
					inst = (String)institutionMap.get(key);
					sb.setInstitution(inst);
					sb.setShortName((String)hash.get(sb.getOldName()));

					//get current array list that belongs to institution or create a new one
					ArrayList instList = null;
					if(inst != null && !inst.equals("")) {
						instList = (ArrayList)results.get(inst);
					}

					if(instList == null) {
						instList = new ArrayList();
					}
					//add system bean to array for that institution and add to results hash
					instList.add(sb);
					results.put(inst, instList);
					sysBeans.add(sb);
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Exception when parsing system account results", e);
		} 
		
		SystemBean[] sorted = new SystemBean[sysBeans.size()];
		sysBeans.toArray(sorted);
		Arrays.sort(sorted);
		return sorted;
	}

	/*
	 * Maps data from the rsDNs ResultSet to a Map object
	 *
	 * @param rsDNs ResultSet from DN list query
	 * @return a Map object containing the data from rsDNs
	 */
	private Map createDistinguishedNameDAO(ResultSet rsDNs) {

		Map dns = new TreeMap();
		try {
			while ( rsDNs.next() ) {
				dns.put(rsDNs.getString("dn"), rsDNs.getString("state"));
			}
		} catch (SQLException e) {
			logger.error("SQL Exception when parsing system account results", e);
		} 

		return dns;
	}

	@Override
    public boolean makeAllocationManager(int accountId, int personId) {
		
		CallableStatement cs;
		try {
			cs = conn.prepareCall("{call portal.set_allocation_manager(?, ?)}");
			cs.setInt(1, personId);
			cs.setInt(2, accountId);
			cs.execute();
			return true;
		} catch (SQLException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
    public User getPerson(int personId) {
		String sql = 
			"SELECT p.first_name, p.last_name, p.person_id, p.email " +
			"FROM portal.user_info p " +
			"WHERE p.person_id = ?";
		
		User u = null;
		
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, personId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				u = new User(
						rs.getString("first_name"),
						rs.getString("last_name"),
						rs.getString("email"),
						"",
						rs.getInt("person_id")
						);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		
		return u;
	}

    @Override
    public Project[] getProjects() {
        return new Project[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

}
