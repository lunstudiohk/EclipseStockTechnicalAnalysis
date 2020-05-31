package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="tb_stocksignal")
public class StockSignalEntity extends BaseEntity implements Serializable {

	public static final String SIGNAL_TYPE_BUY = "B";
	public static final String SIGNAL_TYPE_SELL = "S";
	
	public static final Integer MACD_BELOW_ZERO = 0;
	public static final Integer MACD_ABOVE_ZERO = 1;
	public static final Integer MACD_CROSS_ZERO = 2;
	public static final Integer MACD_LOWER = 3;
	public static final Integer MACD_HIGHER = 4;
	public static final Integer MACD_ABOVE_DAILY = 5;
	public static final Integer MACD_BELOW_DAILY = 6;

	public static final Integer RSI_ABOVE = 0;
	public static final Integer RSI_BELOW = 1;
	
	public static final Integer CANDLESTICK_FILLED = 0;
	public static final Integer CANDLESTICK_HALLOW = 1;
	public static final Integer CANDLESTICK_FILLED_GAPUP = 2;
	public static final Integer CANDLESTICK_FILLED_GAPDOWN = 3;
	public static final Integer CANDLESTICK_HALLOW_GAPUP = 4;
	public static final Integer CANDLESTICK_HALLOW_GAPDOWN = 5;
	
	public static final Integer SMA_SHORT_MEDIUM_LONG = 0;
	public static final Integer SMA_SHORT_LONG_MEDIUM = 1;
	public static final Integer SMA_MEDIUM_SHORT_LONG = 2;
	public static final Integer SMA_MEDIUM_LONG_SHORT = 3;
	public static final Integer SMA_LONG_SHORT_MEDIUM = 4;
	public static final Integer SMA_LONG_MEDIUM_SHORT = 5;

	public static final String STRENGTH_WEAK = "W";
	public static final String STRENGTH_STRONG = "S";
	public static final String STRENGTH_EQUAL = "E";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final static BigDecimal[] SMA = { BigDecimal.valueOf(10), BigDecimal.valueOf(20), BigDecimal.valueOf(50) };
	
	@Id
	private String stockCode;
	
	@Id
	private Date tradeDate;

	@Id
	private Integer signalSeq;
	
	@Id
	private String signalType;	//Buy or Sell
	
	private Integer type;
	
	private Integer period = 20;
	
	private String priceType;
	
	private BigDecimal targetReturn;
	
	private BigDecimal confident;
	
	private Integer count;
	
	private BigDecimal upperMin;
	private BigDecimal upperMedian;
	private BigDecimal upperMax;
	private Integer upperDayMedian;
	
	private BigDecimal lowerMin;
	private BigDecimal lowerMedian;
	private BigDecimal lowerMax;
	private Integer lowerDayMedian;

	private BigDecimal upperDailyRsi;
	private BigDecimal lowerDailyRsi;
	private BigDecimal upperDailySma;
	private BigDecimal lowerDailySma;
		
	private Integer lowerPeriod;
	private Integer upperPeriod;
	
	private String shortStrength;
	private String mediumStrength;
	private String longStrength;
	
	private Integer macdType;
	private Integer smaType;
	private Integer rsiType;
	
	private BigDecimal lowerPriceDiff;
	private BigDecimal upperPriceDiff;

	private Integer candlestickType;
	
	private Integer completed = 0;
	
	@Transient
	private Integer dayCount;
	
	@Transient
	private BigDecimal maxPrice;
	
	@Transient
	private Integer maxPriceDayCount;
	
	@Transient
	private BigDecimal minPrice;
	
	@Transient
	private Integer minPriceDayCount;
	
	@Transient
	private List<StockSignalDateEntity> stockSignalDateList;
	
	@Transient
	private Boolean isEmpty = true;
	
	public String getKeyString() {
		return String.format("%s%s%s%s%s", this.stockCode, this.tradeDate.toString(), this.signalSeq, this.tradeDate.toString(), this.signalType );
	}
		
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getSignalType() {
		return signalType;
	}

	public void setSignalType(String signalType) {
		this.signalType = signalType;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getUpperMin() {
		return upperMin;
	}

	public void setUpperMin(BigDecimal upperMin) {
		this.upperMin = upperMin;
	}

	public BigDecimal getUpperMax() {
		return upperMax;
	}

	public void setUpperMax(BigDecimal upperMax) {
		this.upperMax = upperMax;
	}

	public BigDecimal getLowerMin() {
		return lowerMin;
	}

	public void setLowerMin(BigDecimal lowerMin) {
		this.lowerMin = lowerMin;
	}

	public BigDecimal getLowerMax() {
		return lowerMax;
	}

	public void setLowerMax(BigDecimal lowerMax) {
		this.lowerMax = lowerMax;
	}

	public BigDecimal getUpperDailyRsi() {
		return upperDailyRsi;
	}

	public void setUpperDailyRsi(BigDecimal upperDailyRsi) {
		this.isEmpty = false;
		this.upperDailyRsi = upperDailyRsi;
	}

	public BigDecimal getLowerDailyRsi() {
		return lowerDailyRsi;
	}

	public void setLowerDailyRsi(BigDecimal lowerDailyRsi) {
		this.isEmpty = false;
		this.lowerDailyRsi = lowerDailyRsi;
	}
	
	public BigDecimal getUpperDailySma() {
		return upperDailySma;
	}

	public void setUpperDailySma(BigDecimal upperDailySma) {
		this.isEmpty = false;
		this.upperDailySma = upperDailySma;
	}

	public BigDecimal getLowerDailySma() {
		return lowerDailySma;
	}

	public void setLowerDailySma(BigDecimal lowerDailySma) {
		this.isEmpty = false;
		this.lowerDailySma = lowerDailySma;
	}

	public Integer getLowerPeriod() {
		return lowerPeriod;
	}

	public void setLowerPeriod(Integer lowerDailyMacdPeriod) {
		this.isEmpty = false;
		this.lowerPeriod = lowerDailyMacdPeriod;
	}

	public Integer getUpperPeriod() {
		return upperPeriod;
	}

	public void setUpperPeriod(Integer upperDailyMacdPeriod) {
		this.isEmpty = false;
		this.upperPeriod = upperDailyMacdPeriod;
	}

	public Integer getMacdType() {
		return macdType;
	}

	public void setMacdType(Integer macdType) {
		this.isEmpty = false;
		this.macdType = macdType;
	}

	public BigDecimal getLowerPriceDiff() {
		return lowerPriceDiff;
	}

	public void setLowerPriceDiff(BigDecimal lowerPriceDiff) {
		this.isEmpty = false;
		this.lowerPriceDiff = lowerPriceDiff;
	}

	public BigDecimal getUpperMedian() {
		return upperMedian;
	}

	public void setUpperMedian(BigDecimal upperMedian) {
		this.upperMedian = upperMedian;
	}

	public BigDecimal getLowerMedian() {
		return lowerMedian;
	}

	public void setLowerMedian(BigDecimal lowerMedian) {
		this.lowerMedian = lowerMedian;
	}
	
	public Boolean isEmpty() {
		return this.isEmpty;
	}
	
	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public Integer getRsiType() {
		return rsiType;
	}

	public void setRsiType(Integer rsiType) {
		this.isEmpty = false;
		this.rsiType = rsiType;
	}

	public Integer getCandlestickType() {
		return candlestickType;
	}

	public void setCandlestickType(Integer candlestickType) {
		this.candlestickType = candlestickType;
		this.isEmpty = false;
	}

	public BigDecimal getUpperPriceDiff() {
		return upperPriceDiff;
	}

	public void setUpperPriceDiff(BigDecimal upperPriceDiff) {
		this.upperPriceDiff = upperPriceDiff;
		this.isEmpty = false;
	}

	public Integer getUpperDayMedian() {
		return upperDayMedian;
	}

	public void setUpperDayMedian(Integer upperDayMedian) {
		this.upperDayMedian = upperDayMedian;
	}

	public Integer getLowerDayMedian() {
		return lowerDayMedian;
	}

	public void setLowerDayMedian(Integer lowerDayMedian) {
		this.lowerDayMedian = lowerDayMedian;
	}

	public Integer getSmaType() {
		return smaType;
	}

	public void setSmaType(Integer smaType) {
		this.isEmpty = false;
		this.smaType = smaType;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getShortStrength() {
		return shortStrength;
	}

	public void setShortStrength(String shortStrength) {
		this.shortStrength = shortStrength;
	}

	public String getMediumStrength() {
		return mediumStrength;
	}

	public void setMediumStrength(String mediumStrength) {
		this.mediumStrength = mediumStrength;
	}

	public String getLongStrength() {
		return longStrength;
	}

	public void setLongStrength(String longStrength) {
		this.longStrength = longStrength;
	}

	public BigDecimal getTargetReturn() {
		return targetReturn;
	}

	public void setTargetReturn(BigDecimal targetReturn) {
		this.targetReturn = targetReturn;
	}

	public BigDecimal getConfident() {
		return confident;
	}

	public void setConfident(BigDecimal confident) {
		this.confident = confident;
	}

	public Integer getSignalSeq() {
		return signalSeq;
	}

	public void setSignalSeq(Integer signalSeq) {
		this.signalSeq = signalSeq;
		for(StockSignalDateEntity signalDate : this.getStockSignalDateList()) {
			signalDate.setSignalSeq(signalSeq);
		}
	}


	public List<StockSignalDateEntity> getStockSignalDateList() {
		return stockSignalDateList;
	}

	public void setStockSignalDateList(List<StockSignalDateEntity> stockSignalDateList) {
		this.stockSignalDateList = stockSignalDateList;
	}

	public Integer getCompleted() {
		return completed;
	}

	public void setCompleted(Integer completed) {
		this.completed = completed;
	}

	public boolean isSame(StockSignalEntity that) {
		if( this.count != that.count ) {
			return false;
		}
		if( this.upperMin.compareTo(that.upperMin) != 0) {
			return false;
		}
		if( this.upperMax.compareTo(that.upperMax) != 0) {
			return false;
		}
		if( this.lowerMin.compareTo(that.lowerMin) != 0) {
			return false;
		}
		if( this.lowerMax.compareTo(that.lowerMax) != 0) {
			return false;
		}
		return true;
	}
}
