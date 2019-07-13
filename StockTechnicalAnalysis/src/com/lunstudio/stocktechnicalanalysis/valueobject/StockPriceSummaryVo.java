package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.sql.Date;

import com.lunstudio.stocktechnicalanalysis.entity.BaseEntity;

public class StockPriceSummaryVo extends BaseEntity {
/*Price	Daily 
 * RSI	Weekly RSI	
 * Daily Histogram	Weekly Histogram	
 * Daily Candlestick	Weekly Candlestick	
 * Volatility	
 * Warrant Call Ratio	
 * Cbbc Bull Ratio	
 * Option Call Ratio	
 * Active Call Option	//Turnover
 * Active Put Option	
 * Heavy Call Option	//OpenInterest
 * Heavy Put Option	
 * 10 Day Difference	
 * 50 Day Difference */
	
	private String stockPrice;
	private Date tradeDate;
	private BigDecimal dailyClosePrice;
	private BigDecimal dailyRsi;
	private BigDecimal weeklyRsi;
	private BigDecimal dailyMacdHistogram;
	private String dailyMacdHistogramTurn;
	private BigDecimal weeklyMacdHistogram;
	private String weeklyMacdHistogramTurn;
	private String dailyCandlestick;
	private BigDecimal dailyCandlestickConfirm;
	private BigDecimal dailyCandlestickCancel;
	private String weeklyCandlestick;
	private BigDecimal weeklyCandlestickConfirm;
	private BigDecimal weeklyCandlestickCancel;
	private BigDecimal dailyVolatility;
	private BigDecimal dailyWarrantCallRatio;
	private BigDecimal dailyCbbcBullRatio;
	private BigDecimal dailyCbbcActiveBullRatio;	//Most turnover
	private BigDecimal dailyCbbcActiveBullPrice;
	private BigDecimal dailyCbbcActiveBearRatio;
	private BigDecimal dailyCbbcActiveBearPrice;
	private BigDecimal dailyOptionCallRatio;
	private BigDecimal dailyOptionActiveCallPrice;	//Volume
	private BigDecimal dailyOptionActiveCallRatio;
	private BigDecimal dailyOptionActivePutPrice;
	private BigDecimal dailyOptionActivePutRatio;
	private BigDecimal dailyOptionHeavyCallPrice;	//OpenInterest
	private BigDecimal dailyOptionHeavyCallRatio;
	private BigDecimal dailyOptionHeavyPutPrice;
	private BigDecimal dailyOptionHeavyPutRatio;
	private BigDecimal tenDayDiff;
	private BigDecimal fiftyDayDiff;
		
}
