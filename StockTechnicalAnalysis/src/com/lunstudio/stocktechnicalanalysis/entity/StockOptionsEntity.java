package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
@Table(name="tb_stockoptions")
public class StockOptionsEntity extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String stockCode;
	@Id
	private Date tradeDate;
	@Id
	private Integer month;
	@Id
	private String optionType;
	@Id
	private BigDecimal strikePrice;
	
	private BigDecimal nightOpen;
	private BigDecimal nightHigh;
	private BigDecimal nightLow;
	private BigDecimal nightClose;
	private Integer nightVolume;
	private BigDecimal dayOpen;
	private BigDecimal dayHigh;
	private BigDecimal dayLow;
	private BigDecimal dayClose;
	private BigDecimal dayChange;
	private BigDecimal iv;
	private Integer dayVolume;
	private Integer contractHigh;
	private Integer contractLow;
	private Integer volume;
	private Integer openInterest;
	private Integer openInterestChange;
    
	public String getStockCode() {
		return stockCode;
	}


	public void setStockCode(String indexCode) {
		this.stockCode = indexCode;
	}


	public Date getTradeDate() {
		return tradeDate;
	}


	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}


	public Integer getMonth() {
		return month;
	}


	public void setMonth(Integer month) {
		this.month = month;
	}


	public String getOptionType() {
		return optionType;
	}


	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}

	public Integer getNightVolume() {
		return nightVolume;
	}


	public void setNightVolume(Integer nightVolume) {
		this.nightVolume = nightVolume;
	}

	public Integer getDayVolume() {
		return dayVolume;
	}

	public void setDayVolume(Integer dayVolume) {
		this.dayVolume = dayVolume;
	}


	public Integer getContractHigh() {
		return contractHigh;
	}


	public void setContractHigh(Integer contractHigh) {
		this.contractHigh = contractHigh;
	}


	public Integer getContractLow() {
		return contractLow;
	}


	public void setContractLow(Integer contractLow) {
		this.contractLow = contractLow;
	}


	public Integer getVolume() {
		return volume;
	}


	public void setVolume(Integer volume) {
		this.volume = volume;
	}


	public Integer getOpenInterest() {
		return openInterest;
	}


	public void setOpenInterest(Integer openInterest) {
		this.openInterest = openInterest;
	}


	public Integer getOpenInterestChange() {
		return openInterestChange;
	}


	public void setOpenInterestChange(Integer openInterestChange) {
		this.openInterestChange = openInterestChange;
	}


	public BigDecimal getStrikePrice() {
		return strikePrice;
	}


	public void setStrikePrice(BigDecimal strikePrice) {
		this.strikePrice = strikePrice;
	}


	public BigDecimal getNightOpen() {
		return nightOpen;
	}


	public void setNightOpen(BigDecimal nightOpen) {
		this.nightOpen = nightOpen;
	}


	public BigDecimal getNightHigh() {
		return nightHigh;
	}


	public void setNightHigh(BigDecimal nightHigh) {
		this.nightHigh = nightHigh;
	}


	public BigDecimal getNightLow() {
		return nightLow;
	}


	public void setNightLow(BigDecimal nightLow) {
		this.nightLow = nightLow;
	}


	public BigDecimal getNightClose() {
		return nightClose;
	}


	public void setNightClose(BigDecimal nightClose) {
		this.nightClose = nightClose;
	}


	public BigDecimal getDayOpen() {
		return dayOpen;
	}


	public void setDayOpen(BigDecimal dayOpen) {
		this.dayOpen = dayOpen;
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


	public BigDecimal getDayClose() {
		return dayClose;
	}


	public void setDayClose(BigDecimal dayClose) {
		this.dayClose = dayClose;
	}


	public BigDecimal getDayChange() {
		return dayChange;
	}


	public void setDayChange(BigDecimal dayChange) {
		this.dayChange = dayChange;
	}


	public BigDecimal getIv() {
		return iv;
	}


	public void setIv(BigDecimal iv) {
		this.iv = iv;
	}


	@Override
    public boolean equals(Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !(obj instanceof StockOptionsEntity) ) {
			return false;
		}
		
		StockOptionsEntity that = (StockOptionsEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.stockCode, that.stockCode);
		eb.append(this.tradeDate, that.tradeDate);
		eb.append(this.month, that.month);
		eb.append(this.optionType, that.optionType);
		eb.append(this.strikePrice, that.strikePrice);
		return eb.isEquals();
	}
	
}