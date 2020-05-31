package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public class DailyLongSmaCrossAboveSignal extends BullishSignal {

	private static final Logger logger = LogManager.getLogger();

	private List<Integer> longSmaList = null;
	
	public DailyLongSmaCrossAboveSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, Integer signalType) throws Exception {
		super(stock, priceType, refPriceDateMap, stockPriceVoList, signalType);
		this.longSmaList = new ArrayList<Integer>();
		int currentTrend = 0;
		int previousTrend = 0;
		
		for(int i=10; i<stockPriceVoList.size(); i++) {
			if( this.stockPriceVoList.get(i).getClosePrice().compareTo(this.stockPriceVoList.get(i).getDailyLongSma()) < 0 ) {
				currentTrend = -1;
			} else if( this.stockPriceVoList.get(i).getClosePrice().compareTo(this.stockPriceVoList.get(i).getDailyLongSma()) > 0 ) {
				currentTrend = 1;
			} else if( this.stockPriceVoList.get(i).getClosePrice().compareTo(this.stockPriceVoList.get(i).getDailyLongSma()) == 0 ) {
				currentTrend = previousTrend;
			}
			if( currentTrend == -1 && previousTrend == 1) {
				this.longSmaList.add(-1*i);
			} else if( currentTrend == 1 && previousTrend == -1) {
				this.longSmaList.add(i);
			}
			previousTrend = currentTrend;			
		}

		//logger.info(this.shortSmaList);
		return;
	}
	
	@Override
	public List<StockSignalEntity> getSignalParameterList() throws Exception {
		List<StockSignalEntity> finalList = new ArrayList<StockSignalEntity>();
		finalList.addAll(this.getSignalParameterList(20));
		return finalList;
	}
	
	public List<StockSignalEntity> getSignalParameterList(Integer period) throws Exception {
		super.period = period;
		
		List<StockSignalEntity> finalList = new ArrayList<StockSignalEntity>();
		
		//Empty Signal
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getEmptyParameterList()));

		//RSI-Range
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getRsiRangeParameterList()));

		//RSI-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getRsiTypeParameterList()));

		//Candlestick-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getCandlestickTypeParameterList()));

		//SMA-Period
		//finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaPeriodParameterList()));
		finalList.addAll(SignalParameterValidator.getLongestSmaPeriodSignal(super.getValidSignalList(SignalParameterGenerator.getSmaPeriodParameterList())));
		
		//MACD-Type
		Integer[] macdType = {StockSignalEntity.MACD_ABOVE_ZERO, StockSignalEntity.MACD_BELOW_ZERO};
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getMacdTypeParameterList(macdType)));
		
		//SMA-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaTypeParameterList()));
		
		//SAM-Price-Diff
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaUpperPriceDiffParameterList()));
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaLowerPriceDiffParameterList()));
		
		return finalList;
	}

	@Override
	public StockSignalEntity findInvalidSignal(StockSignalEntity signal1, StockSignalEntity signal2) throws Exception {
		if( signal1.isEmpty() && signal2.isEmpty() ) {
			return null;
		}
		//RSI-Range
		if( signal1.getUpperDailyRsi() != null && signal2.getUpperDailyRsi() != null ) {
			return SignalParameterValidator.getValidRsiRangeSignal(signal1, signal2);
		}
		//SMA-Period
		if( signal1.getLowerPeriod() != null && signal2.getLowerPeriod() != null) {
			return SignalParameterValidator.getValidSmaPeriodSignal(signal1, signal2);
		}
		//SAM-Price-Diff
		if( signal1.getUpperDailySma() != null && signal1.getUpperPriceDiff() != null && signal2.getUpperDailySma() != null && signal2.getUpperPriceDiff() != null ) {
			return SignalParameterValidator.getValidSmaUpperPriceDiff(signal1, signal2);
		} else if( signal1.getLowerDailySma() != null && signal1.getLowerPriceDiff() != null && signal2.getLowerDailySma() != null && signal2.getLowerPriceDiff() != null ) {
			return SignalParameterValidator.getValidSmaLowerPriceDiff(signal1, signal2);
		}		
		return null;
	}

	@Override
	public boolean isValid(StockSignalEntity signal, Integer tradeIndex) throws Exception {
		if( this.longSmaList.indexOf(tradeIndex) != -1 ) {
			if( signal.isEmpty() ) {
				return true;
			}
			
			//RSI-Range
			if( SignalParameterValidator.isRsiRangeValid(stockPriceVoList, signal, tradeIndex) ) {
				return true;
			}
			
			//RSI-Type
			if( SignalParameterValidator.isRsiTypeValid(stockPriceVoList, signal, tradeIndex) ) {
				return true;
			}

			//Candlestick-Type
			if( SignalParameterValidator.isCandlestickTypeValid(stockPriceVoList, signal, tradeIndex) ) {
				return true;
			}
			
			//MACD-Type
			if( SignalParameterValidator.isDailyMacdTypeValid(stockPriceVoList, null, signal, tradeIndex, 0) ) {
				return true;
			}
			
			//SMA-Period
			if( SignalParameterValidator.isSmaBelowPeriodValid(stockPriceVoList, this.longSmaList, signal, tradeIndex) ) {
				return true;
			}
			
			//SMA-Type
			if( SignalParameterValidator.isSmaTypeValid(stockPriceVoList, signal, tradeIndex) ) {
				return true;
			}
			//SAM-Price-Diff
			if( SignalParameterValidator.isSmaPriceDiffValid(stockPriceVoList, signal, tradeIndex) ) {
				return true;
			}			
		}
		return false;
	}

	public static String getSignalDesc(StockSignalEntity signal) {
		StringBuffer buf = new StringBuffer();
		buf.append(String.format("%s [買入 - 升穿50MA]: ", signal.getStockCode()));
		if( signal.getLowerPeriod() != null ) {
			buf.append(String.format("[跌穿50MA多於 %s日] ", signal.getLowerPeriod()));
		}
		buf.append(BullishSignal.getSignalDesc(signal));
		return buf.toString();
	}

	public static List<String> getSignalShortDesc(StockSignalEntity signal) {
		List<String> lists = new ArrayList<String>();
		lists.add("升穿50MA");
		if( signal.getLowerPeriod() != null ) {
			lists.add(String.format("跌穿50MA多於 %s日", signal.getLowerPeriod()));
		}
		lists.addAll(GeneralSignal.getSecondarySignalDesc(signal));
		return lists;
	}
}
