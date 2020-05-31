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
	private BigDecimal highPrice;
	private BigDecimal lowPrice;
	private BigDecimal volume;
	
	private BigDecimal macd;
	
	private BigDecimal macdSignal;
		
	private BigDecimal shortRsi;
	
	private BigDecimal longRsi;

	private BigDecimal shortSma;
	
	private BigDecimal mediumSma;
	
	private BigDecimal longSma;
	
	public StockPriceEntity() {
		super();
		return;
	}
	
	public StockPriceEntity(StockPriceEntity that) {
		super();
		this.stockCode = that.stockCode;
		this.tradeDate = that.tradeDate;
		this.openPrice = that.openPrice.add(BigDecimal.ZERO);
		this.closePrice = that.closePrice.add(BigDecimal.ZERO);
		this.highPrice = that.highPrice.add(BigDecimal.ZERO);
		this.lowPrice = that.lowPrice.add(BigDecimal.ZERO);
		this.volume = that.volume.add(BigDecimal.ZERO);
		return;
	}
	
	public StockPriceEntity(String stockCode, String tradeDate, String type, JSONObject json) {
		super();
		this.setStockCode(stockCode);
		this.setTradeDate(Date.valueOf(tradeDate));
		this.setPriceType(type);
		this.setOpenPrice(new BigDecimal((String)json.get("1. open")));
		this.setHighPrice(new BigDecimal((String)json.get("2. high")));
		this.setLowPrice(new BigDecimal((String)json.get("3. low")));
		this.setClosePrice(new BigDecimal((String)json.get("4. close")));
		this.setDayVolume(new BigDecimal((String)json.get("5. volume")));
		return;
	}
	
	public StockPriceEntity(String stockCode, Date tradeDate, String type, JSONObject json) {
		super();
		this.setStockCode(stockCode);
		this.setTradeDate(tradeDate);
		this.setPriceType(type);
		this.setOpenPrice(new BigDecimal((String)json.get("1. open")));
		this.setHighPrice(new BigDecimal((String)json.get("2. high")));
		this.setLowPrice(new BigDecimal((String)json.get("3. low")));
		this.setClosePrice(new BigDecimal((String)json.get("4. close")));
		this.setDayVolume(new BigDecimal((String)json.get("5. volume")));
		return;
	}
	
	public static StockPriceEntity getStooqStockPriceEntity(String stockCode, String stooq) {
		StockPriceEntity stockPrice = new StockPriceEntity();
		//Date,Open,High,Low,Close,Volume 
		//2020-05-20,54.8,54.95,54,54.15,4703873
		String[] token = stooq.split(",");
		if( token.length >= 5 ) {
			stockPrice.setStockCode(stockCode);
			stockPrice.setPriceType("D");
			stockPrice.setTradeDate(DateUtils.getDateFromString(token[0]));
			stockPrice.setOpenPrice(new BigDecimal(token[1]));
			stockPrice.setHighPrice(new BigDecimal(token[2]));
			stockPrice.setLowPrice(new BigDecimal(token[3]));
			stockPrice.setClosePrice(new BigDecimal(token[4]));
			if( token.length > 5 ) {
				stockPrice.setDayVolume(new BigDecimal(token[5]));
			} else {
				//System.out.println(String.format("%s - %s : No Volume", stockCode, token[0]));
			}
			return stockPrice;
		} else {
			System.out.println(String.format("%s - %s", stockCode, stooq));
			return null;
		}
		
	}
	
	public BigDecimal getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(BigDecimal highPrice) {
		this.highPrice = highPrice;
	}

	public BigDecimal getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(BigDecimal lowPrice) {
		this.lowPrice = lowPrice;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getShortSma() {
		return shortSma;
	}

	public void setShortSma(BigDecimal shortSma) {
		this.shortSma = shortSma;
	}

	public BigDecimal getMediumSma() {
		return mediumSma;
	}

	public void setMediumSma(BigDecimal mediumSma) {
		this.mediumSma = mediumSma;
	}

	public BigDecimal getLongSma() {
		return longSma;
	}

	public void setLongSma(BigDecimal longSma) {
		this.longSma = longSma;
	}

	public BigDecimal getShortRsi() {
		return shortRsi;
	}

	public void setShortRsi(BigDecimal shortRsi) {
		this.shortRsi = shortRsi;
	}

	public BigDecimal getLongRsi() {
		return longRsi;
	}

	public void setLongRsi(BigDecimal longRsi) {
		this.longRsi = longRsi;
	}

	public BigDecimal getMacdHistogram() {
		//return macdHistogram;
		return this.getMacd().subtract(this.getMacdSignal());
	}
/*
	public void setMacdHistogram(BigDecimal dailyMacdHistogram) {
		this.macdHistogram = dailyMacdHistogram;
	}
*/
	public Date getTradeDate() {
		return tradeDate;
	}

	public BigDecimal getMacd() {
		return macd;
	}

	public void setMacd(BigDecimal macd) {
		this.macd = macd;
	}

	public BigDecimal getMacdSignal() {
		return macdSignal;
	}

	public void setMacdSignal(BigDecimal macdSignal) {
		this.macdSignal = macdSignal;
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

	public BigDecimal getDayLow() {
		return lowPrice;
	}

	public void setDayLow(BigDecimal dayLow) {
		this.lowPrice = dayLow;
	}

	public BigDecimal getDayVolume() {
		return volume;
	}

	public void setDayVolume(BigDecimal dayVolume) {
		this.volume = dayVolume;
	}

	public boolean isHollow() {
		if( this.getClosePrice().compareTo(this.getOpenPrice()) > 0 ) {
			return true;
		}
		return false;
	}
	
	public boolean isFilled() {
		if( this.getOpenPrice().compareTo(this.getClosePrice()) > 0 ) {
			return true;
		}
		return false;
	}
/*
	public Tick toTick() {
        ZonedDateTime date = DateUtils.getLocalDate(this.tradeDate).atStartOfDay(ZoneId.systemDefault());
        String open = this.openPrice.toString();
        String high = this.highPrice.toString();
        String low = this.lowPrice.toString();
        String close = this.closePrice.toString();
        String volume = "0";
    	try{
    		volume = this.volume.toString(); //Long.toString(this.dayVolume);
    	}catch(Exception e) {
    		
    	}
		return new Tick(date, open, high, low, close, volume);
	}
*/
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
		if( that == null ) {
			return false;
		}
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
		if( this.lowPrice.compareTo(that.lowPrice) != 0 ) {
			return false;
		}
		if( this.highPrice.compareTo(that.highPrice) != 0 ) {
			return false;
		}
		if( that.volume == null && that.volume != null ) {
			return false;
		}
		if( that.volume != null && that.volume == null ) {
			return false;
		}
		if( that.volume != null && that.volume != null ) {
			BigDecimal upperLimit = this.volume.multiply(BigDecimal.valueOf(1.05));
			BigDecimal lowerLimit = this.volume.multiply(BigDecimal.valueOf(0.95));
			if( that.volume.compareTo(upperLimit) > 0 || that.volume.compareTo(lowerLimit) < 0 ) {
				return false;
			}
		}
		return true;
	}

}
