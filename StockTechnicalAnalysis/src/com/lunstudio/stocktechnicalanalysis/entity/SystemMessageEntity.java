package com.lunstudio.stocktechnicalanalysis.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
@Table(name = "tb_systemmessage")
public class SystemMessageEntity extends BaseEntity implements Serializable {

	public static final String ERROR = "E";
	public static final String WARNING = "W";
	public static final String INFO = "I";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Timestamp createTimestamp;
	
	private String messageType;
	
	private String messageContent;

	public SystemMessageEntity(String messageType, String messageContent) {
		super();
		this.createTimestamp = new Timestamp(System.currentTimeMillis());
		this.messageType = messageType;
		this.messageContent = messageContent;
		return;
	}
	
	public Timestamp getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Timestamp createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	
	@Override
    public boolean equals(Object obj) {
		if( this == obj ) {
			return true;
		}
		if( !(obj instanceof SystemMessageEntity) ) {
			return false;
		}
		
		SystemMessageEntity that = (SystemMessageEntity) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.createTimestamp, that.createTimestamp);
		return eb.isEquals();
	}

	
}
