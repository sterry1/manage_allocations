package edu.utexas.tacc.portlets;

import java.util.WeakHashMap;

public class DBUtil {
    
    // sentinel for a value that should not be displayed
    public static final String DEAD = "DEAD";

	public static WeakHashMap<String, String> getShortNameMap() {
		WeakHashMap<String, String> shortMap = new WeakHashMap<String, String>();

		//this block was pulled directly from the DB
		shortMap.put("abe.ncsa.teragrid","Abe");
		shortMap.put("albedo.psc.teragrid","Albedo");
		shortMap.put("abe-queenbee-steele.teragrid", "Abe/Queenbee/Steele");
		shortMap.put("asta.teragrid", "ECS Services");
		shortMap.put("ember.ncsa.teragrid","Ember");
		shortMap.put("abe-queenbee.teragrid", "Abe & Queen Bee");
		shortMap.put("mss.ncsa.teragrid", "NCSA Tape");
		shortMap.put("login-abe.ncsa.teragrid", "Abe");
		shortMap.put("login-ember.ncsa.teragrid", "Ember");
		shortMap.put("avidd-ia32.iu.teragrid", "Avidd");
		shortMap.put("bigred.iu.teragrid", "Big Red");
		shortMap.put("bluegene.sdsc.teragrid", "Blue Gene");
		shortMap.put("cloud.purdue.teragrid", "Cloud");
		shortMap.put("cobalt.ncsa.teragrid", "Cobalt");
		shortMap.put("collections.sdsc.teragrid", "Collections");
		shortMap.put("condor.purdue.teragrid", "Condor");
		shortMap.put("copper.ncsa.teragrid", "Copper");
		shortMap.put("database.sdsc.teragrid", "SDSC Database");
		shortMap.put("datastar-p655.sdsc.teragrid", "DataStar P655");
		shortMap.put("dash.sdsc.teragrid", "Dash");
		shortMap.put("datastar.sdsc.teragrid", "DataStar P690");
		shortMap.put("forge.ncsa.teragrid", "Forge");
		shortMap.put("frost.ncar.teragrid", "Frost");
		shortMap.put("gpfs-wan.teragrid", "GPFS WAN");
		shortMap.put("gordon-ion.sdsc.teragrid", "Gordon ION");
		shortMap.put("gordon.sdsc.teragrid", "Gordon Compute");
		shortMap.put("hpss.iu.teragrid", "IU HPSS");
		shortMap.put("lemieux.psc.teragrid", "Lemieux");
		shortMap.put("blacklight.psc.teragrid", "Blacklight");
		shortMap.put("lonestar.tacc.teragrid", "Lonestar");
		shortMap.put("lonestar4.tacc.teragrid", "Lonestar");
		shortMap.put("longhorn.tacc.teragrid", "Longhorn");
		shortMap.put("nautilus.nics.teragrid", "Nautilus");
		shortMap.put("spur.tacc.teragrid", "Spur");
		shortMap.put("queenbee.loni-lsu.teragrid", "Queen Bee");
		shortMap.put("quarry.iu.teragrid", "Quarry");
		shortMap.put("pople.psc.teragrid", "Pople");
		shortMap.put("radium.ncsa.teragrid", "Radium");
		shortMap.put("radon.purdue.teragrid", "Radon");
		shortMap.put("steele.purdue.teragrid", "Steele");
		shortMap.put("ranger.tacc.teragrid", "Ranger");
		shortMap.put("tape.sdsc.teragrid", "SDSC Tape");
		shortMap.put("teragrid", "TeraGrid Cluster");
		shortMap.put("teragrid_roaming", "TeraGrid Roaming");
		shortMap.put("tiger.iu.teragrid", "Tiger");
		shortMap.put("viz.anl.teragrid", "ANL Vis");
		shortMap.put("kraken.nics.teragrid", "Kraken");
		shortMap.put("athena.nics.teragrid", "Athena");
		shortMap.put("tungsten.ncsa.teragrid", "Tungsten");
		shortMap.put("lincoln.ncsa.teragrid", "Lincoln");
		shortMap.put("trestles.sdsc.teragrid", "Trestles");
		shortMap.put("wispy.purdue.teragrid", "Wispy");

		//OSG
		shortMap.put("grid1.osg.xsede","Open Science Grid");

		// ORNL
		shortMap.put("nstg.ornl.teragrid","NSTG");

		//Staff
		shortMap.put("staff.teragrid","Staff Resources");


		return shortMap;
	}

    /**
     * returns a hashtable containing the mapping of resource names
     * identified in the TGCDB to the actual pingable hostnames of those resources.
     */
    public static WeakHashMap<String, String> getResourceMap() {
	WeakHashMap<String, String> resourceMap = new WeakHashMap<String, String>();
	//resourceMap.put("portal.teragrid","https://portal.teragrid.org");
	
	// IU

	//NCSA
	resourceMap.put("lincoln.ncsa.teragrid","lincoln.ncsa.uiuc.edu");
	resourceMap.put("forge.ncsa.teragrid","	login-forge.ncsa.xsede.org");

	// PSC
	resourceMap.put("blacklight.psc.teragrid","blacklight.psc.teragrid.org");
	
	// PURDUE
	resourceMap.put("steele.purdue.teragrid","tg-steele.purdue.teragrid.org");
	resourceMap.put("condor.purdue.teragrid","tg-condor.purdue.teragrid.org");
	
	// SDSC
	resourceMap.put("trestles.sdsc.teragrid","trestles.sdsc.edu");
	resourceMap.put("gordon.sdsc.teragrid","gordon.sdsc.teragrid.org");
	resourceMap.put("gordon-ion.sdsc.teragrid","gordon-ion.sdsc.teragrid.org");

	// TACC
	resourceMap.put("spur.tacc.teragrid","tg-login.spur.tacc.teragrid.org");
	resourceMap.put("ranger.tacc.teragrid","tg-login.ranger.tacc.teragrid.org");
	resourceMap.put("longhorn.tacc.teragrid","tg-login.longhorn.tacc.teragrid.org");	
	resourceMap.put("lonestar4.tacc.teragrid","lonestar.tacc.teragrid.org");	

	//NICS
	resourceMap.put("kraken.nics.teragrid","kraken-gsi.nics.utk.edu");

	//OSG
	resourceMap.put("grid1.osg.xsede","osg-xsede.grid.iu.edu");
	
	// RETIRED
	resourceMap.put("dash.sdsc.teragrid",DEAD);
	resourceMap.put("pople.psc.teragrid",DEAD);
	resourceMap.put("lonestar.tacc.teragrid",DEAD);
	resourceMap.put("brutus.purdue.teragrid",DEAD);
	resourceMap.put("dtf.caltech.teragrid",DEAD);
	resourceMap.put("radium.ncsa.teragrid",DEAD);
	resourceMap.put("cloud.purdue.teragrid",DEAD);
	resourceMap.put("lemieux.psc.teragrid",DEAD);
	resourceMap.put("avidd-ia32.iu.teragrid",DEAD);
	resourceMap.put("tiger.iu.teragrid",DEAD);
        resourceMap.put("datastar.sdsc.teragrid",DEAD);
        resourceMap.put("bluegene.sdsc.teragrid",DEAD);
	resourceMap.put("dtf.anl.teragrid",DEAD);
	resourceMap.put("dtf.sdsc.teragrid", DEAD);
	resourceMap.put("cobalt.ncsa.teragrid",DEAD);
	resourceMap.put("abe.ncsa.teragrid",DEAD);
	resourceMap.put("queenbee.loni-lsu.teragrid",DEAD);
	resourceMap.put("bigred.iu.teragrid",DEAD);
	resourceMap.put("frost.ncar.teragrid",DEAD);
	resourceMap.put("athena.nics.teragrid",DEAD);
	resourceMap.put("nstg.ornl.teragrid",DEAD);
	resourceMap.put("ember.ncsa.teragrid",DEAD);
	
	return resourceMap;
    }

    /**
     * returns a hashtable containing the mapping of resource names
     * identified in the TGCDB to the acronym of the institution that
     * the resource belongs to
     */
    public static WeakHashMap<String, String> getInstitutionMap() {
	
   	WeakHashMap<String, String> instMap = new WeakHashMap<String, String>();
   	instMap.put("portal.teragrid","User Portal");
	// ANL
   	instMap.put("dtf.anl.teragrid","UC/ANL");

	// IU
   	instMap.put("avidd-ia32.iu.teragrid","IU");
   	instMap.put("tiger.iu.teragrid","IU");
   	instMap.put("bigred.iu.teragrid","IU");

	// NCSA
   	instMap.put("dtf.ncsa.teragrid","NCSA");
   	instMap.put("cobalt.ncsa.teragrid","NCSA");
	instMap.put("radium.ncsa.teragrid","NCSA");
	instMap.put("abe.ncsa.teragrid","NCSA");
	instMap.put("ember.ncsa.teragrid","NCSA");
	instMap.put("mss.ncsa.teragrid","NCSA");
	instMap.put("lincoln.ncsa.teragrid","NCSA");

	//NCAR
	instMap.put("frost.ncar.teragrid","NCAR");

	// ORNL
   	instMap.put("nstg.ornl.teragrid","ORNL");

	// PSC
   	instMap.put("lemieux.psc.teragrid","PSC");
	instMap.put("pople.psc.teragrid","PSC");
	instMap.put("blacklight.psc.teragrid","PSC");

   	// PURDUE
	instMap.put("cloud.purdue.teragrid","Purdue");
   	instMap.put("radon.purdue.teragrid","Purdue");
   	instMap.put("steele.purdue.teragrid","Purdue");
   	instMap.put("condor.purdue.teragrid","Purdue");

	// SDSC
   	instMap.put("datastar.sdsc.teragrid","SDSC");
   	instMap.put("dtf.sdsc.teragrid","SDSC");
	instMap.put("bluegene.sdsc.teragrid","SDSC");
	instMap.put("trestles.sdsc.teragrid","SDSC");
	instMap.put("dash.sdsc.teragrid","SDSC");

	// TACC
   	instMap.put("spur.tacc.teragrid","TACC");
   	instMap.put("lonestar.tacc.teragrid","TACC");
   	instMap.put("lonestar4.tacc.teragrid","TACC");
	instMap.put("ranger.tacc.teragrid","TACC");
   	instMap.put("longhorn.tacc.teragrid","TACC");

	//LONI
	instMap.put("queenbee.loni-lsu.teragrid","LONI");

	//NICS
	instMap.put("kraken.nics.teragrid","NICS");
	instMap.put("athena.nics.teragrid","NICS");

	instMap.put("grid1.osg.xsede","USC");
   	return instMap;
    }
}
