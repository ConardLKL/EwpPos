package com.limahao.ticket.entity;

public class ItemTickerOrder extends ItemBase {
	String OrderNo;      
	String OrderTime;   
	String OrderCash;    
	String OrderNum;       
	String Status;  
	String UserPhone;   
	String IdNumber;    
	String Routename; 
	String LeaveDate;       
	String LeaveTime;        
	String Expired;
	String SitNo;
	String BusNo;
	String Gate;
	
	public String getOrderNo() {
		return OrderNo;
	}
	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}
	public String getOrderTime() {
		return OrderTime;
	}
	public void setOrderTime(String orderTime) {
		OrderTime = orderTime;
	}
	public String getOrderCash() {
		return OrderCash;
	}
	public void setOrderCash(String orderCash) {
		OrderCash = orderCash;
	}
	public String getOrderNum() {
		return OrderNum;
	}
	public void setOrderNum(String orderNum) {
		OrderNum = orderNum;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getUserPhone() {
		return UserPhone;
	}
	public void setUserPhone(String userPhone) {
		UserPhone = userPhone;
	}
	public String getIdNumber() {
		return IdNumber;
	}
	public void setIdNumber(String idNumber) {
		IdNumber = idNumber;
	}
	public String getLeaveDate() {
		return LeaveDate;
	}
	public void setLeaveDate(String leaveDate) {
		LeaveDate = leaveDate;
	}
	public String getLeaveTime() {
		return LeaveTime;
	}
	public void setLeaveTime(String leaveTime) {
		LeaveTime = leaveTime;
	}
	public String getExpired() {
		return Expired;
	}
	public void setExpired(String expired) {
		Expired = expired;
	}
	public String getRoutename() {
		return Routename;
	}
	public void setRoutename(String routename) {
		Routename = routename;
	}
	public String getSitNo() {
		return SitNo;
	}
	public void setSitNo(String sitNo) {
		SitNo = sitNo;
	}
	public String getBusNo() {
		return BusNo;
	}
	public void setBusNo(String busNo) {
		BusNo = busNo;
	}
	public String getGate() {
		return Gate;
	}
	public void setGate(String gate) {
		Gate = gate;
	}
	
}
