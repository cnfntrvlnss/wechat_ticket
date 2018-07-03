package com.zsr.wechat_ticket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zsr.wechat_ticket.entity.User;

@Repository
public class UserRepository {

	@Autowired
	public JdbcTemplate jdbc;

	void save(User user) {
		String INSERT_USER = "insert into user(username, department, staffNumber, office, email) "
				+"values(?,?,?,?,?)";
		jdbc.update(INSERT_USER, user.getUserName(), user.getDepartment(), user.getStaffNumber(),
				user.getOffice(), user.getEmail());
	}
	
	void setPassword(String staffNumber, String password) {
		String UPDATE_PASSWD = "update user set password=? where staffNumber = ?";
		jdbc.update(UPDATE_PASSWD, password, staffNumber);
	}
	
	User convertUser(ResultSet rs) throws SQLException {
		User user = new User();
		
		user.setId(rs.getInt("id"));
		user.setUserName(rs.getString("userName"));
		user.setDepartment(rs.getString("department"));
		user.setOffice(rs.getString("office"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setStaffNumber(rs.getString("staffNumber"));
		
		return user;
	}
	
	List<User> findByName(String department, String office, String username) {
		String SELECT_USER_BY_NAME = "select * from user";
		List<String> params = new ArrayList<>();
		String WHERE = null;
		if(department != null) {
			WHERE = "department = ?";
			params.add(department);
		}
		if(office != null) {
			WHERE += "office = ?";
			params.add(office);
		}
		if(username != null) {
			WHERE += "username = ?";
			params.add(username);
		}
		if(WHERE != null) {
			SELECT_USER_BY_NAME += " where " + WHERE;
		}
		return jdbc.query(SELECT_USER_BY_NAME, (rs, rn)->{
			return convertUser(rs);
		}, params);
	}
	
	User findByNumber(String staffNumber) {
		String SELECT_USER = "select * from user where staffNumber = ?";
		List<User> users = jdbc.query(SELECT_USER, (rs, rn)->{
			return convertUser(rs);
		}, staffNumber);
		if(users.size() == 0) {
			return null;
		}else {
			return users.get(0);
		}
	}
	
}
