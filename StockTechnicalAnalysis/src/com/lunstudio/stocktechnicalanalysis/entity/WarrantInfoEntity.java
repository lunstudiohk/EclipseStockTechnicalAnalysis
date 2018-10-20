package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
@Table(name = "tb_warrantinfo")
public class WarrantInfoEntity extends BaseEntity implements Serializable {

	public static final String WARRANT_TYPE_CALL = "C";
	public static final String WARRANT_TYPE_PUT = "P";

	private static final long serialVersionUID = 1L;

	@Id
	private String warrantCode;

	private String warrantIssuer;

	private String warrantUnderlying;

	private String warrantType;

	private Date warrantListDate;

	private Date warrantMaturityDate;

	private BigDecimal warrantStrikePrice;

	private BigDecimal warrantRatio;

	public String getWarrantCode() {
		return warrantCode;
	}

	public void setWarrantCode(String warrantCode) {
		this.warrantCode = warrantCode;
	}

	public String getWarrantIssuer() {
		return warrantIssuer;
	}

	public void setWarrantIssuer(String warrantIssuer) {
		this.warrantIssuer = warrantIssuer;
	}

	public String getWarrantUnderlying() {
		return warrantUnderlying;
	}

	public void setWarrantUnderlying(String warrantUnderlying) {
		this.warrantUnderlying = warrantUnderlying;
	}

	public String getWarrantType() {
		return warrantType;
	}

	public void setWarrantType(String warrantType) {
		this.warrantType = warrantType;
	}

	public Date getWarrantListDate() {
		return warrantListDate;
	}

	public void setWarrantListDate(Date warrantListDate) {
		this.warrantListDate = warrantListDate;
	}

	public Date getWarrantMaturityDate() {
		return warrantMaturityDate;
	}

	public void setWarrantMaturityDate(Date warrantMaturityDate) {
		this.warrantMaturityDate = warrantMaturityDate;
	}

	public BigDecimal getWarrantStrikePrice() {
		return warrantStrikePrice;
	}

	public void setWarrantStrikePrice(BigDecimal warrantStrikePrice) {
		this.warrantStrikePrice = warrantStrikePrice;
	}

	public BigDecimal getWarrantRatio() {
		return warrantRatio;
	}

	public void setWarrantRatio(BigDecimal warrantRatio) {
		this.warrantRatio = warrantRatio;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof WarrantInfoEntity)) {
			return false;
		}

		WarrantInfoEntity that = (WarrantInfoEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.warrantCode, that.warrantCode);
		return eb.isEquals();
	}
}
