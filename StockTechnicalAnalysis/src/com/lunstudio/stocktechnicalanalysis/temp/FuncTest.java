package com.lunstudio.stocktechnicalanalysis.temp;

import java.sql.Date;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockTechnicalAnalysisSrv;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockResultVo;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SmoothedRSIIndicator;

@Component
public class FuncTest {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockTechnicalAnalysisSrv stockTechnicalAnalysisSrv;
	
	@Autowired
	private StockSrv stockSrv;

	@Autowired
	private StockPriceSrv stockPriceSrv;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			FuncTest instance = context.getBean(FuncTest.class);
			instance.start3();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	
	private void start() throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			if( stock.getStockCode().equals("HKG:0700") ) {
				logger.info(String.format("Processing stock: %s - %s", stock.getStockCode(), stock.getStockCname()));
				List<StockResultVo> stockResultList = this.stockTechnicalAnalysisSrv.getDailyRsiResult(stock);
				//List<StockResultVo> stockResultList = this.stockTechnicalAnalysisSrv.getDailyCandlestickResult(stock);
				for(StockResultVo stockResult: stockResultList) {
					stockResult.generateTradeStat(10, 5);
				}
			}
			//break;
		}
		return;
	}
	
	private void start2() throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			if( !stock.getStockCode().contentEquals("INDEXHANGSENG:HSI") ) {
				continue;
			}
			System.out.println(stock.getStockCode() + " : " + stock.getStockCname());
		List<StockPriceEntity> dailyStockPriceList = this.stockPriceSrv.getDailyStockPriceList(stock.getStockCode(), Date.valueOf("2019-03-01"));
		TimeSeries dailySeries = this.stockPriceSrv.getStockTimeSeries(stock.getStockCode(), dailyStockPriceList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
        SmoothedRSIIndicator rsi = new SmoothedRSIIndicator(dailyClosePrice, 5);
		SMAIndicator sma = new SMAIndicator(dailyClosePrice, 20);
		double currentSlope = 0;
		int size = 3; 
		int currentIndex = 0;
		for(int i=20; i<dailyStockPriceList.size()-size; i++) {
			SimpleRegression regression = new SimpleRegression();
			for(int j=1; j<=size; j++) {
				System.out.println(dailyStockPriceList.get(i+j).getTradeDate() + " : " + sma.getValue(i+j).toDouble());
				regression.addData(j, sma.getValue(i+j).toDouble());
				currentIndex = i+j;
			}
			double slope = regression.getSlope(); //1000*(regression.getSlope()/sma.getValue(i+size-1).toDouble());
			System.out.println(" ===== " + dailyStockPriceList.get(currentIndex).getTradeDate() + " : " + slope + " , " + rsi.getValue(currentIndex).toDouble());
			
			/*
			if( slope < 1.0 && slope > -1.0 ) {
				continue;
			}
			*/
			//System.out.println(String.format("%s - %.2f : %.2f", dailyStockPriceList.get(i+4).getTradeDate(), dailyStockPriceList.get(i+4).getClosePrice().doubleValue(), 1000*(regression.getSlope()/sma.getValue(i+4).toDouble())));
			
			//if( (currentSlope > 0 && slope < 0 && rsi.getValue(i+size-1).toDouble() < 50) || (currentSlope < 0 && slope > 0 && rsi.getValue(i+size-1).toDouble() > 50 ) ) {
				/*
				System.out.println(String.format("%s - %.2f : %.2f , RSI: %.1f", 
						dailyStockPriceList.get(i+size-1).getTradeDate(), 
						dailyStockPriceList.get(i+size-1).getClosePrice().doubleValue(), 
						1000*(regression.getSlope()/sma.getValue(i+size-1).toDouble()),
						rsi.getValue(i+size-1).toDouble()
						));
						*/	
			//}
			currentSlope = slope;
			}
		}
		return;
	}
	
	
	private void start3() throws Exception {
		SimpleRegression regression = new SimpleRegression();
		regression.addData(1, 1);
		regression.addData(2, 2);
		regression.addData(3, 3);
		regression.addData(4, 4);
		regression.addData(5, 5);
		System.out.println(regression.getSlope());
	}
	
}
