package com.lunstudio.stocktechnicalanalysis.batch;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPriceData;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;

@Component
public class UpdateStockPriceDataToFirebase {
	
	private static boolean isUpdated = false;

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	private static int UPDATE_DAYS = 10;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			UpdateStockPriceDataToFirebase instance = context.getBean(UpdateStockPriceDataToFirebase.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}

	
	private void start() throws Exception {
		this.dailyUpdate();
		return;
	}

	private void dailyUpdate() throws Exception {
		//this.clearStockPriceData();
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			this.updateToFirebase(stock);
		}
		this.updateTradeDate(UPDATE_DAYS);
		return;
	}
	
	private void updateTradeDate(Integer updateDays) throws Exception {
		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList("INDEXHANGSENG:HSI", updateDays);
		Map<String, Object> stockTradeDateMap = new HashMap<String, Object>();
		for(StockPriceEntity stockPrice : stockPriceList) {
			stockTradeDateMap.put(stockPrice.getTradeDate().toString(), stockPrice.getTradeDate().toString());
		}
		UpdateStockPriceDataToFirebase.isUpdated = false;
		FirebaseDao.getInstance().getRootRef().child("StockTradeDate").updateChildren(stockTradeDateMap, new DatabaseReference.CompletionListener() {
		    @Override
		    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
		    	UpdateStockPriceDataToFirebase.isUpdated = true;
		    	if (databaseError != null) {
		    		logger.info("Trade Date could not be saved " + databaseError.getMessage());
		        } else {
		        	logger.info(" Trade Date saved successfully.");
		        }
		    }
		});
		while( !UpdateStockPriceDataToFirebase.isUpdated ) {
			Thread.sleep(500);
		}
		return;
	
	}
	
	private void updateToFirebase(StockEntity stock) throws Exception {
		UpdateStockPriceDataToFirebase.isUpdated = false;
		String stockCode = stock.getStockCode();
		
		List<StockPriceData> stockPriceList = this.stockPriceSrv.getFirbaseStockPriceDataList(stockCode, 300);
		
		Map<String, Object> stockPriceDataeMap = new HashMap<String, Object>();
		
		int startIndex = stockPriceList.size() - UPDATE_DAYS;
		if( startIndex < 0 ) {
			startIndex = 0;
		}
		int endIndex = stockPriceList.size();
		logger.info("Start Date: " + stockPriceList.get(startIndex).getT());
		for(int i=startIndex; i<endIndex; i++) {
			StockPriceData stockPrice = stockPriceList.get(i);
			String key = String.format("%s%s", stock.getStockCode(), stockPrice.getT());
			stockPriceDataeMap.put(key, stockPrice);
		}
		
		FirebaseDao.getInstance().getRootRef().child("StockPriceData").updateChildren(stockPriceDataeMap, new DatabaseReference.CompletionListener() {
		    @Override
		    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
		    	UpdateStockPriceDataToFirebase.isUpdated = true;
		    	if (databaseError != null) {
		    		logger.info("Other Data could not be saved " + databaseError.getMessage());
		        } else {
		        	logger.info(stockCode + " Data saved successfully.");
		        }
		    }
		});
		while( !UpdateStockPriceDataToFirebase.isUpdated ) {
			Thread.sleep(1000);
		}
		return;	
	}
	
}

