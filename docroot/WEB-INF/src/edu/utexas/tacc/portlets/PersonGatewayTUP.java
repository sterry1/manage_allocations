package edu.utexas.tacc.portlets;

import edu.utexas.tacc.portlets.beans.SystemBean;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: steve
 * Date: 7/9/12
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PersonGatewayTUP implements PersonGateway {

    private static final Logger logger = Logger.getLogger(PersonGatewayTUP.class);
    public static GProjectsBuilder gProjectsBuilder;

    /**
     * default constructor
     */
    public PersonGatewayTUP() {
        gProjectsBuilder = new GProjectsBuilder();
        initConnection();
    }


    public void initConnection() {
        logger.debug("in initConnection");

    }

    public void close() throws SQLException {

    }

    public boolean checkUsername(String username) {
        return false;
    }

    public int findPersonId(String username) {
        return 0;
    }

    public ResultSet findName(int personId) {
        return null;
    }

    /**
     * Gets the projects associated with the supplied username
     * <p/>
     * The projects returned from a query as an array of Projects from a supplied username.
     *
     * @param username Description the username used in the query for projects
     * @return Project[]
     */
    @Override
    public Project[] findProjects(String username) {
        logger.debug("in findProjects");
        Project[] projects = new Project[0];
        try {
            projects = gProjectsBuilder.getProjects();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return projects;
    }

    @Override
    public SystemBean[] findSystemAccounts(String personId) {
        return new SystemBean[0];
    }

    @Override
    public SystemBean findSystemAccount(String personId, String resourceName) {
        return null;
    }

    @Override
    public void populateUsers(Project project, Allocation allocation) {
        ResultSet rs = null;
        PreparedStatement ps = null;

        // get the mathching piId, requestID and resource name
        int piId = project.getPiId();
        int requestId = allocation.getRequestID();
        String resource = allocation.getResource();

        Map<Integer, User> users = new LinkedHashMap<Integer, User>();
        Map<Integer, User> deadUsers = new LinkedHashMap<Integer, User>();
        Map<Integer, User> allUsers = gProjectsBuilder.getUsers();

        // separate dead from active
        for(User user: allUsers.values()){
            user.setLocalUsername(allocation.getResource(), user.getUsername());

            boolean dead = new String("inactive").equals(user.getStatus());
            // find dead accounts
            if (dead) {
                if (deadUsers.containsKey(user.getPersonID())) {
                    deadUsers.get(user.getPersonID()).addRole(user.getRoles().get(0));
                    deadUsers.get(user.getPersonID()).getLocalUsernames().putAll(user.getLocalUsernames());
                } else {
                    deadUsers.put(user.getPersonID(), user);
                }
            } else {
                if (users.containsKey(user.getPersonID())) {
                    users.get(user.getPersonID()).addRole(user.getRoles().get(0));
                    users.get(user.getPersonID()).getLocalUsernames().putAll(user.getLocalUsernames());

                } else {
                    users.put(user.getPersonID(), user);
                }
            }
        }

        allocation.setUsers(users.values().toArray(new User[users.size()]));
        allocation.setUsers(deadUsers.values().toArray(new User[deadUsers.size()]));
        allocation.setUsers(allUsers.values().toArray(new User[allUsers.size()]));

    }


    @Override
    public boolean insertDN(String username, String dn) {
        return false;
    }

    @Override
    public boolean removeDN(String username, String dn) {
        return false;
    }

    @Override
    public Map findDNs(String username) {
        return null;
    }

    @Override
    public int insertPiAddRemoveRequest(String status, String chargeNum, String resource, int personId, int popsPersonId, String firstName, String lastName, String piPortalUsername, String action) {
        return 0;
    }

    @Override
    public boolean makeAllocationManager(int accountId, int personId) {
        return false;
    }

    @Override
    public User getPerson(int personId) {
        return null;
    }

    @Override
    public Project[] getProjects() {
        return new Project[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
