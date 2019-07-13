package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
 * 
 * @author alankam
 *
 */
@Component
public class GetWeeklyStockPrice {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private SystemMessageSrv systemMessageSrv;
	
	private static final Integer RECORDSIZE = 3;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetWeeklyStockPrice instance = context.getBean(GetWeeklyStockPrice.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start() throws Exception {
		logger.info("Get Weekly Stock Price Start");
		this.getLatestWeeklyStockPrice();
		logger.info("Get Weekly Stock Price End");
		return;
	}
	
	public void getLatestWeeklyStockPrice() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date startDate = new Date(cal.getTime().getTime());
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			try {
				if( !stock.getStockCode().contentEquals("HKG:2888")) {
					continue;
				}
				List<StockPriceEntity> stockPriceList = this.getStockWeeklyHistorialPrice(stock, startDate);
				this.stockPriceSrv.saveStockPrice(stockPriceList);
				Thread.sleep(20000);
			} catch(Exception e) {
				logger.error(e);
				this.systemMessageSrv.saveSystemErrorMessage(String.format("[%s-%s] Get weekly stock price failed: %s", stock.getStockCode(), stock.getStockCode(), e.getMessage()));
				Thread.sleep(20000);
			}
		}
		return;
	}
	
	private List<StockPriceEntity> getStockWeeklyHistorialPrice(StockEntity stock, Date startDate) throws Exception {
		logger.info(String.format("Get Stock Weekly Price: %s-%s", stock.getStockCode(), stock.getStockCname()));
		String jsonData = HttpUtils.getInstance().sendHttpsGet(String.format(SystemUtils.getTimeSeriesWeeklyUrl(), stock.getStockYahooCode(), "compact"));	//compact
		List<StockPriceEntity> stockPriceList = new ArrayList<StockPriceEntity>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
		JSONObject weekly = (JSONObject)jsonObject.get("Weekly Time Series");
		for(Object key : weekly.keySet() ) {
			Date tradeDate = Date.valueOf(key.toString());
			if( tradeDate.compareTo(startDate) >= 0 ) {
				StockPriceEntity stockPrice = new StockPriceEntity(stock.getStockCode(), tradeDate, StockPriceEntity.PRICE_TYPE_WEEKLY, (JSONObject) weekly.get(key));
				if( stockPrice.getStockCode().startsWith("HKG:") ) {
					if( stockPrice.getDayVolume() != null && stockPrice.getDayVolume().compareTo(BigDecimal.ZERO) > 0 ) {
						stockPriceList.add(stockPrice);
					}
				} else {
					stockPriceList.add(stockPrice);
				}
			}
		}
		//Collections.sort(stockPriceList, new StockPriceTradeDateComparator());
		return stockPriceList;
	}

}
