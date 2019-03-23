package com.lunstudio.stocktechnicalanalysis.firebase;

import java.math.RoundingMode;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;

public class CandlestickData extends FirebaseData {

	private String s = null;	//Stock Code
	private String d = null;	//Trade Date
	private String o = null;	//Buy or Sell
	private Integer t = null;	//Type
	private Double c = null;	//Confirm
	private Double l = null;	//Stop loss
	
	public CandlestickData(CandlestickEntity candlestick) {
		this.s = candlestick.getStockCode();
		this.d = candlestick.getTradeDate().toString();
		this.o = candlestick.getType();
		this.t = candlestick.getCandlestickType();
		this.c = candlestick.getConfirmPrice().setScale(3, RoundingMode.HALF_UP).doubleValue();
		this.l = candlestick.getStoplossPrice().setScale(3, RoundingMode.HALF_UP).doubleValue();
		return;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	public Integer getT() {
		return t;
	}

	public void setT(Integer t) {
		this.t = t;
	}

	public Double getC() {
		return c;
	}

	public void setC(Double c) {
		this.c = c;
	}

	public Double getL() {
		return l;
	}

	public void setL(Double l) {
		this.l = l;
	}

	
}
