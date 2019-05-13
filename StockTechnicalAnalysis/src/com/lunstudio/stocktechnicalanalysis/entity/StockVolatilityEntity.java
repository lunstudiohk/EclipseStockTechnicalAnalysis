package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;


@Entity
@Table(name="tb_stockvolatility")
public class StockVolatilityEntity extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String stockCode;
	
	@Id
	private Date tradeDate;
	
	private BigDecimal implVol;

	
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


	public BigDecimal getImplVol() {
		return implVol;
	}


	public void setImplVol(BigDecimal implVol) {
		this.implVol = implVol;
	}


	@Override
    public boolean equals(Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !(obj instanceof StockVolatilityEntity) ) {
			return false;
		}
		
		StockVolatilityEntity that = (StockVolatilityEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.stockCode, that.stockCode);
		eb.append(this.tradeDate, that.tradeDate);
		return eb.isEquals();
	}
}
