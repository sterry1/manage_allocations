package edu.utexas.tacc.portlets;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.sql.DataSource;

import edu.utexas.tacc.bean.Resource;
import edu.utexas.tacc.clients.CouchClient;
import edu.utexas.tacc.dao.ResourceDao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * ManageAllocationsPortlet.java This portlet class handles reporting accounts and
 * usage data from the TeraGrid central database.
 * <p/>
 * Created: Thu Dec 2 17:02:24 2004
 * Modified: Tues July 7 2009
 *
 * @author <a href="mailto:ericrobe@tacc.utexas.edu"></a>, praveen@tacc.utexas.edu
 * @version 2.0
 * @changes
 * @Name: Praveen Nuthulapati (praveen@tacc.utexas.edu)
 * @desc: Migration to liferay
 */
public class ManageAllocationsPortlet extends GenericPortlet {

    public static final Logger logger = Logger.getLogger(ManageAllocationsPortlet.class);

    // ERRORS
    public static final String UNKNOWN_ERROR = "An unknown error occured.  Please contact adminstrator";

    // SESSION PARAMS
    public static final String SESS_ADD_REM_BEAN = "SESS_ADD_REM_BEAN";
    public static final String SESS_PROJECT_LIST = "SESS_PROJECTS";
    public static final String SESS_PROJECT_LIST_USERNAME = "SESS_PROJECT_LIST_USERNAME";

    // REQUEST PARAMS
    public static final String ADMIN = "ADMIN";
    public static final String PI = "PI";
    public static final String ALLOCATION_MGR = "ALLOCATION_MGR";
    public static final String ADD_REM_BEAN = "ADD_REM_BEAN";
    public static final String PARAM_ERROR = "error";
    public static final String ACTION = "action";
    public static final String ACTION_METHOD = "ACTION_METHOD";
    public static final String ERROR = "error";
    public static final String STATUS = "status";
    public static final String PERSON = "PERSON";
    public static final String PROJECT = "PROJECT";
    public static final String PROJECT_LIST = "PROJECT_LIST";
    public static final String PROJECT_IDX = "PROJECT_IDX";
    public static final String ALLOCATION = "allocation";
    public static final String ALLOCATION_IDX = "ALLOCATION_IDX";
    public static final String FORM_STATUS = "formStatus";
    public static final String TO_EMAIL = "to_email";
    public static final String FROM_EMAIL = "from_email";
    public static final String REMOVE_ALL = "REMOVE_ALL";
    public static final String REMOVE_ALL_YES = "YES";
    public static final String REMOVE_ALL_NO = "NO";
    public static final String ACCOUNT_ID = "ACCOUNT_ID";
    public static final String PERSON_ID = "PERSON_ID";

    // JSP VIEWS
    public static final String VIEW_DEFAULT = "/jsp/view-allocations.jsp";
    public static final String VIEW_MINI = "/jsp/view-allocations-mini.jsp";
    public static final String VIEW_MINI_SPEDO = "/jsp/view-alloc-mini-spedo.jsp";
    public static final String VIEW_USERS = "/jsp/view-users.jsp";
    public static final String VIEW_HELP = "/jsp/tgusage-help.jsp";
    public static final String VIEW_DATA_ERROR = "/jsp/data-error.jsp";
    public static final String VIEW_NO_ACTIVE_PROJECTS = "/jsp/no-projects.jsp";
    public static final String VIEW_ADDUSER = "/jsp/adduser.jsp";
    public static final String VIEW_ADD_CONFIRM = "/jsp/adduser-confirm.jsp";
    public static final String VIEW_ADD_COMPLETE = "/jsp/adduser-complete.jsp";
    public static final String VIEW_DELETEUSER = "/jsp/deleteuser.jsp";
    public static final String VIEW_DELETE_CONFIRM = "/jsp/deleteuser-confirm.jsp";
    public static final String VIEW_DELETE_COMPLETE = "/jsp/deleteuser-complete.jsp";
    public static final String MAKE_ALLOCATION_MGR_JSP = "/jsp/make-alloc-manager.jsp";
    public static final String VIEW_USAGE = "/jsp/usage.jsp";
    public static final String VIEW_PROJECT_USAGE = "/jsp/project-usage.jsp";

    // ACTIONS
    public static final String PI_ADD = "PI_ADD";
    public static final String PI_DELETE = "PI_DELETE";
    public static final String PI_ADD_CONFIRM = "PI_ADD_CONFIRM";
    public static final String PI_DELETE_CONFIRM = "PI_DELETE_CONFIRM";
    public static final String PI_ADD_COMPLETE = "PI_ADD_COMPLETE";
    public static final String PI_DELETE_COMPLETE = "PI_DELETE_COMPLETE";
    public static final String MAKE_ALLOCATION_MGR = "MAKE_ALLOCATION_MGR";
    public static final String DO_MAKE_ALLOCATION_MGR = "DO_MAKE_ALLOCATION_MGR";

    // STATUSES
    public static final String LIST = "list";
    public static final String ADD = "add";
    public static final String DELETE = "delete";
    public static final String CONFIRM = "confirm";
    public static final String COMPLETE = "complete";

    // FORM SUBMISSION PARAMETERS
    public static final String RESOURCE = "RESOURCE";
    public static final String ORIGINAL_RESOURCE = "ORIGINAL_RESOURCE";
    public static final String CHARGE_NUMBER = "CHARGE_NUMBER";
    public static final String GRANT_NUMBER = "GRANT_NUMBER";
    public static final String USER = "USER";

    // RESOURCES
    public static final String RESOURCE_REQUEST = "resourceRequest";
    public static final String DOWNLOAD_ALLOC_DATA = "downloadAllocData";
    public static final String PROJECT_USERS = "PROJECT_USERS";

    // private CouchClient couchClient;

    /* (non-Javadoc)
      * @see javax.portlet.GenericPortlet#destroy()
      */
    @Override
    public void destroy() {
        // couchClient.shutdown();
        super.destroy();
    }

    /* (non-Javadoc)
      * @see javax.portlet.GenericPortlet#init()
      */
    @Override
    public void init() throws PortletException {
/*
		ResourceBundle bundle = ResourceBundle.getBundle("couchdb");
		String server = bundle.getString("server");
		int port = Integer.parseInt(bundle.getString("port"));
		boolean ssl = Boolean.parseBoolean(bundle.getString("ssl"));
		String database = bundle.getString("database");
		String couchuser = bundle.getString("username");
		String password = bundle.getString("password");
		couchClient = new CouchClient(server, port, database, couchuser, password, ssl);
*/
        super.init();
    }

    /**
     * By default, display the usage for current authenticated user.
     *
     * @param request  a <code>RenderRequest</code> value
     * @param response a <code>RenderResponse</code> value
     * @throws PortletException    if an error occurs
     * @throws java.io.IOException if an error occurs
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        logger.info("Entering doView()");

        PortletRequestDispatcher rd = null;

        // ADMIN OVERRIDE todo isAdmin always returns false
        boolean admin = isAdmin(request);
        request.setAttribute(ADMIN, admin);

        // Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
        // String username = (userInfo!=null) ? (String) userInfo.get("user.name") : "";

        // Get username
        //String username = null;
        String username = "sterry1";

/*
        try {
            username = getPortalUsername(request);
        } catch (PortalException e1) {
            logger.error(e1);
        } catch (SystemException e1) {
            logger.error(e1);
        }
*/

        String status = (String) request.getParameter(STATUS);
        String action = (String) request.getParameter(ACTION);
        logger.info("status: " + status + " action: " + action);
        // todo comment all the next actions out so operations other than read don't activate
/*
		if (status != null && status.equals(CONFIRM) && action.equals(PI_ADD)) {
			AddRemoveBean arb = (AddRemoveBean) request.getPortletSession().getAttribute(SESS_ADD_REM_BEAN);
			if (arb != null) {
				// Go on to confirm page
				rd = getPortletContext().getRequestDispatcher(VIEW_ADD_CONFIRM);
				request.setAttribute(ADD_REM_BEAN, arb);
				rd.include(request, response);
				return;
			}
		}
		
		if (status != null && status.equals(COMPLETE) && action.equals(PI_ADD)) {
			// Go on to complete page
			rd = getPortletContext().getRequestDispatcher(VIEW_ADD_COMPLETE);
			rd.include(request, response);
			return;
		}

		if (status != null && status.equals(CONFIRM) && action.equals(PI_DELETE)) {
			logger.debug("render delete confirm");
			AddRemoveBean arb = (AddRemoveBean) request.getPortletSession().getAttribute(SESS_ADD_REM_BEAN);
			String removeAll = (String) request.getPortletSession().getAttribute(REMOVE_ALL);
			String projNum = (String) request.getPortletSession().getAttribute(PROJECT_IDX);
			if (arb != null) {
				// Go on to confirm page
				rd = getPortletContext().getRequestDispatcher(VIEW_DELETE_CONFIRM);
				request.setAttribute(ADD_REM_BEAN, arb);
				request.setAttribute(REMOVE_ALL, removeAll);
				request.setAttribute(PROJECT_IDX, projNum);
				rd.include(request, response);
				return;
			}
		}

		if (status != null && status.equals(COMPLETE) && action.equals(PI_DELETE)) {
			// Go on to complete page
			rd = getPortletContext().getRequestDispatcher(VIEW_DELETE_COMPLETE);
			rd.include(request, response);
			return;
		}

		if (status != null && status.equals(CONFIRM) && action.equals(MAKE_ALLOCATION_MGR)) {
			PortletSession session = request.getPortletSession();
			Project[] projects = (Project[]) session.getAttribute(SESS_PROJECT_LIST);
			int projectIdx = Integer.parseInt(request.getParameter(PROJECT_IDX));
			int personId = Integer.parseInt(request.getParameter(PERSON_ID));
			
			PersonGateway pg = new PersonGatewayTUP();
			User u = pg.getPerson(personId);
			try {
				pg.close();
			} catch (SQLException e) {
				logger.error(e);
			}
			request.setAttribute(PERSON, u);
			request.setAttribute(PROJECT, projects[projectIdx]);
			request.setAttribute(PROJECT_IDX, projectIdx);

			rd = getPortletContext().getRequestDispatcher(MAKE_ALLOCATION_MGR_JSP);
			rd.include(request, response);
			return;
		}
*/

        // Find a list of projects for the user
        Project[] projects = null;
        PortletSession session = request.getPortletSession();
        projects = (Project[]) session.getAttribute(SESS_PROJECT_LIST);
        String sessionUsername = (String) session.getAttribute(SESS_PROJECT_LIST_USERNAME);
        if (!username.equals(sessionUsername)) {
            projects = null;
        }
        if (projects == null) { // If projects not available in session; get projects
            PersonGateway pg = new PersonGatewayTUP();
            projects = pg.findProjects(username);   // todo this is me for now

/*          // todo this is all geared to DB access
			try {
				//long start = System.currentTimeMillis();
                // PersonGateway pg = new PersonGateway(); todo stub out with new PersonGateway
                PersonGateway pg = new PersonGatewayTUP();
                projects = pg.findProjects(username);   // todo this is me for now
				// pg.close(); todo unecessary call since there is no DB involved
				//long stop = System.currentTimeMillis();
				//long total = stop - start;
				//System.out.println(new java.util.Date() + " - ManageAllocationsPortlet Query Time: " + total);
			} catch (SQLException e) {
				logger.error("Error", e);
			}
*/
            session.setAttribute(SESS_PROJECT_LIST, projects);
            session.setAttribute(SESS_PROJECT_LIST_USERNAME, username);
        }

        if (projects.length > 0) {
            request.setAttribute(PROJECT_LIST, projects);
            if (status != null && action != null) {
                if (action.equals(PI_ADD) && status.equals(LIST)) {
                    // List all the users, make sure projects are set as attribute
                    int projIdx = Integer.parseInt(request.getParameter(PROJECT_IDX));
                    int allocIdx = Integer.parseInt(request.getParameter(ALLOCATION_IDX));
                    request.setAttribute(ACTION, action);
                    request.setAttribute(STATUS, status);
                    request.setAttribute(PROJECT_IDX, projIdx);
                    request.setAttribute(ALLOCATION_IDX, allocIdx);
                    request.setAttribute(PROJECT, projects[projIdx]);
                    request.setAttribute(ALLOCATION, projects[projIdx].getAllocations()[allocIdx]);
                    rd = getPortletContext().getRequestDispatcher(VIEW_ADDUSER);
                } else if (action.equals(PI_DELETE) && status.equals(LIST)) {
                    // List all the users, make sure projects are set as attribute
                    int projIdx = Integer.parseInt(request.getParameter(PROJECT_IDX));
                    int allocIdx = Integer.parseInt(request.getParameter(ALLOCATION_IDX));
                    request.setAttribute(ACTION, action);
                    request.setAttribute(STATUS, status);
                    request.setAttribute(PROJECT_IDX, projIdx);
                    request.setAttribute(ALLOCATION_IDX, allocIdx);
                    request.setAttribute(PROJECT, projects[projIdx]);
                    request.setAttribute(ALLOCATION, projects[projIdx].getAllocations()[allocIdx]);
                    rd = getPortletContext().getRequestDispatcher(VIEW_DELETEUSER);
                } else {
                    rd = getPortletContext().getRequestDispatcher(VIEW_DEFAULT);
                }
            } else if (status == null && action != null && (action.equals("usage") || action.equals("project_usage"))) {
                if (action.equals("usage")) {
                    int project = ParamUtil.getInteger(request, "project"),
                            allocation = ParamUtil.getInteger(request, "allocation");

                    Project p = projects[project];
                    Allocation a = projects[project].getAllocations()[allocation];
                    if (a.getUsers().length == 0) {
                        PersonGateway pg = new PersonGatewayTUP();
                        pg.populateUsers(p, a);
                        try {
                            pg.close();
                        } catch (SQLException e) {
                            logger.error("error closing PersonGateway connection", e);
                        }
                    }

                    if (!(admin || p.getIsPI() || p.isCoPI() || p.isAllocationMgr())) {
                        request.setAttribute("userOnly", true);
                        request.setAttribute("user", username);
                    }

                    request.setAttribute("project", p);
                    request.setAttribute("allocation", a);
                    request.setAttribute("projectIndex", Integer.toString(project));
                    request.setAttribute("allocationIndex", Integer.toString(allocation));
                    request.setAttribute("resourceName", DBUtil.getShortNameMap().get(a.getResource()));
                    rd = getPortletContext().getRequestDispatcher(VIEW_USAGE);
                } else if (action.equals("project_usage")) {
                    int project = ParamUtil.getInteger(request, "project");
                    Project p = projects[project];
                    Date startDate = null, endDate = null;

                    // TreeMap will sort by request IDs, which are increasing chronologically
                    Map<Integer, Date[]> allocationDates = new TreeMap<Integer, Date[]>();
                    Map<Integer, List<String>> allocationResources = new TreeMap<Integer, List<String>>();
                    for (Allocation a : p.getAllocations()) {
                        if (!(a.getType().equals("new") || a.getType().equals("renewal"))) {
                            continue;
                        }
                        if (!allocationResources.containsKey(a.getRequestID())) {
                            allocationResources.put(a.getRequestID(), new ArrayList<String>());
                        }
                        allocationResources.get(a.getRequestID()).add(a.getResource());

                        if (allocationDates.containsKey(a.getRequestID())) continue;

                        Date[] dates = new Date[2];
                        dates[0] = a.getStartDate();
                        dates[1] = a.getEndDate();
                        allocationDates.put(a.getRequestID(), dates);
                    }

                    // reorder lists to reverse chronological
                    List<Date[]> dates = new ArrayList<Date[]>(allocationDates.values());
                    Collections.sort(dates, new Comparator<Date[]>() {

                        @Override
                        public int compare(Date[] d0, Date[] d1) {
                            // reverse sort
                            return d1[0].compareTo(d0[0]);
                        }

                    });

                    request.setAttribute("dates", dates);
                    request.setAttribute("project", p);
                    request.setAttribute("projectIndex", Integer.toString(project));
                    request.setAttribute("resourceNames", DBUtil.getShortNameMap());
                    request.setAttribute("startDate", startDate);
                    request.setAttribute("endDate", endDate);
                    rd = getPortletContext().getRequestDispatcher(VIEW_PROJECT_USAGE);
                }
            } else {
                //which view, mini or full?
                PortletPreferences prefs = request.getPreferences();
                String view = prefs.getValue("mode", "full-view");
                request.setAttribute("mode", view);

                if (view.equals("mini-view")) {
                    //include(VIEW_MINI, renderRequest, renderResponse);
                    rd = getPortletContext().getRequestDispatcher(VIEW_MINI_SPEDO);
                } else {
                    //include(VIEW_TICKETS, renderRequest, renderResponse);
                    rd = getPortletContext().getRequestDispatcher(VIEW_DEFAULT);
                }

                //rd = getPortletContext().getRequestDispatcher(VIEW_DEFAULT);
            }
        } else {
            rd = getPortletContext().getRequestDispatcher(VIEW_NO_ACTIVE_PROJECTS);
        }

        rd.include(request, response);
        logger.info("Leaving doView()");

    }


    /**
     * Handles querying single resource information and storing portlet
     * preferences
     *
     * @param request  an <code>ActionRequest</code> value
     * @param response an <code>ActionResponse</code> value
     * @throws PortletException    if an error occurs
     * @throws java.io.IOException if an error occurs
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
/*
		// decide what action method to call
		String status = (String) request.getParameter(STATUS);
		String actionMethod = (String) request.getParameter(ACTION_METHOD);
		String projectNumber = (String) request.getParameter(PROJECT_IDX);
		String allocationNumber = (String) request.getParameter(ALLOCATION_IDX);

		if (actionMethod != null) {
			if (actionMethod.equals(PI_ADD)) {
				// START THE PI_ADD PROCESS, LIST ALL USERS
				if (status == null || status.equals(LIST)) {
					// LIST ALL THE USERS FOR PI TO SELECT
					response.setRenderParameter(ACTION, PI_ADD);
					response.setRenderParameter(STATUS, LIST);
					response.setRenderParameter(PROJECT_IDX, projectNumber);
					response.setRenderParameter(ALLOCATION_IDX, allocationNumber);
				}
			} else if (actionMethod.equals(PI_ADD_CONFIRM)) {
				// Confirm that the user wants to add them
				doAddConfirm(request, response);
			} else if (actionMethod.equals(PI_ADD_COMPLETE)) {
				// Send email to admins, complete request
				doAddComplete(request, response);
			} else if (actionMethod.equals(PI_DELETE)) {
				// START THE PI_DELETE PROCESS, LIST ALL USERS
				if (status == null || status.equals(LIST)) {
					// LIST ALL THE USERS FOR PI TO SELECT
					response.setRenderParameter(ACTION, PI_DELETE);
					response.setRenderParameter(STATUS, LIST);
					response.setRenderParameter(PROJECT_IDX, projectNumber);
					response.setRenderParameter(ALLOCATION_IDX, allocationNumber);
				}
			} else if (actionMethod.equals(PI_DELETE_CONFIRM)) {
				// Confirm that the user wants to add them
				doDeleteConfirm(request, response);
			} else if (actionMethod.equals(PI_DELETE_COMPLETE)) {
				// Send email to admins, complete request
				doDeleteComplete(request, response);
			} else if (actionMethod.equals(MAKE_ALLOCATION_MGR)) {
				response.setRenderParameters(request.getParameterMap());
				response.setRenderParameter(ACTION, MAKE_ALLOCATION_MGR);
				response.setRenderParameter(STATUS, CONFIRM);
			} else if (actionMethod.equals(DO_MAKE_ALLOCATION_MGR)) {
				doMakeAllocationManager(request, response);
			} else if (actionMethod.equals("ADMIN_USERNAME_OVERRIDE")) {
				String override = ParamUtil.getString(request, "ADMIN_USERNAME_OVERRIDE");
				if (override != null && override.length() > 0) {
					request.setAttribute("ADMIN_USERNAME_OVERRIDE", override);
					PersonGateway pg = new PersonGatewayTUP();
					if (pg.checkUsername(override)) {
						request.getPortletSession().setAttribute("ADMIN_USERNAME_OVERRIDE", override);
					} else {
						SessionErrors.add(request, "admin-username-override-dne");
					}
					try {
						pg.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				} else {
					request.getPortletSession().removeAttribute("ADMIN_USERNAME_OVERRIDE");
				}
			} else if (actionMethod.equals("ADMIN_USERNAME_OVERRIDE_RESET")) {
				request.getPortletSession().removeAttribute("ADMIN_USERNAME_OVERRIDE");
			}
		}
*/
    }


    private String getPortalUsername(PortletRequest request) throws PortalException, SystemException {
        String username = PortalUtil.getUser(request).getScreenName();

        // check for override
        String override = (String) request.getPortletSession().getAttribute("ADMIN_USERNAME_OVERRIDE");
        if (override != null && override.length() > 0) {
            username = override;
            request.setAttribute("ADMIN_USERNAME_OVERRIDE", username);
        }

        return username;
    }


    private void getProjectUsage(PortletRequest request, MimeResponse response) throws JsonGenerationException, JsonMappingException, IOException {
/*
		int projectIndex = ParamUtil.getInteger(request, "project");
		String startDate = ParamUtil.getString(request, "startDate");
		String endDate = ParamUtil.getString(request, "endDate");
		
		Project[] projects = (Project[]) request.getPortletSession().getAttribute(SESS_PROJECT_LIST);
		String projectNumber = projects[projectIndex].getChargeNumber();

		ComplexKey startKey = ComplexKey.of(projectNumber, startDate),
							 endKey = ComplexKey.of(projectNumber, endDate);
		
		String designDoc = "_design/project_date";
		ViewQuery query = couchClient.getViewQuery(designDoc, "jobs", startKey, endKey, true, false).group(true);
		Map<String, Integer> jobs = new LinkedHashMap<String, Integer>();
		ViewResult result = couchClient.executeQuery(query);
		for (Row row : result.getRows()) {
			JsonNode node = row.getKeyAsNode();
			String key = node.get(1).getTextValue();
			Integer count = jobs.get(key);
			count = count == null ? row.getValueAsInt() : count + row.getValueAsInt();
			jobs.put(key, count);
		}

		query = couchClient.getViewQuery(designDoc, "sus", startKey, endKey, true, false).group(true);
		Map<String, Double> sus = new LinkedHashMap<String, Double>();
		result = couchClient.executeQuery(query);
		for (Row row : result.getRows()) {
			JsonNode node = row.getKeyAsNode();
			String key = node.get(1).getTextValue();
			Double sum = sus.get(key);
			sum = sum == null ? Double.parseDouble(row.getValue()) : sum + Double.parseDouble(row.getValue());
			sus.put(key, sum);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonResp = mapper.createObjectNode();

		ArrayNode arrayNode = mapper.createArrayNode();
		for (String key : jobs.keySet()) {
			ObjectNode node = mapper.createObjectNode();
			node.put("month", key);
			node.put("jobs", jobs.get(key));
			node.put("sus", BigDecimal.valueOf(sus.get(key)).setScale(2, BigDecimal.ROUND_HALF_UP));
			arrayNode.add(node);
		}
		jsonResp.put("result", arrayNode);

		mapper.writeValue(response.getPortletOutputStream(), jsonResp);
*/
    }

    private void getUsage(PortletRequest request, MimeResponse response) throws JsonGenerationException, JsonMappingException, IOException, PortalException, SystemException {
/*
		int projectIndex = ParamUtil.getInteger(request, "project");
		int allocationIndex = ParamUtil.getInteger(request, "allocation");
		String startDate = ParamUtil.getString(request, "startDate");
		String endDate = ParamUtil.getString(request, "endDate");
		String user = ParamUtil.getString(request, "user", null);
		
		Project[] projects = (Project[]) request.getPortletSession().getAttribute(SESS_PROJECT_LIST);
		String projectNumber = projects[projectIndex].getChargeNumber();
		String resource = projects[projectIndex].getAllocations()[allocationIndex].getResource();
		
		if (! (isAdmin(request) || projects[projectIndex].getIsPI() || projects[projectIndex].isCoPI() || projects[projectIndex].isAllocationMgr())) {
			user = getPortalUsername(request);
		}
		
		Map<String, Integer> jobs = new LinkedHashMap<String, Integer>();
		Map<String, Double> sus = new LinkedHashMap<String, Double>();

		String designDoc;
		ComplexKey startKey, endKey;
		int dateIndex;
		if (user == null) {
			designDoc = "_design/project_resource_date";
			startKey = ComplexKey.of(projectNumber, resource, startDate);
			endKey = ComplexKey.of(projectNumber, resource, endDate);
			dateIndex = 2;
		} else {
			designDoc = "_design/project_resource_user_date";
			startKey = ComplexKey.of(projectNumber, resource, user, startDate);
			endKey = ComplexKey.of(projectNumber, resource, user, endDate);
			dateIndex = 3;
		}
		
		ViewQuery query = couchClient.getViewQuery(designDoc, "jobs", startKey, endKey, true, false).group(true);
		ViewResult result = couchClient.executeQuery(query);
		for (Row row : result.getRows()) {
			JsonNode node = row.getKeyAsNode();
			String key = node.get(dateIndex).getTextValue();
			Integer count = jobs.get(key);
			count = count == null ? row.getValueAsInt() : count + row.getValueAsInt();
			jobs.put(key, count);
		}
		
		query = couchClient.getViewQuery(designDoc, "sus", startKey, endKey, true, false).group(true);
		result = couchClient.executeQuery(query);
		for (Row row : result.getRows()) {
			JsonNode node = row.getKeyAsNode();
			String key = node.get(dateIndex).getTextValue();
			Double sum = sus.get(key);
			sum = sum == null ? Double.parseDouble(row.getValue()) : sum + Double.parseDouble(row.getValue());
			sus.put(key, sum);
		}

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonResp = mapper.createObjectNode();

		ArrayNode arrayNode = mapper.createArrayNode();
		for (String key : jobs.keySet()) {
			ObjectNode node = mapper.createObjectNode();
			node.put("month", key);
			node.put("jobs", jobs.get(key));
			node.put("sus", BigDecimal.valueOf(sus.get(key)).setScale(2, BigDecimal.ROUND_HALF_UP));
			arrayNode.add(node);
		}
		jsonResp.put("result", arrayNode);

		mapper.writeValue(response.getPortletOutputStream(), jsonResp);

*/
    }

    private boolean isAdmin(PortletRequest request) {
        boolean admin = false;
/*
		try {
			com.liferay.portal.model.User user = PortalUtil.getUser(request);
			long adminRoleId = RoleLocalServiceUtil.getRole(user.getCompanyId(), "Administrator").getRoleId();
			long allocationsAdminRoleId = RoleLocalServiceUtil.getRole(user.getCompanyId(), "Allocations Admin").getRoleId();
			if (UserLocalServiceUtil.hasRoleUser(adminRoleId, user.getUserId()) || UserLocalServiceUtil.hasRoleUser(allocationsAdminRoleId, user.getUserId())) {
				admin = true;
			}
		} catch (Throwable t) {
			logger.error("Problem looking for the Admin role(s)! (You may be missing one of the roles (Administrator || Allocations Admin) in LifeRay!", t);
		}
*/
        return admin;
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {
        String username = null;
        try {
            username = getPortalUsername(request);
        } catch (Exception e) {
            username = "";
            logger.error("Unable to retrieve portal username!", e);
        }
        String resource = ParamUtil.getString(request, RESOURCE_REQUEST);
        if (resource.equals(DOWNLOAD_ALLOC_DATA)) {
            PortletSession session = request.getPortletSession();
            Project[] projects = (Project[]) session.getAttribute(SESS_PROJECT_LIST);
            int p = Integer.parseInt(request.getParameter(PROJECT_IDX));
            Project proj = projects[p];
            int a = Integer.parseInt(request.getParameter(ALLOCATION_IDX));
            Allocation alloc = projects[p].getAllocations()[a];
            byte[] download = getAllocationDataCSVDownload(proj, alloc);
            response.setContentType("text/csv");
            response.setContentLength(download.length);
            response.setProperty(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + username + "_" + proj.getTitle().replaceAll("\\s+", "_") + "_" + alloc.getResource() + "_" + alloc.getFormattedStartDate().replace('-', '_') + ".csv");
            response.getPortletOutputStream().write(download);
            response.getPortletOutputStream().flush();
            response.getPortletOutputStream().close();
        } else if (resource.equals(PROJECT_USERS)) {
            // ADMIN OVERRIDE
            boolean admin = isAdmin(request);
            request.setAttribute(ADMIN, admin);

            int projIdx = Integer.parseInt(request.getParameter(PROJECT_IDX));
            int allocIdx = Integer.parseInt(request.getParameter(ALLOCATION_IDX));
            PortletSession session = request.getPortletSession();
            Project[] projects = (Project[]) session.getAttribute(SESS_PROJECT_LIST);

            Project p = projects[projIdx];
            Allocation a = p.getAllocations()[allocIdx];
            if (a.getUsers().length == 0) {
                PersonGateway pg = new PersonGatewayTUP();
                pg.populateUsers(p, a);
                try {
                    pg.close();
                } catch (SQLException e) {
                    logger.error("error closing PersonGateway connection", e);
                }
            }

            try {
                Resource r = new ResourceDao(getPortalDataSource()).getResource(a.getResource());
                request.setAttribute("resourceActive", r.isActive());
            } catch (NamingException e) {
                logger.error(e);
            }

            request.setAttribute(PROJECT, p);
            request.setAttribute(ALLOCATION, a);
            request.setAttribute(PROJECT_IDX, projIdx);
            request.setAttribute(ALLOCATION_IDX, allocIdx);
            PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(VIEW_USERS);
            rd.include(request, response);
        } else if (resource.equals("usage")) {
            try {
                getUsage(request, response);
            } catch (PortalException e) {
                logger.error(e);
            } catch (SystemException e) {
                logger.error(e);
            }
        } else if (resource.equals("project_usage")) {
            getProjectUsage(request, response);
        }
    }

/**
 * Creates a CSV document for a project/allocation with user data.
 * @param project The project
 * @param allocation The allocation
 * @return the bytes of the generated CSV document
 */
	private byte[] getAllocationDataCSVDownload(Project project, Allocation allocation) {
		StringBuilder data = new StringBuilder();
		String delim = "\",\"";

		// project and allocation header
		data.append("\"PROJECT\"\n");
		data.append("\"Project\",\"Start Date\",\"End Date\",\"Resource\",\"SU Remaining\",\"SUs Awarded\",\"% Remaining\",\"Alloc. Type\",\"State\"\n");

		double base = Double.parseDouble(allocation.getBaseAllocation());
		double rem = Double.parseDouble(allocation.getRemainingAllocation());
		double perc = rem / base;

		// project and alloc data
		data.append("\"");
		data.append(project.getTitle().replaceAll("\\s+", " "));
		data.append(delim);
		data.append(allocation.getFormattedStartDate());
		data.append(delim);
		data.append(allocation.getFormattedEndDate());
		data.append(delim);
		data.append(allocation.getResource());
		data.append(delim);
		data.append(allocation.getRemainingAllocation());
		data.append(delim);
		data.append(allocation.getBaseAllocation());
		data.append(delim);
		data.append(java.text.NumberFormat.getPercentInstance().format(perc));
		data.append(delim);
		data.append(allocation.getType());
		data.append(delim);
		data.append(allocation.getState());
		data.append("\"\n\n\n");

		// users header
		data.append("\"USERS\"\n");
		data.append("\"Name\",\"Portal Username\",\"Allocation Usage (SU)\",\"Contact Info\"\n");

		// users data
		for (User u : allocation.getUsers()) {
			data.append("\"");
			data.append(u.getFullNameLastFirst());
			data.append(delim);
			data.append(u.getUsername());
			data.append(delim);
			data.append(u.getUsedAllocation());
			data.append(delim);
			data.append(u.getEmail());
			data.append("\"\n");
		}
		return data.toString().getBytes();
	}

/**
 * Displays the edit mode screen for modifying this portlets preferences.
 *
 * @param request
 *            a <code>RenderRequest</code> value
 * @param response
 *            a <code>RenderResponse</code> value
 * @exception PortletException
 *                if an error occurs
 * @exception java.io.IOException
 *                if an error occurs
 */
	public void doHelp(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		PortletRequestDispatcher rd = null;
		rd = getPortletContext().getRequestDispatcher(VIEW_HELP);
		rd.include(request, response);
	}


	private void doMakeAllocationManager(ActionRequest request, ActionResponse response) {
		int accountId = Integer.parseInt(request.getParameter(ACCOUNT_ID)),
				personId = Integer.parseInt(request.getParameter(PERSON_ID));

		PersonGateway pg = new PersonGatewayXSEDE();
		if (pg.makeAllocationManager(accountId, personId)) {
			request.getPortletSession().removeAttribute(SESS_PROJECT_LIST);
			SessionMessages.add(request, "success-make-allocation-mgr");
		} else {
			SessionErrors.add(request, "error-make-allocation-mgr");
			request.setAttribute(ACCOUNT_ID, accountId);
			request.setAttribute(PERSON_ID, personId);
			response.setRenderParameter(STATUS, CONFIRM);
			response.setRenderParameter(ACTION, MAKE_ALLOCATION_MGR);
		}
		try {
			pg.close();
		} catch (SQLException e) {
			logger.error(e);
		}
	}


	protected DataSource getPortalDataSource() throws NamingException {
		Context context = new InitialContext();
		DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/TGCDB-portal");
		return ds;
	}

/**
 * Reads in form parameters, sets them to the bean variables, and confirm
 * with user
 */
	public void doDeleteConfirm(ActionRequest request, ActionResponse response) {

		String res = request.getParameter(RESOURCE);
		String origResource = request.getParameter(ORIGINAL_RESOURCE);
		String chargeNum = request.getParameter(CHARGE_NUMBER);
		String removeAll = request.getParameter(REMOVE_ALL);
		String projNum = request.getParameter(PROJECT_IDX);
		AddRemoveBean arb = new AddRemoveBean();
		arb.setResource(res);
		arb.setChargeNumber(chargeNum);
		arb.setOrigResource(origResource);

		String[] names = request.getParameterValues(USER);
		if (names != null && names.length > 0) {
			List<String> userList = new LinkedList<String>();
			for (int i = 0; i < names.length; i++) {
				userList.add(names[i]);
			}
			arb.setUserList(userList);
		} else {
			// Error PI did not select any users
			// Return to allocations listing
			response.setRenderParameter(STATUS, "");
			response.setRenderParameter(ACTION, "");
			return;
		}
		response.setRenderParameter(STATUS, CONFIRM);
		response.setRenderParameter(ACTION, PI_DELETE);
		PortletSession session = request.getPortletSession();
		session.setAttribute(SESS_ADD_REM_BEAN, arb);
		session.setAttribute(REMOVE_ALL, removeAll);
		session.setAttribute(PROJECT_IDX, projNum);
		logger.debug("doDeleteConfirm");
	}

/**
 * Reads in form parameters, sets them to the bean variables, and confirm
 * with user
 */
	public void doAddConfirm(ActionRequest request, ActionResponse response) {

		String res = request.getParameter(RESOURCE);
		String origResource = request.getParameter(ORIGINAL_RESOURCE);
		String chargeNum = request.getParameter(CHARGE_NUMBER);

		AddRemoveBean arb = new AddRemoveBean();
		arb.setResource(res);
		arb.setChargeNumber(chargeNum);
		arb.setOrigResource(origResource);

		String[] names = request.getParameterValues(USER);
		if (names != null && names.length > 0) {
			List<String> userList = new LinkedList<String>();
			for (int i = 0; i < names.length; i++) {
				userList.add(names[i]);
			}
			arb.setUserList(userList);
		} else {
			// Error PI did not select any users
			// Go back to allocations listing b/c this situation shouldn't happen
			response.setRenderParameter(STATUS, "");
			response.setRenderParameter(ACTION, "");
			return;
		}
		response.setRenderParameter(STATUS, CONFIRM);
		response.setRenderParameter(ACTION, PI_ADD);
		request.getPortletSession().setAttribute(SESS_ADD_REM_BEAN, arb);
	}

/**
 * Sends email to admins to add users
 */
public void doDeleteComplete(ActionRequest request, ActionResponse response) {

        PortletSession session = request.getPortletSession();
        AddRemoveBean arb = (AddRemoveBean) session.getAttribute(SESS_ADD_REM_BEAN);

        // Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
        // String piPortalUsername = (userInfo!=null) ? (String)
        // userInfo.get("user.name") : "";

        // Get username
        String piPortalUsername = null;
        try {
            piPortalUsername = getPortalUsername(request);
        } catch (Exception e) {
            logger.error("Unable to retrieve portal username!", e);
        }

        // code to check for removeall and complete removeall
        String removeAll = request.getParameter(REMOVE_ALL);
        String proj = request.getParameter(PROJECT_IDX);
        int projNum = Integer.parseInt(proj); // get the project number in projects[] for list of all allocations
        if (removeAll.equals(REMOVE_ALL_YES) && arb != null) {
            // get all params
            String chargeNum = arb.getChargeNumber();
            List<String> userList = arb.getUserList();
            Project[] projects = null;
            projects = (Project[]) session.getAttribute(SESS_PROJECT_LIST);

            if ((projects == null) || (projects.length == 0)) { // If projects not available in session abort
                logger.info("ERROR: In PI_DELETE_COMPLETE, cannot get projects[] from session");
            } else {
                // get allocations[] for project number from projects []
                Allocation[] allocations = projects[projNum].getAllocations();
                // WeakHashMap resShortNameHash = DBUtil.getShortNameMap();
                // get the user from PI_delete user list
                Iterator<String> it = userList.iterator();
                while (it.hasNext()) {
                    String item = (String) it.next();
                    String[] fields = item.split("=");
                    if (fields.length == 4) {
                        String popsPersonId = fields[0];
                        String personId = fields[1];
                        String lastName = fields[2];
                        String firstName = fields[3];

                        String status = "pending";
                        String action = "remove";

                        // make a list of all allocations for the user
                        List<String> userAllocList = new LinkedList<String>();
                        for (int i = 0; i < allocations.length; i++) {
                            // get allocation name
                            String origResource = allocations[i].getResource();

                            // if allocation is expired then ignore.
                            if (allocations[i].isExpired()) {
                                continue;
                            }
                            // get users list for the allocation
                            User[] users = allocations[i].getUsers();
                            // search for username in user list
                            for (int u = 0; u < users.length; u++) {
                                // if username matches then add allocation to hash map
                                // logger.info("users[u].getLastName(): "+users[u].getLastName()+" users[u].getFirstName(): "+users[u].getFirstName());
                                // logger.info("userListlastName: "+lastName+" userListfirstName: "+firstName);
                                if (lastName.equals(users[u].getLastName())
                                        && firstName.equals(users[u].getFirstName())) {
                                    userAllocList.add(origResource);
                                    logger.info(lastName + " " + firstName + " found in allocation. Resource added to allocationlist: " + origResource);
                                    break;
                                }
                            }// end for(int u=0; u < users.length; u++)
                        }// end for for(int i = 0; i < allocations.length; i++)

                        // for each allocation of the user Add a row in the database
                        int insertResult = 0;
                        Iterator<String> it1 = userAllocList.iterator();
                        while (it1.hasNext()) {
                            String origResource = it1.next();
                            try {
                                PersonGateway pg = new PersonGatewayXSEDE();
                                insertResult = pg.insertPiAddRemoveRequest(status, chargeNum, origResource, Integer.parseInt(personId), Integer.parseInt(popsPersonId), firstName, lastName, piPortalUsername, action);
                                logger.info("Portal Username " + piPortalUsername + " is Adding to TGCDB: " + status + ", " + chargeNum + ", " + origResource + ", " + popsPersonId + ", " + firstName + ", " + lastName + ", " + action);
                                logger.info("INSERT RESULT: " + insertResult);
                                pg.close();
                            } catch (SQLException e) {
                                logger.error("Error", e);
                            }
                        }// end while(it1.hasnext); for each allocation of user

                    }// if fields lenght == 4
                }// end while(it.hasNext()); for each user in the PI_delete userlist

                // remove session attributes no longer needed
                session.removeAttribute(SESS_ADD_REM_BEAN);
                session.removeAttribute(REMOVE_ALL);
                session.removeAttribute(PROJECT_IDX);

                // set render parameter
                response.setRenderParameter(STATUS, COMPLETE);
                response.setRenderParameter(ACTION, PI_DELETE);
            }// end if projects == null
        } else {
            if (arb != null) {
                String origResource = arb.getOrigResource();
                String chargeNum = arb.getChargeNumber();
                List<String> userList = arb.getUserList();

                int insertResult = 0;

                logger.info("***NEW REMOVE EXISTING USER REQUEST***");

                Iterator<String> it = userList.iterator();
                while (it.hasNext()) {
                    String item = (String) it.next();
                    String[] fields = item.split("=");
                    if (fields.length == 4) {
                        String popsPersonId = fields[0];
                        String personId = fields[1];
                        String lastName = fields[2];
                        String firstName = fields[3];

                        String status = "pending";
                        String action = "remove";
                        // Add a row in the database for this request
                        try {
                            PersonGateway pg = new PersonGatewayXSEDE();
                            insertResult = pg.insertPiAddRemoveRequest(status, chargeNum, origResource, Integer.parseInt(personId), Integer.parseInt(popsPersonId), firstName, lastName, piPortalUsername, action);
                            logger.info("Portal Username " + piPortalUsername + " is Adding to TGCDB: " + status + ", " + chargeNum + ", " + origResource + ", " + popsPersonId + ", " + firstName + ", " + lastName + ", " + action);
                            logger.info("INSERT RESULT: " + insertResult);
                            pg.close();
                        } catch (SQLException e) {
                            logger.error("Error", e);
                        }
                    }
                }// close while

                // remove session attributes no longer needed
                session.removeAttribute(SESS_ADD_REM_BEAN);
                session.removeAttribute(REMOVE_ALL);
                session.removeAttribute(PROJECT_IDX);

                response.setRenderParameter(STATUS, COMPLETE);
                response.setRenderParameter(ACTION, PI_DELETE);
            }
        } // end if removal == yes //pNut200906
    }
/**
 * Sends email to admins to add users
 */
	public void doAddComplete(ActionRequest request, ActionResponse response) {
		PortletSession session = request.getPortletSession();
		AddRemoveBean arb = (AddRemoveBean) session.getAttribute(SESS_ADD_REM_BEAN);

		// Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
		// String piPortalUsername = (userInfo!=null) ? (String) userInfo.get("user.name") : "";

		// Get username
		String piPortalUsername = null;
		try {
			piPortalUsername = getPortalUsername(request);
		} catch (Exception e) {
			logger.error("Unable to retrieve portal username!", e);
		}
		int insertResult = 0;

		if (arb != null) {
			String origResource = arb.getOrigResource();
			String chargeNum = arb.getChargeNumber();
			List<String> userList = arb.getUserList();

			Iterator<String> it = userList.iterator();

			logger.info("***NEW ADD EXISTING USER REQUEST***");

			while (it.hasNext()) {
				String item = (String) it.next();
				String[] fields = item.split("=");
				if (fields.length == 4) {
					String popsPersonId = fields[0];
					String personId = fields[1];
					String lastName = fields[2];
					String firstName = fields[3];
					String status = "pending";
					String action = "add";

					// Add a row in the database for this request
					try {
						PersonGateway pg = new PersonGatewayXSEDE();
						insertResult = pg.insertPiAddRemoveRequest(status, chargeNum, origResource, Integer.parseInt(personId), Integer.parseInt(popsPersonId), firstName, lastName, piPortalUsername, action);
						pg.close();
						logger.info("Portal Username " + piPortalUsername + " is Adding to TGCDB: " + status + ", " + chargeNum + ", " + origResource + ", " + popsPersonId + ", " + firstName + ", " + lastName + ", " + action);
						logger.info("INSERT RESULT: " + insertResult);
					} catch (SQLException e) {
						logger.error("Error", e);
					}
				}// close if
			}// close while

			session.removeAttribute(SESS_ADD_REM_BEAN);
			response.setRenderParameter(STATUS, COMPLETE);
			response.setRenderParameter(ACTION, PI_ADD);
		}
	}

}

