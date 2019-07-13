package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.CandleStickSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockResultVo;

@Component
public class GenerateSignalStat {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	@Autowired
	private CandleStickSrv candlestickSrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GenerateSignalStat instance = context.getBean(GenerateSignalStat.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start() throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();

		for(StockEntity stock : stockList) {
			if( !stock.getStockCode().equals("HKG:0700") 
				//	&& !stock.getStockCode().equals("HKG:0005") 
				//	&& !stock.getStockCode().equals("HKG:0939") 
				//	&& !stock.getStockCode().equals("HKG:2318") 
				//	&& !stock.getStockCode().equals("HKG:0700") 
				//	&& !stock.getStockCode().equals("INDEXHANGSENG:HSI") 
				) {
				//continue;
			}
			this.generateBuySignalStat(stock);
			this.generateSellSignalStat(stock);
		}
		return;
	}
	
	private void generateBuySignalStat(StockEntity stock) throws Exception {
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), null);
		List<StockPriceEntity> weeklyStockPriceList = this.stockPriceSrv.getLastWeeklyStockPriceEntityList(stock.getStockCode(), null);
        List<StockPriceVo> stockPriceVoList = this.stockPriceSrv.getStockPriceVoList(stock, dailyStockPriceList, weeklyStockPriceList);
       	//this.getDailyMacdBuySignalTradeIndexList(stock, stockPriceVoList);
       	//this.getWeeklyMacdBuySignalTradeIndexList(stock, stockPriceVoList);
        this.generateDailyMacdDivergenceTradeIndexList(stock, stockPriceVoList);
        /*
        for(int i=0; i<=14; i++) {
        	this.getBullCandlestickBuySignalTradeIndexList(stock, stockPriceVoList, i);
        }
        */
		return;
	}
	
	private void generateSellSignalStat(StockEntity stock) throws Exception {
		return;
	}
	
	
	private void generateDailyMacdDivergenceTradeIndexList(StockEntity stock, List<StockPriceVo> stockPriceVoList) throws Exception {
        StockResultVo stockResult = new StockResultVo(stock, stockPriceVoList);
		List<Integer> tradeIndexList = new ArrayList<Integer>();
		
		for(int i=0; i<stockPriceVoList.size(); i++) {
			if(stockPriceVoList.get(i).getDailyMacdHistogramChange() != null && stockPriceVoList.get(i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
				Integer currentMinIndex = this.getCurrentMacdMinTradeIndex(stockPriceVoList, i);
				Integer prevMinIndex = this.getPreviousMacdMinTradeIndex(stockPriceVoList, i);
				if( currentMinIndex != null && prevMinIndex != null ) {
					//logger.info(String.format("%s - %s", stockPriceVoList.get(currentMinIndex).getTradeDate(), stockPriceVoList.get(prevMinIndex).getTradeDate()));
					if( stockPriceVoList.get(currentMinIndex).getClosePrice().compareTo(stockPriceVoList.get(prevMinIndex).getClosePrice()) <= 0 ) {
						if( stockPriceVoList.get(currentMinIndex).getDailyMacd().compareTo(stockPriceVoList.get(prevMinIndex).getDailyMacd()) > 0 ) {
							tradeIndexList.add(i);
						}
					}
				}
			}
		}
		stockResult.setTradeIndexList(tradeIndexList);
		stockResult.setDesc(String.format("MACD Divergence"));
    	stockResult.generateTradeStat(20, -1);
		return;
	}

	private Integer getCurrentMacdMinTradeIndex(List<StockPriceVo> stockPriceVoList, int tradeIndex) throws Exception {
		Integer currentCrossUp = tradeIndex;
		Integer currentCrossDown = this.getPrevMacdCrossDownIndex(stockPriceVoList, currentCrossUp);
		Integer currentMinIndex = null;
		if( currentCrossDown != null ) {
			currentMinIndex = this.getMinMacdIndex(stockPriceVoList, currentCrossDown, currentCrossUp);
		}
		return currentMinIndex;
	}
	
	private Integer getPreviousMacdMinTradeIndex(List<StockPriceVo> stockPriceVoList, int tradeIndex) throws Exception {
		Integer currentCrossUp = tradeIndex;
		Integer currentCrossDown = this.getPrevMacdCrossDownIndex(stockPriceVoList, currentCrossUp);
		Integer prevCrossUp = null;
		Integer prevCrossDown = null;
		Integer prevMinIndex = null;
		if( currentCrossDown != null ) {
			prevCrossUp = this.getPrevMacdCrossUpIndex(stockPriceVoList, currentCrossDown);
		}
		if( prevCrossUp != null ) {
			prevCrossDown = this.getPrevMacdCrossDownIndex(stockPriceVoList, prevCrossUp);
		}
		if( prevCrossUp != null && prevCrossDown != null ) {
			prevMinIndex = this.getMinMacdIndex(stockPriceVoList, prevCrossDown, prevCrossUp);
		}
		return prevMinIndex;
	}
	
	private List<Integer> getDailyMacdBuySignalTradeIndexList(StockEntity stock, List<StockPriceVo> stockPriceVoList) throws Exception {
        StockResultVo stockResult = null;
		List<Integer> tradeIndexList = null;
		
		for(int rsi=20; rsi<=80; rsi+=10) {
			stockResult = new StockResultVo(stock, stockPriceVoList);
			tradeIndexList = new ArrayList<Integer>();
			for(int i=0; i<stockPriceVoList.size(); i++) {
				if(stockPriceVoList.get(i).getDailyMacdHistogramChange() != null && stockPriceVoList.get(i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
					if( stockPriceVoList.get(i).getDailyLongRsi().compareTo(BigDecimal.valueOf(rsi)) < 0 ) {
						tradeIndexList.add(i);
					}
				}
			}
			stockResult.setTradeIndexList(tradeIndexList);
			stockResult.setDesc(String.format("MACD-Up & RSI < %s", rsi));
	    	stockResult.generateTradeStat(20, -1);
		}
    	
		for(int macd=-30; macd<=0; macd+=5) {
			stockResult = new StockResultVo(stock, stockPriceVoList);
			tradeIndexList = new ArrayList<Integer>();
			for(int i=0; i<stockPriceVoList.size(); i++) {
				if(stockPriceVoList.get(i).getDailyMacdHistogramChange() != null && stockPriceVoList.get(i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
					if( stockPriceVoList.get(i).getDailyMacd().compareTo(BigDecimal.valueOf((double)macd/10)) < 0 ) {
						tradeIndexList.add(i);
					}
				}
			}
			stockResult.setTradeIndexList(tradeIndexList);
			stockResult.setDesc(String.format("MACD-Up & MACD < %s", (double)macd/10));
	    	stockResult.generateTradeStat(20, -1);
		}

				
		return tradeIndexList;
	}

	private List<Integer> getWeeklyMacdBuySignalTradeIndexList(StockEntity stock, List<StockPriceVo> stockPriceVoList) throws Exception {
        StockResultVo stockResult = null;
		List<Integer> tradeIndexList = null;
		
		for(int rsi=20; rsi<=80; rsi+=10) {
			tradeIndexList = new ArrayList<Integer>();
			stockResult = new StockResultVo(stock, stockPriceVoList);
			for(int i=0; i<stockPriceVoList.size(); i++) {
				if( stockPriceVoList.get(i).getDailyLongRsi().compareTo(BigDecimal.valueOf(rsi)) < 0 ) {
					if(stockPriceVoList.get(i).getWeeklyMacdHistogramChange() != null && stockPriceVoList.get(i).getWeeklyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
						tradeIndexList.add(i);
					}
				}
			}
			stockResult.setTradeIndexList(tradeIndexList);
			stockResult.setDesc(String.format("W-MACD-Up & RSI < %s", rsi));
	    	stockResult.generateTradeStat(20, -1);
		}
    	
		for(int macd=-30; macd<=0; macd+=5) {
			stockResult = new StockResultVo(stock, stockPriceVoList);
			tradeIndexList = new ArrayList<Integer>();
			for(int i=0; i<stockPriceVoList.size(); i++) {
				if( stockPriceVoList.get(i).getWeeklyMacd() != null && stockPriceVoList.get(i).getWeeklyMacd().compareTo(BigDecimal.valueOf((double)macd/10)) < 0 ) {
					if(stockPriceVoList.get(i).getWeeklyMacdHistogramChange() != null && stockPriceVoList.get(i).getWeeklyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
						tradeIndexList.add(i);
					}
				}
			}
			stockResult.setTradeIndexList(tradeIndexList);
			stockResult.setDesc(String.format("W-MACD-Up & W-MACD < %s", (double)macd/10));
	    	stockResult.generateTradeStat(20, -1);
		}
		return tradeIndexList;
	}
	
	private List<Integer> getBullCandlestickBuySignalTradeIndexList(StockEntity stock, List<StockPriceVo> stockPriceVoList, Integer candlestickType) throws Exception {
        StockResultVo stockResult = null;
		List<Integer> tradeIndexList = null;
		Map<Date, CandlestickEntity> candleStickDateMap = this.candlestickSrv.getCandlestickDateMap(stock.getStockCode(), CandlestickEntity.DAILY, CandlestickEntity.Buy, candlestickType);
		
		for(int rsi=20; rsi<=80; rsi+=10) {
			stockResult = new StockResultVo(stock, stockPriceVoList);
			tradeIndexList = new ArrayList<Integer>();
			for(int i=0; i<stockPriceVoList.size(); i++) {
				if( candleStickDateMap.containsKey(stockPriceVoList.get(i).getTradeDate()) ) {
					if( stockPriceVoList.get(i).getDailyLongRsi().compareTo(BigDecimal.valueOf(rsi)) < 0 ) {
						tradeIndexList.add(i);
					}
				}
			}
			stockResult.setTradeIndexList(tradeIndexList);
			stockResult.setDesc(String.format("D-%s & RSI < %s", BullishCandlestickPatterns.getBullishCandlestickPatternDesc(candlestickType), rsi));
	    	stockResult.generateTradeStat(20, -1);
		}
		
		for(int macd=-30; macd<=0; macd+=5) {
			stockResult = new StockResultVo(stock, stockPriceVoList);
			tradeIndexList = new ArrayList<Integer>();
			for(int i=0; i<stockPriceVoList.size(); i++) {
				if( candleStickDateMap.containsKey(stockPriceVoList.get(i).getTradeDate()) ) {
					if( stockPriceVoList.get(i).getDailyMacd().compareTo(BigDecimal.valueOf((double)macd/10)) < 0 ) {
						tradeIndexList.add(i);
					}
				}
			}
			stockResult.setTradeIndexList(tradeIndexList);
			stockResult.setDesc(String.format("D-%s & D-MACD < %s", BullishCandlestickPatterns.getBullishCandlestickPatternDesc(candlestickType), (double)macd/10));
	    	stockResult.generateTradeStat(20, -1);
		}
		
		
		{
			stockResult = new StockResultVo(stock, stockPriceVoList);
			tradeIndexList = new ArrayList<Integer>();
			for(int i=0; i<stockPriceVoList.size(); i++) {
				if( candleStickDateMap.containsKey(stockPriceVoList.get(i).getTradeDate()) ) {
					if( stockPriceVoList.get(i).getDailyMacdHistogram().compareTo(BigDecimal.ZERO) < 0 ) {
						tradeIndexList.add(i);
					}
				}
			}
			stockResult.setTradeIndexList(tradeIndexList);
			stockResult.setDesc(String.format("D-%s & D-MACD-H < 0", BullishCandlestickPatterns.getBullishCandlestickPatternDesc(candlestickType)));
	    	stockResult.generateTradeStat(20, -1);
		}

		{
			stockResult = new StockResultVo(stock, stockPriceVoList);
			tradeIndexList = new ArrayList<Integer>();
			for(int i=0; i<stockPriceVoList.size(); i++) {
				if( candleStickDateMap.containsKey(stockPriceVoList.get(i).getTradeDate()) ) {
					if( stockPriceVoList.get(i).getDailyMacdHistogram().compareTo(BigDecimal.ZERO) < 0 ) {
						tradeIndexList.add(i);
					}
				}
			}
			stockResult.setTradeIndexList(tradeIndexList);
			stockResult.setDesc(String.format("D-%s & D-MACD-H > 0", BullishCandlestickPatterns.getBullishCandlestickPatternDesc(candlestickType)));
	    	stockResult.generateTradeStat(20, -1);
		}
		return tradeIndexList;
	}
	
	private Integer getPrevMacdCrossDownIndex(List<StockPriceVo> stockDataList, int tradeIndex) {
		for(int i=0; i<tradeIndex; i++) {
			if( stockDataList.get(tradeIndex-i).getDailyMacdHistogramChange() != null && stockDataList.get(tradeIndex-i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) < 0 ) {
				return tradeIndex-i;
			}			
		}
		return null;
	}
	private Integer getPrevMacdCrossUpIndex(List<StockPriceVo> stockDataList, int tradeIndex) {
		for(int i=0; i<tradeIndex; i++) {
			if( stockDataList.get(tradeIndex-i).getDailyMacdHistogramChange() != null && stockDataList.get(tradeIndex-i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
				return tradeIndex-i;
			}			
		}
		return null;
	}
		
	private Integer getMinMacdIndex(List<StockPriceVo> stockDataList, int startIndex, int endIndex) {
		BigDecimal minMacd = BigDecimal.valueOf(99999);
		Integer index = null;
		for(int i=startIndex; i<endIndex; i++) {
			if( stockDataList.get(i).getDailyMacd().compareTo(minMacd) <= 0 ) {
				minMacd = stockDataList.get(i).getDailyMacd();
				index = i;
			}
		}
		return index;
	}

}
