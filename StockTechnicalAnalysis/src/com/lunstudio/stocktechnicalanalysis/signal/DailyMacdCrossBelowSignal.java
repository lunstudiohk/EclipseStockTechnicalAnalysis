package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lunstudio.stocktechnicalanalysis.entity.SignalParameterEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public class DailyMacdCrossBelowSignal extends BearishSignal {

	private static final Logger logger = LogManager.getLogger();

	private List<Integer> macdCrossTradeIndexList = null;
	
	public DailyMacdCrossBelowSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, Integer signalType) throws Exception {
		super(stock, priceType, refPriceDateMap, stockPriceVoList, signalType);
		this.macdCrossTradeIndexList = SignalUtils.getDailyMacdCrossTradeIndexList(stockPriceVoList);
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
		Integer[] macdType = { SignalParameterEntity.MACD_ABOVE_ZERO, SignalParameterEntity.MACD_BELOW_ZERO, SignalParameterEntity.MACD_CROSS_ZERO };
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getMacdTypeParameterList(macdType)));
		
		//MACD-Relative
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getMacdRelativeParameterList()));
		
		//MACD-Period
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getMacdPeriodParameterList()));
				
		//Candlestick-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getCandlestickTypeParameterList()));
		
		//MACD-Price-Diff
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getPricePositiveDiffParameterList()));
		
		//SMA-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaTypeParameterList()));

		return finalList;
	}
	
	@Override
	public boolean isValid(SignalParameterEntity signal, Integer tradeIndex) throws Exception {
		int macdIndex = this.macdCrossTradeIndexList.indexOf(-1*tradeIndex);
		if( macdIndex > 0 ) {
			if( signal.isEmpty() ) {
				//logger.info(String.format("Date: %s", this.stockPriceVoList.get(tradeIndex).getTradeDate()));
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
			
			//MACD-Period
			if( SignalParameterValidator.isMacdAbovePeriodValid(stockPriceVoList, macdCrossTradeIndexList, signal, tradeIndex) ) {
				return true;
			}
			
			//MACD-Type
			if( SignalParameterValidator.isDailyMacdTypeValid(stockPriceVoList, macdCrossTradeIndexList, signal, tradeIndex, macdIndex) ) {
				return true;
			}
			
			//MACD-Price-Diff
			if( SignalParameterValidator.isMacdPricePositiveDiffValid(stockPriceVoList, macdCrossTradeIndexList, signal, tradeIndex) ) {
				return true;
			}
			
			//Candlestick-Type
			if( SignalParameterValidator.isCandlestickTypeValid(stockPriceVoList, signal, tradeIndex) ) {
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
		//RSI-Range
		if( signal1.getUpperDailyRsi() != null && signal2.getUpperDailyRsi() != null ) {
			return SignalParameterValidator.getValidRsiRangeSignal(signal1, signal2);
		} 
		//MACD-Period
		else if( signal1.getLowerPeriod() != null && signal2.getLowerPeriod() != null ) {
			return SignalParameterValidator.getValidMacdPeriodSignal(signal1, signal2);
		}
		//Price Diff
		else if( signal1.getUpperPriceDiff() != null && signal2.getUpperPriceDiff() != null ) {
			return SignalParameterValidator.getValidUpperPriceDiffSignal(signal1, signal2);
		}
		return null;
	}
	
	public static String getSignalDesc(SignalParameterEntity signal) {
		StringBuffer buf = new StringBuffer();
		buf.append(String.format("%s [賣出 - MACD跌穿]: ", signal.getStockCode()));
		if( signal.getLowerDailyRsi() != null && signal.getUpperDailyRsi() != null ) {
			buf.append(String.format("[RSI: %s - %s] ", signal.getLowerDailyRsi(), signal.getUpperDailyRsi()));
		}
		if( signal.getUpperPriceDiff() != null ) {
			buf.append(String.format("[已上升多於 %s%%] ", signal.getUpperPriceDiff()));
		}
		if( signal.getLowerPeriod() != null ) {
			buf.append(String.format("[上升多於 %s日] ", signal.getLowerPeriod()));
		}
		buf.append(BullishSignal.getSignalDesc(signal));
		return buf.toString();
	}
	
	
}
