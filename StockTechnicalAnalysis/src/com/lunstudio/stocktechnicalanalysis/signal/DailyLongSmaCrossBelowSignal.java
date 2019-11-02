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

public class DailyLongSmaCrossBelowSignal extends BearishSignal {

	private static final Logger logger = LogManager.getLogger();

	private List<Integer> longSmaList = null;
	
	public DailyLongSmaCrossBelowSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, Integer signalType) throws Exception {
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
		/*
		for(int tradeIndex : this.shortSmaList ) {
			if( tradeIndex > 0 ) {
				logger.info("U: " + this.stockPriceVoList.get(tradeIndex).getTradeDate());
			} else if( tradeIndex < 0 ) {
				logger.info("D: " + this.stockPriceVoList.get(-1*tradeIndex).getTradeDate());
			}
		}
		*/
		return;
	}

	@Override
	public List<SignalParameterEntity> getSignalParameterList() throws Exception {
		List<SignalParameterEntity> finalList = new ArrayList<SignalParameterEntity>();
		finalList.addAll(this.getSignalParameterList(20));
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

		//Candlestick-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getCandlestickTypeParameterList()));
		
		//SMA-Period
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaPeriodParameterList()));

		//MACD-Type
		Integer[] macdType = {SignalParameterEntity.MACD_ABOVE_ZERO, SignalParameterEntity.MACD_BELOW_ZERO};
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getMacdTypeParameterList(macdType)));

		//SMA-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaTypeParameterList()));
		
		//SMA-Type
		finalList.addAll(super.getValidSignalList(SignalParameterGenerator.getSmaTypeParameterList()));
		return finalList;
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

	@Override
	public boolean isValid(SignalParameterEntity signal, Integer tradeIndex) throws Exception {
		if( this.longSmaList.indexOf(-1*tradeIndex) != -1 ) {
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
			if( SignalParameterValidator.isSmaAbovePeriodValid(stockPriceVoList, this.longSmaList, signal, tradeIndex) ) {
				return true;
			}
			
			//SMA-Type
			if( SignalParameterValidator.isSmaTypeValid(stockPriceVoList, signal, tradeIndex) ) {
				return true;
			}
		}
		return false;
	}

	public static String getSignalDesc(SignalParameterEntity signal) {
		StringBuffer buf = new StringBuffer();
		buf.append(String.format("%s [賣出 - 跌穿50MA]: ", signal.getStockCode()));
		if( signal.getLowerPeriod() != null ) {
			buf.append(String.format("[升穿50MA多於 %s日] ", signal.getLowerPeriod()));
		}
		buf.append(BullishSignal.getSignalDesc(signal));
		return buf.toString();
	}

}
