package com.lunstudio.stocktechnicalanalysis.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockSignalDateDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;

@Service
public class StockSignalDateSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSignalDateDao stockSignalDateDao;
	
	public void saveStockSignalDate(StockSignalDateEntity entity) throws Exception {
		this.stockSignalDateDao.save(entity);
		return;
	}
	
	public void saveStockSignalDateList(List<StockSignalDateEntity> list) throws Exception {
		this.stockSignalDateDao.save(list, 500);
		return;
	}

	public StockSignalDateEntity getStockSignalDate(String stockCode, Date tradeDate, Integer signalSeq, String signalType, Date signalDate) throws Exception {
		return this.stockSignalDateDao.findByPrimaryKey(stockCode, tradeDate, signalSeq, signalType, signalDate);
	}
	
	public List<StockSignalDateEntity> getStockSignalDateList(String stockCode, Date tradeDate, Integer signalSeq, String signalType, Integer order) throws Exception {
		return this.stockSignalDateDao.getStockSignalDateList(stockCode, tradeDate, signalSeq, signalType, order);
	}
	
	public List<StockSignalDateEntity> getStockSignalHistoricalDateList(String stockCode, Date startDate) throws Exception {
		return this.stockSignalDateDao.getStockSignalHistoricalDateList(stockCode, startDate);
	}
	
	/**
	 * Only get the Current Signal Date
	 * @param startDate Inclusive
	 * @return
	 * @throws Exception
	 */
	public List<StockSignalDateEntity> getStockSignalTradeDateList(Date startDate) throws Exception {
		return this.stockSignalDateDao.getStockSignalTradeDateList(null, startDate);
	}
	
	public Map<String, StockSignalDateEntity> getStockSignalTradeDateMap(String stockCode, Date startDate) throws Exception {
		Map<String, StockSignalDateEntity> keyMap = new HashMap<String, StockSignalDateEntity>();
		List<StockSignalDateEntity> stockSignalList = this.stockSignalDateDao.getStockSignalTradeDateList(stockCode, startDate);
		for(StockSignalDateEntity stockSignal : stockSignalList) {
			keyMap.put(stockSignal.getKeyString(), stockSignal);
		}
		return keyMap;
	}
	
	public Map<String, StockSignalDateEntity> getStockSignalTradeDateMap(Date startDate) throws Exception {
		Map<String, StockSignalDateEntity> keyMap = new HashMap<String, StockSignalDateEntity>();
		List<StockSignalDateEntity> stockSignalList = this.stockSignalDateDao.getStockSignalTradeDateList(null, startDate);
		for(StockSignalDateEntity stockSignal : stockSignalList) {
			keyMap.put(stockSignal.getKeyString(), stockSignal);
		}
		return keyMap;
	}
		
}
