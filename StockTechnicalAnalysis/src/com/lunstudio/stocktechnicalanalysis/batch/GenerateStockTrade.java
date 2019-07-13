package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockTradeVo;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SlopeIndicator;

@Component
public class GenerateStockTrade {

	private static final Logger logger = LogManager.getLogger();

	private static final String OUTPUTPATH = "/Volumes/HD2/Temp/TradeResult.csv";

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GenerateStockTrade instance = context.getBean(GenerateStockTrade.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start() throws Exception {
		List<StockTradeVo> allStockTradeVoList = new ArrayList<StockTradeVo>(); 
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		Map<Date, StockPriceVo> refStockPriceVoMap = this.getHsiStockPriceVoMap();
		BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUTPATH));
		//writer.write(StockTradeVo.toStringHeader());
		for(StockEntity stock : stockList) {
			if( !stock.getStockCode().equals("HKG:0700") 
				//	&& !stock.getStockCode().equals("HKG:0005") 
				//	&& !stock.getStockCode().equals("HKG:0939") 
				//	&& !stock.getStockCode().equals("HKG:2318") 
				//	&& !stock.getStockCode().equals("HKG:0700") 
					&& !stock.getStockCode().equals("INDEXHANGSENG:HSI") 
				) {
				continue;
			}
			List<StockTradeVo> stockTradeVoList = this.getStockTradeVoList(stock, refStockPriceVoMap);
			
			if( !stockTradeVoList.isEmpty() ) {
				logger.info(StockTradeVo.getTotalProfitDetail(stockTradeVoList, null));
			} else {
				logger.info(String.format("%s no trade", stock.getStockCode()));
			}
			
			allStockTradeVoList.addAll(stockTradeVoList);
		}
		
		if( !allStockTradeVoList.isEmpty() ) {
			Collections.sort(allStockTradeVoList,new Comparator<StockTradeVo>(){
				@Override
				public int compare(StockTradeVo first, StockTradeVo second) {
					return first.getBuyDate().compareTo(second.getBuyDate());
				}
			});
			writer.write(String.format("Generate Stock Trade At : %s", new Date(System.currentTimeMillis())));
			writer.write(System.lineSeparator());
			for(StockTradeVo stockTradeVo : allStockTradeVoList) {
				writer.write(stockTradeVo.toString());
				writer.write(System.lineSeparator());
			}
			logger.info(StockTradeVo.getTotalProfitDetail(allStockTradeVoList, null/*Date.valueOf("2018-01-01")*/));
		}
		writer.close();
		return;
	}
	
	
	
	
	
	
	private List<StockTradeVo> getStockTradeVoList(StockEntity stock, Map<Date, StockPriceVo> refStockPriceVoMap) throws Exception {
		List<StockTradeVo> stockTradeList = new ArrayList<StockTradeVo>();
        List<StockPriceVo> stockPriceVoList = this.stockPriceSrv.getStockPriceVoList(stock);
		
		StockTradeVo stockTradeVo = null;
		StockPriceVo stockPriceVo = null;
		BigDecimal totalHistogram = BigDecimal.ZERO;
		for(int i=3; i<stockPriceVoList.size()-1; i++) {
			stockPriceVo = stockPriceVoList.get(i);
			if( stockPriceVo.getDailyMacdHistogram() != null ) {
				totalHistogram = totalHistogram.add(stockPriceVo.getDailyMacdHistogram());
			}
			if( this.isConfirmToShortSell(stockPriceVoList, i) ) {
				if( stockTradeVo != null && StockTradeVo.TRADE_TYPE_SHORT.equals(stockTradeVo.getTradeType()) && stockTradeVo.getBuyDate() != null && stockTradeVo.getSellDate() == null ) {
					stockTradeVo.setSellDate(stockPriceVoList.get(i+1).getTradeDate());
					stockTradeVo.setSellPrice(stockPriceVoList.get(i+1).getDayHigh());
					stockTradeVo.setSellSignal(stockPriceVo);
					stockTradeVo.setSellRefSignal(refStockPriceVoMap.get(stockPriceVo.getTradeDate()));
					stockTradeList.add(stockTradeVo);
				}
			}
			
			if( stockTradeVo != null && this.isConfirmToLongSell(stockPriceVoList, i, stockTradeVo) ) {
				if( stockTradeVo != null && StockTradeVo.TRADE_TYPE_LONG.equals(stockTradeVo.getTradeType()) && stockTradeVo.getBuyDate() != null && stockTradeVo.getSellDate() == null ) {
					stockTradeVo.setSellDate(stockPriceVoList.get(i+1).getTradeDate());
					stockTradeVo.setSellPrice(stockPriceVoList.get(i+1).getDayLow());
					stockTradeVo.setSellSignal(stockPriceVo);
					stockTradeVo.setSellRefSignal(refStockPriceVoMap.get(stockPriceVo.getTradeDate()));
					stockTradeList.add(stockTradeVo);
				}
			}
			
			if( stockTradeVo == null || stockTradeVo.getSellDate() != null ) {
				stockTradeVo = this.isConfirmToLong(stockPriceVoList, i, refStockPriceVoMap);
			}
			
			if( this.isConfirmToShort(stockPriceVoList, i) ) {
				if( stockTradeVo == null || stockTradeVo.getSellDate() != null ) {
					stockTradeVo = new StockTradeVo(stock.getStockCode());
					stockTradeVo.setTradeType(StockTradeVo.TRADE_TYPE_SHORT);
					stockTradeVo.setBuyDate(stockPriceVoList.get(i+1).getTradeDate());
					stockTradeVo.setBuyPrice(stockPriceVoList.get(i+1).getDayLow());
					stockTradeVo.setBuySignal(stockPriceVo);
					stockTradeVo.setBuyRefSignal(refStockPriceVoMap.get(stockPriceVo.getTradeDate()));
					stockTradeVo.setTotalHistogram(totalHistogram);
				}
				totalHistogram = stockPriceVo.getDailyMacdHistogram();
			}
			
		}
		
		for(StockTradeVo vo : stockTradeList) {
			logger.info(vo.toString());
		}
		
		//Daily MACD Cross-Up And Weekly MACD Crossed-Up => Long
		
		//Daily MACD Cross-Down And Weekly MACD Crossed-Down or Slope of 3-days MACD is down => Long-Flat
		
		//Daily or Weekly Bull Candlestick And MACD Crossed-Up => Long
		
		return stockTradeList;
	}

	private Map<Date, StockPriceVo> getHsiStockPriceVoMap() throws Exception {
		Map<Date, StockPriceVo> map = new HashMap<Date, StockPriceVo>();
		StockEntity hsiStock = this.stockSrv.getStockInfo("INDEXHANGSENG:HSCEI");
		List<StockPriceVo> list = this.stockPriceSrv.getStockPriceVoList(hsiStock);
		for(StockPriceVo vo : list) {
			map.put(vo.getTradeDate(), vo);
		}
		return map;
	}
	
	private int getPreMacdCrossDownIndex(List<StockPriceVo> stockDataList, int tradeIndex) {
		for(int i=0; i<tradeIndex; i++) {
			if( stockDataList.get(tradeIndex-i).getDailyMacdHistogramChange() != null && stockDataList.get(tradeIndex-i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) < 0 ) {
				return tradeIndex-i;
			}			
		}
		return -1;
	}
	private int getPreMacdCrossUpIndex(List<StockPriceVo> stockDataList, int tradeIndex) {
		for(int i=0; i<tradeIndex; i++) {
			if( stockDataList.get(tradeIndex-i).getDailyMacdHistogramChange() != null && stockDataList.get(tradeIndex-i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
				return tradeIndex-i;
			}			
		}
		return -1;
	}
	
	private BigDecimal getMinMacd(List<StockPriceVo> stockDataList, int startIndex, int endIndex) {
		BigDecimal minMacd = BigDecimal.valueOf(99999);
		for(int i=startIndex; i<endIndex; i++) {
			if( stockDataList.get(i).getDailyMacd().compareTo(minMacd) <= 0 ) {
				minMacd = stockDataList.get(i).getDailyMacd();
			}
		}
		return minMacd;
	}
	
	private int getMinMacdIndex(List<StockPriceVo> stockDataList, int startIndex, int endIndex) {
		BigDecimal minMacd = BigDecimal.valueOf(99999);
		int index = -1;
		for(int i=startIndex; i<endIndex; i++) {
			if( stockDataList.get(i).getDailyMacd().compareTo(minMacd) <= 0 ) {
				minMacd = stockDataList.get(i).getDailyMacd();
				index = i;
			}
		}
		return index;
	}
	
	private int getMaxMacdIndex(List<StockPriceVo> stockDataList, int startIndex, int endIndex) {
		BigDecimal maxMacd = BigDecimal.valueOf(-99999);
		int index = -1;
		for(int i=startIndex; i<endIndex; i++) {
			if( stockDataList.get(i).getDailyMacd().compareTo(maxMacd) >= 0 ) {
				maxMacd = stockDataList.get(i).getDailyMacd();
				index = i;
			}
		}
		return index;
	}
	
	private BigDecimal getMinRsi(List<StockPriceVo> stockDataList, int startIndex, int endIndex) {
		BigDecimal minRsi = BigDecimal.valueOf(99999);
		for(int i=startIndex; i<endIndex; i++) {
			if( stockDataList.get(i).getDailyLongRsi().compareTo(minRsi) <= 0 ) {
				minRsi = stockDataList.get(i).getDailyLongRsi();
			}
		}
		return minRsi;
	}
	
	private BigDecimal getMinDayLostPrice(List<StockPriceVo> stockDataList, int startIndex, int endIndex) {
		BigDecimal minDayLow = BigDecimal.valueOf(99999);
		for(int i=startIndex; i<endIndex; i++) {
			if( stockDataList.get(i).getDayLow().compareTo(minDayLow) <= 0 ) {
				minDayLow = stockDataList.get(i).getDayLow();
			}
		}
		return minDayLow;
	}
	
	private StockTradeVo isConfirmToLong(List<StockPriceVo> stockDataList, int tradeIndex, Map<Date, StockPriceVo> refStockPriceVoMap) {
		StockTradeVo stockTradeVo = null;
		StockPriceVo stockPriceVo = stockDataList.get(tradeIndex);
/*
		if( stockPriceVo.getDailyMacdHistogramChange() != null && stockPriceVo.getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
			if( stockPriceVo.getDailyLongRsi().compareTo(BigDecimal.valueOf(35)) < 0 ) {
				return true;
			}
		}
*/
		/*
		if( stockPriceVo.getDailyMacdHistogramChange() != null && stockPriceVo.getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
			int endIndex = tradeIndex;
			int startIndex = this.getPreMacdCrossDownIndex(stockDataList, endIndex-1);
			if( startIndex > -1 ) {
				int currentMinIndex = this.getMinMacdIndex(stockDataList, startIndex, endIndex);
				if( stockDataList.get(currentMinIndex).getDailyLongRsi().compareTo(BigDecimal.valueOf(40)) < 0 ) {
					stockTradeVo = new StockTradeVo(stockDataList.get(0).getStockCode());
					stockTradeVo.setTradeType(StockTradeVo.TRADE_TYPE_LONG);
					stockTradeVo.setBuyDate(stockDataList.get(tradeIndex+1).getTradeDate());
					stockTradeVo.setBuyPrice(stockDataList.get(tradeIndex+1).getDayHigh());
					stockTradeVo.setBuySignal(stockPriceVo);
					stockTradeVo.setStoplossPrice(stockDataList.get(currentMinIndex).getDayLow());
					stockTradeVo.setBuyRefSignal(refStockPriceVoMap.get(stockPriceVo.getTradeDate()));
				}
			}
		}
		*/
		
		/*
		if( stockPriceVo.getDailyMacdHistogramChange() != null && stockPriceVo.getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
			int endIndex = tradeIndex;
			int startIndex = this.getPreMacdCrossDownIndex(stockDataList, endIndex-1);
			if( startIndex > -1 ) {
				int currentMinIndex = this.getMinMacdIndex(stockDataList, startIndex, endIndex);
				endIndex = this.getPreMacdCrossUpIndex(stockDataList, startIndex);
				//int prevMaxIndex = this.getMaxMacdIndex(stockDataList, endIndex, startIndex);
				startIndex = this.getPreMacdCrossDownIndex(stockDataList, endIndex-1);
				if( startIndex > -1 && endIndex > -1 ) {
					int prevMinIndex = this.getMinMacdIndex(stockDataList, startIndex, endIndex);
					if( prevMinIndex > -1 ) {
						if( stockDataList.get(prevMinIndex).getClosePrice().compareTo(stockDataList.get(currentMinIndex).getClosePrice()) > 0 ) {
							if( stockDataList.get(prevMinIndex).getDailyMacd().compareTo(stockDataList.get(currentMinIndex).getDailyMacd()) < 0 &&
								stockDataList.get(prevMinIndex).getDailyLongRsi().compareTo(stockDataList.get(currentMinIndex).getDailyLongRsi()) < 0 ) {
								stockTradeVo = new StockTradeVo(stockDataList.get(0).getStockCode());
								stockTradeVo.setTradeType(StockTradeVo.TRADE_TYPE_LONG);
								stockTradeVo.setBuyDate(stockDataList.get(tradeIndex+1).getTradeDate());
								stockTradeVo.setBuyPrice(stockDataList.get(tradeIndex+1).getDayHigh());
								stockTradeVo.setBuySignal(stockPriceVo);
								stockTradeVo.setStoplossPrice(stockDataList.get(currentMinIndex).getDayLow());
								stockTradeVo.setBuyRefSignal(refStockPriceVoMap.get(stockPriceVo.getTradeDate()));
							}
						}
						
					}
				}
			}
		}
		*/
		
		int startIndex = tradeIndex - 6;
		if( startIndex < 0 ) return null;
		int endIndex = tradeIndex-1;
		for(int i=startIndex; i<endIndex; i++) {
			if( stockDataList.get(i).getDailyLongRsi().compareTo(stockDataList.get(i+1).getDailyLongRsi()) < 0 ) {
				return null;
			}
		}
		if( stockDataList.get(tradeIndex).getDailyLongRsi().compareTo(stockDataList.get(endIndex).getDailyLongRsi()) > 0 ) {
			stockTradeVo = new StockTradeVo(stockDataList.get(0).getStockCode());
			stockTradeVo.setTradeType(StockTradeVo.TRADE_TYPE_LONG);
			stockTradeVo.setBuyDate(stockDataList.get(tradeIndex+1).getTradeDate());
			stockTradeVo.setBuyPrice(stockDataList.get(tradeIndex+1).getOpenPrice());
			stockTradeVo.setBuySignal(stockPriceVo);
			stockTradeVo.setStoplossPrice(stockDataList.get(tradeIndex).getDayLow());
			stockTradeVo.setBuyRefSignal(refStockPriceVoMap.get(stockPriceVo.getTradeDate()));
		}
		return stockTradeVo;
	}
	
	private boolean isConfirmToLongSell(List<StockPriceVo> stockDataList, int tradeIndex, StockTradeVo stockTradeVo) {
		StockPriceVo stockPriceVo = stockDataList.get(tradeIndex);
		BigDecimal priceDiff = MathUtils.getPriceDiff(stockTradeVo.getBuyPrice(), stockPriceVo.getClosePrice(), 2);
		if( stockTradeVo.getBuyDate().compareTo(Date.valueOf("2011-06-22")) == 0 ) {
			logger.info(String.format("%s - %s = %s", stockPriceVo.getTradeDate(), stockPriceVo.getClosePrice(), priceDiff));
		}
		if( MathUtils.getPriceDiff(stockTradeVo.getBuyPrice(), stockPriceVo.getClosePrice(), 2).compareTo(BigDecimal.valueOf(3)) > 0 ) {
			stockTradeVo.setReadyToSell(true);
		} 
		
		if( stockTradeVo.isReadyToSell()) {
			if( stockDataList.get(tradeIndex).getDailyMacdHistogram().compareTo(stockDataList.get(tradeIndex-1).getDailyMacdHistogram()) < 0 ) {
				return true;
			}
		} else {
			if( stockTradeVo.getStoplossPrice().compareTo(stockPriceVo.getClosePrice()) > 0 ) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isConfirmToShort(List<StockPriceVo> stockDataList, int tradeIndex) {
		//More than 10% and Histogram Change
		/*
		StockPriceVo stockPriceVo = stockDataList.get(tradeIndex);
		if( stockPriceVo.getWeeklyMacdHistogramSma() != null 
				&& stockPriceVo.getWeeklyMacdHistogram().compareTo(BigDecimal.ZERO) < 0
				&& stockPriceVo.getWeeklyMacdHistogram().compareTo(stockPriceVo.getWeeklyMacdHistogramSma()) < 0 ) {
			return true;
		}
		*/
		/*
		if( stockPriceVo.getWeeklyMacdHistogramChange() != null && stockPriceVo.getWeeklyMacdHistogramChange().compareTo(BigDecimal.ZERO) < 0 ) {
			return false;
		}
		*/
		/*
		if( stockPriceVo.getDailyMacdHistogramChange() != null && stockPriceVo.getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) < 0 ) {
			return true;
		}
		*/
		return false;
	}
	
	private boolean isConfirmToShortSell(List<StockPriceVo> stockDataList, int tradeIndex) {
		/*
		StockPriceVo stockPriceVo = stockDataList.get(tradeIndex);
		if( stockPriceVo.getWeeklyMacdHistogramSma() != null 
				&& stockPriceVo.getWeeklyMacdHistogram().compareTo(stockPriceVo.getWeeklyMacdHistogramSma()) > 0 ) {
			return true;
		}
		*/
		/*
		if( stockPriceVo.getWeeklyMacdHistogramChange() != null && stockPriceVo.getWeeklyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
			return true;
		}
		*/
		/*
		if( stockPriceVo.getDailyMacdHistogramChange() != null && stockPriceVo.getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
			return true;
		}
		*/
		return false;
	}
}
