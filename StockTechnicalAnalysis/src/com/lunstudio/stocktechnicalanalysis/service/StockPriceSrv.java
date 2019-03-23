package com.lunstudio.stocktechnicalanalysis.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPriceData;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SmoothedRSIIndicator;

@Service
public class StockPriceSrv {
	
	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockPriceDao stockPriceDao;

	public void saveStockPrice(List<StockPriceEntity> stockPriceEntityList) {
		this.stockPriceDao.save(stockPriceEntityList, stockPriceEntityList.size());
		return;
	}

	public StockPriceEntity getDailyStockPrice(String stockCode, Date tradeDate) {
		return this.stockPriceDao.getStockPrice(stockCode, tradeDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}
	
	public List<StockPriceEntity> getDailyStockPriceList(Date tradeDate) {
		return this.stockPriceDao.getStockPriceList(tradeDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	public StockPriceEntity getLatestDailyStockPriceEntity(String stockCode) {
		try {
			return this.getLastDailyStockPriceEntityList(stockCode, 1).get(0);
		} catch(Exception e) {
			return null;
		}
	}
	
	public StockPriceEntity getPreviousDailyStockPriceEntity(String stockCode, Date tradeDate) {
		return this.stockPriceDao.getPreviousStockPrice(stockCode, tradeDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}
	
	public List<StockPriceEntity> getLastDailyStockPriceEntityList(String stockCode, Integer size) {
		return this.stockPriceDao.getLastStockPriceList(stockCode, size, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	public List<StockPriceData> getFirbaseStockPriceDataList(String stockCode, Integer size) throws Exception {
		List<StockPriceEntity> stockPriceList = this.getLastDailyStockPriceEntityList(stockCode, size);
		List<StockPriceData> stockPriceDataList = new ArrayList<StockPriceData>();
		this.generateDailyMacdTechnicalIndicator(stockCode, stockPriceList, 12, 26, 9);
		this.generateDailyRsiTechnicalIndicator(stockCode, stockPriceList, 5, 14);
		this.generateDailyMovingAverageTechnicalIndicator(stockCode, stockPriceList, 20, 50, 250);
		
		//this.generateWeeklyMacdTechnicalIndicator(stockCode, stockPriceList, 12, 26, 9);

		for(int i=2; i<stockPriceList.size(); i++) {
			StockPriceEntity current = stockPriceList.get(i);
			StockPriceEntity previous = stockPriceList.get(i-1);
			StockPriceData stockPriceData = new StockPriceData(current);
			stockPriceData.initDetail(current);
			Double dayDiff = null;
			try {
				dayDiff = MathUtils.getPriceDiff(previous.getClosePrice(), current.getClosePrice(), 5).doubleValue();
			}catch(Exception e) {
				return null;
			}
			stockPriceData.setD(dayDiff);
			
			Double openDiff = MathUtils.getPriceDiff(previous.getClosePrice(), current.getOpenPrice(), 5).doubleValue();
			stockPriceData.setOc(openDiff);
			
			if( current.getDailyShortRsi().compareTo(current.getDailyLongRsi()) > 0 && 
					previous.getDailyShortRsi().compareTo(previous.getDailyLongRsi()) < 0 ) {
				stockPriceData.setRc(StockPriceData.UP);
			} else if( current.getDailyShortRsi().compareTo(current.getDailyLongRsi()) < 0 && 
					previous.getDailyShortRsi().compareTo(previous.getDailyLongRsi()) > 0 ) {
				stockPriceData.setRc(StockPriceData.DOWN);
			}
			
			if( current.getDailyMacdHistogram() != null && previous.getDailyMacdHistogram() != null ) {
				BigDecimal histogramDiff = current.getDailyMacdHistogram().subtract(previous.getDailyMacdHistogram());
				stockPriceData.setDhd(histogramDiff.doubleValue());
				
				StockPriceEntity first = stockPriceList.get(i-2);
				if( first.getDailyMacdHistogram() != null ) {
					if( previous.getDailyMacdHistogram().compareTo(first.getDailyMacdHistogram()) > 0 
							&& previous.getDailyMacdHistogram().compareTo(current.getDailyMacdHistogram()) > 0 ) {
						stockPriceData.setDhc(StockPriceData.DOWN);
					} else if( previous.getDailyMacdHistogram().compareTo(first.getDailyMacdHistogram()) < 0 
							&& previous.getDailyMacdHistogram().compareTo(current.getDailyMacdHistogram()) < 0 ) {
						stockPriceData.setDhc(StockPriceData.UP);
					}
				}
			}

			if( current.getClosePrice().compareTo(current.getDailyShortSma()) > 0 && 
					previous.getClosePrice().compareTo(current.getDailyShortSma()) < 0 ) {
				stockPriceData.setSsc(StockPriceData.UP);
			} else if( current.getClosePrice().compareTo(current.getDailyShortSma()) < 0 && 
					previous.getClosePrice().compareTo(current.getDailyShortSma()) > 0 ) {
				stockPriceData.setSsc(StockPriceData.DOWN);
			}
			
			if( current.getClosePrice().compareTo(current.getDailyMediumSma()) > 0 && 
					previous.getClosePrice().compareTo(current.getDailyMediumSma()) < 0 ) {
				stockPriceData.setMsc(StockPriceData.UP);
			} else if( current.getClosePrice().compareTo(current.getDailyMediumSma()) < 0 && 
					previous.getClosePrice().compareTo(current.getDailyMediumSma()) > 0 ) {
				stockPriceData.setMsc(StockPriceData.DOWN);
			}
			
			if( current.getClosePrice().compareTo(current.getDailyLongSma()) > 0 && 
					previous.getClosePrice().compareTo(current.getDailyLongSma()) < 0 ) {
				stockPriceData.setLsc(StockPriceData.UP);
			} else if( current.getClosePrice().compareTo(current.getDailyLongSma()) < 0 && 
					previous.getClosePrice().compareTo(current.getDailyLongSma()) > 0 ) {
				stockPriceData.setLsc(StockPriceData.DOWN);
			}
			
			if( previous.getDailyMacd().compareTo(previous.getDailyMacdSignal()) < 0 
					&& current.getDailyMacd().compareTo(current.getDailyMacdSignal()) > 0 ) {
				stockPriceData.setDmc(StockPriceData.UP);
			} else if( previous.getDailyMacd().compareTo(previous.getDailyMacdSignal()) > 0 
					&& current.getDailyMacd().compareTo(current.getDailyMacdSignal()) < 0 ) {
				stockPriceData.setDmc(StockPriceData.DOWN);
			} 
			
			if( this.isNthDayHigh(10, i, stockPriceList) || this.isNthDayLow(10, i, stockPriceList) ) {
				Double tenDiff = MathUtils.getPriceDiff(stockPriceList.get(i-10).getClosePrice(), current.getClosePrice(), 5).doubleValue();
				stockPriceData.setD10(tenDiff);
			} else if( this.isNthDayHigh(20, i, stockPriceList) || this.isNthDayLow(20, i, stockPriceList) ) {
				Double twentyDiff = MathUtils.getPriceDiff(stockPriceList.get(i-20).getClosePrice(), current.getClosePrice(), 5).doubleValue();
				stockPriceData.setD20(twentyDiff);
			}
			stockPriceDataList.add(stockPriceData);
		}
		return stockPriceDataList;
	}
	
	public void generateDailyMovingAverageTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortSma, int mediumSma, int longSma) throws Exception {
		TimeSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
		SMAIndicator dailyShortSma = new SMAIndicator(dailyClosePrice, shortSma);
		SMAIndicator dailyMediumSma = new SMAIndicator(dailyClosePrice, mediumSma);
		SMAIndicator dailyLongSma = new SMAIndicator(dailyClosePrice, longSma);
		for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
			stockPriceEntity.setDailyShortSma(new BigDecimal(dailyShortSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
			stockPriceEntity.setDailyMediumSma(new BigDecimal(dailyMediumSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
			stockPriceEntity.setDailyLongSma(new BigDecimal(dailyLongSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
		}
		return;
	}
	
	public void generateDailyMacdTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortSma, int longSma, int ema) throws Exception {
		TimeSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
        MACDIndicator dailyMacd = new MACDIndicator(dailyClosePrice, shortSma, longSma);
        EMAIndicator dailyMacdSignal = new EMAIndicator(dailyMacd, ema);
        for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
    		stockPriceEntity.setDailyMacd(new BigDecimal(dailyMacd.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
    		stockPriceEntity.setDailyMacdSignal(new BigDecimal(dailyMacdSignal.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
    		stockPriceEntity.setDailyMacdHistogram(stockPriceEntity.getDailyMacd().subtract(stockPriceEntity.getDailyMacdSignal()));
        }
		return;
	}
	
	public void generateDailyRsiTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortRsi, int longRsi) throws Exception {
		TimeSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
		SmoothedRSIIndicator shortRsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, shortRsi);
		SmoothedRSIIndicator longRsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, longRsi);
        for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
        	stockPriceEntity.setDailyShortRsi(new BigDecimal(shortRsiIndicator.getValue(i).toDouble()).setScale(3,RoundingMode.HALF_UP));
    		stockPriceEntity.setDailyLongRsi(new BigDecimal(longRsiIndicator.getValue(i).toDouble()).setScale(3,RoundingMode.HALF_UP));
        }
		return;
	}
	
	private StockPriceEntity getLastDateWeeklyStockPrice(List<StockPriceEntity> dailyStockPriceList, Date endDate) {
		Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(endDate.getTime());
    	int year = cal.get(Calendar.YEAR);
    	int week = cal.get(Calendar.WEEK_OF_YEAR);
    	int startIndex = -1;
    	for(int i=dailyStockPriceList.size()-1; i>=0; i--) {
    		cal.setTimeInMillis(dailyStockPriceList.get(i).getTradeDate().getTime());
    		if( year == cal.get(Calendar.YEAR) && week == cal.get(Calendar.WEEK_OF_YEAR) ) {
    			startIndex = i;
    			continue;
    		}
    		break;
    	}
    	StockPriceEntity weeklyStockPriceEntity = null;
    	if( startIndex != - 1) {
    		weeklyStockPriceEntity = dailyStockPriceList.get(startIndex);
    		for(int i=startIndex+1; i<dailyStockPriceList.size(); i++) {
    			StockPriceEntity dateStockPriceEntity = dailyStockPriceList.get(i);
    			weeklyStockPriceEntity.setTradeDate(dateStockPriceEntity.getTradeDate());
    			weeklyStockPriceEntity.setClosePrice(dateStockPriceEntity.getClosePrice());
    			if( dateStockPriceEntity.getDayHigh().compareTo(weeklyStockPriceEntity.getDayHigh()) > 0 ) {
    				weeklyStockPriceEntity.setDayHigh(dateStockPriceEntity.getDayHigh());
    			}
    			if( dateStockPriceEntity.getDayLow().compareTo(weeklyStockPriceEntity.getDayLow()) < 0 ) {
    				weeklyStockPriceEntity.setDayLow(dateStockPriceEntity.getDayLow());
    			}
    			if( dateStockPriceEntity.getDayVolume() != null ) {
    				weeklyStockPriceEntity.setDayVolume(weeklyStockPriceEntity.getDayVolume().add(dateStockPriceEntity.getDayVolume()));
    			}
    		}
    	}
    	return weeklyStockPriceEntity;
	}
	
	public void generateWeeklyMacdTechnicalIndicator(String stockCode, List<StockPriceEntity> dailyStockPriceList, int shortSma, int longSma, int ema) throws Exception {
		
		java.sql.Date startDate = dailyStockPriceList.get(0).getTradeDate();
		java.sql.Date endDate = dailyStockPriceList.get(dailyStockPriceList.size()-1).getTradeDate();
		List<StockPriceEntity> weeklyStockPriceList = this.stockPriceDao.getStockPriceEntityListInDate(stockCode, startDate, endDate, StockPriceEntity.PRICE_TYPE_WEEKLY);
		
		if( weeklyStockPriceList.get(weeklyStockPriceList.size()-1).getTradeDate().compareTo(endDate) != 0 ) {
			StockPriceEntity weeklyStockPriceEntity = this.getLastDateWeeklyStockPrice(dailyStockPriceList, endDate);
			if( weeklyStockPriceEntity != null ) {
				weeklyStockPriceList.add(weeklyStockPriceEntity);
			}
		}
		
		TimeSeries weeklySeries = this.getStockTimeSeries(stockCode, weeklyStockPriceList);
		ClosePriceIndicator weeklyClosePrice = new ClosePriceIndicator(weeklySeries);
        MACDIndicator weeklyMacd = new MACDIndicator(weeklyClosePrice, shortSma, longSma);
        EMAIndicator weeklyMacdSignal = new EMAIndicator(weeklyMacd, ema);
        Map<Date, Integer> weeklytStockPriceEntityIndexMap = new HashMap<Date, Integer>();
        for(int i=0; i<weeklyStockPriceList.size(); i++) {
        	weeklytStockPriceEntityIndexMap.put(weeklyStockPriceList.get(i).getTradeDate(), i);
        }
        
        for(int i=0; i<dailyStockPriceList.size(); i++) {
        	StockPriceEntity stockPriceEntity = dailyStockPriceList.get(i);
        	if( weeklytStockPriceEntityIndexMap.containsKey(stockPriceEntity.getTradeDate()) ) {
        		int index = weeklytStockPriceEntityIndexMap.get(stockPriceEntity.getTradeDate());
        		stockPriceEntity.setWeeklyMacd(new BigDecimal(weeklyMacd.getValue(index).toDouble()));
        		stockPriceEntity.setWeeklyMacdSignal(new BigDecimal(weeklyMacdSignal.getValue(index).toDouble()));
        		stockPriceEntity.setWeeklyMacdHistogram(stockPriceEntity.getWeeklyMacd().subtract(stockPriceEntity.getWeeklyMacdSignal()));
        	}
        }
        
		return;
	}

	public TimeSeries getStockTimeSeries(String stockCode, List<StockPriceEntity> stockPriceEntityList) throws Exception {
		List<Tick> ticks = new ArrayList<Tick>();
		for(int i=0; i<stockPriceEntityList.size(); i++) {
			try {
				ticks.add(stockPriceEntityList.get(i).toTick());
			}catch(Exception e) {
				
			}
		}
		return new TimeSeries(stockCode, ticks);
	}
	
	private boolean isNthDayHigh(int nth, int index, List<StockPriceEntity> stockPriceList) {
		if( index - nth >= 0 ) {
			StockPriceEntity current = stockPriceList.get(index);
			StockPriceEntity init = stockPriceList.get(index-nth);
			if( current.getClosePrice().compareTo(init.getClosePrice()) > 0 ) {
				for(int i=index-nth; i<index; i++) {
					if( stockPriceList.get(i).getClosePrice().compareTo(init.getClosePrice()) > 0 ) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean isNthDayLow(int nth, int index, List<StockPriceEntity> stockPriceList) {
		if( index - nth >= 0 ) {
			StockPriceEntity current = stockPriceList.get(index);
			StockPriceEntity init = stockPriceList.get(index-nth);
			if( current.getClosePrice().compareTo(init.getClosePrice()) < 0 ) {
				for(int i=index-nth; i<index; i++) {
					if( stockPriceList.get(i).getClosePrice().compareTo(init.getClosePrice()) < 0 ) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}
