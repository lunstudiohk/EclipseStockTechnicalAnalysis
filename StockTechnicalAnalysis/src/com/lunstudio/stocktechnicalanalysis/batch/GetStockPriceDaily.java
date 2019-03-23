package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.HttpUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;


/**
 * Get latest stock price from google csv
 * @author alankam
 *
 */
@Component
public class GetStockPriceDaily {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetStockPriceDaily instance = context.getBean(GetStockPriceDaily.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start() throws Exception {
		logger.info("Get Stock Price Start");
		String csvData = HttpUtils.sendGet(SystemUtils.getGoogleStockPriceUrl());
		//String csvData = HttpUtils.sendGet(SystemUtils.getGoogleStockDatePriceUrl());
		List<StockPriceEntity> stockPriceEntityList = this.getLatestStockPriceFromGoogle(csvData);
		//System.out.println(stockPriceEntityList);
		this.stockPriceSrv.saveStockPrice(stockPriceEntityList);
		logger.info("Number of Stock Price  : " + stockPriceEntityList.size());
		logger.info("Get Stock Price End");
		return;
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
