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
import com.lunstudio.stocktechnicalanalysis.signal.BearishSignal;
import com.lunstudio.stocktechnicalanalysis.signal.BullishSignal;
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

	private static final int HISTORICAL_SIZE = 2500;
	
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
		Map<Date,BigDecimal> refPriceDateMap = this.getRefPriceDateMap();
		
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
			//logger.info(String.format("Processing: %s", stock.getStockCode()));
			this.generateSignalStat(refPriceDateMap, stock);
			//this.generateSellSignalStat(stock);
		}
		return;
	}
	
	private void generateSignalStat(Map<Date,BigDecimal> refPriceDateMap, StockEntity stock) throws Exception {
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), HISTORICAL_SIZE);
		//List<StockPriceEntity> weeklyStockPriceList = this.stockPriceSrv.getLastWeeklyStockPriceEntityList(stock.getStockCode(), null);
        List<StockPriceVo> stockPriceVoList = this.stockPriceSrv.getStockPriceVoList(stock, dailyStockPriceList, new ArrayList<StockPriceEntity>() /*weeklyStockPriceList*/);
        List<CandlestickEntity> candlestickList = this.candlestickSrv.getCandlestickListByType(stock.getStockCode(), CandlestickEntity.Buy);
        BullishSignal.generateBullishSignal(stock, refPriceDateMap, stockPriceVoList, candlestickList, StockPriceEntity.PRICE_TYPE_DAILY);
        candlestickList = this.candlestickSrv.getCandlestickListByType(stock.getStockCode(), CandlestickEntity.Sell);
        BearishSignal.generateBearishSignal(stock, refPriceDateMap, stockPriceVoList, candlestickList, StockPriceEntity.PRICE_TYPE_DAILY);
		return;
	}
	
	private Map<Date,BigDecimal> getRefPriceDateMap() throws Exception {
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(StockSrv.INDEXHANGSENGHSI, 100);
		return this.stockPriceSrv.getStockClosePriceDateMap(dailyStockPriceList);
	}
	
}
