package com.lunstudio.stocktechnicalanalysis.batch;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPriceData;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;

@Component
public class UpdateStockPriceDataToFirebase {
	
	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private FirebaseSrv firebaseSrv;
	
	private static int UPDATE_DAYS = 10;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			UpdateStockPriceDataToFirebase instance = context.getBean(UpdateStockPriceDataToFirebase.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}

	
	private void start(String[] args) throws Exception {
		if( args == null || args.length == 0 ) {
			this.dailyUpdate(UPDATE_DAYS);
		} else {
			this.dailyUpdate(Integer.parseInt(args[0]));
		}
		return;
	}

	private void dailyUpdate(Integer updateDays) throws Exception {
		//this.clearStockPriceData();
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		Map<String, Object> stockPriceSummaryMap = new HashMap<String, Object>();
 		for(StockEntity stock : stockList) {
			stockPriceSummaryMap.put(stock.getStockCode(), this.updateStockPriceDataToFirebase(stock, updateDays));
			//Debug 
			break;
		}
 		this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getStockPriceSummaryRef(), null);
 		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockPriceSummaryRef(), stockPriceSummaryMap);
		this.updateTradeDateToFirebase(updateDays);
		return;
	}
		
	private void updateTradeDateToFirebase(Integer updateDays) throws Exception {
		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList("INDEXHANGSENG:HSI", updateDays);
		Map<String, Object> stockTradeDateMap = new HashMap<String, Object>();
		for(StockPriceEntity stockPrice : stockPriceList) {
			stockTradeDateMap.put(stockPrice.getTradeDate().toString(), stockPrice.getTradeDate().toString());
		}
		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockTradeDateRef(), stockTradeDateMap);
		return;
	}
	
	/**
	 * Return the latest stock price data
	 * @param stock
	 * @return
	 * @throws Exception
	 */
	private StockPriceData updateStockPriceDataToFirebase(StockEntity stock, Integer updateDays) throws Exception {
		String stockCode = stock.getStockCode();
		
		List<StockPriceData> stockPriceList = this.stockPriceSrv.getFirbaseStockPriceDataList(stockCode, 300);	//300 because of 250 SMA
		//List<StockPriceData> stockPriceList = this.stockPriceSrv.getFirbaseStockPriceDataList(stockCode, 1000);	//TMP
		
		Map<String, Object> stockPriceDataMap = new HashMap<String, Object>();
		
		int startIndex = stockPriceList.size() - updateDays;
		if( startIndex < 0 ) {
			startIndex = 0;
		}
		int endIndex = stockPriceList.size();
		logger.info(String.format("%s - Start Date: %s", stock.getStockCode(), stockPriceList.get(startIndex).getT()));
		StockPriceData stockPrice = null;
		for(int i=startIndex; i<endIndex; i++) {
			stockPrice = stockPriceList.get(i);
			String key = String.format("%s%s", stock.getStockCode(), stockPrice.getT());
			stockPriceDataMap.put(key, stockPrice);
		}
		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockPriceDataRef(), stockPriceDataMap);
		return stockPrice;
	}
	
}

