package com.lunstudio.stocktechnicalanalysis.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockDao;
import com.lunstudio.stocktechnicalanalysis.dao.StockPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

@Service
public class StockSrv {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockPriceDao stockPriceDao;
	
	@Autowired
	private StockDao stockDao;
	
	public void saveStockPrice(List<StockPriceEntity> stockPriceEntityList) {
		this.stockPriceDao.save(stockPriceEntityList, stockPriceEntityList.size());
		return;
	}
	
	public StockPriceEntity getDailyStockPrice(String stockCode, Date tradeDate) {
		return this.stockPriceDao.getStockPrice(stockCode, tradeDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	public List<String> getStockHkexCodeList() throws Exception {
		List<String> stockCodeList = new ArrayList<String>();
		List<StockEntity> stockList = this.stockDao.getStockList();
		for(StockEntity entity : stockList) {
			stockCodeList.add(entity.getStockHkexCode());
		}
		return stockCodeList;
	}

}
