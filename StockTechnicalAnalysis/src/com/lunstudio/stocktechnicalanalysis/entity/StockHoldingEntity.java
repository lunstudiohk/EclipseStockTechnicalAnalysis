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
@Table(name = "tb_stockholding")
public class StockHoldingEntity extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	private String holderCode;
	
	@Id
	private Date holdingDate;

	@Id
	private String stockCode;

	private BigDecimal stockHolding;
	
	private BigDecimal holdingPercentage;

	@Transient
	private String holderName;
	
	public String getHolderName() {
		return holderName;
	}


	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}


	public String getHolderCode() {
		return holderCode;
	}


	public void setHolderCode(String holderCode) {
		this.holderCode = holderCode;
	}


	public Date getHoldingDate() {
		return holdingDate;
	}


	public void setHoldingDate(Date holdingDate) {
		this.holdingDate = holdingDate;
	}


	public String getStockCode() {
		return stockCode;
	}


	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}


	public BigDecimal getStockHolding() {
		return stockHolding;
	}


	public void setStockHolding(BigDecimal stockHolding) {
		this.stockHolding = stockHolding;
	}


	public BigDecimal getHoldingPercentage() {
		return holdingPercentage;
	}


	public void setHoldingPercentage(BigDecimal holdingPercentage) {
		this.holdingPercentage = holdingPercentage;
	}


	@Override
    public boolean equals(Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !(obj instanceof StockHoldingEntity) ) {
			return false;
		}
		
		StockHoldingEntity that = (StockHoldingEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.holderCode, that.holderCode);
		eb.append(this.holdingDate, that.holdingDate);
		eb.append(this.stockCode, that.stockCode);
		return eb.isEquals();
	}
}
