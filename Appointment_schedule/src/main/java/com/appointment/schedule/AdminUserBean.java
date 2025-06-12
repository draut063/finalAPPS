package com.appointment.schedule;

public class AdminUserBean {

		private int UserID;
	    private String Username;
	    private String Password_AU;
	    private String First_Name;
	    private String Last_Name;
	    private String Mail;
	    private String IsAdmin;
	    private int IsActive;
	    
	    
		public int getUserID() {
			return UserID;
		}
		public void setUserID(int userID) {
			UserID = userID;
		}
		public String getUsername() {
			return Username;
		}
		public void setUsername(String username) {
			Username = username;
		}
		public String getPassword_AU() {
			return Password_AU;
		}
		public void setPassword_AU(String password_AU) {
			Password_AU = password_AU;
		}
		public String getFirst_Name() {
			return First_Name;
		}
		public void setFirst_Name(String first_Name) {
			First_Name = first_Name;
		}
		public String getLast_Name() {
			return Last_Name;
		}
		public void setLast_Name(String last_Name) {
			Last_Name = last_Name;
		}
		public String getMail() {
			return Mail;
		}
		public void setMail(String mail) {
			Mail = mail;
		}
		public String getIsAdmin() {
			return IsAdmin;
		}
		public void setIsAdmin(String isAdmin) {
			IsAdmin = isAdmin;
		}
		public int getIsActive() {  
			return IsActive;
		}
		public void setIsActive(int isActive) {
			IsActive = isActive;
		} 
	       
	    
	     public AdminUserBean() {}
	    
}
