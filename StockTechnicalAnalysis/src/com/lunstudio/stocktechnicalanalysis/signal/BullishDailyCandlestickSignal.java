package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns.BullishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public class BullishDailyCandlestickSignal extends BullishSignal {

	protected static final Logger logger = LogManager.getLogger();

	protected Map<Date, CandlestickEntity> candlestickDateMap = null;
	
	private static int offset = 5;
	public BullishDailyCandlestickSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, Integer type, List<CandlestickEntity> candlestickList) throws Exception {
		super(stock, priceType, stockPriceVoList, null);
		this.candlestickDateMap = new HashMap<Date, CandlestickEntity>();
		for(CandlestickEntity candlestick : candlestickList) {
			if( candlestick.getCandlestickType().intValue() == (type-offset) ) {
				if( CandlestickEntity.DAILY.equals(candlestick.getPriceType()) ) {
					this.candlestickDateMap.put(candlestick.getTradeDate(), candlestick);
				}
			}
		}
		return;
	}

	@Override
	public List<StockSignalEntity> getSignalParameterList() throws Exception {		
		List<StockSignalEntity> finalList = new ArrayList<StockSignalEntity>();
		finalList.addAll(this.getSignalParameterList(20));
		//finalList.addAll(this.getSignalParameterList(20));
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
	public boolean isValid(StockSignalEntity signal, Integer tradeIndex) throws Exception {
		
		if( candlestickDateMap.containsKey(this.stockPriceVoList.get(tradeIndex).getTradeDate()) ) {
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
			
			//MACD-Type
			if( SignalParameterValidator.isDailyMacdTypeValid(stockPriceVoList, null, signal, tradeIndex, 0) ) {
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

	public static String getSignalDesc(StockSignalEntity signal) {
		StringBuffer buf = new StringBuffer();
		buf.append(String.format("%s [買入 - %s]: ", signal.getStockCode(), BullishCandlestickPatterns.getBullishCandlestickPatternDesc(signal.getSignalType()-offset)));
		buf.append(BullishSignal.getSignalDesc(signal));
		return buf.toString();
	}

	public static List<String> getSignalShortDesc(StockSignalEntity signal) {
		List<String> lists = new ArrayList<String>();
		lists.add(BullishCandlestickPatterns.getBullishCandlestickPatternDesc(signal.getSignalType()-offset));
		lists.addAll(GeneralSignal.getSecondarySignalDesc(signal));
		return lists;
	}
}
