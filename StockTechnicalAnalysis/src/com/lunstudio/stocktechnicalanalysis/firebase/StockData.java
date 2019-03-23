package com.lunstudio.stocktechnicalanalysis.firebase;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;

public class StockData extends FirebaseData{
	private String c;
	private Boolean isHSCE;
	private Boolean isHSI;
	private String s;
	
	public StockData(StockEntity stock) {
		super();
		this.c = stock.getStockCname();
		this.isHSI = stock.getIsHSI();
		this.isHSCE = stock.getIsHSCE();
		this.s = stock.getStockCode();
		return;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public Boolean getIsHSCE() {
		return isHSCE;
	}
	public void setIsHSCE(Boolean isHSCE) {
		this.isHSCE = isHSCE;
	}
	public Boolean getIsHSI() {
		return isHSI;
	}
	public void setIsHSI(Boolean isHSI) {
		this.isHSI = isHSI;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	
	

}
