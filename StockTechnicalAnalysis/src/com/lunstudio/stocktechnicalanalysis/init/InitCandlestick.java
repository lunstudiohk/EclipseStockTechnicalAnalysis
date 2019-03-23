package com.lunstudio.stocktechnicalanalysis.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.CandleStickSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;

@Component
public class InitCandlestick {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private CandleStickSrv candleStickSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	public static void main(String[] args) {
		try {
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(configPath);
			InitCandlestick instance = context.getBean(InitCandlestick.class);
			instance.start(args);
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start(String[] args) throws Exception {
		if( args != null && args.length == 1) {
			this.generateCandlestick(Integer.parseInt(args[0]));
		} else {
			this.initCandlestick();
		}
		//this.stimulateStockTrade("HKG:0011");
		return;
	}
	
	private void generateCandlestick(Integer size) throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			//if( !"HKG:0700".equals(stock.getStockCode()) ) continue;
			List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), size);
			if( stockPriceList.size() > 2 ) {
				this.candleStickSrv.deleteCandleStick(stock.getStockCode(),stockPriceList.get(2).getTradeDate());
			}
			List<CandlestickEntity> bullishCandlestickList = this.candleStickSrv.generateBullishCandleStick(stockPriceList);
			logger.info(String.format("%s - Bullish Candle Stick Count: %s", stock.getStockCode(), bullishCandlestickList.size()));
			this.candleStickSrv.saveCandlestickList(bullishCandlestickList);
			
			List<CandlestickEntity> bearishCandlestickList = this.candleStickSrv.generateBearishCandleStick(stockPriceList);
			logger.info(String.format("%s - Bearish Candle Stick Count: %s", stock.getStockCode(), bearishCandlestickList.size()));
			this.candleStickSrv.saveCandlestickList(bearishCandlestickList);
		}
		return;
	}
	
	private void initCandlestick() throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			//if( !"HKG:0388".equals(stock.getStockCode()) && !"HKG:2318".equals(stock.getStockCode()) && !"HKG:1299".equals(stock.getStockCode()) ) continue;
			//if( !"HKG:0700".equals(stock.getStockCode()) ) continue;
			List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), null);
			
			//List<CandlestickEntity> candlestickList = new ArrayList<CandlestickEntity>();
			
			List<CandlestickEntity> bullishCandlestickList = this.candleStickSrv.generateBullishCandleStick(stockPriceList);
			logger.info(String.format("%s - Bullish Candle Stick Count: %s", stock.getStockCode(), bullishCandlestickList.size()));
			this.candleStickSrv.saveCandlestickList(bullishCandlestickList);
			
			List<CandlestickEntity> bearishCandlestickList = this.candleStickSrv.generateBearishCandleStick(stockPriceList);
			logger.info(String.format("%s - Bearish Candle Stick Count: %s", stock.getStockCode(), bearishCandlestickList.size()));
			this.candleStickSrv.saveCandlestickList(bearishCandlestickList);
			
			/*
			candlestickList.addAll(bullishCandlestickList);
			candlestickList.addAll(bearishCandlestickList);
			Collections.sort(candlestickList,new Comparator<CandlestickEntity>(){
				@Override
				public int compare(CandlestickEntity first, CandlestickEntity second) {
					return first.getTradeDate().compareTo(second.getTradeDate());
				}
			});
			for(CandlestickEntity entity : candlestickList) {
				if( entity.getType().equals("B") ) {
					logger.info(String.format("%s - %s : ", entity.getStockCode(), BullishCandlestickPatterns.getBullishCandlestickPatternDesc(entity.getCandlestickType())) + entity);
				} else {
					logger.info(String.format("%s - %s : ", entity.getStockCode(), BearishCandlestickPatterns.getBearishCandlestickPatternDesc(entity.getCandlestickType())) + entity);
				}
			}
			*/
		}
		return;
	}
	
	
	
}
