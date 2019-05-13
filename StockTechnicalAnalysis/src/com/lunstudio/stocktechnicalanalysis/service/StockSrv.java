package com.lunstudio.stocktechnicalanalysis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;


@Service
public class StockSrv {

	private static final Logger logger = LogManager.getLogger();
	
	private static final String HSI = "HSI";
	private static final String INDEXHANGSENGHSI = "INDEXHANGSENG:HSI";
	private static final String HSCEI = "HSCEI";
	private static final String INDEXHANGSENGHSCEI = "INDEXHANGSENG:HSCEI";
	
	@Autowired
	private StockDao stockDao;
	
	public void updateStock(StockEntity stock) {
		this.stockDao.save(stock);
		return;
	}
	
	public StockEntity getStockInfo(String stockCode) {
		try {
			return this.stockDao.getStock(stockCode);
		} catch(Exception e) {
			return null;
		}
	}
	
	public List<StockEntity> getStockInfoList() {
		return this.stockDao.getStockList();
	}
	
	public Map<String, StockEntity> getStockInfoMap() {
		Map<String, StockEntity> stockMap = new HashMap<String, StockEntity>();
		List<StockEntity> stockList = this.getStockInfoList();
		for(StockEntity stock : stockList) {
			stockMap.put(stock.getStockCode(), stock);
		}
		return stockMap;
	}
	
	public Map<String, StockEntity> getStockInfoHkexMap() {
		Map<String, StockEntity> stockMap = new HashMap<String, StockEntity>();
		List<StockEntity> stockList = this.getStockInfoList();
		for(StockEntity stock : stockList) {
			stockMap.put(stock.getStockHkexCode(), stock);
		}
		return stockMap;
	}
	
	public List<String> getStockHkexCodeList() throws Exception {
		List<String> stockCodeList = new ArrayList<String>();
		List<StockEntity> stockList = this.stockDao.getStockList();
		for(StockEntity entity : stockList) {
			stockCodeList.add(entity.getStockHkexCode());
		}
		return stockCodeList;
	}

	public String getStockCode(String hkexCode) throws Exception {
		if( HSI.equals(hkexCode) ) {
			return INDEXHANGSENGHSI;
		} else if( HSCEI.equals(hkexCode) ) {
			return INDEXHANGSENGHSCEI;
		} else {
			return String.format("HKG:%s", StringUtils.right(hkexCode, 4));
		}
	}
}
