package com.lunstudio.stocktechnicalanalysis.signal;

import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BollingerSignal extends BaseSignal {

	public BollingerSignal(SignalIndicatorPattern pattern) {
		super(pattern);
		return;
	}

	public static BaseSignal[] getBollingerReboundResist(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[4];
		signals[0] = new SmaSignal(SignalIndicatorPattern.BollingerRebound);
		signals[0].isTriggerSignal = true;
		signals[1] = new SmaSignal(SignalIndicatorPattern.BollingerResist);
		signals[1].isTriggerSignal = true;
		signals[2] = new SmaSignal(SignalIndicatorPattern.BollingerAboveUpper);
		signals[3] = new SmaSignal(SignalIndicatorPattern.BollingerBelowLower);
		for(int i=1; i<stockPriceList.size(); i++) {
			StockPriceEntity previous = stockPriceList.get(i-1);
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( previous.getClosePrice().compareTo(previous.getBbLower()) > 0
					&& stockPrice.getLowPrice().compareTo(stockPrice.getBbLower()) <= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getBbLower()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} 
			if( previous.getClosePrice().compareTo(previous.getBbUpper()) < 0
					&& stockPrice.getHighPrice().compareTo(stockPrice.getBbUpper()) >= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getBbUpper()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
			if( stockPrice.getClosePrice().compareTo(stockPrice.getBbUpper()) > 0 ) {
				signals[2].tradeIndexList.add(i);
			} else if( stockPrice.getClosePrice().compareTo(stockPrice.getBbLower()) < 0 ) {
				signals[3].tradeIndexList.add(i);
			}
		}
		return signals;
	}
}
