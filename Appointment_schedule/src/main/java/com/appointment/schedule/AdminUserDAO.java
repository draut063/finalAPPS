package com.appointment.schedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminUserDAO {

	public AdminUserBean checkAdminUserlogin(String Username,String Password_AU) {
		AdminUserBean abean=null;
		
		try {
				Connection con =DBConnect.getConnection();
				PreparedStatement prst=con.prepareStatement("SELECT * FROM AdminUsers Where Username=? and Password_AU=?");
				prst.setString(1, Username);
				prst.setString(2, Password_AU);
				
				ResultSet rs=prst.executeQuery();
				
				if(rs.next()) {
					abean=new AdminUserBean();
					
					abean.setUserID(rs.getInt(1));
					abean.setUsername(rs.getString(2));
					abean.setPassword_AU(rs.getString(3));
					abean.setFirst_Name(rs.getNString(4));
					abean.setLast_Name(rs.getString(5));
					abean.setMail(rs.getString(6));
					abean.setIsAdmin(rs.getString(7));
					abean.setIsActive(rs.getInt(8));
				}
				
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return abean;
		
	}
}
