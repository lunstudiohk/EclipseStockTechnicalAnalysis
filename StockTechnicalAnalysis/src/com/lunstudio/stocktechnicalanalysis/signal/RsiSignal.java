package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.signal.BaseSignal.SignalIndicatorPattern;

public class RsiSignal extends BaseSignal {

	private final static int ABOVE = 1;
	private final static int BELOW = -1;
	
	public RsiSignal(SignalIndicatorPattern pattern) {
		super(pattern);
		return;
	}
	
	public static BaseSignal[] getShortRsiCrossLongRsiSignalPattern(List<StockPriceEntity> stockPriceList) throws Exception {
		RsiSignal[] signals = new RsiSignal[4];
		
		signals[0] = new RsiSignal(SignalIndicatorPattern.ShortRsiCrossAboveLongRsi);
		signals[0].isTriggerSignal = true;
		signals[1] = new RsiSignal(SignalIndicatorPattern.ShortRsiCrossBelowLongRsi);
		signals[1].isTriggerSignal = true;
		signals[2] = new RsiSignal(SignalIndicatorPattern.ShortRsiAboveLongRsi);
		signals[3] = new RsiSignal(SignalIndicatorPattern.ShortRsiBelowLongRsi);
		
		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getShortRsi().compareTo(stockPrice.getLongRsi()) > 0 ) {
				currentTrend = ABOVE;
				signals[2].tradeIndexList.add(i);
			} else if( stockPrice.getShortRsi().compareTo(stockPrice.getLongRsi()) < 0 ) {
				currentTrend = BELOW;
				signals[3].tradeIndexList.add(i);
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
	
	public static BaseSignal[] getShortRsiRangeSignalPattern (List<StockPriceEntity> stockPriceList) throws Exception {
		RsiSignal[] signals = new RsiSignal[10];
		int[][] range = { {0,10}, {10,20}, {20,30}, {30,40}, {40,50}, {50,60}, {60,70}, {70,80}, {80,90}, {90,100} };
		
		for(int i=0; i<range.length; i++) {
			signals[i] = new RsiSignal(SignalIndicatorPattern.ShortRsiRange);
			signals[i].param1 = BigDecimal.valueOf(range[i][0]);
			signals[i].param2 = BigDecimal.valueOf(range[i][1]);
			/*
			signals[i+10] = new RsiSignal(SignalIndicatorPattern.ShortRsiRangeUpTrend);
			signals[i+10].param1 = BigDecimal.valueOf(range[i][0]);
			signals[i+10].param2 = BigDecimal.valueOf(range[i][1]);
			
			signals[i+20] = new RsiSignal(SignalIndicatorPattern.ShortRsiRangeDownTrend);
			signals[i+20].param1 = BigDecimal.valueOf(range[i][0]);
			signals[i+20].param2 = BigDecimal.valueOf(range[i][1]);
			*/
		}
		for(int i=1; i<stockPriceList.size(); i++) {
			for(int j=0; j<range.length; j++) {
				if( signals[j].isBetweenRange(stockPriceList.get(i).getShortRsi()) ) {
					signals[j].tradeIndexList.add(i);
					/*
					if( stockPriceList.get(i-1).getShortRsi().compareTo(stockPriceList.get(i).getShortRsi()) < 0 ) {
						signals[j+10].tradeIndexList.add(i);	
					}
					if( stockPriceList.get(i-1).getShortRsi().compareTo(stockPriceList.get(i).getShortRsi()) > 0 ) {
						signals[j+20].tradeIndexList.add(i);
					}
					*/
					break;
				}
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getLongRsiRangeSignalPattern (List<StockPriceEntity> stockPriceList) throws Exception {
		RsiSignal[] signals = new RsiSignal[10];
		int[][] range = { {0,10}, {10,20}, {20,30}, {30,40}, {40,50}, {50,60}, {60,70}, {70,80}, {80,90}, {90,100} };
		
		for(int i=0; i<range.length; i++) {
			signals[i] = new RsiSignal(SignalIndicatorPattern.LongRsiRange);
			signals[i].param1 = BigDecimal.valueOf(range[i][0]);
			signals[i].param2 = BigDecimal.valueOf(range[i][1]);
			/*
			signals[i+10] = new RsiSignal(SignalIndicatorPattern.LongRsiRangeUpTrend);
			signals[i+10].param1 = BigDecimal.valueOf(range[i][0]);
			signals[i+10].param2 = BigDecimal.valueOf(range[i][1]);
			
			signals[i+20] = new RsiSignal(SignalIndicatorPattern.LongRsiRangeDownTrend);
			signals[i+20].param1 = BigDecimal.valueOf(range[i][0]);
			signals[i+20].param2 = BigDecimal.valueOf(range[i][1]);
			*/
		}
		for(int i=1; i<stockPriceList.size(); i++) {
			for(int j=0; j<range.length; j++) {
				if( signals[j].isBetweenRange(stockPriceList.get(i).getLongRsi()) ) {
					signals[j].tradeIndexList.add(i);
					/*
					if( stockPriceList.get(i-1).getLongRsi().compareTo(stockPriceList.get(i).getLongRsi()) < 0 ) {
						signals[j+10].tradeIndexList.add(i);	
					}
					if( stockPriceList.get(i-1).getLongRsi().compareTo(stockPriceList.get(i).getLongRsi()) > 0 ) {
						signals[j+20].tradeIndexList.add(i);
					}
					*/
					break;
				}
			}
		}
		
		return signals;
	}	
	/**
	 * Above lower limit and below or equal upper limit
	 * @param val
	 * @return
	 */
	private boolean isBetweenRange(BigDecimal val) {
		if( val.compareTo(param1) > 0 ) {
			if( val.compareTo(param2) <= 0 ) {
				return true;
			}
		}
		return false;
	}
	
}
