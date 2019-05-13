package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
@Table(name="tb_stockoptionsstat")
public class StockOptionsStatEntity extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String stockCode;
	
	@Id
	private Date tradeDate;
	
	private Integer calls;
	
	private Integer puts;
	
	private Integer openInterestCalls;
	
	private Integer openInterestPuts;

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


	public Integer getCalls() {
		return calls;
	}


	public void setCalls(Integer calls) {
		this.calls = calls;
	}


	public Integer getPuts() {
		return puts;
	}


	public void setPuts(Integer puts) {
		this.puts = puts;
	}


	public Integer getOpenInterestCalls() {
		return openInterestCalls;
	}


	public void setOpenInterestCalls(Integer openInterestCalls) {
		this.openInterestCalls = openInterestCalls;
	}


	public Integer getOpenInterestPuts() {
		return openInterestPuts;
	}

	public void setOpenInterestPuts(Integer openInterestPuts) {
		this.openInterestPuts = openInterestPuts;
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
		if( !(obj instanceof StockOptionsStatEntity) ) {
			return false;
		}
		
		StockOptionsStatEntity that = (StockOptionsStatEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.stockCode, that.stockCode);
		eb.append(this.tradeDate, that.tradeDate);
		return eb.isEquals();
	}
}
