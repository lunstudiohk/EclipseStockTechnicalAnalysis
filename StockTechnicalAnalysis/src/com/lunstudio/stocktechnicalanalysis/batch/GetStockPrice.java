package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
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
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
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
	
	private final static int FULL_SIZE = -7300;	//365 x 20yrs
	
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
/*			
if( !stock.getStockCode().equals("700-HK") ) {
	continue;
}
*/
			if( "ALL".equals(stockRegion) || stockRegion.equals(stock.getStockRegion()) ) {
				try {
					/*
					List<StockPriceEntity> stockPriceAvList = this.getLatestStockPriceListFromAv(stock, size);
					this.systemMessageSrv.saveSystemInfoMessage(String.format("[%s] - %s to %s = %s", 
							stock.getStockCode(), stockPriceAvList.get(0).getTradeDate(), stockPriceAvList.get(stockPriceAvList.size()-1).getTradeDate(), stockPriceAvList.size()));
					this.stockPriceSrv.saveStockPrice(stockPriceAvList);
					*/
					List<StockPriceEntity> stockPriceCnbcList = this.getLatestStockPriceListFromCnbc(stock, size);
					this.systemMessageSrv.saveSystemInfoMessage(String.format("[%s] - %s to %s = %s", 
							stock.getStockCode(), stockPriceCnbcList.get(0).getTradeDate(), stockPriceCnbcList.get(stockPriceCnbcList.size()-1).getTradeDate(), stockPriceCnbcList.size()));
					this.stockPriceSrv.saveStockPrice(stockPriceCnbcList);
				} catch(Exception e) {
					e.printStackTrace();
				}
				Thread.sleep(10000);
			}
		}
		return;
	}
	
	
	public List<StockPriceEntity> getLatestStockPriceListFromCnbc(StockEntity stock, int size) throws Exception {
		logger.info(String.format("Get Stock Daily Price From Cnbc: %s-%s %s", stock.getStockCode(), stock.getStockCname(), stock.getStockEname()));
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		String jsonData = null;
		//20000601000000
		String startDate = "";
		Date today = new Date(System.currentTimeMillis());
		String endDate = DateUtils.getCnbcDateEndString(today);
		if( size == 0 ) {
			Date date = DateUtils.addDays(today, FULL_SIZE);
			startDate = DateUtils.getCnbcDateString(date);
		} else {
			StockPriceEntity stockPrice = this.stockPriceSrv.getLatestDailyStockPriceEntity(stock.getStockCode());
			if( stockPrice != null ) {
				Date date = DateUtils.addDays(stockPrice.getTradeDate(), size);
				startDate = DateUtils.getCnbcDateString(date);	
			} else {
				Date date = DateUtils.addDays(today, FULL_SIZE);
				startDate = DateUtils.getCnbcDateString(date);
			}
			
		}
		jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getCnbcStockPriceUrl(), stock.getStockCode(), startDate, endDate));
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
		JSONObject barData = (JSONObject)jsonObject.get("barData");
		JSONArray array = (JSONArray) barData.get("priceBars");
		for(Object data : array) {
			StockPriceEntity stockPrice = new StockPriceEntity(stock.getStockCode(), StockPriceEntity.PRICE_TYPE_DAILY, (JSONObject) data);
			if( stockPrice.getOpenPrice().compareTo(BigDecimal.ZERO) <= 0 || stockPrice.getClosePrice().compareTo(BigDecimal.ZERO) <= 0 
					|| stockPrice.getHighPrice().compareTo(BigDecimal.ZERO) <= 0 || stockPrice.getLowPrice().compareTo(BigDecimal.ZERO) <= 0 ) {
				continue;
			}
			stockPriceList.add(stockPrice);
		}
		return stockPriceList;
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
