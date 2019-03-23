package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

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
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetStockPrice instance = context.getBean(GetStockPrice.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start() throws Exception {
		logger.info("Get Stock Price Start");
		//String csvData = HttpUtils.sendGet(SystemUtils.getGoogleStockPriceUrl());
		//String csvData = HttpUtils.sendGet(SystemUtils.getGoogleStockDatePriceUrl());
		//List<StockPriceEntity> stockPriceEntityList = this.getLatestStockPriceFromGoogle(csvData);
		List<StockPriceEntity> stockPriceEntityList = this.getLatestStockPrice();
		//System.out.println(stockPriceEntityList);
		this.stockPriceSrv.saveStockPrice(stockPriceEntityList);
		logger.info("Number of Stock Price  : " + stockPriceEntityList.size());
		logger.info("Get Stock Price End");
		return;
	}
	
	public List<StockPriceEntity> getLatestStockPrice() throws Exception {
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			try {
				StockPriceEntity stockPrice1 = this.getLatestStockPriceFromWtd(stock);
				StockPriceEntity stockPrice2 = this.getLatestStockPriceFromAv(stock);
				if( stockPrice1 != null && stockPrice2 != null ) {
					if( stockPrice1.isSame(stockPrice2) ) {
						stockPriceList.add(stockPrice1);	
					} else {
						StringBuffer buf = new StringBuffer();
						if( stockPrice1.getOpenPrice().compareTo(stockPrice2.getOpenPrice()) != 0 ) {
							buf.append(String.format("Open:%s(%s), ", stockPrice1.getOpenPrice(), stockPrice2.getOpenPrice()));
						}
						if( stockPrice1.getClosePrice().compareTo(stockPrice2.getClosePrice()) != 0 ) {
							buf.append(String.format("Close:%s(%s) ", stockPrice1.getClosePrice(), stockPrice2.getClosePrice()));
						}
						if( stockPrice1.getDayHigh().compareTo(stockPrice2.getDayHigh()) != 0 ) {
							buf.append(String.format("High:%s(%s) ", stockPrice1.getDayHigh(), stockPrice2.getDayHigh()));
						}
						if( stockPrice1.getDayLow().compareTo(stockPrice2.getDayLow()) != 0 ) {
							buf.append(String.format("Low:%s(%s) ", stockPrice1.getDayLow(), stockPrice2.getDayLow()));
						}
						if( stockPrice1.getDayVolume().compareTo(stockPrice2.getDayVolume()) != 0 ) {
							buf.append(String.format("Volume:%s(%s)", stockPrice1.getDayVolume(), stockPrice2.getDayVolume()));
						}
						this.systemMessageSrv.saveSystemWarningMessage(String.format("%s - %s", stock.getStockCode(), buf.toString()));
						stockPriceList.add(stockPrice2);
					}
				} else if( stockPrice1 != null && stockPrice2 == null ) {
					stockPriceList.add(stockPrice1);
				} else if( stockPrice1 == null && stockPrice2 != null ) {
					stockPriceList.add(stockPrice2);
				} else {
					this.systemMessageSrv.saveSystemErrorMessage(String.format("%s - %s", stock.getStockCode(), "Stock Price not found!"));
				}
			}catch(Exception e) {
				logger.error(e.getMessage());
			}
			Thread.sleep(15000);
			//break;
		}
		return stockPriceList;
	}
	
	public StockPriceEntity getLatestStockPriceFromWtd(StockEntity stock) throws Exception {
		StockPriceEntity stockPrice = null;
		try {
			String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getLatestStockPriceUrl(), stock.getStockYahooCode()));
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
			JSONArray jsonArray = (JSONArray)jsonObject.get("data");
			JSONObject jsonEntry = (JSONObject) jsonArray.get(0);
			stockPrice = new StockPriceEntity();
			stockPrice.setStockCode(stock.getStockCode());
			stockPrice.setPriceType(StockPriceEntity.PRICE_TYPE_DAILY);
			stockPrice.setTradeDate(Date.valueOf(((String)jsonEntry.get("last_trade_time")).substring(0, 10)));
			stockPrice.setClosePrice(new BigDecimal((String)jsonEntry.get("price")));
			stockPrice.setOpenPrice(new BigDecimal((String)jsonEntry.get("price_open")));
			stockPrice.setDayHigh(new BigDecimal((String)jsonEntry.get("day_high")));
			stockPrice.setDayLow(new BigDecimal((String)jsonEntry.get("day_low")));
			stockPrice.setDayVolume(new BigDecimal((String)jsonEntry.get("volume")));
		} catch(Exception e) {
			logger.error("Failed to process " + stock.getStockCode() + " : " + e.getMessage());
			return null;
		}
		return stockPrice;
	}
	
	public StockPriceEntity getLatestStockPriceFromAv(StockEntity stock) throws Exception {
		StockPriceEntity stockPrice = null;
		try {
			String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesDailyUrl(), stock.getStockYahooCode(), "compact"));
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
			JSONObject metaData = (JSONObject)jsonObject.get("Meta Data");
			String latestDate = (String) metaData.get("3. Last Refreshed");
			JSONObject stockPriceJson = (JSONObject)jsonObject.get("Time Series (Daily)");
			JSONObject stockPriceData = (JSONObject) stockPriceJson.get(latestDate);
			stockPrice = new StockPriceEntity();
			stockPrice.setStockCode(stock.getStockCode());
			stockPrice.setTradeDate(Date.valueOf(latestDate));
			stockPrice.setPriceType(StockPriceEntity.PRICE_TYPE_DAILY);
			stockPrice.setOpenPrice(new BigDecimal((String)stockPriceData.get("1. open")));
			stockPrice.setDayHigh(new BigDecimal((String)stockPriceData.get("2. high")));
			stockPrice.setDayLow(new BigDecimal((String)stockPriceData.get("3. low")));
			stockPrice.setClosePrice(new BigDecimal((String)stockPriceData.get("4. close")));
			stockPrice.setDayVolume(new BigDecimal((String)stockPriceData.get("5. volume")));
		} catch(Exception e) {
			logger.error("Failed to process " + stock.getStockCode() + " : " + e.getMessage());
			return null;
		}
		return stockPrice;
	}
	
	
	//https://query1.finance.yahoo.com/v7/finance/download/0003.HK?period1=1547994951&period2=1550673351&interval=1d&events=history&crumb=u5GCenFLYpD
	public List<StockPriceEntity> getLatestStockPriceFromGoogle(String csvData) {
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		try{
			BufferedReader reader = new BufferedReader(new StringReader(csvData));
			String line = reader.readLine();
			line = reader.readLine();	//Skip First Line (Header)
			while( line != null ) {
				boolean isDataCorrect = true;
				String[] val = line.split(",");
				String stockCode = val[1];
				Date tradeDate = DateUtils.getGoogleDateString(val[3]);
				StockPriceEntity model = this.stockPriceSrv.getDailyStockPrice(stockCode, tradeDate);
				if( model == null ) {
					logger.info("Stock Code: " + stockCode + " - New stock price create");
					model = new StockPriceEntity();
				} else {
					logger.info("Stock Code: " + stockCode + " - Stock price update");
				}
				model.setStockCode(stockCode);
				model.setTradeDate(tradeDate);
				model.setPriceType(StockPriceEntity.PRICE_TYPE_DAILY);
				try{
					model.setClosePrice(new BigDecimal(val[4]));
				}catch(Exception e) {
					
				}
				try{
					model.setOpenPrice(new BigDecimal(val[5]));
				}catch(Exception e) {
					if("#N/A".equals(val[5])) {
						model.setOpenPrice(new BigDecimal(val[4]));
					}
				}
				try{
					model.setDayHigh(new BigDecimal(val[6]));
				}catch(Exception e) {
					if("#N/A".equals(val[6])) {
						model.setDayHigh(new BigDecimal(val[4]));
					}
				}
				try{
					model.setDayLow(new BigDecimal(val[7]));
				}catch(Exception e) {
					if("#N/A".equals(val[7])) {
						model.setDayLow(new BigDecimal(val[4]));
					}
				}
				try{
					model.setDayVolume(new BigDecimal(val[8]));
				}catch(Exception e) {
					if("#N/A".equals(val[8])) {
						model.setDayVolume(new BigDecimal(val[8]));
					}
				}
				if( isDataCorrect && !stockPriceList.contains(model) ) {
					stockPriceList.add(model);
				}
				line = reader.readLine();
			}
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		return stockPriceList;
	}	

}
