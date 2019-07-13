package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.sql.Date;

import com.lunstudio.stocktechnicalanalysis.entity.BaseEntity;

public class CbbcAmountVo extends BaseEntity {
	
	private Date tradeDate;

	private String stockCode;

	private BigDecimal cbbcBullAmount;
	private BigDecimal cbbcBearAmount;
	private BigDecimal cbbcBullTurnover;
	private BigDecimal cbbcBearTurnover;
	
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
	public BigDecimal getCbbcBullAmount() {
		return cbbcBullAmount;
	}
	public void setCbbcBullAmount(BigDecimal cbbcBullAmount) {
		this.cbbcBullAmount = cbbcBullAmount;
	}
	public BigDecimal getCbbcBearAmount() {
		return cbbcBearAmount;
	}
	public void setCbbcBearAmount(BigDecimal cbbcBearAmount) {
		this.cbbcBearAmount = cbbcBearAmount;
	}
	public BigDecimal getCbbcBullTurnover() {
		return cbbcBullTurnover;
	}
	public void setCbbcBullTurnover(BigDecimal cbbcBullTurnover) {
		this.cbbcBullTurnover = cbbcBullTurnover;
	}
	public BigDecimal getCbbcBearTurnover() {
		return cbbcBearTurnover;
	}
	public void setCbbcBearTurnover(BigDecimal cbbcBearTurnover) {
		this.cbbcBearTurnover = cbbcBearTurnover;
	}

	
}
