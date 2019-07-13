package com.lunstudio.stocktechnicalanalysis.init;

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

@Component
public class InitWeeklyStockPrice {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			InitWeeklyStockPrice instance = context.getBean(InitWeeklyStockPrice.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return;
	}

	private void start() throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			logger.info(String.format("Processing Stock: %s-%s", stock.getStockCode(), stock.getStockCname()));
			List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), null);
			List<StockPriceEntity> weeklyStockPriceList = this.stockPriceSrv.getWeeklyStockPriceEntityList(dailyStockPriceList);
			/*
			int size = weeklyStockPriceList.size();
			for(int i=size-5; i<size; i++) {
				System.out.println(weeklyStockPriceList.get(i));
			}
			*/
			logger.info(String.format("Weekly Stock Price Record: %s ", weeklyStockPriceList.size()));
			
			this.stockPriceSrv.saveStockPrice(weeklyStockPriceList);
			//break;
		}
		return;
	}
	
	
	
}
