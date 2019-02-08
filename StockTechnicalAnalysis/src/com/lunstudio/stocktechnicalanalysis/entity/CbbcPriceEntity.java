package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
@Table(name="tb_cbbcprice")
public class CbbcPriceEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private String cbbcCode;
	
	@Id
	private Date tradeDate;
	
	private String cbbcIssuer;

	private String cbbcUnderlying;
	
	private String cbbcType;
	
	private Date cbbcListDate;
	
	private Date cbbcMaturityDate;
	
	private BigDecimal cbbcStrikeLevel;
	
	private BigDecimal cbbcCallLevel;
	
	private BigDecimal cbbcRatio;
	
	private Long issueSize;
	
	private BigDecimal qustanding;
	
	private BigDecimal dayHigh;
	
	private BigDecimal dayLow;
	
	private BigDecimal closePrice;
	
	private BigDecimal turnover;
	
	@Transient
	private BigDecimal cbbcAmount;
	
	public BigDecimal getCbbcAmount() {
		if( this.cbbcAmount == null ) {
			if( this.qustanding == null || this.qustanding == null || this.closePrice == null ) {
				this.cbbcAmount = BigDecimal.ZERO;
			} else {
				this.cbbcAmount = BigDecimal.valueOf(this.issueSize).multiply(this.qustanding).multiply(this.closePrice);
			}
		}
		return cbbcAmount;
	}

	public void setCbbcAmount(BigDecimal cbbcAmount) {
		this.cbbcAmount = cbbcAmount;
	}

	public String getCbbcCode() {
		return cbbcCode;
	}

	public void setCbbcCode(String cbbcCode) {
		this.cbbcCode = cbbcCode;
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

	public String getCbbcIssuer() {
		return cbbcIssuer;
	}

	public void setCbbcIssuer(String cbbcIssuer) {
		this.cbbcIssuer = cbbcIssuer;
	}

	public String getCbbcUnderlying() {
		return cbbcUnderlying;
	}

	public void setCbbcUnderlying(String cbbcUnderlying) {
		this.cbbcUnderlying = cbbcUnderlying;
	}

	public String getCbbcType() {
		return cbbcType;
	}

	public void setCbbcType(String cbbcType) {
		this.cbbcType = cbbcType;
	}

	public Date getCbbcListDate() {
		return cbbcListDate;
	}

	public void setCbbcListDate(Date cbbcListDate) {
		this.cbbcListDate = cbbcListDate;
	}

	public Date getCbbcMaturityDate() {
		return cbbcMaturityDate;
	}

	public void setCbbcMaturityDate(Date cbbcMaturityDate) {
		this.cbbcMaturityDate = cbbcMaturityDate;
	}

	public BigDecimal getCbbcStrikeLevel() {
		return cbbcStrikeLevel;
	}

	public void setCbbcStrikeLevel(BigDecimal cbbcStrikeLevel) {
		this.cbbcStrikeLevel = cbbcStrikeLevel;
	}

	public BigDecimal getCbbcCallLevel() {
		return cbbcCallLevel;
	}

	public void setCbbcCallLevel(BigDecimal cbbcCallLevel) {
		this.cbbcCallLevel = cbbcCallLevel;
	}

	public BigDecimal getCbbcRatio() {
		return cbbcRatio;
	}

	public void setCbbcRatio(BigDecimal cbbcRatio) {
		this.cbbcRatio = cbbcRatio;
	}

	@Override
    public boolean equals(Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !(obj instanceof WarrantPriceEntity) ) {
			return false;
		}
		
		CbbcPriceEntity that = (CbbcPriceEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.cbbcCode, that.cbbcCode);
		eb.append(this.tradeDate, that.tradeDate);
		return eb.isEquals();
	}
	
}
