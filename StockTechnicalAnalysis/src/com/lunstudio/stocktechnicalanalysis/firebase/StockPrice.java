package com.lunstudio.stocktechnicalanalysis.firebase;

import java.util.List;

public class StockPrice extends FirebaseData{

	private String s = null;	//Stock Code
	private String t = null;	//Trade Date

	private List<Object> data = null;	//Stock Data
	
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public List<Object> getData() {
		return data;
	}
	public void setData(List<Object> data) {
		this.data = data;
	}
	
}
