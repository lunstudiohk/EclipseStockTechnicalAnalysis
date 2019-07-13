package com.lunstudio.stocktechnicalanalysis.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns.BullishPatterns;
import com.lunstudio.stocktechnicalanalysis.dao.StockPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceSummaryVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockResultVo;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SmoothedRSIIndicator;

@Service
public class StockTechnicalAnalysisSrv {

	private static final Logger logger = LogManager.getLogger();
	
	private static final String FILL = "F";
	private static final String HOLLOW = "H";
	private static final String ALL = "A";
	private static final String[] CANDLESTICKTYPE = { FILL, HOLLOW, ALL }; 
	
	private static final String CROSSUP = "U";
	private static final String CROSSDOWN = "D";
	private static final String BELOW = "B";
	private static final String ABOVE = "A";
	private static final String[] CROSSTYPE = { CROSSUP, CROSSDOWN, BELOW, ABOVE };
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	@Autowired
	private CandleStickSrv candleStickSrv;
	
	private Decimal[] rsiLimits = { 
			Decimal.valueOf(30), /* Decimal.valueOf(29), Decimal.valueOf(28), Decimal.valueOf(27), Decimal.valueOf(26),*/ Decimal.valueOf(25),
			/*Decimal.valueOf(24), Decimal.valueOf(23), Decimal.valueOf(22), Decimal.valueOf(21),*/ Decimal.valueOf(20),
			
			Decimal.valueOf(70), /* Decimal.valueOf(71), Decimal.valueOf(72), Decimal.valueOf(73), Decimal.valueOf(74), */ Decimal.valueOf(75),
			/* Decimal.valueOf(76), Decimal.valueOf(77), Decimal.valueOf(78), Decimal.valueOf(79), */ Decimal.valueOf(80)
			
			//Decimal.valueOf(50)
	};
	private int[] shortRsi = { 3, 4, 5 };
	
	
	private int[] candlestickAcceptDay = { 3 /*1, 2, 3, 4, 5*/ };
	
	public List<StockResultVo> getDailyCandlestickResult(StockEntity stock) throws Exception {
		List<StockResultVo> stockResultList = new ArrayList<StockResultVo>();
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getDailyStockPriceList(stock.getStockCode(), Date.valueOf("2008-01-01"));
		Map<Date, List<CandlestickEntity>> candlestickListDateMap = this.candleStickSrv.getCandlestickDateMapFromDate(stock.getStockCode(), Date.valueOf("2008-01-01"), StockPriceEntity.PRICE_TYPE_DAILY);
		Map<Date, Integer> dateTradeIndexMap = new HashMap<Date, Integer>();
		for(int i=0; i<dailyStockPriceList.size(); i++) {
			dateTradeIndexMap.put(dailyStockPriceList.get(i).getTradeDate(), i);
		}
		
		for(int i=0; i<BullishPatterns.values().length; i++ ) {
			for(int acceptDay: candlestickAcceptDay) {
				StockResultVo stockResult = new StockResultVo(dailyStockPriceList);
				stockResult.setDesc(String.format("%s Within %s-days", BullishCandlestickPatterns.getBullishCandlestickPatternDesc(i), acceptDay));
				stockResult.setTradeIndexList(new ArrayList<Integer>());
				for(Date tradeDate: candlestickListDateMap.keySet() ) {
					List<CandlestickEntity> candlestickList = candlestickListDateMap.get(tradeDate);
					for(CandlestickEntity candlestick : candlestickList) {
						if( CandlestickEntity.Buy.equals(candlestick.getType()) && candlestick.getCandlestickType() == i ) {
							int confirmTradeIndex = this.getBuyConfirmTradeIndex(dailyStockPriceList, dateTradeIndexMap, candlestick, acceptDay);
							if( confirmTradeIndex != -1 ) {
								stockResult.getTradeIndexList().add(confirmTradeIndex);					
							}
						}
					}
				}
				stockResultList.add(stockResult);
			}
		}
		
		for(int i=0; i<BearishPatterns.values().length; i++ ) {
			for(int acceptDay: candlestickAcceptDay) {
				StockResultVo stockResult = new StockResultVo(dailyStockPriceList);
				stockResult.setDesc(String.format("%s Within %s-days", BullishCandlestickPatterns.getBullishCandlestickPatternDesc(i), acceptDay));
				stockResult.setTradeIndexList(new ArrayList<Integer>());
				for(Date tradeDate: candlestickListDateMap.keySet() ) {
					List<CandlestickEntity> candlestickList = candlestickListDateMap.get(tradeDate);
					for(CandlestickEntity candlestick : candlestickList) {
						if( CandlestickEntity.Sell.equals(candlestick.getType()) && candlestick.getCandlestickType() == i ) {
							int confirmTradeIndex = this.getSellConfirmTradeIndex(dailyStockPriceList, dateTradeIndexMap, candlestick, acceptDay);
							if( confirmTradeIndex != -1 ) {
								stockResult.getTradeIndexList().add(confirmTradeIndex);					
							}
						}
					}
				}
				stockResultList.add(stockResult);
			}
		}
			
		return stockResultList;
	}
	
	private int getBuyConfirmTradeIndex(List<StockPriceEntity> dailyStockPriceList, Map<Date, Integer> dateTradeIndexMap, CandlestickEntity candlestick, int days) {
		int candlestickIndex = dateTradeIndexMap.get(candlestick.getTradeDate());
		int startIndex = candlestickIndex+1;
		int endIndex = startIndex + days;
		for(int i=startIndex; i<endIndex && i<dailyStockPriceList.size(); i++) {
			StockPriceEntity stockPrice = dailyStockPriceList.get(i);
			if( stockPrice.getClosePrice().compareTo(candlestick.getConfirmPrice()) > 0 ) {
				return i;
			} else if( stockPrice.getClosePrice().compareTo(candlestick.getStoplossPrice()) < 0 ) {
				return -1;
			}
		}
		return -1;
	}
	
	private int getSellConfirmTradeIndex(List<StockPriceEntity> dailyStockPriceList, Map<Date, Integer> dateTradeIndexMap, CandlestickEntity candlestick, int days) {
		int candlestickIndex = dateTradeIndexMap.get(candlestick.getTradeDate());
		int startIndex = candlestickIndex+1;
		int endIndex = startIndex + days;
		for(int i=startIndex; i<endIndex && i<dailyStockPriceList.size(); i++) {
			StockPriceEntity stockPrice = dailyStockPriceList.get(i);
			if( stockPrice.getClosePrice().compareTo(candlestick.getConfirmPrice()) < 0 ) {
				return i;
			} else if( stockPrice.getClosePrice().compareTo(candlestick.getStoplossPrice()) > 0 ) {
				return -1;
			}
		}
		return -1;
	}
	
	public List<StockResultVo> getDailyRsiResult(StockEntity stock) throws Exception {
		List<StockResultVo> stockResultList = new ArrayList<StockResultVo>();
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getDailyStockPriceList(stock.getStockCode(), Date.valueOf("2008-01-01"));
		TimeSeries dailySeries = this.stockPriceSrv.getStockTimeSeries(stock.getStockCode(), dailyStockPriceList);
		for(Decimal rsiLimit: rsiLimits) {
			for(String crossType : CROSSTYPE) {
				for(String candlestickType : CANDLESTICKTYPE) {
					StockResultVo stockResult = new StockResultVo(dailyStockPriceList);
					stockResult.setDesc(String.format("RSI: %s, Cross: %s, CandleStick: %s", rsiLimit, crossType, candlestickType));
					stockResult.setTradeIndexList(this.getRsiTradeIndex(dailySeries, rsiLimit, 10, crossType, candlestickType));
					stockResultList.add(stockResult);
				}
			}
		}
		return stockResultList;
	}
	
	/**
	 * 
	 * @param dailySeries
	 * @param rsiLimit +ve cross-up, -ve cross-down
	 * @param candlestickType F:Filled, H:Hollow
	 * @return
	 */
	private List<Integer> getRsiTradeIndex(TimeSeries dailySeries, Decimal rsiLimit, int rsiSma, String crossType, String candlestickType) {
		List<Integer> tradeIndexList = new ArrayList<Integer>();
	
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
        SmoothedRSIIndicator rsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, 14);
		SMAIndicator sma = new SMAIndicator(rsiIndicator, rsiSma);

        for(int i=1; i<dailySeries.getTickCount(); i++) {
        	if( CROSSUP.equals(crossType) ) {
        		if( rsiIndicator.getValue(i-1).isLessThan(rsiLimit) && rsiIndicator.getValue(i).isGreaterThan(rsiLimit) ) {
        			if( this.isCandlestickType(dailySeries.getTick(i), candlestickType) ) {
        				tradeIndexList.add(i);
        			}
        		}
        	} else if( CROSSDOWN.equals(crossType) ) {
        		if( rsiIndicator.getValue(i-1).isGreaterThan(rsiLimit) && rsiIndicator.getValue(i).isLessThan(rsiLimit) ) {
        			if( this.isCandlestickType(dailySeries.getTick(i), candlestickType) ) {
        				tradeIndexList.add(i);
        			}
        		}
        	}
        }
        
		return tradeIndexList;
	}
	
	private boolean isCandlestickType(Tick tick, String candlestickType) {
		if( candlestickType == null ) {
			return true;
		} 
		if( StockTechnicalAnalysisSrv.FILL.equals(candlestickType) ) {
			if( tick.getOpenPrice().isGreaterThan(tick.getClosePrice()) ) {
				return true;
			}
		} else if( StockTechnicalAnalysisSrv.HOLLOW.equals(candlestickType) ) { 
			if( tick.getClosePrice().isGreaterThan(tick.getOpenPrice()) ) {
				return true;
			}
		}
		return false;
	}
	
}
