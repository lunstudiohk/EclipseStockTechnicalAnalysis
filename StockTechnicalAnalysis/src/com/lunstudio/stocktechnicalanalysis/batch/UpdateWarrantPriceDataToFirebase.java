package com.lunstudio.stocktechnicalanalysis.batch;

import java.sql.Date;
import java.util.Collection;
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
import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.firebase.WarrantPriceData;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.WarrantSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;

@Component
public class UpdateWarrantPriceDataToFirebase {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private WarrantSrv warrantSrv;

	@Autowired
	private FirebaseSrv firebaseSrv;
	
	/**
	 * Trade Date : yyyy-mm-dd
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			UpdateWarrantPriceDataToFirebase instance = context.getBean(UpdateWarrantPriceDataToFirebase.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}

	
	private void start(String[] args) throws Exception {
		Collection<Object> col = null;
		if( args != null && args.length > 0 ) {
			col = this.updateWarrantPriceDataToFirebase(Date.valueOf(args[0]));
		} else {
			StockPriceEntity stockPrice = this.stockPriceSrv.getLatestDailyStockPriceEntity("INDEXHANGSENG:HSI");
			col = this.updateWarrantPriceDataToFirebase(stockPrice.getTradeDate());
		}
		Map<String, Map<String, Object>> warrantStockDataMap = new HashMap<String, Map<String, Object>>();
		for(Object obj : col) {
			WarrantPriceData warrantPriceData = (WarrantPriceData) obj;
			String stockCode = warrantPriceData.getSc();
			Map<String, Object> warrantDataMap = warrantStockDataMap.get(stockCode);
			if( warrantDataMap == null ) {
				warrantDataMap = new HashMap<String, Object>();
				warrantStockDataMap.put(stockCode, warrantDataMap);
			}
			warrantPriceData.setSc(null);
			warrantPriceData.setT(null);
			warrantDataMap.put(warrantPriceData.getWc(), obj);
		}
		this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getWarrantPriceSummaryRef(), null);
		for(String stockCode : warrantStockDataMap.keySet()) {
			Map<String, Object> warrantDataMap = warrantStockDataMap.get(stockCode);
			this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getWarrantPriceSummaryRef().child(stockCode), warrantDataMap);	
		}
		return;
	}

	private Collection<Object> updateWarrantPriceDataToFirebase(Date tradeDate) throws Exception {
		logger.info(String.format("Update Warrant date on Trade Date: %s", DateUtils.getFirebaseDateString(tradeDate)));
		List<WarrantPriceEntity> warrantPriceList = this.warrantSrv.getWarrantPriceList(tradeDate);
		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getDailyStockPriceList(tradeDate);
		Map<String, StockEntity> stockCodeMap = this.stockSrv.getStockInfoMap();
		Map<String, StockPriceEntity> stockPriceMap = new HashMap<String, StockPriceEntity>();
		for(StockPriceEntity stockPrice : stockPriceList) {
			stockPriceMap.put(stockCodeMap.get(stockPrice.getStockCode()).getStockHkexCode(), stockPrice);
		}
		Map<String, Object> warrantDataMap = new HashMap<String, Object>();
		for(WarrantPriceEntity warrantPrice : warrantPriceList) {
			//if( warrantPrice.getWarrantValue() != null ) {
				WarrantPriceData warrantData = new WarrantPriceData(warrantPrice, stockPriceMap.get(warrantPrice.getWarrantUnderlying()));
				String key = String.format("%s%s", warrantData.getWc(), warrantData.getT());
				warrantDataMap.put(key, warrantData);
			//}
		}
		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getWarrantPriceDataRef(), warrantDataMap);
		return warrantDataMap.values();
	}

	
}
