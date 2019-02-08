package com.lunstudio.stocktechnicalanalysis.firebase;

import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class CbbcPriceData extends FirebaseData {

	private String cc = null;	//Cbbc Code
	private String t = null;	//Trade Date
	private String sc = null;	//Stock Code
	private Double sp = null;	//Stock Close
	private String ct = null;	//Cbbc Type
	private String ci = null;	//Cbbc Issuer
	private Double cp = null;	//Cbbc Price
	private Double co = null;	//Cbbc Outstanding 
	private Double ciz = null;	//Cbbc Issue Size
	private Double cs = null;	//Cbbc Strike Price
	private Double ccp = null;	//Cbbc Call Price
	private Double cr = null;	//Cbbc Ratio
	private String cm = null;	//Cbbc Maturity Date
	private String cl = null;	//Cbbc List Date
	
	public CbbcPriceData(CbbcPriceEntity cbbcPrice, StockPriceEntity stockPrice) {
		this.cc = cbbcPrice.getCbbcCode();
		this.t = cbbcPrice.getTradeDate().toString();
		this.sc = stockPrice.getStockCode();
		this.sp = stockPrice.getClosePrice().doubleValue();
		this.ct = cbbcPrice.getCbbcType();
		this.ci = cbbcPrice.getCbbcIssuer();
		this.cp = cbbcPrice.getClosePrice().doubleValue();
		this.ciz = cbbcPrice.getIssueSize().doubleValue();
		this.co = cbbcPrice.getQustanding().doubleValue();
		this.cs = cbbcPrice.getCbbcStrikeLevel().doubleValue();
		this.ccp = cbbcPrice.getCbbcCallLevel().doubleValue();
		this.cr = cbbcPrice.getCbbcRatio().doubleValue();
		this.cm = cbbcPrice.getCbbcMaturityDate().toString();
		this.cl = cbbcPrice.getCbbcListDate().toString();
		return;
	}
	
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
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
	public String getCt() {
		return ct;
	}
	public void setCt(String ct) {
		this.ct = ct;
	}
	public String getCi() {
		return ci;
	}
	public void setCi(String ci) {
		this.ci = ci;
	}
	public Double getCp() {
		return cp;
	}
	public void setCp(Double cp) {
		this.cp = cp;
	}
	public Double getCo() {
		return co;
	}
	public void setCo(Double co) {
		this.co = co;
	}
	public Double getCiz() {
		return ciz;
	}
	public void setCiz(Double ciz) {
		this.ciz = ciz;
	}
	public Double getCs() {
		return cs;
	}
	public void setCs(Double cs) {
		this.cs = cs;
	}
	public Double getCcp() {
		return ccp;
	}
	public void setCcp(Double ccp) {
		this.ccp = ccp;
	}
	public Double getCr() {
		return cr;
	}
	public void setCr(Double cr) {
		this.cr = cr;
	}
	public String getCm() {
		return cm;
	}
	public void setCm(String cm) {
		this.cm = cm;
	}
	public String getCl() {
		return cl;
	}
	public void setCl(String cl) {
		this.cl = cl;
	}
	
	
	
	
}
