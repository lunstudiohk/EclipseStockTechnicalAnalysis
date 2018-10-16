package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;


@Entity
@Table(name = "tb_stock")
public class StockEntity extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String stockCode;

	private String stockHkexCode;

	private String stockCname;

	private Date stockProcessed;

	private Boolean isHSI;

	private Boolean isHSCE;

	private String stockYahooCode;

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getStockCname() {
		return stockCname;
	}

	public void setStockCname(String stockCname) {
		this.stockCname = stockCname;
	}

	public Date getStockProcessed() {
		return stockProcessed;
	}

	public void setStockProcessed(Date stockProcessed) {
		this.stockProcessed = stockProcessed;
	}

	public Boolean getIsHSI() {
		return isHSI;
	}

	public void setIsHSI(Boolean isHSI) {
		this.isHSI = isHSI;
	}

	public Boolean getIsHSCE() {
		return isHSCE;
	}

	public void setIsHSCE(Boolean isHSCE) {
		this.isHSCE = isHSCE;
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
