package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.sql.Date;

import com.lunstudio.stocktechnicalanalysis.entity.BaseEntity;

public class WarrantPriceVo extends BaseEntity {
	
	private Date tradeDate;

	private String stockCode;

	private BigDecimal warrantCallAmount = BigDecimal.ZERO;
	
	private BigDecimal warrantPutAmount = BigDecimal.ZERO;
	
	private BigDecimal warrantCallCost = BigDecimal.ZERO;
	
	private BigDecimal warrantPutCost = BigDecimal.ZERO;
	
	//Close Price < Strike Price 0-5%
	private BigDecimal warrant5CallAmount;
	
	//Close Price < Strike Price 5-10%
	private BigDecimal warrant10CallAmount;

	private BigDecimal warrant5PutAmount;
	
	private BigDecimal warrant10PutAmount;
	
	private BigDecimal warrantAllCallTurnover = BigDecimal.ZERO;
	private BigDecimal warrantAllPutTurnover = BigDecimal.ZERO;
	private BigDecimal warrantOtmCallTurnover = BigDecimal.ZERO;
	private BigDecimal warrantOtmPutTurnover = BigDecimal.ZERO;
	private BigDecimal warrantAllCallAmount = BigDecimal.ZERO;
	private BigDecimal warrantAllPutAmount = BigDecimal.ZERO;
	private BigDecimal warrantOtmCallAmount = BigDecimal.ZERO;
	private BigDecimal warrantOtmPutAmount = BigDecimal.ZERO;
	
	public WarrantPriceVo(String stockCode, Date tradeDate) {
		super();
		this.stockCode = stockCode;
		this.tradeDate = tradeDate;
		return;
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

	public BigDecimal getWarrant5CallAmount() {
		return warrant5CallAmount;
	}

	public void setWarrant5CallAmount(BigDecimal warrant5CallAmount) {
		this.warrant5CallAmount = warrant5CallAmount;
	}

	public BigDecimal getWarrant10CallAmount() {
		return warrant10CallAmount;
	}

	public void setWarrant10CallAmount(BigDecimal warrant10CallAmount) {
		this.warrant10CallAmount = warrant10CallAmount;
	}

	public BigDecimal getWarrant5PutAmount() {
		return warrant5PutAmount;
	}

	public void setWarrant5PutAmount(BigDecimal warrant5PutAmount) {
		this.warrant5PutAmount = warrant5PutAmount;
	}

	public BigDecimal getWarrant10PutAmount() {
		return warrant10PutAmount;
	}

	public void setWarrant10PutAmount(BigDecimal warrant10PutAmount) {
		this.warrant10PutAmount = warrant10PutAmount;
	}

	public BigDecimal getWarrantCallCost() {
		return warrantCallCost;
	}

	public void setWarrantCallCost(BigDecimal warrantCallCost) {
		this.warrantCallCost = warrantCallCost;
	}

	public BigDecimal getWarrantPutCost() {
		return warrantPutCost;
	}

	public void setWarrantPutCost(BigDecimal warrantPutCost) {
		this.warrantPutCost = warrantPutCost;
	}

	public BigDecimal getWarrantAllCallTurnover() {
		return warrantAllCallTurnover;
	}

	public void setWarrantAllCallTurnover(BigDecimal warrantAllCallTurnover) {
		this.warrantAllCallTurnover = warrantAllCallTurnover;
	}

	public BigDecimal getWarrantAllPutTurnover() {
		return warrantAllPutTurnover;
	}

	public void setWarrantAllPutTurnover(BigDecimal warrantAllPutTurnover) {
		this.warrantAllPutTurnover = warrantAllPutTurnover;
	}

	public BigDecimal getWarrantOtmCallTurnover() {
		return warrantOtmCallTurnover;
	}

	public void setWarrantOtmCallTurnover(BigDecimal warrantOtmCallTurnover) {
		this.warrantOtmCallTurnover = warrantOtmCallTurnover;
	}

	public BigDecimal getWarrantOtmPutTurnover() {
		return warrantOtmPutTurnover;
	}

	public void setWarrantOtmPutTurnover(BigDecimal warrantOtmPutTurnover) {
		this.warrantOtmPutTurnover = warrantOtmPutTurnover;
	}

	public BigDecimal getWarrantAllCallAmount() {
		return warrantAllCallAmount;
	}

	public void setWarrantAllCallAmount(BigDecimal warrantAllCallAmount) {
		this.warrantAllCallAmount = warrantAllCallAmount;
	}

	public BigDecimal getWarrantAllPutAmount() {
		return warrantAllPutAmount;
	}

	public void setWarrantAllPutAmount(BigDecimal warrantAllPutAmount) {
		this.warrantAllPutAmount = warrantAllPutAmount;
	}

	public BigDecimal getWarrantOtmCallAmount() {
		return warrantOtmCallAmount;
	}

	public void setWarrantOtmCallAmount(BigDecimal warrantOtmCallAmount) {
		this.warrantOtmCallAmount = warrantOtmCallAmount;
	}

	public BigDecimal getWarrantOtmPutAmount() {
		return warrantOtmPutAmount;
	}

	public void setWarrantOtmPutAmount(BigDecimal warrantOtmPutAmount) {
		this.warrantOtmPutAmount = warrantOtmPutAmount;
	}

	
}
