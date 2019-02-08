package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
@Table(name="tb_indexfutures")
public class IndexFuturesEntity extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String indexCode;
	@Id
	private Date tradeDate;
	@Id
	private Integer month;
	
	private Integer nightOpen;
	private Integer nightHigh;
	private Integer nightLow;
	private Integer nightClose;
	private Integer nightVolume;
	private Integer dayOpen;
	private Integer dayHigh;
	private Integer dayLow;
	private Integer dayVolume;
	private Integer dayClose;
	private Integer dayChange;
	private Integer contractHigh;
	private Integer contractLow;
	private Integer volume;
	private Integer openInterest;
	private Integer openInterestChange;
	
	
		
	public String getIndexCode() {
		return indexCode;
	}



	public void setIndexCode(String indexCode) {
		this.indexCode = indexCode;
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



	public Integer getNightOpen() {
		return nightOpen;
	}



	public void setNightOpen(Integer nightOpen) {
		this.nightOpen = nightOpen;
	}



	public Integer getNightHigh() {
		return nightHigh;
	}



	public void setNightHigh(Integer nightHigh) {
		this.nightHigh = nightHigh;
	}



	public Integer getNightLow() {
		return nightLow;
	}



	public void setNightLow(Integer nightLow) {
		this.nightLow = nightLow;
	}



	public Integer getNightClose() {
		return nightClose;
	}



	public void setNightClose(Integer nightClose) {
		this.nightClose = nightClose;
	}



	public Integer getNightVolume() {
		return nightVolume;
	}



	public void setNightVolume(Integer nightVolume) {
		this.nightVolume = nightVolume;
	}



	public Integer getDayOpen() {
		return dayOpen;
	}



	public void setDayOpen(Integer dayOpen) {
		this.dayOpen = dayOpen;
	}



	public Integer getDayHigh() {
		return dayHigh;
	}



	public void setDayHigh(Integer dayHigh) {
		this.dayHigh = dayHigh;
	}



	public Integer getDayLow() {
		return dayLow;
	}



	public void setDayLow(Integer dayLow) {
		this.dayLow = dayLow;
	}



	public Integer getDayVolume() {
		return dayVolume;
	}



	public void setDayVolume(Integer dayVolume) {
		this.dayVolume = dayVolume;
	}



	public Integer getDayClose() {
		return dayClose;
	}



	public void setDayClose(Integer dayClose) {
		this.dayClose = dayClose;
	}



	public Integer getDayChange() {
		return dayChange;
	}



	public void setDayChange(Integer dayChange) {
		this.dayChange = dayChange;
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



	@Override
    public boolean equals(Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !(obj instanceof IndexFuturesEntity) ) {
			return false;
		}
		
		IndexFuturesEntity that = (IndexFuturesEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.indexCode, that.indexCode);
		eb.append(this.tradeDate, that.tradeDate);
		eb.append(this.month, that.month);
		return eb.isEquals();
	}
}
