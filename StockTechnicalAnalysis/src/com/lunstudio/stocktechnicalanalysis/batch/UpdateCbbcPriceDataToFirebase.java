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

import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.CbbcPriceData;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.service.CbbcSrv;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;

@Component
public class UpdateCbbcPriceDataToFirebase {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private CbbcSrv cbbcSrv;

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
			UpdateCbbcPriceDataToFirebase instance = context.getBean(UpdateCbbcPriceDataToFirebase.class);
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
			col = this.updateCbbcPriceDataToFirebase(Date.valueOf(args[0]));
		} else {
			StockPriceEntity stockPrice = this.stockPriceSrv.getLatestDailyStockPriceEntity("INDEXHANGSENG:HSI");
			col = this.updateCbbcPriceDataToFirebase(stockPrice.getTradeDate());
		}
		Map<String, Map<String, Object>> cbbcStockDataMap = new HashMap<String, Map<String, Object>>();
		for(Object obj : col) {
			CbbcPriceData cbbcPriceData = (CbbcPriceData) obj;
			String stockCode = cbbcPriceData.getSc();
			Map<String, Object> cbbcDataMap = cbbcStockDataMap.get(stockCode);
			if( cbbcDataMap == null ) {
				cbbcDataMap = new HashMap<String, Object>();
				cbbcStockDataMap.put(stockCode, cbbcDataMap);
			}
			cbbcPriceData.setSc(null);
			cbbcPriceData.setT(null);
			cbbcDataMap.put(cbbcPriceData.getCc(), obj);
		}
		this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getCbbcPriceSummaryRef(), null);
		for(String stockCode : cbbcStockDataMap.keySet()) {
			Map<String, Object> cbbcDataMap = cbbcStockDataMap.get(stockCode);
			this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getCbbcPriceSummaryRef().child(stockCode), cbbcDataMap);	
		}
		
		return;
	}

	private Collection<Object> updateCbbcPriceDataToFirebase(Date tradeDate) throws Exception {
		logger.info(String.format("Update Cbbc date on Trade Date: %s", DateUtils.getFirebaseDateString(tradeDate)));
		List<CbbcPriceEntity> cbbcPriceList = this.cbbcSrv.getCbbcPriceList(tradeDate);
		//Date previousTradeDate = this.stockPriceSrv.getPreviousDailyStockPriceEntity("INDEXHANGSENG:HSI", tradeDate).getTradeDate();

		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getDailyStockPriceList(tradeDate);
		//Map<String, StockEntity> stockCodeMap = this.stockSrv.getStockInfoMap();
		Map<String, StockPriceEntity> stockPriceMap = new HashMap<String, StockPriceEntity>();
		//Map<String, List<CbbcPriceEntity>> cbbcPriceListMap = new HashMap<String, List<CbbcPriceEntity>>();
		for(StockPriceEntity stockPrice : stockPriceList) {
			stockPriceMap.put(stockPrice.getStockCode(), stockPrice);
		}
		Map<String, Object> cbbcDataMap = new HashMap<String, Object>();
		for(CbbcPriceEntity cbbcPrice : cbbcPriceList) {
			CbbcPriceData cbbcData = new CbbcPriceData(cbbcPrice, stockPriceMap.get(cbbcPrice.getCbbcUnderlying()));
			String key = String.format("%s%s", cbbcData.getCc(), cbbcData.getT());
			cbbcDataMap.put(key, cbbcData);
		}
		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getCbbcPriceDataRef(), cbbcDataMap);
		return cbbcDataMap.values();
	}

	
	
}
