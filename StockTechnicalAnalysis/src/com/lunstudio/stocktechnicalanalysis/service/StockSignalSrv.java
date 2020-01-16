package com.lunstudio.stocktechnicalanalysis.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockSignalDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;

@Service
public class StockSignalSrv {

	private static final Logger logger = LogManager.getLogger();

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
	
	public List<StockSignalEntity> getStockSignalListAfter(String stockCode, Date startDate) throws Exception {
		return this.stockSignalDao.getStockSignalList(stockCode, startDate);
	}
	
	public List<StockSignalEntity> getAllStockSignalList() throws Exception {
		return this.stockSignalDao.getStockSignalList(null, null);
	}
	
	public List<StockSignalEntity> getAllStockSignalListAfter(Date startDate) throws Exception {
		return this.stockSignalDao.getStockSignalList(null, startDate);
	}
	
	public List<StockSignalEntity> getAllStockSignalListOnDate(Date startDate) throws Exception {
		return this.stockSignalDao.getStockSignalListOnDate(startDate);
	}
	
	public void updateIncompletedStockSignal() throws Exception {
		List<StockSignalEntity> stockSignalList = this.stockSignalDao.getIncompletedStockSignalList();
		for(StockSignalEntity stockSignal : stockSignalList) {
			List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getDailyStockPriceList(stockSignal.getStockCode(), stockSignal.getTradeDate());
			StockSignalDateEntity stockSignalDate = this.stockSignalDateSrv.getStockSignalDate(stockSignal.getStockCode(), stockSignal.getTradeDate(), stockSignal.getSignalSeq(), stockSignal.getSignalType(), stockSignal.getTradeDate());
			BigDecimal min = null;
			BigDecimal max = null;
			Integer maxPeriod = null;
			Integer minPeriod = null;
			for(int i=0; i<20 && i<stockPriceList.size(); i++) {
				BigDecimal high = stockPriceList.get(i).getDayHigh();
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
