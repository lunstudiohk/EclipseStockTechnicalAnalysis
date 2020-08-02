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

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public abstract class BullishSignal extends GeneralSignal {
	
	private static final Logger logger = LogManager.getLogger();
	
	private static final Integer minReturn = 5;
	
	public enum BullishSignalIndicators {
		DailyMacdCrossAboveSignal
		,DailyShortRaiCrossAboveLongRsi
		,DailyShortSmaCrossAboveMediumSma
		,DailyShortSmaCrossAboveLongSma
		,DailyMediumSmaCrossAboveLongSma
	}
	
	private static Integer getBullishSignalIndicators(BullishSignalIndicators indicator) {
		for(int i=0; i<BullishSignalIndicators.values().length; i++) {
			if( indicator == BullishSignalIndicators.values()[i]) {
				return i;
			}
		}
		return -1;
	}
	
	public BullishSignal(StockEntity stock, String priceType, List<StockPriceVo> stockPriceVoList, BullishSignalIndicators indicator) throws Exception{
		super(stock, priceType, stockPriceVoList, BullishSignal.getBullishSignalIndicators(indicator), StockSignalEntity.SIGNAL_TYPE_BUY);
		return;
	}
		
	protected boolean isMeetCriteria(DescriptiveStatistics maxStats, DescriptiveStatistics minStats) throws Exception {
		double[] maxReturn = maxStats.getSortedValues();
		if( maxReturn.length >= GeneralSignal.MIN_TRADE_COUNT ) {
			int index = (int) Math.ceil(maxReturn.length * 0.1);
			if( maxReturn[index] > minReturn ) {
				return true;
			}
		}
		return false;
	}
	
	public static List<StockSignalEntity> generateBullishSignal(StockEntity stock, List<StockPriceVo> stockPriceVoList, List<CandlestickEntity> candlestickList, String priceType) throws Exception {
		List<StockSignalEntity> todaySignalList = new ArrayList<StockSignalEntity>();
		int signalSeq = 1;
		List<StockSignalEntity> signalList = new ArrayList<StockSignalEntity>();
		for(BullishSignalIndicators indicator : BullishSignalIndicators.values() ) {
			BullishSignal bullishSignal = BullishSignal.getDailyBullishSignal(stock, stockPriceVoList, indicator, candlestickList);
			if( bullishSignal != null ) {
				signalList.addAll(bullishSignal.getSignalParameterList());
			}
		}
		
/*		
		Comparator<StockSignalEntity> compareByStockCode = new Comparator<StockSignalEntity>() {
		    @Override
		    public int compare(StockSignalEntity s1, StockSignalEntity s2) {
		        return s1.getStockCode().compareTo(s2.getStockCode());
		    }
		};
		Collections.sort(signalList, compareByStockCode);
*/		
		for(StockSignalEntity signal : signalList) {
			logger.info(signal.getTradeDate() + " : " + getDailyBullishSignalDesc(signal));
			signal.setSignalSeq(signalSeq++);
			todaySignalList.add(signal);
		}
		return todaySignalList;
	}

	
	
	
	
	
	public static List<String> getDailyBullishPrimarySignalDesc(StockSignalEntity signal) {
		switch(signal.getSignalType()) {
		case 1:
			return DailyMacdCrossAboveSignal.getSignalShortDesc(signal);
		case 2:
			return DailyShortSmaCrossAboveSignal.getSignalShortDesc(signal);
		case 3:
			return DailyMediumSmaCrossAboveSignal.getSignalShortDesc(signal);
		case 4:
			return DailyLongSmaCrossAboveSignal.getSignalShortDesc(signal);
		case 5:	case 6:	case 7:	case 8:	case 9:	case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
			return BullishDailyCandlestickSignal.getSignalShortDesc(signal);
		default:
			return null;
		}		
	}
	
	public static String getDailyBullishSignalDesc(StockSignalEntity signal) {
		switch(signal.getSignalType()) {
		case 1:
			return DailyMacdCrossAboveSignal.getSignalDesc(signal);
		case 2:
			return DailyShortSmaCrossAboveSignal.getSignalDesc(signal);
		case 3:
			return DailyMediumSmaCrossAboveSignal.getSignalDesc(signal);
		case 4:
			return DailyLongSmaCrossAboveSignal.getSignalDesc(signal);
		case 5:	case 6:	case 7:	case 8:	case 9:	case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
			return BullishDailyCandlestickSignal.getSignalDesc(signal);
		default:
			return null;
		}		
	}
	
	private static BullishSignal getDailyBullishSignal(StockEntity stock, List<StockPriceVo> stockPriceVoList, BullishSignalIndicators indicator, List<CandlestickEntity> candlestickList) throws Exception {
		switch(indicator) {
		case DailyMacdCrossAboveSignal:
			return new DailyMacdCrossAboveSignal(stock, stockPriceVoList);
			/*
		case 2:
			return new DailyShortSmaCrossAboveSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 3:
			return new DailyMediumSmaCrossAboveSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 4:
			return new DailyLongSmaCrossAboveSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 5:	case 6:	case 7:	case 8:	case 9:	case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
			return new BullishDailyCandlestickSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type, candlestickList);
			*/
		default:
			return null;
		}
	}
		
}
