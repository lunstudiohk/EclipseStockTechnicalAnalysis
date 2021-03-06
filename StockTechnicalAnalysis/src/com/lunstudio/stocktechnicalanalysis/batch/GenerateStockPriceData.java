package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.SystemMessageSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;



/**
 * Get latest stock price from google csv
 * @author alankam
 *
 */
@Component
public class GenerateStockPriceData {

	private static final Logger logger = LogManager.getLogger();
	
	private static final String ALL = "ALL";
	
	private static final int SMOOTH_RSI = 250;
	
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
			GenerateStockPriceData instance = context.getBean(GenerateStockPriceData.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	private void start(String[] args) throws Exception {
		logger.info("Generate Stock Price Date Start");
		this.generateStockPriceData(args[0], Integer.parseInt(args[1]));
		logger.info("Generate Stock Price Data End");
		return;
	}
	
	private void generateStockPriceData(String stockRegion, int size) throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {

/*
if( !"1093-HK".equals(stock.getStockCode()) ) {
	continue;
}
*/			
			if( ALL.equals(stockRegion) || stockRegion.equals(stock.getStockRegion()) ) {
				logger.info(String.format("Process Stock Price Data : %s - %s %s", stock.getStockCode(), stock.getStockCname(), stock.getStockEname()));
				List<StockPriceEntity> stockPriceList = null;
				if( size == 0 ) {
					stockPriceList = this.stockPriceSrv.getDailyStockPriceList(stock.getStockCode(), null);
				} else {
					stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), 50 + SMOOTH_RSI);
				}

				this.stockPriceSrv.generateDailyMinMaxReturn(stock.getStockCode(), stockPriceList, 10, 20, 50);
				this.stockPriceSrv.generateDailyCandlestickTechnicalIndicator(stock.getStockCode(), stockPriceList, 50);
				this.stockPriceSrv.generateDailyVolumeMovingAverageTechnicalIndicator(stock.getStockCode(), stockPriceList, 5, 10, 20);		
				this.stockPriceSrv.generateDailyMovingAverageAndBBTechnicalIndicator(stock.getStockCode(), stockPriceList, 10, 20, 50);			
				this.stockPriceSrv.generateDailyRsiTechnicalIndicator(stock.getStockCode(), stockPriceList, 5, 14);
				this.stockPriceSrv.generateDailyMacdTechnicalIndicator(stock.getStockCode(), stockPriceList, 12, 26, 9);
				this.stockPriceSrv.generateDailyPriceDiff(stockPriceList);

				if( size == 0 ) {
					this.stockPriceSrv.saveStockPrice(stockPriceList);
				} else {
					//this.stockPriceSrv.saveStockPrice(stockPriceList.subList(stockPriceList.size()-size, stockPriceList.size()));
					this.stockPriceSrv.saveStockPrice(stockPriceList.subList(stockPriceList.size()-51, stockPriceList.size()));	//Because update next 50 min/max
				}

/*
				for(StockPriceEntity stockPrice : stockPriceList) {
					System.out.println(String.format("%s - %s : 10=[%s, %s], 20=[%s, %s], 50=[%s, %s]", 
							stockPrice.getTradeDate(), stockPrice.getClosePrice(),
							stockPrice.getShortMinReturn(), stockPrice.getShortMaxReturn(),
							stockPrice.getMediumMinReturn(), stockPrice.getMediumMaxReturn(),
							stockPrice.getLongMinReturn(), stockPrice.getLongMaxReturn()
							));
				}
*/
			}
		}
		return;
	}
	



}
