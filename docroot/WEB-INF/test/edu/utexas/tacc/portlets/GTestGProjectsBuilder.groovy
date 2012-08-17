package edu.utexas.tacc.portlets

/**
 * Created with IntelliJ IDEA.
 * User: steve
 * Date: 8/8/12
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */

GProjectsBuilder g = new GProjectsBuilder()

def r = g.getProjects()

r.each { Project p ->
    println "${p.accountID} ${p.allocations.length}"
}
