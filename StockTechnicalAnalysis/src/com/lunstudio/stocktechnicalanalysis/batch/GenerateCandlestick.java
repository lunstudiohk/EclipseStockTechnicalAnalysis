package com.lunstudio.stocktechnicalanalysis.batch;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.CandleStickSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;

@Component
public class GenerateCandlestick {

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
			GenerateCandlestick instance = context.getBean(GenerateCandlestick.class);
			instance.start(args);
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start(String[] args) throws Exception {
		this.generateCandlestick(args[0], Integer.parseInt(args[1]));
		return;
	}
	
	private void generateCandlestick(String stockRegion, Integer size) throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {

			
if( !"AMZN".equals(stock.getStockCode()) ) {
	continue;
}
			
			if( "ALL".equals(stockRegion) || stock.getStockRegion().equals(stockRegion) ) {
				logger.info(String.format("Generate candlestick for %s - %s %s", stock.getStockCode(), stock.getStockCname(), stock.getStockEname()));
				
				List<StockPriceEntity> stockPriceList = null;
				if( size == 0 ) {
					stockPriceList = this.stockPriceSrv.getDailyStockPriceList(stock.getStockCode(), null);
				} else {
					stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), size + CandleStickSrv.CANDLESTICK_MAXSIZE);
				}
				
				if( stockPriceList.size() > CandleStickSrv.CANDLESTICK_MAXSIZE ) {
					
//					this.candleStickSrv.deleteCandleStick(stock.getStockCode(),stockPriceList.get(CandleStickSrv.CANDLESTICK_MAXSIZE).getTradeDate(), StockPriceEntity.PRICE_TYPE_DAILY);

					List<CandlestickEntity> bullishCandlestickList = this.candleStickSrv.generateBullishCandleStick(stockPriceList);
					logger.info(String.format("%s - Bullish Candle Stick Count: %s", stock.getStockCode(), bullishCandlestickList.size()));
					//this.candleStickSrv.saveCandlestickList(bullishCandlestickList);
					
					/*
					List<CandlestickEntity> bearishCandlestickList = this.candleStickSrv.generateBearishCandleStick(stockPriceList);
					logger.info(String.format("%s - Bearish Candle Stick Count: %s", stock.getStockCode(), bearishCandlestickList.size()));
					*/
					//this.candleStickSrv.saveCandlestickList(bearishCandlestickList);
				
for(CandlestickEntity candlestick : bullishCandlestickList) {
	System.out.println(candlestick);
}

				}
			}
			//return;
		}
		return;
	}
	
	
}
