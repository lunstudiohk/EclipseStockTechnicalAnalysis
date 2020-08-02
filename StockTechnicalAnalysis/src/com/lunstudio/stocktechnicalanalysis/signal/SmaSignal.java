package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;

public class SmaSignal extends BaseSignal {

	private final static int ABOVE = 1;
	private final static int BELOW = -1;

	public SmaSignal(SignalIndicatorPattern pattern) {
		super(pattern);
		return;
	}

	/*
	public static BaseSignal[] getSmaReboundResist(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[6];
		signals[0] = new SmaSignal(SignalIndicatorPattern.ShortSmaRebound);
		signals[0].isTriggerSignal = true;
		signals[1] = new SmaSignal(SignalIndicatorPattern.ShortSmaResist);
		signals[1].isTriggerSignal = true;
		
		signals[2] = new SmaSignal(SignalIndicatorPattern.MediumSmaRebound);
		signals[2].isTriggerSignal = true;
		signals[3] = new SmaSignal(SignalIndicatorPattern.MediumSmaResist);
		signals[3].isTriggerSignal = true;
		
		signals[4] = new SmaSignal(SignalIndicatorPattern.LongSmaRebound);
		signals[4].isTriggerSignal = true;
		signals[5] = new SmaSignal(SignalIndicatorPattern.LongSmaResist);
		signals[5].isTriggerSignal = true;
		
		
		for(int i=1; i<stockPriceList.size(); i++) {
			StockPriceEntity previous = stockPriceList.get(i-1);
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( previous.getClosePrice().compareTo(previous.getShortSma()) > 0 
					&& stockPrice.getLowPrice().compareTo(stockPrice.getShortSma()) <= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getShortSma()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			}
			if(  previous.getClosePrice().compareTo(previous.getShortSma()) < 0 
					&& stockPrice.getHighPrice().compareTo(stockPrice.getShortSma()) >= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getShortSma()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
			if( previous.getClosePrice().compareTo(previous.getMediumSma()) > 0
					&& stockPrice.getLowPrice().compareTo(stockPrice.getMediumSma()) <= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getMediumSma()) > 0 ) {
				signals[2].tradeIndexList.add(i);
			}
			if( previous.getClosePrice().compareTo(previous.getMediumSma()) < 0 
					&& stockPrice.getHighPrice().compareTo(stockPrice.getMediumSma()) >= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getMediumSma()) < 0 ) {
				signals[3].tradeIndexList.add(i);
			}
			if( previous.getClosePrice().compareTo(previous.getLongSma()) > 0
					&& stockPrice.getLowPrice().compareTo(stockPrice.getLongSma()) <= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getLongSma()) > 0 ) {
				signals[4].tradeIndexList.add(i);
			}
			if( previous.getClosePrice().compareTo(previous.getLongSma()) < 0
					&& stockPrice.getHighPrice().compareTo(stockPrice.getLongSma()) >= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getLongSma()) < 0 ) {
				signals[5].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	*/
	public static BaseSignal[] getSmaPosition(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[12];
		signals[0] = new SmaSignal(SignalIndicatorPattern.ShortSmaAboveMediumSmaAboveLongSma);
		signals[1] = new SmaSignal(SignalIndicatorPattern.ShortSmaAboveLongSmaAboveMediumSma);
		signals[2] = new SmaSignal(SignalIndicatorPattern.ShortSmaAboveMediumLongSma);
		signals[3] = new SmaSignal(SignalIndicatorPattern.ShortSmaBelowMediumLongSma);
		
		signals[4] = new SmaSignal(SignalIndicatorPattern.MediumSmaAboveShortSmaAboveLongSma);
		signals[5] = new SmaSignal(SignalIndicatorPattern.MediumSmaAboveLongSmaAboveShortSma);
		signals[6] = new SmaSignal(SignalIndicatorPattern.MediumSmaAboveShortLongSma);
		signals[7] = new SmaSignal(SignalIndicatorPattern.MediumSmaBelowShortLongSma);
		
		signals[8] = new SmaSignal(SignalIndicatorPattern.LongSmaAboveShortSmaAboveMediumSma);
		signals[9] = new SmaSignal(SignalIndicatorPattern.LongSmaAboveMediumSmaAboveShortSma);
		signals[10] = new SmaSignal(SignalIndicatorPattern.LongSmaAboveShortMediumSma);
		signals[11] = new SmaSignal(SignalIndicatorPattern.LongSmaBelowShortMediumSma);
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getShortSma().compareTo(stockPrice.getMediumSma()) > 0 
					&& stockPrice.getMediumSma().compareTo(stockPrice.getLongSma()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			}
			if( stockPrice.getShortSma().compareTo(stockPrice.getLongSma()) > 0 
					&& stockPrice.getLongSma().compareTo(stockPrice.getMediumSma()) > 0 ) {
				signals[1].tradeIndexList.add(i);
			}
			if( stockPrice.getShortSma().compareTo(stockPrice.getLongSma()) > 0 
					&& stockPrice.getShortSma().compareTo(stockPrice.getMediumSma()) > 0 ) {
				signals[2].tradeIndexList.add(i);
			}
			if( stockPrice.getShortSma().compareTo(stockPrice.getLongSma()) < 0 
					&& stockPrice.getShortSma().compareTo(stockPrice.getMediumSma()) < 0 ) {
				signals[3].tradeIndexList.add(i);
			}
			
			if( stockPrice.getMediumSma().compareTo(stockPrice.getShortSma()) > 0 
					&& stockPrice.getShortSma().compareTo(stockPrice.getLongSma()) > 0 ) {
				signals[4].tradeIndexList.add(i);
			}
			if( stockPrice.getMediumSma().compareTo(stockPrice.getLongSma()) > 0 
					&& stockPrice.getLongSma().compareTo(stockPrice.getShortSma()) > 0 ) {
				signals[5].tradeIndexList.add(i);
			}
			if( stockPrice.getMediumSma().compareTo(stockPrice.getShortSma()) > 0 
					&& stockPrice.getMediumSma().compareTo(stockPrice.getLongSma()) > 0 ) {
				signals[6].tradeIndexList.add(i);
			}
			if( stockPrice.getMediumSma().compareTo(stockPrice.getShortSma()) < 0 
					&& stockPrice.getMediumSma().compareTo(stockPrice.getLongSma()) < 0 ) {
				signals[7].tradeIndexList.add(i);
			}
			
			if( stockPrice.getLongSma().compareTo(stockPrice.getShortSma()) > 0 
					&& stockPrice.getShortSma().compareTo(stockPrice.getMediumSma()) > 0 ) {
				signals[8].tradeIndexList.add(i);
			}
			if( stockPrice.getLongSma().compareTo(stockPrice.getMediumSma()) > 0 
					&& stockPrice.getMediumSma().compareTo(stockPrice.getShortSma()) > 0 ) {
				signals[9].tradeIndexList.add(i);
			}
			if( stockPrice.getLongSma().compareTo(stockPrice.getShortSma()) > 0 
					&& stockPrice.getLongSma().compareTo(stockPrice.getMediumSma()) > 0 ) {
				signals[10].tradeIndexList.add(i);
			}
			if( stockPrice.getLongSma().compareTo(stockPrice.getShortSma()) < 0 
					&& stockPrice.getLongSma().compareTo(stockPrice.getMediumSma()) < 0 ) {
				signals[11].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getClosePriceShortSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.ClosePriceAboveShortSma);
		signals[1] = new SmaSignal(SignalIndicatorPattern.ClosePriceBelowShortSma);
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getClosePrice().compareTo(stockPrice.getShortSma()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( stockPrice.getClosePrice().compareTo(stockPrice.getShortSma()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getClosePriceMediumSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.ClosePriceAboveMediumSma);
		signals[1] = new SmaSignal(SignalIndicatorPattern.ClosePriceBelowMediumSma);
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getClosePrice().compareTo(stockPrice.getMediumSma()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( stockPrice.getClosePrice().compareTo(stockPrice.getMediumSma()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}

	public static BaseSignal[] getClosePriceLongSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.ClosePriceAboveLongSma);
		signals[1] = new SmaSignal(SignalIndicatorPattern.ClosePriceBelowLongSma);
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getClosePrice().compareTo(stockPrice.getLongSma()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( stockPrice.getClosePrice().compareTo(stockPrice.getLongSma()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getShortSmaCrossMediumSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.ShortSmaCrossAboveMediumSma);
		signals[0].isTriggerSignal = true;
		signals[1] = new SmaSignal(SignalIndicatorPattern.ShortSmaCrossBelowMediumSma);
		signals[1].isTriggerSignal = true;
	
		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getShortSma().compareTo(stockPrice.getMediumSma()) > 0 ) {
				currentTrend = ABOVE;
			} else if( stockPrice.getShortSma().compareTo(stockPrice.getMediumSma()) < 0 ) {
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
	
	public static BaseSignal[] getShortSmaCrossLongSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.ShortSmaCrossAboveLongSma);
		signals[0].isTriggerSignal = true;
		signals[1] = new SmaSignal(SignalIndicatorPattern.ShortSmaCrossBelowLongSma);
		signals[1].isTriggerSignal = true;
		
		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getShortSma().compareTo(stockPrice.getLongSma()) > 0 ) {
				currentTrend = ABOVE;
			} else if( stockPrice.getShortSma().compareTo(stockPrice.getLongSma()) < 0 ) {
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
	
	public static BaseSignal[] getMediumSmaCrossLongSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.MediumSmaCrossAboveLongSma);
		signals[0].isTriggerSignal = true;
		signals[1] = new SmaSignal(SignalIndicatorPattern.MediumSmaCrossBelowLongSma);
		signals[1].isTriggerSignal = true;
	
		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getMediumSma().compareTo(stockPrice.getLongSma()) > 0 ) {
				currentTrend = ABOVE;
			} else if( stockPrice.getMediumSma().compareTo(stockPrice.getLongSma()) < 0 ) {
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

	
	
	
	
	
	public static BaseSignal[] getClosePriceCrossShortSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[16];
		
		signals[0] = new SmaSignal(SignalIndicatorPattern.ClosePriceCrossAboveShortSma);
		signals[0].isTriggerSignal = true;
		signals[1] = new SmaSignal(SignalIndicatorPattern.ClosePriceCrossBelowShortSma);
		signals[1].isTriggerSignal = true;
		
		signals[2] = new SmaSignal(SignalIndicatorPattern.ShortSmaRebound);
		signals[2].isTriggerSignal = true;
		signals[3] = new SmaSignal(SignalIndicatorPattern.ShortSmaResist);
		signals[3].isTriggerSignal = true;
		
		int[][] above = { {0,2}, {2,4}, {4,6}, {6,8}, {8,10}};//, {10,12}, {12,14}, {14,16}, {16,18}, {18,200} };
		int[][] below = { {-2,0}, {-4,-2}, {-6,-4}, {-8,-6}, {-10,-8}};//, {-12,-10}, {-14,-12}, {-16,-14}, {-18,-16}, {-20,-18} };
		
		signals[4] = new SmaSignal(SignalIndicatorPattern.ClosePriceAboveShortSma);
		signals[5] = new SmaSignal(SignalIndicatorPattern.ClosePriceBelowShortSma);
		
		int index = 6;
		for(int[] range : above) {
			signals[index] = new SmaSignal(SignalIndicatorPattern.ClosePriceAboveShortSma);
			signals[index].param1 = BigDecimal.valueOf(range[0]);
			signals[index].param2 = BigDecimal.valueOf(range[1]);
			index++;
		}
		for(int[] range : below) {
			signals[index] = new SmaSignal(SignalIndicatorPattern.ClosePriceBelowShortSma);
			signals[index].param1 = BigDecimal.valueOf(range[0]);
			signals[index].param2 = BigDecimal.valueOf(range[1]);
			index++;
		}
		
		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=1; i<stockPriceList.size(); i++) {
			StockPriceEntity previous = stockPriceList.get(i-1);
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( previous.getClosePrice().compareTo(previous.getShortSma()) > 0
					&& stockPrice.getLowPrice().compareTo(stockPrice.getShortSma()) <= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getShortSma()) > 0 ) {
				signals[2].tradeIndexList.add(i);
			}
			if( previous.getClosePrice().compareTo(previous.getShortSma()) < 0
					&& stockPrice.getHighPrice().compareTo(stockPrice.getShortSma()) >= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getShortSma()) < 0 ) {
				signals[3].tradeIndexList.add(i);
			}
			if( stockPrice.getClosePrice().compareTo(stockPrice.getShortSma()) > 0 ) {
				currentTrend = ABOVE;
				signals[4].tradeIndexList.add(i);
			} else if( stockPrice.getClosePrice().compareTo(stockPrice.getShortSma()) < 0 ) {
				currentTrend = BELOW;
				signals[5].tradeIndexList.add(i);
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
		
		for(int i : signals[4].tradeIndexList) {
			BigDecimal diff = MathUtils.getPriceDiff(stockPriceList.get(i).getShortSma(), stockPriceList.get(i).getClosePrice(), 3);
			for(int j=6; j<11; j++) {
				if( diff.compareTo(signals[j].param1) > 0 && diff.compareTo(signals[j].param2) < 0 ) {
					signals[j].tradeIndexList.add(i);
					break;
				}
			}
		}
		for(int i : signals[5].tradeIndexList) {
			BigDecimal diff = MathUtils.getPriceDiff(stockPriceList.get(i).getShortSma(), stockPriceList.get(i).getClosePrice(), 3);
			for(int j=11; j<16; j++) {
				if( diff.compareTo(signals[j].param1) > 0 && diff.compareTo(signals[j].param2) < 0 ) {
					signals[j].tradeIndexList.add(i);
					break;
				}
			}
		}		
		return signals;
	}
	
	public static BaseSignal[] getClosePriceCrossMediumSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[16];
		
		signals[0] = new SmaSignal(SignalIndicatorPattern.ClosePriceCrossAboveMediumSma);
		signals[0].isTriggerSignal = true;
		signals[1] = new SmaSignal(SignalIndicatorPattern.ClosePriceCrossBelowMediumSma);
		signals[1].isTriggerSignal = true;
		signals[2] = new SmaSignal(SignalIndicatorPattern.ClosePriceAboveMediumSma);
		signals[3] = new SmaSignal(SignalIndicatorPattern.ClosePriceBelowMediumSma);
		signals[4] = new SmaSignal(SignalIndicatorPattern.MediumSmaRebound);
		signals[4].isTriggerSignal = true;
		signals[5] = new SmaSignal(SignalIndicatorPattern.MediumSmaResist);
		signals[5].isTriggerSignal = true;
		
		int[][] above = { {0,2}, {2,4}, {4,6}, {6,8}, {8,10}};//, {10,12}, {12,14}, {14,16}, {16,18}, {18,200} };
		int[][] below = { {-2,0}, {-4,-2}, {-6,-4}, {-8,-6}, {-10,-8}};//, {-12,-10}, {-14,-12}, {-16,-14}, {-18,-16}, {-20,-18} };
				
		int index = 6;
		for(int[] range : above) {
			signals[index] = new SmaSignal(SignalIndicatorPattern.ClosePriceAboveMediumSma);
			signals[index].param1 = BigDecimal.valueOf(range[0]);
			signals[index].param2 = BigDecimal.valueOf(range[1]);
			index++;
		}
		for(int[] range : below) {
			signals[index] = new SmaSignal(SignalIndicatorPattern.ClosePriceBelowMediumSma);
			signals[index].param1 = BigDecimal.valueOf(range[0]);
			signals[index].param2 = BigDecimal.valueOf(range[1]);
			index++;
		}
		
		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=1; i<stockPriceList.size(); i++) {
			StockPriceEntity previous = stockPriceList.get(i-1);
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( previous.getClosePrice().compareTo(previous.getMediumSma()) > 0
					&& stockPrice.getLowPrice().compareTo(stockPrice.getMediumSma()) <= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getMediumSma()) > 0 ) {
				signals[4].tradeIndexList.add(i);
			}
			if( previous.getClosePrice().compareTo(previous.getMediumSma()) < 0
					&& stockPrice.getHighPrice().compareTo(stockPrice.getMediumSma()) >= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getMediumSma()) < 0 ) {
				signals[5].tradeIndexList.add(i);
			}
			if( stockPrice.getClosePrice().compareTo(stockPrice.getMediumSma()) > 0 ) {
				currentTrend = ABOVE;
				signals[2].tradeIndexList.add(i);
			} else if( stockPrice.getClosePrice().compareTo(stockPrice.getMediumSma()) < 0 ) {
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
		
		for(int i : signals[2].tradeIndexList) {
			BigDecimal diff = MathUtils.getPriceDiff(stockPriceList.get(i).getShortSma(), stockPriceList.get(i).getClosePrice(), 3);
			for(int j=6; j<11; j++) {
				if( diff.compareTo(signals[j].param1) > 0 && diff.compareTo(signals[j].param2) < 0 ) {
					signals[j].tradeIndexList.add(i);
					break;
				}
			}
		}
		for(int i : signals[3].tradeIndexList) {
			BigDecimal diff = MathUtils.getPriceDiff(stockPriceList.get(i).getShortSma(), stockPriceList.get(i).getClosePrice(), 3);
			for(int j=11; j<16; j++) {
				if( diff.compareTo(signals[j].param1) > 0 && diff.compareTo(signals[j].param2) < 0 ) {
					signals[j].tradeIndexList.add(i);
					break;
				}
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getClosePriceCrossLongSma(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[16];
		
		signals[0] = new SmaSignal(SignalIndicatorPattern.ClosePriceCrossAboveLongSma);
		signals[0].isTriggerSignal = true;
		signals[1] = new SmaSignal(SignalIndicatorPattern.ClosePriceCrossBelowLongSma);
		signals[1].isTriggerSignal = true;
		signals[2] = new SmaSignal(SignalIndicatorPattern.ClosePriceAboveLongSma);
		signals[3] = new SmaSignal(SignalIndicatorPattern.ClosePriceBelowLongSma);
		signals[4] = new SmaSignal(SignalIndicatorPattern.LongSmaRebound);
		signals[4].isTriggerSignal = true;
		signals[5] = new SmaSignal(SignalIndicatorPattern.LongSmaResist);
		signals[5].isTriggerSignal = true;
		
		int[][] above = { {0,2}, {2,4}, {4,6}, {6,8}, {8,10}};//, {10,12}, {12,14}, {14,16}, {16,18}, {18,200} };
		int[][] below = { {-2,0}, {-4,-2}, {-6,-4}, {-8,-6}, {-10,-8}};//, {-12,-10}, {-14,-12}, {-16,-14}, {-18,-16}, {-20,-18} };
				
		int index = 6;
		for(int[] range : above) {
			signals[index] = new SmaSignal(SignalIndicatorPattern.ClosePriceAboveLongSma);
			signals[index].param1 = BigDecimal.valueOf(range[0]);
			signals[index].param2 = BigDecimal.valueOf(range[1]);
			index++;
		}
		for(int[] range : below) {
			signals[index] = new SmaSignal(SignalIndicatorPattern.ClosePriceBelowLongSma);
			signals[index].param1 = BigDecimal.valueOf(range[0]);
			signals[index].param2 = BigDecimal.valueOf(range[1]);
			index++;
		}

		int currentTrend = 0; 
		int previousTrend = 0;
		for(int i=1; i<stockPriceList.size(); i++) {
			StockPriceEntity previous = stockPriceList.get(i-1);
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( previous.getClosePrice().compareTo(previous.getLongSma()) > 0
					&& stockPrice.getLowPrice().compareTo(stockPrice.getLongSma()) <= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getLongSma()) > 0 ) {
				signals[4].tradeIndexList.add(i);
			}
			if( previous.getClosePrice().compareTo(previous.getLongSma()) < 0
					&& stockPrice.getHighPrice().compareTo(stockPrice.getLongSma()) >= 0 && stockPrice.getClosePrice().compareTo(stockPrice.getLongSma()) < 0 ) {
				signals[5].tradeIndexList.add(i);
			}
			if( stockPrice.getClosePrice().compareTo(stockPrice.getLongSma()) > 0 ) {
				currentTrend = ABOVE;
				signals[2].tradeIndexList.add(i);
			} else if( stockPrice.getClosePrice().compareTo(stockPrice.getLongSma()) < 0 ) {
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
		for(int i : signals[2].tradeIndexList) {
			BigDecimal diff = MathUtils.getPriceDiff(stockPriceList.get(i).getShortSma(), stockPriceList.get(i).getClosePrice(), 3);
			for(int j=6; j<11; j++) {
				if( diff.compareTo(signals[j].param1) > 0 && diff.compareTo(signals[j].param2) < 0 ) {
					signals[j].tradeIndexList.add(i);
					break;
				}
			}
		}
		for(int i : signals[3].tradeIndexList) {
			BigDecimal diff = MathUtils.getPriceDiff(stockPriceList.get(i).getShortSma(), stockPriceList.get(i).getClosePrice(), 3);
			for(int j=11; j<16; j++) {
				if( diff.compareTo(signals[j].param1) > 0 && diff.compareTo(signals[j].param2) < 0 ) {
					signals[j].tradeIndexList.add(i);
					break;
				}
			}
		}
		return signals;
	}	
}
