package edu.utexas.tacc.portlets;

import edu.utexas.tacc.portlets.beans.SystemBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: steve
 * Date: 7/9/12
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PersonGateway {
    /*
* get a new DB connection from the JNDI data source.
*/
    void initConnection();

    /*
* close the connection.  This method should be called after a client
* is done with this gateway object.
*/
    void close() throws SQLException;

    boolean checkUsername(String username);

    int findPersonId(String username);

    ResultSet findName(int personId);

    Project[] findProjects(String username);

    SystemBean[] findSystemAccounts(String personId);

    SystemBean findSystemAccount(String personId, String resourceName);

    /*
* I know, I know, generally bad practice to rely on functional side effects,
* but that's what we're doing here - mrhanlon :)
*/
    void populateUsers(Project p, Allocation a);

    boolean insertDN(String username, String dn);

    boolean removeDN(String username, String dn);

    Map findDNs(String username);

    int insertPiAddRemoveRequest(String status, String chargeNum, String resource, int personId, int popsPersonId, String firstName, String lastName, String piPortalUsername, String action);

    boolean makeAllocationManager(int accountId, int personId);

    User getPerson(int personId);

    Project[] getProjects();
}
