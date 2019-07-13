package com.lunstudio.stocktechnicalanalysis.temp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns.BullishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.init.InitCandlestick;
import com.lunstudio.stocktechnicalanalysis.service.CandleStickSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;

@Component
public class GenerateCandlestickStat {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private CandleStickSrv candleStickSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	private final static Integer STOPLOSS = 0;
	private final static Integer TARGET = 1;
	private final static Integer HOLD = 2;
	
	public static void main(String[] args) {
		try {
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(configPath);
			GenerateCandlestickStat instance = context.getBean(GenerateCandlestickStat.class);
			instance.start(args);
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start(String[] args) throws Exception {
		this.stimulateStockTrade();
		return;
	}

	private void stimulateStockTrade() throws Exception {
		List<TradeVo> tradeList = new ArrayList<TradeVo>();
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(int technical=0; technical<=14; technical++) {
			for(StockEntity stock : stockList) {
				List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), null);
				this.stockPriceSrv.generateDailyMacdTechnicalIndicator(stock.getStockCode(), stockPriceList, 12, 26, 9);
				this.stockPriceSrv.generateDailyMovingAverageTechnicalIndicator(stock.getStockCode(), stockPriceList, 20, 50, 250);
				this.stockPriceSrv.generateDailyRsiTechnicalIndicator(stock.getStockCode(), stockPriceList, 5, 14);
	
				List<CandlestickEntity> candlestickList = this.candleStickSrv.getCandlestickList(stock.getStockCode(), CandlestickEntity.DAILY);
				for(int i=0; i<BullishPatterns.values().length; i++) {			
					Map<Date, CandlestickEntity> candlestickMap = this.getBullishCandlestickMap(candlestickList, i);
					tradeList.addAll(this.stimulateStockTrade(stockPriceList, candlestickMap, BigDecimal.valueOf(0.1), 10, technical));
				}
			}
		}
		this.getProfitCandlestick(tradeList);
		return;
	}
		
	private List<TradeVo> stimulateStockTrade(List<StockPriceEntity> stockPriceList, Map<Date, CandlestickEntity> candlestickMap, BigDecimal target, int day, int technical) throws Exception {
		List<TradeVo> tradeList = new ArrayList<TradeVo>();
		TradeVo trade = null;
		CandlestickEntity candlestick = null;
		int tradeDate = 0;
		for(int i=1; i<stockPriceList.size()-1; i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			StockPriceEntity prevStockPrice = null;
			if( candlestick != null ) {
				tradeDate++;
				if( trade == null ) {
					if( stockPrice.getClosePrice().compareTo(candlestick.getConfirmPrice()) > 0 ) {
						boolean flag = true;
						switch(technical) {
						case 0:
							flag = true;
							break;
						case 1:
							flag = stockPrice.getDailyMacdHistogram().compareTo(BigDecimal.ZERO) > 0;
							break;
						case 2:
							flag = stockPrice.getDailyMacdHistogram().compareTo(BigDecimal.ZERO) < 0;
							break;
						case 3:
							flag = stockPrice.getDailyMacdSignal().compareTo(BigDecimal.ZERO) > 0;
							break;
						case 4:
							flag = stockPrice.getDailyMacdSignal().compareTo(BigDecimal.ZERO) < 0;
							break;
						case 5:
							flag = stockPrice.getDailyMacd().compareTo(BigDecimal.ZERO) > 0;
							break;
						case 6:
							flag = stockPrice.getDailyMacd().compareTo(BigDecimal.ZERO) < 0;
							break;
						case 7:
							flag = stockPrice.getDailyShortRsi().compareTo(stockPrice.getDailyLongRsi()) > 0;
							break;
						case 8:
							flag = stockPrice.getDailyShortRsi().compareTo(stockPrice.getDailyLongRsi()) < 0;
							break;
						case 9:
							flag = stockPrice.getDailyShortSma().compareTo(stockPrice.getDailyMediumSma()) > 0 && stockPrice.getDailyMediumSma().compareTo(stockPrice.getDailyLongSma()) > 0;
							break;
						case 10:
							flag = stockPrice.getDailyMediumSma().compareTo(stockPrice.getDailyShortSma()) > 0 && stockPrice.getDailyShortSma().compareTo(stockPrice.getDailyLongSma()) > 0;
							break;
						case 11:
							flag = stockPrice.getDailyShortSma().compareTo(stockPrice.getDailyMediumSma()) > 0 && stockPrice.getDailyLongSma().compareTo(stockPrice.getDailyShortSma()) > 0;
							break;
						case 12:
							flag = stockPrice.getDailyMediumSma().compareTo(stockPrice.getDailyShortSma()) > 0 && stockPrice.getDailyLongSma().compareTo(stockPrice.getDailyMediumSma()) > 0;
							break;
						case 13:
							prevStockPrice = stockPriceList.get(i-1);
							flag = stockPrice.getDailyMacdHistogram().compareTo(prevStockPrice.getDailyMacdHistogram()) > 0;
							break;
						case 14:
							prevStockPrice = stockPriceList.get(i-1);
							flag = stockPrice.getDailyMacdHistogram().compareTo(prevStockPrice.getDailyMacdHistogram()) < 0;
							break;
						default:
							flag = true;
							break;
						}
						if( flag ) {
							StockPriceEntity nextDayStockPrice = stockPriceList.get(i+1);
							if( nextDayStockPrice.getDayHigh().compareTo(candlestick.getConfirmPrice()) >= 0 ) {
								trade = new TradeVo();
								trade.buyStockprice = nextDayStockPrice; 
								trade.buyCandlestick = candlestick;
								trade.technical = technical;
								tradeDate = 0;
							}
						}
					} else {
						if( tradeDate > 5 ) {
							candlestick = null;
						}
					}
				} else {
					if( stockPrice.getClosePrice().compareTo(candlestick.getStoplossPrice()) < 0 ) {
						trade.sellStockprice = stockPriceList.get(i+1);
						trade.sellCandlestick = candlestick;
						trade.sellType = STOPLOSS;
					} else if( this.getProfitPrecentage(trade.getBuyPrice(), stockPrice.getClosePrice()).compareTo(target) >= 0 ) {
						trade.sellStockprice = stockPriceList.get(i+1);
						trade.sellCandlestick = candlestick;
						trade.sellType = TARGET;
					} else if( tradeDate > day ) {
						trade.sellStockprice = stockPrice;
						trade.sellCandlestick = candlestick;
						trade.sellType = HOLD;
					}
				}
			}
			if( trade != null && trade.buyStockprice != null && trade.sellStockprice != null ) {
				tradeList.add(trade);
				trade = new TradeVo();
				trade = null;
				candlestick = null;
			}
			if( candlestickMap.containsKey(stockPrice.getTradeDate()) ) {
				candlestick = candlestickMap.get(stockPrice.getTradeDate());
				tradeDate = 0;
			}
			
		}
		return tradeList;
	}
	
	private Map<Date, CandlestickEntity> getBullishCandlestickMap(List<CandlestickEntity> candlestickList, Integer pattern) throws Exception {
		Map<Date, CandlestickEntity> candlestickMap = new HashMap<Date, CandlestickEntity>();
		for(CandlestickEntity candlestick : candlestickList) {
			if( candlestick.getCandlestickType() == pattern && CandlestickEntity.Buy.equals(candlestick.getType()) ) {
				candlestickMap.put(candlestick.getTradeDate(), candlestick);
			}
		}
		return candlestickMap;
	}
	
	private BigDecimal getProfitPrecentage(BigDecimal buy, BigDecimal sell) {
		BigDecimal sub = sell.subtract(buy);
		BigDecimal profit = sub.divide(buy, 5, RoundingMode.HALF_UP);
		//System.out.println(String.format("Buy:%s , Sell:%s , Diff:%s , Proft:%s", buy, sell, sub, profit));
		return profit;
	}
	
	private class TradeVo {
		public CandlestickEntity buyCandlestick;
		public StockPriceEntity buyStockprice;
		public CandlestickEntity sellCandlestick;
		public StockPriceEntity sellStockprice;
		public int sellType;
		public int technical;
		
		public BigDecimal getProfit() {
			return (this.getSellPrice().subtract(this.getBuyPrice())).divide(this.getBuyPrice(), 5, RoundingMode.HALF_UP);
		}
		
		public Date getBuyDate() {
			return this.buyStockprice.getTradeDate();
		}
		
		public BigDecimal getBuyPrice() {
			if( this.buyStockprice.getOpenPrice().compareTo(this.buyCandlestick.getConfirmPrice()) >=0 ) {
				return this.buyStockprice.getOpenPrice();
			} else {
				return this.buyCandlestick.getConfirmPrice();
			}
		}
		
		public Date getSellDate() {
			return this.sellStockprice.getTradeDate();
		}
		
		public BigDecimal getSellPrice() {
			return this.sellStockprice.getOpenPrice();
		}
	}
	
	public void getProfitCandlestick(List<TradeVo> tradeList) {
		for(int i=0; i<BullishPatterns.values().length; i++) {
			for(int technical=0; technical<=14; technical++) {
			DescriptiveStatistics stats = new DescriptiveStatistics();
			Frequency freq = new Frequency();
			for(TradeVo trade : tradeList) {
				if( trade.buyCandlestick.getCandlestickType() == i && trade.technical == technical) {
					/*
					if( !trade.buyCandlestick.getStockCode().equals("HKG:0388")) {
						continue;
					}
					*/
					BigDecimal tradeProfit = trade.getProfit();
					/*
					if( tradeProfit.doubleValue() > 0.7 || tradeProfit.doubleValue() < -0.6 ) {
					System.out.println(String.format("[%s]%s Candlestick Date: %s [$%s - $%s], Buy Date:%s $%s , Sell Date:%s $%s , Profit:%s", 
							trade.sellType, trade.buyStockprice.getStockCode(), trade.buyCandlestick.getTradeDate(), trade.buyCandlestick.getConfirmPrice(), trade.buyCandlestick.getStoplossPrice(),
							trade.buyStockprice.getTradeDate(), trade.getBuyPrice(),
							trade.sellStockprice.getTradeDate(), trade.getSellPrice(), tradeProfit));
					}
					*/
					/*
					totalCount++;
					if( tradeProfit.compareTo(BigDecimal.ZERO) > 0 ) {
						profitCount++;
					} else {
						lossCount++;
					}
					profit = profit.add(tradeProfit);
					*/
					stats.addValue(tradeProfit.doubleValue());
					freq.addValue(tradeProfit);
				}
			}
			logger.info(String.format("[%s] Total Profit: %.3f, Total Count: %s, Profit Count: %.3f, Mean: %.3f, Median: %.3f, Max: %.3f, Min: %.3f", 
					BullishCandlestickPatterns.getBullishCandlestickPatternDesc(i), stats.getSum(), stats.getN(), (1-freq.getCumPct(BigDecimal.ZERO)), 
					stats.getMean(), stats.getPercentile(50), stats.getMax(), stats.getMin()));
			}
		}
	}
	
}

