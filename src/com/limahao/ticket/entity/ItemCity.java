package com.limahao.ticket.entity;


public class ItemCity extends ItemBase{
	String FirstLetter ="";// 首字母索引  例：H
	String spell ="";      // 全拼 注：暂时没数据
	String StopLetter =""; // 首字母缩写  例：杭州 hz
	String EndStation =""; // 站点名称 例：杭州
	String EndStationId =""; // 站点ID：9990
	
	
	public String getEndStationId() {
		return EndStationId;
	}

	public void setEndStationId(String endStationId) {
		EndStationId = endStationId;
	}

	public String getEndStation() {
		return EndStation;
	}

	public void setEndStation(String endStation) {
		this.EndStation = endStation;
	}

	public String getStopLetter() {
		return StopLetter;
	}

	public void setStopLetter(String stopLetter) {
		this.StopLetter = stopLetter;
	}

	public String getSpell() {
		return spell;
	}

	public void setSpell(String spell) {
		this.spell = spell;
	}

	public String getFirstLetter() {
		return FirstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.FirstLetter = firstLetter;
	}
	
	public void toUpperCase(){
		if(StopLetter != null) {
			this.StopLetter = StopLetter.toUpperCase();
		}
		if(FirstLetter != null){
			this.FirstLetter = FirstLetter.toUpperCase();
		}
		if(EndStation != null){
			this.EndStation = EndStation.toUpperCase();
		}
		if(spell != null){
			this.spell = spell.toUpperCase();
		}
	}
}
