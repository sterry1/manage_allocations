package edu.utexas.tacc.portlets

/**
 * Created with IntelliJ IDEA.
 * User: steve
 * Date: 8/8/12
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */

def tup = new PersonGatewayTUP()

Project[] pjs = tup.findProjects("sterry1")

pjs.each{ Project p ->
    println "${p.accountID}"
}

println()





