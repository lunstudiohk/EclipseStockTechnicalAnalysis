package com.lunstudio.stocktechnicalanalysis.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockSignalDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.signal.BearishSignal;
import com.lunstudio.stocktechnicalanalysis.signal.BullishSignal;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;

@Service
public class StockSignalSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockSignalDao stockSignalDao;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private StockSignalDateSrv stockSignalDateSrv;
	
	public void saveStockSignal(StockSignalEntity entity) throws Exception {
		this.stockSignalDao.save(entity);
		return;
	}
	
	public void saveStockSignalList(List<StockSignalEntity> list) throws Exception {
		this.stockSignalDao.save(list, 500);
		return;
	}
	
	public List<StockSignalEntity> getStockSignalList(String stockCode) throws Exception {
		return this.stockSignalDao.getStockSignalList(stockCode, null);
	}
	
	public List<StockSignalEntity> getInCompleteStockSignalList() throws Exception {
		return this.stockSignalDao.getInCompleteStockSignalList();
	}
	
	/**
	 * Include the startDate
	 */
	public List<StockSignalEntity> getStockSignalListAfter(String stockCode, Date startDate) throws Exception {
		return this.stockSignalDao.getStockSignalList(stockCode, startDate);
	}
	
	public List<StockSignalEntity> getAllStockSignalList() throws Exception {
		return this.stockSignalDao.getStockSignalList(null, null);
	}
	
	
	public List<StockSignalEntity> getAllStockSignalListOnDate(Date theDate) throws Exception {
		return this.stockSignalDao.getStockSignalListOnDate(theDate);
	}
	
	
	/**
	 * Get Stock Signal Statistic group by stock
	 * Reference: StockSignalList.json
	 * @param stockCode - null = ALL stock
	 * @param size - trade day count
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStockSignalList(String stockCode, Date startDate) throws Exception {
		Map<String, Object> allStockMap = new HashMap<String, Object>();
		List<StockSignalEntity> stockSignalList = this.getStockSignalListAfter(stockCode, startDate);
		Map<String, StockSignalDateEntity> stockSignalDateMap = this.stockSignalDateSrv.getStockSignalTradeDateMap(stockCode, startDate);
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			String key = stock.getStockHkexCode();
			Map<String, Object> stockMap = new HashMap<String, Object>();
			stockMap.put("name", stock.getStockCname());
			Integer buy = 0;
			Integer buySuccess = 0;

			Integer sell = 0;
			Integer sellSuccess = 0;
			
			for(StockSignalEntity stockSignal : stockSignalList) {
				if( stockSignal.getStockCode().equals(stock.getStockCode()) ) {
					StockSignalDateEntity stockSignalDate = stockSignalDateMap.get(stockSignal.getKeyString());
					if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) ) {
						buy++;
						if( stockSignalDate.getHighReturn() != null && stockSignalDate.getHighReturn().compareTo(stockSignal.getTargetReturn()) >= 0 ) {
							buySuccess++;
						}
					} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) ) {
						sell++;
						if( stockSignalDate.getLowReturn() != null && stockSignalDate.getLowReturn().compareTo(stockSignal.getTargetReturn()) <= 0 ) {
							sellSuccess++;
						}
					}
				}
			}
			Map<String, Object> buyMap = new HashMap<String, Object>();
			buyMap.put("count", buy);
			buyMap.put("success", buySuccess);
			stockMap.put("buy", buyMap);
			
			Map<String, Object> sellMap = new HashMap<String, Object>();
			sellMap.put("count", sell);
			sellMap.put("success", sellSuccess);
			stockMap.put("sell", sellMap);
			
			allStockMap.put(key, stockMap);
		}
		return allStockMap;
	}
	
	/**
	 * Get Stock Signal Statistic group by date
	 * Reference: StockSignalDateList.json
	 * @param size - number of recent days
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStockSignalDateList(Integer size) throws Exception {
		Map<String, Object> allDateMap = new HashMap<String, Object>();
		List<Date> dateList = this.stockPriceSrv.getLastDailyStockPriceTradeDateList(StockEntity.HSI, size, StockPriceSrv.ORDER_BY_DESC);
		if( dateList == null || dateList.size() < 1 ) {
			return allDateMap;
		}
		Date startDate = dateList.get(dateList.size()-1);
		List<StockSignalEntity> stockSignalList = this.getStockSignalListAfter(null, startDate);
		Map<String, StockSignalDateEntity> stockSignalDateMap = this.stockSignalDateSrv.getStockSignalTradeDateMap(null, startDate);
		for(Date theDate : dateList) {
			String key = DateUtils.getLongDateString(theDate);
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("week", DateUtils.getDayOfWeek(theDate));
			Integer buy = 0;
			Integer buySuccess = 0;
	        Set<String> buySet = new HashSet<String>(); 

			Integer sell = 0;
			Integer sellSuccess = 0;
			Set<String> sellSet = new HashSet<String>(); 
			
			for(StockSignalEntity stockSignal : stockSignalList) {
				if( stockSignal.getTradeDate().compareTo(theDate) == 0 ) {
					StockSignalDateEntity stockSignalDate = stockSignalDateMap.get(stockSignal.getKeyString());
					if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) ) {
						buy++;
						buySet.add(stockSignal.getStockCode());
						if( stockSignalDate.getHighReturn() != null && stockSignalDate.getHighReturn().compareTo(stockSignal.getTargetReturn()) >= 0 ) {
							buySuccess++;
						}
					} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) ) {
						sell++;
						sellSet.add(stockSignal.getStockCode());
						if( stockSignalDate.getLowReturn() != null && stockSignalDate.getLowReturn().compareTo(stockSignal.getTargetReturn()) <= 0 ) {
							sellSuccess++;
						}
					}
				}
			}
			Map<String, Object> buyMap = new HashMap<String, Object>();
			buyMap.put("count", buy);
			buyMap.put("success", buySuccess);
			buyMap.put("stock", buySet.size());
			dataMap.put("buy", buyMap);
			
			Map<String, Object> sellMap = new HashMap<String, Object>();
			sellMap.put("count", sell);
			sellMap.put("success", sellSuccess);
			sellMap.put("stock", sellSet.size());
			dataMap.put("sell", sellMap);
			
			allDateMap.put(key, dataMap);
		}
		return allDateMap;
	}
	
	/**
	 * Get Stock Signal Detail List
	 * Reference: StockSignalDetailList.json
	 * @param stockCode
	 * @param startDate
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getStockSignalDetailList(String stockCode, Date startDate) throws Exception {
		Map<String, Object> stockSignalMap = new HashMap<String, Object>();
		List<StockSignalEntity> stockSignalList = this.getStockSignalListAfter(stockCode, startDate);
		Map<String, List<StockSignalEntity>> stockSignalListMap = new HashMap<String, List<StockSignalEntity>>();
		Map<String, StockEntity> stockMap = this.stockSrv.getStockInfoMap();
		for(StockSignalEntity stockSignalEntity : stockSignalList) {
			String key = String.format("%s%s", DateUtils.getLongDateString(stockSignalEntity.getTradeDate()), stockMap.get(stockSignalEntity.getStockCode()).getStockHkexCode());
			if( !stockSignalListMap.containsKey(key) ) {
				stockSignalListMap.put(key, new ArrayList<StockSignalEntity>());
			}
			stockSignalListMap.get(key).add(stockSignalEntity);
		}
		//Map<String, StockSignalDateEntity> stockSignalDateMap = this.stockSignalDateSrv.getStockSignalTradeDateMap(stockCode, startDate);
		
		for(String key : stockSignalListMap.keySet()) {
			Map<String, Object> map = new HashMap<String, Object>();
			List<StockSignalEntity> list = stockSignalListMap.get(key);
			map.put("stock", stockMap.get(list.get(0).getStockCode()).getStockHkexCode());
			map.put("name", stockMap.get(list.get(0).getStockCode()).getStockCname());
			map.put("date", DateUtils.getLongDateString(list.get(0).getTradeDate()));
			List<Object> subList = new ArrayList<Object>();
			for(StockSignalEntity stockSignal : list) {
				Map<String, Object> subMap = new HashMap<String, Object>();
				subMap.put("tradeType", stockSignal.getSignalType());
				subMap.put("count", stockSignal.getCount());
				subMap.put("confident", stockSignal.getConfident());
				subMap.put("target", stockSignal.getTargetReturn());
				subMap.put("day", stockSignal.getCompleted());
				
				List<String> signalDescList = null;
		    	if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) ) {
		    		signalDescList = BullishSignal.getDailyBullishPrimarySignalDesc(stockSignal);
		    	} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) ) {
		    		signalDescList = BearishSignal.getDailyBearishPrimarySignalDesc(stockSignal);
		    	}
		    	subMap.put("desc", signalDescList);
		    	
				Map<String, Object> lowMap = new HashMap<String, Object>();
				lowMap.put("day", stockSignal.getLowerDayMedian());
				lowMap.put("min", stockSignal.getLowerMin());
				lowMap.put("max", stockSignal.getLowerMedian());
				lowMap.put("median", stockSignal.getLowerMax());
				subMap.put("low", lowMap);
				
				Map<String, Object> highMap = new HashMap<String, Object>();
				highMap.put("day", stockSignal.getUpperDayMedian());
				highMap.put("min", stockSignal.getUpperMin());
				highMap.put("max", stockSignal.getUpperMedian());
				highMap.put("median", stockSignal.getUpperMax());
				subMap.put("high", highMap);
				
				List<StockSignalDateEntity> stockSignalDateList = this.stockSignalDateSrv.getStockSignalDateList(stockSignal.getStockCode(), stockSignal.getTradeDate(), stockSignal.getSignalSeq(), stockSignal.getSignalType(), StockSignalDateEntity.DESC);
				List<Map<String, Object>> dateMapList = new ArrayList<Map<String, Object>>();
				for(StockSignalDateEntity dateEntity : stockSignalDateList) {
					Map<String, Object> dateMap = new HashMap<String, Object>();
					dateMap.put("tradeDate", DateUtils.getLongDateString(dateEntity.getSignalDate()));
					dateMap.put("price", dateEntity.getSignalPrice());
					dateMap.put("highDay", dateEntity.getHighDay());
					dateMap.put("highReturn", dateEntity.getHighReturn());
					dateMap.put("lowDay", dateEntity.getLowDay());
					dateMap.put("lowReturn", dateEntity.getLowReturn());
					dateMapList.add(dateMap);
				}
				subMap.put("dateList", dateMapList);
				subList.add(subMap);
			}		
			
			map.put("signal", subList);
			stockSignalMap.put(key, map);
		}

		return stockSignalMap;
	}

	
	

	
	public void updateIncompletedStockSignal() throws Exception {
		List<StockSignalEntity> stockSignalList = this.stockSignalDao.getIncompletedStockSignalList();
		for(StockSignalEntity stockSignal : stockSignalList) {
			List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getDailyStockPriceList(stockSignal.getStockCode(), stockSignal.getTradeDate());
			StockSignalDateEntity stockSignalDate = this.stockSignalDateSrv.getStockSignalDate(stockSignal.getStockCode(), stockSignal.getTradeDate(), 
					stockSignal.getSignalSeq(), stockSignal.getSignalType(), stockSignal.getTradeDate());
			BigDecimal min = null;
			BigDecimal max = null;
			Integer maxPeriod = null;
			Integer minPeriod = null;
			for(int i=0; i<20 && i<stockPriceList.size(); i++) {
				BigDecimal high = stockPriceList.get(i).getHighPrice();
				BigDecimal low = stockPriceList.get(i).getDayLow();
				BigDecimal highDiff = MathUtils.getPriceDiff(stockSignalDate.getSignalPrice(), high, 2);
				BigDecimal lowDiff = MathUtils.getPriceDiff(stockSignalDate.getSignalPrice(), low, 2);
				if( min == null || min.compareTo(lowDiff) > 0 ) {
					min = lowDiff;
					minPeriod = i+1;
				}
				if( max == null || max.compareTo(highDiff) < 0 ) {
					max = highDiff;
					maxPeriod = i+1;
				}
			}
			stockSignalDate.setHighDay(maxPeriod);
			stockSignalDate.setHighReturn(max);
			stockSignalDate.setLowDay(minPeriod);
			stockSignalDate.setLowReturn(min);
			this.stockSignalDateSrv.saveStockSignalDate(stockSignalDate);
			if( stockPriceList.size() >= stockSignal.getPeriod() ) {
				stockSignal.setCompleted(stockSignal.getPeriod());
			} else {
				stockSignal.setCompleted(stockPriceList.size());
			}
			this.saveStockSignal(stockSignal);
		}
		return;
	}
}
