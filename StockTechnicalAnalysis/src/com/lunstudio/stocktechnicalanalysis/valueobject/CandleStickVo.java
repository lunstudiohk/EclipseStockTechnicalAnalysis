package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;

public class CandleStickVo extends StockPriceEntity {
	
	private static final BigDecimal samePrice = BigDecimal.valueOf(0.0025);
	
	private static final BigDecimal veryShortShadow = BigDecimal.valueOf(0.1);
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
		this.setHighPrice(stockPrice.getHighPrice());
		this.setLowPrice(stockPrice.getLowPrice());
		this.setVolume(stockPrice.getDayVolume());
		this.setBodyMedian(stockPrice.getBodyMedian());
		this.setHighlowMedian(stockPrice.getHighlowMedian());
		this.setPriceType(stockPrice.getPriceType());
		this.setShortBody(stockPrice.getShortBody());
		this.setLongBody(stockPrice.getLongBody());
		this.setShortCandle(stockPrice.getShortCandle());
		this.setLongCandle(stockPrice.getLongCandle());
		this.setShortSma(stockPrice.getShortSma());
		this.setMediumSma(stockPrice.getMediumSma());
		this.setLongSma(stockPrice.getLongSma());
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
	
	public boolean isNoUpperShadow() {
		if( this.getUpperShadowLength().compareTo(this.getCandleLength().multiply(veryShortShadow)) <= 0 ) {
			return true;
		}
		return false;
	}
	
	public boolean isNoLowerShadow() {
		if( this.getLowerShadowLength().compareTo(this.getCandleLength().multiply(veryShortShadow)) <= 0 ) {
			return true;
		}
		return false;
	}
	
	public boolean isShortUpperShadow() {
		if( this.getUpperShadowLength().compareTo(this.getBodyLength()) < 0 ) {
			return true;
		}
		return false;
	}
	
	public boolean isShortLowerShadow() {
		if( this.getLowerShadowLength().compareTo(this.getBodyLength()) < 0 ) {
			return true;
		}
		return false;
	}
	
	public boolean isLongCandlestick() {
		if( this.getCandleLength().compareTo(this.getLongCandle()) >= 0 ) {
			return true;
		}
		return false;
	}
	public boolean isShortCandlestick() {
		if( this.getCandleLength().compareTo(this.getShortCandle()) <= 0 ) {
			return true;
		}
		return false;
	}
	public boolean isMedianCandlestick() {
		if( this.getCandleLength().compareTo(this.getHighlowMedian()) >= 0 ) {
			return true;
		}
		return false;
	}
	public boolean isShortBody() {
		return this.getBodyLength().compareTo(this.getShortBody()) <= 0;
	}
	
	public boolean isLongLowerShadow() {
		if( this.getLowerShadowLength().compareTo(this.getBodyLength()) > 0 ) {
			return true;
		}
		return false;
	}
	
	
	
	
	public BigDecimal getBodyLength() {
		return this.getClosePrice().subtract(this.getOpenPrice()).abs();
	}

	public BigDecimal getUpperShadowLength() {
		return this.getHighPrice().subtract(this.getTop());
	}
	
	public BigDecimal getLowerShadowLength() {
		return this.getBottom().subtract(this.getLowPrice());
	}
	
	public BigDecimal getBodyPercentage() {
		return MathUtils.getPriceDiff(this.getOpenPrice(), this.getClosePrice(), 3).abs();
	}
	
	public BigDecimal getUpperShadowPercentage() {
		return MathUtils.getPriceDiff(this.getTop(), this.getHighPrice(), 3);
	}

	public BigDecimal getLowerShadowPercentage() {
		return MathUtils.getPriceDiff(this.getBottom(), this.getLowPrice(), 3).abs();
	}
	
	public BigDecimal getBodyMiddle() {
		return (this.getOpenPrice().add(this.getClosePrice())).setScale(5).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
	}
		
	public BigDecimal getLowerShadowMiddle() {
		return (this.getBottom().add(this.getLowPrice())).setScale(5).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
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
	
	public BigDecimal getCandleLength() {
		return this.getHighPrice().subtract(this.getLowPrice());
	}
	
	public boolean isLongBody() {
		return this.getBodyLength().compareTo(this.getLongBody()) >= 0;
	}
	/*
	public boolean isShortCandle() {
		return this.getCandleLength().compareTo(this.getShortCandle()) <= 0;
	}
	
	public boolean isLongCandle() {
		return this.getCandleLength().compareTo(this.getLongCandle()) >= 0;
	}
	*/	
	public boolean isGapDown(CandleStickVo prevDate) {
		return this.getOpenPrice().compareTo(prevDate.getLowPrice()) < 0;
	}
	
	public boolean isGapUp(CandleStickVo prevDate) {
		return this.getOpenPrice().compareTo(prevDate.getHighPrice()) > 0;
	}

	public boolean isDoji() {
		if( this.getCandleLength().compareTo(BigDecimal.ZERO) > 0 ) {
			if( this.getBodyLength().divide(this.getCandleLength(), RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(0.1)) < 0 ) {
				if( !this.isNoLowerShadow() && !this.isNoUpperShadow() ) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	public boolean isLongUpperShadow() {
		return false;
	}
	public boolean isShortShadow(BigDecimal a) {
		return false;
	}
	public boolean isLongShadow(BigDecimal a) {
		return false;
	}
	
	
	
	public boolean isOpenInsideBody(CandleStickVo anotherCandlestick) {
		if( this.getOpenPrice().compareTo(anotherCandlestick.getTop()) < 0 && this.getOpenPrice().compareTo(anotherCandlestick.getBottom()) > 0 ) {
			return true;
		}
		return false;
	}
	
	public boolean isCloseInsideBody(CandleStickVo anotherCandlestick) {
		if( this.getClosePrice().compareTo(anotherCandlestick.getTop()) < 0 && this.getClosePrice().compareTo(anotherCandlestick.getBottom()) > 0 ) {
			return true;
		}
		return false;
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
