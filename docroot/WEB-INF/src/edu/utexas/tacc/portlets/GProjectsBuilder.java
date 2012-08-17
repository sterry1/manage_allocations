package edu.utexas.tacc.portlets;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: steve
 * Date: 7/26/12
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
class GProjectsBuilder {

    public Project bindProject(JsonNode jsonProject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Project project = new Project();
        project.setAccountID(jsonProject.path("id").asInt());
        project.setTitle(jsonProject.path("title").asText());
        project.setChargeNumber(jsonProject.path("chargeCode").asText());

        // bind pi information
        JsonNode piNode = jsonProject.path("pi");
        User pi = mapper.readValue(piNode.toString(), User.class);


        // bind allocations list
        JsonNode allocationsNode = jsonProject.path("allocations");
        Map<String,Allocation> allocations = mapper.readValue(allocationsNode.toString(),new TypeReference<ArrayList<Allocation>>(){});
        //project.setAllocations(allocations);

        return project;
    }

    // populate two Projects and pass back an Array of Projects
    Project[] getProjects() throws IOException {
        Project[] projects = new Project[2];

        Project p1 = new Project();
        Project p2 = new Project();
        Allocation a2 = new Allocation();
        Allocation a1 = new Allocation();
        Allocation a3 = new Allocation();
        User u1 = new User("Karl","Schulz","kschulz@mail.tacc","kschulz",1626);
        User u2 = new User("Laura E","Timm","ltimm@mail.tacc","ltimm",4117);

        p1.setAccountID(489);
        p1.setChargeNumber("A-ccsc1");
        p1.setTitle("Mesoscale Numerical Weather Prediction");
        p1.setGrantNumber("36");
        p1.setAllocationMgr(true);
        p1.setIsPI(true);
        p1.setCoPI(false);
        p1.setPiId(1626);
        p1.setPiFirstName("Karl");
        p1.setPiLastName("Schulz");

        a1.setPiID(1626);
        a1.setResource("Lonestar(Old)");
        a1.setStartDate("2011-11-02");
        a1.setEndDate("2012-11-02");
        a1.setState("Active");
        a1.setBaseAllocation("10000");
        a1.setRequestID(24);
        a1.setUsedAllocation("755");
        a1.setType("renewal");
        a1.setRemainingAllocation("9245");


        p1.addAllocation(a1);


        p2.setAccountID(2);
        p2.setChargeNumber("test_charge2");
        p2.setTitle("Administrative project for Collab");
        p2.setGrantNumber("AdminCollab");
        p2.setAllocationMgr(true);
        p2.setIsPI(true);
        p2.setCoPI(true);
        p2.setPiId(1626);
        p2.setPiFirstName("Karl");
        p2.setPiLastName("Schulz");

        a2.setPiID(1626);
        a2.setResource("Ranger");
        a2.setStartDate("2011-11-02");
        a2.setEndDate("2012-11-02");
        a2.setState("Active");
        a2.setBaseAllocation("1000");
        a2.setRequestID(24);
        a2.setUsedAllocation("550");
        a2.setType("renewal");
        a2.setRemainingAllocation("450");

        p2.addAllocation(a2);

        projects[0]= p1;
        projects[1]= p2;

        return projects;

    }

    public Map getUsers(){
        Map<Integer, User> allUsers = new LinkedHashMap<Integer, User>();
        User u1 = new User("Karl","Schulz","kschulz","kschulz@mail.tacc","0",1626,"PI","active");
        User u2 = new User("Laura E","Timm","ltimm","ltimm@mail.tacc","0",4117,"PI","active");
        User u3 = new User("Steve","Terry","sterry1","sterry1@mail.tacc","20",5617,"Standard","active");
        User u4 = new User("Test","User","tuser","tuser@mail.tacc","30",2645,"Standard","inactive");
        User u5 = new User("Test2","User2","tuser2","tuser2@mail.tacc","25",2667,"Standard","active");
        User u6 = new User("Test3","User3","tuser3","tuser3@mail.tacc","55",3456,"Standard","active");
        //String first, String last, String username, String email, String used, int personID, String role, String status

        allUsers.put(u1.getPersonID(),u1);
        allUsers.put(u2.getPersonID(),u2);
        allUsers.put(u3.getPersonID(),u3);
        allUsers.put(u4.getPersonID(),u4);
        allUsers.put(u5.getPersonID(),u5);
        allUsers.put(u6.getPersonID(),u6);

        return allUsers;
    }

    // populate two Projects and pass back an Array of Projects
    Project[] getRestProjects() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        URL src = new URL("file:///Users/steve/development/scrap/akhil/tas-json.json");
        ArrayList<Project> restProjects = new ArrayList<Project>();
        JsonNode rootNode = mapper.readTree(src);


        System.out.println();

        // iterate over projectlist
        for(JsonNode projectNode : rootNode){
            // System.out.println(projectNode);
            Project project = this.bindProject(projectNode);
            restProjects.add(project);
            System.out.println(project.getTitle());
            // System.out.println(project.allocations);

        }
        System.out.println();
        // Project project = mapper.readValues()
        //ArrayList<Project> project = mapper.

        Project[] projects = new Project[restProjects.size()];
/*
        for(Project p : restProjects){

        }


        projects = restProjects.toArray();
*/
        // projects = new Project[2];

/*
        Project project1 = new Project();
        Project project2 = new Project();
        project1.setAccountID(1);
        project1.setChargeNumber("test_charge");
        project1.setTitle("MyProj");
        project1.setGrantNumber("myGrant");
        project1.setAllocationMgr(true);
        project1.setIsPI(true);
        project1.setCoPI(false);
        project1.setPiId(1);
        project1.setPiFirstName("Steve");
        project1.setPiLastName("Terry");

        project2.setAccountID(2);
        project2.setChargeNumber("test_charge2");
        project2.setTitle("Proj2");
        project2.setGrantNumber("Grant2");
        project2.setAllocationMgr(true);
        project2.setIsPI(true);
        project2.setCoPI(true);
        project2.setPiId(1);
        project2.setPiFirstName("Steve");
        project2.setPiLastName("Terry");

        projects[0]= project1;
        projects[1]= project2;
*/

        return projects;

    }

    public static void main(String[] args) throws IOException {
        GProjectsBuilder gp = new GProjectsBuilder();
        Project[] projects = gp.getProjects();
        Map<Integer, User> usrs = gp.getUsers();
        System.out.print("");
    }
}
