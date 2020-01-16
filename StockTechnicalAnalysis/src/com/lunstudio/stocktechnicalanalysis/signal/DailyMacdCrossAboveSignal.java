package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public class DailyMacdCrossAboveSignal extends BullishSignal {

	private static final Logger logger = LogManager.getLogger();

	private List<Integer> macdCrossTradeIndexList = null;
	
	public DailyMacdCrossAboveSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, Integer signalType) throws Exception {
		super(stock, priceType, refPriceDateMap, stockPriceVoList, signalType);
		this.macdCrossTradeIndexList = SignalUtils.getDailyMacdCrossTradeIndexList(stockPriceVoList);
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
		Integer[] macdType = { StockSignalEntity.MACD_ABOVE_ZERO, StockSignalEntity.MACD_BELOW_ZERO, StockSignalEntity.MACD_CROSS_ZERO };
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getMacdTypeParameterList(macdType)));
		
		//MACD-Relative
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getMacdRelativeParameterList()));
		
		//MACD-Period
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getMacdPeriodParameterList()));
				
		//Candlestick-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getCandlestickTypeParameterList()));
		
		//MACD-Price-Diff
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getPriceNegativeDiffParameterList()));
		
		//SMA-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaTypeParameterList()));
		
		return finalList;
	}
	
	@Override
	public boolean isValid(StockSignalEntity signal, Integer tradeIndex) throws Exception {
		int macdIndex = this.macdCrossTradeIndexList.indexOf(tradeIndex);
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
			if( SignalParameterValidator.isMacdBelowPeriodValid(stockPriceVoList, macdCrossTradeIndexList, signal, tradeIndex) ) {
				return true;
			}
			
			//MACD-Type
			if( SignalParameterValidator.isDailyMacdTypeValid(stockPriceVoList, macdCrossTradeIndexList, signal, tradeIndex, macdIndex) ) {
				return true;
			}
			
			//MACD-Price-Diff
			if( SignalParameterValidator.isMacdPriceNegativeDiffValid(stockPriceVoList, macdCrossTradeIndexList, signal, tradeIndex) ) {
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
	public StockSignalEntity findInvalidSignal(StockSignalEntity signal1, StockSignalEntity signal2) throws Exception {
		//RSI-Range
		if( signal1.getUpperDailyRsi() != null && signal2.getUpperDailyRsi() != null ) {
			return SignalParameterValidator.getValidRsiRangeSignal(signal1, signal2);
		} 
		//MACD-Period
		else if( signal1.getLowerPeriod() != null ) {
			return SignalParameterValidator.getValidMacdPeriodSignal(signal1, signal2);
		}
		//Price Diff
		else if( signal1.getLowerPriceDiff() != null && signal2.getLowerPriceDiff() != null ) {
			return SignalParameterValidator.getValidLowerPriceDiffSignal(signal1, signal2);
		}
		return null;
	}
	
	public static String getSignalDesc(StockSignalEntity signal) {
		StringBuffer buf = new StringBuffer();
		buf.append(String.format("%s [買入 - MACD升穿]: ", signal.getStockCode()));
		if( signal.getLowerPriceDiff() != null ) {
			buf.append(String.format("[已下跌多於 %s%%] ", signal.getLowerPriceDiff()));
		}
		if( signal.getLowerPeriod() != null ) {
			buf.append(String.format("[下跌多於 %s日] ", signal.getLowerPeriod()));
		}
		buf.append(BullishSignal.getSignalDesc(signal));
		return buf.toString();
	}
	
	public static String getSignalShortDesc(StockSignalEntity signal) {
		StringBuffer buf = new StringBuffer();
		buf.append("MACD升穿");
		if( signal.getLowerPriceDiff() != null ) {
			buf.append(String.format("  已下跌多於 %s%%", signal.getLowerPriceDiff().setScale(1, RoundingMode.HALF_UP)));
		}
		if( signal.getLowerPeriod() != null ) {
			buf.append(String.format("  下跌多於 %s日", signal.getLowerPeriod()));
		}
		return buf.toString();
	}
	
}
