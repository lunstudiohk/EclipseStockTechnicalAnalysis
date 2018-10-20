package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
@Table(name = "tb_warrantprice")
public class WarrantPriceEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String warrantCode;

	@Id
	private Date tradeDate;

	private Long issueSize;

	private BigDecimal qustanding;

	private BigDecimal delta;

	private BigDecimal impVol;

	private BigDecimal dayHigh;

	private BigDecimal dayLow;

	private BigDecimal closePrice;

	private BigDecimal turnover;

	private String warrantIssuer;
	
	private String warrantUnderlying;
	
	private String warrantType;
	
	private Date warrantListDate;
	
	private Date warrantMaturityDate;
	
	private BigDecimal warrantStrikePrice;
	
	private BigDecimal warrantRatio;

	public String getWarrantCode() {
		return warrantCode;
	}

	public void setWarrantCode(String warrantCode) {
		this.warrantCode = warrantCode;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public Long getIssueSize() {
		return issueSize;
	}

	public void setIssueSize(Long issueSize) {
		this.issueSize = issueSize;
	}

	public BigDecimal getQustanding() {
		return qustanding;
	}

	public void setQustanding(BigDecimal qustanding) {
		this.qustanding = qustanding;
	}

	public BigDecimal getDelta() {
		return delta;
	}

	public void setDelta(BigDecimal delta) {
		this.delta = delta;
	}

	public BigDecimal getImpVol() {
		return impVol;
	}

	public void setImpVol(BigDecimal impVol) {
		this.impVol = impVol;
	}

	public BigDecimal getDayHigh() {
		return dayHigh;
	}

	public void setDayHigh(BigDecimal dayHigh) {
		this.dayHigh = dayHigh;
	}

	public BigDecimal getDayLow() {
		return dayLow;
	}

	public void setDayLow(BigDecimal dayLow) {
		this.dayLow = dayLow;
	}

	public BigDecimal getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}

	public BigDecimal getTurnover() {
		return turnover;
	}

	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
	}

	public String getWarrantIssuer() {
		return warrantIssuer;
	}

	public void setWarrantIssuer(String warrantIssuer) {
		this.warrantIssuer = warrantIssuer;
	}

	public String getWarrantUnderlying() {
		return warrantUnderlying;
	}

	public void setWarrantUnderlying(String warrantUnderlying) {
		this.warrantUnderlying = warrantUnderlying;
	}

	public String getWarrantType() {
		return warrantType;
	}

	public void setWarrantType(String warrantType) {
		this.warrantType = warrantType;
	}

	public Date getWarrantListDate() {
		return warrantListDate;
	}

	public void setWarrantListDate(Date warrantListDate) {
		this.warrantListDate = warrantListDate;
	}

	public Date getWarrantMaturityDate() {
		return warrantMaturityDate;
	}

	public void setWarrantMaturityDate(Date warrantMaturityDate) {
		this.warrantMaturityDate = warrantMaturityDate;
	}

	public BigDecimal getWarrantStrikePrice() {
		return warrantStrikePrice;
	}

	public void setWarrantStrikePrice(BigDecimal warrantStrikePrice) {
		this.warrantStrikePrice = warrantStrikePrice;
	}

	public BigDecimal getWarrantRatio() {
		return warrantRatio;
	}

	public void setWarrantRatio(BigDecimal warrantRatio) {
		this.warrantRatio = warrantRatio;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof WarrantPriceEntity)) {
			return false;
		}

		WarrantPriceEntity that = (WarrantPriceEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.warrantCode, that.warrantCode);
		eb.append(this.tradeDate, that.tradeDate);
		return eb.isEquals();
	}

}
