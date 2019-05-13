package com.lunstudio.stocktechnicalanalysis.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPrice;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

@Component
public class InitStockPriceToFirebase {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	@Autowired
	private FirebaseSrv firebaseSrv;
	
	private static final Integer INIT_SIZE = 500;
	
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

	private void start(String[] stockCode) throws Exception {
		if( stockCode == null || stockCode.length == 0 ) {
			this.clearStockPrice();
		}
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			if( stockCode != null && stockCode.length > 0 && stockCode[0].equals(stock.getStockCode()) ) {
				this.updateToFirebase(stock);
			} else if( stockCode == null || stockCode.length == 0 ) {
				//Filter for test
				if( !stock.getStockCode().equals("HKG:0700") && !stock.getStockCode().equals("INDEXHANGSENG:HSI") ) continue;
				this.updateToFirebase(stock);
			}
		}
		return;
	}
		
	private void updateToFirebase(StockEntity stock) throws Exception {
		logger.info("Initial Stock Price: " + stock.getStockCode());
		String stockCode = stock.getStockCode();
		
		List<StockPriceVo> stockPriceVoList = this.stockPriceSrv.getFirbaseStockPriceVoList(stockCode, 760);
		
		Map<String, Object> stockPriceMap = new HashMap<String, Object>();
		
		int startIndex = stockPriceVoList.size() - InitStockPriceToFirebase.INIT_SIZE;
		if( startIndex < 0 ) {
			startIndex = 0;
		}
		int endIndex = stockPriceVoList.size();
		logger.info("Start Date: " + stockPriceVoList.get(startIndex).getTradeDate());
		for(int i=startIndex; i<endIndex; i++) {
			StockPriceVo stockPriceVo = stockPriceVoList.get(i);
			StockPrice stockPrice = new StockPrice();
			stockPrice.setS(stockPriceVo.getStockCode());
			stockPrice.setT(stockPriceVo.getTradeDate().toString());
			//stockPrice.setPrice(stockPriceVo.getPriceData());
			stockPrice.setData(stockPriceVo.getDataList());
			String key = String.format("%s%s", stock.getStockShortCode(), DateUtils.getShortDateString(stockPriceVo.getTradeDate()));
			stockPriceMap.put(key, stockPrice);
			//this.logger.info(stockPriceVo.getDataList());
		}
		this.firebaseSrv.updateToFirebase(FirebaseDao.getInstance().getStockPriceRef(), stockPriceMap);
		return;	
	}
	
	private void clearStockPrice() throws Exception {
		this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getStockPriceRef(), "");
		return;
	}
}
