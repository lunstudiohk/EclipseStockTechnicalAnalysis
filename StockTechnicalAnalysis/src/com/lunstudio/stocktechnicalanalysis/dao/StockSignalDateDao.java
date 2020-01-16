package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;

public interface StockSignalDateDao extends BaseDao {

	public StockSignalDateEntity findByPrimaryKey(String stockCode, Date tradeDate, Integer signalSeq, String signalType, Date signalDate) throws Exception;
	
	public List<StockSignalDateEntity> getStockSignalDateList(String stockCode, Date tradeDate, Integer signalSeq, String signalType, Integer order) throws Exception;
}
