package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name = "tb_stocktradesignaldate")
public class StockTradeSignalDateEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private Date tradeDate;

	@Id
	private String stockCode;

	@Id
	private Integer tradeSignalSeq;

	@Id
	private Date occurrenceDate;
	
	private BigDecimal minReturn;
	
	private BigDecimal maxReturn;

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

	public Date getOccurrenceDate() {
		return occurrenceDate;
	}

	public void setOccurrenceDate(Date occurrenceDate) {
		this.occurrenceDate = occurrenceDate;
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
	
	
}
