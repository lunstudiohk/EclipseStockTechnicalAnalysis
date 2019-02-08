package com.lunstudio.stocktechnicalanalysis.firebase;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;

public class WarrantPriceData extends FirebaseData{

	private String wc = null;	//Warrant Code
	private String t = null;	//Trade Date
	private String sc = null;	//Stock Code
	private Double sp = null;	//Stock Close
	private String wt = null;	//Warrant Type
	private String wi = null;	//Warrant Issuer
	private Double wp = null;	//Warrant Price
	private Double wv = null;	//Warrant Value
	private Double wiv = null;	//Warrant Implied Volatility
	private Double wo = null;	//Warrant Outstanding 
	private Double wiz = null;	//Warrant Issue Size
	private Double ws = null;	//Warrant Strike Price
	private Double wr = null;	//Warrant Ratio
	private Double wd = null;	//Warrant Delta
	private String wm = null;	//Warrant Maturity Date
	
	public WarrantPriceData(WarrantPriceEntity warrantPrice, StockPriceEntity stockPrice) {
		this.wc = warrantPrice.getWarrantCode();
		this.t = warrantPrice.getTradeDate().toString();
		this.sc = stockPrice.getStockCode();
		this.sp = stockPrice.getClosePrice().doubleValue();
		this.wt = warrantPrice.getWarrantType();
		this.wi = warrantPrice.getWarrantIssuer();
		this.wp = warrantPrice.getClosePrice().doubleValue();
		if( warrantPrice.getWarrantValue() != null ) {
			this.wv = warrantPrice.getWarrantValue().doubleValue();
		}
		if( warrantPrice.getImpVol() != null ) {
			this.wiv = warrantPrice.getImpVol().doubleValue();
		}
		this.wo = warrantPrice.getQustanding().doubleValue();
		this.ws = warrantPrice.getWarrantStrikePrice().doubleValue();
		this.wm = warrantPrice.getWarrantMaturityDate().toString();
		
		this.wiz = warrantPrice.getIssueSize().doubleValue();
		this.wr = warrantPrice.getWarrantRatio().doubleValue();
		if( warrantPrice.getDelta() != null ) {
			this.wd = warrantPrice.getDelta().doubleValue();
		}
		return;
	}
	
	public Double getWr() {
		return wr;
	}

	public void setWr(Double wr) {
		this.wr = wr;
	}

	public Double getWd() {
		return wd;
	}

	public void setWd(Double wd) {
		this.wd = wd;
	}

	public Double getWiz() {
		return wiz;
	}

	public void setWiz(Double wiz) {
		this.wiz = wiz;
	}

	public String getWc() {
		return wc;
	}
	public void setWc(String wc) {
		this.wc = wc;
	}
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public String getSc() {
		return sc;
	}
	public void setSc(String sc) {
		this.sc = sc;
	}
	public Double getSp() {
		return sp;
	}
	public void setSp(Double sp) {
		this.sp = sp;
	}
	public String getWt() {
		return wt;
	}
	public void setWt(String wt) {
		this.wt = wt;
	}
	public String getWi() {
		return wi;
	}
	public void setWi(String wi) {
		this.wi = wi;
	}
	public Double getWp() {
		return wp;
	}
	public void setWp(Double wp) {
		this.wp = wp;
	}
	public Double getWv() {
		return wv;
	}
	public void setWv(Double wv) {
		this.wv = wv;
	}
	public Double getWiv() {
		return wiv;
	}
	public void setWiv(Double wiv) {
		this.wiv = wiv;
	}
	public Double getWo() {
		return wo;
	}
	public void setWo(Double wo) {
		this.wo = wo;
	}
	public Double getWs() {
		return ws;
	}
	public void setWs(Double ws) {
		this.ws = ws;
	}
	public String getWm() {
		return wm;
	}
	public void setWm(String wm) {
		this.wm = wm;
	}

	
}
