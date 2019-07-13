package com.lunstudio.stocktechnicalanalysis.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.SystemMessageSrv;

@Component
public class GenerateWeeklyStockPrice {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private SystemMessageSrv systemMessageSrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GenerateWeeklyStockPrice instance = context.getBean(GenerateWeeklyStockPrice.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start() throws Exception {
		logger.info("Generate weekly stock price");
		List<StockPriceEntity> weeklyStockPriceList = new ArrayList<StockPriceEntity>();
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			StockPriceEntity latestWeeklyStockPrice = this.stockPriceSrv.getLatestWeeklyStockPriceEntity(stock.getStockCode());
			List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getDailyStockPriceList(stock.getStockCode(), latestWeeklyStockPrice.getTradeDate());
			weeklyStockPriceList.addAll(this.stockPriceSrv.getWeeklyStockPriceEntityList(dailyStockPriceList));
		}
		logger.info(String.format("No. of records : %s", weeklyStockPriceList.size()));
		/*
		for(StockPriceEntity stockPrice : weeklyStockPriceList) {
			logger.info(stockPrice);
		}
		*/
		this.stockPriceSrv.saveStockPrice(weeklyStockPriceList);
		return;
	}

}
