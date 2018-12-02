package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;


@Entity
@Table(name = "tb_stockholder")
public class StockHolderEntity extends BaseEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String holderCode;
	
	private String holderName;


	public String getHolderCode() {
		return holderCode;
	}


	public void setHolderCode(String holderCode) {
		this.holderCode = holderCode;
	}


	public String getHolderName() {
		return holderName;
	}


	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}


	@Override
    public boolean equals(Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !(obj instanceof StockHolderEntity) ) {
			return false;
		}
		
		StockHolderEntity that = (StockHolderEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.holderCode, that.holderCode);
		return eb.isEquals();
	}

}
