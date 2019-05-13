package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockVolatilityEntity;

public interface StockVolatilityDao extends BaseDao{

	public List<StockVolatilityEntity> getStockVolatilityEntityListOnDate(Date tradeDate);

	public List<StockVolatilityEntity> getStockVolatilityEntityListAfterDate(String stockCode, Date tradeDate);
}
