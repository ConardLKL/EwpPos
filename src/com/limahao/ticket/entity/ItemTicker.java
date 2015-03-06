package com.limahao.ticket.entity;

public class ItemTicker extends ItemBase {
	String BusType;      //  大高3
	String Routename;    // "杭州南-武义(快)"
	String LeaveTime;    // "07:55"
	String Gate;         //  "19"
	String Vehiclemode;  //"快客"
	String FullPrice;    //"8000"
	String HalfPrice;    //"4000"
	String SurplusTicket; //"36"
	String Mileage;        //"213"
	String SitType;        //"座位"
	String StartStationId; //"9991"
	String StartStation;   //杭州南站
	String BusNo;           //"9190"
	String LeaveDate;       //"2014-07-31"
	// 服务费
	String ServiceCash;    // 300 
	
	public String getServiceCash() {
		return ServiceCash;
	}
	public void setServiceCash(String serviceCash) {
		ServiceCash = serviceCash;
	}
	public String getBusType() {
		return BusType;
	}
	public void setBusType(String busType) {
		BusType = busType;
	}
	public String getRoutename() {
		return Routename;
	}
	public void setRoutename(String routename) {
		Routename = routename;
	}
	public String getLeaveTime() {
		return LeaveTime;
	}
	public void setLeaveTime(String leaveTime) {
		LeaveTime = leaveTime;
	}
	public String getGate() {
		return Gate;
	}
	public void setGate(String gate) {
		Gate = gate;
	}
	public String getVehiclemode() {
		return Vehiclemode;
	}
	public void setVehiclemode(String vehiclemode) {
		Vehiclemode = vehiclemode;
	}
	public String getFullPrice() {
		return FullPrice;
	}
	public void setFullPrice(String fullPrice) {
		FullPrice = fullPrice;
	}
	public String getHalfPrice() {
		return HalfPrice;
	}
	public void setHalfPrice(String halfPrice) {
		HalfPrice = halfPrice;
	}
	public String getSurplusTicket() {
		return SurplusTicket;
	}
	public void setSurplusTicket(String surplusTicket) {
		SurplusTicket = surplusTicket;
	}
	public String getMileage() {
		return Mileage;
	}
	public void setMileage(String mileage) {
		Mileage = mileage;
	}
	public String getSitType() {
		return SitType;
	}
	public void setSitType(String sitType) {
		SitType = sitType;
	}
	public String getStartStationId() {
		return StartStationId;
	}
	public void setStartStationId(String startStationId) {
		StartStationId = startStationId;
	}
	public String getStartStation() {
		return StartStation;
	}
	public void setStartStation(String startStation) {
		StartStation = startStation;
	}
	public String getBusNo() {
		return BusNo;
	}
	public void setBusNo(String busNo) {
		BusNo = busNo;
	}
	public String getLeaveDate() {
		return LeaveDate;
	}
	public void setLeaveDate(String leaveDate) {
		LeaveDate = leaveDate;
	}
	@Override
	public String toString() {
		return "ItemTicker [BusType=" + BusType + ", Routename=" + Routename
				+ ", Leavetime=" + LeaveTime + ", Gate=" + Gate
				+ ", Vehiclemode=" + Vehiclemode + ", FullPrice=" + FullPrice
				+ ", HalfPrice=" + HalfPrice + ", SurplusTicket="
				+ SurplusTicket + ", Mileage=" + Mileage + ", SitType="
				+ SitType + ", StartStationId=" + StartStationId
				+ ", StartStation=" + StartStation + ", BusNo=" + BusNo
				+ ", LeaveDate=" + LeaveDate + "]";
	}
	
}
