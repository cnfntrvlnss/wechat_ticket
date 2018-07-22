package com.zsr.wechat_ticket.entity;

import java.util.Date;

public class Ticket {
	int id;
	String staffNumber;
	Date bookTime;
	String useDate;
	String useTime;
	String usedFlag;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStaffNumber() {
		return staffNumber;
	}
	public void setStaffNumber(String staffNumber) {
		this.staffNumber = staffNumber;
	}
	public Date getBookTime() {
		return bookTime;
	}
	public void setBookTime(Date bookTime) {
		this.bookTime = bookTime;
	}
	public String getUseDate() {
		return useDate;
	}
	public void setUseDate(String useDate) {
		this.useDate = useDate;
	}
	public String getUseTime() {
		return useTime;
	}
	public void setUseTime(String useTime) {
		this.useTime = useTime;
	}
	public String getUsedFlag() {
		return usedFlag;
	}
	public void setUsedFlag(String useFlag) {
		this.usedFlag = useFlag;
	}
	
}
