package com.lunstudio.stocktechnicalanalysis.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockOptionsStatDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsStatEntity;

@Service
public class StockOptionsStatSrv {

	@Autowired
	private StockOptionsStatDao stockOptionsStatDao;
	
	public Map<Date, StockOptionsStatEntity> getStockOptionSttDateMap(String stockCode, Date startDate) throws Exception {
		Map<Date, StockOptionsStatEntity> stockOptionMap = new HashMap<Date, StockOptionsStatEntity>();
		List<StockOptionsStatEntity> stockOptionList = this.stockOptionsStatDao.getStockOptionStatList(stockCode, startDate);
		for(StockOptionsStatEntity entity : stockOptionList) {
			stockOptionMap.put(entity.getTradeDate(), entity);
		}
		return stockOptionMap;
	}
}
