package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;


@Entity
@Table(name = "tb_stock")
public class StockEntity extends BaseEntity implements Serializable {

	public final static String HSI = "^HSI";	//"INDEXHANGSENG:HSI";
	public final static String HSCEI = "^HSCE";	//"INDEXHANGSENG:HSCEI";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String COLON = ":";
	
	@Id
	private String stockCode;
	
	private String stockType;

	@Transient
	private String stockHkexCode;

	private String stockCname;

	private String stockEname;

	private String stockRegion;

	private Boolean isHsi;
	
	private Boolean isHsce;

	private Boolean isNasdaq;
	
	private Boolean isDji;
	
	@Transient
	private String stockYahooCode;
	@Transient
	private String stockNasdaqCode;
	
	//private Date stockProcessed;

	//private Boolean isHSI;

	//private Boolean isHSCE;

	//private BigDecimal hsiRatio;
	
	//private BigDecimal hsceiRatio;

	//private String stockAtsCode;
	
	public String getStockShortCode() {
		return this.stockCode.split(COLON)[1];
	}
	
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getStockCname() {
		return stockCname;
	}
	public String getStockName() {
		if( this.stockCname != null && this.stockCname.trim().length() > 0 ) {
			return this.stockCname;
		} else {
			return this.stockEname;
		}
	}
	public void setStockCname(String stockCname) {
		this.stockCname = stockCname;
	}

	public String getStockHkexCode() {
		return stockHkexCode;
	}
	
	public void setStockHkexCode(String stockHkexCode) {
		this.stockHkexCode = stockHkexCode;
	}

	public String getStockYahooCode() {
		return stockYahooCode;
	}

	public void setStockYahooCode(String stockYahooCode) {
		this.stockYahooCode = stockYahooCode;
	}

	
	public String getStockNasdaqCode() {
		return stockNasdaqCode;
	}

	public void setStockNasdaqCode(String stockNasdaqCode) {
		this.stockNasdaqCode = stockNasdaqCode;
	}

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	public String getStockEname() {
		return stockEname;
	}

	public void setStockEname(String stockEname) {
		this.stockEname = stockEname;
	}

	public String getStockRegion() {
		return stockRegion;
	}

	public void setStockRegion(String stockRegion) {
		this.stockRegion = stockRegion;
	}

	public Boolean getIsHsi() {
		return isHsi;
	}

	public void setIsHsi(Boolean isHsi) {
		this.isHsi = isHsi;
	}

	public Boolean getIsHsce() {
		return isHsce;
	}

	public void setIsHsce(Boolean isHsce) {
		this.isHsce = isHsce;
	}

	public Boolean getIsNasdaq() {
		return isNasdaq;
	}

	public void setIsNasdaq(Boolean isNasdaq) {
		this.isNasdaq = isNasdaq;
	}

	public Boolean getIsDji() {
		return isDji;
	}

	public void setIsDji(Boolean isDji) {
		this.isDji = isDji;
	}

	public String getTrimStockHexCode() throws Exception {
		/*
		if( stockHkexCode.startsWith("0") ) {
			return stockHkexCode.substring(1);
		} else {
			return stockHkexCode;
		}
		*/
		return this.stockCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StockEntity)) {
			return false;
		}

		StockEntity that = (StockEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.stockCode, that.stockCode);
		return eb.isEquals();
	}
}
