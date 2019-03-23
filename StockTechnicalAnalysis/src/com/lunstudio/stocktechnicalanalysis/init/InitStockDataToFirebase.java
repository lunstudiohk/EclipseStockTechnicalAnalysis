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
import com.lunstudio.stocktechnicalanalysis.firebase.StockData;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPriceData;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;

@Component
public class InitStockDataToFirebase {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private FirebaseSrv firebaseSrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			InitStockDataToFirebase instance = context.getBean(InitStockDataToFirebase.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}

	private void start() throws Exception {
		this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getStockDataRef(), "");
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		Map<String, Object> stockDataMap = new HashMap<String, Object>();
		for(StockEntity stock : stockList) {
			stockDataMap.put(stock.getStockCode(), new StockData(stock));
		}
		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockDataRef(), stockDataMap);
		return;
	}

}
