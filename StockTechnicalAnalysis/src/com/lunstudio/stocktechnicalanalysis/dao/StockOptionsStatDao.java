package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsStatEntity;

public interface StockOptionsStatDao extends BaseDao {

	public List<StockOptionsStatEntity> getStockOptionStatList(String stockCode, Date startDate) throws Exception;
}
