package com.lunstudio.stocktechnicalanalysis.batch;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.firebase.database.DataSnapshot;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPrice;
import com.lunstudio.stocktechnicalanalysis.firebase.StockSignal;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSignalDateSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSignalSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;

@Component
public class UpdateStockSignalToFirebase {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private StockSignalSrv stockSignalSrv;
	
	@Autowired
	private StockSignalDateSrv stockSignalDateSrv;
	
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
			UpdateStockSignalToFirebase instance = context.getBean(UpdateStockSignalToFirebase.class);
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
				this.updateStockSignal(parameter[1], Integer.parseInt(parameter[2]));
				break;
			case HOUSEKEEP:
				this.housekeepStockSignal(parameter[1], Integer.parseInt(parameter[2]));
				break;
			case CLEAR:
				this.clearStockSignal(parameter[1]);
				break;					
		}
		return;
	}
	
	private void updateStockSignal(String stockCode, Integer size) throws Exception {
		logger.info("Update stock signal: " + stockCode);
		if( ALL.equals(stockCode) ) {
			this.updateToFirebase(null, size);
		} else {
			StockEntity stock = this.stockSrv.getStockInfo(stockCode);
			this.updateToFirebase(stock, size);
		}
		return;
	}
	
	private void updateToFirebase(StockEntity stock, Integer size) throws Exception {
		List<StockSignalEntity> stockSignalList = null;
		if( stock != null && size != null ) {
			List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), size);
			Date startDate = stockPriceList.get(0).getTradeDate();
			stockSignalList = this.stockSignalSrv.getStockSignalListAfter(stock.getStockCode(), startDate);
		} else if( stock == null && size != null ) {
			List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(StockSrv.INDEXHANGSENGHSCEI, size);
			Date startDate = stockPriceList.get(0).getTradeDate();
			stockSignalList = this.stockSignalSrv.getStockSignalListAfter(null, startDate);
		} else if( stock != null && size == null ) {
			stockSignalList = this.stockSignalSrv.getStockSignalList(stock.getStockCode());
		} else {
			stockSignalList = this.stockSignalSrv.getAllStockSignalList();
		}
		
		Map<String, Object> stockSignalMap = new HashMap<String, Object>();
		for(StockSignalEntity entity : stockSignalList) {
			//entity.setStockSignalDateList(this.stockSignalDateSrv.getStockSignalDateList(entity.getStockCode(), entity.getTradeDate(), entity.getSignalSeq(), entity.getSignalType(), StockSignalDateEntity.ASC));
			String dateStr = DateUtils.getShortDateString(entity.getTradeDate());
			String key = String.format("%s%s", entity.getStockCode(), dateStr);
		
			if( !stockSignalMap.containsKey(key) ) {
				stockSignalMap.put(key, new StockSignal(entity.getStockCode(), entity.getTradeDate()));
			} 
			((StockSignal) stockSignalMap.get(key)).addSignal(entity);
		}
		
		for( String key : stockSignalMap.keySet() ) {
			stockSignalMap.put(key, ((StockSignal) stockSignalMap.get(key)).getData());
		}
		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockSignalRef(), stockSignalMap);
		return;
	}
	
	private void housekeepStockSignal(String stockCode, int size) throws Exception {
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
	
	private void clearStockSignal(String stockCode) throws Exception {
		logger.info("Clear stock signal: " + stockCode);
		if( ALL.equals(stockCode) ) {
			this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getStockSignalRef(), "");
		} else {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<DataSnapshot> dataSnapshot = this.firebaseSrv.getFromFirebase(FirebaseDao.getInstance().getStockPriceRef().orderByChild("stock").equalTo(stockCode));
			for(DataSnapshot data : dataSnapshot) {
				dataMap.put(data.getKey(), null);
			}
			this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockSignalRef(), dataMap);
		}
		return;
	}
}
