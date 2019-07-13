package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.sql.Date;

import com.lunstudio.stocktechnicalanalysis.entity.BaseEntity;

public class WarrantAmountVo extends BaseEntity {
	
	private Date tradeDate;

	private String stockCode;

	private BigDecimal warrantCallAmount;
	
	private BigDecimal warrantPutAmount;
	
	private BigDecimal warrantCallTurnover;
	
	private BigDecimal warrantPutTurnover;

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

	public BigDecimal getWarrantCallAmount() {
		return warrantCallAmount;
	}

	public void setWarrantCallAmount(BigDecimal warrantCallAmount) {
		this.warrantCallAmount = warrantCallAmount;
	}

	public BigDecimal getWarrantPutAmount() {
		return warrantPutAmount;
	}

	public void setWarrantPutAmount(BigDecimal warrantPutAmount) {
		this.warrantPutAmount = warrantPutAmount;
	}

	public BigDecimal getWarrantCallTurnover() {
		return warrantCallTurnover;
	}

	public void setWarrantCallTurnover(BigDecimal warrantCallTurnover) {
		this.warrantCallTurnover = warrantCallTurnover;
	}

	public BigDecimal getWarrantPutTurnover() {
		return warrantPutTurnover;
	}

	public void setWarrantPutTurnover(BigDecimal warrantPutTurnover) {
		this.warrantPutTurnover = warrantPutTurnover;
	}
	
	
	
}
