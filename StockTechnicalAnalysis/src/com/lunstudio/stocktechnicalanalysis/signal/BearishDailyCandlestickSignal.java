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
import com.lunstudio.stocktechnicalanalysis.entity.SignalParameterEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public class BearishDailyCandlestickSignal extends BearishSignal {

	protected static final Logger logger = LogManager.getLogger();

	protected Map<Date, CandlestickEntity> candlestickDateMap = null;
	
	private static int offset = 5;
	
	public BearishDailyCandlestickSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, Integer type, List<CandlestickEntity> candlestickList) throws Exception {
		super(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		this.candlestickDateMap = new HashMap<Date, CandlestickEntity>();
		for(CandlestickEntity candlestick : candlestickList) {
			if( candlestick.getCandlestickType().intValue() == type-offset ) {
				if( CandlestickEntity.DAILY.equals(candlestick.getPriceType()) ) {
					this.candlestickDateMap.put(candlestick.getTradeDate(), candlestick);
				}
			}
		}
		return;
	}

	@Override
	public List<SignalParameterEntity> getSignalParameterList() throws Exception {		
		List<SignalParameterEntity> finalList = new ArrayList<SignalParameterEntity>();
		finalList.addAll(this.getSignalParameterList(20));
		//finalList.addAll(this.getSignalParameterList(20));
		return finalList;
	}

	public List<SignalParameterEntity> getSignalParameterList(Integer period) throws Exception {
		super.period = period;
		
		List<SignalParameterEntity> finalList = new ArrayList<SignalParameterEntity>();
		
		//Empty Signal
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getEmptyParameterList()));

		//RSI-Range
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getRsiRangeParameterList()));

		//RSI-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getRsiTypeParameterList()));

		//MACD-Type
		Integer[] macdType = {SignalParameterEntity.MACD_ABOVE_ZERO, SignalParameterEntity.MACD_BELOW_ZERO};
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getMacdTypeParameterList(macdType)));
		
		//SMA-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaTypeParameterList()));
		return finalList;
	}
	
	@Override
	public boolean isValid(SignalParameterEntity signal, Integer tradeIndex) throws Exception {
		
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
		}
		return false;
	}
	
	@Override
	public SignalParameterEntity findInvalidSignal(SignalParameterEntity signal1, SignalParameterEntity signal2) throws Exception {
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
		return null;
	}

	public static String getSignalDesc(SignalParameterEntity signal) {
		StringBuffer buf = new StringBuffer();
		buf.append(String.format("%s [賣出 - %s]: ", signal.getStockCode(), BullishCandlestickPatterns.getBullishCandlestickPatternDesc(signal.getType()-offset)));
		buf.append(BullishSignal.getSignalDesc(signal));
		return buf.toString();
	}

}
