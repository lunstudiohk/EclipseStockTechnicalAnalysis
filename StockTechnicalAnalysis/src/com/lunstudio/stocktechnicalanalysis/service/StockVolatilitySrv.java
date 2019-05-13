package com.lunstudio.stocktechnicalanalysis.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockVolatilityDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockVolatilityEntity;

@Service
public class StockVolatilitySrv {

	@Autowired
	private StockVolatilityDao stockVolatilityDao;
	
	public void saveStockVolatilityList(List<StockVolatilityEntity> stockVolatilityList) throws Exception {
		this.stockVolatilityDao.save(stockVolatilityList, stockVolatilityList.size());
		return;
	}
	
	public Map<Date, StockVolatilityEntity> getStockVolatilityDateMap(String stockCode, Date startDate) throws Exception {
		List<StockVolatilityEntity> stockVolatilityList = this.stockVolatilityDao.getStockVolatilityEntityListAfterDate(stockCode, startDate);
		Map<Date, StockVolatilityEntity> dateMap = new HashMap<Date, StockVolatilityEntity>();
		for(StockVolatilityEntity entity : stockVolatilityList) {
			dateMap.put(entity.getTradeDate(), entity);
		}
		return dateMap;
	}
}
