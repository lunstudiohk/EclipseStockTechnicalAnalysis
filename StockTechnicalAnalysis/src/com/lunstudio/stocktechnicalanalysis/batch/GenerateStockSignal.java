package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;


import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.CandleStickSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSignalDateSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSignalSrv;
import com.lunstudio.stocktechnicalanalysis.signal.BaseSignal;
import com.lunstudio.stocktechnicalanalysis.signal.StockTradeSignal;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockTradeVo;

@Component
public class GenerateStockSignal {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	@Autowired
	private CandleStickSrv candlestickSrv;

	@Autowired
	private StockSignalSrv stockSignalSrv;
	
	@Autowired
	private StockSignalDateSrv stockSignalDateSrv;
	
	private static final int HISTORICAL_SIZE = 2500;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GenerateStockSignal instance = context.getBean(GenerateStockSignal.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	
	private void start(String[] args) throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		List<StockTradeVo> stockTradeList = new ArrayList<StockTradeVo>();

		for(StockEntity stock : stockList) {
			if( stock.getStockRegion().equals(args[0]) || "ALL".equals(args[0]) ) {
				stockTradeList.addAll(this.generateTradeSignal(stock, Integer.parseInt(args[1])));
			}
		}
		stockTradeList.sort(new Comparator<StockTradeVo>() {
			@Override
			public int compare(StockTradeVo s1, StockTradeVo s2) {
				return s1.getBuyDate().compareTo(s2.getBuyDate());
		     }
		});
		BigDecimal totalProfit = BigDecimal.ZERO;
		int gain = 0;
		int loss = 0;
		for(StockTradeVo stockTrade : stockTradeList) {
			BigDecimal profit = stockTrade.getProfit();
			totalProfit = totalProfit.add(profit);
			if( profit.compareTo(BigDecimal.ZERO) > 0 ) {
				gain++;
			} else if(profit.compareTo(BigDecimal.ZERO) < 0 ) {
				loss++;
			}
			System.out.println(stockTrade.toString());
		}
		System.out.println(String.format("Total Trade: %s ; Gain: %s ; Loss: %s ; Profit: %s", stockTradeList.size(), gain, loss, totalProfit));
		return;
	}

	private List<StockTradeVo> generateTradeSignal(StockEntity stock, Integer size) throws Exception {
		if( !stock.getStockCode().equals("5-HK")
				//&& !stock.getStockCode().equals("700-HK")
				//&& !stock.getStockCode().equals("1299-HK")
				//&& !stock.getStockCode().equals("2382-HK")
				//&& !stock.getStockCode().equals(".HSI")
				//&& !stock.getStockCode().equals("0700.HK")
				//&& !stock.getStockCode().equals(".DJI")
				//&& !stock.getStockCode().equals(".IXIC")
				//&& !stock.getStockCode().equals(".SSEC")
		) {
			//PAYX
			//return;
		}
		
		//StockTradeSignal.init(BigDecimal.valueOf(0.9), BigDecimal.valueOf(0.9), BigDecimal.valueOf(5), BigDecimal.valueOf(-5));
		Integer period = StockTradeSignal.SHORT;
		//System.out.print(String.format("Method Processing stock: %s - %s	", stock.getStockCname(), stock.getStockCode()));
		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), HISTORICAL_SIZE);
		int startTradeIndex = stockPriceList.size() - size;
		List<StockTradeSignal> stockTradeSignalList = this.generateStockTradeSignal(stock, stockPriceList, startTradeIndex, period);
		//System.out.println(stockTradeSignalList.size());
		return this.stimulateStockTrade(stock, stockPriceList, stockTradeSignalList, startTradeIndex, period);
			
	}

	private List<StockTradeVo> stimulateStockTrade(StockEntity stock, List<StockPriceEntity> stockPriceList, List<StockTradeSignal> stockTradeSignalList, int startTradeIndex, Integer period) throws Exception {
		int endTradeIndex = stockPriceList.size();
		Map<Integer, List<StockTradeSignal>> dateStockTradeSignalMap = new HashMap<Integer, List<StockTradeSignal>>();
		for(int tradeIndex=startTradeIndex; tradeIndex<endTradeIndex; tradeIndex++) {
			for(StockTradeSignal stockTradeSignal : stockTradeSignalList) {
				if( stockTradeSignal.getTradeIndexList().contains(tradeIndex) ) {
					if( !dateStockTradeSignalMap.containsKey(tradeIndex) ) {
						dateStockTradeSignalMap.put(tradeIndex, new ArrayList<StockTradeSignal>());
					}
					dateStockTradeSignalMap.get(tradeIndex).add(stockTradeSignal);
				}
			}
		}
		/*
		List<Integer> sortedKeys = new ArrayList<Integer>(dateStockTradeSignalMap.size());
		sortedKeys.addAll(dateStockTradeSignalMap.keySet());
		Collections.sort(sortedKeys);
		*/
		List<StockTradeVo> stockTradeList = new ArrayList<StockTradeVo>();
		for(Integer key : dateStockTradeSignalMap.keySet()) {
			DescriptiveStatistics stats = new DescriptiveStatistics();
			List<StockTradeSignal> dateStockTradeSignalList = dateStockTradeSignalMap.get(key);
			for(StockTradeSignal stockTradeSignal : dateStockTradeSignalList) {
				for(double val : stockTradeSignal.getReturnStatistics(period, key, 2).getValues()) {
					stats.addValue(val);
				}
			}
			if( stats.getPercentile(50) > 5 && stats.getPercentile(50) > stats.getMean() ) {
				double targetPrice = stockPriceList.get(key).getClosePrice().doubleValue() * (1 + stats.getPercentile(50)/100);
				if( key+1 < stockPriceList.size() ) {
					if( stockPriceList.get(key+1).getOpenPrice().doubleValue() < targetPrice ) {
						StockTradeVo stockTrade = new StockTradeVo(stock.getStockCode());
						stockTrade.setTradeType(StockTradeVo.TRADE_TYPE_LONG);
						stockTradeList.add(stockTrade);
						stockTrade.setBuyPrice(stockPriceList.get(key+1).getOpenPrice());
						stockTrade.setBuyDate(stockPriceList.get(key+1).getTradeDate());
						for(int i=key+1; i<key+10 && i<stockPriceList.size(); i++) {
							if( stockPriceList.get(i).getHighPrice().doubleValue() > targetPrice ) {
								stockTrade.setSellPrice(BigDecimal.valueOf(targetPrice).setScale(3, RoundingMode.HALF_UP));
								stockTrade.setSellDate(stockPriceList.get(i).getTradeDate());
								stockTrade.setDayCount(i-key);
							}
						}
						if( stockTrade.getSellPrice() == null ) {
							if( key+10 < stockPriceList.size() ) {
								stockTrade.setSellPrice(stockPriceList.get(key+10).getClosePrice());
								stockTrade.setSellDate(stockPriceList.get(key+10).getTradeDate());
								stockTrade.setDayCount(10);
							}
						}
					}
				} else {
					StockTradeVo stockTrade = new StockTradeVo(stock.getStockCode());
					stockTrade.setTradeType(StockTradeVo.TRADE_TYPE_LONG);
					stockTradeList.add(stockTrade);
					stockTrade.setBuyDate(DateUtils.addDays(stockPriceList.get(key).getTradeDate(), 1));
				}
			}
		}
		/*
		int[] lastSizeList = { 5, 6, 7, 8 };
		int[] upperPercentileList = { 20, 30, 40, 50 }; 
		int[] lowerPercentileList = { 20, 30, 40, 50 };
		double[] upperList = { 5, 6, 7, 8 };
		double[] lowerList = { -2, -1, 0 };
		
		for(int lastSize : lastSizeList) {
			for(int upperPercentile : upperPercentileList ) {
				for(int lowerPercentile : lowerPercentileList ) {
					for(double upper : upperList) {
						for(double lower : lowerList) {
							for(Integer key : sortedKeys) {
								int signalCount = 0;
								List<StockTradeSignal> dateStockTradeSignalList = dateStockTradeSignalMap.get(key);
								for(StockTradeSignal stockTradeSignal : dateStockTradeSignalList) {
									if( stockTradeSignal.isValid(key, lastSize, BigDecimal.valueOf(upper), period) ) {
										DescriptiveStatistics max = stockTradeSignal.getMaxReturn(period, key);
										DescriptiveStatistics min = stockTradeSignal.getMinReturn(period, key);
										if( max.getPercentile(upperPercentile) > upper && min.getPercentile(lowerPercentile) > lower ) {
											//System.out.println(stockTradeSignal.toString(key));
											signalCount++;
										}
									}
								}
								if( signalCount > 0 ) {
								System.out.println(String.format("Size: %s ; Upper[%s]: %s ; Lower[%s]: %s", lastSize, upperPercentile, upper, lowerPercentile, lower));
								System.out.print(String.format("Date: %s - Signal Count: %s", stockPriceList.get(key).getTradeDate(), dateStockTradeSignalMap.get(key).size()));
								System.out.println(" ; Filtered Signal Count: " + signalCount);
								}
							}							
						}
					}
				}
			}
		}
		*/
		
		
		
		
		
		/*
		
		Map<Integer, List<StockTradeSignal>> dateStockTradeSignalMap = new HashMap<Integer, List<StockTradeSignal>>();
		for(StockTradeSignal stockTradeSignal : stockTradeSignalList) {
			if( stockTradeSignal.getTradeIndexList().contains(o))
				
			List<Integer> tradeIndexList = stockTradeSignal.getTradeIndexList(tradeIndex);
				if( this.isValid(tradeIndexList, stockPriceList, 5, upper, period) ) {
					DescriptiveStatistics max = stockTradeSignal.getMaxReturn(period, tradeIndex);
					DescriptiveStatistics min = stockTradeSignal.getMinReturn(period, tradeIndex);
					if( max.getPercentile(50) > upper.doubleValue() && min.getPercentile(50) > lower.doubleValue() ) {
						validStockTradeSignalList.add(stockTradeSignal);		
					}
				}
		}
		
		BigDecimal totalProfit = BigDecimal.ZERO;
		int tradeCount = 0;

		if( !validStockTradeSignalList.isEmpty() ) {
			BigDecimal buyPrice = null;
			int buyIndex = 0;
			BigDecimal sellPrice = null;
			int sellIndex = 0;
			BigDecimal targetBuyPrice = stockPriceList.get(tradeIndex).getClosePrice().multiply(BigDecimal.valueOf(0.99));
			BigDecimal targetSellPrice = stockPriceList.get(tradeIndex).getClosePrice().multiply(BigDecimal.valueOf(1.05));
				
			boolean isConfirmSignal = true;
			for(StockTradeSignal stockTradeSignal : validStockTradeSignalList) {
				if( !stockTradeSignal.isConfirmSignal() ) {
					isConfirmSignal = false;
				}
			}
				
			if( stockPriceList.get(tradeIndex+1).getLowPrice().compareTo(targetBuyPrice) < 0 ) {
				buyIndex = tradeIndex+1;
				buyPrice = targetBuyPrice;
			} else if( stockPriceList.get(tradeIndex+2).getLowPrice().compareTo(targetBuyPrice) < 0 ) {
				buyIndex = tradeIndex+2;
				buyPrice = targetBuyPrice;
			}
				
			for(int i=buyIndex; i<=tradeIndex+10; i++) {
				if(stockPriceList.get(i).getHighPrice().compareTo(targetSellPrice) >= 0 ) {
					sellPrice = targetSellPrice;
					sellIndex = i;
				}
			}
			if( sellIndex == 0 ) {
				sellPrice = stockPriceList.get(tradeIndex+10).getClosePrice();
				sellIndex = tradeIndex+10;
			}
			BigDecimal profit = MathUtils.getPriceDiff(buyPrice, sellPrice, 3);
			totalProfit = totalProfit.add(profit);
			tradeCount++;
		}
		if( tradeCount > 0 ) {
			System.out.println(String.format("Trade Count: %s - Total Profit: %s = Avg: %s", tradeCount, totalProfit, totalProfit.divide(BigDecimal.valueOf(tradeCount), 3, RoundingMode.HALF_UP)));
		} else {
			System.out.println("N/A");
		}
		*/
		return stockTradeList;
	}
	
	private boolean isValid(List<Integer> tradeIndexList, List<StockPriceEntity> stockPriceList, int size, BigDecimal target, Integer period) {
		int startIndex = tradeIndexList.size()-size-1;
		if( startIndex < 0 ) {
			return false;
		}
		for(int i=startIndex; i<tradeIndexList.size(); i++) {
			int tradeIndex = tradeIndexList.get(i);
			if( period == StockTradeSignal.SHORT && stockPriceList.get(tradeIndex).getShortMaxReturn().compareTo(target) < 0 ) {
				return false;
			}
			if( period == StockTradeSignal.MEDIUM && stockPriceList.get(tradeIndex).getMediumMaxReturn().compareTo(target) < 0 ) {
				return false;
			}
			if( period == StockTradeSignal.LONG && stockPriceList.get(tradeIndex).getLongMaxReturn().compareTo(target) < 0 ) {
				return false;
			}
		}
		return true;
	}
	
	private BigDecimal getTotalProfit(Integer tradeIndex, List<StockPriceEntity> stockPriceList, List<StockTradeSignal> tradeDateSignalList, Integer period) {
		BigDecimal totalProfit = BigDecimal.ZERO;
		
		return totalProfit;
	}
	
	
	private List<StockTradeSignal> generateStockTradeSignal(StockEntity stock, List<StockPriceEntity> stockPriceList, Integer startTradeIndex, Integer period) throws Exception {

		List<List<StockTradeSignal>> signalList = new ArrayList<List<StockTradeSignal>>();
		
		List<List<StockTradeSignal>> macdStockTradeSignalList = StockTradeSignal.getStockTradeSignalList(stock, stockPriceList, BaseSignal.getMacdSignalList(stockPriceList), startTradeIndex, period);
		/*
		List<StockTradeSignal> macdResult = this.getStockTradeSignalCombinationList(macdStockTradeSignalList, startTradeIndex, period);
		System.out.println("Macd Size: " + macdResult.size());
		signalList.add(macdResult);
		*/
		List<List<StockTradeSignal>> rsiStockTradeSignalList = StockTradeSignal.getStockTradeSignalList(stock, stockPriceList, BaseSignal.getRsiSignalList(stockPriceList), startTradeIndex, period);
		/*
		List<StockTradeSignal> rsiResult = this.getStockTradeSignalCombinationList(rsiStockTradeSignalList, startTradeIndex, period);
		System.out.println("Rsi Size: " + rsiResult.size());
		signalList.add(rsiResult);
		*/
		List<List<StockTradeSignal>> smaStockTradeSignalList = StockTradeSignal.getStockTradeSignalList(stock, stockPriceList, BaseSignal.getSmaSignalList(stockPriceList), startTradeIndex, period);
		/*
		List<StockTradeSignal> smaResult = this.getStockTradeSignalCombinationList(smaStockTradeSignalList, startTradeIndex, period);
		System.out.println("Sma Size: " + smaResult.size());
		signalList.add(smaResult);
		*/
		List<List<StockTradeSignal>> volStockTradeSignalList = StockTradeSignal.getStockTradeSignalList(stock, stockPriceList, BaseSignal.getVolumeSignalList(stockPriceList), startTradeIndex, period);
		/*
		List<StockTradeSignal> volResult = this.getStockTradeSignalCombinationList(volStockTradeSignalList, startTradeIndex, period);
		System.out.println("Volume Size: " + volResult.size());
		signalList.add(volResult);
		*/
		List<List<StockTradeSignal>> candlestickStockTradeSignalList = StockTradeSignal.getStockTradeSignalList(stock, stockPriceList, BaseSignal.getCandlestickSignalList(stockPriceList), startTradeIndex, period);
		/*
		List<StockTradeSignal> candlestickResult = this.getStockTradeSignalCombinationList(candlestickStockTradeSignalList, startTradeIndex, period);
		System.out.println("Candlestick Size: " + candlestickResult.size());
		signalList.add(candlestickResult);
		*/
		List<List<StockTradeSignal>> bollingerStockTradeSignalList = StockTradeSignal.getStockTradeSignalList(stock, stockPriceList, BaseSignal.getBollingerSignalList(stockPriceList), startTradeIndex, period);
		/*
		List<StockTradeSignal> bollingerResult = this.getStockTradeSignalCombinationList(bollingerStockTradeSignalList, startTradeIndex, period);
		System.out.println("Bollinger Size: " + bollingerResult.size());
		signalList.add(bollingerResult);
		*/
		List<List<StockTradeSignal>> confirmStockTradeSignalList = StockTradeSignal.getStockTradeSignalList(stock, stockPriceList, BaseSignal.getConfirmSignalList(stockPriceList), startTradeIndex, period);
		/*
		List<StockTradeSignal> confirmSignalResult = this.getStockTradeSignalCombinationList(confirmStockTradeSignalList, startTradeIndex, period);
		System.out.println("Confirm Size: " + confirmSignalResult.size());
		signalList.add(confirmSignalResult);
		*/
		//List<StockTradeSignal> finalResults = this.getUniqueTriggerStockTradeSignal(this.getStockTradeSignalCombinationList(signalList, startTradeIndex, period));
		List<StockTradeSignal> finalResults = new ArrayList<StockTradeSignal>();
		finalResults.addAll(this.getList(macdStockTradeSignalList));
		finalResults.addAll(this.getList(rsiStockTradeSignalList));
		finalResults.addAll(this.getList(smaStockTradeSignalList));
		finalResults.addAll(this.getList(volStockTradeSignalList));
		finalResults.addAll(this.getList(candlestickStockTradeSignalList));
		finalResults.addAll(this.getList(bollingerStockTradeSignalList));
		//finalResults.addAll(this.getList(confirmStockTradeSignalList));
		/*
		finalResults.sort(new Comparator<StockTradeSignal>() {
			@Override
			public int compare(StockTradeSignal s1, StockTradeSignal s2) {
				if( s1.getSignalList().size() == s2.getSignalList().size() ) {
					return 0;
				} else if( s1.getSignalList().size() > s2.getSignalList().size() ) {
					return 1;
				} else {
					return -1;
				}
		     }
		});
		*/
		return finalResults;
		/*
		Map<Integer, List<StockTradeSignal>> tradeDateMap = new HashMap<Integer, List<StockTradeSignal>>();
		for(int i=1; i<=size; i++) {
			int targetIndex = stockPriceList.size()-i;
			//System.out.println(" ========== Target Index : " + targetIndex + " ========== ");
			for(StockTradeSignal stockTradeSignal : finalResults) {
				stockTradeSignal.calculate(targetIndex);
				if( stockTradeSignal.isValid() ) {
					if( !tradeDateMap.containsKey(targetIndex) ) {
						tradeDateMap.put(targetIndex, new ArrayList<StockTradeSignal>());
					}
					//stockTradeSignal.print();
					tradeDateMap.get(targetIndex).add(stockTradeSignal);
				}
			}
		}
		
		
		BigDecimal totalProfit = BigDecimal.ZERO;
		int tradeCount = 0;
		List<Integer> sortedKeys = new ArrayList<Integer>(tradeDateMap.size());
		sortedKeys.addAll(tradeDateMap.keySet());
		Collections.sort(sortedKeys); 
		
		for(int tradeIndex : sortedKeys) {
			if( tradeIndex + 11 >= stockPriceList.size() ) {
				continue;
			}
			tradeCount++;
			List<StockTradeSignal> tradeDateSignalList = tradeDateMap.get(tradeIndex);
			BigDecimal buyPrice = stockPriceList.get(tradeIndex+1).getOpenPrice();
			Integer buyTradeIndex = tradeIndex+1;
			BigDecimal sellPrice = null;
			Integer sellTradeIndex = null;
			BigDecimal target = null;
			for(StockTradeSignal signal : tradeDateSignalList) {
				signal.calculate(tradeIndex);
				if( signal.isConfirmSignal() ) {
					buyPrice = stockPriceList.get(tradeIndex+2).getOpenPrice();
					buyTradeIndex = tradeIndex+2;
				}
				if( target == null ) {
					target = BigDecimal.valueOf(signal.getShortMaxReturn().getPercentile(50));
				} else if( target.doubleValue() > signal.getShortMaxReturn().getPercentile(50) ) {
					target = BigDecimal.valueOf(signal.getShortMaxReturn().getPercentile(50));
				}
			}
			BigDecimal targetPrice = stockPriceList.get(tradeIndex).getClosePrice().multiply(BigDecimal.ONE.add(target.divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP)));
			int endIndex = tradeIndex + 10;
			for(int i=tradeIndex+1; i<=endIndex; i++) {
				if( stockPriceList.get(i).getHighlowMedian().compareTo(targetPrice) >= 0 ) {
					sellPrice = targetPrice;
					sellTradeIndex = i;
					break;
				}
			}
			if( sellPrice == null ) {
				sellPrice = stockPriceList.get(endIndex).getClosePrice();
				sellTradeIndex = endIndex;
			}
			BigDecimal profit = MathUtils.getPriceDiff(buyPrice, sellPrice, 3);
			System.out.println(String.format("%03d-Buy: %s[%s] ; Sell: %s[%s] = %s[%s]", tradeCount, 
					buyPrice.toString(), stockPriceList.get(buyTradeIndex).getTradeDate(),
					sellPrice.toString(), stockPriceList.get(sellTradeIndex).getTradeDate(), 
					profit, (sellTradeIndex-tradeIndex) ));

			totalProfit = totalProfit.add(profit);
		}
		System.out.println(String.format("Trade Count: %s ; Total Profit: %s", tradeCount, totalProfit.toString()));
		
		return;
		*/
	}
	
	private List<StockTradeSignal> getList(List<List<StockTradeSignal>> lists) {
		List<StockTradeSignal> results = new ArrayList<StockTradeSignal>();
		for(List<StockTradeSignal> result : lists) {
			results.addAll(result);
		}
		return results;
	}
	
	private List<StockTradeSignal> getStockTradeSignalCombinationList(List<List<StockTradeSignal>> stockTradeSignalList, Integer startTradeIndex, Integer type) throws Exception {
		List<StockTradeSignal> results = new ArrayList<StockTradeSignal>();
		Map<String, List<StockTradeSignal>> map = new HashMap<String,List<StockTradeSignal>>();
		int size = stockTradeSignalList.size();
		
		for(int i=0; i<stockTradeSignalList.size(); i++) {
			map.put(String.format("%02d", i), stockTradeSignalList.get(i));
		}
		
		for(int comb=2; comb<=size; comb++) {
			Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(size, comb);
			while (iterator.hasNext()) {
				int[] combination = iterator.next();
				String key = this.getKey(combination, comb);
				List<StockTradeSignal> signal1 = map.get(this.getKey(combination, comb-1));
				List<StockTradeSignal> signal2 = map.get(String.format("%02d", combination[comb-1]));
				if( signal1 != null && !signal1.isEmpty() && signal2 != null && !signal2.isEmpty() ) {
					for(StockTradeSignal s1 : signal1) {
						if (s1.getTradeIndexList().stream().anyMatch(i -> i >= startTradeIndex)) {
							for(StockTradeSignal s2 : signal2) {
								if( s2.getTradeIndexList().stream().anyMatch(i -> i >= startTradeIndex)) {
									StockTradeSignal stockTradeSignal = new StockTradeSignal(s1, s2);
									if( stockTradeSignal.getTradeIndexList().stream().anyMatch(i -> i >= startTradeIndex)) {
										if( stockTradeSignal.isMeetMediumCriteria(type)) {
							        		if( !map.containsKey(key) ) {
							        			map.put(key, new ArrayList<StockTradeSignal>());
							        		}
							        		map.get(key).add(stockTradeSignal);
							        	}
									}
								}
							}
						}
					}
				}
			}
		}
		for(List<StockTradeSignal> list : map.values()) {
			results.addAll(list);
		}
		return results;
	}
	
	private String getKey(int[] combination, int size) {
		String key = "";
		for(int i=0; i<size; i++) {
			key += String.format("%02d", combination[i]);
		}
		return key;
	}
		
	private List<StockTradeSignal> getUniqueTriggerStockTradeSignal(List<StockTradeSignal> stockTradeSignalList) throws Exception {
		List<StockTradeSignal> result = new ArrayList<StockTradeSignal>();
		Map<String, List<StockTradeSignal>> stockTradeSignalMap = new HashMap<String, List<StockTradeSignal>>();
		for(StockTradeSignal stockTradeSignal : stockTradeSignalList) {
			if( stockTradeSignal.isTriggerSignal() ) {
				String key = stockTradeSignal.getTradeIndexList().toString();
				if( !stockTradeSignalMap.containsKey(key) ) {
					stockTradeSignalMap.put(key, new ArrayList<StockTradeSignal>());
				}
				stockTradeSignalMap.get(key).add(stockTradeSignal);
			}
		}
		for(List<StockTradeSignal> list : stockTradeSignalMap.values()) {
			if( list.size() >  1 ) {
				list.sort(new Comparator<StockTradeSignal>() {
					@Override
					public int compare(StockTradeSignal s1, StockTradeSignal s2) {
						if( s1.getSignalList().size() == s2.getSignalList().size() ) {
							return 0;
						} else if( s1.getSignalList().size() > s2.getSignalList().size() ) {
							return 1;
						} else {
							return -1;
						}
				     }
				});
			}
			result.add(list.get(0));
		}
		return result;
	}
	
}
