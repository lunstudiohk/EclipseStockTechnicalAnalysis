package com.lunstudio.stocktechnicalanalysis.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CandleStickVo extends StockPriceEntity {

	private static BigDecimal shortShadow = BigDecimal.valueOf(0.005);
	private static BigDecimal normalShadow = BigDecimal.valueOf(0.01);
	private static BigDecimal longShadow = BigDecimal.valueOf(0.02);
	private static BigDecimal shortBody = BigDecimal.valueOf(0.01);
	private static BigDecimal dojiBody = BigDecimal.valueOf(0.005);
	private static BigDecimal longBody = BigDecimal.valueOf(0.03);
	private static BigDecimal samePrice = BigDecimal.valueOf(0.0025);
	private static BigDecimal two = BigDecimal.valueOf(2);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//private StockPriceEntity stockPrice = null;
	
	public CandleStickVo(StockPriceEntity stockPrice) {
		super();
		this.setStockCode(stockPrice.getStockCode());
		this.setTradeDate(stockPrice.getTradeDate());
		this.setOpenPrice(stockPrice.getOpenPrice());
		this.setClosePrice(stockPrice.getClosePrice());
		this.setDayHigh(stockPrice.getDayHigh());
		this.setDayLow(stockPrice.getDayLow());
		return;
	}
	
	public boolean isHollow() {
		if( this.getClosePrice().compareTo(this.getOpenPrice()) > 0 ) {
			return true;
		}
		return false;
	}
	
	public boolean isFilled() {
		if( this.getOpenPrice().compareTo(this.getClosePrice()) > 0 ) {
			return true;
		}
		return false;
	}
	
	public boolean isDoji() {
		if( this.getBodyPercentage().compareTo(dojiBody) < 0 ) {
			if( this.getLowerShadowPercentage().compareTo(normalShadow) > 0 
					&& this.getUpperShadowPercentage().compareTo(normalShadow) > 0) {
				return true;
			}
		}
		return false;
	}
	
	public BigDecimal getBody() {
		if( this.isHollow() ) {
			return this.getClosePrice().subtract(this.getOpenPrice());
		} else if( this.isFilled() ) {
			return this.getOpenPrice().subtract(this.getClosePrice());
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	public BigDecimal getBodyHalf() {
		return (this.getOpenPrice().add(this.getClosePrice())).setScale(5).divide(two, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getBodyPercentage() {
		if( this.isHollow() ) {
			return (this.getClosePrice().subtract(this.getOpenPrice())).setScale(5).divide(this.getOpenPrice(), RoundingMode.HALF_UP);
		} else if( this.isFilled() ) {
			return (this.getOpenPrice().subtract(this.getClosePrice())).setScale(5).divide(this.getClosePrice(), RoundingMode.HALF_UP);
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	public boolean isShortBody() {
		return this.getBodyPercentage().compareTo(shortBody) < 0;
	}
	
	public boolean isLongBody() {
		return this.getBodyPercentage().compareTo(longBody) > 0;
	}
	
	public BigDecimal getTop() {
		if( this.isHollow() ) {
			return this.getClosePrice();
		} else {
			return this.getOpenPrice();
		}
	}
	
	public BigDecimal getBottom() {
		if( this.isHollow() ) {
			return this.getOpenPrice();
		} else {
			return this.getClosePrice();
		}
	}
	
	public BigDecimal getUpperShadow() {
		return this.getDayHigh().subtract(this.getTop());
	}
	
	public BigDecimal getUpperShadowPercentage() {
		return (this.getDayHigh().subtract(this.getTop())).setScale(5).divide(this.getTop(), RoundingMode.HALF_UP);
	}
	
	public boolean isLongUpperShadow() {
		return this.getUpperShadowPercentage().compareTo(longShadow) > 0;
	}
	
	public boolean isShortUpperShadow() {
		return this.getUpperShadowPercentage().compareTo(shortShadow) < 0;
	}
	
	public BigDecimal getLowerShadow() {
		return this.getBottom().subtract(this.getDayLow());
	}
	
	public boolean isShortLowerShadow() {
		return this.getLowerShadowPercentage().compareTo(shortShadow) < 0;
	}
	
	public boolean isLongLowerShadow() {
		return this.getLowerShadowPercentage().compareTo(longShadow) > 0;
	}
	
	public BigDecimal getLowerShadowPercentage() {
		return (this.getBottom().subtract(this.getDayLow())).setScale(5).divide(this.getDayLow(), RoundingMode.HALF_UP);
	}
	
	public boolean isGapDown(CandleStickVo prevDate) {
		return this.getOpenPrice().compareTo(prevDate.getDayLow()) < 0;
	}
	
	public boolean isGapUp(CandleStickVo prevDate) {
		return this.getOpenPrice().compareTo(prevDate.getDayHigh()) > 0;
	}
	
	public static boolean isSamePrice(BigDecimal price, BigDecimal targetPrice) {
		BigDecimal upperLimit = targetPrice.multiply(BigDecimal.ONE.add(samePrice));
		BigDecimal lowerLimit = targetPrice.multiply(BigDecimal.ONE.subtract(samePrice));
		if( price.compareTo(lowerLimit) >= 0 && price.compareTo(upperLimit) <= 0 ) {
			return true;
		}
		return false;
	}
	
	
}
