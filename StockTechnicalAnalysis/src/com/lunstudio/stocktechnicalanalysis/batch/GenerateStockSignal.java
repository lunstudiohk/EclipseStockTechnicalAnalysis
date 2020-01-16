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
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.CandleStickSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSignalDateSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSignalSrv;
import com.lunstudio.stocktechnicalanalysis.signal.BearishSignal;
import com.lunstudio.stocktechnicalanalysis.signal.BullishSignal;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockResultVo;

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
		Map<Date,BigDecimal> refPriceDateMap = this.getRefPriceDateMap();
		
		for(StockEntity stock : stockList) {
			/*
			if( !stock.getStockCode().equals("HKG:3888") 
					&& !stock.getStockCode().equals("INDEXHANGSENG:HSI") 
				) {
				continue;
			}
			*/
			this.generateSignalStat(refPriceDateMap, stock, Integer.parseInt(args[0]));
		}
        this.stockSignalSrv.updateIncompletedStockSignal();
		return;
	}
	
	private void generateSignalStat(Map<Date,BigDecimal> refPriceDateMap, StockEntity stock, Integer size) throws Exception {
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), HISTORICAL_SIZE);
		//List<StockPriceEntity> weeklyStockPriceList = this.stockPriceSrv.getLastWeeklyStockPriceEntityList(stock.getStockCode(), null);
        List<StockPriceVo> stockPriceVoList = this.stockPriceSrv.getStockPriceVoList(stock, dailyStockPriceList, new ArrayList<StockPriceEntity>() /*weeklyStockPriceList*/);
        List<CandlestickEntity> bullCandlestickList = this.candlestickSrv.getCandlestickListByType(stock.getStockCode(), CandlestickEntity.Buy);
        List<CandlestickEntity> bearCandlestickList = this.candlestickSrv.getCandlestickListByType(stock.getStockCode(), CandlestickEntity.Sell);
        
        List<StockSignalEntity> todaySignalList = new ArrayList<StockSignalEntity>();
    	todaySignalList.addAll(BullishSignal.generateBullishSignal(stock, refPriceDateMap, stockPriceVoList, bullCandlestickList, StockPriceEntity.PRICE_TYPE_DAILY));
    	todaySignalList.addAll(BearishSignal.generateBearishSignal(stock, refPriceDateMap, stockPriceVoList, bearCandlestickList, StockPriceEntity.PRICE_TYPE_DAILY));
    	this.stockSignalSrv.saveStockSignalList(todaySignalList);
    	for(StockSignalEntity stockSignal : todaySignalList) {
   			this.stockSignalDateSrv.saveStockSignalDateList(stockSignal.getStockSignalDateList());
    	}
    	
    	if( size > 1 ) {
	        for(int i=0; i<size; i++) {
	        	stockPriceVoList.remove(stockPriceVoList.size()-1);
	        	todaySignalList = new ArrayList<StockSignalEntity>();
	        	todaySignalList.addAll(BullishSignal.generateBullishSignal(stock, refPriceDateMap, stockPriceVoList, bullCandlestickList, StockPriceEntity.PRICE_TYPE_DAILY));
	        	todaySignalList.addAll(BearishSignal.generateBearishSignal(stock, refPriceDateMap, stockPriceVoList, bearCandlestickList, StockPriceEntity.PRICE_TYPE_DAILY));
	        	this.stockSignalSrv.saveStockSignalList(todaySignalList);	
	        	for(StockSignalEntity stockSignal : todaySignalList) {
	        		this.stockSignalDateSrv.saveStockSignalDateList(stockSignal.getStockSignalDateList());
	        	}
	        }
    	}
		return;
	}
	
	
	private Map<Date,BigDecimal> getRefPriceDateMap() throws Exception {
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(StockSrv.INDEXHANGSENGHSI, 100);
		return this.stockPriceSrv.getStockClosePriceDateMap(dailyStockPriceList);
	}
	
}
