package com.lunstudio.stocktechnicalanalysis.signal;

import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.signal.BaseSignal.SignalIndicatorPattern;

public class ConfirmSignal extends BaseSignal {
	
	public ConfirmSignal(SignalIndicatorPattern pattern) {
		super(pattern);
		return;
	}

	public static BaseSignal[] getNextClose(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[4];
		signals[0] = new SmaSignal(SignalIndicatorPattern.ConfirmCloseAbove);
		signals[0].isTriggerSignal = false;
		signals[1] = new SmaSignal(SignalIndicatorPattern.ConfirmCloseBelow);
		signals[1].isTriggerSignal = false;
		signals[2] = new SmaSignal(SignalIndicatorPattern.ConfirmCloseAboveHigh);
		signals[2].isTriggerSignal = false;
		signals[3] = new SmaSignal(SignalIndicatorPattern.ConfirmCloseBelowLow);
		signals[3].isTriggerSignal = false;

		for(int i=0; i<stockPriceList.size()-1; i++) {
			StockPriceEntity current = stockPriceList.get(i);
			StockPriceEntity next = stockPriceList.get(i+1);
			if( next.getClosePrice().compareTo(current.getClosePrice()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( next.getClosePrice().compareTo(current.getClosePrice()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
			if( next.getClosePrice().compareTo(current.getHighPrice()) > 0 ) {
				signals[2].tradeIndexList.add(i);
			} else if( next.getClosePrice().compareTo(current.getLowPrice()) < 0 ) {
				signals[3].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getNextVolume(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.ConfirmVolumeAbove);
		signals[0].isTriggerSignal = false;
		signals[1] = new SmaSignal(SignalIndicatorPattern.ConfirmVolumeBelow);
		signals[1].isTriggerSignal = false;

		for(int i=0; i<stockPriceList.size()-1; i++) {
			StockPriceEntity current = stockPriceList.get(i);
			StockPriceEntity next = stockPriceList.get(i+1);
			if( next.getVolume().compareTo(current.getVolume()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( next.getVolume().compareTo(current.getVolume()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}

}
