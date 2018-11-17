package com.lunstudio.stocktechnicalanalysis.init;

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
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPriceData;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;

@Component
public class InitStockPriceDataToFirebase {

	private static boolean isUpdated = false;

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
			InitStockPriceDataToFirebase instance = context.getBean(InitStockPriceDataToFirebase.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}

	private void start(String[] stockCode) throws Exception {
		if( stockCode == null ) {
			this.clearStockPriceData();
		}
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			if( stockCode != null && stockCode[0].equals(stock.getStockCode()) ) {
				this.updateToFirebase(stock);
			}
		}
		return;
	}
		
	private void updateToFirebase(StockEntity stock) throws Exception {
		InitStockPriceDataToFirebase.isUpdated = false;
		String stockCode = stock.getStockCode();
		
		List<StockPriceData> stockPriceList = this.stockPriceSrv.getFirbaseStockPriceDataList(stockCode, null);
		
		Map<String, Object> stockPriceDataeMap = new HashMap<String, Object>();
		
		int startIndex = stockPriceList.size() - 2500;
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
		    	InitStockPriceDataToFirebase.isUpdated = true;
		    	if (databaseError != null) {
		    		logger.info("Other Data could not be saved " + databaseError.getMessage());
		        } else {
		        	logger.info(stockCode + " Data saved successfully.");
		        }
		    }
		});
		while( !InitStockPriceDataToFirebase.isUpdated ) {
			Thread.sleep(1000);
		}
		return;	
	}
	
	private void clearStockPriceData() throws Exception {
		InitStockPriceDataToFirebase.isUpdated = false;
		FirebaseDao.getInstance().getRootRef().child("StockPriceData").setValue("", new DatabaseReference.CompletionListener() {
		    @Override
		    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
		    	InitStockPriceDataToFirebase.isUpdated = true;
		    	if (databaseError != null) {
		            System.out.println("StockPrice Data could not be clear " + databaseError.getMessage());
		        } else {
		            System.out.println("StockPrice Data clear successfully.");
		        }
		    }
		});
		while( !InitStockPriceDataToFirebase.isUpdated ) {
			Thread.sleep(1000);
		}
	}
}
