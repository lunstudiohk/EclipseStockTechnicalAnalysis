package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.sql.Date;

import com.lunstudio.stocktechnicalanalysis.entity.BaseEntity;

public class OptionAmountVo extends BaseEntity {

	private Date tradeDate;

	private String stockCode;

	private Long optionCallOpenInterest;
	private Long optionPutOpenInterest;
	private Long optionCallVolume;
	private Long optionPutVolume;
	
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
	public Long getOptionCallOpenInterest() {
		return optionCallOpenInterest;
	}
	public void setOptionCallOpenInterest(Long optionCallOpenInterest) {
		this.optionCallOpenInterest = optionCallOpenInterest;
	}
	public Long getOptionPutOpenInterest() {
		return optionPutOpenInterest;
	}
	public void setOptionPutOpenInterest(Long optionPutOpenInterest) {
		this.optionPutOpenInterest = optionPutOpenInterest;
	}
	public Long getOptionCallVolume() {
		return optionCallVolume;
	}
	public void setOptionCallVolume(Long optionCallVolume) {
		this.optionCallVolume = optionCallVolume;
	}
	public Long getOptionPutVolume() {
		return optionPutVolume;
	}
	public void setOptionPutVolume(Long optionPutVolume) {
		this.optionPutVolume = optionPutVolume;
	}
	
}
