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

public abstract class BullishSignal extends GeneralSignal {
	
	private static final Logger logger = LogManager.getLogger();

	public BullishSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, Integer type) throws Exception{
		super(stock, priceType, refPriceDateMap, stockPriceVoList, SignalParameterEntity.BUY, type);
		return;
	}
		
	protected boolean isMeetCriteria(DescriptiveStatistics maxStats, DescriptiveStatistics minStats) throws Exception {
		double[] maxReturn = maxStats.getSortedValues();
		if( StockPriceEntity.PRICE_TYPE_DAILY.equals(this.priceType) ) {
			if( maxReturn.length >= 10 ) {
				
				int index = (int) Math.ceil(maxReturn.length * 0.1);	//80%
				if( maxReturn[index] > 3 ) {	// greater than 3%
					return true;
				}
				
				//return true;
			}
		} else if( StockPriceEntity.PRICE_TYPE_WEEKLY.equals(this.priceType) ) {
			if( maxReturn.length >= 5 && maxReturn[1] > 5 ) {
				return true;
			}
		}
		return false;
	}
	
	public static void generateBullishSignal(StockEntity stock, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, List<CandlestickEntity> candlestickList, String priceType) throws Exception {
		List<SignalParameterEntity> signalList = new ArrayList<SignalParameterEntity>();
		
		if( StockPriceEntity.PRICE_TYPE_DAILY.equals(priceType) ) {
			for(int i=1; i<=19; i++) {
				BullishSignal bullishSignal = BullishSignal.getDailyBullishSignal(stock, refPriceDateMap, stockPriceVoList, i, candlestickList);
				if( bullishSignal != null ) {
					signalList.addAll(bullishSignal.getSignalParameterList());
				}
			}
		}
		Comparator<SignalParameterEntity> compareByStockCode = new Comparator<SignalParameterEntity>() {
		    @Override
		    public int compare(SignalParameterEntity s1, SignalParameterEntity s2) {
		        return s1.getStockCode().compareTo(s2.getStockCode());
		    }
		};
		Collections.sort(signalList, compareByStockCode);
		for(SignalParameterEntity signal : signalList) {
			logger.info(signal.getTradeDate() + " : " + getDailyBullishSignalDesc(signal));
		}
		return;
	}

	private static String getDailyBullishSignalDesc(SignalParameterEntity signal) {
		switch(signal.getType()) {
		case 1:
			if( StockPriceEntity.PRICE_TYPE_DAILY.equals(signal.getPriceType()) ) {
				return DailyMacdCrossAboveSignal.getSignalDesc(signal);
			}
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
	
	private static BullishSignal getDailyBullishSignal(StockEntity stock, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, int type, List<CandlestickEntity> candlestickList) throws Exception {
		String priceType = StockPriceEntity.PRICE_TYPE_DAILY;
		switch(type) {
		case 1:
			return new DailyMacdCrossAboveSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 2:
			return new DailyShortSmaCrossAboveSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 3:
			return new DailyMediumSmaCrossAboveSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 4:
			return new DailyLongSmaCrossAboveSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type);
		case 5:	case 6:	case 7:	case 8:	case 9:	case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
			return new BullishDailyCandlestickSignal(stock, priceType, refPriceDateMap, stockPriceVoList, type, candlestickList);
		default:
			return null;
		}
	}
		
}