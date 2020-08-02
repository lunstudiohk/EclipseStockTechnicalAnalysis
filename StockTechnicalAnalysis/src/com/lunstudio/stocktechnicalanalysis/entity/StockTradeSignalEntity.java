package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name = "tb_stocktradesignal")
public class StockTradeSignalEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Integer TRADE_SIGNAL_SHORT_BULLISH = 1;
	public static final Integer TRADE_SIGNAL_SHORT_BEARISH = 2;
	
	public static final Integer TRADE_SIGNAL_MEDIUM_BULLISH = 3;
	public static final Integer TRADE_SIGNAL_MEDIUM_BEARISH = 4;
	
	public static final Integer TRADE_SIGNAL_LONG_BULLISH = 5;
	public static final Integer TRADE_SIGNAL_LONG_BEARISH = 6;
	
	public static final String SIGNAL_TYPE_BUY = "B";
	public static final String SIGNAL_TYPE_SELL = "S";
	public static final String SIGNAL_TYPE_BOTH = "W";
	
	@Id
	private Date tradeDate;

	@Id
	private String stockCode;

	@Id
	private Integer tradeSignalSeq;
	
	private String tradeSignalType;
	
	private Integer occurrence;
	
	private BigDecimal minReturn;
	
	private BigDecimal maxReturn;
	
	private BigDecimal medianReturn;
	
	private BigDecimal meanReturn;
	
	private BigDecimal targetReturn;

	private BigDecimal targetPercentage;
	
	public BigDecimal getTargetPercentage() {
		return targetPercentage;
	}

	public void setTargetPercentage(BigDecimal targetPercentage) {
		this.targetPercentage = targetPercentage;
	}

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

	public Integer getTradeSignalSeq() {
		return tradeSignalSeq;
	}

	public void setTradeSignalSeq(Integer tradeSignalSeq) {
		this.tradeSignalSeq = tradeSignalSeq;
	}

	public String getTradeSignalType() {
		return tradeSignalType;
	}

	public void setTradeSignalType(String tradeSignalType) {
		this.tradeSignalType = tradeSignalType;
	}

	public Integer getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(Integer occurrence) {
		this.occurrence = occurrence;
	}

	public BigDecimal getMinReturn() {
		return minReturn;
	}

	public void setMinReturn(BigDecimal minReturn) {
		this.minReturn = minReturn;
	}

	public BigDecimal getMaxReturn() {
		return maxReturn;
	}

	public void setMaxReturn(BigDecimal maxReturn) {
		this.maxReturn = maxReturn;
	}

	public BigDecimal getMedianReturn() {
		return medianReturn;
	}

	public void setMedianReturn(BigDecimal medianReturn) {
		this.medianReturn = medianReturn;
	}

	public BigDecimal getMeanReturn() {
		return meanReturn;
	}

	public void setMeanReturn(BigDecimal meanReturn) {
		this.meanReturn = meanReturn;
	}

	public BigDecimal getTargetReturn() {
		return targetReturn;
	}

	public void setTargetReturn(BigDecimal targetReturn) {
		this.targetReturn = targetReturn;
	}


}
