package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.time.ZoneId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.json.simple.JSONObject;

import com.lunstudio.stocktechnicalanalysis.util.DateUtils;

import eu.verdelhan.ta4j.Tick;

@Entity
@Table(name = "tb_stockprice")
public class StockPriceEntity extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String PRICE_TYPE_DAILY = "D";
	public static final String PRICE_TYPE_WEEKLY = "W";
	public static final String PRICE_TYPE_MONTHLY = "M";

	@Id
	private Date tradeDate;

	@Id
	private String stockCode;

	@Id
	private String priceType;

	private BigDecimal openPrice;
	private BigDecimal closePrice;
	private BigDecimal dayHigh;
	private BigDecimal dayLow;
	private BigDecimal dayVolume;

	//private BigDecimal implVol;

	@Transient
	private BigDecimal dailyMacd;
	
	@Transient
	private BigDecimal dailyMacdSignal;

	@Transient
	private BigDecimal dailyMacdHistogram;
	
	@Transient
	private BigDecimal weeklyMacdSignal;
	
	@Transient
	private BigDecimal weeklyMacd;
	
	@Transient
	private BigDecimal weeklyMacdHistogram;
	
	@Transient
	private BigDecimal dailyShortRsi;
	
	@Transient
	private BigDecimal dailyLongRsi;

	@Transient
	private BigDecimal dailyShortSma;
	
	@Transient
	private BigDecimal dailyMediumSma;
	
	@Transient
	private BigDecimal dailyLongSma;
	
	public StockPriceEntity() {
		super();
		return;
	}
	
	public StockPriceEntity(String stockCode, String tradeDate, String type, JSONObject json) {
		super();
		this.setStockCode(stockCode);
		this.setTradeDate(Date.valueOf(tradeDate));
		this.setPriceType(type);
		this.setOpenPrice(new BigDecimal((String)json.get("1. open")));
		this.setDayHigh(new BigDecimal((String)json.get("2. high")));
		this.setDayLow(new BigDecimal((String)json.get("3. low")));
		this.setClosePrice(new BigDecimal((String)json.get("4. close")));
		this.setDayVolume(new BigDecimal((String)json.get("5. volume")));
		return;
	}
	
	public BigDecimal getDailyShortSma() {
		return dailyShortSma;
	}

	public void setDailyShortSma(BigDecimal dailyShortSma) {
		this.dailyShortSma = dailyShortSma;
	}

	public BigDecimal getDailyMediumSma() {
		return dailyMediumSma;
	}

	public void setDailyMediumSma(BigDecimal dailyMediumSma) {
		this.dailyMediumSma = dailyMediumSma;
	}

	public BigDecimal getDailyLongSma() {
		return dailyLongSma;
	}

	public void setDailyLongSma(BigDecimal dailyLongSma) {
		this.dailyLongSma = dailyLongSma;
	}

	public BigDecimal getDailyShortRsi() {
		return dailyShortRsi;
	}

	public void setDailyShortRsi(BigDecimal dailyShortRsi) {
		this.dailyShortRsi = dailyShortRsi;
	}

	public BigDecimal getDailyLongRsi() {
		return dailyLongRsi;
	}

	public void setDailyLongRsi(BigDecimal dailyLongRsi) {
		this.dailyLongRsi = dailyLongRsi;
	}

	public BigDecimal getWeeklyMacdSignal() {
		return weeklyMacdSignal;
	}

	public void setWeeklyMacdSignal(BigDecimal weeklyMacdSignal) {
		this.weeklyMacdSignal = weeklyMacdSignal;
	}

	public BigDecimal getWeeklyMacd() {
		return weeklyMacd;
	}

	public void setWeeklyMacd(BigDecimal weeklyMacd) {
		this.weeklyMacd = weeklyMacd;
	}

	public BigDecimal getWeeklyMacdHistogram() {
		return weeklyMacdHistogram;
	}

	public void setWeeklyMacdHistogram(BigDecimal weeklyMacdHistogram) {
		this.weeklyMacdHistogram = weeklyMacdHistogram;
	}

	public BigDecimal getDailyMacdHistogram() {
		return dailyMacdHistogram;
	}

	public void setDailyMacdHistogram(BigDecimal dailyMacdHistogram) {
		this.dailyMacdHistogram = dailyMacdHistogram;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public BigDecimal getDailyMacd() {
		return dailyMacd;
	}

	public void setDailyMacd(BigDecimal dailyMacd) {
		this.dailyMacd = dailyMacd;
	}

	public BigDecimal getDailyMacdSignal() {
		return dailyMacdSignal;
	}

	public void setDailyMacdSignal(BigDecimal dailyMacdSignal) {
		this.dailyMacdSignal = dailyMacdSignal;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

	public BigDecimal getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}

	public BigDecimal getDayHigh() {
		return dayHigh;
	}

	public void setDayHigh(BigDecimal dayHigh) {
		this.dayHigh = dayHigh;
	}

	public BigDecimal getDayLow() {
		return dayLow;
	}

	public void setDayLow(BigDecimal dayLow) {
		this.dayLow = dayLow;
	}

	public BigDecimal getDayVolume() {
		return dayVolume;
	}

	public void setDayVolume(BigDecimal dayVolume) {
		this.dayVolume = dayVolume;
	}

	public Tick toTick() {
        ZonedDateTime date = DateUtils.getLocalDate(this.tradeDate).atStartOfDay(ZoneId.systemDefault());
        String open = this.openPrice.toString();
        String high = this.dayHigh.toString();
        String low = this.dayLow.toString();
        String close = this.closePrice.toString();
        String volume = "0";
    	try{
    		volume = this.dayVolume.toString(); //Long.toString(this.dayVolume);
    	}catch(Exception e) {
    		
    	}
		return new Tick(date, open, high, low, close, volume);
	}

	@Override
    public boolean equals(Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !(obj instanceof StockPriceEntity) ) {
			return false;
		}
		
		StockPriceEntity that = (StockPriceEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.tradeDate, that.tradeDate);
		eb.append(this.stockCode, that.stockCode);
		return eb.isEquals();
	}
	
	public boolean isSame(StockPriceEntity that) {
		if( this.tradeDate.compareTo(that.tradeDate) != 0 ) {
			return false;
		}
		if( this.stockCode.compareTo(that.stockCode) != 0 ) {
			return false;
		}
		if( this.closePrice.compareTo(that.closePrice) != 0 ) {
			return false;
		}
		if( this.openPrice.compareTo(that.openPrice) != 0 ) {
			return false;
		}
		if( this.dayLow.compareTo(that.dayLow) != 0 ) {
			return false;
		}
		if( this.dayHigh.compareTo(that.dayHigh) != 0 ) {
			return false;
		}
		BigDecimal upperLimit = this.dayVolume.multiply(BigDecimal.valueOf(1.05));
		BigDecimal lowerLimit = this.dayVolume.multiply(BigDecimal.valueOf(0.95));
		if( that.dayVolume.compareTo(upperLimit) > 0 || that.dayVolume.compareTo(lowerLimit) < 0 ) {
			return false;
		}
		return true;
	}

}
