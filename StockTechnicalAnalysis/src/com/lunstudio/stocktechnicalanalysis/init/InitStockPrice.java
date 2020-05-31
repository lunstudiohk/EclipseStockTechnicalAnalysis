package com.lunstudio.stocktechnicalanalysis.init;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.HttpUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;

@Component
public class InitStockPrice {

	private static final Logger logger = LogManager.getLogger();

	private static final Date StockPriceInitDate = Date.valueOf("2008-01-01");

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			InitStockPrice instance = context.getBean(InitStockPrice.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return;
	}

	private void start(String[] args) throws Exception {
		if( args[0].equals("D") ) {
			this.initDailyStockPrice();
		} else if( args[0].equals("W") ) {
			this.initWeeklyStockPrice();
		}
		return;
	}
	
	private void initDailyStockPrice() throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			/*
			if( "INDEXHANGSENG:HSI".equals(stock.getStockCode()) ) {
				this.getStockDailyHistorialPrice(stock);
			} else {
				continue;
			}
			*/
			try {
				this.getStockDailyHistorialPriceFromStooq(stock);
				Thread.sleep(3000);
			} catch(Exception e) {
				logger.error(e.getMessage());
			}
			
			/*
			StockPriceEntity stockPrice = this.stockPriceSrv.getLatestDailyStockPriceEntity(stock.getStockCode());
			if( stockPrice == null ) {
				try {
					long startTime = System.currentTimeMillis();
					this.getStockDailyHistorialPrice(stock);
					Thread.sleep(1000);
					}catch(Exception e) {
					logger.error(e.getMessage());
				}
			}
			*/
		}
		return;
	}
	
	
	private void initWeeklyStockPrice() throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			StockPriceEntity stockPrice = this.stockPriceSrv.getLatestWeeklyStockPriceEntity(stock.getStockCode());
			if( stockPrice == null ) {
				try {
					long startTime = System.currentTimeMillis();
					this.getStockWeeklyHistorialPrice(stock);
					while(System.currentTimeMillis()-startTime < 20000 ) {
						Thread.sleep(1000);
					}
				}catch(Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
		return;
	}
	
	private void getStockDailyHistorialPriceFromStooq(StockEntity stock) throws Exception {
		logger.info(String.format("Get Stock Daily Price: %s-%s", stock.getStockCode(), stock.getStockName()));
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		List<String> contentList = HttpUtils.getStooqStockPriceListCsv(stock, StockPriceInitDate, new Date(System.currentTimeMillis()));
		if( contentList.size() > 1 ) {
			for(int i=1; i<contentList.size(); i++) {
				StockPriceEntity stockPrice = StockPriceEntity.getStooqStockPriceEntity(stock.getStockCode(), contentList.get(i));
				if( stockPrice != null ) {
					stockPriceList.add(stockPrice);
				}
			}
		}
		logger.info("No. of records created: " + stockPriceList.size());
		this.stockPriceSrv.saveStockPrice(stockPriceList);
		return;
	}
	
	//AlphaVantage
	private void getStockDailyHistorialPrice(StockEntity stock) throws Exception {
		logger.info(String.format("Get Stock Daily Price: %s-%s", stock.getStockCode(), stock.getStockCname()));
		String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesDailyUrl(), stock.getStockYahooCode(), "full"));	//compact or full
		//String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesDailyUrl(), stock.getStockYahooCode(), "compact"));
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
		JSONObject daily = (JSONObject)jsonObject.get("Time Series (Daily)");
		for(Object key : daily.keySet() ) {
			StockPriceEntity stockPrice = new StockPriceEntity(stock.getStockCode(), (String) key, StockPriceEntity.PRICE_TYPE_DAILY, (JSONObject) daily.get(key));
			if( stockPrice.getTradeDate().compareTo(StockPriceInitDate) >= 0 ) {
				stockPriceList.add(stockPrice);
			}
		}
		logger.info("No. of records created: " + stockPriceList.size());
		this.stockPriceSrv.saveStockPrice(stockPriceList);
		return;
	}
	
	private void getStockWeeklyHistorialPrice(StockEntity stock) throws Exception {
		logger.info(String.format("Get Stock Weekly Price: %s-%s", stock.getStockCode(), stock.getStockCname()));
		String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesWeeklyUrl(), stock.getStockYahooCode(), "full"));	//compact
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
		JSONObject weekly = (JSONObject)jsonObject.get("Weekly Time Series");
		for(Object key : weekly.keySet() ) {
			Date tradeDate = Date.valueOf(key.toString());
			StockPriceEntity stockPrice = new StockPriceEntity(stock.getStockCode(), tradeDate, StockPriceEntity.PRICE_TYPE_WEEKLY, (JSONObject) weekly.get(key));
			if( stockPrice.getTradeDate().compareTo(StockPriceInitDate) >= 0 ) {
				if( stockPrice.getStockCode().startsWith("HKG:") ) {
					if( stockPrice.getDayVolume() != null && stockPrice.getDayVolume().compareTo(BigDecimal.ZERO) > 0 ) {
						stockPriceList.add(stockPrice);
					}
				} else {
					stockPriceList.add(stockPrice);
				}
			}
		}
		logger.info("No. of records created: " + stockPriceList.size());
		this.stockPriceSrv.saveStockPrice(stockPriceList);
		return;
	}
}
