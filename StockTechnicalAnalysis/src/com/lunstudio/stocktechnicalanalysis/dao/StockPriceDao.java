package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;


public interface StockPriceDao extends BaseDao{

	public StockPriceEntity getStockPrice(String stockCode, Date tradeDate, String priceType);

}
