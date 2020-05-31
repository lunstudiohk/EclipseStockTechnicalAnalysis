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
		Map<Date,BigDecimal> refPriceDateMap = null;	//this.getRefPriceDateMap();
		
		for(StockEntity stock : stockList) {
			if( !stock.getStockCode().equals("0700.HK") ) {
				continue;
			}
			if( stock.getStockRegion().equals(args[0]) ) {
				this.generateSignalStat(refPriceDateMap, stock, Integer.parseInt(args[1]));
			}
		}
		//Comment for Debug
        this.stockSignalSrv.updateIncompletedStockSignal();
		return;
	}
	
	private void generateSignalStat(Map<Date,BigDecimal> refPriceDateMap, StockEntity stock, Integer size) throws Exception {
		System.out.println(String.format("Processing stock: %s - %s", stock.getStockCname(), stock.getStockCode()));
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), HISTORICAL_SIZE);
		if( dailyStockPriceList == null || dailyStockPriceList.isEmpty() ) {
			return;
		}
		//List<StockPriceEntity> weeklyStockPriceList = this.stockPriceSrv.getLastWeeklyStockPriceEntityList(stock.getStockCode(), null);
        List<StockPriceVo> stockPriceVoList = this.stockPriceSrv.getStockPriceVoList(stock, dailyStockPriceList, new ArrayList<StockPriceEntity>() /*weeklyStockPriceList*/);
        List<CandlestickEntity> bullCandlestickList = this.candlestickSrv.getCandlestickListByType(stock.getStockCode(), CandlestickEntity.Buy);
        List<CandlestickEntity> bearCandlestickList = this.candlestickSrv.getCandlestickListByType(stock.getStockCode(), CandlestickEntity.Sell);
        
        List<StockSignalEntity> todaySignalList = new ArrayList<StockSignalEntity>();
        
        //Generate the latest day
        /*
    	todaySignalList.addAll(BullishSignal.generateBullishSignal(stock, refPriceDateMap, stockPriceVoList, bullCandlestickList, StockPriceEntity.PRICE_TYPE_DAILY));
    	todaySignalList.addAll(BearishSignal.generateBearishSignal(stock, refPriceDateMap, stockPriceVoList, bearCandlestickList, StockPriceEntity.PRICE_TYPE_DAILY));   	
    	this.stockSignalSrv.saveStockSignalList(todaySignalList);
    	for(StockSignalEntity stockSignal : todaySignalList) {
   			this.stockSignalDateSrv.saveStockSignalDateList(stockSignal.getStockSignalDateList());
        }
    	*/
        
    	/* Generate for last $size days*/
    	if( size > 1 ) {
	        for(int i=0; i<size && stockPriceVoList.size() > 20; i++) {
	        	//stockPriceVoList.remove(stockPriceVoList.size()-1);
	        	todaySignalList = new ArrayList<StockSignalEntity>();
	        	todaySignalList.addAll(BullishSignal.generateBullishSignal(stock, refPriceDateMap, stockPriceVoList.subList(0, stockPriceVoList.size()-i), bullCandlestickList, StockPriceEntity.PRICE_TYPE_DAILY));
	        	todaySignalList.addAll(BearishSignal.generateBearishSignal(stock, refPriceDateMap, stockPriceVoList.subList(0, stockPriceVoList.size()-i), bearCandlestickList, StockPriceEntity.PRICE_TYPE_DAILY));
	        	//Comment for Debug
	        	this.stockSignalSrv.saveStockSignalList(todaySignalList);	
	        	for(StockSignalEntity stockSignal : todaySignalList) {
	        		//Comment for Debug
	        		this.stockSignalDateSrv.saveStockSignalDateList(stockSignal.getStockSignalDateList());
	        	}
	        }
    	}
		return;
	}
	
	/*
	private Map<Date,BigDecimal> getRefPriceDateMap() throws Exception {
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(StockSrv.INDEXHANGSENGHSI, 100);
		return this.stockPriceSrv.getStockClosePriceDateMap(dailyStockPriceList);
	}
	*/
}
