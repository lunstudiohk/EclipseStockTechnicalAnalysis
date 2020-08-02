package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name = "tb_stocktradesignaldetail")
public class StockTradeSignalDetailEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private Date tradeDate;

	@Id
	private String stockCode;

	@Id
	private Integer tradeSignalSeq;

	@Id
	private Integer signalSeq;
	
	private BigDecimal param1;
	
	private BigDecimal param2;

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

	public Integer getSignalSeq() {
		return signalSeq;
	}

	public void setSignalSeq(Integer signalSeq) {
		this.signalSeq = signalSeq;
	}

	public BigDecimal getParam1() {
		return param1;
	}

	public void setParam1(BigDecimal param1) {
		this.param1 = param1;
	}

	public BigDecimal getParam2() {
		return param2;
	}

	public void setParam2(BigDecimal param2) {
		this.param2 = param2;
	}
	
	
}
