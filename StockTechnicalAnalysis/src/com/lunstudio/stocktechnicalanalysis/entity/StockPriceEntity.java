package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

	public Date getTradeDate() {
		return tradeDate;
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

}
