package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
@Table(name = "tb_candlestick")
public class CandlestickEntity extends BaseEntity implements Serializable {

	public static final String Buy = "B";
	public static final String Sell = "S";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Date tradeDate;

	@Id
	private String stockCode;

	@Id
	private Integer candlestickType;
	
	@Id
	private String type;	//B:Buy, S:Sell
	
	private BigDecimal confirmPrice;
	
	private BigDecimal stoplossPrice;

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

	public Integer getCandlestickType() {
		return candlestickType;
	}

	public void setCandlestickType(Integer candlestickType) {
		this.candlestickType = candlestickType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getConfirmPrice() {
		return confirmPrice;
	}

	public void setConfirmPrice(BigDecimal confirmPrice) {
		this.confirmPrice = confirmPrice;
	}

	public BigDecimal getStoplossPrice() {
		return stoplossPrice;
	}

	public void setStoplossPrice(BigDecimal stoplossPrice) {
		this.stoplossPrice = stoplossPrice;
	}
	
	@Override
    public boolean equals(Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !(obj instanceof CandlestickEntity) ) {
			return false;
		}
		
		CandlestickEntity that = (CandlestickEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.tradeDate, that.tradeDate);
		eb.append(this.stockCode, that.stockCode);
		eb.append(this.candlestickType, that.candlestickType);
		return eb.isEquals();
	}

	
	
}
