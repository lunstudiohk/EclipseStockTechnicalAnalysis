package com.lunstudio.stockanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stockanalysis.entity.StockEntity;

public interface StockDao extends BaseDao{

	public StockEntity getStock(String stockCode);
	
	public List<StockEntity> getStockList();
 
	public int updateStockProcessedDate(String stockCode, Date date);
}
