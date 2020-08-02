package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class MacdSignal extends BaseSignal {

	private final static int ABOVE = 1;
	private final static int BELOW = -1;

	public MacdSignal(SignalIndicatorPattern pattern) {
		super(pattern);
		return;
	}

	public static BaseSignal[] getMacdCrossSignalPattern(List<StockPriceEntity> stockPriceList) throws Exception {
		MacdSignal[] signals = new MacdSignal[2];
		
		signals[0] = new MacdSignal(SignalIndicatorPattern.MacdCrossAboveSignal);
		signals[0].isTriggerSignal = true;
		signals[1] = new MacdSignal(SignalIndicatorPattern.MacdCrossBelowSignal);
		signals[1].isTriggerSignal = true;
		
		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getMacd().compareTo(stockPrice.getMacdSignal()) > 0 ) {
				currentTrend = ABOVE;
			} else if( stockPrice.getMacd().compareTo(stockPrice.getMacdSignal()) < 0 ) {
				currentTrend = BELOW;
			} else {
				currentTrend = previousTrend;
			}
			if( currentTrend != 0 && previousTrend != 0 ) {
				if( currentTrend != previousTrend ) {
					if( currentTrend == ABOVE ) {
						signals[0].tradeIndexList.add(i);
					} else if( currentTrend == BELOW ) {
						signals[1].tradeIndexList.add(i);
					}
				}
			}
			previousTrend = currentTrend;
		}

		return signals;
	}

	
	
	public static BaseSignal[] getMacdZeroPattern(List<StockPriceEntity> stockPriceList) throws Exception {
		MacdSignal[] signals = new MacdSignal[4];
		
		signals[0] = new MacdSignal(SignalIndicatorPattern.MacdCrossAboveZero);
		signals[0].isTriggerSignal = true;
		signals[1] = new MacdSignal(SignalIndicatorPattern.MacdCrossBelowZero);
		signals[1].isTriggerSignal = true;
		signals[2] = new MacdSignal(SignalIndicatorPattern.MacdAboveZero);
		signals[3] = new MacdSignal(SignalIndicatorPattern.MacdBelowZero);
		
		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getMacd().compareTo(BigDecimal.ZERO) > 0 ) {
				signals[2].tradeIndexList.add(i);				
				currentTrend = ABOVE;
			} else if( stockPrice.getMacd().compareTo(BigDecimal.ZERO) < 0 ) {
				signals[3].tradeIndexList.add(i);
				currentTrend = BELOW;
			} else {
				currentTrend = previousTrend;
			}
			if( currentTrend != 0 && previousTrend != 0 ) {
				if( currentTrend != previousTrend ) {
					if( currentTrend == ABOVE ) {
						signals[0].tradeIndexList.add(i);
					} else if( currentTrend == BELOW ) {
						signals[1].tradeIndexList.add(i);
					}
				}
			}
			previousTrend = currentTrend;
		}
		
		return signals;
	}
	
	public static BaseSignal[] getMacdSignalZeroPattern(List<StockPriceEntity> stockPriceList) throws Exception {
		MacdSignal[] signals = new MacdSignal[4];
		
		signals[0] = new MacdSignal(SignalIndicatorPattern.MacdSignalCrossAboveZero);
		signals[0].isTriggerSignal = true;
		signals[1] = new MacdSignal(SignalIndicatorPattern.MacdSignalCrossBelowZero);
		signals[1].isTriggerSignal = true;
		signals[2] = new MacdSignal(SignalIndicatorPattern.MacdSignalAboveZero);
		signals[3] = new MacdSignal(SignalIndicatorPattern.MacdSignalBelowZero);
		
		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getMacdSignal().compareTo(BigDecimal.ZERO) > 0 ) {
				signals[2].tradeIndexList.add(i);				
				currentTrend = ABOVE;
			} else if( stockPrice.getMacdSignal().compareTo(BigDecimal.ZERO) < 0 ) {
				signals[3].tradeIndexList.add(i);
				currentTrend = BELOW;
			} else {
				currentTrend = previousTrend;
			}
			if( currentTrend != 0 && previousTrend != 0 ) {
				if( currentTrend != previousTrend ) {
					if( currentTrend == ABOVE ) {
						signals[0].tradeIndexList.add(i);
					} else if( currentTrend == BELOW ) {
						signals[1].tradeIndexList.add(i);
					}
				}
			}
			previousTrend = currentTrend;
		}
		
		return signals;
	}
	
}
