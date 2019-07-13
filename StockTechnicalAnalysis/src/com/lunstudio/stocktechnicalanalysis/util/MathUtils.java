package com.lunstudio.stocktechnicalanalysis.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import eu.verdelhan.ta4j.Trade;
import eu.verdelhan.ta4j.TradingRecord;

public class MathUtils {

	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final BigDecimal MINUSHUNDRED = BigDecimal.valueOf(-100);
	
	public static BigDecimal getPriceDiff(BigDecimal initialPrice, BigDecimal finalPrice, int dp) {
		if( initialPrice != null && finalPrice != null && initialPrice.compareTo(BigDecimal.ZERO) != 0 ) {
			return (((finalPrice.subtract(initialPrice)).divide(initialPrice, 8, RoundingMode.HALF_UP)).multiply(HUNDRED)).setScale(dp, RoundingMode.HALF_UP);
		} else if( initialPrice == null && finalPrice != null ) {
			return BigDecimal.valueOf(100).setScale(dp, RoundingMode.HALF_UP);
		} else if( initialPrice != null && finalPrice == null ) {
			return BigDecimal.valueOf(-100).setScale(dp, RoundingMode.HALF_UP);
		} else {
			return null;
		}
	}
	

	
	
	
	/*
	public static double getRoundedValue(BigDecimal val) {
		if( val.doubleValue() < 25.0 ) {
			BigDecimal roundedValue = val.setScale(1, RoundingMode.HALF_UP);
			return roundedValue.doubleValue();
		}
		
		if( val.doubleValue() < 250.0 ) {
			BigDecimal roundedValue = val.setScale(0, RoundingMode.HALF_UP);
			return roundedValue.doubleValue();
		}
		
		return Math.round((val.doubleValue()/10.0) * 10.0);
	}
	
	public static double getPriceDiff(double initialPrice, double finalPrice) {
		return ((finalPrice - initialPrice)/initialPrice) * 100.0;
	}
	
	public static double getPriceDiff(BigDecimal initialPrice, BigDecimal finalPrice) {
		return ((finalPrice.doubleValue() - initialPrice.doubleValue())/initialPrice.doubleValue()) * 100.0;
	}
	
	
	
	public static BigDecimal getPriceDiffInBigDecimal(BigDecimal initialPrice, BigDecimal finalPrice) {
		if( initialPrice != null && finalPrice != null && initialPrice.compareTo(BigDecimal.ZERO) > 0 ) {
			return ((finalPrice.subtract(initialPrice)).divide(initialPrice, 5, RoundingMode.HALF_UP)).multiply(HUNDRED);
		} else if( initialPrice == null && finalPrice != null ) {
			return HUNDRED;
		} else if( initialPrice != null && finalPrice == null ) {
			return MINUSHUNDRED;
		} else {
			return null;
		}
	}
	
	public static BigDecimal getTargetPrice(BigDecimal initPrice, BigDecimal change) {
		double finalPrice = initPrice.doubleValue() * (1+change.doubleValue()/100);
		return BigDecimal.valueOf(finalPrice).setScale(3, RoundingMode.HALF_UP);
	}
	
	public static double getTotalProditInPrecent(TradingRecord tradingRecord) {
		double totalProfit = 0.0;
		for(Trade trade : tradingRecord.getTrades()) {
			if( !trade.getExit().getPrice().isNaN() && !trade.getEntry().getPrice().isNaN() ) {
				double buyPrice = trade.getEntry().getPrice().toDouble();
				double sellPrice = trade.getExit().getPrice().toDouble();
				double profit = ((sellPrice - buyPrice)/buyPrice);
				totalProfit += profit;
			}
		}
		return totalProfit * 100;
	}
	*/
}
