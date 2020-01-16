package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;

public interface StockSignalDao extends BaseDao {

	public List<StockSignalEntity> getStockSignalList(String stockCode, Date startDate) throws Exception;
	
	public List<StockSignalEntity> getStockSignalListOnDate(Date date) throws Exception;
	
	public List<StockSignalEntity> getIncompletedStockSignalList() throws Exception;
	
}
