package com.lunstudio.stocktechnicalanalysis.entity;

import java.math.BigDecimal;
import java.sql.Date;

public class StrategyEntity {

	//@Id
	private String stockCode;
	
	//@Id
	private Date tradeDate;
	
	//@Id
	private Integer candleStickPattern;
	
	private Integer tradeDay;
	
	private BigDecimal targetBuyPrice;
	
	private BigDecimal targetSellPrice;
	
	private BigDecimal stopLossPrice;
	
	
}
