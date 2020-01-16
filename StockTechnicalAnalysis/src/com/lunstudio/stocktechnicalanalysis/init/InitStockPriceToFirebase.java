package com.lunstudio.stocktechnicalanalysis.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.firebase.database.DataSnapshot;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPrice;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;

@Component
public class InitStockPriceToFirebase {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	@Autowired
	private FirebaseSrv firebaseSrv;
	
	private static final String ALL = "ALL";
	private static final String UPDATE = "U";
	private static final String HOUSEKEEP = "H";
	private static final String CLEAR = "C";
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			InitStockPriceToFirebase instance = context.getBean(InitStockPriceToFirebase.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}

	private void start(String[] parameter) throws Exception {
		switch(parameter[0]) {
				case UPDATE:
					this.updateStockPrice(parameter[1], Integer.parseInt(parameter[2]));
					break;
				case HOUSEKEEP:
					this.housekeepStockPrice(parameter[1], Integer.parseInt(parameter[2]));
					break;
				case CLEAR:
					this.clearStockPrice(parameter[1]);
					break;					
		}
		return;
	}
	
	private void updateStockPrice(String stockCode, int size) throws Exception {
		logger.info("Update stock price: " + stockCode);
		if( ALL.equals(stockCode) ) {
			List<StockEntity> stockList = this.stockSrv.getStockInfoList();
			for(StockEntity stock : stockList) {
				this.updateToFirebase(stock, size);
			}
		} else {
			StockEntity stock = this.stockSrv.getStockInfo(stockCode);
			this.updateToFirebase(stock, size);
		}
		return;
	}
	
	private void housekeepStockPrice(String stockCode, int size) throws Exception {
		logger.info("Housekeep stock price: " + stockCode);
		if( ALL.equals(stockCode) ) {
			List<StockEntity> stockList = this.stockSrv.getStockInfoList();
			for(StockEntity stock : stockList) {
				this.housekeepOnFirebase(stock.getStockCode(), size);
			}
		} else {
			this.housekeepOnFirebase(stockCode, size);
		}
		return;
	}
	
	private void housekeepOnFirebase(String stockCode, int size) throws Exception {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<DataSnapshot> dataSnapshot = this.firebaseSrv.getFromFirebase(FirebaseDao.getInstance().getStockPriceRef().orderByChild("stock").equalTo(stockCode));
		List<String> keyList = new ArrayList<String>();
		for(DataSnapshot data : dataSnapshot) {
			keyList.add(data.getKey());
		}
		dataSnapshot = null;	//Release memory
	    Collections.sort(keyList, Collections.reverseOrder());         

		for(int i=size; i<keyList.size(); i++) {
			dataMap.put(keyList.get(i), null);
		}
		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockPriceRef(), dataMap);
		return;
	}
	
	
	private void updateToFirebase(StockEntity stock, int size) throws Exception {
		String stockCode = stock.getStockCode();
		logger.info(String.format("Processing on stock: %s", stockCode));
		List<StockPrice> stockPriceList = this.stockPriceSrv.getFirbaseStockPriceList(stockCode, size+250);
		Map<String, Object> stockPriceMap = new HashMap<String, Object>();
		int startIndex = stockPriceList.size() - size;
		if( startIndex < 0 ) {
			startIndex = 0;
		}
		int endIndex = stockPriceList.size();
		for(int i=startIndex; i<endIndex; i++) {
			StockPrice stockPrice = stockPriceList.get(i);
			String key = String.format("%s%s", stock.getStockShortCode(), DateUtils.getShortDateString(stockPrice.getDate()));
			stockPriceMap.put(key, stockPrice.getData());
		}
		logger.info(String.format("No. of records to be updated: %s", stockPriceMap.size()));
		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockPriceRef(), stockPriceMap);
		return;
	}
	
	private void clearStockPrice(String stockCode) throws Exception {
		logger.info("Clear stock price: " + stockCode);
		if( ALL.equals(stockCode) ) {
			this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getStockPriceRef(), "");
		} else {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<DataSnapshot> dataSnapshot = this.firebaseSrv.getFromFirebase(FirebaseDao.getInstance().getStockPriceRef().orderByChild("stock").equalTo(stockCode));
			for(DataSnapshot data : dataSnapshot) {
				dataMap.put(data.getKey(), null);
			}
			this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockPriceRef(), dataMap);
		}
		return;
	}
}
