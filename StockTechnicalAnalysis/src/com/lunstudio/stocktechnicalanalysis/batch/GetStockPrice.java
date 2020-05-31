package com.lunstudio.stocktechnicalanalysis.batch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.SystemMessageSrv;
import com.lunstudio.stocktechnicalanalysis.util.HttpUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;


/**
 * Get latest stock price from google csv
 * @author alankam
 *
 */
@Component
public class GetStockPrice {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private SystemMessageSrv systemMessageSrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetStockPrice instance = context.getBean(GetStockPrice.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	private void start(String[] args) throws Exception {
		logger.info("Get Stock Price Start");
		this.getLatestStockPriceAv(args[0], Integer.parseInt(args[1]));
		logger.info("Get Stock Price End");
		return;
	}
	
	public void getLatestStockPriceAv(String stockRegion, int size) throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			if( "ALL".equals(stockRegion) || stockRegion.equals(stock.getStockRegion()) ) {
				try {
					List<StockPriceEntity> stockPriceAvList = this.getLatestStockPriceListFromAv(stock, size);
					this.systemMessageSrv.saveSystemInfoMessage(String.format("[%s] - %s to %s = %s", 
							stock.getStockCode(), stockPriceAvList.get(0).getTradeDate(), stockPriceAvList.get(stockPriceAvList.size()-1).getTradeDate(), stockPriceAvList.size()));
					this.stockPriceSrv.saveStockPrice(stockPriceAvList);
				} catch(Exception e) {
					e.printStackTrace();
				}
				Thread.sleep(15000);
			}
		}
		return;
	}
	
	public List<StockPriceEntity> getLatestStockPriceListFromAv(StockEntity stock, int size) throws Exception {
		logger.info(String.format("Get Stock Daily Price: %s-%s %s", stock.getStockCode(), stock.getStockCname(), stock.getStockEname()));
		String jsonData = null;
		if( size == 0 ) {
			jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesDailyUrl(), stock.getStockCode(), "full"));	//compact or full
		} else {
			jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesDailyUrl(), stock.getStockCode(), "compact"));	//compact or full
		}
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
		JSONObject daily = (JSONObject)jsonObject.get("Time Series (Daily)");
		if( daily == null ) {
			logger.info(String.format("Failed to get stock price : %s", stock.getStockCode()));
			return new ArrayList<StockPriceEntity>();
		}
		for(Object key : daily.keySet() ) {
			stockPriceList.add(new StockPriceEntity(stock.getStockCode(), (String) key, StockPriceEntity.PRICE_TYPE_DAILY, (JSONObject) daily.get(key)));
		}
		Collections.sort(stockPriceList,new Comparator<StockPriceEntity>(){
            public int compare(StockPriceEntity s1, StockPriceEntity s2){
            	return s2.getTradeDate().compareTo(s1.getTradeDate());
            }
		});
		if( size == 0 ) {
			return stockPriceList;
		} else {
			return stockPriceList.subList(0, size);
		}
	}
	
}
