package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;

public interface StockDao extends BaseDao{

	public StockEntity getStock(String stockCode) throws Exception;
	
	public List<StockEntity> getStockList();
 
	public int updateStockProcessedDate(String stockCode, Date date);
}
