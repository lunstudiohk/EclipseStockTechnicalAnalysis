package com.lunstudio.stocktechnicalanalysis.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;


@Service
public class StockSrv {

	private static final Logger logger = LogManager.getLogger();
	
	
	@Autowired
	private StockDao stockDao;
	
	
	public List<StockEntity> getStockInfoList() {
		return this.stockDao.getStockList();
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
