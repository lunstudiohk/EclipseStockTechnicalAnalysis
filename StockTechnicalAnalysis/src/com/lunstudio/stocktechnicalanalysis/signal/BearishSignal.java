package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.SignalParameterEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public abstract class BearishSignal extends GeneralSignal {
	
	private static final Logger logger = LogManager.getLogger();

	public BearishSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, Integer type) throws Exception{
		super(stock, priceType, refPriceDateMap, stockPriceVoList, SignalParameterEntity.SELL, type);
		return;
	}
		
	protected boolean isMeetCriteria(DescriptiveStatistics maxStats, DescriptiveStatistics minStats) throws Exception {
		double[] minReturn = minStats.getSortedValues();
		
		if( StockPriceEntity.PRICE_TYPE_DAILY.equals(this.priceType) ) {
			if( minReturn.length >= 10 ) {
				int index = (int) Math.floor(minReturn.length * 0.9);	//90%
				if( minReturn[index] < -3 ) {	// lesser than -3%
					return true;
				}
			}
		} else if( StockPriceEntity.PRICE_TYPE_WEEKLY.equals(this.priceType) ) {
			if( minReturn.length >= 5 && minReturn[minReturn.length-2] < -5 ) {
				return true;
			}
		}
		return false;
	}
	
	public static void generateBearishSignal(StockEntity stock, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, List<CandlestickEntity> candlestickList, String priceType) throws Exception {
		List<SignalParameterEntity> signalList = new ArrayList<SignalParameterEntity>();
		if( StockPriceEntity.PRICE_TYPE_DAILY.equals(priceType) ) {
			for(int i=1; i<=21; i++) {
				BearishSignal bearishSignal = BearishSignal.getDailyBearishSignal(stock, StockPriceEntity.PRICE_TYPE_DAILY, refPriceDateMap, stockPriceVoList, i, candlestickList);
				if( bearishSignal != null ) {
					signalList.addAll(bearishSignal.getSignalParameterList());
				}
			}
		}
		/*
		if( StockPriceEntity.PRICE_TYPE_WEEKLY.equals(priceType) ) {
			for(int i=100; i<=116; i++) {
				BullishSignal bullishSignal = BullishSignal.getWeeklyBullishSignal(stock, stockPriceVoList, i, candlestickList);
				if( bullishSignal != null ) {
					signalList.addAll(bullishSignal.getSignalParameterList());
				}
			}
		}
		 */
		Comparator<SignalParameterEntity> compareByStockCode = new Comparator<SignalParameterEntity>() {
		    @Override
		    public int compare(SignalParameterEntity s1, SignalParameterEntity s2) {
		        return s1.getStockCode().compareTo(s2.getStockCode());
		    }
		};
		Collections.sort(signalList, compareByStockCode);
		for(SignalParameterEntity signal : signalList) {
			logger.info(signal.getTradeDate() + " : " + getDailyBearishSignalDesc(signal));
		}
		return;
	}

	public static String getSignalDesc(SignalParameterEntity signal) {
		return signal.toString();
	}
	
	private static String getDailyBearishSignalDesc(SignalParameterEntity signal) {
		switch(signal.getType()) {
		case 1:
			return DailyMacdCrossBelowSignal.getSignalDesc(signal);
		case 2:
			return DailyShortSmaCrossBelowSignal.getSignalDesc(signal);
		case 3:
			return DailyMediumSmaCrossBelowSignal.getSignalDesc(signal);
		case 4:
			return DailyLongSmaCrossBelowSignal.getSignalDesc(signal);
		case 5:	case 6:	case 7:	case 8:	case 9:	case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19: case 20: case 21:
			return BearishDailyCandlestickSignal.getSignalDesc(signal);
		default:
			return null;
		}		
	}
	
	private static BearishSignal getDailyBearishSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, int type, List<CandlestickEntity> candlestickList) throws Exception {
		switch(type) {
		case 1:
			return new DailyMacdCrossBelowSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 2:
			return new DailyShortSmaCrossBelowSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 3:
			return new DailyMediumSmaCrossBelowSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 4:
			return new DailyLongSmaCrossBelowSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 5:	case 6:	case 7:	case 8:	case 9:	case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19: case 20: case 21:
			return new BearishDailyCandlestickSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type, candlestickList);
		default:
			return null;
		}
	}
	
}
