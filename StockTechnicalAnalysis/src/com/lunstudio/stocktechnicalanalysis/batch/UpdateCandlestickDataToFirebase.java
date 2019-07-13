package com.lunstudio.stocktechnicalanalysis.batch;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.CandlestickData;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.service.CandleStickSrv;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;

@Component
public class UpdateCandlestickDataToFirebase {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private CandleStickSrv candleStickSrv;
	
	@Autowired
	private FirebaseSrv firebaseSrv;

	private static int UPDATE_DAYS = 25;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			UpdateCandlestickDataToFirebase instance = context.getBean(UpdateCandlestickDataToFirebase.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}

	private void start(String[] args) throws Exception {
		Integer size = UPDATE_DAYS;
		/*
		if( args != null && args.length > 0 ) {
			size = Integer.parseInt(args[0]);
		}
		*/
		List<StockPriceEntity> tradeDateList = this.stockPriceSrv.getLastDailyStockPriceEntityList("INDEXHANGSENG:HSI", size);
		Date startDate = tradeDateList.get(0).getTradeDate();
		List<CandlestickEntity> candlestickList = this.candleStickSrv.getCandlestickListFromDate(startDate, CandlestickEntity.DAILY);
		Map<String, Object> candlestickDataMap = new HashMap<String, Object>();
		for(CandlestickEntity candlestick : candlestickList) {
			String key = String.format("%s%s%s", candlestick.getStockCode().split(":")[1], DateUtils.getShortDateString(candlestick.getTradeDate()), candlestick.getCandlestickType());
			candlestickDataMap.put(key, new CandlestickData(candlestick));
		}
 		this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getCandlestickDataRef(), null);
 		this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getCandlestickDataRef(), candlestickDataMap);
 		logger.info(String.format("No.of candlestick records updated: %s", candlestickList.size()));
		return;
	}
}
