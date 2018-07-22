package com.zsr.wechat_ticket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import com.zsr.wechat_ticket.entity.Ticket;
import com.zsr.wechat_ticket.entity.User;

@Repository
public class UserRepository {

	@Autowired
	public JdbcTemplate jdbc;

	void save(User user) {
		String INSERT_USER = "insert into user(username, department, staffNumber, office, email, province, city) "
				+"values(?,?,?,?,?,?,?); ";
		String INSERT_PASSWORD = "insert into password_email(staffNumber) values(?);";
		
		jdbc.update(INSERT_USER, user.getUserName(), user.getDepartment(), user.getStaffNumber(),
				user.getOffice(), user.getEmail(), user.getProvince(), user.getCity());
		jdbc.update(INSERT_PASSWORD, user.getStaffNumber());
	}
	
	void setBindFlag(String staffNumber, String bindFlag) {
		String UPDATE_BINDFLAG = "update user set bindFlag=? where staffNumber = ?";
		jdbc.update(UPDATE_BINDFLAG, bindFlag, staffNumber);
	}
	
	void setOpenId(String staffNumber, String openId) {
		String UPDATE_OPENID = "update user set openId=? where staffNumber = ?";
		jdbc.update(UPDATE_OPENID, openId, staffNumber);
	}
	
	void remove(String staffNumber) {
		String DELETE_USER = "delete from user where staffNumber = ?;";
		String DELETE_PASSWORD = " delete from password_email where staffNumber = ?;";
		
		jdbc.update(DELETE_USER, staffNumber);
		jdbc.update(DELETE_PASSWORD, staffNumber);
	}
	
	User convertUser(ResultSet rs) throws SQLException {
		User user = new User();
		
		user.setId(rs.getInt("id"));
		user.setStaffNumber(rs.getString("staffNumber"));
		user.setUserName(rs.getString("userName"));
		user.setGender(rs.getString("gender"));
		user.setDepartment(rs.getString("department"));
		user.setOffice(rs.getString("office"));
		user.setEmail(rs.getString("email"));
		user.setBindFlag(rs.getString("bindFlag"));
		user.setProvince(rs.getString("province"));
		user.setCity(rs.getString("city"));
		
		return user;
	}
	
	List<User> findByName(String department, String office, String username) {
		String SELECT_USER_BY_NAME = "select * from user";
		List<String> params = new ArrayList<>();
		String WHERE = "";
		if(department != null) {
			WHERE += "department = ? and ";
			params.add(department);
		}
		if(office != null) {
			WHERE += "office = ? and ";
			params.add(office);
		}
		if(username != null) {
			WHERE += "username = ? and ";
			params.add(username);
		}
		if(!WHERE.equals("")) {
			WHERE = WHERE.replaceAll(" and $", ";");
			SELECT_USER_BY_NAME += " where " + WHERE;
		}
		return jdbc.query(SELECT_USER_BY_NAME, params.toArray(), (rs, rn)->{
			return convertUser(rs);
		});
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
	
	User findByOpenId(String openId) {
		String SELECT_USER = "select * from user where openid = ?";
		List<User> users = jdbc.query(SELECT_USER, (rs, rn)->{
			return convertUser(rs);
		}, openId);
		
		if(users.size() == 0) return null;
		else return users.get(0);
	}
	
	void setPassword(String staffNumber, String password) {
		String UPDATE_PASSWORD = "update password_email set password=?, sendTime=null where staffNumber = ?;";
		jdbc.update(UPDATE_PASSWORD, password, staffNumber);
	}
	
	String fetchPassword(String staffNumber) {
		String SELECT_PASSWORD = "select password from password_email where staffNumber = ?";
		final String[] ret = new String[1];
		jdbc.query(SELECT_PASSWORD, rch->{
			ret[0] = rch.getString("password");
		});
		
		return ret[0];
	}
	
	void setSendTime(String staffNumber, Date date) {
		String UPDATE_SENDTIME = "update password_email set sendTime = ? where staffNumber = ?";
		jdbc.update(UPDATE_SENDTIME, date, staffNumber);
	}
	
	Map<String, Map<String, Object>> findAllPassword() {
		Map<String, Map<String, Object>> ret = new HashMap<>();
		String SELECT_PASSWORD = "select staffNumber, password, sendTime from password_email;";

		jdbc.query(SELECT_PASSWORD, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet arg0) throws SQLException {
				Map<String, Object> row = new HashMap<>();
				String st = arg0.getString("staffNumber");
				String pa = arg0.getString("password");
				Date se = arg0.getDate("sendTime");
				row.put("staffNumber", st);
				row.put("password", pa);
				row.put("sendTime", se);
				ret.put(st, row);
			}
			
		});
		
		return ret;
	}
	
	void addTicket(Ticket ticket) {
		if(ticket.getBookTime() == null) {
			ticket.setBookTime(new Date());
		}
		String INSERT_TICKET = "insert into ticket(staffNumber, bookTime, useDate, useTime, usedFlag) values(?,?,?,?,?)";
		jdbc.update(INSERT_TICKET, ticket.getStaffNumber(), ticket.getBookTime(), ticket.getUseDate(), 
				ticket.getUseTime(), ticket.getUsedFlag());
	}
	
	void deleteTicket(Ticket ticket) {
		String DELETE_TICKET = "delete from ticket where staffNumber = ? and userDate = ? and userTime = ?;";
		jdbc.update(DELETE_TICKET, ticket.getStaffNumber(), ticket.getUseDate(), ticket.getUseTime());
	}
	
	void deleteAllTickets() {
		String DELETE_TICKETS = "delete from ticket";
		jdbc.update(DELETE_TICKETS);
	}
	
	List<Ticket> findTickets(String staffNumber, String downDate, String upDate) {
		List<String> params = new ArrayList<>();

		String WHERE = "";
		String SELECT_TICKET = "select * from ticket ";
		if(staffNumber != null) {
			WHERE += "staffNumber = ? and ";
			params.add(staffNumber);
		}
		if(downDate != null) {
			WHERE += "useDate >= ? and ";
			params.add(downDate);
		}
		if(upDate != null) {
			WHERE += "useDate <= ? and ";
			params.add(upDate);
		}
		if(!WHERE.equals("")) {
			WHERE = WHERE.replaceAll(" and $", ";");
			SELECT_TICKET += " where " + WHERE;
		}
		
		List<Ticket> retli = jdbc.query(SELECT_TICKET, params.toArray(), (rs, rn)->{
			Ticket t = new Ticket();
			t.setId(rs.getInt("id"));
			t.setStaffNumber(rs.getString("staffNumber"));
			t.setUseDate(rs.getString("useDate"));
			t.setUseTime(rs.getString("useTime"));
			t.setBookTime(rs.getDate("bookTime"));
			t.setUsedFlag(rs.getString("usedFlag"));
			
			return t;
		});
		
		return retli;
	}
}
