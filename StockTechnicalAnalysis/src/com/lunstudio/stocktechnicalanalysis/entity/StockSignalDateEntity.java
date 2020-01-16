package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="tb_stocksignaldate")
public class StockSignalDateEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Integer ASC = 0;
	public static final Integer DESC = 1;
	
	@Id
	private String stockCode;
	
	@Id
	private Date tradeDate;

	@Id
	private Integer signalSeq;

	@Id
	private Date signalDate;
	
	@Id
	private String signalType;
	
	private BigDecimal signalPrice;
	
	private BigDecimal highReturn;
	
	private Integer highDay;
	
	private BigDecimal lowReturn;
	
	private Integer lowDay;

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public Integer getSignalSeq() {
		return signalSeq;
	}

	public void setSignalSeq(Integer signalSeq) {
		this.signalSeq = signalSeq;
	}

	public Date getSignalDate() {
		return signalDate;
	}

	public void setSignalDate(Date signalDate) {
		this.signalDate = signalDate;
	}

	public BigDecimal getSignalPrice() {
		return signalPrice;
	}

	public void setSignalPrice(BigDecimal signalPrice) {
		this.signalPrice = signalPrice;
	}

	public BigDecimal getHighReturn() {
		return highReturn;
	}

	public void setHighReturn(BigDecimal highReturn) {
		this.highReturn = highReturn;
	}

	public Integer getHighDay() {
		return highDay;
	}

	public void setHighDay(Integer highDay) {
		this.highDay = highDay;
	}

	public BigDecimal getLowReturn() {
		return lowReturn;
	}

	public void setLowReturn(BigDecimal lowReturn) {
		this.lowReturn = lowReturn;
	}

	public Integer getLowDay() {
		return lowDay;
	}

	public void setLowDay(Integer lowDay) {
		this.lowDay = lowDay;
	}

	public String getSignalType() {
		return signalType;
	}

	public void setSignalType(String signalType) {
		this.signalType = signalType;
	}
	
	
}
