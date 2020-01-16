package com.lunstudio.stocktechnicalanalysis.firebase;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class StockPrice extends FirebaseData {

	private final static String PRICE = "price";
	private final static String RSI = "rsi";
	private final static String MACD = "macd";
	private final static String SMA = "sma";
	
	private Map<String, Object> data = null;
	
	public Map<String, Object> getData() {
		return this.data;
	}
	
	private String stockCode;
	private Date date;
	
	public StockPrice(String stockCode, Date date) {
		data = new HashMap<String, Object>();
		data.put(PRICE, new HashMap<String, Object>());
		data.put(RSI, new HashMap<String, Object>());
		data.put(MACD, new HashMap<String, Object>());
		data.put(SMA, new HashMap<String, Object>());
		
		this.stockCode = stockCode;
		this.date = date;
		
		data.put("stock", stockCode);
		data.put("date", date.toString());
		return;
	}
	
	public String getStockCode() {
		return stockCode;
	}

	public Date getDate() {
		return date;
	}

	@SuppressWarnings("unchecked")
	public void setRsi(String period, Double val) {
		((HashMap<String, Double>) data.get(RSI)).put(period, val);
		return;
	}

	@SuppressWarnings("unchecked")
	public void setSma(String period, Double val) {
		((HashMap<String, Double>) data.get(SMA)).put(period, val);
		return;
	}
	
	@SuppressWarnings("unchecked")
	public void setPrice(Double open, Double close, Double high, Double low, Double volume) {
		((HashMap<String, Double>) data.get(PRICE)).put("o", open);
		((HashMap<String, Double>) data.get(PRICE)).put("c", close);
		((HashMap<String, Double>) data.get(PRICE)).put("h", high);
		((HashMap<String, Double>) data.get(PRICE)).put("l", low);
		((HashMap<String, Double>) data.get(PRICE)).put("v", volume);
		return;
	}
	
	@SuppressWarnings("unchecked")
	public void setMacd(Double macd, Double signal) {
		((HashMap<String, Double>) data.get(MACD)).put("macd", macd);
		((HashMap<String, Double>) data.get(MACD)).put("signal", signal);
		return;
	}
	
}
