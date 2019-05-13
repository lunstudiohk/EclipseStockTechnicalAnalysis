package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.sql.Date;

import com.lunstudio.stocktechnicalanalysis.entity.BaseEntity;

public class CbbcPriceVo extends BaseEntity {
	
	private Date tradeDate;

	private String stockCode;

	private BigDecimal cbbcBullAmount= BigDecimal.ZERO;
	private BigDecimal cbbcBullCost= BigDecimal.ZERO;

	private BigDecimal cbbcBearAmount= BigDecimal.ZERO;
	private BigDecimal cbbcBearCost= BigDecimal.ZERO;
	
	private BigDecimal cbbcAllBullAmount= BigDecimal.ZERO;
	private BigDecimal cbbcAllBearAmount= BigDecimal.ZERO;
	private BigDecimal cbbcAllBullTurnover= BigDecimal.ZERO;
	private BigDecimal cbbcAllBearTurnover= BigDecimal.ZERO;
	private BigDecimal cbbcNearBullAmount= BigDecimal.ZERO;
	private BigDecimal cbbcNearBearAmount= BigDecimal.ZERO;
	private BigDecimal cbbcNearBullTurnover= BigDecimal.ZERO;
	private BigDecimal cbbcNearBearTurnover= BigDecimal.ZERO;
	
	public CbbcPriceVo(String stockCode, Date tradeDate) {
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

	public BigDecimal getCbbcBullCost() {
		return cbbcBullCost;
	}

	public void setCbbcBullCost(BigDecimal cbbcBullCost) {
		this.cbbcBullCost = cbbcBullCost;
	}

	public BigDecimal getCbbcBearCost() {
		return cbbcBearCost;
	}

	public void setCbbcBearCost(BigDecimal cbbcBearCost) {
		this.cbbcBearCost = cbbcBearCost;
	}

	public BigDecimal getCbbcAllBullAmount() {
		return cbbcAllBullAmount;
	}

	public void setCbbcAllBullAmount(BigDecimal cbbcAllBullAmount) {
		this.cbbcAllBullAmount = cbbcAllBullAmount;
	}

	public BigDecimal getCbbcAllBearAmount() {
		return cbbcAllBearAmount;
	}

	public void setCbbcAllBearAmount(BigDecimal cbbcAllBearAmount) {
		this.cbbcAllBearAmount = cbbcAllBearAmount;
	}

	public BigDecimal getCbbcAllBullTurnover() {
		return cbbcAllBullTurnover;
	}

	public void setCbbcAllBullTurnover(BigDecimal cbbcAllBullTurnover) {
		this.cbbcAllBullTurnover = cbbcAllBullTurnover;
	}

	public BigDecimal getCbbcAllBearTurnover() {
		return cbbcAllBearTurnover;
	}

	public void setCbbcAllBearTurnover(BigDecimal cbbcAllBearTurnover) {
		this.cbbcAllBearTurnover = cbbcAllBearTurnover;
	}

	public BigDecimal getCbbcNearBullAmount() {
		return cbbcNearBullAmount;
	}

	public void setCbbcNearBullAmount(BigDecimal cbbcNearBullAmount) {
		this.cbbcNearBullAmount = cbbcNearBullAmount;
	}

	public BigDecimal getCbbcNearBearAmount() {
		return cbbcNearBearAmount;
	}

	public void setCbbcNearBearAmount(BigDecimal cbbcNearBearAmount) {
		this.cbbcNearBearAmount = cbbcNearBearAmount;
	}

	public BigDecimal getCbbcNearBullTurnover() {
		return cbbcNearBullTurnover;
	}

	public void setCbbcNearBullTurnover(BigDecimal cbbcNearBullTurnover) {
		this.cbbcNearBullTurnover = cbbcNearBullTurnover;
	}

	public BigDecimal getCbbcNearBearTurnover() {
		return cbbcNearBearTurnover;
	}

	public void setCbbcNearBearTurnover(BigDecimal cbbcNearBearTurnover) {
		this.cbbcNearBearTurnover = cbbcNearBearTurnover;
	}

	
}
