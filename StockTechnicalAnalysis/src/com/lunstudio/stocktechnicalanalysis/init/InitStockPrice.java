package com.lunstudio.stocktechnicalanalysis.init;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
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
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			if( "INDEXHANGSENG:HSI".equals(stock.getStockCode()) ) {
				this.getStockDailyHistorialPrice(stock);
			} else {
				continue;
			}
			StockPriceEntity stockPrice = this.stockPriceSrv.getLatestDailyStockPriceEntity(stock.getStockCode());
			if( stockPrice == null ) {
				try {
					long startTime = System.currentTimeMillis();
					this.getStockDailyHistorialPrice(stock);
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
	
	//AlphaVantage
	private void getStockDailyHistorialPrice(StockEntity stock) throws Exception {
		logger.info(String.format("Get Stock Daily Price: %s-%s", stock.getStockCode(), stock.getStockCname()));
		//String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesDailyUrl(), stock.getStockYahooCode(), "full"));	//compact
		String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesDailyUrl(), stock.getStockYahooCode(), "compact"));
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
		JSONObject daily = (JSONObject)jsonObject.get("Time Series (Daily)");
		for(Object key : daily.keySet() ) {
			StockPriceEntity stockPrice = new StockPriceEntity(stock.getStockCode(), (String) key, StockPriceEntity.PRICE_TYPE_DAILY, (JSONObject) daily.get(key));
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
		this.stockPriceSrv.saveStockPrice(stockPriceList);
		return;
	}
	
	
	/*
	private void getStockDailyHistorialPrice(StockEntity stock) throws Exception {
		logger.info(String.format("Get Stock Daily Price: %s-%s", stock.getStockCode(), stock.getStockCname()));
		String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getHistoricalStockPriceUrl(), stock.getStockYahooCode()));	//compact
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
		JSONObject daily = (JSONObject)jsonObject.get("history");
		for(Object key : daily.keySet() ) {
			JSONObject jsonEntry = (JSONObject) daily.get(key);
			StockPriceEntity stockPrice = new StockPriceEntity();
			stockPrice.setStockCode(stock.getStockCode());
			stockPrice.setPriceType(StockPriceEntity.PRICE_TYPE_DAILY);
			stockPrice.setTradeDate(Date.valueOf(((String)key).substring(0, 10)));
			stockPrice.setClosePrice(new BigDecimal((String)jsonEntry.get("close")));
			stockPrice.setOpenPrice(new BigDecimal((String)jsonEntry.get("open")));
			stockPrice.setDayHigh(new BigDecimal((String)jsonEntry.get("high")));
			stockPrice.setDayLow(new BigDecimal((String)jsonEntry.get("low")));
			stockPrice.setDayVolume(new BigDecimal((String)jsonEntry.get("volume")));
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
		this.stockPriceSrv.saveStockPrice(stockPriceList);
		return;
	}
	*/
	
}
