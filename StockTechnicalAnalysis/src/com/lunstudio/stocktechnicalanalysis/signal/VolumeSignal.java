package com.lunstudio.stocktechnicalanalysis.signal;

import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class VolumeSignal extends BaseSignal {
	
	public VolumeSignal(SignalIndicatorPattern pattern) {
		super(pattern);
		return;
	}
	
	public static BaseSignal[] getVolumeSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[6];
		signals[0] = new SmaSignal(SignalIndicatorPattern.VolumeAboveShortSma);
		signals[1] = new SmaSignal(SignalIndicatorPattern.VolumeBelowShortSma);
		signals[2] = new SmaSignal(SignalIndicatorPattern.VolumeAboveMediumSma);
		signals[3] = new SmaSignal(SignalIndicatorPattern.VolumeBelowMediumSma);
		signals[4] = new SmaSignal(SignalIndicatorPattern.VolumeAboveLongSma);
		signals[5] = new SmaSignal(SignalIndicatorPattern.VolumeBelowLongSma);
		
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getVolume().compareTo(stockPrice.getVolShortSma()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( stockPrice.getVolume().compareTo(stockPrice.getVolShortSma()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
			if( stockPrice.getVolume().compareTo(stockPrice.getVolMediumSma()) > 0 ) {
				signals[2].tradeIndexList.add(i);
			} else if( stockPrice.getVolume().compareTo(stockPrice.getVolMediumSma()) < 0 ) {
				signals[3].tradeIndexList.add(i);
			}
			if( stockPrice.getVolume().compareTo(stockPrice.getVolLongSma()) > 0 ) {
				signals[4].tradeIndexList.add(i);
			} else if( stockPrice.getVolume().compareTo(stockPrice.getVolLongSma()) < 0 ) {
				signals[5].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getVolumeShortSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.VolumeAboveShortSma);
		signals[1] = new SmaSignal(SignalIndicatorPattern.VolumeBelowShortSma);
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getVolume().compareTo(stockPrice.getVolShortSma()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( stockPrice.getVolume().compareTo(stockPrice.getVolShortSma()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getVolumeMediumSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.VolumeAboveMediumSma);
		signals[1] = new SmaSignal(SignalIndicatorPattern.VolumeBelowMediumSma);
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getVolume().compareTo(stockPrice.getVolMediumSma()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( stockPrice.getVolume().compareTo(stockPrice.getVolMediumSma()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getVolumeLongSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.VolumeAboveLongSma);
		signals[1] = new SmaSignal(SignalIndicatorPattern.VolumeBelowLongSma);
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getVolume().compareTo(stockPrice.getVolLongSma()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( stockPrice.getVolume().compareTo(stockPrice.getVolLongSma()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}
}
