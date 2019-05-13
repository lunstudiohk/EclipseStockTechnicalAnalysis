package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
public class GetIndexVolume {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
		
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetIndexVolume instance = context.getBean(GetIndexVolume.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start() throws Exception {
		logger.info("Update Index Volume Start");
		List<StockPriceEntity> stockPriceEntityList = this.getIndexVolume(50);
		this.stockPriceSrv.saveStockPrice(stockPriceEntityList);
		logger.info("Update Index Volume End");
		return;
	}

	public List<StockPriceEntity> getIndexVolume(int size) throws Exception {
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		
		List<StockPriceEntity> hsiList = this.stockPriceSrv.getLastDailyStockPriceEntityList("INDEXHANGSENG:HSI", size);
		List<StockPriceEntity> hsceiList = this.stockPriceSrv.getLastDailyStockPriceEntityList("INDEXHANGSENG:HSCEI", size);
		
		StockEntity hsiStock = this.stockSrv.getStockInfo("INDEXHANGSENG:HSI");
		StockEntity hsceiStock = this.stockSrv.getStockInfo("INDEXHANGSENG:HSCEI");
		
		stockPriceList.addAll(this.getStockPriceFromAv(hsiStock, hsiList));
		stockPriceList.addAll(this.getStockPriceFromAv(hsceiStock, hsceiList));
		
		return stockPriceList;
	}
	
	public List<StockPriceEntity> getStockPriceFromAv(StockEntity stock, List<StockPriceEntity> stockPriceList) throws Exception {
		try {
			String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesDailyUrl(), stock.getStockYahooCode(), "compact"));
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
			JSONObject stockPriceJson = (JSONObject)jsonObject.get("Time Series (Daily)");
			for(StockPriceEntity stockPrice : stockPriceList) {
				String tradeDate = stockPrice.getTradeDate().toString();
				JSONObject stockPriceData = (JSONObject) stockPriceJson.get(tradeDate);
				stockPrice.setDayVolume(new BigDecimal((String)stockPriceData.get("5. volume")));
			}
		} catch(Exception e) {
			logger.error("Failed to process " + stock.getStockCode() + " : " + e.getMessage());
			return null;
		}
		return stockPriceList;
	}
	
}
