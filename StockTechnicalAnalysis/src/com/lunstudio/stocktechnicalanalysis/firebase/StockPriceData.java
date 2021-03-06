package com.lunstudio.stocktechnicalanalysis.firebase;

import java.math.RoundingMode;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class StockPriceData extends FirebaseData{
	
	private String s = null;	//Stock Code
	private String t = null;	//Trade Date	
	private Double c = null;	//Close Price
	private Double o = null;	//Open Price
	private Double h = null;	//Day High
	private Double l = null;	//Day Low
	private Double d = null;	//Day Change
	private Long v = null;		//Volume
	
	private Double oc = null;	//Open Price Change
	
	private Double sr = null;	//Short RSI
	private Double lr = null;	//Long RSI
	private String rc = null;	//RSI-Cross
	
	private Double dm = null;
	private Double dms = null;
	private String dmc = null;	//Daily MACD line Cross Signal
	
	private Double wm = null;
	private Double wms = null;
	
	private Double dhd = null;	//Daily Histogram Diff.
	private String dhc = null;	//Daily Histogram Change

	private Double d10 = null;	//Break 10-days Diff
	private Double d20 = null;	//Break 20-days Diff
	
	private Double ss = null;	//Short Sma
	private String ssc = null;	//Short Sma Cross
	private Double ms = null;	//Medium Sma
	private String msc = null;	//Medium Sma Cross
	private Double ls = null;	//Long Sma
	private String lsc = null;	//Long Sma Cross
	
	private Double iv = null;	//ImplVol
	private Long opc = null;	//Option Call Count
	private Long opp = null;	//Option Put Count
	private Long opoic = null;	//Option open interest call count
	private Long opoip = null;	//Option open interest put count

	public StockPriceData(StockPriceEntity stockPrice) {
		this.s = stockPrice.getStockCode();
		this.t = stockPrice.getTradeDate().toString();
		this.c = stockPrice.getClosePrice().setScale(3, RoundingMode.HALF_UP).doubleValue();
		return;
	}

	public void initDetail(StockPriceEntity stockPrice) {
		
		this.s = stockPrice.getStockCode();
		this.t = stockPrice.getTradeDate().toString();
		this.o = stockPrice.getOpenPrice().setScale(3, RoundingMode.HALF_UP).doubleValue();
		this.c = stockPrice.getClosePrice().setScale(3, RoundingMode.HALF_UP).doubleValue();
		this.h = stockPrice.getHighPrice().setScale(3, RoundingMode.HALF_UP).doubleValue();
		this.l = stockPrice.getLowPrice().setScale(3, RoundingMode.HALF_UP).doubleValue();
		this.v = stockPrice.getDayVolume().longValue();
		this.sr = stockPrice.getShortRsi().setScale(5, RoundingMode.HALF_UP).doubleValue();
		this.lr = stockPrice.getLongRsi().setScale(5, RoundingMode.HALF_UP).doubleValue();
		this.dm = stockPrice.getMacd().setScale(5, RoundingMode.HALF_UP).doubleValue();
		this.dms = stockPrice.getMacdSignal().setScale(5, RoundingMode.HALF_UP).doubleValue();
		this.ss = stockPrice.getShortSma().setScale(3, RoundingMode.HALF_UP).doubleValue();
		this.ms = stockPrice.getMediumSma().setScale(3, RoundingMode.HALF_UP).doubleValue();
		this.ls = stockPrice.getLongSma().setScale(3, RoundingMode.HALF_UP).doubleValue();
		return;
	}

	public Long getOpc() {
		return opc;
	}

	public void setOpc(Long opc) {
		this.opc = opc;
	}

	public Long getOpp() {
		return opp;
	}

	public void setOpp(Long opp) {
		this.opp = opp;
	}

	public Long getOpoic() {
		return opoic;
	}

	public void setOpoic(Long opoic) {
		this.opoic = opoic;
	}

	public Long getOpoip() {
		return opoip;
	}

	public void setOpoip(Long opoip) {
		this.opoip = opoip;
	}

	public Double getIv() {
		return iv;
	}

	public void setIv(Double iv) {
		this.iv = iv;
	}

	public Double getOc() {
		return oc;
	}

	public void setOc(Double oc) {
		this.oc = oc;
	}

	public String getSsc() {
		return ssc;
	}

	public void setSsc(String ssc) {
		this.ssc = ssc;
	}

	public String getMsc() {
		return msc;
	}

	public void setMsc(String msc) {
		this.msc = msc;
	}

	public String getLsc() {
		return lsc;
	}

	public void setLsc(String lsc) {
		this.lsc = lsc;
	}

	public Double getSs() {
		return ss;
	}

	public void setSs(Double ss) {
		this.ss = ss;
	}

	public Double getMs() {
		return ms;
	}

	public void setMs(Double ms) {
		this.ms = ms;
	}

	public Double getLs() {
		return ls;
	}

	public void setLs(Double ls) {
		this.ls = ls;
	}

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

	public Double getC() {
		return c;
	}

	public void setC(Double c) {
		this.c = c;
	}

	public Double getO() {
		return o;
	}

	public void setO(Double o) {
		this.o = o;
	}

	public Double getH() {
		return h;
	}

	public void setH(Double h) {
		this.h = h;
	}

	public Double getL() {
		return l;
	}

	public void setL(Double l) {
		this.l = l;
	}

	public Double getD() {
		return d;
	}

	public void setD(Double d) {
		this.d = d;
	}

	public Long getV() {
		return v;
	}

	public void setV(Long v) {
		this.v = v;
	}

	public Double getSr() {
		return sr;
	}

	public void setSr(Double sr) {
		this.sr = sr;
	}

	public Double getLr() {
		return lr;
	}

	public void setLr(Double lr) {
		this.lr = lr;
	}

	public String getRc() {
		return rc;
	}

	public void setRc(String rc) {
		this.rc = rc;
	}

	public Double getDm() {
		return dm;
	}

	public void setDm(Double dm) {
		this.dm = dm;
	}

	public Double getDms() {
		return dms;
	}

	public void setDms(Double dms) {
		this.dms = dms;
	}

	public String getDmc() {
		return dmc;
	}

	public void setDmc(String dmc) {
		this.dmc = dmc;
	}

	public Double getWm() {
		return wm;
	}

	public void setWm(Double wm) {
		this.wm = wm;
	}

	public Double getWms() {
		return wms;
	}

	public void setWms(Double wms) {
		this.wms = wms;
	}

	public Double getDhd() {
		return dhd;
	}

	public void setDhd(Double dhd) {
		this.dhd = dhd;
	}

	public String getDhc() {
		return dhc;
	}

	public void setDhc(String dhc) {
		this.dhc = dhc;
	}

	public Double getD10() {
		return d10;
	}

	public void setD10(Double d10) {
		this.d10 = d10;
	}

	public Double getD20() {
		return d20;
	}

	public void setD20(Double d20) {
		this.d20 = d20;
	}	
	
}
