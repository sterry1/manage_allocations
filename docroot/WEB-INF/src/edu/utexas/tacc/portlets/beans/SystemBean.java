package edu.utexas.tacc.portlets.beans;

/**
 * @author maytal
 *
 * Bean to handle the system account information 
 *
 */
public class SystemBean implements Comparable {

		private String resourceName ;
        private String oldName;
        private String userName;
        private String institution;
        private String shortName;

        /**
         * @return Returns the resourceName.
         */
        public String getResourceName() {
                return resourceName;
        }
        /**
         * @param resourceName The resource name to set.
         */
        public void setResourceName(String resourceName) {
                this.resourceName = resourceName;
		  }
        /**
         * @return Returns the userName.
         */
        public String getUserName() {
                return userName;
        }
        /**
         * @param userName The user name to set.
         */
        public void setUserName(String userName) {
                this.userName = userName;
	    }
		public String getOldName() {
			return oldName;
		}
		public void setOldName(String oldName) {
			this.oldName = oldName;
		}
		
        public int compareTo(Object o) {
        	SystemBean other = (SystemBean)o;
        	return this.shortName.compareTo(other.shortName);
    	}
		public String getInstitution() {
			return institution;
		}
		public void setInstitution(String institution) {
			this.institution = institution;
		}
		public String getShortName() {
			return shortName;
		}
		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
}
